package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.transactions.TransactionalElementService;
import com.easydb.easydb.domain.bucket.factories.ElementServiceFactory;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;

public class TransactionCommitterFactory {

    private final ElementsLockerFactory elementsLockerFactory;
    private final Retryier lockerRetryier;
    private final ElementServiceFactory elementServiceFactory;

    public TransactionCommitterFactory(ElementsLockerFactory elementsLockerFactory, Retryier lockerRetryier,
                                       ElementServiceFactory elementServiceFactory) {
        this.elementsLockerFactory = elementsLockerFactory;
        this.lockerRetryier = lockerRetryier;
        this.elementServiceFactory = elementServiceFactory;
    }

    TransactionCommitter build(String spaceName) {
        TransactionalElementService elementService =
                elementServiceFactory.buildElementService(spaceName);
        return new TransactionCommitter(elementsLockerFactory, lockerRetryier, elementService);

    }
}
