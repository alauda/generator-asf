package <%= packageName %>.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Spring Cloud Stream 消息通道示例代码
 */
public interface <%= upperProjectName %>StreamSource {

	/**
     * 定义队列名称，必须与配置文件中 destination 保持一致
     */
    public static final String DEMO_QUEUE = "DEMO_QUEUE";

    /**
     * 定义接收通道，用于接收来自 DEMO_QUEUE 的消息
     * @return
     */
    @Input(DEMO_QUEUE)
    MessageChannel pingInput();

    /**
     * 定义发送通道，用于向 DEMO_QUEUE 发送消息
     * @return
     */
    @Output(DEMO_QUEUE)
    MessageChannel pingOutput();
}
