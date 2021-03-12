package com.cryptoloanfront.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApiConfig {

    @Value("${api.endpoint.prod}")
    private String apiEndpoint;

}
