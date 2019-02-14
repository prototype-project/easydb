package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.factories.ElementServiceFactory;

import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.DefaultTransactionManager;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.TransactionAbortedException;
import com.easydb.easydb.domain.transactions.TransactionConstraintsValidator;
import com.easydb.easydb.domain.transactions.TransactionCommitterFactory;
import com.easydb.easydb.domain.transactions.TransactionRepository;
import com.easydb.easydb.domain.transactions.Retryier;
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
    DefaultTransactionManager defaultTransactionManager(UUIDProvider uuidProvider,
                                                        TransactionRepository transactionRepository,
                                                        TransactionConstraintsValidator transactionConstraintsValidator,
                                                        TransactionCommitterFactory transactionCommitterFactory,
                                                        ElementServiceFactory elementServiceFactory,
                                                        ApplicationMetrics metrics) {
        return new DefaultTransactionManager(uuidProvider, transactionRepository, transactionConstraintsValidator,
                transactionCommitterFactory, elementServiceFactory, metrics);
    }

    @Bean
    TransactionCommitterFactory transactionCommitterFactory(ElementsLockerFactory elementsLockerFactory,
                                                         @Qualifier("lockerRetryier") Retryier lockerRetryier,
                                                         ElementServiceFactory elementServiceFactory) {
        return new TransactionCommitterFactory(elementsLockerFactory, lockerRetryier, elementServiceFactory);
    }

    @Bean
    OptimizedTransactionManager optimizedTransactionManager(UUIDProvider uuidProvider,
                                                            TransactionConstraintsValidator transactionConstraintsValidator,
                                                            TransactionCommitterFactory transactionCommitterFactory,
                                                            ApplicationMetrics metrics) {
        return new OptimizedTransactionManager(uuidProvider, transactionConstraintsValidator, transactionCommitterFactory, metrics);
    }


    @Bean
    Retryier transactionRetryier(TransactionsProperties properties) {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy boundedRetriesPolicy = new SimpleRetryPolicy(
                properties.getTransactionAttempts(), Collections.singletonMap(TransactionAbortedException.class, true));

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(properties.getBackoffMillis());

        retryTemplate.setRetryPolicy(boundedRetriesPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return new Retryier(retryTemplate);
    }

    @Bean
    TransactionConstraintsValidator transactionConstraintsValidator(SpaceRepository spaceRepository,
                                                                    BucketRepository bucketRepository,
                                                                    ElementServiceFactory elementServiceFactory) {
        return new TransactionConstraintsValidator(spaceRepository, bucketRepository, elementServiceFactory);
    }

}
