package com.cryptoloanfront.api.client;

import com.cryptoloanfront.domain.Installment;
import com.cryptoloanfront.domain.Loan;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ApiInstallmentsClient extends ApiClient {

    public ApiInstallmentsClient() {
        super(ApiInstallmentsClient.class);
    }

    public void deleteInstallment(int id) {
        try {
            restTemplate.delete(getInstallmentURI(id));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public void updateInstallment(Installment installment) {
        try {
        restTemplate.put(getInstallmentsURI(),createJsonRequest(installment));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public void createInstallment(Installment installment) {
        try {
            restTemplate.postForObject(getInstallmentsURI(),createJsonRequest(installment),String.class);
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public Optional<Installment> fetchInstallment(int id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(getInstallmentURI(id), Installment.class));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
        return Optional.empty();
    }

    public List<Installment> fetchInstallments() {
        try {
            Optional<Installment[]> loanTypes =  Optional.ofNullable(restTemplate.getForObject(getInstallmentsURI(), Installment[].class));
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

    private URI getInstallmentURI(int id) {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/installments/"+id).build().encode().toUri();
    }

    private URI getInstallmentsURI() {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/installments").build().encode().toUri();
    }

}
