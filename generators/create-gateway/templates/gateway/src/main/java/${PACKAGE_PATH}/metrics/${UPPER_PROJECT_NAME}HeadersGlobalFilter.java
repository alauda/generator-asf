package <%= packageName %>.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class <%= upperProjectName %>HeadersGlobalFilter implements GlobalFilter, Ordered {

    static final Logger logger = LoggerFactory.getLogger(<%= upperProjectName %>HeadersGlobalFilter.class);
    @Autowired
    <%= upperProjectName %>MetricTags metricTags;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Adding the client headers {} and {}", metricTags.getSourceServiceName(), metricTags.getSourceServiceNamespace());
        String previousName = exchange.getRequest().getHeaders().getFirst("X-Client-Name");
        String previousNamespace = exchange.getRequest().getHeaders().getFirst("X-Client-Namespace");
        exchange.getRequest().mutate()
            .header("X-Client-Name", metricTags.getSourceServiceName())
            .header("X-Client-Namespace", metricTags.getSourceServiceNamespace())
            .header(<%= upperProjectName %>MetricTags.GATEWAY_CLIENT_NAME, previousName)
            .header(<%= upperProjectName %>MetricTags.GATEWAY_CLIENT_NAMESPACE, previousNamespace)
            .build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
