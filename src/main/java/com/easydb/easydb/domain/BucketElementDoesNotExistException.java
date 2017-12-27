package com.easydb.easydb.domain;

public class BucketElementDoesNotExistException extends RuntimeException {

	public BucketElementDoesNotExistException(String bucketName, String id) {
		super("Bucket or element does not exist. Bucket name: " + bucketName + " Element id: " + id);
	}
}
