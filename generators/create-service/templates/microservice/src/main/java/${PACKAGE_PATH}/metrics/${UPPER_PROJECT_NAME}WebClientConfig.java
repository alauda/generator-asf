package <%= packageName %>.metrics;

import <%= packageName %>.metrics.<%= upperProjectName %>MetricTags;
import <%= packageName %>.config.<%= upperProjectName %>RulesParser;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTags;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTagsProvider;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTags;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTagsProvider;
import org.springframework.boot.actuate.metrics.web.reactive.server.WebFluxTags;
import org.springframework.boot.actuate.metrics.web.reactive.server.WebFluxTagsProvider;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTags;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class <%= upperProjectName %>WebClientConfig {

    public static final String X_CLIENT_NAME = "X-Client-Name";
    public static final String X_CLIENT_NAMESPACE = "X-Client-Namespace";
    public static final String DESTINATION_SERVICE = "destination_service";
    public static final String DESTINATION_NAMESPACE = "destination_namespace";
    public static final String CLIENT_NAME_KEY = "client_name";
    public static final String CLIENT_NAMESPACE_KEY = "client_namespace";
    public static final String REQUEST_OPERATION = "request_operation";
    private static Logger logger = LoggerFactory.getLogger(<%= upperProjectName %>WebClientConfig.class);

    @Autowired
    private <%= upperProjectName %>MetricTags metricTags;

    @Autowired
    private <%= upperProjectName %>RulesParser parser;

    @Bean
    public WebClient createWebClient(WebClient.Builder webClientBuilder) {

        WebClient webClient = webClientBuilder
            .defaultHeader(X_CLIENT_NAME, metricTags.getSourceServiceName())
            .defaultHeader(X_CLIENT_NAMESPACE, metricTags.getSourceServiceNamespace())
            .build();
        return webClient;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder templateBuilder) {
        return templateBuilder.requestFactory(OkHttp3ClientHttpRequestFactory::new)
            .defaultHeader(X_CLIENT_NAME, metricTags.getSourceServiceName())
            .defaultHeader(X_CLIENT_NAMESPACE, metricTags.getSourceServiceNamespace())
            .build();
    }

    @Bean
    public WebMvcTagsProvider webMvcTagsProvider() {
        return new WebMvcTagsProvider() {
            @Override
            public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable exception) {
                String clientName = request.getHeader(X_CLIENT_NAME);
                String clientNamespace = request.getHeader(X_CLIENT_NAMESPACE);
                Tags tags = Tags.of(WebMvcTags.method(request),
                    WebMvcTags.exception(exception), WebMvcTags.status(response), WebMvcTags.outcome(response));
                if(io.micrometer.core.instrument.util.StringUtils.isNotEmpty(clientName)) {
                    tags = tags.and(Tag.of(CLIENT_NAME_KEY, clientName));
                }
                if(io.micrometer.core.instrument.util.StringUtils.isNotEmpty(clientNamespace)) {
                    tags = tags.and(Tag.of(CLIENT_NAMESPACE_KEY, clientNamespace));
                }
                tags = tags.and(Tag.of(REQUEST_OPERATION, parser.parseClassification(request)));
                logger.info("Adding metrics tag of request_operation");

                return tags;
            }

            @Override
            public Iterable<Tag> getLongRequestTags(HttpServletRequest request, Object handler) {
                return new ArrayList<>();
            }
        };
    }

    @Bean
    public WebFluxTagsProvider webFluxTagsProvider() {
        return new WebFluxTagsProvider() {
        @Override
            public Iterable<Tag> httpRequestTags(ServerWebExchange exchange, Throwable ex) {
            ServerHttpRequest request = exchange.getRequest();
            String clientName = request.getHeaders().getFirst(X_CLIENT_NAME);
            String clientNamespace = request.getHeaders().getFirst(X_CLIENT_NAMESPACE);
            Tags tags = Tags.of(WebFluxTags.method(exchange),
            WebFluxTags.exception(ex), WebFluxTags.status(exchange), WebFluxTags.outcome(exchange));
            if(io.micrometer.core.instrument.util.StringUtils.isNotEmpty(clientName)) {
                tags = tags.and(Tag.of(CLIENT_NAME_KEY, clientName));
            }
            if(io.micrometer.core.instrument.util.StringUtils.isNotEmpty(clientNamespace)) {
                tags = tags.and(Tag.of(CLIENT_NAMESPACE_KEY, clientNamespace));
            }
            tags = tags.and(Tag.of(REQUEST_OPERATION, parser.parseClassification(exchange)));
            logger.info("Adding metrics tag of request_operation for Reactive");
            return tags;
            }
        };
    }


    @Bean
    public RestTemplateExchangeTagsProvider restTemplateExchangeTagsProvider() {
        return new RestTemplateExchangeTagsProvider() {
            @Override
            public Iterable<Tag> getTags(String urlTemplate, HttpRequest request, ClientHttpResponse response) {
                String destinationService;
                String destinationNamespace;
                String host = request.getURI().getHost();
                if(host.contains(".")){
                    String[] hosts = host.split("\\.");
                    destinationService = hosts[0];
                    destinationNamespace = hosts[1];
                }
                else {
                    destinationService = "localhost".equalsIgnoreCase(host) ? metricTags.getSourceServiceName() : host;
                    destinationNamespace = metricTags.getSourceServiceNamespace();
                }
                Tag dsTag = Tag.of(DESTINATION_SERVICE, destinationService);
                Tag dnsTag = Tag.of(DESTINATION_NAMESPACE, destinationNamespace);
                return Arrays.asList(RestTemplateExchangeTags.method(request), dsTag, dnsTag,
                    RestTemplateExchangeTags.status(response), RestTemplateExchangeTags.clientName(request),
                    Tag.of(REQUEST_OPERATION, parser.parseClassification(request)));
            }
        };
    }

    @Bean
    public WebClientExchangeTagsProvider webClientExchangeTagsProvider() {
        return new WebClientExchangeTagsProvider() {
            @Override
            public Iterable<Tag> tags(ClientRequest request, ClientResponse response, Throwable throwable) {
                String destinationService;
                String destinationNamespace;
                String host = request.url().getHost();
                if(host.contains(".")){
                    String[] hosts = host.split("\\.");
                    destinationService = hosts[0];
                    destinationNamespace = hosts[1];
                }
                else {
                    destinationService = "localhost".equalsIgnoreCase(host) ? metricTags.getSourceServiceName() : host;
                    destinationNamespace = metricTags.getSourceServiceNamespace();
                }
                Tag dsTag = Tag.of(DESTINATION_SERVICE, destinationService);
                Tag dnsTag = Tag.of(DESTINATION_NAMESPACE, destinationNamespace);
                return Arrays.asList(WebClientExchangeTags.method(request), dsTag, dnsTag,
                    WebClientExchangeTags.status(response, new IOException("IO Exception")), WebClientExchangeTags.clientName(request),
                    Tag.of(REQUEST_OPERATION, parser.parseClassification(request)));
            }
        };
    }
}
