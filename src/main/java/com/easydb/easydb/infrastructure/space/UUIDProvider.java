package com.easydb.easydb.infrastructure.space;

import java.util.UUID;

public class UUIDProvider {
	public String generateUUID() {
		return UUID.randomUUID().toString();
	}
}
