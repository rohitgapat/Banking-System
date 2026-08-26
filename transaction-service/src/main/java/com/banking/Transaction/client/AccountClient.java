package com.banking.Transaction.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import com.banking.Transaction.exception.AccountOperationException;
import com.banking.Transaction.model.AccountResponse;

@Component
public class AccountClient {

	private final RestClient restClient;

	public AccountClient(
			RestClient.Builder builder,
			@Value("${account.service.url}") String accountServiceUrl,
			@Value("${account.service.username}") String username,
			@Value("${account.service.password}") String password) {

		this.restClient = builder
				.baseUrl(accountServiceUrl)
				.defaultHeaders(headers ->
				headers.setBasicAuth(username, password))
				.build();
	}

	public AccountResponse getAccount(String accountNumber) {

		return restClient.get()
				.uri("/accounts/number/{accountNumber}", accountNumber)
				.retrieve()
				.body(AccountResponse.class);
	}

	public AccountResponse deposit(String accountNumber, Double amount) {

		try {

			return restClient.patch()
					.uri(uriBuilder -> uriBuilder
							.path("/accounts/{accountNumber}/deposit")
							.queryParam("amount", amount)
							.build(accountNumber))
					.retrieve()
					.body(AccountResponse.class);

		} catch (HttpClientErrorException ex) {

			throw new AccountOperationException(
					ex.getResponseBodyAsString());
		}
	}

	public AccountResponse withdraw(String accountNumber, Double amount) {

		try {

			return restClient.patch()
					.uri(uriBuilder -> uriBuilder
							.path("/accounts/{accountNumber}/withdraw")
							.queryParam("amount", amount)
							.build(accountNumber))
					.retrieve()
					.body(AccountResponse.class);

		} catch (HttpClientErrorException ex) {

			throw new AccountOperationException(
					ex.getResponseBodyAsString());
		}
	}
}