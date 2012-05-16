package de.flapdoodle.embedmongo.io;


/**
 * {@link IStreamListener} using {@link System#out} as a log backend.
 * 
 * @author Alexandre Dutra
 */
public class StdoutStreamListener implements IStreamListener {

	private final String prefix;
	
	public StdoutStreamListener(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void println(String line) {
		if(prefix != null) System.out.print(prefix);
		System.out.println(line);
	}

}