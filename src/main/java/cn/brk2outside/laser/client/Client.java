package cn.brk2outside.laser.client;

import cn.brk2outside.laser.client.event.MsgArrivalEvent;
import com.sun.istack.internal.NotNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Client implements Closeable, ApplicationEventPublisherAware {

    private static final AtomicReference<ApplicationEventPublisher> PUBLISHER_CONTEXT = new AtomicReference<>();

    private static final AtomicBoolean TERMINATED = new AtomicBoolean(false);

    private SocketChannel channel;
    private final SocketAddress address;

    private final Selector readOnly = Selector.open();
    private final Selector readWrite = Selector.open();

    public Client(SocketAddress address) throws IOException {
        this.address = address;
        this.init();
    }

    public void doWrite(byte[] command) throws IOException {
        checkConnection();

        readWrite.select(TimeUnit.SECONDS.toMillis(1));
        Set<SelectionKey> selectionKeys = readWrite.selectedKeys();
        Iterator<SelectionKey> keyItr = selectionKeys.iterator();
        while (keyItr.hasNext()) {
            SelectionKey key = keyItr.next();
            if (key.isWritable()) {
                SocketChannel writeChannel = (SocketChannel) key.channel();
                ByteBuffer commandBuff = ByteBuffer.wrap(command);
                writeChannel.write(commandBuff);
                break;
            }
            keyItr.remove();
        }
    }

    public void startListen() {
        this.startListen(null);
    }

    public void startListen(ExecutorService executor) {
        Runnable task = new WaitMessageArrivalTask();

        if (null != executor) {
            executor.submit(task);
        } else {
            Thread thread = new Thread(task);
            thread.setName("MESSAGE LISTENER");
            thread.setDaemon(true);
            thread.setContextClassLoader(this.getClass().getClassLoader());
            thread.setUncaughtExceptionHandler((t, e) -> {
                log.error("Uncaught exception {} in {}", e.getLocalizedMessage(), t.getName(), e);
            });
            thread.start();
        }
    }

    private void checkConnection() throws IOException {
        if (null == channel || !channel.isOpen()) {
            channel = SocketChannel.open();
            channel.connect(this.address);
            channel.configureBlocking(false);
            channel.register(readOnly, SelectionKey.OP_READ);
            channel.register(readWrite, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    private void init() throws IOException {
        checkConnection();
    }



    @Override
    public void close() throws IOException {
        TERMINATED.set(true);
        if (null != channel) {
            if (channel.isConnected()) {
                channel.finishConnect();
            }
            if (channel.isOpen()) {
                channel.close();
            }
        }
    }

    private void publish(@NotNull ByteBuffer msg) {
        ApplicationEventPublisher publisher = PUBLISHER_CONTEXT.get();
        if (null == publisher) {
            return;
        }
        assert null != msg;
        publisher.publishEvent(
                new MsgArrivalEvent(
                        msg,
                        ((InetSocketAddress)this.address).getHostString()
                )
        );
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        Client.PUBLISHER_CONTEXT.set(applicationEventPublisher);
    }

    private final class WaitMessageArrivalTask implements Runnable {

        @Override
        @SneakyThrows
        public void run() {
            while (!TERMINATED.get()) {
                try {
                    checkConnection();

                    readOnly.select(TimeUnit.SECONDS.toMillis(1));
                    Set<SelectionKey> selectionKeys = readOnly.selectedKeys();
                    for (SelectionKey key : selectionKeys) {
                        if (key.isValid() && key.isReadable()) {
                            ByteBuffer msg = ByteBuffer.allocate(1024);
                            SocketChannel readChannel = (SocketChannel) key.channel();
                            readChannel.read(msg);
                            msg.flip();
                            if (msg.limit() > 0) {
                                publish(msg);
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("Error when listening to laser message", e);
                }
                Thread.yield();
            }
        }
    }

}
