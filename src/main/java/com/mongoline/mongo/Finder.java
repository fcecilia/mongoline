package com.mongoline.mongo;

import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

import com.mongodb.*;
import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.QueryImpl;

public class Finder<T> extends QueryImpl<T> {
    public static Datastore ds;
    public static MongoClient mongo_;

    public Finder(Class<T> idType) {

        this(idType, getDataStore());

    }

    private Class<T> class_;

    public Finder(Class<T> idType, Datastore ds) {
        super(idType, ds.getCollection(idType), ds);
        class_ = idType;

    }

    private static String login;
    private static String pwd;
    private static String db;
    private static String adresse;
    private static Integer port;


    public static final String prop_login = "mongo.login";
    public static final String prop_pwd = "mongo.pwd";
    public static final String prop_db = "mongo.db";
    public static final String prop_url = "mongo.url";
    public static final String prop_port = "mongo.port";
    private static final String file_name = "conf/mongoline.properties";
    private static final String file_dev_name = "conf/mongoline-dev.properties";
    private static final String classpath_file_name = "/mongoline.properties";
    private static final String classpath_dev_file_name = "/mongoline-dev.properties";

    public T byId(String id) {
        return this.field(Mapper.ID_KEY).equal(new ObjectId(id)).get();
    }

    public static Datastore getDataStore() {
        if (ds == null) {

            Morphia morphia = new Morphia();
            ds = morphia.createDatastore(getMango(), getBaseName());

        }

        ds.ensureIndexes(); // creates all defined with @Indexed
        ds.ensureCaps(); // creates all collections for

        // @Entity(cap=@CappedAt(...))
        return ds;

    }

    public static Properties getProperties() {
        Properties prop = new Properties();
        try {
            InputStream io = Finder.class.getClass().getResourceAsStream(classpath_file_name);
            if (io == null) {
                io = Finder.class.getClass().getResourceAsStream(classpath_dev_file_name);
            }
            if (io == null) {

                File f = new File(file_name);

                if (!f.exists()) {
                    f = new File(file_dev_name);
                }


                prop.load(new FileInputStream(f));
            } else {
                prop.load(io);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return prop;
    }


    private static String getBaseName() {
        return db;
    }

    public static DB getDB() {
        // Finder.mongo_.getDB(getBaseName());

        return mongo_.getDB(Finder.getBaseName());
    }

    private static Mongo getMango() {

        if (mongo_ == null) {
            try {

                Properties prop = getProperties();

                login = prop.getProperty(prop_login);
                pwd = prop.getProperty(prop_pwd);
                db = prop.getProperty(prop_db);
                adresse = prop.getProperty(prop_url);
                port = Integer.parseInt(prop.getProperty(prop_port));


                String[] split = adresse.split(",");

                ArrayList<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();

                for (String adrr : split) {

                    ServerAddress adressems;
                    String[] split1 = adrr.split(":");
                    if (split1.length > 1) {

                        Integer port_parsed = Integer.getInteger(split1[1]);
                        if (port_parsed != null) {

                            adressems = new ServerAddress(split1[0], port_parsed);
                        } else {

                            adressems = new ServerAddress(adrr, port);

                        }

                    } else {

                        adressems = new ServerAddress(adrr, port);
                    }


                        serverAddresses.add(adressems);

                }
               /* MongoOptions opt = new MongoOptions();
                opt.autoConnectRetry = true;
                opt.connectionsPerHost = Integer.MAX_VALUE;
*/

                mongo_ = new MongoClient(serverAddresses);

                if (login != null && login.length() > 0 && pwd != null
                        && pwd.length() > 0) {
                    mongo_.getDB(getBaseName()).authenticate(login,
                            pwd.toCharArray());
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
