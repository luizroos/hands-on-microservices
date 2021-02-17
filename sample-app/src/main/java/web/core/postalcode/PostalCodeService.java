package web.core.postalcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PostalCodeService {

	@Value("${correios.host}")
	private String correiosHost;

	@Autowired
	public RestTemplate restTemplate;

	public Address getPostalCode(String postalCode) {
		final ResponseEntity<Address> response = restTemplate
				.getForEntity(String.format("http://%s/postalcodes?%s", correiosHost, postalCode), Address.class);
		return response.getBody();
	}

	public static class Address {

		private String address;

		private String city;

		private String uf;

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getUf() {
			return uf;
		}

		public void setUf(String uf) {
			this.uf = uf;
		}

	}

}
