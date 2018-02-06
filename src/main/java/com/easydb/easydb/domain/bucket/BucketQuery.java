package com.easydb.easydb.domain.bucket;


public class BucketQuery {

	private final String name;
	private final int limit;
	private final int offset;

	private BucketQuery(String name, int limit, int offset) {
		this.name = name;
		this.limit = limit;
		this.offset = offset;
		validateConstraints();
	}

	public String getName() {
		return name;
	}

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}

	public static BucketQuery of(String name, int limit, int offset) {
		return new BucketQuery(name, limit, offset);
	}

	private void validateConstraints() {
		if (limit <= 0 || offset < 0) {
			throw new InvalidPaginationDataException();
		}
	}
}