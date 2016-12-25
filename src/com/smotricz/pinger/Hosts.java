package com.smotricz.pinger;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

public class Hosts {

	private static List<Host> hosts = new ArrayList<Host>();
	private static List<UpdateListener> listeners 
		= new ArrayList<UpdateListener>();

	/** Create a new Host and add it to the host list. */
	public static Host add() {
		Host newHost = new Host();
		hosts.add(newHost);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				informListeners();
			}
		});
		return newHost;
	}

	/** Create a new Host from parameters and add it to the host list. */
	public static Host add(String name, InetAddress addr, boolean hide) {
		final Host newHost = new Host(name, addr, hide);
		hosts.add(newHost);
		return newHost;
	}
	
	/** Remove a Host from the list. */
	public static void remove(Host h) {
		hosts.remove(h);
		informListeners();
	}

	/** Return an Iterator for the host list. */
	public static Iterator<Host> iterator() {
		return hosts.iterator();
	}
	
	public static void addListener(UpdateListener listener) {
		listeners.add(listener);
	}
	
	public static void up(Host host) {
		int pos = hosts.indexOf(host);
		hosts.set(pos, hosts.get(pos - 1));
		hosts.set(pos - 1, host);
		informListeners();
	}
	
	public static void down(Host host) {
		int pos = hosts.indexOf(host);
		hosts.set(pos, hosts.get(pos + 1));
		hosts.set(pos + 1, host);
		informListeners();
	}
	
	public static void informListeners() {
		for (UpdateListener listener: listeners) {
			listener.hostsUpdated(hosts.iterator());
		}
	}
	
	/**
	 * Interface for anyone wanting to be informed of changes.
	 * The listener gets an Iterator for the current host list.
	 */
	public interface UpdateListener {
		
		public void hostsUpdated(Iterator<Host> iterator);
		
	}
	
}
