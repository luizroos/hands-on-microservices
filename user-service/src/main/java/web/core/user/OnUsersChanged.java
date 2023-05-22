package web.core.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class OnUsersChanged {

	private static final Logger LOGGER = LoggerFactory.getLogger(OnUsersChanged.class);

	public static final String GMAIL_USER_CHANGED = "gmail.user.changed";

	public static final String LOG_USER_CHANGED = "log.user.changed";

	@RabbitListener(queues = OnUsersChanged.GMAIL_USER_CHANGED, concurrency = "5")
	public void gmailUserChanged(UserChangedMessage message) {
		LOGGER.info("usuario com email do gmail alterado, id={}, name={}", message.getUserId(), message.getUserName());
	}

	@RabbitListener(queues = OnUsersChanged.LOG_USER_CHANGED, concurrency = "5")
	public void logUserChanged(UserChangedMessage message) {
		LOGGER.info("usuario alterado, id={}, name={}", message.getUserId(), message.getUserName());
		if (message.getUserEmail().indexOf("hotmail") > 0) {
			LOGGER.error("erro ao consumir mensagem do usuario {}", message.getUserId());
			throw new RuntimeException();
		}
	}
}
