package cn.brk2outside.laser.constant;

import lombok.Getter;

import static cn.brk2outside.laser.constant.ValueTypeEnum.*;

public enum CommandEnum {
    STX         (   0x02,   U_BYTE),
    ETX         (   0x03,   U_BYTE),
    SYSTEM_STAT (   0x0070, WORD),
    START_PRINT (   0x002D, WORD),
        WORK_MODE       (   0xFFFF, WORD),
        DE_AVT_BATCH    (   0x0000, WORD),
    STOP_PRINT  (   0x002E, WORD),
    SESSION_CTL (   0x0080, WORD),
        START_SESSION   (0x0001, WORD),
        END_SESSION     (0x0000, WORD),
    EXTEND_CMD  (0x04, U_BYTE),
    SENDING_CODE(0x0141, WORD),
        SENDING_OPTION_SET(0x00, U_BYTE)
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
            case WORD:
                return new byte[] {
                        (byte) (value & 0xFF),
                        (byte) ((value >>> 8) & 0xFF)
                };
            case U_BYTE:
                return new byte[] {
                        (byte) (value & 0xFF)
                };
            case DWORD:
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
