package com.cryptoloanfront.api.client;

import com.cryptoloanfront.domain.Loan;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ApiLoansClient extends ApiClient {

    public ApiLoansClient() {
        super(ApiLoansClient.class);
    }

    public void deleteLoan(int id) {
        try {
            restTemplate.delete(getLoanURI(id));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public void updateLoan(Loan loan) {
        try {
        restTemplate.put(getLoansURI(),createJsonRequest(loan));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public void createLoan(Loan loan) {
        try {
            restTemplate.postForObject(getLoansURI(),createJsonRequest(loan),String.class);
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public Optional<Loan> fetchLoan(int id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(getLoanURI(id), Loan.class));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
        return Optional.empty();
    }

    public List<Loan> fetchLoans() {
        try {
            Optional<Loan[]> loanTypes =  Optional.ofNullable(restTemplate.getForObject(getLoansURI(),Loan[].class));
            return loanTypes
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(p -> Objects.nonNull(p.getId()))
                    .collect(Collectors.toList());

        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    private URI getLoanURI(int id) {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/loans/"+id).build().encode().toUri();
    }

    private URI getLoansURI() {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/loans").build().encode().toUri();
    }

}
