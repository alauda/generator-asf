package <%= packageName %>.config;

import lombok.Data;

@Data
public class <%= upperProjectName %>Rule {
    private String value;
    private String condition;
    public static final <%= upperProjectName %>Rule UNKNOWN;
    static  {
        UNKNOWN = new <%= upperProjectName %>Rule();
        UNKNOWN.setValue("unknown");
    }
}
