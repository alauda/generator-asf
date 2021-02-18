package <%= packageName %>.metrics;

import <%= packageName %>.metrics.<%= upperProjectName %>MetricTags;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.AutoTimer;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTags;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTagsProvider;
import org.springframework.boot.actuate.metrics.web.reactive.client.DefaultWebClientExchangeTagsProvider;
import org.springframework.boot.actuate.metrics.web.reactive.client.MetricsWebClientFilterFunction;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTags;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTagsProvider;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTags;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
public class <%= upperProjectName %>WebClientConfig {

    private static Logger logger = LoggerFactory.getLogger(<%= upperProjectName %>WebClientConfig.class);
    @Autowired
    <%= upperProjectName %>MetricTags metricTags;

    @Bean
    public WebClient createWebClient(WebClient.Builder webClientBuilder) {

        WebClient webClient = webClientBuilder
            .defaultHeader("X-Client-Name", metricTags.getSourceServiceName())
            .defaultHeader("X-Client-Namespace", metricTags.getSourceServiceNamespace())
            .build();
        return webClient;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder templateBuilder) {
        return templateBuilder.requestFactory(OkHttp3ClientHttpRequestFactory::new)
            .defaultHeader("X-Client-Name", metricTags.getSourceServiceName())
            .defaultHeader("X-Client-Namespace", metricTags.getSourceServiceNamespace())
            .build();
    }

    @Bean
    public WebMvcTagsProvider webMvcTagsProvider() {
        return new WebMvcTagsProvider() {
            @Override
            public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable exception) {
                String clientName = request.getHeader("X-Client-Name");
                String clientNamespace = request.getHeader("X-Client-Namespace");
                Tags tags = Tags.of(WebMvcTags.method(request),
                    WebMvcTags.exception(exception), WebMvcTags.status(response), WebMvcTags.outcome(response));
                if(io.micrometer.core.instrument.util.StringUtils.isNotEmpty(clientName)) {
                    tags = tags.and(Tag.of("client_name", clientName));
                }
                if(io.micrometer.core.instrument.util.StringUtils.isNotEmpty(clientNamespace)) {
                    tags = tags.and(Tag.of("client_namespace", clientNamespace));
                }
                return tags;
            }

            @Override
            public Iterable<Tag> getLongRequestTags(HttpServletRequest request, Object handler) {
                return new ArrayList<>();
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
                Tag dsTag = Tag.of("destination_service", destinationService);
                Tag dnsTag = Tag.of("destination_namespace", destinationNamespace);
                return Arrays.asList(RestTemplateExchangeTags.method(request), dsTag, dnsTag,
                    RestTemplateExchangeTags.status(response), RestTemplateExchangeTags.clientName(request));
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
                Tag dsTag = Tag.of("destination_service", destinationService);
                Tag dnsTag = Tag.of("destination_namespace", destinationNamespace);
                return Arrays.asList(WebClientExchangeTags.method(request), dsTag, dnsTag,
                    WebClientExchangeTags.status(response, new IOException("IO Exception")), WebClientExchangeTags.clientName(request));
            }
        };
    }
}
