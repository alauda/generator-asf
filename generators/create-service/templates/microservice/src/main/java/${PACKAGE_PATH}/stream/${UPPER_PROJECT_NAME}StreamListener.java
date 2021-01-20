package <%= packageName %>.stream;

<%_ if (tracingEnabled) { -%>
//import io.opentracing.Tracer;
//import io.opentracing.contrib.spring.integration.messaging.OpenTracingChannelInterceptor;
import brave.ScopedSpan;
import brave.Tracer;
import brave.propagation.B3SingleFormat;
import brave.propagation.TraceContext;
<%_ } -%>
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
<%_ if (tracingEnabled) { -%>
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
<%_ } -%>
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Spring Cloud Stream 消息监听示例代码，绑定消息通道 <%= lowerProjectName %>StreamSource
 */
@Slf4j
@Component
@EnableBinding(<%= upperProjectName %>StreamSource.class)
public class <%= upperProjectName %>StreamListener {

    <%_ if (tracingEnabled) { -%>
    /**
     * 获取自动注入的 Tracer，用于记录调用链跟踪
     */
    final Tracer tracer;
    <%_ } -%>

    /**
     * 获取自动注入的 Spring Cloud Stream 消息通道
     */
    final <%= upperProjectName %>StreamSource <%= lowerProjectName %>StreamSource;

    /**
     * 构造函数
     <%_ if (tracingEnabled) { -%>
     * @param tracer
     <%_ } -%>
     * @param <%= lowerProjectName %>StreamSource
     */
    public <%= upperProjectName %>StreamListener(<%_ if (tracingEnabled) { -%>Tracer tracer, <%_ } -%><%= upperProjectName %>StreamSource <%= lowerProjectName %>StreamSource) {
        <%_ if (tracingEnabled) { -%>
        this.tracer = tracer;
        <%_ } -%>
        this.<%= lowerProjectName %>StreamSource = <%= lowerProjectName %>StreamSource;
    }

    /**
     * 监听来自 DEMO_QUEUE 的消息
     * @param msg 消息内容
     <%_ if (tracingEnabled) { -%>
     * @param headers 调用链附加的请求头
     <%_ } -%>
     */
    @StreamListener(<%= upperProjectName %>StreamSource.DEMO_QUEUE)
    public void onPingInput(@Payload String msg<%_ if (tracingEnabled) { -%>, @Headers MessageHeaders headers<%_ } -%>){
        <%_ if (tracingEnabled) { -%>
        //目前（opentracing-spring-messaging-1.0.0）OpenTracing并未自动实现异步消息的接收记录，因此采用该方式实现
        //根据消息请求头中的信息，构造新的消息，用于关联同一个 TraceID 下的两次异步调用 Span
        Message<String> message = MessageBuilder
                .withPayload(msg)
                .copyHeaders(headers).build();

        TraceContext traceContext = null;

        if(headers.get("b3")!=null){
            String b3 = String.valueOf(headers.get("b3"));
            traceContext = B3SingleFormat.parseB3SingleFormat(b3).context();
        }

        ScopedSpan span = tracer.startScopedSpanWithParent("receive",traceContext);
        span.finish();

        <%_ } -%>
        //在日志中输出消息内容
        log.info(msg);
    }
}
