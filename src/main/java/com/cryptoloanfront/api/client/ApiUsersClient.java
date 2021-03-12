package com.cryptoloanfront.api.client;

import com.cryptoloanfront.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ApiUsersClient extends ApiClient {

    public ApiUsersClient() {
        super(ApiUsersClient.class);
    }

    public void deleteUser(int id) {
        try {
            restTemplate.delete(getUserURI(id));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public void updateUser(User user) {
        try {
        restTemplate.put(getUsersURI(),createJsonRequest(user));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public void createUser(User user) {
        try {
            restTemplate.postForObject(getUsersURI(),createJsonRequest(user),String.class);
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    public Optional<User> fetchUser(int id) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(getUserURI(id), User.class));
        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
        }
        return Optional.empty();
    }

    public List<User> fetchUsers() {
        try {
            Optional<User[]> users =  Optional.ofNullable(restTemplate.getForObject(getUsersURI(),User[].class));
            return users
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(p -> Objects.nonNull(p.getId()) && Objects.nonNull(p.getFirstName()))
                    .collect(Collectors.toList());

        } catch (RestClientException e) {
            LOGGER.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    private URI getUserURI(int id) {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/users/"+id).build().encode().toUri();
    }

    private URI getUsersURI() {
        return UriComponentsBuilder.fromHttpUrl(apiConfig.getApiEndpoint()+"/users").build().encode().toUri();
    }

}
