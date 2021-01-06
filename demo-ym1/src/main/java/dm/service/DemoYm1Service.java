package dm.service;

import dm.stream.DemoYm1StreamSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * Service 层示例代码，绑定消息通道 DemoYm1StreamSource */
@EnableBinding(DemoYm1StreamSource.class)
@Service
public class DemoYm1Service {

    /**
     * 获取配置的服务名称
     */
    @Value("${spring.application.name}")
    String applicationName;


    /**
     * 获取自动注入的 Spring Cloud Stream 消息通道
     */
    final
    DemoYm1StreamSource demoYm1StreamSource;

    /**
     * 构造函数
     * @param demoYm1StreamSource
     */
    public DemoYm1Service(DemoYm1StreamSource demoYm1StreamSource) {
        this.demoYm1StreamSource = demoYm1StreamSource;
    }

    /**
     * 获取 Ping 消息
     * @return
     */
    public String ping(){
        return String.format("**** ^_^ **** %s",applicationName);
    }

    /**
     * 通过 Spring Cloud Stream 异步发送 Ping 消息
     */
    public String pingAsync(){
        //构造异步消息
        MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(ping());
        //响应请求
        return "OK！Asynchronous message sent";
    }
}
