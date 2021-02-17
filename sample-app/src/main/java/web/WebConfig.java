package web;

import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.datastax.oss.driver.api.core.CqlSession;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class WebConfig implements WebMvcConfigurer {

	@Bean
	public OpenAPI apiDoc() {
		return new OpenAPI().info(new Info().title("Sample APP").description("Sample APP"));
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CqlSessionFactoryBean session() throws Exception {
		final CqlSessionFactoryBean session = new CqlSessionFactoryBean();
		session.setPassword("");
		session.setUsername("");
		session.setKeyspaceName("sample-cassandra-cluster");
		session.setContactPoints("127.0.0.1");
		return session;
	}

	@Bean
	public CassandraTemplate cassandraTemplate(CqlSessionFactoryBean sessionFactory) throws Exception {
		return new CassandraTemplate(sessionFactory.getObject());
	}

}
