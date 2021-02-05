package web.core.user;

import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.core.exc.EntityAlreadyExistsException;

@Service
@Transactional(rollbackOn = Exception.class)
public class UserCreateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateService.class);

	@Autowired
	private UserRepository userRepository;

	public UserEntity createUser(String email, String name, int age, String addressPostalCode)
			throws EntityAlreadyExistsException {
		LOGGER.info("Criando usuario {}", email);

		final Optional<UserEntity> exist = userRepository.findUserByEmail(email);
		if (exist.isPresent()) {
			throw new EntityAlreadyExistsException(UserEntity.class, exist.get().getId(), "email", email);
		}

		LOGGER.info("Persistindo usuario {}", email);
		final UserEntity user = userRepository.createUser(email, name, age, addressPostalCode);
		return user;
	}

}
