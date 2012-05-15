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
package de.flapdoodle.embedmongo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.io.IStreamListener;
import de.flapdoodle.embedmongo.io.StreamConsumer;
import de.flapdoodle.embedmongo.runtime.NUMA;
import de.flapdoodle.embedmongo.runtime.ProcessShutdownWatcher;

public class MongodProcess {

	private static final Logger LOGGER = Logger.getLogger(MongodProcess.class.getName());

	private final MongodExecutable mongodExecutable;
	
	private final MongodConfig mongodConfig;
	
	private Process process;

	private boolean stopped = false;

	private StreamConsumer stdStreamConsumer;

	private StreamConsumer errStreamConsumer;
	
	public MongodProcess(MongodConfig mongodConfig, MongodExecutable mongodExecutable) throws IOException {
		
		this.mongodExecutable = mongodExecutable;
		this.mongodConfig = mongodConfig;
		
		try {
			List<String> commandLine = buildCommandLine();
			ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
			processBuilder.redirectErrorStream(mongodConfig.isRedirectErrorStream());
			process = processBuilder.start();
			Runtime.getRuntime().addShutdownHook(new MongodProcessStopper());
			ProcessStartListener statusListener = startStreamConsumers();
			boolean started = statusListener.waitForProcessStarted(mongodConfig.getStartTimeout());
			if ( ! started) {
				throw new IOException("Could not start mongod process");
			}
		} catch (IOException io) {
			stop();
			throw io;
		}

	}


	public synchronized void stop() {
		if (!stopped) {
			if (process != null) {
				try {
					sendShutdownCommand();
					waitForProcessStopped();
					stopStreamConsumers();
				} catch (IOException e) {
					LOGGER.severe(e.getMessage());
				} catch (InterruptedException e) {
					LOGGER.severe(e.getMessage());
				} finally {
					process.destroy();
				}
			}
			try {
				if ((mongodConfig.getDatabaseDir() != null) && (!Files.forceDelete(mongodConfig.getDatabaseDir())))
					LOGGER.warning("Could not delete temp db dir: " + mongodConfig.getDatabaseDir());
			} catch (IOException e) {
				//nothing more we can do
			}
			stopped = true;
		}
	}

	protected List<String> buildCommandLine() throws IOException {
		List<String> ret = new ArrayList<String>();
		ret.addAll(
			Arrays.asList(
				mongodExecutable.getFile().getAbsolutePath(), 
				"-v", 
				"--port", "" + mongodConfig.getPort(), 
				"--dbpath", mongodConfig.getDatabaseDir().getAbsolutePath(), 
				"--noprealloc", 
				"--nohttpinterface", 
				"--smallfiles"));
		if(mongodConfig.isIpv6()) ret.add("--ipv6");
		if (NUMA.isNUMA(mongodExecutable.getDistribution().getPlatform())) {
			switch (mongodExecutable.getDistribution().getPlatform()) {
				case Linux:
					ret.add("numactl");
					ret.add("--interleave=all");
					return ret;
				default:
					LOGGER.warning("NUMA Plattform detected, but not supported.");
			}
		}
		return ret;
	}

	protected ProcessStartListener startStreamConsumers() throws UnsupportedEncodingException {
		ProcessStartListener startListener = new ProcessStartListener();
		//standard output
		BufferedReader stdReader = new BufferedReader(new InputStreamReader(process.getInputStream(), mongodConfig.getEncoding()), mongodConfig.getBufferLength());
		List<IStreamListener> standardOutputListeners = mongodConfig.getStandardStreamListeners();
		stdStreamConsumer = new StreamConsumer("Mongo Std Output", stdReader, standardOutputListeners);
		stdStreamConsumer.addListener(startListener);
		stdStreamConsumer.setDaemon(true);
		stdStreamConsumer.start();
		//error output
		if( ! mongodConfig.isRedirectErrorStream()) {
			BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), mongodConfig.getEncoding()), mongodConfig.getBufferLength());
			errStreamConsumer = new StreamConsumer("Mongo Err Output", errReader, mongodConfig.getErrorStreamListeners());
			errStreamConsumer.addListener(startListener);
			errStreamConsumer.setDaemon(true);
			errStreamConsumer.start();
		}
		return startListener;
	}

	protected void sendShutdownCommand() throws UnknownHostException {
		//do NOT use "localhost", we need the raw IP to get through without authentication
		Mongo mongo = new Mongo("127.0.0.1", mongodConfig.getPort());
		//annoying: the driver prints the whole stack trace
		Logger logger = Logger.getLogger("com.mongodb");
		Level level = logger.getLevel();
		try {
			logger.setLevel(Level.OFF);
			mongo.getDB("admin").command(new BasicDBObject("shutdown",1));
		} catch (MongoException e) {
		} finally {
			logger.setLevel(level);
			mongo.close();
		}
	}

	protected void stopStreamConsumers() throws InterruptedException {
		if(stdStreamConsumer != null) {
			stdStreamConsumer.join(mongodConfig.getShutdownTimeout());
		}
		if(errStreamConsumer != null) {
			errStreamConsumer.join(mongodConfig.getShutdownTimeout());
		}
	}

	protected void waitForProcessStopped() {
		ProcessShutdownWatcher t = new ProcessShutdownWatcher(process);
		t.start();
		try {
			t.join(mongodConfig.getShutdownTimeout());
		} catch (InterruptedException e1) {
		}
		if (!t.isShutdown()) {
			throw new IllegalStateException("Couldn't stop mongod process!");
		}
	}


	private static class ProcessStartListener implements IStreamListener {

		private static final String SUCCESS_MSG = "waiting for connections on port";
		
		private CountDownLatch latch = new CountDownLatch(1);

		private boolean success = false;

		@Override
		public void println(String line) {
			if( ! success) {
				if (line.indexOf(SUCCESS_MSG) != -1) {
					success = true;
					latch.countDown();
				}
			}
		}

		public boolean waitForProcessStarted(long timeout) {
			try {
				latch.await(timeout, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				return false;
			}
			return success;
		}

	}
	
	private class MongodProcessStopper extends Thread {
		@Override
		public void run() {
			MongodProcess.this.stop();
		}
	}

}
