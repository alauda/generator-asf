package <%= packageName %>.metrics;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
public class <%= upperProjectName %>MetricTags {
    @Value("${spring.application.name}")
    private String sourceApp;
    @Value("${spring.cloud.kubernetes.service.namespace}")
    private String sourceServiceNamespace;
    @Value("${spring.cloud.kubernetes.service.name}")
    private String sourceServiceName;
    private String protocol = "http";
    @Value("${server.names}")
    private String appName;
}
