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

public class MongoRestoreConfigBuilder extends AbstractMongoConfigBuilder<IMongoRestoreConfig> {
   protected static final TypedProperty<Boolean> VERBOSE = TypedProperty.with("verbose", Boolean.class);
   protected static final TypedProperty<String> DB_NAME = TypedProperty.with("db", String.class);
   protected static final TypedProperty<String> COLLECTION = TypedProperty.with("collection", String.class);

   protected static final TypedProperty<Boolean> OBJECT_CHECK = TypedProperty.with("objCheck", Boolean.class);
   protected static final TypedProperty<Boolean> OPLOG_REPLAY = TypedProperty.with("oplogReplay", Boolean.class);
   protected static final TypedProperty<Long> OPLOG_LIMIT = TypedProperty.with("oplogLimit", Long.class);
   protected static final TypedProperty<String> ARCHIVE = TypedProperty.with("archive", String.class);
   protected static final TypedProperty<Boolean> RESTORE_DB_USERS_ROLES = TypedProperty.with("restoreDbUsersAndRoles", Boolean.class);
   protected static final TypedProperty<String> DIR = TypedProperty.with("dir", String.class);
   protected static final TypedProperty<Boolean> GZIP = TypedProperty.with("gzip", Boolean.class);

   protected static final TypedProperty<Boolean> DROP = TypedProperty.with("drop", Boolean.class);
   protected static final TypedProperty<String> WRITE_CONCERN = TypedProperty.with("writeConcern", String.class);
   protected static final TypedProperty<Boolean> NO_INDEX_RESTORE = TypedProperty.with("noIndexRestore", Boolean.class);
   protected static final TypedProperty<Boolean> NO_OPTIONS_RESTORE = TypedProperty.with("noOptionsRestore", Boolean.class);
   protected static final TypedProperty<Boolean> KEEP_INDEX_VERSION = TypedProperty.with("keepIndexVersion", Boolean.class);
   protected static final TypedProperty<Boolean> MAINTAIN_INSERTION_ORDER = TypedProperty.with("maintainInsertionOrder", Boolean.class);
   protected static final TypedProperty<Integer> NUM_PARALLEL_COLLECTIONS = TypedProperty.with("numParallelCollections", Integer.class);
   protected static final TypedProperty<Integer> NUM_INSERTION_WORKERS = TypedProperty.with("numInsertionWorkersPerCollection", Integer.class);
   protected static final TypedProperty<Boolean> STOP_ON_ERROR = TypedProperty.with("stopOnError", Boolean.class);
   protected static final TypedProperty<Boolean> BYPASS_DOCUMENT_VALIDATION = TypedProperty.with("bypassDocumentValidation", Boolean.class);

   public MongoRestoreConfigBuilder() throws IOException {
      super();
      property(PID_FILE).setDefault("mongorestore.pid");
      property(VERBOSE).setDefault(false);
      property(GZIP).setDefault(Boolean.FALSE);
      property(OBJECT_CHECK).setDefault(Boolean.FALSE);
      property(OPLOG_REPLAY).setDefault(Boolean.FALSE);
      property(RESTORE_DB_USERS_ROLES).setDefault(Boolean.FALSE);
      property(NO_INDEX_RESTORE).setDefault(Boolean.FALSE);
      property(NO_OPTIONS_RESTORE).setDefault(Boolean.FALSE);
      property(KEEP_INDEX_VERSION).setDefault(Boolean.FALSE);
      property(MAINTAIN_INSERTION_ORDER).setDefault(Boolean.FALSE);
      property(STOP_ON_ERROR).setDefault(Boolean.FALSE);
      property(BYPASS_DOCUMENT_VALIDATION).setDefault(Boolean.FALSE);
      property(NUM_PARALLEL_COLLECTIONS).setDefault(4);
      property(NUM_INSERTION_WORKERS).setDefault(1);
   }

   public MongoRestoreConfigBuilder version(IFeatureAwareVersion version) {
      version().set(version);
      return this;
   }

   public MongoRestoreConfigBuilder timeout(Timeout timeout) {
      timeout().set(timeout);
      return this;
   }

   public MongoRestoreConfigBuilder net(Net net) {
      net().set(net);
      return this;
   }

