package web.core.user;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.core.exc.EntityAlreadyExistsException;

@Service
@Transactional
public class UserCreateService {

	@Autowired
	private UserRepository userRepository;

	public UserEntity createUser(String email, String name, int age, String addressPostalCode)
			throws EntityAlreadyExistsException {
		final Optional<UserEntity> exist = userRepository.findUserByEmail(email);
		if (exist.isPresent()) {
			throw new EntityAlreadyExistsException(UserEntity.class, exist.get().getId(), "email", email);
		}
		final UserEntity user = userRepository.createUser(email, name, age, addressPostalCode);
		return user;
	}

}
