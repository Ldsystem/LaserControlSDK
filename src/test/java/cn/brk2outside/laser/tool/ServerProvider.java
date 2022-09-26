package cn.brk2outside.laser.tool;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerProvider {


    @PostConstruct
    public void init() {
        log.info("==================================================");
        log.info("Mock Server Socket Started");
        log.info("==================================================");
        Thread thread = new Thread(() -> {
            try {
                ServerSocket server = new ServerSocket(10011);
                Socket socket = server.accept();
                log.info("==================================================");
                log.info("Client {} connected to server", ((InetSocketAddress)socket.getRemoteSocketAddress()).getHostString());
                log.info("==================================================");
                InputStream in = null;
                OutputStream os = null;
                try {
                    in = socket.getInputStream();
                    os = socket.getOutputStream();
                    byte[] bytes = new byte[1024];
                    while (true) {
                        int pos;
                        while ((pos = in.read(bytes)) != -1) {
                            log.info("==================================================");
                            log.info("message received: {}", new String(bytes, 0, pos, StandardCharsets.UTF_8));
                            log.info("==================================================");
                            os.write(bytes, 0, pos);
                            os.flush();
                        }
                        os.write("1234123".getBytes(StandardCharsets.UTF_8));
                        os.flush();
                        System.out.println("wrote");
                    }
                } finally {
                    if (null != in) {
                        in.close();
                    }
                    if (null != os) {
                        os.close();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}
