package com.easydb.easydb.config;

import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.TransactionManager;
import com.easydb.easydb.domain.transactions.TransactionRepository;
import com.easydb.easydb.infrastructure.transactions.MongoTransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class TransactionConfig {

    @Bean
    public TransactionRepository transactionRepository(MongoTemplate template) {
        return new MongoTransactionRepository(template);
    }

    @Bean
    TransactionManager transactionManager(UUIDProvider uuidProvider,
                                          TransactionRepository transactionRepository,
                                          SpaceRepository spaceRepository,
                                          ElementsLockerFactory lockerFactory,
                                          SimpleElementOperationsFactory simpleElementOperationsFactory) {
        return new TransactionManager(uuidProvider, transactionRepository, spaceRepository,
                lockerFactory, simpleElementOperationsFactory);
    }
}
