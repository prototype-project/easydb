package com.easydb.easydb.infrastructure.space;

import java.util.UUID;

public class UUIDProvider {
	String generateUUID() {
		return UUID.randomUUID().toString();
	}
}
