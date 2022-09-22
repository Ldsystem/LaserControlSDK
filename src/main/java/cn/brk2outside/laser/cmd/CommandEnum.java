package cn.brk2outside.laser.cmd;

import lombok.Getter;

import static cn.brk2outside.laser.cmd.ValueTypeEnum.*;

public enum CommandEnum {
    STX         (   0x02,   U_BYTE),
    ETX         (   0x03,   U_BYTE),
    SYSTEM_STAT (   0x0070, DWORD),
    START_PRINT (   0x002D, DWORD),
        WORK_MODE       (   0xFFFF, DWORD),
        DE_AVT_BATCH    (   0x0000, DWORD),
    STOP_PRINT  (   0x002E, DWORD),
    SESSION_CTL (   0x0080, DWORD),
        START_SESSION   (0x0001, DWORD),
        END_SESSION     (0x0000, DWORD),

    ;

    CommandEnum(int value, ValueTypeEnum valueType) {
        this.value = value;
        this.valueType = valueType;
    }

    @Getter
    private final ValueTypeEnum valueType;
    @Getter
    private final int value;


    public byte[] getBytes() {
        switch (valueType) {
            case DWORD:
                return new byte[] {
                        (byte) (value & 0xFF),
                        (byte) ((value >>> 8) & 0xFF)
                };
            case U_BYTE:
                return new byte[] {
                        (byte) (value & 0xFF)
                };
            case U_INT:
                return new byte[] {
                        (byte) (value & 0xFF),
                        (byte) ((value >>> 8) & 0xFF),
                        (byte) ((value >>> 16) & 0xFF),
                        (byte) ((value >>> 24) & 0xFF)
                };
            default:
                return new byte[0];
        }
    }

}
