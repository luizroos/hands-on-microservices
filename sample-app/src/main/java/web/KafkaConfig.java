package web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import web.core.user.UserChangedMessage;

@EnableKafka
public class KafkaConfig {

	@Value("${kafka.bootstrapAddress}")
	private String kafkaBootstrapAddress;

	@Value("${kafka.consumer.groupId}")
	private String kafkaConsumerGroupId;

	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public ConsumerFactory<String, UserChangedMessage> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroupId);
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
				new JsonDeserializer<>(UserChangedMessage.class));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, UserChangedMessage> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, UserChangedMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(6);
		factory.setRetryTemplate(retryTemplate());
		factory.getContainerProperties().setAckMode(AckMode.MANUAL);
		return factory;
	}

	private RetryTemplate retryTemplate() {
		final RetryTemplate template = new RetryTemplate();
		template.setRetryPolicy(retryPolicy());
		template.setBackOffPolicy(backOffPolicy());
		return template;
	}

	private RetryPolicy retryPolicy() {
		return new AlwaysRetryPolicy();
	}

	private BackOffPolicy backOffPolicy() {
		final ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
		policy.setMaxInterval(TimeUnit.SECONDS.toMillis(30));
		return policy;
	}
}
