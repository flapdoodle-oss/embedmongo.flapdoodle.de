/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,
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
package de.flapdoodle.embedmongo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.config.MongodProcessOutputConfig;
import de.flapdoodle.embedmongo.config.RuntimeConfig;
import de.flapdoodle.embedmongo.distribution.Version;
import de.flapdoodle.embedmongo.io.Processors;
import de.flapdoodle.embedmongo.output.IProgressListener;
import de.flapdoodle.embedmongo.runtime.Network;

/**
 * This class encapsulates everything that would be needed to do embedded
 * MongoDB testing.
 */
public class MongoTestingUtility {

	private static Logger logger = Logger.getLogger(MongodProcess.class
			.getName());

	/**
	 * A progess listener that logs to java util logging.
	 */
	private static final IProgressListener loggerProgressListener = new IProgressListener() {

		@Override
		public void done(final String label) {
			logger.fine(label + ": done");
		}

		@Override
		public void info(final String label, final String message) {
			logger.info(label + ": " + message);
		}

		@Override
		public void progress(final String label, final int percent) {
			logger.finer(label + ": " + percent + "%");
		}

		@Override
		public void start(final String label) {
			logger.fine(label + ": start");
		}
	};

	private final MongodExecutable mongodExecutable;
	private final MongodProcess mongodProcess;
	private final int port;

	/**
	 * Create the testing utility using the latest production version of
	 * MongoDB.
	 * 
	 * @throws IOException
	 */
	public MongoTestingUtility() throws IOException {
		// TODO use Version.Main.V2_0 if there is a way to convert it to Version
		this(Version.V2_0_6);
	}

	/**
	 * Create the testing utility using the specified version of MongoDB.
	 * 
	 * @param version
	 *            version of MongoDB.
	 */
	public MongoTestingUtility(final Version version) throws IOException {
		// Get open port
		final ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();

		final RuntimeConfig config = new RuntimeConfig();
		config.setMongodOutputConfig(new MongodProcessOutputConfig(Processors
				.logTo(logger, Level.INFO), Processors.logTo(logger,
				Level.SEVERE), Processors.logTo(logger, Level.FINE)));
		config.setProgressListener(loggerProgressListener);

		final MongoDBRuntime runtime = MongoDBRuntime.getInstance(config);
		mongodExecutable = runtime.prepare(new MongodConfig(Version.Main.V2_0,
				port, Network.localhostIsIPv6()));
		mongodProcess = mongodExecutable.start();

	}

	/**
	 * Creates a new DB on a new Mongo connection.
	 * 
	 * @return
	 * @throws MongoException
	 * @throws UnknownHostException
	 */
	public DB newDB() throws UnknownHostException, MongoException {
		return newMongo().getDB(UUID.randomUUID().toString());
	}

	/**
	 * Creates a new Mongo connection.
	 * 
	 * @throws MongoException
	 * @throws UnknownHostException
	 */
	public Mongo newMongo() throws UnknownHostException, MongoException {
		return new Mongo("localhost", port);
	}

	/**
	 * Cleans up the resources created by the utility.
	 */
	public void shutdown() {
		mongodProcess.stop();
		mongodExecutable.cleanup();
	}
}
