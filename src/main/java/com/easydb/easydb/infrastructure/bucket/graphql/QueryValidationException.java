package com.easydb.easydb.infrastructure.bucket.graphql;

public class QueryValidationException extends RuntimeException {
    QueryValidationException(String msg) {
        super(msg);
    }
}
