package web;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(GreetingResource.class);

	@GetMapping(path = "/hello")
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public String hello() {
		LOGGER.info("request");
		return Optional.ofNullable(System.getenv("HELLO_MESSAGE")).orElse("hello");
	}

}
