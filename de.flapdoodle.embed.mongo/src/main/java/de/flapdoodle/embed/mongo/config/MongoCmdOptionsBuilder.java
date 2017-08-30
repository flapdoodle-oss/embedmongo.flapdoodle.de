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

import de.flapdoodle.embed.process.builder.AbstractBuilder;
import de.flapdoodle.embed.process.builder.TypedProperty;


public class MongoCmdOptionsBuilder extends AbstractBuilder<IMongoCmdOptions> {

	protected static final TypedProperty<Integer> SYNC_DELAY = TypedProperty.with("syncDelay", Integer.class);
	protected static final TypedProperty<String> STORAGE_ENGINE = TypedProperty.with("storageEngine", String.class);
	protected static final TypedProperty<Boolean> VERBOSE = TypedProperty.with("verbose", Boolean.class);
	protected static final TypedProperty<Boolean> NOPREALLOC = TypedProperty.with("noprealloc", Boolean.class);
	protected static final TypedProperty<Boolean> SMALLFILES = TypedProperty.with("smallfiles", Boolean.class);
	protected static final TypedProperty<Boolean> NOJOURNAL = TypedProperty.with("nojournal", Boolean.class);
	protected static final TypedProperty<Boolean> ENABLE_TEXTSEARCH = TypedProperty.with("enableTextSearch", Boolean.class);
	protected static final TypedProperty<Boolean> ENABLE_AUTH = TypedProperty.with("auth", Boolean.class);
	protected static final TypedProperty<Boolean> MASTER = TypedProperty.with("master", Boolean.class);


	public MongoCmdOptionsBuilder() {
		property(SYNC_DELAY).setDefault(0);
		property(STORAGE_ENGINE).setDefault(null);
		property(VERBOSE).setDefault(false);
		property(NOPREALLOC).setDefault(true);
		property(SMALLFILES).setDefault(true);
		property(NOJOURNAL).setDefault(true);
		property(ENABLE_TEXTSEARCH).setDefault(false);
		property(ENABLE_AUTH).setDefault(false);
		property(MASTER).setDefault(false);
	}

	public MongoCmdOptionsBuilder useNoPrealloc(boolean value) {
		set(NOPREALLOC, value);
		return this;
	}

	public MongoCmdOptionsBuilder useSmallFiles(boolean value) {
		set(SMALLFILES, value);
		return this;
	}

	public MongoCmdOptionsBuilder useNoJournal(boolean value) {
		set(NOJOURNAL, value);
		return this;
	}

	public MongoCmdOptionsBuilder syncDelay(int delay) {
		set(SYNC_DELAY, delay);
		return this;
	}

	public MongoCmdOptionsBuilder verbose(boolean verbose) {
		set(VERBOSE, verbose);
		return this;
	}

	public MongoCmdOptionsBuilder enableTextSearch(boolean verbose) {
		set(ENABLE_TEXTSEARCH, verbose);
		return this;
	}

	public MongoCmdOptionsBuilder useStorageEngine(String storageEngine) {
		set(STORAGE_ENGINE, storageEngine);
		return this;
	}

	public MongoCmdOptionsBuilder enableAuth(boolean enable) {
		set(ENABLE_AUTH, enable);
		return this;
	}

	public MongoCmdOptionsBuilder master(boolean enable) {
		set(MASTER, enable);
		return this;
	}

	public MongoCmdOptionsBuilder defaultSyncDelay() {
		set(SYNC_DELAY, null);
		return this;
	}

	@Override
	public IMongoCmdOptions build() {
		Integer syncDelay = get(SYNC_DELAY, null);
		String storageEngine = get(STORAGE_ENGINE, null);
		boolean verbose = get(VERBOSE);
		boolean noPrealloc = get(NOPREALLOC);
		boolean smallFiles = get(SMALLFILES);
		boolean noJournal = get(NOJOURNAL);
		boolean enableTextSearch = get(ENABLE_TEXTSEARCH);
		boolean auth = get(ENABLE_AUTH);
		boolean master = get(MASTER);
		return new MongoCmdOptions(syncDelay, storageEngine, verbose, noPrealloc, smallFiles, noJournal, enableTextSearch, auth, master);
	}

	static class MongoCmdOptions implements IMongoCmdOptions {

		private final Integer _syncDelay;
		private final boolean _verbose;
		private final boolean _noPrealloc;
		private final boolean _smallFiles;
		private final boolean _noJournal;
		private final boolean _enableTextSearch;
		private final boolean _auth;
		private final boolean _master;
		private final String _storageEngine;

		public MongoCmdOptions(Integer syncDelay, String storageEngine, boolean verbose, boolean noPrealloc, boolean smallFiles,
                               boolean noJournal, boolean enableTextSearch, boolean auth, boolean master) {
			_syncDelay = syncDelay;
			_storageEngine = storageEngine;
			_verbose = verbose;
			_noPrealloc = noPrealloc;
			_smallFiles = smallFiles;
			_noJournal = noJournal;
			_enableTextSearch = enableTextSearch;
			_auth = auth;
			_master = master;
		}

		@Override
		public Integer syncDelay() {
			return _syncDelay;
		}

		@Override
		public String storageEngine() {
			return _storageEngine;
		}

		@Override
		public boolean isVerbose() {
			return _verbose;
		}

		@Override
		public boolean useNoPrealloc() {
			return _noPrealloc;
		}

		@Override
		public boolean useSmallFiles() {
			return _smallFiles;
		}

		@Override
		public boolean useNoJournal() {
			return _noJournal;
		}

		@Override
		public boolean enableTextSearch() {
			return _enableTextSearch;
		}

		@Override
		public boolean auth() {
			return _auth;
		}

		@Override
		public boolean master() {
			return _master;
		}
	}
}
