package com.screenprog.application;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.screenprog.application.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.Document;
import java.net.URI;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BankAccountManagementSystemApplicationTests {
	@Autowired
	private TestRestTemplate restTemplate;


	@Test
	void addNewCustomer() {
			Customer customer = new Customer();
			customer.setFirstName("John");
			customer.setLastName("Maxwell");
			customer.setDob(Date.valueOf("2000-12-10").toLocalDate());
			customer.setAddress("Street 12, Lava Hotel, Main City, London");
			customer.setPassword("secured");
			customer.setPhoneNumber("8909345786");
			customer.setEmail("johnmaxwell@example.com");

			ResponseEntity<Void> response = restTemplate
					.postForEntity("/admin/add-one-customer", customer, Void.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

	}

	@Test
	void shouldCreateAnAccount() {
		AccountDTO accountDTO = new AccountDTO(1L, 1000.00, Status.ACTIVE, AccountType.SAVING);
		ResponseEntity<String> response = restTemplate
				.postForEntity("/admin/add-one-account", accountDTO, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfCreated = response.getHeaders().getLocation();
		ResponseEntity<String> accountResponse = restTemplate
				.getForEntity(locationOfCreated, String.class);
		assertThat(accountResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(accountResponse.getBody());
		Number amount = documentContext.read("$.balance");
		assertThat(amount).isEqualTo(1000.00);
	}

	@Test
	void shouldNotCreateAccountOfNonExistingCustomer(){
		AccountDTO accountDTO = new AccountDTO(2L, 1000.00, Status.ACTIVE, AccountType.SAVING);
		ResponseEntity<String> response = restTemplate
				.postForEntity("/admin/add-one-account", accountDTO, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

}
