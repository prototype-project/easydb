package com.easydb.easydb.domain.locker.factories;

import com.easydb.easydb.domain.locker.ElementsLocker;

public interface ElementsLockerFactory {
    ElementsLocker build(String spaceName);
}
