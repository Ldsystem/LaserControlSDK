package cn.brk2outside.laser.cmd.response;

import cn.brk2outside.laser.exception.UnsupportedCmdException;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static cn.brk2outside.laser.constant.CommandEnum.*;

public abstract class IResponse {

    protected static final Map<Integer, Class<? extends IResponse>> COMMAND_MAP = new HashMap<>();

    static {
        IResponse.COMMAND_MAP.put(
                SYSTEM_STAT.getValue(), LaserStatResponse.class
        );
        IResponse.COMMAND_MAP.put(
                SENDING_CODE.getValue(), SendCodeResponse.class
        );
    }

    abstract void doRead(ByteBuffer buffer);

    public static IResponse read(ByteBuffer buffer) throws Exception {
        IResponse cmd;
        byte cap = buffer.get();
        if (cap == (byte) STX.getValue()) {
            byte next = buffer.get();
            if (next == EXTEND_CMD.getValue() && buffer.remaining() != next - 1) {
                int cmdByte = Character.reverseBytes(buffer.getChar());
                Class<? extends IResponse> readerClass = COMMAND_MAP.get(cmdByte);
                if (null == readerClass)
                    throw new UnsupportedCmdException(
                            "Extended command "+ String.format("0x%04x", (int)Character.reverseBytes(buffer.getChar())) + " not supported"
                    );
                cmd = readerClass.newInstance();
            } else {
                int cmdByte = Character.reverseBytes(buffer.getChar());
                Class<? extends IResponse> readerClass = COMMAND_MAP.get(cmdByte);
                if (null == readerClass) {
                    throw new UnsupportedCmdException(
                            "Command " + String.format("0x%04x", cmdByte) + " not supported"
                    );
                }
                cmd = readerClass.newInstance();
            }
        } else {
            cmd = new InitConnection();
        }
        cmd.doRead(buffer);
        return cmd;
    }

}
