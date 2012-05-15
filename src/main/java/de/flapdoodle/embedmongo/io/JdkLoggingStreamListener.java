package de.flapdoodle.embedmongo.io;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JdkLoggingStreamListener implements IStreamListener {

	private final Logger logger;
	
	private final Level level;

	private final String prefix;
	
	public JdkLoggingStreamListener(Logger logger, Level level, String prefix) {
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