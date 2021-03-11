package <%= packageName %>.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.server.ServerWebExchange;
import java.util.Objects;

@Component
public class <%= upperProjectName %>RulesParser {
    private static final Logger log = LoggerFactory.getLogger(<%= upperProjectName %>RulesParser.class);

    @Autowired
    private <%= upperProjectName %>Rules rules;

    public String parseClassification(ServerWebExchange exchange) {
        if(Objects.isNull(rules) || Objects.isNull(rules.getClassification())) {
            return <%= upperProjectName %>Rule.UNKNOWN.getValue();
        }
        <%= upperProjectName %>Request req = new <%= upperProjectName %>Request();
        req.setMethod(exchange.getRequest().getMethod().name().toUpperCase());
        req.setPath(exchange.getRequest().getPath().value());
        return getRuleValue(req);
    }

    public String parseClassification(HttpRequest request) {
        if(Objects.isNull(rules) || Objects.isNull(rules.getClassification())) {
            return <%= upperProjectName %>Rule.UNKNOWN.getValue();
        }
        <%= upperProjectName %>Request req = new <%= upperProjectName %>Request();
        req.setMethod(request.getMethod().name());
        req.setPath(request.getURI().getPath());
        return getRuleValue(req);
    }
    public String parseClassification(ClientRequest request) {
        if(Objects.isNull(rules) || Objects.isNull(rules.getClassification())) {
            return <%= upperProjectName %>Rule.UNKNOWN.getValue();
        }
        <%= upperProjectName %>Request req = new <%= upperProjectName %>Request();
        req.setMethod(request.method().name());
        req.setPath(request.url().getPath());
        return getRuleValue(req);
    }

    private String getRuleValue(<%= upperProjectName %>Request req) {
        <%= upperProjectName %>RequestWrapper model = new <%= upperProjectName %>RequestWrapper();
        model.setRequest(req);
        <%= upperProjectName %>Rule rule = rules.getClassification().stream()
            .filter(r -> {
                ExpressionParser parser = new SpelExpressionParser();
                Expression exp = parser.parseExpression(r.getCondition());
                return (Boolean) exp.getValue(model);
            })
            .findFirst()
            .orElse(<%= upperProjectName %>Rule.UNKNOWN);
        log.info("Got value {}", rule.getValue());
        return rule.getValue();
    }
}
