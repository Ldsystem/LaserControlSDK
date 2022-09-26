package cn.brk2outside.laser.socket;

import cn.brk2outside.laser.LaserCommApplicationTests;
import cn.brk2outside.laser.client.Client;
import cn.brk2outside.laser.config.TestResourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.ByteBuffer;
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
        String message = "hello";
        ByteBuffer byteBuffer = client.doWrite(message.getBytes(StandardCharsets.UTF_8));
        String s = new String(byteBuffer.array(), 0, byteBuffer.limit(), StandardCharsets.UTF_8);
        System.out.println(s);
        TimeUnit.SECONDS.sleep(15);
        Assertions.assertEquals(s, message);
    }

}
