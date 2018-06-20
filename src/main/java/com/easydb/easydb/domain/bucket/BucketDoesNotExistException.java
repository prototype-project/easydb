package com.easydb.easydb.domain.bucket;

public class BucketDoesNotExistException extends RuntimeException {

	public BucketDoesNotExistException(String bucketName) {
		super("Bucket " + bucketName + " does not exist.");
	}
}
