package web.core.user;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.core.exc.EntityAlreadyExistsException;
import web.core.exc.UnknownPostalCodeException;
import web.core.postalcode.PostalCodeService;
import web.core.postalcode.PostalCodeService.Address;

@Service
@Transactional
public class UserCreateService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostalCodeService postalCodeService;

	public UserEntity createUser(String email, String name, int age, String addressPostalCode)
			throws EntityAlreadyExistsException, UnknownPostalCodeException {
		final Optional<UserEntity> exist = userRepository.findUserByEmail(email);
		if (exist.isPresent()) {
			throw new EntityAlreadyExistsException(UserEntity.class, exist.get().getId(), "email", email);
		}

		final Address address = postalCodeService.getPostalCode(addressPostalCode);
		if (address == null || !address.getUf().equalsIgnoreCase("SP")) {
			throw new UnknownPostalCodeException(addressPostalCode);
		}

		final UserEntity user = userRepository.createUser(email, name, age, addressPostalCode);
		return user;
	}

}
