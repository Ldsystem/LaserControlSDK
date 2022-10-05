package cn.brk2outside.laser.cmd.response;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
@SuppressWarnings("unused")
public class SendCodeResponse extends IResponse {

    boolean success = true;
    int words;

    @Override
    void doRead(ByteBuffer buffer) {
        buffer.get();
        buffer.get();
        words = buffer.get();
        log.debug("{} bytes response from laser", words);
        for (int i = 0; i < words; i++) {
            if (buffer.get() == 0) {
                success = false;
            }
        }
    }

    public boolean success() {
        return success;
    }

    @Override
    public String toString() {
        return "=================================" +
                String.format("\n%d words write", words) +
                String.format("\nsuccess: %b", success) +
                "=================================";
    }

}
