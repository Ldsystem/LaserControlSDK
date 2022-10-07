package cn.brk2outside.laser.client.event;

import cn.brk2outside.laser.cmd.response.SendCodeResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendCodeResultEvent extends ApplicationEvent {

    private final String laserIp;
    private final SendCodeResponse msg;

    public SendCodeResultEvent(String laserIp, SendCodeResponse msg) {
        super(msg);
        this.laserIp = laserIp;
        this.msg = msg;
    }

}
