package com.example.demo.product;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.DemoApplication;
import com.example.demo.security.AuthenticationRequest;
import com.example.demo.security.AuthenticationResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerIntegrationTest {

	@LocalServerPort
	int randomServerPort;
	private RestTemplate restTemplate;
	private final String username = "admin";
	private final String password = "secret";
	private final AuthenticationRequest user = new AuthenticationRequest(username, password);

	@BeforeEach
	void setUp() {
		restTemplate = new RestTemplate();
	}

	private String createUrl(String uri) {
		return "http://localhost:" + randomServerPort + uri;
	}

	@Test
	@Order(1)
	@DisplayName("POST /products - Adding a new Product")
	void testAddProduct() {
		final String uri = "/login";
		System.out.println("User: " + user);
		ResponseEntity<AuthenticationResponse> loginResponse = restTemplate.postForEntity(createUrl(uri), user,
				AuthenticationResponse.class);
		final String authToken = loginResponse.getBody().getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer "+ authToken );
		Product product = new Product(1, "product1", "category1", "desc1", 100.00);
		ResponseEntity<Product> response = restTemplate.exchange(createUrl("/products"), HttpMethod.POST,
				new HttpEntity<>(product, headers), Product.class);
		assertEquals(product, response.getBody());
	}

	@Test
	@Order(2)
	@DisplayName("GET /products/1 - Find existing product with Token")
	void testGetProductDetails_With_Token() {
		final String uri = "/login";
		ResponseEntity<AuthenticationResponse> loginResponse = restTemplate.postForEntity(createUrl(uri), user,
				AuthenticationResponse.class);
		final String authToken = loginResponse.getBody().getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + authToken);
		Product expectedProduct = new Product(1, "product1", "category1", "desc1", 100.00);
		ResponseEntity<Product> response = restTemplate.exchange(createUrl("/products/1"), HttpMethod.GET,
				new HttpEntity<>(headers), Product.class);
		assertEquals(expectedProduct, response.getBody());
	}

	@Test
	@Order(3)
	@DisplayName("GET /products/1 - Find existing product without Token")
	void testGetProductDetails_Without_Token() {
		try {
			restTemplate.getForEntity(createUrl("/products/1"), String.class);
		} catch (HttpClientErrorException e) {
			assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
		}
	}

	@Test
	@Order(4)
	@DisplayName("GET /products - Get Product list")
	void testGetProductList() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String expected = "[" + "{id:1,name:product1,category:category1,description:desc1,price:100.00}" + "]";
		ResponseEntity<String> response = restTemplate.exchange(createUrl("/products"), HttpMethod.GET,
				new HttpEntity<>(headers), String.class);
		JSONAssert.assertEquals(expected, response.getBody(), false);

	}

	@Test
	@Order(5)
	@DisplayName("PUT /products/1 - Update existing Product with token")
	void testUpdateProduct() throws Exception {
		final String uri = "/login";
		ResponseEntity<AuthenticationResponse> loginResponse = restTemplate.postForEntity(createUrl(uri), user,
				AuthenticationResponse.class);
		final String authToken = loginResponse.getBody().getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + authToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		String expected = "{id:1,name:product2,category:category2,description:desc1,price:100.00}";
		Product product = new Product(1, "product2", "category2", "desc1", 100.00);
		ResponseEntity<String> response = restTemplate.exchange(createUrl("/products/1"), HttpMethod.PUT,
				new HttpEntity<>(product, headers), String.class);
		JSONAssert.assertEquals(expected, response.getBody(), false);

	}

	@Test
	@Order(6)
	@DisplayName("DELETE /products/1 - Delete existing Product with token")
	void testDeleteProduct() {
		final String uri = "/login";
		ResponseEntity<AuthenticationResponse> loginResponse = restTemplate.postForEntity(createUrl(uri), user,
				AuthenticationResponse.class);
		final String authToken = loginResponse.getBody().getToken();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + authToken);
		int id = 1;
		ResponseEntity<String> response = restTemplate.exchange(createUrl("/products/" + id), HttpMethod.DELETE,
				new HttpEntity<>(headers), String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

}
