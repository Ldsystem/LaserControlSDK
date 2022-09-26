package cn.brk2outside.laser.socket.tool;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.lang.System.in;

@Slf4j
public class ServerProvider {

    @PostConstruct
    public void init() {
        log.info("==================================================");
        log.info("Mock Server Socket Started");
        log.info("==================================================");
        Thread thread = new Thread(() -> {
            try {
                ServerSocketChannel server = ServerSocketChannel.open();
                server.bind(new InetSocketAddress(10011));
                server.configureBlocking(false);

                Selector acceptor = Selector.open();
                server.register(acceptor, SelectionKey.OP_ACCEPT);
                acceptor.select();
                Set<SelectionKey> selectionKeys = acceptor.selectedKeys();

                SocketChannel socket = null;

                for (SelectionKey key : selectionKeys) {
                    if (key.isValid() && key.isAcceptable()) {
                        socket = ((ServerSocketChannel) key.channel()).accept();
                        socket.configureBlocking(false);
                        log.info("==================================================");
                        log.info("Client {} connected to server", ((InetSocketAddress)socket.getRemoteAddress()).getHostString());
                        log.info("==================================================");
                    }
                }

                Selector ops = Selector.open();
                socket.register(ops, SelectionKey.OP_READ | SelectionKey.OP_WRITE);


                try {
                    while (true) {
                        String msg = null;
                        ByteBuffer messageBuf = ByteBuffer.allocate(1024);
                        ops.select(TimeUnit.SECONDS.toMillis(1));

                        Set<SelectionKey> keys = ops.selectedKeys();

                        for (SelectionKey key : keys) {
                            if (key.isValid() && key.isReadable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                channel.read(messageBuf);
                                messageBuf.flip();
                                log.info("==================================================");
                                msg = new String(messageBuf.array(), 0, messageBuf.limit(), StandardCharsets.UTF_8);
                                log.info("message received: {}", msg);
                                log.info("==================================================");
                            }

                            if (key.isValid() && key.isWritable()) {
                                messageBuf = ByteBuffer.wrap((msg == null ? "finish" : msg).getBytes(StandardCharsets.UTF_8));
                                log.info("==================================================");
                                log.info("writing message: {}", new String(messageBuf.array(), 0, messageBuf.limit(), StandardCharsets.UTF_8));
                                log.info("==================================================");
                                SocketChannel channel = (SocketChannel) key.channel();
                                channel.write(messageBuf);
                            }

                        }
                        TimeUnit.SECONDS.sleep(1);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}
