package cn.brk2outside.laser.laser.config;

import cn.brk2outside.laser.client.Client;
import cn.brk2outside.laser.client.event.LaserStatEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@TestConfiguration
public class ReadStatEventConfig {

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

    @EventListener(LaserStatEvent.class)
    public void listen(LaserStatEvent event) {
        System.out.println(event.getLaserIp());
        System.out.println(event.getMsg());
        System.out.println(event.getMsg().ready());
    }

}
