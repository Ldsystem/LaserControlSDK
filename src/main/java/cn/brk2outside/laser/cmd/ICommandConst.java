package cn.brk2outside.laser.cmd;

public interface ICommandConst {

    byte ZERO = 0x00;

    byte STX = 0x02;
    byte ETX = 0x03;

    /* 激光机状态 */
    byte READ_STAT = 0x0070;

    byte START_PRINT = 0x002D;
    int EXT_MSG = 0xFFFF;


}
