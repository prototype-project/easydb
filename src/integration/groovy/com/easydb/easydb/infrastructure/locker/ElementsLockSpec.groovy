package com.easydb.easydb.infrastructure.locker

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.domain.BucketName
import com.easydb.easydb.domain.locker.ElementsLocker
import com.easydb.easydb.domain.locker.LockNotHoldException
import com.easydb.easydb.domain.locker.LockTimeoutException
import org.springframework.beans.factory.annotation.Autowired

import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ElementsLockSpec extends BaseIntegrationSpec {

    @Autowired
    ElementsLocker locker

    BucketName testBucketName = new BucketName("SpaceX", "testBucket")

    def cleanup() {
        try {
            locker.unlockElement(testBucketName, "123")
        }
        catch (LockNotHoldException ignored) {
        }
    }

    def "should unlock element"() {
        given:
        locker.lockElement(testBucketName, "123")

        when:
        locker.unlockElement(testBucketName, "123")

        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockElement(testBucketName, "123", Duration.ofMillis(300))
            locker.unlockElement(testBucketName, "123")
        })
        future.get()

        then:
        noExceptionThrown()
    }

    def "should throw error when unlocking not held lock"() {
        when:
        locker.unlockElement(testBucketName, "123")

        then:
        thrown(LockNotHoldException)
    }

    def "should wait no longer than timeout"() {
        given:
        locker.lockElement(testBucketName, "123")

        when:
        long start = System.currentTimeMillis()
        long waitTime = 0
        try {
            Future<?> future = Executors.newSingleThreadExecutor().submit({
                locker.lockElement(testBucketName, "123", Duration.ofMillis(400))
            })
            future.get()
        }
        catch (ExecutionException e) {
            assert e.getCause() instanceof LockTimeoutException
            waitTime = System.currentTimeMillis() - start
        }

        then:
        waitTime > 300 && waitTime < 500
    }

    def "should not block on two different elements"() {
        given:
        locker.lockElement(testBucketName, "123")

        when:
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockElement(testBucketName, "456", Duration.ofMillis(300))
            locker.unlockElement(testBucketName, "456")
        })
        future.get()

        then:
        noExceptionThrown()
    }

    def "should block another threads"() {
        given:
        locker.lockElement(testBucketName, "123")

        when:
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockElement(testBucketName, "123", Duration.ofMillis(300))
        })

        future.get()

        then:
        def e = thrown(ExecutionException)
        e.getCause() instanceof LockTimeoutException
    }

    def "lock should be re-entrant for the same thread"() {
        given:
        locker.lockElement(testBucketName, "123")

        when:
        locker.lockElement(testBucketName, "123")

        then:
        noExceptionThrown()

        when:
        locker.unlockElement(testBucketName, "123")
        locker.unlockElement(testBucketName, "123")

        then:
        noExceptionThrown()
    }
}
