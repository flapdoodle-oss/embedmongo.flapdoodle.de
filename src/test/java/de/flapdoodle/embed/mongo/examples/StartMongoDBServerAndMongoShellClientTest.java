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
package de.flapdoodle.embed.mongo.examples;

import static de.flapdoodle.embed.mongo.TestUtils.getCmdOptions;

import java.io.IOException;
import java.net.UnknownHostException;

import de.flapdoodle.embed.mongo.TestUtils;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongoShellExecutable;
import de.flapdoodle.embed.mongo.MongoShellProcess;
import de.flapdoodle.embed.mongo.MongoShellStarter;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongoShellConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;


public class StartMongoDBServerAndMongoShellClientTest {

	/*
	 // ->
	 this is an very easy example to use mongos and mongod
	 // <- 
	 */
	@Test
	public void startAndStopMongoDBAndMongoShell() throws IOException {
			// ->
		int port = Network.getFreeServerPort();
		String defaultHost = "localhost";

		MongodProcess mongod = startMongod(port);

		try {
			Thread.sleep(1000);
			MongoShellProcess mongoShell = startMongoShell(port, defaultHost);
			Thread.sleep(1000);
			try {
				MongoClient mongoClient = new MongoClient(defaultHost, port);
				System.out.println("DB Names: " + mongoClient.getDatabaseNames());
			} finally {
				mongoShell.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mongod.stop();
		}
			// <-
	}
	
	// ->
	private MongoShellProcess startMongoShell(int defaultConfigPort, String defaultHost) throws UnknownHostException,
			IOException {
		MongoShellConfig mongoShellConfig = MongoShellConfig.builder()
			.version(Version.Main.PRODUCTION)
			.net(new Net(defaultConfigPort, Network.localhostIsIPv6()))
			.scriptParameters(Lists.newArrayList("var hight=3","var width=2","function multip() { print('area ' + hight * width); }","multip()"))
			.build();

		MongoShellExecutable mongosExecutable = MongoShellStarter.getDefaultInstance().prepare(mongoShellConfig);
		MongoShellProcess mongos = mongosExecutable.start();
		return mongos;
	}

	private MongodProcess startMongod(int defaultConfigPort) throws IOException {
		final Version.Main version = Version.Main.PRODUCTION;
		MongodConfig mongoConfigConfig = MongodConfig.builder()
			.version(version)
			.cmdOptions(getCmdOptions(version))
			.net(new Net(defaultConfigPort, Network.localhostIsIPv6()))
			.build();

		MongodExecutable mongodExecutable = MongodStarter.getDefaultInstance().prepare(mongoConfigConfig);
		MongodProcess mongod = mongodExecutable.start();
		return mongod;
	}
	// <-
}
