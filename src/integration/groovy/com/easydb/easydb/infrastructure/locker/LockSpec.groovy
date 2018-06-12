package com.easydb.easydb.infrastructure.locker

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.domain.space.BaseSpecification
import com.easydb.easydb.domain.locker.ElementsLocker
import com.easydb.easydb.domain.locker.ElementsLockerFactory
import com.easydb.easydb.domain.locker.LockNotHoldException
import com.easydb.easydb.domain.locker.LockTimeoutException
import org.springframework.beans.factory.annotation.Autowired

import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class LockSpec extends BaseIntegrationSpec {

    @Autowired
    ElementsLockerFactory lockerFactory

    ElementsLocker locker

    def setupSpec() {
        startZookeeperServer()
    }

    def cleanupSpec() {
        stopZookeeperServer()
    }

    def setup() {
        locker = lockerFactory.build("SpaceX")
    }

    def cleanup() {
        try {
            locker.unlockElement("testBucket", "123")
        }
        catch (LockNotHoldException ignored) {}
    }

	def "should lock on element"() {
        given:
        locker.lockElement("testBucket", "123")

        when:
        locker.lockElement("testBucket", "123", Duration.ofMillis(300))

        then:
        thrown(LockTimeoutException)
    }

    def "should unlock element"() {
        given:
        locker.lockElement("testBucket", "123")

        when:
        locker.unlockElement("testBucket", "123")
        locker.lockElement("testBucket", "123")

        then:
        noExceptionThrown()
    }

    def "should throw error when unlocking not held lock"() {
        when:
        locker.unlockElement("testBucket", "123")

        then:
        thrown(LockNotHoldException)
    }

    def "should wait no longer than timeout"() {
        given:
        locker.lockElement("testBucket", "123")

        when:
        long start = System.currentTimeMillis()
        long waitTime = 0
        try {
            locker.lockElement("testBucket", "123", Duration.ofMillis(400))
        }
        catch (LockTimeoutException e) {
            waitTime = System.currentTimeMillis() - start
        }

        then:
        waitTime > 300 && waitTime < 500
    }

    def "should not block on two different elements"() {
        given:
        locker.lockElement("testBucket", "123")

        when:
        locker.lockElement("testBucket", "456", Duration.ofMillis(300))
        locker.unlockElement("testBucket", "456")

        then:
        noExceptionThrown()
    }

    def "should block another threads when lock is acquired with new locker object"() {
        given:
        locker.lockElement("testBucket", "123")

        when:
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            lockerFactory.build("SpaceX")
                    .lockElement("testBucket", "123", Duration.ofMillis(300))
            null
        })

        future.get()

        then:
        def e = thrown(ExecutionException)
        e.getCause() instanceof LockTimeoutException
    }

    def "should block another threads when lock is acquired with the same locker object"() {
        given:
        locker.lockElement("testBucket", "123")

        when:
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockElement("testBucket", "123", Duration.ofMillis(300))
            null
        })

        future.get()

        then:
        def e = thrown(ExecutionException)
        e.getCause() instanceof LockTimeoutException
    }
}
