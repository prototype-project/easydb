package com.easydb.easydb.api.transactions

import com.easydb.easydb.ElementTestBuilder
import com.easydb.easydb.IntegrationWithCleanedDatabaseSpec
import com.easydb.easydb.domain.bucket.BucketRepository
import com.easydb.easydb.domain.bucket.BucketService
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.TransactionalBucketService
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory
import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory
import com.easydb.easydb.domain.space.SpaceRepository
import com.easydb.easydb.domain.space.UUIDProvider
import com.easydb.easydb.domain.transactions.Operation
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager
import com.easydb.easydb.domain.transactions.Transaction
import com.easydb.easydb.domain.transactions.TransactionAbortedException
import com.easydb.easydb.domain.transactions.TransactionRetryier
import org.springframework.beans.factory.annotation.Autowired

class TransactionRetryierSpec extends IntegrationWithCleanedDatabaseSpec {

    @Autowired
    TransactionRetryier transactionRetryier

    @Autowired
    BucketServiceFactory bucketServiceFactory

    @Autowired
    UUIDProvider uuidProvider

    BucketService mockedBucketService

    Element element = ElementTestBuilder.builder().bucketName(TEST_BUCKET_NAME).build()

    def optimizedTransactionManagerMock = Mock(OptimizedTransactionManager)

    def setup() {
        mockedBucketService = new TransactionalBucketService("testSpace", Mock(SpaceRepository),
                Mock(BucketRepository), Mock(SimpleElementOperationsFactory), optimizedTransactionManagerMock, transactionRetryier)

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
