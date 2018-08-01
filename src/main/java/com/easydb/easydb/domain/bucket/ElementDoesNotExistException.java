package com.easydb.easydb.domain.bucket;

public class ElementDoesNotExistException extends RuntimeException {

    public ElementDoesNotExistException(String bucketName, String id) {
        super("Element with id " + id + " does not exist in bucket " + bucketName);
    }
}
