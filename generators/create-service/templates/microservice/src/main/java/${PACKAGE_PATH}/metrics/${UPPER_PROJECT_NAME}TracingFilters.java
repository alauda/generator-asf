package <%= packageName %>.metrics;

import brave.Span;
import brave.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.instrument.web.TraceWebServletAutoConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component
@Order(TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER + 1)
public class <%= upperProjectName %>TracingFilters extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(<%= upperProjectName %>TracingFilters.class);
    private final Tracer tracer;
    @Autowired
    private <%= upperProjectName %>MetricTags metricTags;

    <%= upperProjectName %>TracingFilters(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        Span currentSpan = this.tracer.currentSpan();
        logger.info("Getting current span");
        if (currentSpan == null) {
            chain.doFilter(request, response);
            return;
        }
        // add some custom tags
        currentSpan.tag("msname", metricTags.getSourceServiceName());
        chain.doFilter(request, response);
    }
}
