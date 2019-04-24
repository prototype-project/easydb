package com.easydb.easydb.infrastructure.locker;

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.domain.bucket.BucketName;
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

    BucketName bucketName = new BucketName("SpaceX", "testBucket")

    def cleanup() {
        try {
            locker.unlockBucket(bucketName)
        }
        catch (LockNotHoldException ignored) {}
    }

    def "should lock bucket"() {
        given:
        locker.lockBucket(bucketName)

        when:
        locker.lockBucket(bucketName)

        then:
        thrown(LockTimeoutException)
    }

    def "should unlock bucket"() {
        given:
        locker.lockBucket(bucketName)

        locker.unlockBucket(bucketName)

        when:
        locker.lockBucket(bucketName)

        then:
        noExceptionThrown()
    }

    def "should throw error when unlocking not held lock"() {
        when:
        locker.unlockBucket(bucketName)

        then:
        thrown(LockNotHoldException)
    }

    def "should wait no longer than timeout"() {
        given:
        locker.lockBucket(bucketName)

        when:
        long start = System.currentTimeMillis()
        long waitTime = 0
        try {
            Future<?> future = Executors.newSingleThreadExecutor().submit({
                locker.lockBucket(bucketName, Duration.ofMillis(400))
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
        locker.lockBucket(bucketName)

        when:
        BucketName otherBucket = new BucketName("SpaceX", "otherBucket")
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockBucket(otherBucket, Duration.ofMillis(300))
            locker.unlockBucket(otherBucket)
        })
        future.get()

        then:
        noExceptionThrown()
    }

    def "should block another threads"() {
        given:
        locker.lockBucket(bucketName)

        when:
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockBucket(bucketName, Duration.ofMillis(300))
        })

        future.get()

        then:
        def e = thrown(ExecutionException)
        e.getCause() instanceof LockTimeoutException
    }
}
