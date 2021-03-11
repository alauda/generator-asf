package <%= packageName %>.metrics;

import org.springframework.boot.actuate.metrics.web.reactive.server.WebFluxTags;
import org.springframework.boot.actuate.metrics.web.reactive.server.WebFluxTagsProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.tagsprovider.GatewayTagsProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.Objects;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import org.springframework.cloud.gateway.route.Route;
import reactor.core.publisher.Mono;
import <%= packageName %>.metrics.<%= upperProjectName %>MetricTags;
import <%= packageName %>.config.<%= upperProjectName %>RulesParser;

@Configuration
public class <%= upperProjectName %>WebClientConfig {

    private static final String REQUEST_OPERATION = "request_operation";
    private static Logger logger = LoggerFactory.getLogger(<%= upperProjectName %>WebClientConfig.class);
    @Autowired
    <%= upperProjectName %>MetricTags metricTags;
    @Autowired
    private <%= upperProjectName %>RulesParser parser;

    //Client 端数据tag添加
    @Bean
    public GatewayTagsProvider gatewayTagsProvider(){
        return new GatewayTagsProvider() {
            @Override
            public Tags apply(ServerWebExchange serverWebExchange) {

                logger.info("gateway tags provider started");

                String destinationService;
                String destinationNamespace;
                String clientName;
                String statusString;
                String statusCode;

                Route route = serverWebExchange.getAttribute(GATEWAY_ROUTE_ATTR);
                String host = route.getUri().getHost();

                clientName = metricTags.getSourceServiceName();
                statusString = Objects.requireNonNull(serverWebExchange.getResponse().getStatusCode()).toString();

                logger.info("gateway tags provider host==== " + host);
                logger.info("gateway tags provider statuscode");

                if (statusString.contains(" ")){
                    String[] statusStrings = statusString.split(" ");
                    statusCode = statusStrings[0];
                }else{
                    statusCode = "default_unknow";
                }

                if(host.contains(".")){
                    String[] hosts = host.split("\\.");
                    destinationService = hosts[0];
                    destinationNamespace = hosts[1];
                } else {
                    destinationService = "localhost".equalsIgnoreCase(host) ? metricTags.getSourceServiceName() : host;
                    destinationNamespace = metricTags.getSourceServiceNamespace();
                }

                Tag dsTag = Tag.of("destination_service", destinationService);
                Tag dnsTag = Tag.of("destination_namespace", destinationNamespace);
                Tag clientNameTag = Tag.of("clientName", clientName);
                Tag statusCodeTag = Tag.of("status", statusCode);
                Tag servieNameTag = Tag.of("service", metricTags.getSourceServiceName());
                Tag classification = Tag.of(REQUEST_OPERATION, parser.parseClassification(serverWebExchange));

                return Tags.of(dsTag, dnsTag, clientNameTag, servieNameTag, statusCodeTag, classification);
            }
        };
    }

    //Server 端数据tag添加
    @Bean
    public WebFluxTagsProvider webFluxTagsProvider() {
        return new WebFluxTagsProvider() {
            @Override
            public Iterable<Tag> httpRequestTags(ServerWebExchange exchange, Throwable exception) {

                logger.info("webflux tags provider started");

                String clientName;
                String clientNamespace;

                HttpHeaders httpHeadersClientName = exchange.getRequest().getHeaders();
                clientName = httpHeadersClientName.getFirst(<%= upperProjectName %>MetricTags.GATEWAY_CLIENT_NAME);

                HttpHeaders httpHeadersClientNameSpace = exchange.getRequest().getHeaders();
                clientNamespace = httpHeadersClientNameSpace.getFirst(<%= upperProjectName %>MetricTags.GATEWAY_CLIENT_NAMESPACE);

                String host = exchange.getRequest().getURI().getHost();

                logger.info("webflux tags provider client name ==== " +  clientName);
                logger.info("webflux tags provider client namespace ==== " +  clientNamespace);

                Tag clientNameTag;
                Tag clientNamespaceTag;
                Tag servieNameTag = Tag.of("service", metricTags.getSourceServiceName());

                if (clientName != null) {
                    clientNameTag = Tag.of("client_name", clientName);
                }else{
                    clientNameTag = Tag.of("client_name", "default_unknown");
                }

                if (clientNamespace != null) {
                    clientNamespaceTag = Tag.of("client_namespace", clientNamespace);
                }else{
                    clientNamespaceTag = Tag.of("client_namespace", "default_unknown");
                }
                return Arrays.asList(clientNameTag, clientNamespaceTag, servieNameTag,
                    WebFluxTags.status(exchange), Tag.of(REQUEST_OPERATION, parser.parseClassification(exchange)));
            }
        };
    }
}
