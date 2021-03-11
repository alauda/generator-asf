package <%= packageName %>.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties()
public class <%= upperProjectName %>Rules {
    private List<<%= upperProjectName %>Rule> classification;
}
