package com.easydb.easydb.domain.bucket;

public class InvalidPaginationDataException extends IllegalArgumentException {
    InvalidPaginationDataException() {
        super("Limit must be grater or equal 0 and offset must be positive");
    }
}
