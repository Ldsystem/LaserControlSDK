package cn.brk2outside.laser.socket;

import cn.brk2outside.laser.LaserCommApplicationTests;
import cn.brk2outside.laser.client.Client;
import cn.brk2outside.laser.socket.config.TestResourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Import(TestResourceConfig.class)
public class SocketTest extends LaserCommApplicationTests {

    @Autowired
    private Client client;

    @Test
    public void testReceive() throws IOException, InterruptedException {
        client.startListen();
        client.doWrite("hello".getBytes(StandardCharsets.UTF_8));
        TimeUnit.SECONDS.sleep(5);
    }

}
