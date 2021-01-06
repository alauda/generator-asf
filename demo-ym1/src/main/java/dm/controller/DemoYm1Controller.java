package dm.controller;

import dm.service.DemoYm1Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller 层示例代码
 */
@Api("dm")
@RestController
public class DemoYm1Controller {

    /**
     * Demo 服务
     */
    final
    DemoYm1Service demoYm1Service;

    /**
     * 构造函数
     * @param demoYm1Service
     */
    public DemoYm1Controller(DemoYm1Service demoYm1Service) {
        this.demoYm1Service = demoYm1Service;
    }

    /**
     * 发送 Ping 请求
     * @return
     */
    @ApiOperation(value = "ping", notes = "Say Hello")
    @GetMapping("/ping")
    public String ping(){
        //响应 Ping 消息
        return demoYm1Service.ping();
    }

    /**
     * 通过 Spring Cloud Stream 发送异步 Ping 请求
     */
    @GetMapping("/ping/async")
    public String pingAsync(){
        //发送异步 Ping 消息
        return demoYm1Service.pingAsync();
    }
}
