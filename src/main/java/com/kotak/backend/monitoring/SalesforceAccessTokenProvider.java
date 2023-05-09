package com.kotak.backend.monitoring;

import com.google.gson.Gson;
import com.kotak.backend.monitoring.connector.DelegatingBayeuxParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.kotak.backend.monitoring.connector.BayeuxParameters;

import java.util.Map;

@Component
public class SalesforceAccessTokenProvider {
	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private Gson gson;

	private static Logger logger = LoggerFactory.getLogger(SalesforceAccessTokenProvider.class);

	public BayeuxParameters updateBayeuxParametersWithSalesforceAccessToken(String sfdcHost, String username,
			String password, String consumerKey, String secret, BayeuxParameters parameters) {
		return new DelegatingBayeuxParameters(parameters) {
			@Override
			public String bearerToken() {
				String token = "";
				try {
					String url = sfdcHost + "/services/oauth2/token";
					HttpHeaders headers = new HttpHeaders();
					headers.add("Content-Type", "application/x-www-form-urlencoded");
					String tokenRequest = "grant_type=password&client_id=" + consumerKey + "&client_secret=" + secret
							+ "&username=" + username + "&password=" + password;
					HttpEntity<String> request = new HttpEntity<>(tokenRequest, headers);
					ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
					String responseText = response.getBody();
					@SuppressWarnings("unchecked")
					Map<String, String> jwtMap = gson.fromJson(responseText, Map.class);
					token = jwtMap.get("access_token");
					logger.info("received salesforce access token by salesforce username password"+token);
				} catch (Exception e) {
					logger.error("Failed to get salesforce access token", e);
					System.exit(1);
				}
				return token;
			}
		};
	}

}
