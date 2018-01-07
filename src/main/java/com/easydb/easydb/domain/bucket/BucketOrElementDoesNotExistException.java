package com.easydb.easydb.domain.bucket;

public class BucketOrElementDoesNotExistException extends RuntimeException {

	public BucketOrElementDoesNotExistException(String bucketName, String id) {
		super("Bucket or element does not exist. Bucket spaceName: " + bucketName + " Element id: " + id);
	}
}
