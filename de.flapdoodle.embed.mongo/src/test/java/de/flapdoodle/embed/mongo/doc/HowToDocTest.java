/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embed.mongo.doc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.ClassRule;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongoImportExecutable;
import de.flapdoodle.embed.mongo.MongoImportProcess;
import de.flapdoodle.embed.mongo.MongoImportStarter;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.ExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.IMongoImportConfig;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongoImportConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.config.Timeout;
import de.flapdoodle.embed.mongo.config.processlistener.ProcessListenerBuilder;
import de.flapdoodle.embed.mongo.distribution.Feature;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.distribution.Versions;
import de.flapdoodle.embed.mongo.examples.AbstractMongoDBTest;
import de.flapdoodle.embed.mongo.examples.FileStreamProcessor;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.config.store.HttpProxyFactory;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.progress.LoggingProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.testdoc.Includes;
import de.flapdoodle.testdoc.Recorder;
import de.flapdoodle.testdoc.Recording;
import de.flapdoodle.testdoc.TabSize;

public class HowToDocTest {

	@ClassRule
	public static final Recording recording=Recorder.generateMarkDown("Howto.md",TabSize.spaces(2));
	
	@Test
	public void testStandard() throws UnknownHostException, IOException {
		recording.begin();
		MongodStarter starter = MongodStarter.getDefaultInstance();

		int port = Network.getFreeServerPort();
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(port, Network.localhostIsIPv6()))
				.build();

		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = starter.prepare(mongodConfig);
			MongodProcess mongod = mongodExecutable.start();

