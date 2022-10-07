package cn.brk2outside.laser.laser;


import cn.brk2outside.laser.LaserCommApplicationTests;
import cn.brk2outside.laser.client.Client;
import cn.brk2outside.laser.cmd.write.CommandWriter;
import cn.brk2outside.laser.laser.config.ReadStatEventConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@Import(ReadStatEventConfig.class)
public class TestEvent extends LaserCommApplicationTests {

    @Autowired
    private Client client;

    @Test
    public void writeCommand() throws IOException, InterruptedException {
        ByteBuffer command = CommandWriter.readLaserStatusCommand();
        client.doWrite(command);
        TimeUnit.SECONDS.sleep(5);
    }

}
