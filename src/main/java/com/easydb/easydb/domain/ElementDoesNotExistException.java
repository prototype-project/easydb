package com.easydb.easydb.domain;

public class ElementDoesNotExistException extends RuntimeException {
	ElementDoesNotExistException(String name, String id) {
		super("Element with name: " + name + " and id: " + id + " does not exist");
	}
}
