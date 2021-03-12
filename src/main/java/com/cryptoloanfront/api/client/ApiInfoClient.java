package com.cryptoloanfront.api.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

@Component
public class ApiInfoClient extends ApiClient{

    public ApiInfoClient() {
        super(ApiInfoClient.class);
    }

    public Set<String> fetchCurrencies() {
        try {
            Optional<String[]> response = Optional.ofNullable(restTemplate.getForObject(getAppCurrenciesURI(), String[].class));
            return new HashSet<>(new ArrayList<>(response
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList())));
        } catch (RestClientException e ) {
            LOGGER.warn(e.getMessage());
        }
        return Collections.emptySet();
    }

    public Integer fetchTimeInDays() {
        try {
            ResponseEntity<Integer> response = restTemplate.getForEntity(getAppTimeURI(), int.class);

            if (response.getStatusCode() == HttpStatus.OK) return response.getBody();
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
        return 0;
    }

    public BigDecimal getExchangeForCurrencies(String currencyGiven, String currencyReturned) {
        try {
            ResponseEntity<BigDecimal> response = restTemplate.getForEntity(getExchangeURI(currencyGiven,currencyReturned),BigDecimal.class);
            if (response.getStatusCode() == HttpStatus.OK) return response.getBody();
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private URI getAppCurrenciesURI() {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/app/currencies").build().encode().toUri();
    }

    private URI getAppTimeURI() {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/app/time").build().encode().toUri();
    }

    private URI getBlockChainURI() {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/blockchain/rates").build().encode().toUri();
    }

    private URI getExchangeURI(String curr1, String curr2) {
        return UriComponentsBuilder.fromHttpUrl(
                apiConfig.getApiEndpoint()+
                        "/app/exchange/"+
                        curr1.toUpperCase()+
                        '/'+
                        curr2.toUpperCase())
                .build().encode().toUri();
    }

    private URI getExchangesURI() {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/exchange/rates").build().encode().toUri();
    }

}
