package com.easydb.easydb.infrastructure.bucket.graphql;

import com.coxautodev.graphql.tools.SchemaParser;
import com.easydb.easydb.domain.bucket.BucketQuery;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;

public class GraphQlProvider {
    private static String SCHEMA_FILE_NAME = "/schema.graphql";

    private final String schema;
    private MongoTemplate mongoTemplate;
    private final ElementFilterToMongoQueryConverter converter;

    public GraphQlProvider(MongoTemplate mongoTemplate, ElementFilterToMongoQueryConverter converter) {
        this.converter = converter;
        this.schema = readSchema();
        this.mongoTemplate = mongoTemplate;
    }

    GraphQL graphQL(BucketQuery bucketQuery) {
        GraphQLSchema graphQLSchema = SchemaParser.newParser().schemaString(schema)
                .resolvers(new Query(new GraphQlMongoRepository(converter, bucketQuery, mongoTemplate)))
                .build().makeExecutableSchema();
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private String readSchema() {
        try {
            InputStream in = getClass().getResourceAsStream(SCHEMA_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.lines().collect(Collectors.joining());
        } catch (Exception ex) {
            throw new RuntimeException("Error during reading schema", ex);
        }
    }
}
