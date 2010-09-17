package com.googlecode.reunion.jreunion.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

class PerformanceEvent {
	private Date moment;

	PerformanceEvent() {
		moment = new Date();
	}

	Date getTime() {
		return moment;
	}

}

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_PerformanceStats extends S_ClassModule {

	private long totalServerloops = 0;
	private List<Tick> ticksList = new Vector<Tick>();
	private int frames = 0;
	private PerformanceEvent startServer;
	private S_Timer loopTimer = new S_Timer();
	public S_Timer serverTimer = new S_Timer();
	long bytesSent = 0;
	long bytesReceived = 0;
	long packetsSent = 0;
	long packetsReceived = 0;
	private static S_PerformanceStats _instance = null;

	static void createPerformanceStats(S_Module parent) {
		if (_instance == null) {
			_instance = new S_PerformanceStats(parent);
		}
	}

	public static S_PerformanceStats getInstance() {
		return _instance;
	}

	private S_PerformanceStats(S_Module parent) {
		super(parent);

	}

	public void dumpPerformance() {
		dumpPerformance("performance.txt");
	}

	public void dumpPerformance(String filename) {
		try {
			PerformanceEvent now = new PerformanceEvent();
			Tick highestTick = new Tick(0);
			Tick lowestTick = new Tick(Integer.MAX_VALUE);
			double averageTicks = 0;
			double timeSpan;
			Iterator<Tick> iter = ticksList.iterator();
			while (iter.hasNext()) {

				Tick tick = iter.next();
				if (tick.getTicks() > highestTick.getTicks()) {
					highestTick = tick;
				}
				if (tick.getTicks() < lowestTick.getTicks()) {
					lowestTick = tick;
				}

			}

			timeSpan = serverTimer.getTimeElapsedSeconds();// (now.getTime().getTime()-startServer.getTime().getTime())/1000;
			averageTicks = totalServerloops / timeSpan;
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			out.write("---Performance Stats---\n" + "Start:              "
					+ startServer.getTime().toString() + "\n"
					+ "Stop:               " + now.getTime().toString() + "\n"
					+ "Uptime:             " + timeSpan + "\n"
					+ "---Ticks---\n" + "TotalTicks:         "
					+ totalServerloops + "\n" + "AverageTicks:       "
					+ averageTicks + "\n" + "HighestTick:        "
					+ highestTick.getTicks() + " @ " + highestTick.getTime()
					+ "\n" + "LowestTick:         " + lowestTick.getTicks()
					+ " @ " + lowestTick.getTime() + "\n" + "---Network---\n"
					+ "Received Packets:   " + packetsReceived + "\n"
					+ "Received Bytes:     " + bytesReceived + "\n"
					+ "Sent Packets:       " + packetsSent + "\n"
					+ "Sent Bytes:         " + bytesSent + "\n"

			);
			out.close();
		} catch (IOException e) {
		}
	}

	public void receivedPacket(int size) {
		packetsReceived++;
		bytesReceived += size;
	}

	public void sentPacket(int size) {
		packetsSent++;
		bytesSent += size;

	}

	@Override
	public void start() {
		serverTimer.Start();

		startServer = new PerformanceEvent();

	}

	@Override
	public void stop() {
		serverTimer.Stop();

	}

	@Override
	public void Work() {

		totalServerloops++;
		frames++;
		if (!loopTimer.running) {
			loopTimer.Start();
			return;
		}
		if (loopTimer.getTimeElapsedSeconds() >= 1) {
			ticksList.add(new Tick(frames));
			// System.out.println(frames);
			loopTimer.Reset();
			frames = 0;
		}
	}

}

class Tick extends PerformanceEvent {
	private int ticks = -1;

	Tick(int ticks) {
		super();
		this.ticks = ticks;

	}

	public int getTicks() {
		return ticks;
	}

}
