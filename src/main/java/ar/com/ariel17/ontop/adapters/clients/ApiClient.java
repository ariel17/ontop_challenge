package ar.com.ariel17.ontop.adapters.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public abstract class ApiClient {

    protected RestTemplate restTemplate;

    protected ObjectMapper mapper;

    public ApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
    }

    protected String get(String uri) throws IllegalStateException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);
        String body = response.getBody();
        HttpStatusCode statusCode = response.getStatusCode();
        handleErrors(uri, statusCode, body, true);
        return body;
    }

    protected String post(String uri, String requestBody, boolean raiseException) throws IllegalStateException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        String responseBody = response.getBody();
        HttpStatusCode statusCode = response.getStatusCode();

        handleErrors(uri, statusCode, responseBody, raiseException);
        return responseBody;
    }

    private void handleErrors(String uri, HttpStatusCode statusCode, String responseBody, boolean raiseException) {
        if (statusCode.is5xxServerError() || statusCode.is4xxClientError()) {
            // TODO log error response
            if (raiseException) {
                throw new IllegalStateException(String.format("Error requesting resource: uri=%s, status_code=%d, body=%s", uri, statusCode.value(), responseBody));
            }
        }
    }
}
