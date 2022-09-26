package cn.brk2outside.laser.socket.config;

import cn.brk2outside.laser.client.Client;
import cn.brk2outside.laser.client.event.MsgArrivalEvent;
import cn.brk2outside.laser.socket.tool.ServerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

@Slf4j
@TestConfiguration
@Import({ServerProvider.class, Client.class})
public class TestResourceConfig {

    @Bean
    public SocketAddress serverAddress() {
        return new InetSocketAddress("127.0.0.1", 10011);
    }

    @EventListener(MsgArrivalEvent.class)
    public void listen(MsgArrivalEvent msgArrivalEvent) {
        ByteBuffer msg = msgArrivalEvent.getMsg();
        log.info("Laser IP: {}", msgArrivalEvent.getLaserIP());
        log.info("Bytes: {}, Message: {}", msg.limit(), new String(msg.array(), 0, msg.limit()));
    }

}
