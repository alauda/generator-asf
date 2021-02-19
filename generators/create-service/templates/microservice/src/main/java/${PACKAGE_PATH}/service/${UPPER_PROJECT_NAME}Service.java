package <%= packageName %>.service;

<%_ if (messageQueueEnabled) { -%>
import <%= packageName %>.stream.<%= upperProjectName %>StreamSource;
<%_ } -%>
<%_ if (tracingEnabled) { -%>
import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import brave.propagation.B3SingleFormat;
import brave.propagation.TraceContext;
<%_ } -%>
import org.springframework.beans.factory.annotation.Value;
<%_ if (messageQueueEnabled) { -%>
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
<%_ } -%>
import org.springframework.stereotype.Service;

/**
 * Service 层示例代码<%_ if (messageQueueEnabled) { -%>，绑定消息通道 <%= upperProjectName %>StreamSource<%_ } -%>
 */
<%_ if (messageQueueEnabled) { -%>
@EnableBinding(<%= upperProjectName %>StreamSource.class)
<%_ } -%>
@Service
public class <%= upperProjectName %>Service {

    /**
     * 获取配置的服务名称
     */
    @Value("${spring.application.name}")
    String applicationName;

    <%_ if (tracingEnabled) { -%>
    /**
     * 获取自动注入的 Tracer，用于记录调用链跟踪
     */
    final Tracer tracer;
    <%_ } -%>

    <%_ if (messageQueueEnabled) { -%>
    /**
     * 获取自动注入的 Spring Cloud Stream 消息通道
     */
    final <%= upperProjectName %>StreamSource <%= lowerProjectName %>StreamSource;
    <%_ } -%>

    <%_ if (messageQueueEnabled || tracingEnabled) { -%>
    /**
     * 构造函数
    <%_ if (messageQueueEnabled) { -%>
     * @param <%= lowerProjectName %>StreamSource
    <%_ } -%>
    <%_ if (tracingEnabled) { -%>
     * @param tracer
    <%_ } -%>
     */
    public <%= upperProjectName %>Service(<%_ if (messageQueueEnabled) { -%><%= upperProjectName %>StreamSource <%= lowerProjectName %>StreamSource<%_ } -%><%_ if (messageQueueEnabled && tracingEnabled) { -%>, <%_ } -%><%_ if (tracingEnabled) { -%>Tracer tracer<%_ } -%>) {
        <%_ if (messageQueueEnabled) { -%>
        this.<%= lowerProjectName %>StreamSource = <%= lowerProjectName %>StreamSource;
        <%_ } -%>
        <%_ if (tracingEnabled) { -%>
        this.tracer = tracer;
        <%_ } -%>
    }
    <%_ } -%>

    /**
     * 获取 Ping 消息
     * @return
     */
    public String ping(){
        return String.format("**** ^_^ **** %s",applicationName);
    }

    <%_ if (messageQueueEnabled) { -%>
    /**
     * 通过 Spring Cloud Stream 异步发送 Ping 消息
     */
    public String pingAsync(){
        //构造异步消息
        MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(ping());
        <%_ if (tracingEnabled) { -%>

        //获取当前请求上下文中的 Span
        Span serverSpan = tracer.currentSpan();
        if(serverSpan != null){
            //当前请求上下文中不存在 Span 则自动创建
            TraceContext spanContext = serverSpan.context();
            //在消息中注入调用链信息
            ScopedSpan scopedSpan = tracer.startScopedSpanWithParent("send",spanContext);
            messageBuilder.setHeader("b3", B3SingleFormat.writeB3SingleFormat(scopedSpan.context()));
        }

        //发送注入了调用链信息的消息，调用链信息将附在消息头中发送
        <%= lowerProjectName %>StreamSource.pingOutput()
                .send(messageBuilder.build());
        <%_ } -%>
        //响应请求
        return "OK！Asynchronous message sent";
    }
    <%_ } -%>
}
