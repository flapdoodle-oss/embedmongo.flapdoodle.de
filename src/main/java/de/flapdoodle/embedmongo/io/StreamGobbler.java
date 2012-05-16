package de.flapdoodle.embedmongo.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Gobbles (consumes and throws away) the output from the underlying stream.
 * 
 * @author Alexandre Dutra
 */
public class StreamGobbler extends Thread {
	
	private final InputStream is;

	public StreamGobbler(String name, InputStream is) {
		super(name);
		this.is = is;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			while (br.readLine() != null);
		} catch (IOException ioe) {}
	}
}
