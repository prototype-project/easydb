package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.bucket.BucketQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

class ElementFilterToMongoQueryTransformer {
    private final BucketQuery bucketQuery;

    ElementFilterToMongoQueryTransformer(BucketQuery bucketQuery) {
        this.bucketQuery = bucketQuery;
    }

    Query transform(Optional<ElementFilter> elementFilter) {
        Query query = new Query();
        query.addCriteria(transformRecursively(elementFilter.get(), new Criteria()));
        return query;
    }

    private Criteria transformRecursively(ElementFilter rootFilter, Criteria rootCriteria) {
        if (rootFilter.getAnd() != null) {
            Criteria andCriteria = new Criteria();
            List<Criteria> andCriteries = new ArrayList<>();
            for (ElementFilter andFilter : rootFilter.getAnd()) {
                andCriteries.add(transformRecursively(andFilter, andCriteria));
            }
            Criteria [] andCriteriesArray = new Criteria[andCriteries.size()];
            andCriteria.andOperator(andCriteries.toArray(andCriteriesArray));
            rootCriteria.andOperator(andCriteria);
        }
        if (rootFilter.getOr() != null) {
            Criteria orCriteria = new Criteria();
            List<Criteria> orCriteries = new ArrayList<>();
            for (ElementFilter orFilter : rootFilter.getOr()) {
                orCriteries.add(transformRecursively(orFilter, orCriteria));
            }
            Criteria [] orCriteriesArray = new Criteria[orCriteries.size()];
            orCriteria.orOperator(orCriteries.toArray(orCriteriesArray));
            rootCriteria.orOperator(orCriteria);
        }
        if (rootFilter.getFieldsFilters() != null) {
            Criteria fieldFiltersCriteria = new Criteria();
            List<Criteria> andFieldCriteries = new ArrayList<>();
            for (FieldFilter andFieldFilter : rootFilter.getFieldsFilters()) {
                andFieldCriteries.add(Criteria.where(andFieldFilter.getName()).is(andFieldFilter.getValue()));
            }
            Criteria [] andFieldCriteriesArray = new Criteria[andFieldCriteries.size()];
            fieldFiltersCriteria.andOperator(andFieldCriteries.toArray(andFieldCriteriesArray));
            rootCriteria.andOperator(fieldFiltersCriteria);
        }
        return rootCriteria;
    }
}
