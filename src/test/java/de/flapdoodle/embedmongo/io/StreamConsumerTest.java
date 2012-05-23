package de.flapdoodle.embedmongo.io;

import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StreamConsumerTest {

	private static final String PREFIX = "PREFIX";

	private static final String LINE = "Wed May 16 17:26:11 [initandlisten] waiting for connections on port 12345";

	@Mock
	private BufferedReader reader;

	@Test
	public void testShouldNotifyJULLogger() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		java.util.logging.Logger logger = Mockito.mock(java.util.logging.Logger.class);
		
		//When
		java.util.logging.Level level = java.util.logging.Level.INFO;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new JdkLoggingStreamListener(logger, level, null));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).log(level, LINE);
	}


	@Test
	public void testShouldNotifyJULLoggerWithPrefix() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		java.util.logging.Logger logger = Mockito.mock(java.util.logging.Logger.class);
		
		//When
		java.util.logging.Level level = java.util.logging.Level.INFO;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new JdkLoggingStreamListener(logger, level, PREFIX));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).log(level, PREFIX + LINE);
	}

	@Test
	public void testShouldNotifyJCLLogger() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		org.apache.commons.logging.Log logger = Mockito.mock(org.apache.commons.logging.Log.class);
		
		//When
		de.flapdoodle.embedmongo.io.JCLStreamListener.Level level = de.flapdoodle.embedmongo.io.JCLStreamListener.Level.INFO;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new JCLStreamListener(logger, level, null));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).info(LINE);
	}

	@Test
	public void testShouldNotifyJCLLoggerWithPrefix() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		org.apache.commons.logging.Log logger = Mockito.mock(org.apache.commons.logging.Log.class);
		
		//When
		de.flapdoodle.embedmongo.io.JCLStreamListener.Level level = de.flapdoodle.embedmongo.io.JCLStreamListener.Level.INFO;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new JCLStreamListener(logger, level, PREFIX));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).info(PREFIX + LINE);
	}
	
	@Test
	public void testShouldNotifyJdkLogger() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		java.util.logging.Logger logger = Mockito.mock(java.util.logging.Logger.class);
		
		//When
		java.util.logging.Level level = java.util.logging.Level.SEVERE;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new JdkLoggingStreamListener(logger, level, null));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).log(level, LINE);
	}

	@Test
	public void testShouldNotifyJdkLoggerWithPrefix() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		java.util.logging.Logger logger = Mockito.mock(java.util.logging.Logger.class);
		
		//When
		java.util.logging.Level level = java.util.logging.Level.SEVERE;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new JdkLoggingStreamListener(logger, level, PREFIX));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).log(level, PREFIX + LINE);
	}
	
	@Test
	public void testShouldNotifyLog4jLogger() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		org.apache.log4j.Logger logger = Mockito.mock(org.apache.log4j.Logger.class);
		
		//When
		org.apache.log4j.Level level = org.apache.log4j.Level.DEBUG;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new Log4jStreamListener(logger, level, null));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).log(level, LINE);
	}

	@Test
	public void testShouldNotifyLog4jLoggerWithPrefix() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		org.apache.log4j.Logger logger = Mockito.mock(org.apache.log4j.Logger.class);
		
		//When
		org.apache.log4j.Level level = org.apache.log4j.Level.DEBUG;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new Log4jStreamListener(logger, level, PREFIX));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).log(level, PREFIX + LINE);
	}
	
	@Test
	public void testShouldNotifySlf4jLogger() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		org.slf4j.Logger logger = Mockito.mock(org.slf4j.Logger.class);
		
		//When
		de.flapdoodle.embedmongo.io.Slf4jStreamListener.Level level = de.flapdoodle.embedmongo.io.Slf4jStreamListener.Level.TRACE;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new Slf4jStreamListener(logger, level, null));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).trace("{}{}", null, LINE);
	}

	@Test
	public void testShouldNotifySlf4jLoggerWithPrefix() throws IOException {
		//Given
		when(reader.readLine()).thenReturn(LINE).thenReturn(null);
		org.slf4j.Logger logger = Mockito.mock(org.slf4j.Logger.class);
		
		//When
		de.flapdoodle.embedmongo.io.Slf4jStreamListener.Level level = de.flapdoodle.embedmongo.io.Slf4jStreamListener.Level.TRACE;
		List<IStreamListener> listeners = new ArrayList<IStreamListener>();
		listeners.add(new Slf4jStreamListener(logger, level, PREFIX));
		StreamConsumer sc = new StreamConsumer("test", reader, listeners);
		sc.run();
		
		//Then
		verify(logger).trace("{}{}", PREFIX, LINE);
	}
}
