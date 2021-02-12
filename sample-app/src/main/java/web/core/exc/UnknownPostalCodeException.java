package web.core.exc;

import java.util.Objects;

public class UnknownPostalCodeException extends Exception {

	private static final long serialVersionUID = 1L;

	private String postalCode;

	public UnknownPostalCodeException(String postalCode) {
		this.postalCode = Objects.requireNonNull(postalCode);
	}

	public String getPostalCode() {
		return postalCode;
	}

}
