package com.easydb.easydb.config;

import com.easydb.easydb.domain.space.SpaceService;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.TransactionManager;
import com.easydb.easydb.domain.transactions.TransactionRepository;
import com.easydb.easydb.infrastructure.transactions.DefaultTransactionManager;
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
	public TransactionManager transactionManager(TransactionRepository repository, UUIDProvider uuidProvider,
	                                             SpaceService spaceService) {
		return new DefaultTransactionManager(repository, uuidProvider, spaceService);
	}
}