   public MongoRestoreConfigBuilder verbose(Boolean verbose) {
      set(VERBOSE, verbose);
      return this;
   }

   public MongoRestoreConfigBuilder db(String dbName) {
      set(DB_NAME, dbName);
      return this;
   }

   public MongoRestoreConfigBuilder collection(String collection) {
      set(COLLECTION, collection);
      return this;
   }

   public MongoRestoreConfigBuilder objectCheck(Boolean shouldCheckObjects) {
      set(OBJECT_CHECK, shouldCheckObjects);
      return this;
   }

   public MongoRestoreConfigBuilder oplogReplay(Boolean shouldReplayOplog) {
      set(OPLOG_REPLAY, shouldReplayOplog);
      return this;
   }

   public MongoRestoreConfigBuilder oplogLimit(Long oplogLimit) {
      set(OPLOG_LIMIT, oplogLimit);
      return this;
   }

   public MongoRestoreConfigBuilder archive(String archive) {
      set(ARCHIVE, archive);
      return this;
   }

   public MongoRestoreConfigBuilder restoreDbUsersAndRoles(Boolean restoreDbUsersAndRoles) {
      set(RESTORE_DB_USERS_ROLES, restoreDbUsersAndRoles);
      return this;
   }

   public MongoRestoreConfigBuilder dir(String directory) {
      set(DIR, directory);
      return this;
   }

   public MongoRestoreConfigBuilder gzip(Boolean gzip) {
      set(GZIP, gzip);
      return this;
   }

   public MongoRestoreConfigBuilder dropCollection(boolean dropCollection) {
      set(DROP, dropCollection);
      return this;
   }

   public MongoRestoreConfigBuilder writeConcern(String writeConcern) {
      set(WRITE_CONCERN, writeConcern);
      return this;
   }

   public MongoRestoreConfigBuilder noIndexRestore(boolean noIndexRestore) {
      set(NO_INDEX_RESTORE, noIndexRestore);
      return this;
   }

   public MongoRestoreConfigBuilder noOptionsRestore(boolean noOptionsRestore) {
      set(NO_OPTIONS_RESTORE, noOptionsRestore);
      return this;
   }

   public MongoRestoreConfigBuilder keepIndexVersion(boolean keepIndexVersion) {
      set(KEEP_INDEX_VERSION, keepIndexVersion);
      return this;
   }

   public MongoRestoreConfigBuilder maintainInsertionOrder(boolean maintainInsertionOrder) {
      set(MAINTAIN_INSERTION_ORDER, maintainInsertionOrder);
      return this;
   }

   public MongoRestoreConfigBuilder numParallelCollections(Integer numParallelCollections) {
      set(NUM_PARALLEL_COLLECTIONS, numParallelCollections);
      return this;
   }

   public MongoRestoreConfigBuilder numInsertionWorkers(Integer numInsertionWorkers) {
      set(NUM_INSERTION_WORKERS, numInsertionWorkers);
      return this;
   }

   public MongoRestoreConfigBuilder stopOnError(boolean stopOnError) {
      set(STOP_ON_ERROR, stopOnError);
      return this;
   }

   public MongoRestoreConfigBuilder bypassDocumentValidation(boolean bypassDocumentValidation) {
      set(BYPASS_DOCUMENT_VALIDATION, bypassDocumentValidation);
      return this;
   }

   @Override
   public IMongoRestoreConfig build() {
      Net net = net().get();
      Timeout timeout = timeout().get();

      return new ImmutableMongoRestoreConfig(get(VERSION, null), net, timeout, get(VERBOSE, false), get(PID_FILE),
         get(DB_NAME, null), get(COLLECTION, null), get(OBJECT_CHECK, false), get(OPLOG_REPLAY, false), get(OPLOG_LIMIT, null),
         get(ARCHIVE, null), get(RESTORE_DB_USERS_ROLES, false), get(DIR, null), get(GZIP, false), get(DROP, false), get(WRITE_CONCERN, null),
         get(NO_INDEX_RESTORE, false), get(NO_OPTIONS_RESTORE, false), get(KEEP_INDEX_VERSION, false), get(MAINTAIN_INSERTION_ORDER, false),
         get(NUM_PARALLEL_COLLECTIONS, 4), get(NUM_INSERTION_WORKERS, 1), get(STOP_ON_ERROR, false), get(BYPASS_DOCUMENT_VALIDATION, false));
   }

