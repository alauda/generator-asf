package <%= packageName %>.config;

import lombok.Data;

@Data
public class <%= upperProjectName %>Request {
    private String path;
    private String method;
}
