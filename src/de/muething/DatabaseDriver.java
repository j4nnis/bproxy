package de.muething;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;

public class DatabaseDriver {
	public static final DatabaseDriver INSTANCE = new DatabaseDriver();
	
	private Datastore datastore;
	
	private DatabaseDriver() {
		final Morphia morphia = new Morphia();

		// tell Morphia where to find your classes
		// can be called multiple times with different packages or classes
		morphia.mapPackage("de.muething.models");

		// create the Datastore connecting to the default port on the local host
		datastore = morphia.createDatastore(new MongoClient("localhost"), "bproxy");
		datastore.ensureIndexes();
	}

	public Datastore getDatastore() {
		return datastore;
	}
	
}
