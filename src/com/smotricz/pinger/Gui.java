package com.smotricz.pinger;

import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class Gui extends JFrame {

	public final static Gui instance = new Gui();

	private Rectangle normalBounds = new Rectangle();
	
	public Gui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Pinger");
        JTabbedPane tabbedPane = new JTabbedPane();
        getContentPane().add(tabbedPane);
        tabbedPane.add(new ChartPane(), "Charts");
        tabbedPane.add(new HostPane(), "Hosts");
        tabbedPane.add(new LogPane(), "Log");
        addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				Settings.instance.save(normalBounds);
			}
		});
        addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent arg0) {
				saveCoords();
			}
			public void componentResized(ComponentEvent arg0) {
				saveCoords();
			}
			public void componentShown(ComponentEvent arg0) {
				saveCoords();
			}
			private void saveCoords() {
				if (Gui.this.getState() == JFrame.NORMAL) {
					normalBounds = Gui.this.getBounds();
				}
			}
		});
	}
	
}
