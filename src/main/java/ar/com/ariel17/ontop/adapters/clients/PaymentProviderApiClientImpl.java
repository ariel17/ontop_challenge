package ar.com.ariel17.ontop.adapters.clients;

import ar.com.ariel17.ontop.adapters.clients.entities.PaymentProviderMapper;
import ar.com.ariel17.ontop.adapters.clients.entities.requests.PaymentProviderRequest;
import ar.com.ariel17.ontop.adapters.clients.entities.responses.PaymentProviderResponse;
import ar.com.ariel17.ontop.core.clients.PaymentProviderApiClient;
import ar.com.ariel17.ontop.core.clients.PaymentProviderApiException;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class PaymentProviderApiClientImpl extends ApiClient implements PaymentProviderApiClient {

    protected static final String PAYMENT_URI = "/api/v1/payments";

    private PaymentProviderMapper providerMapper;

    @Autowired
    public PaymentProviderApiClientImpl(RestTemplate paymentProviderRestTemplate) {
        super(paymentProviderRestTemplate);
        providerMapper = new PaymentProviderMapper();
    }

    @Override
    public Payment createPayment(BankAccountOwner from, BankAccountOwner to, BigDecimal amount) throws PaymentProviderApiException {
        PaymentProviderRequest request = providerMapper.toPaymentProviderRequest(from, to, amount);

        String requestBody;
        try {
            requestBody = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new PaymentProviderApiException(e);
        }

        ResponseEntity<String> response;
        try {
            response = post(PAYMENT_URI, requestBody);
        } catch (Exception e) {
            throw new PaymentProviderApiException(e);
        }

        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode.is4xxClientError()) {
            throw new PaymentProviderApiException("Payment provider API error");
        }

        String responseBody = response.getBody();
        PaymentProviderResponse responseEntity;
        try {
            responseEntity = mapper.readValue(responseBody, PaymentProviderResponse.class);
        } catch (JsonProcessingException e) {
            throw new PaymentProviderApiException(e);
        }

        return providerMapper.paymentProviderResponseToPayment(responseEntity);
    }
}
