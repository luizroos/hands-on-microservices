package web.core.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class OnUserChanged {

	private static final Logger LOGGER = LoggerFactory.getLogger(OnUserChanged.class);

	@KafkaListener(topics = UserChangedMessage.TOPIC_NAME, groupId = "sampleApp.onUserChanged")
	public void onUserChanged(UserChangedMessage message, Acknowledgment ack) {
		LOGGER.info("user created, id={}, name={}", message.getUserId(), message.getUserName());
		if (message.getUserName().equalsIgnoreCase("consumer_name_err")) {
			LOGGER.info("Não foi possivel processar a alteração no usuario {}", message.getUserId());
			throw new RuntimeException("Não é possivel tratar esse nome");
		}
		ack.acknowledge();
	}
}
