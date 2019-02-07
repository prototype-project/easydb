package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.SimpleElementOperations;
import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;

public class TransactionEngineFactory {

    private final ElementsLockerFactory elementsLockerFactory;
    private final Retryier lockerRetryier;
    private final SimpleElementOperationsFactory simpleElementOperationsFactory;

    public TransactionEngineFactory(ElementsLockerFactory elementsLockerFactory, Retryier lockerRetryier,
                                    SimpleElementOperationsFactory simpleElementOperationsFactory) {
        this.elementsLockerFactory = elementsLockerFactory;
        this.lockerRetryier = lockerRetryier;
        this.simpleElementOperationsFactory = simpleElementOperationsFactory;
    }

    TransactionEngine build(String spaceName) {
        SimpleElementOperations simpleElementOperations =
                simpleElementOperationsFactory.buildSimpleElementOperations(spaceName);
        return new TransactionEngine(elementsLockerFactory, lockerRetryier, simpleElementOperations);

    }
}
