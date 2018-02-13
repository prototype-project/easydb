package com.easydb.easydb.domain.bucket;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BucketQuery {

	private final String bucketName;
	private final int limit;
	private final int offset;
	private final List<Criteria> filters;

	private BucketQuery(String bucketName, int limit, int offset) {
		this.bucketName = bucketName;
		this.limit = limit;
		this.offset = offset;
		this.filters = new ArrayList<>();
		validateConstraints();
	}

	public String getBucketName() {
		return bucketName;
	}

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}

	public BucketQuery whereFieldEq(String fieldName, String fieldValue) {
		filters.add(Criteria.where("fields")
				.elemMatch(Criteria.where("name")
						.is(fieldName)
						.and("value")
						.is(fieldValue))
				.exists(true));
		return this;
	}

	public Optional<Criteria> buildMongoCriteria() {
		Criteria result = new Criteria();
		return getCriteriaList().flatMap(criteria ->
            Optional.of(result.andOperator(criteria.toArray(new Criteria[criteria.size()])))
        );
	}

	private Optional<List<Criteria>> getCriteriaList() {
		return filters.isEmpty() ? Optional.empty(): Optional.of(filters);
	}

	public static BucketQuery of(String name, int limit, int offset) {
		return new BucketQuery(name, limit, offset);
	}

	public BucketQuery rename(String bucketName) {
		BucketQuery q = BucketQuery.of(bucketName, limit, offset);
		q.filters.addAll(filters);
		return q;
	}

	private void validateConstraints() {
		if (limit <= 0 || offset < 0) {
			throw new InvalidPaginationDataException();
		}
	}

}