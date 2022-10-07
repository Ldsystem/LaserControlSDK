package cn.brk2outside.laser;

import cn.brk2outside.laser.client.event.LaserStatEvent;
import cn.brk2outside.laser.client.event.MsgArrivalEvent;
import cn.brk2outside.laser.client.event.SendCodeResultEvent;
import cn.brk2outside.laser.cmd.response.IResponse;
import cn.brk2outside.laser.cmd.response.LaserStatResponse;
import cn.brk2outside.laser.cmd.response.SendCodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.nio.ByteBuffer;

@Configuration
public class ClientConfig {

    @Autowired
    public ApplicationEventPublisher publisher;

    @EventListener(classes = MsgArrivalEvent.class)
    public void listen(MsgArrivalEvent msgArrivalEvent) throws Exception {
        String laserIp = msgArrivalEvent.getLaserIP();
        ByteBuffer respBuffer = msgArrivalEvent.getMsg();
        IResponse response = IResponse.read(respBuffer);
        if (response instanceof LaserStatResponse) {
            publisher.publishEvent(
                    new LaserStatEvent(
                            laserIp,
                            (LaserStatResponse) response
                    )
            );
        } else if (response instanceof SendCodeResponse) {
            publisher.publishEvent(
                    new SendCodeResultEvent(
                            laserIp,
                            (SendCodeResponse) response
                    )
            );
        }
    }

}
