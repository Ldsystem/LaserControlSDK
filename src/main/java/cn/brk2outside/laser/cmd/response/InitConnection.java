package cn.brk2outside.laser.cmd.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.nio.ByteBuffer;

@Getter
@EqualsAndHashCode(callSuper = true)
public class InitConnection extends IResponse {

    public InitConnection() {}

    private byte library;
    private String version;
    private byte status;
    private String statusStr;
    private String settings;
    private String error;

    @Override
    void doRead(ByteBuffer buffer) {
        buffer.rewind();
        this.library = buffer.get();
        this.version = String.format("0x%04d", buffer.getInt());
        this.status = buffer.get();
        this.statusStr = String.format("0x%02x", status);
        this.settings = String.valueOf(buffer.getInt());
    }

    public String toString() {
        return "==============================\n" +
                "Library: " + String.format("%02x",this.library) +
                "\nVersion: " + this.version +
                "\nStatus:  " + this.statusStr +
                "\nSettings: " + this.settings +
                "\n============================";
    }

}
