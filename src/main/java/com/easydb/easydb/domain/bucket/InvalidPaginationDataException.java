package com.easydb.easydb.domain.bucket;

public class InvalidPaginationDataException extends IllegalArgumentException {
	public InvalidPaginationDataException() {
		super("Limit must be grater than 0 and offset must be positive");
	}
}
