package com.easydb.easydb.domain.space;

import java.util.UUID;

public class UUIDProvider {
	public String generateUUID() {
		return UUID.randomUUID().toString();
	}
}
