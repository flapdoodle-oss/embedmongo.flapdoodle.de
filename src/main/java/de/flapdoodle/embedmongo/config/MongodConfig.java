/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
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
package de.flapdoodle.embedmongo.config;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import de.flapdoodle.embedmongo.Files;
import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.distribution.Version;
import de.flapdoodle.embedmongo.io.IStreamListener;
import de.flapdoodle.embedmongo.io.StreamConsumer;
import de.flapdoodle.embedmongo.io.StreamListenerFactory;
import de.flapdoodle.embedmongo.runtime.Network;


/**
 * Configuration options for a {@link MongodProcess}.
 * 
 * @author Alexandre Dutra
 */
public class MongodConfig {

	/**
	 * Default Mongo port.
	 */
	private static final int DEFAULT_PORT = 27017;

	/**
	 * Default timeout (either for startup and shutdown), in milliseconds.
	 */
	private static final long DEFAULT_TIMEOUT = 20000;

	/**
	 * Default buffer length for {@link StreamConsumer}s.
	 */
	private static final int DEFAULT_BUFFER_LENGTH = 512;

	/**
	 * Mongo version.
	 */
	private Version version;

	/**
	 * Mongo port.
	 */
	private int port = DEFAULT_PORT;
	
	/**
	 * The data directory. Will be created if it does not exist.
	 */
	private File databaseDir;
	
	/**
	 * Whether this system uses IPV6 protocol. Will default to {@link Network#localhostIsIPv6()}.
	 */
	private Boolean ipv6;

	/**
	 * Whether to redirect Mongod process error stream to standard output.
	 */
	private boolean redirectErrorStream = false;

	/**
	 * {@link IStreamListener} to listen to Mongod process standard output.
	 */
	private List<IStreamListener> standardStreamListeners = new ArrayList<IStreamListener>();

	/**
	 * {@link IStreamListener} to listen to Mongod process error output.
	 */
	private List<IStreamListener> errorStreamListeners = new ArrayList<IStreamListener>();
	
	/**
	 * Startup timeout in milliseconds.
	 */
	private long startTimeout = DEFAULT_TIMEOUT;

	/**
	 * Shutdown timeout in milliseconds.
	 */
	private long shutdownTimeout = DEFAULT_TIMEOUT;
	
	/**
	 * Stream character encoding. Will default to the platform's default encoding,
	 * as reported by the system property "file.encoding".
	 */
	private String encoding;

	/**
	 * Buffer length for {@link StreamConsumer}s.
	 */
	private int bufferLength = DEFAULT_BUFFER_LENGTH;
	
	public MongodConfig() {
	}

	/**
	 * Convenience constructor.
	 * @param version
	 * @param port
	 * @param ipv6
	 */
	public MongodConfig(Version version, int port, boolean ipv6) {
		super();
		this.version = version;
		this.port = port;
		this.ipv6 = ipv6;
	}
	
	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public File getDatabaseDir() throws IOException {
		if (databaseDir == null) {
			//different dir each time
			return Files.createTempDir("embedmongo-db");
		}
		if(!databaseDir.exists()) {
			if (!databaseDir.mkdirs())
				throw new IOException("Could not create data directory: " + databaseDir);
		}
		return databaseDir;
	}

	public void setDatabaseDir(File databaseDir) {
		this.databaseDir = databaseDir;
	}

	public boolean isIpv6() throws UnknownHostException {
		if(ipv6 == null) {
			ipv6 = Network.localhostIsIPv6();
		}
		return ipv6;
	}

	public void setIpv6(boolean ipv6) {
		this.ipv6 = ipv6;
	}

	public boolean isRedirectErrorStream() {
		return redirectErrorStream;
	}

	public void setRedirectErrorStream(boolean redirectErrorStream) {
		this.redirectErrorStream = redirectErrorStream;
	}

	public List<IStreamListener> getStandardStreamListeners() {
		if(standardStreamListeners.isEmpty()) {
			standardStreamListeners.add(StreamListenerFactory.pickBestStandardStreamListener(false));
		}
		return standardStreamListeners;
	}

	public void setStandardStreamListeners(List<IStreamListener> standardStreamListeners) {
		this.standardStreamListeners = standardStreamListeners;
	}

	public List<IStreamListener> getErrorStreamListeners() {
		if(errorStreamListeners.isEmpty()) {
			errorStreamListeners.add(StreamListenerFactory.pickBestErrorStreamListener(false));
		}
		return errorStreamListeners;
	}

	public void setErrorStreamListeners(List<IStreamListener> errorStreamListeners) {
		this.errorStreamListeners = errorStreamListeners;
	}

	public long getStartTimeout() {
		return startTimeout;
	}

	public void setStartTimeout(long startTimeout) {
		this.startTimeout = startTimeout;
	}

	public long getShutdownTimeout() {
		return shutdownTimeout;
	}

	public void setShutdownTimeout(long shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}

	public String getEncoding() {
		if(encoding == null) {
			encoding = System.getProperty("file.encoding");
		}
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getBufferLength() {
		return bufferLength;
	}

	public void setBufferLength(int bufferLength) {
		this.bufferLength = bufferLength;
	}

}
