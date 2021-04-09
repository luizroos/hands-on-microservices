package web.core.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.core.exc.EntityAlreadyExistsException;
import web.core.exc.UnknownPostalCodeException;
import web.core.user.pub.UserChangedMessage;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserCreateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private KafkaTemplate<String, Object> KafkaTemplate;

	@Transactional(readOnly = true)
	public void _validate(String email) throws EntityAlreadyExistsException {
	}

	public UserEntity createUser(String email, String name, int age, String addressPostalCode)
			throws EntityAlreadyExistsException, UnknownPostalCodeException {
		LOGGER.info("Criando usuario {}", email);

		final Optional<UserEntity> exist = userRepository.findUserByEmail(email);
		if (exist.isPresent()) {
			throw new EntityAlreadyExistsException(UserEntity.class, exist.get().getId(), "email", email);
		}

		LOGGER.info("Persistindo usuario {}", email);
		final UserEntity user = userRepository.createUser(email, name, age, addressPostalCode);

		final UserChangedMessage userChangeMessage = UserChangedMessage.newBuilder()//
				.setUserName(user.getName()) //
				.setUserEmail(user.getEmail()) //
				.setUserId(user.getId().toString()).build();
		KafkaTemplate.send(OnUserChanged.TOPIC_NAME, user.getId().toString(), userChangeMessage);

		if (user.getEmail().indexOf("hotmail") > 0) {
			throw new RuntimeException();
		}
		return user;
	}

}
