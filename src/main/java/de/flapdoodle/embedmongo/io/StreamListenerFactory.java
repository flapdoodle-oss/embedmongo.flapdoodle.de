package de.flapdoodle.embedmongo.io;



public class StreamListenerFactory {

	private static final String STD_PREFIX = "[mongod std] ";

	private static final String ERR_PREFIX = "[mongod err] ";

	public static IStreamListener pickBestStandardStreamListener(boolean fallbackToJdkLogging) {
		if(isSlf4jAvailable()){
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Slf4jStreamListener.class);
			return new Slf4jStreamListener(logger, Slf4jStreamListener.Level.INFO, STD_PREFIX);
		}
		if(isLog4jAvailable()){
			org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Log4jStreamListener.class);
			return new Log4jStreamListener(logger, org.apache.log4j.Level.INFO, STD_PREFIX);
		}
		if(fallbackToJdkLogging) {
			java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JdkLoggingStreamListener.class.getName());
			return new JdkLoggingStreamListener(logger, java.util.logging.Level.INFO, STD_PREFIX);
		}
		return new StdoutStreamListener(STD_PREFIX);
	}

	public static IStreamListener pickBestErrorStreamListener(boolean fallbackToJdkLogging) {
		if(isSlf4jAvailable()){
			org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Slf4jStreamListener.class);
			return new Slf4jStreamListener(logger, Slf4jStreamListener.Level.ERROR, ERR_PREFIX);
		}
		if(isLog4jAvailable()){
			org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Log4jStreamListener.class);
			return new Log4jStreamListener(logger, org.apache.log4j.Level.ERROR, ERR_PREFIX);
		}
		if(fallbackToJdkLogging) {
			java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JdkLoggingStreamListener.class.getName());
			return new JdkLoggingStreamListener(logger, java.util.logging.Level.SEVERE, ERR_PREFIX);
		}
		return new StderrStreamListener(ERR_PREFIX);
	}

	private static boolean isSlf4jAvailable() {
		try {
			Class.forName("org.slf4j.Logger");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private static boolean isLog4jAvailable() {
		try {
			Class.forName("org.apache.log4j.Logger");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}