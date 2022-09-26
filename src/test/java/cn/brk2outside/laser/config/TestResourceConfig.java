package cn.brk2outside.laser.config;

import cn.brk2outside.laser.client.Client;
import cn.brk2outside.laser.client.event.MsgArrivalEvent;
import cn.brk2outside.laser.tool.ServerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Slf4j
@TestConfiguration
@Import({ServerProvider.class, Client.class})
public class TestResourceConfig {

    @Bean
    public SocketAddress serverAddress() {
        return new InetSocketAddress("127.0.0.1", 10011);
    }

    @EventListener(MsgArrivalEvent.class)
    public void listen(ApplicationEvent event) {
        if (event instanceof MsgArrivalEvent) {
            MsgArrivalEvent msgArrivalEvent = (MsgArrivalEvent) event;
            log.info("Laser IP: {}", msgArrivalEvent.getLaserIP());
            log.info("Message: {}", msgArrivalEvent.getMsg());
        }
    }

}
