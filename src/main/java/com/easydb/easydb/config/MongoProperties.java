package com.easydb.easydb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mongo")
public class MongoProperties {
    private String databaseName;
    private String hosts;
    private String username;
    private String password;
    private String adminDatabaseName;
    private String adminUsername;
    private String adminPassword;
    private boolean shardingEnabled = false;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getAdminDatabaseName() {
        return adminDatabaseName;
    }

    public void setAdminDatabaseName(String adminDatabaseName) {
        this.adminDatabaseName = adminDatabaseName;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public boolean getShardingEnabled() {
        return shardingEnabled;
    }

    public void setShardingEnabled(boolean shardingEnabled) {
        this.shardingEnabled = shardingEnabled;
    }
}
