package de.flapdoodle.embedmongo.io;

import org.apache.commons.logging.Log;

/**
 * {@link IStreamListener} using Apache Jakarta Commons Logging (JCL) as a log backend.
 * 
 * @author Alexandre Dutra
 */
public class JCLStreamListener implements IStreamListener {

	public enum Level {
		TRACE {
			@Override
			public void log(String prefix, String message, Log logger) {
				logger.trace(prefix + message);
			}
		},
		DEBUG {
			@Override
			public void log(String prefix, String message, Log logger) {
				logger.debug(prefix + message);
			}
		},
		INFO {
			@Override
			public void log(String prefix, String message, Log logger) {
				logger.info(prefix + message);
			}
		},
		WARN {
			@Override
			public void log(String prefix, String message, Log logger) {
				logger.warn(prefix + message);
			}
		},
		ERROR {
			@Override
			public void log(String prefix, String message, Log logger) {
				logger.error(prefix + message);
			}
		}

		;

		public abstract void log(String prefix, String message, Log logger);
	}

	private final Log logger;
	
	private final Level level;

	private final String prefix;
	
	public JCLStreamListener(Log logger, Level level, String prefix) {
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
		level.log(prefix, line, logger);
	}

}