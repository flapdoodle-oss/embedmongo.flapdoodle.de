package de.flapdoodle.embedmongo.io;



/**
 * {@link IStreamListener} using {@link System#err} as a log backend.
 * 
 * @author Alexandre Dutra
 */
public class StderrStreamListener implements IStreamListener {

	private final String prefix;
	
	public StderrStreamListener(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void println(String line) {
		if(prefix != null) System.err.print(prefix);
		System.err.println(line);
	}

}