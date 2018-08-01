package com.easydb.easydb.config;

import com.easydb.easydb.domain.locker.ElementsLockerFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.SpaceService;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.TransactionManagerFactory;
import com.easydb.easydb.domain.transactions.TransactionRepository;
import com.easydb.easydb.infrastructure.transactions.DefaultTransactionManagerFactory;
import com.easydb.easydb.infrastructure.transactions.MongoTransactionRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(TransactionProperties.class)
public class TransactionConfig {

    @Bean
    public TransactionRepository transactionRepository(MongoTemplate template) {
        return new MongoTransactionRepository(template);
    }

    @Bean
    TransactionManagerFactory transactionManagerFactory(UUIDProvider uuidProvider,
                                                        TransactionRepository transactionRepository,
                                                        SpaceRepository spaceRepository,
                                                        ElementsLockerFactory lockerFactory,
                                                        TransactionProperties transactionProperties) {
        return new DefaultTransactionManagerFactory(uuidProvider, transactionRepository, spaceRepository,
                lockerFactory, transactionProperties.getNumberOfRetries());
    }
}
