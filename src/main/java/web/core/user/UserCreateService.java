package web.core.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import web.core.exc.EntityAlreadyExistsException;
import web.core.exc.UnknownPostalCodeException;
import web.core.postalcode.PostalCodeService;
import web.core.postalcode.PostalCodeService.Address;

@Service
@Transactional(propagation = Propagation.NEVER)
public class UserCreateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostalCodeService postalCodeService;

	@Autowired
	private UserCreateService userCreateService;

	@Transactional(readOnly = true)
	public void _validate(String email) throws EntityAlreadyExistsException {
		LOGGER.info("Criando usuario {}", email);

		final Optional<UserEntity> exist = userRepository.findUserByEmail(email);
		if (exist.isPresent()) {
			throw new EntityAlreadyExistsException(UserEntity.class, exist.get().getId(), "email", email);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public UserEntity _persist(String email, String name, int age, String addressPostalCode) {
		LOGGER.info("Persistindo usuario {}", email);
		return userRepository.createUser(email, name, age, addressPostalCode);
	}

	public UserEntity createUser(String email, String name, int age, String addressPostalCode)
			throws EntityAlreadyExistsException, UnknownPostalCodeException {
		userCreateService._validate(email);

		LOGGER.info("Validando estado do cep {}", addressPostalCode);
		final Address address = postalCodeService.getPostalCode(addressPostalCode);
		if (address == null || !address.getUf().equalsIgnoreCase("SP")) {
			throw new UnknownPostalCodeException(addressPostalCode);
		}

		return userCreateService._persist(email, name, age, addressPostalCode);
	}

}
