package dm.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Spring Cloud Stream 消息监听示例代码，绑定消息通道 demoYm1StreamSource
 */
@Slf4j
@Component
@EnableBinding(DemoYm1StreamSource.class)
public class DemoYm1StreamListener {


    /**
     * 获取自动注入的 Spring Cloud Stream 消息通道
     */
    final
    DemoYm1StreamSource demoYm1StreamSource;

    /**
     * 构造函数
     * @param demoYm1StreamSource
     */
    public DemoYm1StreamListener(DemoYm1StreamSource demoYm1StreamSource) {
        this.demoYm1StreamSource = demoYm1StreamSource;
    }

    /**
     * 监听来自 DEMO_QUEUE 的消息
     * @param msg 消息内容
     */
    @StreamListener(DemoYm1StreamSource.DEMO_QUEUE)
    public void onPingInput(@Payload String msg){
        //在日志中输出消息内容
        log.info(msg);
    }
}
