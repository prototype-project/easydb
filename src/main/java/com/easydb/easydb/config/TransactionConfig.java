package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.BucketObserversContainer;
import com.easydb.easydb.domain.bucket.ElementService;
import com.easydb.easydb.domain.bucket.transactions.BucketRepository;

import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.PersistentTransactionManager;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.Retryer;
import com.easydb.easydb.domain.transactions.TransactionAbortedException;
import com.easydb.easydb.domain.transactions.TransactionCommitter;
import com.easydb.easydb.domain.transactions.TransactionConstraintsValidator;
import com.easydb.easydb.domain.transactions.TransactionRepository;
import com.easydb.easydb.infrastructure.transactions.MongoTransactionRepository;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableConfigurationProperties({TransactionsProperties.class, LockingProperties.class})
public class TransactionConfig {

    @Bean
    public TransactionRepository transactionRepository(MongoTemplate template) {
        return new MongoTransactionRepository(template);
    }


    @Bean
    PersistentTransactionManager defaultTransactionManager(UUIDProvider uuidProvider,
                                                           TransactionRepository transactionRepository,
                                                           TransactionConstraintsValidator transactionConstraintsValidator,
                                                           TransactionCommitter transactionCommitter,
                                                           ElementService elementService,
                                                           ApplicationMetrics metrics) {
        return new PersistentTransactionManager(uuidProvider, transactionRepository, transactionConstraintsValidator,
                transactionCommitter, elementService, metrics);
    }

    @Bean
    TransactionCommitter transactionCommitter(ElementsLocker elementsLocker,
                                              @Qualifier("lockerRetryer") Retryer lockerRetryer,
                                              ElementService elementService,
                                              BucketObserversContainer observersContainer) {
        return new TransactionCommitter(elementsLocker, lockerRetryer, elementService, observersContainer);
    }

    @Bean
    OptimizedTransactionManager optimizedTransactionManager(UUIDProvider uuidProvider,
                                                            TransactionConstraintsValidator transactionConstraintsValidator,
                                                            TransactionCommitter transactionCommitter,
                                                            ApplicationMetrics metrics) {
        return new OptimizedTransactionManager(uuidProvider, transactionConstraintsValidator, transactionCommitter, metrics);
    }


    @Bean
    Retryer transactionRetryer(TransactionsProperties properties) {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy boundedRetriesPolicy = new SimpleRetryPolicy(
                properties.getTransactionAttempts(), Collections.singletonMap(TransactionAbortedException.class, true));

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(properties.getBackoffMillis());

        retryTemplate.setRetryPolicy(boundedRetriesPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return new Retryer(retryTemplate);
    }

    @Bean
    TransactionConstraintsValidator transactionConstraintsValidator(SpaceRepository spaceRepository,
                                                                    BucketRepository bucketRepository,
                                                                    ElementService elementService) {
        return new TransactionConstraintsValidator(spaceRepository, bucketRepository, elementService);
    }

}
