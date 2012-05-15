package de.flapdoodle.embedmongo.runtime;

public class ProcessShutdownWatcher extends Thread {

	private final Process process;

	private boolean shutdown = false;

	public ProcessShutdownWatcher(Process process) {
		this.process = process;
	}

	@Override
	public void run() {
		try {
			process.waitFor();
			shutdown = true;
		} catch (InterruptedException e) {
		}
	}

	public boolean isShutdown() {
		return shutdown;
	}
}