package com.easydb.easydb.domain;

public class BucketElementDoesNotExistException extends RuntimeException {

	public BucketElementDoesNotExistException(String bucketName, String id) {
		super("Bucket element with id: " + id + " does not exist in bucket with name: " + bucketName);
	}
}
