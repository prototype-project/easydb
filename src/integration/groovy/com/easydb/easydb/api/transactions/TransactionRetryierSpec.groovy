package com.easydb.easydb.api.transactions

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.BucketRepository
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.TransactionalBucketService
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.bucket.factories.ElementServiceFactory
import com.easydb.easydb.domain.locker.BucketLocker
import com.easydb.easydb.domain.locker.SpaceLocker
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.UUIDProvider
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager
import com.easydb.easydb.domain.transactions.Transaction
import com.easydb.easydb.domain.transactions.TransactionAbortedException
import com.easydb.easydb.domain.transactions.Retryier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class TransactionRetryierSpec extends IntegrationWithCleanedDatabaseSpec {

    @Autowired
    @Qualifier("transactionRetryier")
    Retryier transactionRetryier

    @Autowired
    @Qualifier("lockerRetryier")
    Retryier lockerRetryier

    @Autowired
    BucketServiceFactory bucketServiceFactory

    @Autowired
    UUIDProvider uuidProvider

    @Autowired
    SpaceLocker spaceLocker

    @Autowired
    BucketLocker bucketLocker

    BucketService mockedBucketService

    Element element = ElementTestBuilder.builder().bucketName(TEST_BUCKET_NAME).build()

    def optimizedTransactionManagerMock = Mock(OptimizedTransactionManager)

    def setup() {
        mockedBucketService = new TransactionalBucketService("testSpace", Mock(SpaceRepository),
                Mock(BucketRepository), Mock(ElementServiceFactory), optimizedTransactionManagerMock,
                bucketLocker, spaceLocker, transactionRetryier, lockerRetryier)

    }

    def "should retry update operation in case of transaction errors"() {
        given:
        Transaction transaction = new Transaction(TEST_SPACE, uuidProvider.generateUUID())
        optimizedTransactionManagerMock.beginTransaction(TEST_SPACE) >> transaction

        when:
        mockedBucketService.updateElement(element)

        then:
        2 * optimizedTransactionManagerMock.commitTransaction(transaction) >>
                {t -> throw new TransactionAbortedException("msg", new RuntimeException())} >> null
    }
}
