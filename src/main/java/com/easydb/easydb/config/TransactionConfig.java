package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.DefaultTransactionManager;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.TransactionAbortedException;
import com.easydb.easydb.domain.transactions.TransactionRepository;
import com.easydb.easydb.domain.transactions.TransactionRetryier;
import com.easydb.easydb.infrastructure.transactions.MongoTransactionRepository;
import java.util.Collections;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableConfigurationProperties(TransactionsProperties.class)
public class TransactionConfig {

    @Bean
    public TransactionRepository transactionRepository(MongoTemplate template) {
        return new MongoTransactionRepository(template);
    }

    @Bean
    DefaultTransactionManager defaultTransactionManager(UUIDProvider uuidProvider,
                                                        TransactionRepository transactionRepository,
                                                        SpaceRepository spaceRepository,
                                                        ElementsLockerFactory lockerFactory,
                                                        SimpleElementOperationsFactory simpleElementOperationsFactory,
                                                        ApplicationMetrics metrics) {
        return new DefaultTransactionManager(uuidProvider, transactionRepository, spaceRepository,
                lockerFactory, simpleElementOperationsFactory, metrics);
    }

    @Bean
    OptimizedTransactionManager optimizedTransactionManager(UUIDProvider uuidProvider,
                                                            SpaceRepository spaceRepository,
                                                            ElementsLockerFactory lockerFactory,
                                                            SimpleElementOperationsFactory simpleElementOperationsFactory,
                                                            ApplicationMetrics metrics) {
        return new OptimizedTransactionManager(uuidProvider, spaceRepository, lockerFactory,
                simpleElementOperationsFactory, metrics);
    }

    @Bean
    TransactionRetryier transactionRetryier(TransactionsProperties properties) {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy boundedRetriesPolicy = new SimpleRetryPolicy(
                properties.getTransactionAttempts(), Collections.singletonMap(TransactionAbortedException.class, true));

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(properties.getBackoffMillis());

        retryTemplate.setRetryPolicy(boundedRetriesPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return new TransactionRetryier(retryTemplate);
    }
}
