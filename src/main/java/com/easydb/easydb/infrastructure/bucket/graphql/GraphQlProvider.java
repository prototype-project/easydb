package com.easydb.easydb.infrastructure.bucket.graphql;

import com.coxautodev.graphql.tools.SchemaParser;
import com.easydb.easydb.domain.bucket.BucketQuery;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

public class GraphQlProvider {
    private static String SCHEMA_FILE_NAME = "schema.graphql";

    private final String schema;
    private MongoTemplate mongoTemplate;

    public GraphQlProvider(MongoTemplate mongoTemplate) {
        this.schema = readSchema();
        this.mongoTemplate = mongoTemplate;
    }

    public GraphQL graphQL(BucketQuery bucketQuery) {
        ElementFilterToMongoQueryTransformer transformer = new ElementFilterToMongoQueryTransformer(bucketQuery);
        GraphQLSchema graphQLSchema = SchemaParser.newParser().schemaString(schema)
                .resolvers(new Query(new MongoRepository(transformer, bucketQuery, mongoTemplate)))
                .build().makeExecutableSchema();
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private String readSchema() {
        try {
            File file = new ClassPathResource(SCHEMA_FILE_NAME).getFile();
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
            throw new RuntimeException("Error during reading schema", ex);
        }
    }
}
