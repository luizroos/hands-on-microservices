package web;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class WebConfig implements WebMvcConfigurer {

	@Bean
	public OpenAPI apiDoc() {
		return new OpenAPI().info(new Info().title("User Service").description("User Service"));
	}

}