			try (MongoClient mongo = new MongoClient("localhost", port)) {
				DB db = mongo.getDB("test");
				DBCollection col = db.createCollection("testCol", new BasicDBObject());
				col.save(new BasicDBObject("testDoc", new Date()));
			}

		} finally {
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
		recording.end();
	}

	@Test
	public void testCustomMongodFilename() throws UnknownHostException, IOException {
		recording.begin();		
		int port = Network.getFreeServerPort();

		Command command = Command.MongoD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
		.defaults(command)
		.artifactStore(new ExtractedArtifactStoreBuilder()
				.defaults(command)
				.download(new DownloadConfigBuilder()
						.defaultsForCommand(command).build())
				.executableNaming(new UserTempNaming()))
		.build();

		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(port, Network.localhostIsIPv6()))
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);

		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = runtime.prepare(mongodConfig);
			MongodProcess mongod = mongodExecutable.start();

			try (MongoClient mongo = new MongoClient("localhost", port)) {
				DB db = mongo.getDB("test");
				DBCollection col = db.createCollection("testCol", new BasicDBObject());
				col.save(new BasicDBObject("testDoc", new Date()));
			}

		} finally {
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
		recording.end();		
	}

	public void testUnitTests() {
		// @include AbstractMongoDBTest.java
		Class<?> see = AbstractMongoDBTest.class;
	}

	@Test
	public void testMongodForTests() throws IOException {
		recording.begin();
		MongodForTestsFactory factory = null;
		try {
			factory = MongodForTestsFactory.with(Version.Main.PRODUCTION);

			try (MongoClient mongo = factory.newMongo()) {
				DB db = mongo.getDB("test-" + UUID.randomUUID());
				DBCollection col = db.createCollection("testCol", new BasicDBObject());
				col.save(new BasicDBObject("testDoc", new Date()));
			}

		} finally {
			if (factory != null)
				factory.shutdown();
		}
		recording.end();
	}

	@Test
	public void testCustomizeDownloadURL() {
		recording.begin();
		Command command = Command.MongoD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(new ExtractedArtifactStoreBuilder()
						.defaults(command)
						.download(new DownloadConfigBuilder()
								.defaultsForCommand(command)
								.downloadPath("http://my.custom.download.domain/")))
				.build();
		recording.end();
	}

	@Test 
	public void testCustomProxy() {
		recording.begin();
		Command command = Command.MongoD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(new ExtractedArtifactStoreBuilder()
						.defaults(command)
						.download(new DownloadConfigBuilder()
								.defaultsForCommand(command)
								.proxyFactory(new HttpProxyFactory("fooo", 1234))))
				.build();
		recording.end();
	}
	
	@Test
	public void testCustomizeArtifactStorage() throws IOException {
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(Network.getFreeServerPort(), Network.localhostIsIPv6()))
				.build();

		// ->
		// ...
		recording.begin();
		IDirectory artifactStorePath = new FixedPath(System.getProperty("user.home") + "/.embeddedMongodbCustomPath");
		ITempNaming executableNaming = new UUIDTempNaming();

		Command command = Command.MongoD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(new ExtractedArtifactStoreBuilder()
						.defaults(command)
						.download(new DownloadConfigBuilder()
								.defaultsForCommand(command)
								.artifactStorePath(artifactStorePath))
						.executableNaming(executableNaming))
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		MongodExecutable mongodExe = runtime.prepare(mongodConfig);
		recording.end();
		// ...
		// <-
		MongodProcess mongod = mongodExe.start();

		mongod.stop();
		mongodExe.stop();
	}

	@Test
	public void testCustomOutputToConsolePrefix() {
		// ->
		// ...
		recording.begin();
		ProcessOutput processOutput = new ProcessOutput(Processors.namedConsole("[mongod>]"),
				Processors.namedConsole("[MONGOD>]"), Processors.namedConsole("[console>]"));

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MongoD)
				.processOutput(processOutput)
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		recording.end();
		// ...
		// <-
	}

	@Test
	public void testCustomOutputToFile() throws FileNotFoundException, IOException {
		recording.include(FileStreamProcessor.class, Includes.WithoutImports, Includes.WithoutPackage, Includes.Trim);
		// ->
		// ...
		recording.begin();
		IStreamProcessor mongodOutput = Processors.named("[mongod>]",
				new FileStreamProcessor(File.createTempFile("mongod", "log")));
		IStreamProcessor mongodError = new FileStreamProcessor(File.createTempFile("mongod-error", "log"));
		IStreamProcessor commandsOutput = Processors.namedConsole("[console>]");

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MongoD)
				.processOutput(new ProcessOutput(mongodOutput, mongodError, commandsOutput))
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		recording.end();
		// ...
		// <-
	}

	@Test
	public void testCustomOutputToLogging() throws FileNotFoundException, IOException {
		// ->
		// ...
		recording.begin();
		Logger logger = Logger.getLogger(getClass().getName());

		ProcessOutput processOutput = new ProcessOutput(Processors.logTo(logger, Level.INFO), Processors.logTo(logger,
				Level.SEVERE), Processors.named("[console>]", Processors.logTo(logger, Level.FINE)));

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.MongoD, logger)
				.processOutput(processOutput)
				.artifactStore(new ExtractedArtifactStoreBuilder()
						.defaults(Command.MongoD)
						.download(new DownloadConfigBuilder()
								.defaultsForCommand(Command.MongoD)
								.progressListener(new LoggingProgressListener(logger, Level.FINE))))
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		recording.end();
		// ...
		// <-
	}

	// #### ... to default java logging (the easy way)
	@Test
	public void testDefaultOutputToLogging() throws FileNotFoundException, IOException {
		// ->
		// ...
		recording.begin();
		Logger logger = Logger.getLogger(getClass().getName());

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.MongoD, logger)
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		recording.end();
		// ...
		// <-
	}

	// #### ... to null device
	@Test
	public void testDefaultOutputToNone() throws FileNotFoundException, IOException {
		int port = 12345;
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Versions.withFeatures(new GenericVersion("2.0.7-rc1"), Feature.SYNC_DELAY))
				.net(new Net(port, Network.localhostIsIPv6()))
				.build();
		// ->
		// ...
		recording.begin();
		Logger logger = Logger.getLogger(getClass().getName());

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.MongoD, logger)
				.processOutput(ProcessOutput.getDefaultInstanceSilent())
				.build();

		MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);
		recording.end();
		// ...
		// <-
		MongodProcess mongod = null;

		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = runtime.prepare(mongodConfig);
			mongod = mongodExecutable.start();

			try (MongoClient mongo = new MongoClient("localhost", port)) {
				DB db = mongo.getDB("test");
				DBCollection col = db.createCollection("testCol", new BasicDBObject());
				col.save(new BasicDBObject("testDoc", new Date()));
			}

		} finally {
			if (mongod != null) {
				mongod.stop();
			}
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
	}

	// ### Custom Version
	@Test
	public void testCustomVersion() throws UnknownHostException, IOException {
		// ->
		// ...
		recording.begin();
		int port = 12345;
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Versions.withFeatures(new GenericVersion("2.0.7-rc1"), Feature.SYNC_DELAY))
				.net(new Net(port, Network.localhostIsIPv6()))
				.build();

		MongodStarter runtime = MongodStarter.getDefaultInstance();
		MongodProcess mongod = null;

		MongodExecutable mongodExecutable = null;
		try {
			mongodExecutable = runtime.prepare(mongodConfig);
			mongod = mongodExecutable.start();

			// <-
			recording.end();
			try (MongoClient mongo = new MongoClient("localhost", port)) {
				DB db = mongo.getDB("test");
				DBCollection col = db.createCollection("testCol", new BasicDBObject());
				col.save(new BasicDBObject("testDoc", new Date()));
			}
			recording.begin();
			// ->
			// ...

		} finally {
			if (mongod != null) {
				mongod.stop();
			}
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
		recording.end();
		// ...
		// <-

	}

	// ### Main Versions
	@Test
	public void testMainVersions() throws UnknownHostException, IOException {
		// ->
		recording.begin();
		IVersion version = Version.V2_2_5;
		// uses latest supported 2.2.x Version
		version = Version.Main.V2_2;
		// uses latest supported production version
		version = Version.Main.PRODUCTION;
		// uses latest supported development version
		version = Version.Main.DEVELOPMENT;
		recording.end();
		// <-
	}

	// ### Use Free Server Port
	/*
	// ->
		Warning: maybe not as stable, as expected.
	// <-
	 */
	// #### ... by hand
	@Test
	public void testFreeServerPort() throws UnknownHostException, IOException {
		// ->
		// ...
		recording.begin();
		int port = Network.getFreeServerPort();
		recording.end();
		// ...
		// <-
	}

	// #### ... automagic
	@Test
	public void testFreeServerPortAuto() throws UnknownHostException, IOException {
		// ->
		// ...
		recording.begin();
		IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();

		MongodStarter runtime = MongodStarter.getDefaultInstance();

		MongodExecutable mongodExecutable = null;
		MongodProcess mongod = null;
		try {
			mongodExecutable = runtime.prepare(mongodConfig);
			mongod = mongodExecutable.start();

			try (MongoClient mongo = new MongoClient(
					new ServerAddress(mongodConfig.net().getServerAddress(), mongodConfig.net().getPort()))) {
			// <-
				recording.end();
				DB db = mongo.getDB("test");
				DBCollection col = db.createCollection("testCol", new BasicDBObject());
				col.save(new BasicDBObject("testDoc", new Date()));
				recording.begin();
			}
			// ->
			// ...

		} finally {
			if (mongod != null) {
				mongod.stop();
			}
			if (mongodExecutable != null)
				mongodExecutable.stop();
		}
		recording.end();
		// ...
		// <-
	}

	// ### ... custom timeouts
	@Test
	public void testCustomTimeouts() throws UnknownHostException, IOException {
		// ->
		// ...
		recording.begin();
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.timeout(new Timeout(30000))
				.build();
		recording.end();
		// ...
		// <-
	}

	// ### Command Line Post Processing
	@Test
	public void testCommandLinePostProcessing() {

		// ->
		// ...
		recording.begin();
		ICommandLinePostProcessor postProcessor = // ...
				// <-
				new ICommandLinePostProcessor() {
					@Override
					public List<String> process(Distribution distribution, List<String> args) {
						return null;
					}
				};
		recording.end();
		// ->
		recording.begin();
		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MongoD)
				.commandLinePostProcessor(postProcessor)
				.build();
		recording.end();
		// ...
		// <-
	}

	// ### Custom Command Line Options
	/*
	// ->
		We changed the syncDelay to 0 which turns off sync to disc. To turn on default value used defaultSyncDelay().
	// <-
	 */
	@Test
	public void testCommandLineOptions() throws UnknownHostException, IOException {
		// ->
		recording.begin();
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.cmdOptions(new MongoCmdOptionsBuilder()
						.syncDelay(10)
						.useNoPrealloc(false)
						.useSmallFiles(false)
						.useNoJournal(false)
						.enableTextSearch(true)
						.build())
				.build();
		recording.end();
		// ...
		// <-

	}

	// ### Snapshot database files from temp dir
	/*
	// ->
		We changed the syncDelay to 0 which turns off sync to disc. To get the files to create an snapshot you must turn on default value (use defaultSyncDelay()).
	// <-
	 */
	@Test
	public void testSnapshotDbFiles() throws UnknownHostException, IOException {
		File destination = null;
		// ->
		recording.begin();
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.processListener(new ProcessListenerBuilder()
						.copyDbFilesBeforeStopInto(destination)
						.build())
				.cmdOptions(new MongoCmdOptionsBuilder()
						.defaultSyncDelay()
						.build())
				.build();
		recording.end();
		// ...
		// <-
	}
	// ### Custom database directory  
	/*
	// ->
		If you set a custom database directory, it will not be deleted after shutdown
	// <-
	 */
	@Test
	public void testCustomDatabaseDirectory() throws UnknownHostException, IOException {
		// ->
		recording.begin();
		Storage replication = new Storage("/custom/databaseDir",null,0);
		
		IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.replication(replication)
				.build();
		recording.end();
		// ...
		// <-
	}
	// ### Start mongos with mongod instance
	// @include StartConfigAndMongoDBServerTest.java
	
	// ## Common Errors
	
	// ### Executable Collision

	/*
	// ->
	There is a good chance of filename collisions if you use a custom naming schema for the executable (see [Usage - custom mongod filename](#usage---custom-mongod-filename)).
	If you got an exception, then you should make your RuntimeConfig or MongoStarter class or jvm static (static final in your test class or singleton class for all tests).
	// <-
	*/
	
  @Test
  public void importJsonIntoMongoDB() throws UnknownHostException, IOException {
		String jsonFile = Thread.currentThread().getContextClassLoader().getResource("sample.json").toString();
		jsonFile = jsonFile.replaceFirst("file:", "");
		String defaultHost = "localhost";
		
  	recording.begin();
		int defaultConfigPort = Network.getFreeServerPort();
		String database = "importTestDB";
		String collection = "importedCollection";

		IMongodConfig mongoConfigConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(defaultConfigPort, Network.localhostIsIPv6()))
				.build();

		MongodExecutable mongodExecutable = MongodStarter.getDefaultInstance().prepare(mongoConfigConfig);
		MongodProcess mongod = mongodExecutable.start();

		try {
			IMongoImportConfig mongoImportConfig = new MongoImportConfigBuilder()
					.version(Version.Main.PRODUCTION)
					.net(new Net(defaultConfigPort, Network.localhostIsIPv6()))
					.db(database)
					.collection(collection)
					.upsert(true)
					.dropCollection(true)
					.jsonArray(true)
					.importFile(jsonFile)
					.build();

			MongoImportExecutable mongoImportExecutable = MongoImportStarter.getDefaultInstance().prepare(mongoImportConfig);
			MongoImportProcess mongoImport = mongoImportExecutable.start();
			try {
				recording.end();
				MongoClient mongoClient = new MongoClient(defaultHost, defaultConfigPort);
				System.out.println("DB Names: " + mongoClient.getDatabaseNames());
				recording.begin();
			}
			finally {
				mongoImport.stop();
			}
		}
		finally {
			mongod.stop();
		}
		recording.end();
  }
}
