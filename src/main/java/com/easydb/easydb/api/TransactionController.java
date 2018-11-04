package com.easydb.easydb.api;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.OperationResult;
import com.easydb.easydb.domain.transactions.DefaultTransactionManager;
import com.easydb.easydb.domain.transactions.Transaction;
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

    private final DefaultTransactionManager defaultTransactionManager;
    private final UUIDProvider uuidProvider;
    private final ApplicationMetrics metrics;

    TransactionController(DefaultTransactionManager defaultTransactionManager, UUIDProvider uuidProvider,
                          ApplicationMetrics metrics) {
        this.defaultTransactionManager = defaultTransactionManager;
        this.uuidProvider = uuidProvider;
        this.metrics = metrics;
    }

    @PostMapping("/{spaceName}")
    @ResponseStatus(value = HttpStatus.CREATED)
    String beginTransaction(@PathVariable("spaceName") String spaceName) {
        String transactionId = defaultTransactionManager.beginTransaction(spaceName);
        metrics.getBeginTransactionRequestsCounter(spaceName).increment();
        return transactionId;
    }

    @PostMapping("/{transactionId}/add-operation")
    @ResponseStatus(value = HttpStatus.CREATED)
    OperationResultDto addOperation(@PathVariable String transactionId,
                                    @RequestBody @Valid OperationDto dto) {
        OperationResult operationResult = defaultTransactionManager.addOperation(transactionId, dto.toDomain(uuidProvider));
        metrics.getAddOperationToTransactionRequestCounter(operationResult.getSpaceName(), dto.getBucketName(),
                dto.getType().toString()).increment();
        return OperationResultDto.of(operationResult);
    }

    @PostMapping("/{transactionId}/commit")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    void commitTransaction(@PathVariable String transactionId) {
        String spaceName = defaultTransactionManager.commitTransaction(transactionId).getSpaceName();
        metrics.getCommitTransactionRequestCounter(spaceName).increment();
    }
}