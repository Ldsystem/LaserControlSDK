package cn.brk2outside.laser.cmd.response;

import lombok.Getter;

import java.nio.ByteBuffer;

import static cn.brk2outside.laser.constant.CommandEnum.ETX;

@Getter
public class LaserStatResponse extends IResponse {

    /** 1. */
    private int okPrints;
    /** 2. */
    private int allPrints;
    /** 4.1 */
    private byte mode;
    /**
     * 4.3
     * 0b00000001 - int print mode
     * 0b00000010 - printing
     * 0b00000100 - waiting for alarm reset
     * 0b00001000 - waiting for input signal (DSP card)
     * 0b00010000 - waiting for external axis to reach a new position
     * 0b00100000 - in print session mode
     * ... reserved
     * */
    private byte startType;
    /** 5. */
    private int totalPrints;
    /** 6. set to 0 to enter infinity mode */
    private int copies;
    /** 7.1 */
    private int alarmStatus;
    /** 7.2 */
    private int alarmCode;
    private int mask;
    private int signalState;

    @Override
    void doRead(ByteBuffer buffer) {
        this.okPrints = Integer.reverseBytes(buffer.getInt());
        this.allPrints = Integer.reverseBytes(buffer.getInt());
        /* 3. Message port, ignored */
        buffer.getInt();
        this.mode = buffer.get();
        /* 4.2 req, ignored */
        buffer.get();
        /* 4.3 req type, ignored */
        buffer.get();
        /* 4.4 laser status */
        this.startType = buffer.get();
        this.totalPrints = Integer.reverseBytes(buffer.getInt());
        /* 6. jobs laser need to finish */
        this.copies = Integer.reverseBytes(buffer.getInt());
        /* 7.1 */
        this.alarmStatus = Character.reverseBytes(buffer.getChar());
        /* 7.2 */
        this.alarmCode = Character.reverseBytes(buffer.getChar());
        /* 8. print time, ignored */
        buffer.getInt();
        /* 9-10 filename, ignored */
        buffer.position(
                buffer.position() + 8
        );
        /* 11 mask */
        this.mask = Integer.reverseBytes(buffer.getInt());
        this.signalState = Integer.reverseBytes(buffer.getInt());
        byte end = buffer.get();
        assert end == ETX.getValue() && buffer.remaining() == 0;
    }


    @Override
    public String toString() {
        return "=============== Laser Status ===================\n" +
                "Ok Prints: \t\t\t\t\t\t" + this.okPrints +
                "\nAll Prints: \t\t\t\t\t" + this.allPrints +
                "\nTotal Prints: \t\t\t\t\t" + this.totalPrints +
                "\n=============== Status ======================" +
                "\nLaser Mode: \t\t\t\t\t" + String.format("0x%02x", this.mode) +
                "\nPrinting Mode: \t\t\t\t\t" + (this.startType & 0x01) +
                "\nPrinting: \t\t\t\t\t\t" + (this.startType & 0x02) +
                "\nWaiting Alarm Reset: \t\t\t" + (this.startType & 0x04) +
                "\nWaiting Input Signal: \t\t\t" + (this.startType & 0x08) +
                "\nWaiting External Axis: \t\t\t" + (this.startType & 0x10) +
                "\nPrint Session Mode: \t\t\t" + (this.startType & 0x20) +
                "\nReserved 06: \t\t\t\t\t" + (this.startType & 0x40) +
                "\nReserved 07: \t\t\t\t\t" + (this.startType & 0x80) +
                "\nAlarm Status: \t\t\t\t\t" + String.format("0x%04x", this.alarmStatus) +
                "\nSignal State: \t\t\t\t\t" + String.format("0x%08x", this.signalState) +
                "\n============================================="
                ;
    }

    public boolean ready() {
        return this.mode == 0x00
                && (this.startType & 0x01) > 0
                && (this.alarmStatus == 0x0000);
    }

    public boolean ok() {
        return this.alarmStatus == 0x0000;
    }

}
