package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.OperationResult;
import com.easydb.easydb.domain.transactions.TransactionManager;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/transactions")
class TransactionController {

    private final TransactionManager transactionManager;
    private final UUIDProvider uuidProvider;

    TransactionController(TransactionManager transactionManager, UUIDProvider uuidProvider) {
        this.transactionManager = transactionManager;
        this.uuidProvider = uuidProvider;
    }

    @PostMapping("/{spaceName}")
    @ResponseStatus(value = HttpStatus.CREATED)
    String beginTransaction(@PathVariable("spaceName") String spaceName) {
        return transactionManager.beginTransaction(spaceName);
    }

    @PostMapping("/{transactionId}/add-operation")
    @ResponseStatus(value = HttpStatus.CREATED)
    OperationResultDto addOperation(@PathVariable String transactionId,
                                 @RequestBody @Valid OperationDto dto) {
        OperationResult operationResult = transactionManager.addOperation(transactionId, dto.toDomain(uuidProvider));
        return OperationResultDto.of(operationResult);
    }

    @PostMapping("/{transactionId}/commit")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    void commitTransaction(@PathVariable String transactionId) {
        transactionManager.commitTransaction(transactionId);
    }
}
