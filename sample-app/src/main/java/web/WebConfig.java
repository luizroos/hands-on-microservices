package web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class WebConfig implements WebMvcConfigurer {

	@Value("${cassandra.contactpoints}")
	private String contactPoints;

	@Value("${cassandra.port}")
	private String port;

	@Value("${cassandra.keyspace}")
	private String keyspace;

	@Value("${cassandra.username}")
	private String userName;

	@Value("${cassandra.password}")
	private String password;

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
		session.setPassword(password);
		session.setUsername(userName);
		session.setKeyspaceName(keyspace);
		session.setContactPoints(contactPoints);
		session.setPort(Integer.valueOf(port));
		session.setLocalDatacenter("vm");
		return session;
	}

	@Bean
	public CassandraTemplate cassandraTemplate(CqlSessionFactoryBean sessionFactory) throws Exception {
		return new CassandraTemplate(sessionFactory.getObject());
	}

}
