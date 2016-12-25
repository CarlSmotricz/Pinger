package com.smotricz.pinger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GraphPane extends JPanel implements Scheduler.DrawListener {

	// ========== STATIC

	/** How often to redraw (in ms). */
	public static final long DRAW_INTERVAL = 200;	// Redraw 5x per second.
	
    /** The longest allowed event age (e.g. 24h) in milliseconds as a double. */
    private static double MAX_MILLISECONDS_DOUBLE = (1000.0 * ChartWidget.TIME_N.seconds);
    /** The longest event duration (e.g. 5s) in milliseconds as a double. */
    private static double TIMEOUT_MILLIS_DOUBLE = (double) PingControl.TIMEOUT;

    // ========== INSTANCE
    
    private InetAddress addr;
    
    private int graphHeight;
	private int graphWidth;
	private double graphWidthDouble;
	private double graphHeightDouble;
	
	private int[] graphSummary = new int[0];
	private Random random = new Random();
	

	/**
	 * Constructor.
	 */
	public GraphPane(InetAddress addr) {
		super(null);
		this.addr = addr;
		setBackground(Color.white);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onResize();
			}
		});
		Scheduler.addDrawListener(this);
	}
	
    /**
     * Map a timestamp age to a pixel X coordinate on this component.
     * @param dt
     * @return
     */
    public int time2X(long dt) {
        // Calculate the fraction dt / (24 hours)
        double fraction = dt / MAX_MILLISECONDS_DOUBLE;
        // Calculate its 4th root
        fraction = Math.exp(Math.log(fraction) / 4.0);
        // Calculate the X position on this component
        double x = (1.0 - fraction) * graphWidthDouble;
        return (int) x;
    }
    
    /**
     * Map a duration to a pixel Y coordinate on the graph component.
     * @param duration
     * @return
     */
    public int duration2Y(long duration) {
        // Calculate the fraction duration / (5 seconds)
        double fraction = duration / TIMEOUT_MILLIS_DOUBLE;
        // Calculate its 4th root
        fraction = Math.exp(Math.log(fraction) / 4.0);
        // Calculate the Y position on this component
        double y = (1.0 - fraction) * graphHeightDouble;
        return (int) y;
    }


	private FontMetrics fm() {
		return getFontMetrics(getFont());
	}
	
	private void onResize() {
		graphWidth = getSize().width;
		graphWidthDouble = graphWidth;
		graphHeight = getSize().height;
		graphHeightDouble = graphHeight;
		graphSummary = new int[graphWidth];
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(
				20 * fm().charWidth('0'),
				3 * fm().getHeight());
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(
				30 * fm().charWidth('0'),
				4 * fm().getHeight());
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(
				super.getMaximumSize().width,
				5 * fm().getHeight());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Color oldColor = null;
		Color newColor;
		for (int j=0; j<graphSummary.length; j++) {
			int dur = graphSummary[j];
			// Determine draw color
			if (dur <= 100) {
				newColor = Color.BLUE;
			} else if (dur <= 1000) {
				newColor = Color.MAGENTA.darker();
			} else {
				newColor = Color.RED;
			}
			if (oldColor != newColor) {
				g.setColor(newColor);
				oldColor = newColor;
			}
			// Calculate bar height and draw it
			int y = duration2Y(graphSummary[j]);
			g.drawLine(j, y, j, graphHeight);
		}
	}

	// ========== DrawListener method
	
	/**
	 * Respond to nudges from the Scheduler.
	 * Update the graphSummary and schedule a repaint.
	 * <br/>
	 * Optimization strategy:
	 * Bar height is calculated only for a random time interval. Bars for earlier
	 * times are copied from previous results.
	 */
	@Override
	public void draw(long now) {
		int pixels = graphSummary.length;
		int[] newSummary = new int[pixels];
		Iterator<PingResponse> responseIterator = PingControl.responseIterator(addr);
		if (responseIterator == null) {
			return;
		}
		// 10^8 is a bit more than 24h in milliseconds.
		// Thanks to exponential function, 50% of the time the interval
		// will be less than 10 seconds.
		long tMax = (long) Math.pow(10.0, 8 * random.nextDouble());
		int xMax = time2X(tMax);
		if (xMax < 0) xMax = 0;
		System.arraycopy(graphSummary, 0, newSummary, 0, xMax);
		while (responseIterator.hasNext()) {
			PingResponse resp = responseIterator.next();
			long dt = now - resp.timestamp;
			int x = time2X(dt);
			if (x < 0 || x >= pixels) continue;
			if (x < xMax) break;
			int dur = resp.duration;
			if (dur > newSummary[x]) newSummary[x] = dur;
		}
		graphSummary = newSummary;
		// Schedule a repaint for the graph component
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				repaint();
			}
		});
	}

}
