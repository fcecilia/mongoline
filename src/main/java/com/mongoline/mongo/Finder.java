package com.mongoline.mongo;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.QueryImpl;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class Finder<T> extends QueryImpl<T> {
    public static Datastore ds;
    public static MongoClient mongo_;

    public Finder(Class<T> idType, MongolineConfig mongolineConfig) {
        this(idType, getDataStore(mongolineConfig));
    }

    private Class<T> class_;

    public Finder(Class<T> idType, Datastore ds) {
        super(idType, ds.getCollection(idType), ds);
        class_ = idType;
    }

    private static String db;

    public T byId(String id) {
        return this.field(Mapper.ID_KEY).equal(new ObjectId(id)).get();
    }

    public static Datastore getDataStore(MongolineConfig config) {
        Finder.db = config.db;
        if (ds == null) {
            Morphia morphia = new Morphia();
            ds = morphia.createDatastore(getMango(config), getBaseName());
        }
        ds.ensureIndexes();
        ds.ensureCaps();
        return ds;

    }

    private static String getBaseName() {
        return db;
    }

    public static DB getDB() {
        return mongo_.getDB(Finder.getBaseName());
    }

    private static Mongo getMango(MongolineConfig config) {
        if (mongo_ == null) {
            try {
                String[] split = config.adresse.split(",");
                ArrayList<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
                for (String adrr : split) {
                    ServerAddress adressems;
                    String[] split1 = adrr.split(":");
                    if (split1.length > 1) {
                        Integer port_parsed = Integer.getInteger(split1[1]);
                        if (port_parsed != null) {
                            adressems = new ServerAddress(split1[0], port_parsed);
                        } else {
                            adressems = new ServerAddress(adrr, config.port);
                        }
                    } else {
                        adressems = new ServerAddress(adrr, config.port);
                    }
                    serverAddresses.add(adressems);
                }
                mongo_ = new MongoClient(serverAddresses);
                if (config.login != null && config.login.length() > 0 && config.pwd != null
                        && config.pwd.length() > 0) {
                    mongo_.getDB(getBaseName()).authenticate(config.login,
                            config.pwd.toCharArray());
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
        return mongo_;
    }

    public Query<T> getQuery() {
        Query<T> createQuery = ds.createQuery(class_);
        return createQuery;
    }
}
