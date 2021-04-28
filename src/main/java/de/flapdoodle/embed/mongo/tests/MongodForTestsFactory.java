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
package de.flapdoodle.embed.mongo.tests;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import de.flapdoodle.embed.mongo.config.ImmutableMongoCmdOptions;
import de.flapdoodle.embed.mongo.config.MongoCmdOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.Defaults;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.distribution.Version;

/**
 * This class encapsulates everything that would be needed to do embedded
 * MongoDB testing.
 */
public class MongodForTestsFactory {

	private static final Logger logger = LoggerFactory.getLogger(MongodForTestsFactory.class
			.getName());

	public static MongodForTestsFactory with(final IFeatureAwareVersion version)
			throws IOException {
		return new MongodForTestsFactory(version);
	}

	private final MongodExecutable mongodExecutable;

	private final MongodProcess mongodProcess;

	/**
	 * Create the testing utility using the latest production version of
	 * MongoDB.
	 * 
	 */
	public MongodForTestsFactory() throws IOException {
		this(Version.Main.PRODUCTION);
	}

	/**
	 * Create the testing utility using the specified version of MongoDB.
	 * 
	 * @param version
	 *            version of MongoDB.
	 */
	public MongodForTestsFactory(final IFeatureAwareVersion version) throws IOException {
		final MongodStarter runtime = MongodStarter.getInstance(Defaults.runtimeConfigFor(Command.MongoD, logger)
			.build());
		mongodExecutable = runtime.prepare(newMongodConfig(version));
		mongodProcess = mongodExecutable.start();

	}

	protected MongodConfig newMongodConfig(final IFeatureAwareVersion version) throws IOException {
		final ImmutableMongoCmdOptions.Builder cmdOptions = MongoCmdOptions.builder();
		if (version.isNewerOrEqual(4, 2, 0)) {
			cmdOptions
				.useNoPrealloc(false)
				.useSmallFiles(false);
		}

		return MongodConfig.builder()
				.version(version)
				.cmdOptions(cmdOptions.build())
				.build();
	}

	/**
	 * Creates a new Mongo connection.
	 * 
	 */
	public MongoClient newMongo() throws UnknownHostException {
		return new MongoClient(new ServerAddress(mongodProcess.getConfig().net().getServerAddress(),
				mongodProcess.getConfig().net().getPort()));
	}
	
	/**
	 * Creates a new DB with unique name for connection.
	 */
	@Deprecated
	public DB newDB(Mongo mongo) {
		return mongo.getDB(UUID.randomUUID().toString());
	}

	/**
	 * Creates a new DB with unique name for connection.
	 */
	public MongoDatabase newDatabase(MongoClient mongo) {
		return mongo.getDatabase(UUID.randomUUID().toString());
	}

	/**
	 * Cleans up the resources created by the utility.
	 */
	public void shutdown() {
		mongodProcess.stop();
		mongodExecutable.stop();
	}
}
