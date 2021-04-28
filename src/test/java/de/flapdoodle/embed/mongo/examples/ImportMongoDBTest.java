/**
 * Copyright (C) 2011
 *   Can Yaman <can@yaman.me>
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

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongoImportExecutable;
import de.flapdoodle.embed.mongo.MongoImportProcess;
import de.flapdoodle.embed.mongo.MongoImportStarter;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongoImportConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * Created by canyaman on 10/04/14.
 */
public class ImportMongoDBTest {

    @Test
    public void testStartAndStopMongoImportAndMongod() throws UnknownHostException, IOException {
        // ->
        int defaultConfigPort = Network.getFreeServerPort();
        String defaultHost = "localhost";
        String database = "importTestDB";
        String collection = "importedCollection";
        String jsonFile=Thread.currentThread().getContextClassLoader().getResource("sample.json").toString();
        jsonFile=jsonFile.replaceFirst("file:","");
        MongodProcess mongod = startMongod(defaultConfigPort);

        try {
            MongoImportProcess mongoImport = startMongoImport(defaultConfigPort, database,collection,jsonFile,true,true,true);
            try {
                MongoClient mongoClient = new MongoClient(defaultHost, defaultConfigPort);
                System.out.println("DB Names: " + mongoClient.getDatabaseNames());
            } finally {
                mongoImport.stop();
            }
        } finally {
            mongod.stop();
        }
        // <-
    }

    private MongoImportProcess startMongoImport(int port, String dbName, String collection, String jsonFile, Boolean jsonArray,Boolean upsert, Boolean drop) throws UnknownHostException,
            IOException {
        MongoImportConfig mongoImportConfig = MongoImportConfig.builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .databaseName(dbName)
                .collectionName(collection)
                .isUpsertDocuments(upsert)
                .isDropCollection(drop)
                .isJsonArray(jsonArray)
                .importFile(jsonFile)
                .build();

        MongoImportExecutable mongoImportExecutable = MongoImportStarter.getDefaultInstance().prepare(mongoImportConfig);
        MongoImportProcess mongoImport = mongoImportExecutable.start();
        return mongoImport;
    }

    private MongodProcess startMongod(int defaultConfigPort) throws UnknownHostException, IOException {
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
}
