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
package de.flapdoodle.embed.mongo;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.flapdoodle.embed.mongo.config.IMongoDumpConfig;
import de.flapdoodle.embed.mongo.config.IMongoRestoreConfig;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoDumpConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongoRestoreConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.runtime.Network;
import junit.framework.TestCase;

public class MongoDumpExecutableTest extends TestCase {

   private static final Logger _logger = LoggerFactory.getLogger(MongoDumpExecutableTest.class.getName());

   @Test
   public void testStartMongoDump() throws IOException, InterruptedException {

      int serverPort = Network.getFreeServerPort();

      IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION).net(new Net(serverPort, Network.localhostIsIPv6())).build();

      IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(Command.MongoD).build();

      MongodExecutable mongodExe = MongodStarter.getInstance(runtimeConfig).prepare(mongodConfig);
      MongodProcess mongod = mongodExe.start();

      String jsonFile = Thread.currentThread().getContextClassLoader().getResource("dump").getFile();
      try {
         mongoRestoreExecutable(serverPort, jsonFile, true).start();
         File tempDirectory = File.createTempFile("mongo", "dump");
         tempDirectory.delete();
         tempDirectory.mkdir();
         mongoDumpExecutable(serverPort, tempDirectory.getAbsolutePath()).start();
      } catch (Exception e) {
         _logger.info("MongoDump exception: {}", e.getStackTrace());
         Assert.fail("mongoDB did not Dump data in json format");
      }

      mongod.stop();
      mongodExe.stop();
   }

   private MongoRestoreExecutable mongoRestoreExecutable(int port, String dumpLocation, Boolean drop) throws IOException {
      IMongoRestoreConfig mongoRestoreConfig = new MongoRestoreConfigBuilder()
         .version(Version.Main.PRODUCTION)
         .net(new Net(port, Network.localhostIsIPv6()))
         .dropCollection(drop)
         .dir(dumpLocation)
         .build();

      return MongoRestoreStarter.getDefaultInstance().prepare(mongoRestoreConfig);
   }

   private MongoDumpExecutable mongoDumpExecutable(int port, String dumpLocation) throws IOException {
      IMongoDumpConfig mongoDumpConfig = new MongoDumpConfigBuilder()
         .version(Version.Main.PRODUCTION)
         .net(new Net(port, Network.localhostIsIPv6()))
         .out(dumpLocation)
         .build();

      return MongoDumpStarter.getDefaultInstance().prepare(mongoDumpConfig);
   }
}
