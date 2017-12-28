package com.easydb.easydb.domain;

import java.util.UUID;

public class UUIDProvider {
	String generateUUID() {
		return UUID.randomUUID().toString();
	}
}
