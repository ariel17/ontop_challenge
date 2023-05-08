package ar.com.ariel17.ontop.adapters.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * Abstract implementation to be reused by API client callers.
 */
public abstract class ApiClient {

    protected RestTemplate restTemplate;

    protected ObjectMapper mapper;

    public ApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
    }

    protected ResponseEntity<String> get(String uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        return restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
    }

    protected ResponseEntity<String> post(String uri, String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
    }
}
