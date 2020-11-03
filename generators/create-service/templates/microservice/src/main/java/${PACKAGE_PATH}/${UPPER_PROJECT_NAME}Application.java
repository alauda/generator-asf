package <%= packageName %>;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class <%= upperProjectName %>Application {

	public static void main(String[] args) {
		SpringApplication.run(<%= upperProjectName %>Application.class, args);
	}

}
