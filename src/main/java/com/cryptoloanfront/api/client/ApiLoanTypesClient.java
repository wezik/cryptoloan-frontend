package com.cryptoloanfront.api.client;

import com.cryptoloanfront.domain.LoanType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ApiLoanTypesClient extends ApiClient {

    public ApiLoanTypesClient() {
        super(ApiLoanTypesClient.class);
    }

    public void deleteLoanType(int id) {
        try {
            restTemplate.delete(getLoanTypeURI(id));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public void updateLoanType(LoanType loanType) {
        try {
        restTemplate.put(getLoanTypesURI(),createJsonRequest(loanType));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public void createLoanType(LoanType loanType) {
        try {
            restTemplate.postForObject(getLoanTypesURI(),createJsonRequest(loanType),String.class);
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public Optional<LoanType> fetchLoanType(int id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(getLoanTypeURI(id), LoanType.class));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
        return Optional.empty();
    }

    public List<LoanType> fetchLoanTypes() {
        try {
            Optional<LoanType[]> loanTypes =  Optional.ofNullable(restTemplate.getForObject(getLoanTypesURI(),LoanType[].class));
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

    private URI getLoanTypeURI(int id) {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/loanTypes/"+id).build().encode().toUri();
    }

    private URI getLoanTypesURI() {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/loanTypes").build().encode().toUri();
    }

}
