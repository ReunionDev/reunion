package com.googlecode.reunion.jreunion.server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Timer {

	long startTime = -1;
	long endTime = -1;
	boolean running = false;

	public S_Timer() {
		super();

	}

	public long getTimeElapsed() {

		if (startTime == -1) {
			return -1;
		}
		if (running) {
			return System.nanoTime() - startTime;
		}

		return endTime - startTime;

	}

	public double getTimeElapsedSeconds() {
		double elapsed = getTimeElapsed();

		return elapsed / 1000000000;

	}

	public boolean isRunning() {
		return running;
	}

	public void Reset() {
		startTime = System.nanoTime();
		endTime = System.nanoTime();
	}

	public void Start() {
		running = true;
		startTime = System.nanoTime();

	}

	public void Stop() {
		running = false;
		endTime = System.nanoTime();
	}

}
