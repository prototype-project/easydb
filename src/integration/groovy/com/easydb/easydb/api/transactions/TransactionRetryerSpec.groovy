package com.easydb.easydb.api.transactions

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.ElementService
import com.easydb.easydb.domain.bucket.transactions.BucketRepository
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.transactions.TransactionalBucketService
import com.easydb.easydb.domain.locker.BucketLocker
import com.easydb.easydb.domain.locker.SpaceLocker
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.UUIDProvider
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager
import com.easydb.easydb.domain.transactions.Transaction
import com.easydb.easydb.domain.transactions.TransactionAbortedException
import com.easydb.easydb.domain.transactions.Retryer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class TransactionRetryerSpec extends IntegrationWithCleanedDatabaseSpec {

    @Autowired
    @Qualifier("transactionRetryer")
    Retryer transactionRetryer

    @Autowired
    @Qualifier("lockerRetryer")
    Retryer lockerRetryer

    @Autowired
    BucketService bucketService

    @Autowired
    UUIDProvider uuidProvider

    @Autowired
    SpaceLocker spaceLocker

    @Autowired
    BucketLocker bucketLocker

    BucketService mockedBucketService

    Element element = ElementTestBuilder.builder().build()

    def optimizedTransactionManagerMock = Mock(OptimizedTransactionManager)

    def setup() {
        mockedBucketService = new TransactionalBucketService(Mock(SpaceRepository),
                Mock(BucketRepository), Mock(ElementService), optimizedTransactionManagerMock,
                bucketLocker, spaceLocker, transactionRetryer, lockerRetryer)

    }

    def "should retry update operation in case of transaction errors"() {
        given:
        Transaction transaction = new Transaction(TEST_BUCKET_NAME.spaceName, uuidProvider.generateUUID())
        optimizedTransactionManagerMock.beginTransaction(TEST_BUCKET_NAME.spaceName) >> transaction

        when:
        mockedBucketService.updateElement(element)

        then:
        2 * optimizedTransactionManagerMock.commitTransaction(transaction) >>
                {t -> throw new TransactionAbortedException("msg", new RuntimeException())} >> null
    }
}
