package web;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

import web.core.user.OnUsersChanged;

@EnableRabbit
public class RabbitConfig {

	public static String USER_CHANGED_EXCHANGE = "user_changed.exchange";

	@Bean
	public Declarables declare() {
		final Queue gmailUserChangedQueue = new Queue(OnUsersChanged.GMAIL_USER_CHANGED, true);
		final Queue logUserChangedQueue = new Queue(OnUsersChanged.LOG_USER_CHANGED, true);
		final TopicExchange directExchange = new TopicExchange(USER_CHANGED_EXCHANGE);

		return new Declarables(gmailUserChangedQueue, //
				logUserChangedQueue, //
				directExchange, BindingBuilder.bind(gmailUserChangedQueue).to(directExchange).with("gmail.com"), //
				BindingBuilder.bind(logUserChangedQueue).to(directExchange).with("*.*"));
	}

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(converter);
		return rabbitTemplate;
	}

}
