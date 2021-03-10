package <%= packageName %>.controller;

import <%= packageName %>.service.<%= upperProjectName %>Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
<%_ if (tracingEnabled) { -%>
import <%= packageName %>.metrics.<%= upperProjectName %>MetricTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.Arrays;
import java.util.List;
import org.springframework.cloud.sleuth.SpanName;
<%_ } -%>

/**
 * Controller 层示例代码
 */
@Api("<%= projectDescribe %>")
@RestController
public class <%= upperProjectName %>Controller {

    <%_ if (tracingEnabled) { -%>
    private static Logger log = LoggerFactory.getLogger(<%= upperProjectName %>Controller.class);
    <%_ } -%>

    /**
     * Demo 服务
     */
    final <%= upperProjectName %>Service <%= lowerProjectName %>Service;

    /**
     * 构造函数
     * @param <%= lowerProjectName %>Service
     */
    public <%= upperProjectName %>Controller(<%= upperProjectName %>Service <%= lowerProjectName %>Service) {
        this.<%= lowerProjectName %>Service = <%= lowerProjectName %>Service;
    }

    /**
     * 发送 Ping 请求
     * @return
     */
    @ApiOperation(value = "ping", notes = "Say Hello")
    @GetMapping("/ping")
    public String ping(){
        //响应 Ping 消息
        return <%= lowerProjectName %>Service.ping();
    }

    <%_ if (messageQueueEnabled) { -%>
    /**
     * 通过 Spring Cloud Stream 发送异步 Ping 请求
     */
    @GetMapping("/ping/async")
    public String pingAsync(){
        //发送异步 Ping 消息
        return <%= lowerProjectName %>Service.pingAsync();
    }
    <%_ } -%>
}
