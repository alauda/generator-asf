package <%= packageName %>.service;

<%_ if (messageQueueEnabled) { -%>
import <%= packageName %>.stream.<%= upperProjectName %>StreamSource;
<%_ } -%>
<%_ if (tracingEnabled) { -%>
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.spring.integration.messaging.MessageTextMap;
import io.opentracing.propagation.Format;
<%_ } -%>
import org.springframework.beans.factory.annotation.Value;
<%_ if (messageQueueEnabled) { -%>
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
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
    final
    Tracer tracer;
    <%_ } -%>

    <%_ if (messageQueueEnabled) { -%>
    /**
     * 获取自动注入的 Spring Cloud Stream 消息通道
     */
    final
    <%= upperProjectName %>StreamSource <%= projectName %>StreamSource;
    <%_ } -%>

    <%_ if (messageQueueEnabled || tracingEnabled) { -%>
    /**
     * 构造函数
    <%_ if (messageQueueEnabled) { -%>
     * @param <%= projectName %>StreamSource
    <%_ } -%>
    <%_ if (tracingEnabled) { -%>
     * @param tracer
    <%_ } -%>
     */
    public <%= upperProjectName %>Service(<%_ if (messageQueueEnabled) { -%><%= upperProjectName %>StreamSource <%= projectName %>StreamSource<%_ } -%><%_ if (messageQueueEnabled && tracingEnabled) { -%>, <%_ } -%><%_ if (tracingEnabled) { -%>Tracer tracer<%_ } -%>) {
        <%_ if (messageQueueEnabled) { -%>
        this.<%= projectName %>StreamSource = <%= projectName %>StreamSource;
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
        Message<String> message = MessageBuilder.withPayload(ping()).build();
        <%_ if (tracingEnabled) { -%>
        //将消息使用调用链通信类包装
        MessageTextMap<String> textMap = new MessageTextMap<>(message);

        //获取当前请求上下文中的 Span
        Span serverSpan = this.tracer.activeSpan();
        if(serverSpan != null){
            //当前请求上下文中不存在 Span 则自动创建
            SpanContext spanContext = serverSpan.context();
            //在消息中注入调用链信息
            tracer.inject(spanContext,Format.Builtin.TEXT_MAP, textMap);
        }

        //发送注入了调用链信息的消息，调用链信息将附在消息头中发送
        <%= projectName %>StreamSource.pingOutput()
                .send(textMap.getMessage());
        <%_ }else{ -%>
        //发送异步消息
        <%= projectName %>StreamSource.pingOutput()
                .send(message);
        <%_ } -%>
        //响应请求
        return "OK！Asynchronous message sent";
    }
    <%_ } -%>
}
