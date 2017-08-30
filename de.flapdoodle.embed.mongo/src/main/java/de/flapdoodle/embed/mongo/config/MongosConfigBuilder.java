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
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.process.builder.TypedProperty;

public class MongosConfigBuilder extends AbstractMongoConfigBuilder<IMongosConfig> {

	protected static final TypedProperty<String> CONFIG_DB = TypedProperty.with("ConfigDB", String.class);
	protected static final TypedProperty<String> RELICA_SET = TypedProperty.with("ReplicaSet", String.class);
	protected Map<String,String> args= new LinkedHashMap<>();

	public MongosConfigBuilder() throws IOException {
		super();
		property(PID_FILE).setDefault("mongos.pid");
		property(RELICA_SET).setDefault("");
	}

	public MongosConfigBuilder version(IFeatureAwareVersion version) {
		version().set(version);
		return this;
	}

	public MongosConfigBuilder timeout(Timeout timeout) {
		timeout().set(timeout);
		return this;
	}

	public MongosConfigBuilder net(Net net) {
		net().set(net);
		return this;
	}

	public MongosConfigBuilder withLaunchArgument(String name, String value) {
		args.put(name, value);
		return this;
	}

	public MongosConfigBuilder cmdOptions(IMongoCmdOptions cmdOptions) {
		cmdOptions().set(cmdOptions);
		return this;
	}

	public MongosConfigBuilder configDB(String configDB) {
		set(CONFIG_DB, configDB);
		return this;
	}

	public MongosConfigBuilder replicaSet(String replicaSet) {
		set(RELICA_SET, replicaSet);
		return this;
	}
	
	@Override
	public IMongosConfig build() {
		IFeatureAwareVersion version = version().get();
		Net net = net().get();
		Timeout timeout = timeout().get();
		String configDB = get(CONFIG_DB);
		String replicaSet = get(RELICA_SET);
		IMongoCmdOptions cmdOptions=get(CMD_OPTIONS);
		String pidFile = get(PID_FILE);

		return new ImmutableMongosConfig(version, net, timeout, cmdOptions, pidFile, configDB, replicaSet, args);
	}

	static class ImmutableMongosConfig extends ImmutableMongoConfig implements IMongosConfig {

		private final String _configDB;
		private final Map<String, String> _args;
		private final String replicaSet;

		public ImmutableMongosConfig(IFeatureAwareVersion version, Net net, Timeout timeout, IMongoCmdOptions cmdOptions,
										String pidFile, String configDB, String replicaSet, Map<String, String> args) {
			super(MongosSupportConfig.getInstance(), version, net, null, null, timeout, cmdOptions, pidFile);
			_configDB = configDB;
			this.replicaSet = replicaSet;
			_args = new LinkedHashMap<>(args);
		}

		@Override
		public String getConfigDB() {
			return _configDB;
		}
		
		@Override
		public String replicaSet() {
			return replicaSet;
		}

		@Override
		public Map<String, String> args() {
			return Collections.unmodifiableMap(_args);
		}
	}
}
