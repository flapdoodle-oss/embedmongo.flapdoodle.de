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
package de.flapdoodle.embed.mongo.runtime;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.flapdoodle.embed.mongo.config.IMongoDumpConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

public class MongoDump extends AbstractMongo {

   public static List<String> getCommandLine(IMongoDumpConfig config, IExtractedFileSet files)
      throws UnknownHostException {
      List<String> ret = new ArrayList<>();
      ret.addAll(Arrays.asList(files.executable().getAbsolutePath()));
      if (config.isVerbose()) {
         ret.add("-v");
      }
      applyNet(config.net(), ret);

      if (config.getDatabaseName() != null) {
         ret.add("--db");
         ret.add(config.getDatabaseName());
      }
      if (config.getCollectionName() != null) {
         ret.add("--collection");
         ret.add(config.getCollectionName());
      }

      if (config.getQuery() != null) {
         ret.add("--query");
         ret.add(config.getQuery());
      }
      if (config.getQueryFile() != null) {
         ret.add("--queryFile");
         ret.add(config.getQueryFile());
      }
      if (config.getReadPreference() != null) {
         ret.add("--readPreference");
         ret.add(config.getReadPreference());
      }
      if (config.isForceTableScan()) {
         ret.add("--forceTableScan");
      }
      if (config.getArchive() != null) {
         ret.add("--archive");
         ret.add(config.getArchive());
      }
      if (config.isDumpDbUsersAndRoles()) {
         ret.add("--dumpDbUsersAndRoles");
      }
      if (config.getOut() != null) {
         ret.add("--out");
         ret.add(config.getOut());
      }
      if (config.isGzip()) {
         ret.add("--gzip");
      }
      if (config.isRepair()) {
         ret.add("--repair");
      }
      if (config.isOplog()) {
         ret.add("--oplog");
      }
      if (config.getExcludeCollection() != null) {
         ret.add("--excludeCollection");
         ret.add(config.getExcludeCollection());
      }
      if (config.getExcludeCollectionWithPrefix() != null) {
         ret.add("--excludeCollectionWithPrefix");
         ret.add(config.getExcludeCollectionWithPrefix());
      }
      if (config.getNumberOfParallelCollections() != null) {
         ret.add("--numParallelCollections");
         ret.add(config.getNumberOfParallelCollections().toString());
      }
      System.out.println("MongoDump arguments: " + ret);

      return ret;
   }

   protected static void applyNet(Net net, List<String> ret) {
      ret.add("--port");
      ret.add("" + net.getPort());
      if (net.getBindIp()!=null) {
         ret.add("--host");
         ret.add(net.getBindIp());
      }
   }

}
