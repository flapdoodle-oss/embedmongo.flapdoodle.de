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
package de.flapdoodle.embed.mongo.config;

import java.io.IOException;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.process.builder.TypedProperty;

public class MongoDumpConfigBuilder extends AbstractMongoConfigBuilder<IMongoDumpConfig> {
   protected static final TypedProperty<Boolean> VERBOSE = TypedProperty.with("verbose", Boolean.class);
   protected static final TypedProperty<String> DB_NAME = TypedProperty.with("db", String.class);
   protected static final TypedProperty<String> COLLECTION = TypedProperty.with("collection", String.class);

   protected static final TypedProperty<String> QUERY = TypedProperty.with("query", String.class);
   protected static final TypedProperty<String> QUERY_FILE = TypedProperty.with("queryFile", String.class);
   protected static final TypedProperty<String> READ_PREFERENCE = TypedProperty.with("readPreference", String.class);
   protected static final TypedProperty<Boolean> FORCE_TABLE_SCAN = TypedProperty.with("forceTableScan", Boolean.class);

   protected static final TypedProperty<String> ARCHIVE = TypedProperty.with("archive", String.class);
   protected static final TypedProperty<Boolean> DUMP_DB_USERS_ROLES = TypedProperty.with("dumpDbUsersAndRoles", Boolean.class);
   protected static final TypedProperty<Boolean> GZIP = TypedProperty.with("gzip", Boolean.class);
   protected static final TypedProperty<Boolean> REPAIR = TypedProperty.with("repair", Boolean.class);
   protected static final TypedProperty<String> OUT = TypedProperty.with("dir", String.class);
   protected static final TypedProperty<Boolean> OPLOG = TypedProperty.with("oplog", Boolean.class);
   protected static final TypedProperty<String> EXCLUDE_COLLECTION = TypedProperty.with("excludeCollection", String.class);
   protected static final TypedProperty<String> EXCLUDE_COLLECTION_WITH_PREFIX = TypedProperty.with("excludeCollectionWithPrefix", String.class);
   protected static final TypedProperty<Integer> NUM_PARALLEL_COLLECTIONS = TypedProperty.with("numParallelCollections", Integer.class);

   public MongoDumpConfigBuilder() throws IOException {
      super();
      property(PID_FILE).setDefault("mongodump.pid");
      property(VERBOSE).setDefault(false);
      property(GZIP).setDefault(Boolean.FALSE);
      property(OPLOG).setDefault(Boolean.FALSE);
      property(REPAIR).setDefault(Boolean.FALSE);
      property(DUMP_DB_USERS_ROLES).setDefault(Boolean.FALSE);
      property(FORCE_TABLE_SCAN).setDefault(Boolean.FALSE);
      property(NUM_PARALLEL_COLLECTIONS).setDefault(4);
   }

   public MongoDumpConfigBuilder version(IFeatureAwareVersion version) {
      version().set(version);
      return this;
   }

   public MongoDumpConfigBuilder timeout(Timeout timeout) {
      timeout().set(timeout);
      return this;
   }

   public MongoDumpConfigBuilder net(Net net) {
      net().set(net);
      return this;
   }

   public MongoDumpConfigBuilder verbose(Boolean verbose) {
      set(VERBOSE, verbose);
      return this;
   }

   public MongoDumpConfigBuilder db(String dbName) {
      set(DB_NAME, dbName);
      return this;
   }

   public MongoDumpConfigBuilder collection(String collection) {
      set(COLLECTION, collection);
      return this;
   }

   public MongoDumpConfigBuilder query(String query) {
      set(QUERY, query);
      return this;
   }

   public MongoDumpConfigBuilder queryFile(String queryFile) {
      set(QUERY_FILE, queryFile);
      return this;
   }

   public MongoDumpConfigBuilder readPreference(String readPreference) {
      set(READ_PREFERENCE, readPreference);
      return this;
   }

   public MongoDumpConfigBuilder forceTableScan(Boolean forceTableScan) {
      set(FORCE_TABLE_SCAN, forceTableScan);
      return this;
   }

   public MongoDumpConfigBuilder archive(String archive) {
      set(ARCHIVE, archive);
      return this;
   }

   public MongoDumpConfigBuilder dumpDbUsersAndRoles(Boolean dumpDbUsersAndRoles) {
      set(DUMP_DB_USERS_ROLES, dumpDbUsersAndRoles);
      return this;
   }

   public MongoDumpConfigBuilder out(String out) {
      set(OUT, out);
      return this;
   }

   public MongoDumpConfigBuilder repair(Boolean repair) {
      set(REPAIR, repair);
      return this;
   }

