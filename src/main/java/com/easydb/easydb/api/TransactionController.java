package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.bucket.SimpleBucketService;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.SpaceService;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.TransactionManagerFactory;
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

    private final TransactionManagerFactory transactionManagerFactory;
    private final UUIDProvider uuidProvider;
    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;

    TransactionController(TransactionManagerFactory transactionManagerFactory, UUIDProvider uuidProvider,
                          SpaceRepository spaceRepository, BucketRepository bucketRepository) {
        this.transactionManagerFactory = transactionManagerFactory;
        this.uuidProvider = uuidProvider;
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
    }

    @PostMapping("/{spaceName}")
    @ResponseStatus(value = HttpStatus.CREATED)
    String beginTransaction(@PathVariable("spaceName") String spaceName) {
        BucketService simpleBucketService = new SimpleBucketService(spaceName, spaceRepository, bucketRepository);
        return transactionManagerFactory.buildTransactionManager(simpleBucketService)
                .beginTransaction(spaceName);
    }

    @PostMapping("{spaceName}/add-operation/{transactionId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    void addOperation(@PathVariable String transactionId,
                      @PathVariable String spaceName,
                      @RequestBody @Valid OperationDto dto) {
        BucketService simpleBucketService = new SimpleBucketService(spaceName, spaceRepository, bucketRepository);
        transactionManagerFactory.buildTransactionManager(simpleBucketService)
                .addOperation(transactionId, dto.toDomain(uuidProvider));
    }
}
