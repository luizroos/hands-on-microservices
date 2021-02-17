package web.core.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.core.exc.EntityAlreadyExistsException;
import web.core.exc.UnknownPostalCodeException;
import web.core.postalcode.PostalCodeService;
import web.core.postalcode.PostalCodeService.Address;

@Service
public class UserCreateService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostalCodeService postalCodeService;

	public UserEntity createUser(String email, String name, int age, String addressPostalCode)
			throws EntityAlreadyExistsException, UnknownPostalCodeException {
		LOGGER.info("Criando usuario {}", email);

		final Optional<UserEntity> exist = userRepository.findUserByEmail(email);
		if (exist.isPresent()) {
			throw new EntityAlreadyExistsException(UserEntity.class, exist.get().getId(), "email", email);
		}

		LOGGER.info("Validando estado do cep {}", addressPostalCode);
		final Address address = postalCodeService.getPostalCode(addressPostalCode);
		if (address == null || !address.getUf().equalsIgnoreCase("SP")) {
			throw new UnknownPostalCodeException(addressPostalCode);
		}

		LOGGER.info("Persistindo usuario {}", email);
		return userRepository.createUser(email, name, age, addressPostalCode);
	}

}
