package com.cryptoloanfront.api.client;

import com.cryptoloanfront.api.client.adapter.LocalDateAdapter;
import com.cryptoloanfront.api.config.ApiConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

abstract class ApiClient {

    protected static Logger LOGGER;

    @Autowired
    protected ApiConfig apiConfig;

    @Autowired
    protected RestTemplate restTemplate;

    public ApiClient(Class<?> clazz) {
        LOGGER = LoggerFactory.getLogger(clazz);
    }

    protected HttpEntity<String> createJsonRequest(Object o) {
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        String content = gson.toJson(o);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(content,headers);
    }

}
