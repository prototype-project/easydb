package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.ElementService;
import com.easydb.easydb.domain.bucket.factories.ElementServiceFactory;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;

public class TransactionEngineFactory {

    private final ElementsLockerFactory elementsLockerFactory;
    private final Retryier lockerRetryier;
    private final ElementServiceFactory elementServiceFactory;

    public TransactionEngineFactory(ElementsLockerFactory elementsLockerFactory, Retryier lockerRetryier,
                                    ElementServiceFactory elementServiceFactory) {
        this.elementsLockerFactory = elementsLockerFactory;
        this.lockerRetryier = lockerRetryier;
        this.elementServiceFactory = elementServiceFactory;
    }

    TransactionEngine build(String spaceName) {
        ElementService elementService =
                elementServiceFactory.buildElementService(spaceName);
        return new TransactionEngine(elementsLockerFactory, lockerRetryier, elementService);

    }
}
