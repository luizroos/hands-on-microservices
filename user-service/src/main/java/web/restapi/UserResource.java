package web.restapi;

import javax.validation.Valid;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import web.GlobalExceptionHandler.BadRequestException;
import web.core.exc.EntityAlreadyExistsException;
import web.core.user.UserCreateService;
import web.core.user.UserEntity;

@RestController
public class UserResource {

	@Autowired
	private UserCreateService userCreateService;

	@GetMapping(path = "/users/random", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserCreateResponse createRandomUser() throws BadRequestException {
		String name = RandomStringUtils.randomAlphabetic(8);
		final UserCreateForm userCreateForm = new UserCreateForm();
		userCreateForm.setAge((int) (18 + (Math.random() * 30)));
		userCreateForm.setAddressPostalCode(RandomStringUtils.randomNumeric(8));
		userCreateForm.setName(name);
		userCreateForm.setEmail(String.format("%s@%s.com", name, RandomStringUtils.randomAlphabetic(5)));
		return createUser(userCreateForm);
	}

	@PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserCreateResponse createUser(@Valid @RequestBody UserCreateForm userCreateForm) throws BadRequestException {
		final UserEntity createdUser;
		try {
			createdUser = userCreateService.createUser(userCreateForm.getEmail(), userCreateForm.getName(),
					userCreateForm.getAge(), userCreateForm.getAddressPostalCode());
		} catch (EntityAlreadyExistsException e) {
			throw new BadRequestException(String.format("Usuario com %s %s ja cadastrado com id %s", e.getSearchField(),
					e.getSearchValue(), e.getEntityId()));
		}
		return new UserCreateResponse(createdUser);
	}

}
