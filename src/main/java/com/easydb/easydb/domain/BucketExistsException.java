package com.easydb.easydb.domain;

public class BucketExistsException extends RuntimeException {
	public BucketExistsException(String name) {
		super("Bucket " + name + "already exists");
	}
}
