package web.core.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import web.core.event.EventRepository;
import web.core.exc.EntityAlreadyExistsException;
import web.core.exc.UnknownPostalCodeException;
import web.core.user.pub.UserChangedMessage;

@Service
@Transactional(propagation = Propagation.NEVER, rollbackFor = Exception.class)
public class UserCreateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EventRepository eventRepository;

	public UserEntity createUser(String email, String name, int age, String addressPostalCode)
			throws EntityAlreadyExistsException, UnknownPostalCodeException {
		LOGGER.info("Criando usuario {}", email);

		final Optional<UserEntity> exist = userRepository.findUserByEmail(email);
		if (exist.isPresent()) {
			throw new EntityAlreadyExistsException(UserEntity.class, exist.get().getId(), "email", email);
		}

		LOGGER.info("Persistindo usuario {}", email);
		final UserEntity user = userRepository.createUser(email, name, age, addressPostalCode);

		LOGGER.info("Criando evento de criação de usuario");
		final UserChangedMessage message = UserChangedMessage.newBuilder()//
				.setUserId(user.getId().toString())//
				.setUserEmail(user.getEmail()) //
				.setUserName(user.getName())//
				.build();
		eventRepository.createEvent(OnUserChanged.TOPIC_NAME, user.getId().toString(), message);

		if (user.getEmail().indexOf("hotmail") > 0) {
			throw new RuntimeException();
		}

		return user;
	}

}
