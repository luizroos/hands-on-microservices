package web;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<Void> processException(Exception ex) {
		LOGGER.error(ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorBody> badRequest(BadRequestException ex) {
		return ResponseEntity.badRequest().body(ex.getBody());
	}

	public static class ErrorBody {

		private final String message;

		public ErrorBody(String message) {
			this.message = Objects.requireNonNull(message);
		}

		public String getMessage() {
			return message;
		}

	}

	public static class BadRequestException extends Exception {

		private static final long serialVersionUID = 1L;

		private final ErrorBody body;

		public BadRequestException(String message) {
			this.body = new ErrorBody(message);
		}

		public BadRequestException(ErrorBody body) {
			this.body = Objects.requireNonNull(body);
		}

		public ErrorBody getBody() {
			return body;
		}
	}

}