   public MongoDumpConfigBuilder gzip(Boolean gzip) {
      set(GZIP, gzip);
      return this;
   }

   public MongoDumpConfigBuilder oplog(boolean oplog) {
      set(OPLOG, oplog);
      return this;
   }

   public MongoDumpConfigBuilder excludeCollection(String excludeCollection) {
      set(EXCLUDE_COLLECTION, excludeCollection);
      return this;
   }

   public MongoDumpConfigBuilder excludeCollectionWithPrefix(String excludeCollectionWithPrefix) {
      set(EXCLUDE_COLLECTION_WITH_PREFIX, excludeCollectionWithPrefix);
      return this;
   }

   public MongoDumpConfigBuilder numParallelCollections(Integer numParallelCollections) {
      set(NUM_PARALLEL_COLLECTIONS, numParallelCollections);
      return this;
   }

   @Override
   public IMongoDumpConfig build() {
      Net net = net().get();
      Timeout timeout = timeout().get();

      return new ImmutableMongoDumpConfig(get(VERSION, null), net, timeout, get(VERBOSE, false), get(PID_FILE),
         get(DB_NAME, null), get(COLLECTION, null), get(QUERY, null), get(QUERY_FILE, null), get(READ_PREFERENCE, null),
         get(FORCE_TABLE_SCAN, false), get(ARCHIVE, null), get(DUMP_DB_USERS_ROLES, false), get(OUT, null), get(GZIP, false),
         get(REPAIR, false), get(OPLOG, null), get(EXCLUDE_COLLECTION, null), get(EXCLUDE_COLLECTION_WITH_PREFIX, null),
         get(NUM_PARALLEL_COLLECTIONS, 4));
   }

   static class ImmutableMongoDumpConfig extends ImmutableMongoConfig implements IMongoDumpConfig {
      private final boolean _verbose;
      private final String _databaseName;
      private final String _collectionName;

      private final String _query;
      private final String _queryFile;
      private final String _readPreference;
      private final boolean _forceTableScan;

      private final String _archive;
      private final boolean _dumpDbUsersRoles;
      private final String _out;
      private final boolean _gzip;
      private final boolean _repair;
      private final boolean _oplog;
      private final String _excludeCollection;
      private final String _excludeCollectionWithPrefix;
      private final Integer _numParallelCollections;

      public ImmutableMongoDumpConfig(IFeatureAwareVersion version, Net net, Timeout timeout, boolean verbose, String pidFile,
                                         String database, String collection, String query, String queryFile, String readPreference, boolean forceTableScan,
                                         String archive, boolean dumpDbUsersAndRoles, String out, boolean gzip, boolean repair,
                                         boolean oplog, String excludeCollection, String excludeCollectionWithPrefix, Integer numParallelCollections) {
         super(new SupportConfig(Command.MongoRestore), version, net, null, null, timeout, null, pidFile);
         _verbose = verbose;
         _databaseName = database;
         _collectionName = collection;
         _query = query;
         _queryFile = queryFile;
         _readPreference = readPreference;
         _forceTableScan = forceTableScan;
         _archive = archive;
         _dumpDbUsersRoles = dumpDbUsersAndRoles;
         _out = out;
         _gzip = gzip;
         _repair = repair;
         _oplog = oplog;
         _excludeCollection = excludeCollection;
         _excludeCollectionWithPrefix = excludeCollectionWithPrefix;
         _numParallelCollections = numParallelCollections;
      }

      @Override public boolean isVerbose() {
         return _verbose;
      }

      @Override public String getDatabaseName() {
         return _databaseName;
      }

      @Override public String getCollectionName() {
         return _collectionName;
      }

      @Override public String getQuery() {
         return _query;
      }

      @Override public String getQueryFile() {
         return _queryFile;
      }

      @Override public String getReadPreference() {
         return _readPreference;
      }

      @Override public boolean isForceTableScan() {
         return _forceTableScan;
      }

      @Override public String getArchive() {
         return _archive;
      }

      @Override public boolean isDumpDbUsersAndRoles() {
         return _dumpDbUsersRoles;
      }

      @Override public String getOut() {
         return _out;
      }

      @Override public Integer getNumberOfParallelCollections() {
         return _numParallelCollections;
      }


      @Override public boolean isGzip() {
         return _gzip;
      }

      @Override public boolean isRepair() {
         return _repair;
      }

      @Override public boolean isOplog() {
         return _oplog;
      }

      @Override public String getExcludeCollection() {
         return _excludeCollection;
      }

      @Override public String getExcludeCollectionWithPrefix() {
         return _excludeCollectionWithPrefix;
      }
   }
}