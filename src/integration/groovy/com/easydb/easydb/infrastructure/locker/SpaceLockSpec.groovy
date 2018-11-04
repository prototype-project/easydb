package com.easydb.easydb.infrastructure.locker

import com.easydb.easydb.BaseIntegrationSpec
import com.easydb.easydb.domain.locker.LockNotHoldException
import com.easydb.easydb.domain.locker.LockTimeoutException
import com.easydb.easydb.domain.locker.SpaceLocker
import org.springframework.beans.factory.annotation.Autowired

import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SpaceLockSpec extends BaseIntegrationSpec {

    @Autowired
    SpaceLocker locker

    def cleanup() {
        try {
            locker.unlockSpace("SpaceX")
        }
        catch (LockNotHoldException ignored) {}
    }

    def "should lock space"() {
        given:
        locker.lockSpace("SpaceX")

        when:
        locker.lockSpace("SpaceX")

        then:
        thrown(LockTimeoutException)
    }

    def "should unlock space"() {
        given:
        locker.lockSpace("SpaceX")

        locker.unlockSpace("SpaceX")

        when:
        locker.lockSpace("SpaceX")

        then:
        noExceptionThrown()
    }

    def "should throw error when unlocking not held lock"() {
        when:
        locker.unlockSpace("SpaceX")

        then:
        thrown(LockNotHoldException)
    }

    def "should wait no longer than timeout"() {
        given:
        locker.lockSpace("SpaceX")

        when:
        long start = System.currentTimeMillis()
        long waitTime = 0
        try {
            Future<?> future = Executors.newSingleThreadExecutor().submit({
                locker.lockSpace("SpaceX", Duration.ofMillis(400))
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

    def "should not block on two different spaces"() {
        given:
        locker.lockSpace("SpaceX")

        when:
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockSpace("otherSpace", Duration.ofMillis(300))
            locker.unlockSpace("otherSpace")
        })
        future.get()

        then:
        noExceptionThrown()
    }

    def "should block another threads"() {
        given:
        locker.lockSpace("SpaceX")

        when:
        Future<?> future = Executors.newSingleThreadExecutor().submit({
            locker.lockSpace("SpaceX", Duration.ofMillis(300))
        })

        future.get()

        then:
        def e = thrown(ExecutionException)
        e.getCause() instanceof LockTimeoutException
    }
}
