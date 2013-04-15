package com.mongoline.mongo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.QueryImpl;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

public class Finder<T> extends QueryImpl<T> {
	public static Datastore ds;
	public static Mongo mongo_;

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

				Properties prop = new Properties();
				try {
					prop.load(new FileInputStream("conf/mongoline.properties"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				login = prop.getProperty("mongo.login");
				pwd = prop.getProperty("mongo.pwd");
				db = prop.getProperty("mongo.db");
				adresse = prop.getProperty("mongo.url");
				port = Integer.parseInt(prop.getProperty("mongo.port"));

				ServerAddress adressem = new ServerAddress(adresse, port);

				MongoOptions opt = new MongoOptions();
				opt.autoConnectRetry = true;
				opt.connectionsPerHost = Integer.MAX_VALUE;

				mongo_ = new Mongo(adressem, opt);

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
