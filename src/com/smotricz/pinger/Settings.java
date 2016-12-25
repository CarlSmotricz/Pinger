package com.smotricz.pinger;

import java.awt.Rectangle;
import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

public class Settings {

	private static final String INI_FILE_NAME = "pinger.ini";
	
	public static Settings instance = new Settings();
	
	private File iniFile;
	private Properties props;

	/**
	 * Constructor.
	 */
	public Settings() {
		props = new Properties();
		File iniDir = checkDirectory(new File(System.getProperty("user.home")));
		if (iniDir == null) {
			iniDir = checkDirectory(new File(System.getProperty("user.dir")));
		}
		if (iniDir == null) {
			Log.log("+++ Can't store settings.\n");
		}
		iniFile = new File(iniDir, INI_FILE_NAME);
	}

	/**
	 * Check user.home and user.dir for reading/writing settings.
	 * @param iniDir
	 * @return
	 */
	private File checkDirectory(File iniDir) {
		if (!iniDir.exists()) {
			Log.log("+++ Directory %s doesn't exist\n", iniDir.toString());
			return null;
		} else if (!iniDir.canRead()) {
			Log.log("+++ Directory %s can't be read\n", iniDir.toString());
			return null;
		} else if (!iniDir.canWrite()) {
			Log.log("+++ Directory %s can't be written\n", iniDir.toString());
			return null;
		}
		return iniDir;
	}
	
	public void load(Window gui) {
		if (iniFile != null) {
			try {
				props.load(new FileInputStream(iniFile));
				loadBounds(gui);
				loadHosts();
			} catch (FileNotFoundException fnfe) {
				Log.log("+++ File not found: %s\n", iniFile.toString());
			} catch (IOException ioe) {
				Log.log("+++ Couldn't read file: %s because: %s\n", iniFile.toString(), ioe.getMessage());
			}
		}
	}
	
	public void save(Rectangle normalBounds) {
		if (iniFile != null) {
			putBounds(normalBounds);
			putHosts();
			try {
				props.store(new FileOutputStream(iniFile), (new Date()).toString());
			} catch (IOException ioe) {
				Log.log("+++ Could not store properties because %s\n", ioe.getMessage());
			}
		}
	}
	
	private void putBounds(Rectangle bounds) {
		props.put("top", String.valueOf(bounds.y));
		props.put("left", String.valueOf(bounds.x));
		props.put("height", String.valueOf(bounds.height));
		props.put("width", String.valueOf(bounds.width));
	}
	
	private void loadBounds(Window gui) {
		if (props == null) return;
		String top = props.getProperty("top");
		String left = props.getProperty("left");
		String height = props.getProperty("height");
		String width = props.getProperty("width");
		if (top == null || left == null || height == null || width == null) {
			Log.log("+++ top/left/height/width not found in settings.\n");
			return;
		}
		try {
			int iTop = Integer.parseInt(top);
			int iLeft = Integer.parseInt(left);
			int iHeight = Integer.parseInt(height);
			int iWidth = Integer.parseInt(width);
			gui.setBounds(iLeft, iTop, iWidth, iHeight);
		} catch (NumberFormatException nfe) {
			Log.log("+++ top/left/height/width are not valid integers in settings.\n");
		}
	}
	
	private void putHosts() {
		int nHosts = 0;
		Iterator<Host> hostIterator = Hosts.iterator();
		while (hostIterator.hasNext()) {
			Host h = hostIterator.next();
			nHosts++;
			String hostN = String.format("host%d", nHosts);
			props.put(hostN + ".name", h.name);
			props.put(hostN + ".addr", h.addressString());
			props.put(hostN + ".hide", h.hidden ? "true" : "false");
		}
		props.put("hosts.count", String.valueOf(nHosts));
	}
	
	private void loadHosts() {
		if (props == null) return;
		String hostCount = props.getProperty("hosts.count");
		if (hostCount == null) {
			Log.log("+++ hosts.count not found in settings.\n");
			return;
		}
		int iHostCount;
		try {
			iHostCount = Integer.parseInt(hostCount);
		} catch (NumberFormatException nfe) {
			Log.log("+++ hosts.count not a valid integer in settings.\n");
			return;
		}
		for (int j=0; j<iHostCount; j++) {
			int n = j + 1;
			// Name
			String keyName = String.format("host%d.name", n);
			String hostName = props.getProperty(keyName);
			if (hostName == null) {
				Log.log("+++ host%d.name missing in settings.\n", n);
				return;
			}
			// Addr
			String keyAddr = String.format("host%d.addr", n);
			String hostAddr = props.getProperty(keyAddr);
			if (hostAddr == null) {
				Log.log("+++ host%d.addr missing in settings.\n", n);
				return;
			}
			InetAddress aHostAddr;
			try {
				aHostAddr = InetAddress.getByName(hostAddr);
			} catch (Exception e) {
				Log.log("+++ Bad host%d.addr: %s.\n", n, hostAddr);
				return;
			}
			// Hide
			String keyHide = String.format("host%d.hide", n);
			String hostHide = props.getProperty(keyHide);
			if (hostHide == null) {
				Log.log("+++ host%d.hide missing in settings.\n", n);
				return;
			}
			boolean bHostHide;
			if ("true".equals(hostHide)) {
				bHostHide = true;
			} else if ("false".equals(hostHide)) {
				bHostHide = false;
			} else {
				Log.log("+++ bad host%d.hide value: %s.\n", n, hostHide);
				return;
			}
			try {
				Hosts.add(hostName, aHostAddr, bHostHide);
			} catch (Exception e) {
				Log.log("+++ can't initialize host %d from settings.\n", n);
				return;
			}
		}
		Hosts.informListeners();
	}
	
}
