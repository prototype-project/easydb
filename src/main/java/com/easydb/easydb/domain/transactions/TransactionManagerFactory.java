package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.BucketService;

public interface TransactionManagerFactory {
    TransactionManager buildTransactionManager(BucketService simpleBucketService);
}
