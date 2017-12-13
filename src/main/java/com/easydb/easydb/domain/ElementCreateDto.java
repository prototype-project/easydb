package com.easydb.easydb.domain;


public class ElementCreateDto {
	public static ElementCreateDto of(String name, ElementCreateFieldDto... fields) {
		return new ElementCreateDto();
	}
}
