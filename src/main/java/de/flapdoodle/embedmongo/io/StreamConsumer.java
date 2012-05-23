package de.flapdoodle.embedmongo.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Consumes the underlying character stream (typically from a process output) and
 * notifies the registered {@link IStreamListener}s whenever a new line is read.
 * 
 * @author Alexandre Dutra
 *
 */
public class StreamConsumer extends Thread {

	private static final Logger LOGGER = Logger.getLogger(StreamConsumer.class.getName());

	private final BufferedReader reader;
	
	private final List<IStreamListener> listeners = new CopyOnWriteArrayList<IStreamListener>();

	public StreamConsumer(String name, BufferedReader reader, List<IStreamListener> listeners) {
		super(name);
		this.reader = reader;
		this.listeners.addAll(listeners);
	}

	/**
	 * Register a new {@link IStreamListener}.
	 * @param e the new {@link IStreamListener} to register.
	 * @return
	 */
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