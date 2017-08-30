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

import de.flapdoodle.embed.mongo.config.IMongoRestoreConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

public class MongoRestore extends AbstractMongo {

   public static List<String> getCommandLine(IMongoRestoreConfig config, IExtractedFileSet files)
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
      if (config.isObjectCheck()) {
         ret.add("--objCheck");
      }
      if (config.isOplogReplay()) {
         ret.add("--oplogReplay");
      }
      if (config.getOplogLimit() != null) {
         ret.add("--oplogLimit");
         ret.add(config.getOplogLimit().toString());
      }
      if (config.getArchive() != null) {
         ret.add(String.format("--archive=%s", config.getArchive()));
      }
      if (config.isRestoreDbUsersAndRoles()) {
         ret.add("--restoreDbUsersAndRoles");
      }
      if (config.getDir() != null) {
         ret.add("--dir");
         ret.add(config.getDir());
      }
      if (config.isGzip()) {
         ret.add("--gzip");
      }
      if (config.isDropCollection()) {
         ret.add("--drop");
      }
      if (config.getWriteConcern() != null) {
         ret.add("--writeConcern");
         ret.add(config.getWriteConcern());
      }
      if (config.isNoIndexRestore()) {
         ret.add("--noIndexRestore");
      }
      if (config.isNoOptionsRestore()) {
         ret.add("--noOptionsRestore");
      }
      if (config.isKeepIndexVersion()) {
         ret.add("--keepIndexVersion");
      }
      if (config.isMaintainInsertionOrder()) {
         ret.add("--maintainInsertionOrder");
      }
      if (config.getNumberOfParallelCollections() != null) {
         ret.add("--numParallelCollections");
         ret.add(config.getNumberOfParallelCollections().toString());
      }
      if (config.getNumberOfInsertionWorkersPerCollection() != null) {
         ret.add("--numInsertionWorkersPerCollection");
         ret.add(config.getNumberOfInsertionWorkersPerCollection().toString());
      }
      if (config.isStopOnError()) {
         ret.add("--stopOnError");
      }
      if (config.isBypassDocumentValidation()) {
         ret.add("--bypassDocumentValidation");
      }
      System.out.println("arguments = " + ret);

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
