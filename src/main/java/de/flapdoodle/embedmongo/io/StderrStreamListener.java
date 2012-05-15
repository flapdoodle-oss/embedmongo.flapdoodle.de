package de.flapdoodle.embedmongo.io;



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