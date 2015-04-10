package com.mongoline.mongo;

public class MongolineConfig {

    public final String login;
    public final String pwd;
    public final String db;
    public final String adresse;
    public final Integer port;

    public MongolineConfig(String login, String pwd, String db, String adresse, Integer port) {
        this.login = login;
        this.pwd = pwd;
        this.db = db;
        this.adresse = adresse;
        this.port = port;
    }
}
