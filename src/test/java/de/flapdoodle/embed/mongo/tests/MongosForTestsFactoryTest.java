package de.flapdoodle.embed.mongo.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.MongosConfig;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.runtime.Network;

public class MongosForTestsFactoryTest {
	private final static Logger logger = Logger
			.getLogger(MongosForTestsFactoryTest.class.getName());
	
	private MongosSystemForTestFactory factory;
	private final String shardDatabase = "mydb";
	private final String shardCollection = "mycollection";
	private final String shardKey = "name";

	@Test
	public void testPopulateShardedCollection() throws Throwable {
		int max = 1000;
		List<String> names = Arrays.asList("alice", "bernard", "corinne", "chris", "danilo", "erik", "frank", "giovanni", "hector", "loren", "peter", "richard", "salvo", "tiger", "umberto");
		Random randomGenerator = new Random();
		Mongo mongo = factory.getMongo();
		mongo.setWriteConcern(WriteConcern.REPLICAS_SAFE);
		Assert.assertNotNull(mongo);
		DB db = mongo.getDB(shardDatabase);
		DBCollection collection = db.getCollection(shardCollection);
		for (int i = 0 ; i < max; i++) {
			int index = randomGenerator.nextInt(names.size());
			DBObject dbObject = (DBObject) JSON.parse("{ '" + shardKey + "': '" + names.get(index) + "', 'age': " + (30 + index) +", 'email': '" + names.get(index) + "@gmail.com'}");
			collection.save(dbObject);
			logger.info("New item created: " + dbObject);
		}
		Thread.sleep(20000);

		logger.info("Get info from config/shards");
		DBCursor cursor = mongo.getDB("config").getCollection("shards").find();
		while (cursor.hasNext()) {
			DBObject item = cursor.next();
			logger.info(item.toString());
		}

	}

	@Before
	public void testShardedDatabaseExists() throws Throwable {
		Mongo mongo = factory.getMongo();
		Assert.assertNotNull(mongo);
		boolean exists = false;
		for(String name : mongo.getDatabaseNames()) {
			if (name.equals(shardDatabase)) {
				exists = true;
				break;
			}
		}
		Assert.assertTrue(exists);
	}

	@BeforeClass
	public void beforeClass() throws Throwable {

		IVersion version = new GenericVersion("2008plus-2.2.1");
		String configDB = "";
		String replicaName = "replicaset1";
		Map<String, List<MongodConfig>> replicaSets = new HashMap<String, List<MongodConfig>>();
		replicaSets.put(replicaName, Arrays.asList(
				new MongodConfig(version, null, 37017, Network
						.localhostIsIPv6(), null, replicaName, 20),
				new MongodConfig(version, null, 37018, Network
						.localhostIsIPv6(), null, replicaName, 20),
				new MongodConfig(version, null, 37019, Network
						.localhostIsIPv6(), null, replicaName, 20)));
		
		replicaName = "replicaset2";
		replicaSets.put(replicaName, Arrays.asList(
				new MongodConfig(version, null, 37020, Network
						.localhostIsIPv6(), null, replicaName, 20),
				new MongodConfig(version, null, 37021, Network
						.localhostIsIPv6(), null, replicaName, 20),
				new MongodConfig(version, null, 37022, Network
						.localhostIsIPv6(), null, replicaName, 20)));

		List<MongodConfig> configServers = Arrays.asList(
				MongodConfig.getConfigInstance(version, 20001, Network.localhostIsIPv6()),
				MongodConfig.getConfigInstance(version, 20002, Network.localhostIsIPv6()),
				MongodConfig.getConfigInstance(version, 20003, Network.localhostIsIPv6()));
		
		for (MongodConfig mongoConfigServer : configServers) {
			if (! configDB.isEmpty()) {
				configDB += ",";
			}
			configDB += mongoConfigServer.getServerAddress().getHostName() + ":" + mongoConfigServer.getPort();
		}
		MongosConfig mongosConfig = new MongosConfig(version, null, 27017, Network
				.localhostIsIPv6(), configDB);

		factory = new MongosSystemForTestFactory(mongosConfig, replicaSets, configServers, shardDatabase, shardCollection, shardKey);
		Assert.assertNotNull(factory);
		factory.start();
	}

	@AfterClass
	public void afterClass() {
		factory.stop();
	}

}
