package cn.brk2outside.laser.laser;

import cn.brk2outside.laser.LaserCommApplicationTests;
import cn.brk2outside.laser.client.Client;
import cn.brk2outside.laser.laser.config.TestLaserResourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static cn.brk2outside.laser.cmd.write.CommandWriter.readLaserStatusCommand;
import static cn.brk2outside.laser.cmd.write.CommandWriter.sendCodeCommand;

@Slf4j
@Import(TestLaserResourceConfig.class)
public class LaserTest extends LaserCommApplicationTests {

    @Autowired
    private Client client;

    /**
     *
    * */
    @Test
    public void testReceive() throws IOException, InterruptedException {
        ByteBuffer command = readLaserStatusCommand();
        client.doWrite(command);
        TimeUnit.SECONDS.sleep(15);
    }

    @Test
    public void testWriteCode() throws IOException, InterruptedException {
        int fieldNumber = 0;
        String code = "SSSSSS";
        ByteBuffer command = sendCodeCommand(fieldNumber, code);
        client.doWrite(command);
        TimeUnit.SECONDS.sleep(10);
    }

}
