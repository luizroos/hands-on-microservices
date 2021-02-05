package web.restapi;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import web.GlobalExceptionHandler.BadRequestException;
import web.core.exc.EntityAlreadyExistsException;
import web.core.exc.UnknownPostalCodeException;
import web.core.user.UserCreateService;
import web.core.user.UserEntity;

@RestController
public class UserResource {

	@Autowired
	private UserCreateService userCreateService;

	@PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserCreateResponse createUser(@Valid @RequestBody UserCreateForm userCreateForm) throws BadRequestException {
		final UserEntity createdUser;
		try {
			createdUser = userCreateService.createUser(userCreateForm.getEmail(), userCreateForm.getEmail(),
					userCreateForm.getAge(), userCreateForm.getAddressPostalCode());
		} catch (EntityAlreadyExistsException e) {
			throw new BadRequestException(String.format("Usuario com %s %s ja cadastrado com id %s", e.getSearchField(),
					e.getSearchValue(), e.getEntityId()));
		} catch (UnknownPostalCodeException e) {
			throw new BadRequestException(
					String.format("Postal code %s não existente no estado de São Paulo", e.getPostalCode()));
		}
		return new UserCreateResponse(createdUser);
	}

}
