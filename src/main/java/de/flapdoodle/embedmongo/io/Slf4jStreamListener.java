package de.flapdoodle.embedmongo.io;

import org.slf4j.Logger;

public class Slf4jStreamListener implements IStreamListener {

	public enum Level {
		TRACE {
			@Override
			public void log(String prefix, String message, Logger logger) {
				logger.trace("{}{}", prefix, message);
			}
		},
		DEBUG {
			@Override
			public void log(String prefix, String message, Logger logger) {
				logger.debug("{}{}", prefix, message);
			}
		},
		INFO {
			@Override
			public void log(String prefix, String message, Logger logger) {
				logger.info("{}{}", prefix, message);
			}
		},
		WARN {
			@Override
			public void log(String prefix, String message, Logger logger) {
				logger.warn("{}{}", prefix, message);
			}
		},
		ERROR {
			@Override
			public void log(String prefix, String message, Logger logger) {
				logger.error("{}{}", prefix, message);
			}
		}

		;

		public abstract void log(String prefix, String message, Logger logger);
	}

	private final Logger logger;

	private final Level level;

	private final String prefix;

	public Slf4jStreamListener(Logger logger, Level level, String prefix) {
		this.logger = logger;
		this.level = level;
		this.prefix = prefix;
	}

	@Override
	public void println(String line) {
		level.log(prefix, line, logger);
	}

}