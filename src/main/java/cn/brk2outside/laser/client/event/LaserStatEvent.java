package cn.brk2outside.laser.client.event;

import cn.brk2outside.laser.cmd.response.LaserStatResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LaserStatEvent extends ApplicationEvent {

    private final String laserIp;
    private final LaserStatResponse msg;

    public LaserStatEvent(String laserIp, LaserStatResponse msg) {
        super(msg);
        this.laserIp = laserIp;
        this.msg = msg;
    }
}
