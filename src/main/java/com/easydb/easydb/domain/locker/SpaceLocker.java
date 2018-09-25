package com.easydb.easydb.domain.locker;

import java.time.Duration;

public interface SpaceLocker {
    void lockSpace(String spaceName);
    void lockSpace(String spaceName, Duration timeout);
    void unlockSpace(String spaceName);
}
