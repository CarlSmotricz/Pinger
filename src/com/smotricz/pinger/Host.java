package com.smotricz.pinger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

/**
 * Internal representation of a host.
 * Consists of host name, host address, a "hidden" flag
 * and some support stuff. 
 */
public class Host {

	/** Single lookup thread pool shared by all hosts. */
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public String name = "";
	public InetAddress addr;
	public boolean hidden;
	
	private Stack<String> lookupStack = new Stack<String>();
	

	/** Constructor. */
	Host() {
	}
	
	Host(String name, InetAddress addr, boolean hidden) {
		this.name = name;
		this.addr = addr;
		this.hidden = hidden;
	}

	
	public void setName(String newName) {
		String oldName = name;
		name = newName.trim();
		if (!name.equals(oldName)) {
			doLookup();
		}
		Hosts.informListeners();
	}
	
	
	public void setAddr(InetAddress newAddr) {
		addr = newAddr;
		Hosts.informListeners();
	}
	
	
	public void setHidden(boolean newHidden) {
		hidden = newHidden;
		Hosts.informListeners();
	}
	
	
	public boolean isValid() {
		return (addr != null);
	}
	
	
	public void doLookup() {
		synchronized(this) {
			lookupStack.push(name);
		}
		threadPool.submit(new Lookup());
	}
	
	
	public String addressString() {
		if (addr == null) {
			return "?.?.?.?";
		} else {
			return addr.getHostAddress();
		}
	}
	
	
	public String hostString() {
		if (name.isEmpty()) {
			return String.format("[ %s ]", addressString());
		} else if (name.equals(addressString())) {
			return String.format("[ %s ]", addressString());
		} else {
			return String.format("%s [ %s ]", name, addressString());
		}
	}
	
	
	/**
	 * Inner class: Task to look up a host name.
	 * Tries to reduce lookup spam, and calls {@link #setAddr} when done.
	 */
	private class Lookup implements Runnable {

		@Override
		public void run() {
			String hostName;
			synchronized(Host.this) {
				if (lookupStack.isEmpty()) return;
				hostName = lookupStack.pop();
				lookupStack.clear();
			}
			InetAddress luTemp;
			try {
				luTemp = InetAddress.getByName(hostName);
			} catch (UnknownHostException uhe) {
				luTemp = null;
			}
			final InetAddress luResult = luTemp;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setAddr(luResult);
				}
			});
		}
	}
	
}
