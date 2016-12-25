package com.smotricz.pinger;

import javax.swing.SwingUtilities;

public class Main {

	public static void main(String[] args) {
		final Gui gui = new Gui();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initialize(gui);
			}
		});
	}
	
	private static void initialize(Gui gui) {
		Log.log("*** Pinger starting. ***");
		Log.log("Credit [1]: Sal Ingrill's  icmp4j library (http://www.icmp4j.org/d/index.html)");
		Log.log("Credit [2]: jdotsoft.com's JarClassLoader (http://www.jdotsoft.com/JarClassLoader.php)");
		gui.pack();
		gui.setVisible(true);
		Scheduler.init();
		Settings.instance.load(gui);
	}

}
