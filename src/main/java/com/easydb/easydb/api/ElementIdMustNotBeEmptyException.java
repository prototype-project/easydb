package com.easydb.easydb.api;

class ElementIdMustNotBeEmptyException extends RuntimeException {
	ElementIdMustNotBeEmptyException() {
		super("Element id must not be empty");
	}
}
