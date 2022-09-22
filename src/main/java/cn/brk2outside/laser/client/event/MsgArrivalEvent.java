package cn.brk2outside.laser.client.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.nio.ByteBuffer;

public class MsgArrivalEvent extends ApplicationEvent {

    @Getter
    private final String laserIP;
    @Getter
    private final ByteBuffer msg;

    public MsgArrivalEvent(ByteBuffer source, String laserIP) {
        super(source);
        this.laserIP = laserIP;
        this.msg = source;
    }

}
