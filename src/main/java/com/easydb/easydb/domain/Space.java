package com.easydb.easydb.domain;

import java.util.List;

public class Space {
	public static Space of(String name) {
		return new Space();
	}

	public Bucket createBucket(String name, List<String> fieldsNames) {
		return new Bucket();
	}

	public boolean bucketExists(String p) {
		return true;
	}

	public void removeBucket(String name) {
	}

	public ElementQueryDto addElement(ElementCreateDto p) {
		return null;
	}

	public ElementQueryDto getElement(String bucketName, String id) {
		return null;
	}

	public void removeElement(String bucketName, String elementId) {
	}

	public boolean elementExists(String bucketName, String elementId) {
		return true;
	}

	public void updateElement(ElementUpdateDto toUpdate) {
	}
}
