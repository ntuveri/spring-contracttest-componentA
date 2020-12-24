package com.example.componentA;


import java.util.Collections;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.consumer.model.MockProviderConfig;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ConsumerContractTests {

	private static final String PROVIDER_NAME = "componentB";
	private static final String CONSUMER_NAME = "componentA";

	private TestRestTemplate consumer = new TestRestTemplate();

	@Rule
	public PactProviderRule provider = new PactProviderRule
		(PROVIDER_NAME, MockProviderConfig.LOCALHOST, SocketUtils.findAvailableTcpPort(), this);

	@Pact(provider = PROVIDER_NAME, consumer = CONSUMER_NAME)
	public RequestResponsePact getAlligatorMaryOk(PactDslWithProvider builder) {

		RequestResponsePact responsePact = builder
			.given("there is an alligator named Mary")
			.uponReceiving("a request for an alligator")
			.path("/alligators/Mary")
			.method(HttpMethod.GET.name())
			.headers(Collections.singletonMap(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString()))
			.willRespondWith()
			.headers(Collections.singletonMap(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
			.status(HttpStatus.OK.value())
			.body(new PactDslJsonBody().stringValue("name", "Mary"))
			.toPact();

		return responsePact;
	}

	@Test
	@PactVerification(value = PROVIDER_NAME, fragment = "getAlligatorMaryOk")
	public void givenAlligatorMaryExists_WhenAlligatorMaryIsRequested_ThenResponseIsOk() {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> request = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = this.consumer.exchange(
			this.provider.getUrl() + "/alligators/Mary", HttpMethod.GET, request, String.class);

		Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
	}

	/*
	@Pact(provider = PROVIDER_NAME, consumer = CONSUMER_NAME)
	public RequestResponsePact getAlligatorMaryNotFound(PactDslWithProvider builder) {

		RequestResponsePact responsePact = builder

			.given("there is not an alligator named Mary")
			.uponReceiving("a request for an alligator")
			.path("/alligators/Mary")
			.method(HttpMethod.GET.name())
			.headers(Collections.singletonMap(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString()))
			.willRespondWith()
			.headers(Collections.singletonMap(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
			.status(HttpStatus.NOT_FOUND.value())
			.toPact();

		return responsePact;
	}

	@Test
	@PactVerification(value = PROVIDER_NAME, fragment = "getAlligatorMaryNotFound")
	public void givenAlligatorMaryExists_WhenAlligatorMaryIsRequested_ThenResponseIsNotFound() {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> request = new HttpEntity<>(null, headers);

		ResponseEntity<String> response = this.consumer.exchange(
			this.provider.getUrl() + "/alligators/Mary", HttpMethod.GET, request, String.class);

		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}
	*/
}
