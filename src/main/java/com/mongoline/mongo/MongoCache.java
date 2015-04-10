package com.mongoline.mongo;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Indexed;

import java.util.Date;
import java.util.List;

@Entity
public class MongoCache extends Model {
    @Indexed
    private String key;
    private String value;
    private Integer nbrCall;
    private Date lastCall;

    public MongoCache() {
    }

    public MongoCache(String value) {
        this.value = value;
        this.key = calculKey(value);
        this.nbrCall = 1;
    }

    public MongoCache(String value, String key) {
        this.key = key;
        this.value = value;
    }

    public static String calculKey(String value) {
        return String.valueOf(value.hashCode());
    }

    public Integer getNbrCall() {
        return nbrCall;
    }

    public String call(MongolineConfig mongolineConfig) {
        if (isSaved()) {
            nbrCall++;
            lastCall = new Date();
            updateField("nbrCall", nbrCall, mongolineConfig);
            updateField("lastCall", lastCall, mongolineConfig);
        }
        String string = value + " " + nbrCall + " " + lastCall;
        System.out.println(string);
        return string;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static List<MongoCache> getAllMongoCache(MongolineConfig mongolineConfig) {
        Finder<MongoCache> finder = new Finder<MongoCache>(MongoCache.class, mongolineConfig);
        return finder.asList();
    }

    public static MongoCache getByKey(String key, MongolineConfig mongolineConfig) {
        Finder<MongoCache> finder = new Finder<MongoCache>(MongoCache.class, mongolineConfig);
        return finder.field("key").equal(key).get();
    }

    public static void deleteAll(MongolineConfig mongolineConfig) {
        List<MongoCache> allMongoCache = getAllMongoCache(mongolineConfig);
        for (MongoCache mongoCache : allMongoCache) {
            mongoCache.delete(mongolineConfig);
        }
    }
}