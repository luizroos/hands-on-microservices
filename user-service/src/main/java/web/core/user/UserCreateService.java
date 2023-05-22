package web.core.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import web.RabbitConfig;
import web.core.exc.EntityAlreadyExistsException;
import web.core.exc.UnknownPostalCodeException;

@Service
@Transactional(propagation = Propagation.NEVER)
public class UserCreateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public UserEntity createUser(String email, String name, int age, String addressPostalCode)
			throws EntityAlreadyExistsException, UnknownPostalCodeException {
		LOGGER.info("Criando usuario {}", email);

		if (email.indexOf("@") < 0) {
			email += "@corp.com";
		}

		final Optional<UserEntity> exist = userRepository.findUserByEmail(email);
		if (exist.isPresent()) {
			throw new EntityAlreadyExistsException(UserEntity.class, exist.get().getId(), "email", email);
		}

		LOGGER.info("Persistindo usuario {}", email);
		final UserEntity user = userRepository.createUser(email, name, age, addressPostalCode);

		rabbitTemplate.convertAndSend(RabbitConfig.USER_CHANGED_EXCHANGE, email.substring(email.indexOf("@") + 1),
				new UserChangedMessage(user));

		return user;
	}

}
