package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.BaseIntegrationSpec;
import com.easydb.easydb.domain.locker.BucketLocker
import com.easydb.easydb.domain.locker.LockNotHoldException
import com.easydb.easydb.domain.locker.LockTimeoutException;
import org.springframework.beans.factory.annotation.Autowired

import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future


class BucketLockSpec extends BaseIntegrationSpec {

    @Autowired
    BucketLocker locker

    def cleanup() {
        try {
            locker.unlockBucket("SpaceX", "testBucket")
        }
        catch (LockNotHoldException ignored) {}
    }

    def "should lock bucket"() {
        given:
        locker.lockBucket("SpaceX","testBucket")

        when:
        locker.lockBucket("SpaceX","testBucket")

        then:
        thrown(LockTimeoutException)
    }

    def "should unlock bucket"() {
        given:
        locker.lockBucket("SpaceX","testBucket")

        locker.unlockBucket("SpaceX","testBucket")

        when:
        locker.lockBucket("SpaceX","testBucket")

        then:
        noExceptionThrown()
    }

    def "should throw error when unlocking not held lock"() {
        when:
        locker.unlockBucket("SpaceX","testBucket")

        then:
        thrown(LockNotHoldException)
    }

    def "should wait no longer than timeout"() {
        given:
        locker.lockBucket("SpaceX","testBucket")

        when:
        long start = System.currentTimeMillis()
        long waitTime = 0
        try {
            Future<?> future = Executors.newSingleThreadExecutor().submit({
                locker.lockBucket("SpaceX","testBucket", Duration.ofMillis(400))
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

    def "should not block on two different buckets"() {
        given:
        locker.lockBucket("SpaceX","testBucket")

        when:
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockBucket("SpaceX","otherBucket", Duration.ofMillis(300))
            locker.unlockBucket("SpaceX","otherBucket")
        })
        future.get()

        then:
        noExceptionThrown()
    }

    def "should block another threads"() {
        given:
        locker.lockBucket("SpaceX","testBucket")

        when:
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockBucket("SpaceX","testBucket", Duration.ofMillis(300))
        })

        future.get()

        then:
        def e = thrown(ExecutionException)
        e.getCause() instanceof LockTimeoutException
    }
}
