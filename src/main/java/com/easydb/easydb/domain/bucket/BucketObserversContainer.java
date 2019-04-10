package com.easydb.easydb.domain.bucket;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class BucketObserversContainer {
    private final int capacity;
    private final Map<BucketKey, BucketEventsObserver> observers = new ConcurrentHashMap<>();

    public BucketObserversContainer(int capacity, int threadPoolSize) {
        this.capacity = capacity;
    }

    public BucketEventsObserver provide(String spaceName, String bucketName) {
        return observers.computeIfAbsent(new BucketKey(spaceName, bucketName),
                bucketKey -> new BucketEventsObserver(capacity));
    }

    private static class BucketKey {
        private final String spaceName;
        private final String bucketName;

        private BucketKey(String spaceName, String bucketName) {
            this.spaceName = spaceName;
            this.bucketName = bucketName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BucketKey bucketKey = (BucketKey) o;
            return spaceName.equals(bucketKey.spaceName) &&
                    bucketName.equals(bucketKey.bucketName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(spaceName, bucketName);
        }
    }
}
