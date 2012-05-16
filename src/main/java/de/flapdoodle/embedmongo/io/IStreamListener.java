package de.flapdoodle.embedmongo.io;


/**
 * A listener that listens to process streams (standard or error output).
 * @see StreamConsumer
 * @author Alexandre Dutra
 */
public interface IStreamListener {

	/**
	 * Method invoked when a {@link StreamConsumer} receives a new line from the attached process.
	 * @param line the line read from the process output.
	 */
	void println(String line);
	
}