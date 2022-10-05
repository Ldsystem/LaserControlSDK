package cn.brk2outside.laser.laser.config;

import cn.brk2outside.laser.client.Client;
import cn.brk2outside.laser.client.event.MsgArrivalEvent;
import cn.brk2outside.laser.cmd.response.IResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

@Slf4j
@TestConfiguration
public class TestLaserResourceConfig {

    @Bean
    public SocketAddress serverAddress() {
        return new InetSocketAddress("192.168.0.201", 3490);
    }

    @Bean
    public Client client(SocketAddress serverAddress) throws IOException {
        Client client = new Client(serverAddress);
        client.startListen();
        return client;
    }

    @EventListener(MsgArrivalEvent.class)
    public void listen(MsgArrivalEvent msgArrivalEvent) throws Exception {
        ByteBuffer msg = msgArrivalEvent.getMsg();
        log.info("Laser IP: {}", msgArrivalEvent.getLaserIP());
        IResponse read = IResponse.read(msg);
        System.out.println(read);
        log.info("==================== End, remaining: {} ======================", msg.remaining());
    }

}
