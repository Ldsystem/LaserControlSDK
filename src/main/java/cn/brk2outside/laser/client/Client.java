package cn.brk2outside.laser.client;

import cn.brk2outside.laser.client.event.MsgArrivalEvent;
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

    public ByteBuffer doWrite(byte[] command) throws IOException {
        readWrite.select(TimeUnit.SECONDS.toMillis(1));
        Set<SelectionKey> selectionKeys = readWrite.selectedKeys();
        Iterator<SelectionKey> keyItr = selectionKeys.iterator();
        while (keyItr.hasNext()) {
            SelectionKey key = keyItr.next();
            if (key.isWritable()) {
                SocketChannel writeChannel = (SocketChannel) key.channel();
                ByteBuffer commandBuff = ByteBuffer.wrap(command);
                commandBuff.flip();
                writeChannel.write(commandBuff);
                writeChannel.shutdownOutput();
                break;
            }
            keyItr.remove();
        }
        do {
            readWrite.select(TimeUnit.SECONDS.toMillis(1));
            Set<SelectionKey> readKeys = readWrite.selectedKeys();
            for (SelectionKey key : readKeys) {
                if (key.isReadable()) {
                    ByteBuffer resp = ByteBuffer.allocate(1024);
                    SocketChannel readChannel = (SocketChannel) key.channel();
                    readChannel.read(resp);
                    readChannel.shutdownInput();
                    return (ByteBuffer) resp.flip();
                }
            }
        } while (true);
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
        }
    }

    private void init() throws IOException {
        channel = SocketChannel.open();
        channel.connect(this.address);
        channel.configureBlocking(false);
        channel.register(readOnly, SelectionKey.OP_READ);
        channel.register(readWrite, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
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

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        Client.PUBLISHER_CONTEXT.set(applicationEventPublisher);
    }

    private final class WaitMessageArrivalTask implements Runnable {

        @Override
        public void run() {
            while (!TERMINATED.get()) {
                ApplicationEventPublisher publisher = PUBLISHER_CONTEXT.get();
                if (null != publisher) {
                    try {
                        readOnly.select(TimeUnit.SECONDS.toMillis(1));
                        Set<SelectionKey> selectionKeys = readOnly.selectedKeys();
                        for (SelectionKey key : selectionKeys) {
                            if (key.isReadable()) {
                                ByteBuffer msg = ByteBuffer.allocate(1024);
                                SocketChannel readChannel = (SocketChannel) key.channel();
                                readChannel.read(msg);
                                msg.flip();
                                publisher.publishEvent(
                                        new MsgArrivalEvent(msg, ((InetSocketAddress)Client.this.address).getHostString())
                                );
                                readChannel.shutdownInput();
                            }
                        }
                    } catch (IOException e) {
                        log.error("Error when listening to laser message", e);
                    }
                }
                Thread.yield();
            }
        }
    }

}
