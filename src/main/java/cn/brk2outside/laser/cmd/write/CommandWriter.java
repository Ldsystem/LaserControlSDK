package cn.brk2outside.laser.cmd.write;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static cn.brk2outside.laser.constant.CommandEnum.*;

@Slf4j
public class CommandWriter {

    private CommandWriter() {}

    public static ByteBuffer readLaserStatusCommand() {
        ByteBuffer command = ByteBuffer.allocate(1024);
        command.put(STX.getBytes());
        command.put((byte) 0x02);
        command.put(SYSTEM_STAT.getBytes());
        command.put(ETX.getBytes());
        command.flip();
        return command;
    }

    public static ByteBuffer sendCodeCommand(int fieldIndex, String code) {
        ByteBuffer command = ByteBuffer.allocate(1024);
        command.put(STX.getBytes());
        command.put(EXTEND_CMD.getBytes());
        command.put(SENDING_CODE.getBytes());
        byte[] bytes = code.getBytes(StandardCharsets.US_ASCII);
        char byteCount = (char) (2 + bytes.length);
        log.info("writing command {} of {} bytes", code, (int)byteCount);
        command.putChar(
                Character.reverseBytes(byteCount)
        );
        command.put(SENDING_OPTION_SET.getBytes());
        command.put((byte) fieldIndex);
        command.put(bytes);
        command.put(ETX.getBytes());
        return command;
    }

}
