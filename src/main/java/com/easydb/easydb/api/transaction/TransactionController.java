package com.easydb.easydb.api.transaction;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.OperationResult;
import com.easydb.easydb.domain.transactions.PersistentTransactionManager;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/spaces/{spaceName}/transactions")
class TransactionController {

    private final PersistentTransactionManager persistentTransactionManager;
    private final UUIDProvider uuidProvider;
    private final ApplicationMetrics metrics;

    TransactionController(PersistentTransactionManager persistentTransactionManager, UUIDProvider uuidProvider,
                          ApplicationMetrics metrics) {
        this.persistentTransactionManager = persistentTransactionManager;
        this.uuidProvider = uuidProvider;
        this.metrics = metrics;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    TransactionDto beginTransaction(@PathVariable("spaceName") String spaceName) {
        String transactionId = persistentTransactionManager.beginTransaction(spaceName);
        metrics.beginTransactionRequestsCounter(spaceName).increment();
        return new TransactionDto(transactionId);
    }

    @PostMapping("/{transactionId}/add-operation")
    @ResponseStatus(value = HttpStatus.CREATED)
    OperationResultDto addOperation(@PathVariable("spaceName") String spaceName, // TODO
                                    @PathVariable("transactionId") String transactionId,
                                    @RequestBody @Valid OperationDto dto) {
        dto.validate();
        OperationResult operationResult = persistentTransactionManager.addOperation(transactionId, dto.toDomain(uuidProvider));

        BucketName bucketName = new BucketName(operationResult.getSpaceName(), dto.getBucketName());
        metrics.addOperationToTransactionRequestCounter(bucketName, dto.getType().toString()).increment();
        return OperationResultDto.of(operationResult);
    }

    @PostMapping("/{transactionId}/commit")
    @ResponseStatus(value = HttpStatus.OK)
    void commitTransaction(@PathVariable String transactionId) {
        String spaceName = persistentTransactionManager.commitTransaction(transactionId).getSpaceName();
        metrics.commitTransactionRequestCounter(spaceName).increment();
    }
}