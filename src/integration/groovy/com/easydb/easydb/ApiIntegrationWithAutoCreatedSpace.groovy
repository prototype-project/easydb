package com.easydb.easydb


class ApiIntegrationWithAutoCreatedSpace extends IntegrationDatabaseSpec implements TestHttpOperations {
    String spaceName

    def setup() {
        spaceName = addSampleSpace().body.spaceName
    }
}
