package web.core.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import web.core.user.pub.UserChangedMessage;

@Service
public class OnUserChanged {

	public static final String TOPIC_NAME = "user.changed.kconnect";

	private static final Logger LOGGER = LoggerFactory.getLogger(OnUserChanged.class);

	@KafkaListener(topics = OnUserChanged.TOPIC_NAME, groupId = "sampleApp.onUserChanged")
	public void onUserChanged(UserChangedMessage message, Acknowledgment ack) {
		LOGGER.info("user created, id={}, name={}", message.getUserId(), message.getUserName());
		ack.acknowledge();
	}
}
