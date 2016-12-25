package com.smotricz.pinger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The machinery for timing the pinger's activities.
 * Rather than a singleton, it's a class full of static stuff.
 */
public class Scheduler {

	public static interface DrawListener {
		
		public void draw(long currentMillis);
		
	}
	
	public static interface ClockListener {
		
		public void tick(Date time);
		
	}
	
	// Addresses are managed separately from hosts because multiple (user defined)
	// hosts can refer to the same address, and we don't want to ping redundantly.
	
	private static List<Host> activeHosts = new ArrayList<Host>();
	private static List<InetAddress> activeAddresses = new ArrayList<InetAddress>();
	private static List<DrawListener> drawListeners = new ArrayList<DrawListener>();
	private static List<ClockListener> clockListeners = new ArrayList<ClockListener>();
	
	/** 
	 * The Timer for periodic ping timing. 
	 * The interval decreases as the number of addresses increases. 
	 */
	private static Timer pingTimer = new Timer(true);
	
	/**
	 * The Timer for scheduling graphic updates on {@link ChartWidget}s.
	 * All ChartWidgets subscribe to the same clock, this one. 
	 */
	private static Timer drawTimer = new Timer(true);
	
	/**
	 * The Timer for scheduling wall-clock updates on {@link ChartWidget}s.
	 * All ChartWidgets subscribe to the same clock, this one. 
	 */
	private static Timer clockTimer = new Timer(true);
	

	/**
	 * Initialize the Scheduler.
	 * This would be a constructor if Scheduler was an object.
	 */
	public static void init() {
		Hosts.UpdateListener updater = new Hosts.UpdateListener() {
			public void hostsUpdated(Iterator<Host> iterator) {
				Scheduler.hostsUpdated(iterator);
			}
		};
		Hosts.addListener(updater);
		drawTimer.schedule(new DrawTask(), 500L, GraphPane.DRAW_INTERVAL);
		clockTimer.schedule(new ClockTask(), 1000L, ChartWidget.CLOCK_INTERVAL);
	}
	
	/**
	 * Register a subscriber to "draw" events.
	 * @param widget
	 */
	public static void addDrawListener(DrawListener listener) {
		drawListeners.add(listener);
	}
	
	/**
	 * Register a ChartWidget as a subscriber to "draw" events.
	 * @param widget
	 */
	public static void addClockListener(ClockListener listener) {
		clockListeners.add(listener);
	}
	
	/**
	 * This lets ChartPane start up a new list of listeners.
	 */
	public static void clearListeners() {
		clockListeners.clear();
		drawListeners.clear();
	}
	
	// ===== Hosts.UpdateListener method

	/**
	 * Given a change in the host list, update the list of addresses
	 * to ping, and the interval for pinging.
	 * @param iterator
	 */
	private static void hostsUpdated(Iterator<Host> iterator) {
		
		// Update list of active hosts
		activeHosts.clear();
		while (iterator.hasNext()) {
			Host h = iterator.next();
			if (h.isValid() && !h.hidden) {
				activeHosts.add(h);
			}
		}
		
		// Update list of active addresses
		activeAddresses.clear();
		for (Host h: activeHosts) {
			if (!activeAddresses.contains(h.addr)) {
				activeAddresses.add(h.addr);
			}
		}
		
		// Restart ping schedule with new address list.
		// It turns out to be easier to create a new Timer than to 
		// change its activation interval.
		pingTimer.cancel();	// the old one; this is the last we see of it.
		pingTimer = new Timer(true);	// substitute a new Timer.
		if (!activeAddresses.isEmpty()) {
			pingTimer.scheduleAtFixedRate(new PingTask(), 0L, 
					PingControl.INTERVAL / activeAddresses.size());
		}
	}

	/**
	 * Schedule a ping for the next among a number of hosts.
	 * This class keeps a counter to point at the next pingable host
	 * on a round-robin basis.
	 * It can dynamically adjust to a smaller or larger list of active
	 * addresses.
	 */
	private static class PingTask extends TimerTask {
		
		int robin = 0;
		
		public void run() {
			if (activeAddresses.isEmpty()) return;
			robin++;
			if (robin > activeAddresses.size()) robin = 1;
			PingControl.doPing(activeAddresses.get(robin - 1));
		}
	};
	
	/**
	 * Schedule a wall-clock update for all chart widgets.
	 */
	private static class ClockTask extends TimerTask {
		
		public void run() {
			Date time = new Date();
			for (ClockListener w: clockListeners) {
				if (!clockListeners.isEmpty()) {
					w.tick(time);
				}
			}
		}
		
	}
	
	/**
	 * Schedule a (possibly partial) redrawing for graphs on all chart widgets.
	 */
	private static class DrawTask extends TimerTask {
		
		public void run() {
			long currentMillis = System.currentTimeMillis();
			for (DrawListener w: drawListeners) {
				if (!drawListeners.isEmpty()) {
					w.draw(currentMillis);
				}
			}
		}
	}
	
}
