package com.easydb.easydb.domain;

public class BucketDoesNotExistException extends RuntimeException {
	public BucketDoesNotExistException(String name) {
		super("Bucket " + name + "does not exist");
	}
}
