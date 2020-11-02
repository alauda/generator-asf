package <%= packageName %>.controller;

import <%= packageName %>.service.<%= upperProjectName %>Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller 层示例代码
 */
@Api("<%= projectDescribe %>")
@RestController
public class <%= upperProjectName %>Controller {

    /**
     * Demo 服务
     */
    final
    <%= upperProjectName %>Service <%= projectName %>Service;

    /**
     * 构造函数
     * @param <%= projectName %>Service
     */
    public <%= upperProjectName %>Controller(<%= upperProjectName %>Service <%= projectName %>Service) {
        this.<%= projectName %>Service = <%= projectName %>Service;
    }

    /**
     * 发送 Ping 请求
     * @return
     */
    @ApiOperation(value = "ping", notes = "Say Hello")
    @GetMapping("/ping")
    public String ping(){
        //响应 Ping 消息
        return <%= projectName %>Service.ping();
    }

    <%_ if (messageQueueEnabled) { -%>
    /**
     * 通过 Spring Cloud Stream 发送异步 Ping 请求
     */
    @GetMapping("/ping/async")
    public String pingAsync(){
        //发送异步 Ping 消息
        return <%= projectName %>Service.pingAsync();
    }
    <%_ } -%>
}
