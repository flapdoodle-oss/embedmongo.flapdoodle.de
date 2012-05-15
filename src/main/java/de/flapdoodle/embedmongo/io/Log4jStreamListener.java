package de.flapdoodle.embedmongo.io;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class Log4jStreamListener implements IStreamListener {

	private final Logger logger;
	
	private final Level level;

	private final String prefix;
	
	public Log4jStreamListener(Logger logger, Level level, String prefix) {
		this.logger = logger;
		this.level = level;
		this.prefix = prefix;
	}
	
	@Override
	public void println(String line) {
		if(prefix != null) {
			logger.log(level, prefix + line);
		} else {
			logger.log(level, line);
		}
	}

}