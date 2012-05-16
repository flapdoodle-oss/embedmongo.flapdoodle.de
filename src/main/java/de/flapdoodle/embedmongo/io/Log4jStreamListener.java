package de.flapdoodle.embedmongo.io;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * {@link IStreamListener} using Apache Log4J as a log backend.
 * 
 * @author Alexandre Dutra
 */
public class Log4jStreamListener implements IStreamListener {

	private final Logger logger;
	
	private final Level level;

	private final String prefix;
	
	public Log4jStreamListener(Logger logger, Level level, String prefix) {
		this.logger = logger;
		this.level = level;
		this.prefix = prefix;
	}
	
	/**
	 * @see de.flapdoodle.embedmongo.io.IStreamListener#println(java.lang.String)
	 * {@inheritDoc}
	 */
	@Override
	public void println(String line) {
		if(prefix != null) {
			logger.log(level, prefix + line);
		} else {
			logger.log(level, line);
		}
	}

}