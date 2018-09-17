package com.easydb.easydb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {
    private String connectionString = "localhost:2181";
    private int connectionTimeoutMillis = 1000;
    private int sessionTimeoutMillis = 10000;
    private String rootPath = "/easydb";
    private int retrySleepMillis = 1000;
    private int lockerTimeoutMillis = 100;

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public int getConnectionTimeoutMillis() {
        return connectionTimeoutMillis;
    }

    public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
    }

    public int getSessionTimeoutMillis() {
        return sessionTimeoutMillis;
    }

    public void setSessionTimeoutMillis(int sessionTimeoutMillis) {
        this.sessionTimeoutMillis = sessionTimeoutMillis;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public int getRetrySleepMillis() {
        return retrySleepMillis;
    }

    public void setRetrySleepMillis(int retrySleepMillis) {
        this.retrySleepMillis = retrySleepMillis;
    }

    public int getLockerTimeoutMillis() {
        return lockerTimeoutMillis;
    }

    public void setLockerTimeoutMillis(int lockerTimeoutMillis) {
        this.lockerTimeoutMillis = lockerTimeoutMillis;
    }
}
