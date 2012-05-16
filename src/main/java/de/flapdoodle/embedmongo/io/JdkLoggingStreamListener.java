package de.flapdoodle.embedmongo.io;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * {@link IStreamListener} using java.util.Logging (JUL) as a log backend.
 * 
 * @author Alexandre Dutra
 */
public class JdkLoggingStreamListener implements IStreamListener {

	private final Logger logger;
	
	private final Level level;

	private final String prefix;
	
	public JdkLoggingStreamListener(Logger logger, Level level, String prefix) {
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