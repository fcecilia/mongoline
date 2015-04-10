package com.mongoline.mongo;

import org.bson.types.ObjectId;
import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.UpdateOperations;

@Entity
public abstract class Model {

	@Id
	private ObjectId id;

	public String getId() {
		return id != null ? id.toString() : null;
	}

	public ObjectId getObjectId() {
		return id;
	}

	public void save(MongolineConfig mongolineConfig) {
		if (!isSaved()) {
			getFinder(mongolineConfig).getDatastore().save(this);
		}
	}

	public boolean isSaved() {
		return getId() != null;
	}

	private <T> com.mongoline.mongo.Finder<T> getFinder(MongolineConfig mongolineConfig) {
		Class<T> clazz = getClazz();
		com.mongoline.mongo.Finder<T> finder = new com.mongoline.mongo.Finder<T>(clazz, mongolineConfig);
		return finder;
	}

	public void delete(MongolineConfig mongolineConfig) {
		getFinder(mongolineConfig).getDatastore().delete(this);
	}

	public <T> void updateField(String field, Object value, MongolineConfig mongolineConfig) {
		Class<T> clazz = getClazz();
		com.mongoline.mongo.Finder<T> finder = getFinder(mongolineConfig);
		Datastore datastore = finder.getDatastore();
		Query<T> updateQuery = datastore.createQuery(clazz)
				.field(Mapper.ID_KEY).equal(getObjectId());
		UpdateOperations<T> ops;
		if(value!=null) {
		 ops = datastore.createUpdateOperations(clazz).set(
				field, value);
		} else {
			 ops = datastore.createUpdateOperations(clazz).unset(field);
		}
		datastore.update(updateQuery, ops);
	}

	public <T> void updateAdd(String field, Object value, MongolineConfig mongolineConfig) {
		if (getObjectId() == null) {
			save(mongolineConfig);
		}
		Class<T> clazz = getClazz();
		Datastore datastore = getFinder(mongolineConfig).getDatastore();
		Query<T> updateQuery = datastore.createQuery(clazz)
				.field(Mapper.ID_KEY).equal(getObjectId());
		UpdateOperations<T> createUpdateOperations = datastore
				.createUpdateOperations(clazz);
		UpdateOperations<T> ops = createUpdateOperations.add(field, value);
		datastore.update(updateQuery, ops);
	}

	public <T> void remove(String field, Object value, MongolineConfig mongolineConfig) {
		if (getObjectId() == null) {
			save(mongolineConfig);
		}
		Class<T> clazz = getClazz();
		Datastore datastore = getFinder(mongolineConfig).getDatastore();
		Query<T> updateQuery = datastore.createQuery(clazz)
				.field(Mapper.ID_KEY).equal(getObjectId());
		UpdateOperations<T> createUpdateOperations = datastore
				.createUpdateOperations(clazz);
		UpdateOperations<T> ops = createUpdateOperations
				.removeAll(field, value);
		datastore.update(updateQuery, ops);
	}

	@SuppressWarnings("unchecked")
	private <T> Class<T> getClazz() {
		Class<T> clazz = (Class<T>) this.getClass();
		return clazz;
	}
}