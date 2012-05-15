package de.flapdoodle.embedmongo.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StreamConsumer extends Thread {

	private static final Logger LOGGER = Logger.getLogger(StreamConsumer.class.getName());

	private final BufferedReader reader;
	
	private final List<IStreamListener> listeners = new ArrayList<IStreamListener>();

	public StreamConsumer(String name, BufferedReader reader, List<IStreamListener> listeners) {
		super(name);
		this.reader = reader;
		this.listeners.addAll(listeners);
	}

	public boolean addListener(IStreamListener e) {
		return listeners.add(e);
	}
	
	@Override
	public void run() {
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				for (IStreamListener listener : listeners) {
					listener.println(line);
				}
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error reading output stream: " + e, e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
	}
}