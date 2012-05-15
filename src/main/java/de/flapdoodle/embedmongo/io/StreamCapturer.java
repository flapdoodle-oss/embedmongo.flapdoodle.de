package de.flapdoodle.embedmongo.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * Captures the whole input stream into a string
 * that can be retrieved via {@link #getContents()}.
 * 
 * @author Alexandre Dutra
 *
 */
public class StreamCapturer extends Thread {
	
	private final InputStream is;
	
	private String contents;

	public StreamCapturer(String name, InputStream is) {
		super(name);
		this.is = is;
	}

	public void run() {
		try {
			contents = IOUtils.toString(is);
		} catch (IOException e) {
		}
	}

	public String getContents() {
		return contents;
	}
	
	
}
