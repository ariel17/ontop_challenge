package ar.com.ariel17.ontop.adapters.http;

import ar.com.ariel17.ontop.adapters.http.entities.requests.TransferRequest;
import ar.com.ariel17.ontop.adapters.http.entities.responses.TransferResponse;
import ar.com.ariel17.ontop.core.clients.UserNotFoundException;
import ar.com.ariel17.ontop.core.domain.BankAccountOwner;
import ar.com.ariel17.ontop.core.domain.Transaction;
import ar.com.ariel17.ontop.core.repositories.BankAccountOwnerNotFoundException;
import ar.com.ariel17.ontop.core.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Currency;

@RestController
public class TransferController {

    private TransactionService service;

    private TransferMapper mapper;

    @Autowired
    public TransferController(TransactionService service, Currency defaultCurrency) {
        this.service = service;
        mapper = new TransferMapper(defaultCurrency);
    }

    @PostMapping("/transfers")
    public TransferResponse createTransfer(@Valid @RequestBody TransferRequest request) {
        if (request.getRecipientId() != null && request.getRecipient() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use recipient_id or recipient, not both");
        }

        BankAccountOwner owner = mapper.bankAccountOwnerFromTransferRequest(request);
        Transaction transaction = null;
        try {
            transaction = service.transfer(request.getUserId(), owner, request.getAmount());

        } catch (UserNotFoundException | BankAccountOwnerNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.toString(), e);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString(), e);
        }
        return mapper.transactionToTransferResponse(request.getUserId(), transaction);
    }
}
