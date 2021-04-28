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

import java.io.IOException;
import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptions;
import de.flapdoodle.embed.mongo.config.processlistener.CopyDbFilesFromDirBeforeProcessStop;
import de.flapdoodle.embed.process.io.directories.PlatformTempDir;
import de.flapdoodle.embed.process.io.file.Files;


public class SnapshotDbFilesTest extends AbstractMongoDBTest {

	@Override
	protected ImmutableMongodConfig.Builder createMongodConfigBuilder() throws IOException {
		ImmutableMongodConfig.Builder builder = super.createMongodConfigBuilder();
		builder.processListener(
				new CopyDbFilesFromDirBeforeProcessStop(Files.createTempDir(new PlatformTempDir(), "embedmongo-snapshot"))
				)
		.cmdOptions(MongoCmdOptions.builder()
				.useNoPrealloc(false)
				.useSmallFiles(false)
				.syncDelay(1)
				.build());
		return builder;
	}
	
	public void testUseSnapshotFiles() {
		DB db = getMongo().getDB("test");
		DBCollection col = db.createCollection("testCol", new BasicDBObject());
		col.save(new BasicDBObject("testDoc", new Date()));
	}
}