   static class ImmutableMongoRestoreConfig extends ImmutableMongoConfig implements IMongoRestoreConfig {
      private final boolean _verbose;
      private final String _databaseName;
      private final String _collectionName;
      private final boolean _objectCheck;
      private final boolean _oplogReplay;
      private final Long _oplogLimit;
      private final String _archive;
      private final boolean _restoreDbUsersRoles;
      private final String _dir;
      private final boolean _gzip;
      private final boolean _dropCollection;
      private final String _writeConcern;
      private final boolean _noIndexRestore;
      private final boolean _noOptionsRestore;
      private final boolean _keepIndexVersion;
      private final boolean _maintainInsertionOrder;
      private final Integer _numParallelCollections;
      private final Integer _numInsertionWorkers;
      private final boolean _stopOnError;
      private final boolean _bypassDocumentValidation;

      public ImmutableMongoRestoreConfig(IFeatureAwareVersion version, Net net, Timeout timeout, boolean verbose, String pidFile,
                                         String database, String collection, boolean objectCheck, boolean oplogReplay, Long oplogLimit,
                                         String archive, boolean restoreDbUsersAndRoles, String dir, boolean gzip, boolean drop,
                                         String writeConcern, boolean noIndexRestore, boolean noOptionsRestore, boolean keepIndexVersion,
                                         boolean maintainInsertionOrder, Integer numParallelCollections, Integer numInsertionWorkers, boolean stopOnError,
                                         boolean bypassDocumentValidation) {
         super(new SupportConfig(Command.MongoRestore), version, net, null, null, timeout, null, pidFile);
         _verbose = verbose;
         _databaseName = database;
         _collectionName = collection;
         _objectCheck = objectCheck;
         _oplogReplay = oplogReplay;
         _oplogLimit = oplogLimit;
         _archive = archive;
         _restoreDbUsersRoles = restoreDbUsersAndRoles;
         _dir = dir;
         _gzip = gzip;
         _dropCollection = drop;
         _writeConcern = writeConcern;
         _noIndexRestore = noIndexRestore;
         _noOptionsRestore = noOptionsRestore;
         _keepIndexVersion = keepIndexVersion;
         _maintainInsertionOrder = maintainInsertionOrder;
         _numParallelCollections = numParallelCollections;
         _numInsertionWorkers = numInsertionWorkers;
         _stopOnError = stopOnError;
         _bypassDocumentValidation = bypassDocumentValidation;
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

      @Override public Long getOplogLimit() {
         return _oplogLimit;
      }

      @Override public String getArchive() {
         return _archive;
      }

      @Override public String getDir() {
         return _dir;
      }

      @Override public Integer getNumberOfParallelCollections() {
         return _numParallelCollections;
      }

      @Override public Integer getNumberOfInsertionWorkersPerCollection() {
         return _numInsertionWorkers;
      }

      @Override public String getWriteConcern() {
         return _writeConcern;
      }

      @Override public boolean isObjectCheck() {
         return _objectCheck;
      }

      @Override public boolean isOplogReplay() {
         return _oplogReplay;
      }

      @Override public boolean isRestoreDbUsersAndRoles() {
         return _restoreDbUsersRoles;
      }

      @Override public boolean isGzip() {
         return _gzip;
      }

      @Override public boolean isDropCollection() {
         return _dropCollection;
      }

      @Override public boolean isNoIndexRestore() {
         return _noIndexRestore;
      }

      @Override public boolean isNoOptionsRestore() {
         return _noOptionsRestore;
      }

      @Override public boolean isKeepIndexVersion() {
         return _keepIndexVersion;
      }

      @Override public boolean isMaintainInsertionOrder() {
         return _maintainInsertionOrder;
      }

      @Override public boolean isStopOnError() {
         return _stopOnError;
      }

      @Override public boolean isBypassDocumentValidation() {
         return _bypassDocumentValidation;
      }
   }
}