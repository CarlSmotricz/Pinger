package com.smotricz.pinger;

import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.border.BevelBorder;

/**
 * The JComponent representing one chart (for one host).
 */
public class ChartWidget extends JPanel implements Scheduler.ClockListener {

	// ========== STATIC

	public static final long CLOCK_INTERVAL = 1000;	// Clock updates every 5 s.
	
	/**
	 * Internal class: One of the time labels on the chart.
	 */
    public static class SecondsLabel {
        public final int seconds;
        public final String label;

        public SecondsLabel(int seconds, String label) {
            this.seconds = seconds;
            this.label = " " + label + " ";
        }
    }

    /**
     * A list of suggested label points in time and texts.
     * The subset of these labels displayed depends on the available width.
     */
    private final static SecondsLabel[] TIME_INTERVALS = new SecondsLabel[] {
            new SecondsLabel(0, "0"),
            new SecondsLabel(5, "5s"),
            new SecondsLabel(10, "10s"),
            new SecondsLabel(30, "30s"),
            new SecondsLabel(60, "1m"),
            new SecondsLabel(120, "2m"),
            new SecondsLabel(300, "5m"),
            new SecondsLabel(600, "10m"),
            new SecondsLabel(900, "15m"),
            new SecondsLabel(1800, "30m"),
            new SecondsLabel(3600, "1h"),
            new SecondsLabel(7200, "2h"),
            new SecondsLabel(10800, "3h"),
            new SecondsLabel(14400, "4h"),
            new SecondsLabel(18000, "5h"),
            new SecondsLabel(21600, "6h"),
            new SecondsLabel(28800, "8h"),
            new SecondsLabel(32400, "9h"),
            new SecondsLabel(43200, "12h"),
            new SecondsLabel(64800, "18h"),
            new SecondsLabel(72000, "20h"),
            new SecondsLabel(86400, "24h") };
    /** 
     * The "24h" label.
     * The chart will always display at least the "0" label
     * and the "24h" label.
     */
    public static SecondsLabel TIME_N = TIME_INTERVALS[TIME_INTERVALS.length - 1];
    
	/**
	 * Internal class: One of the duration labels on the chart.
	 */
    private static class DurationLabel {
        public final int millis;
        public final String label;

        public DurationLabel(int millis, String label) {
            this.millis = millis;
            this.label = label;
        }
    }

    /**
     * A list of suggested duration labels in time and texts.
     * The subset of these labels displayed depends on the available width.
     */
    private final static DurationLabel[] DURATION_LABELS = new DurationLabel[] {
            new DurationLabel(10, "10"),
            new DurationLabel(100, "100"),
            new DurationLabel(1000, "1s"),
            new DurationLabel(5000, "5s")};
            
    private final static DateFormat CLOCK_FMT = 
    		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /** Distance between components and from edges */
    private final static int GAP = 5;
    /** Length of a tick mark */
    private final static int TICK_SIZE = 5;
    /** Distance between a tick and a label */
    private final static int TICK_GAP = 2;
    
    // ========== INSTANCE
    
	public final Host host;
	
	private final FontMetrics fm;
	private final JLabel lblTime;
	private final GraphPane graphBar;
	
	private int graphLeft;
	private int graphTop;
	private int graphRight;
	private int graphBottom;
	private int yt0, yt1, yt2;	// Heights for ticks and labels

	/**
	 * Constructor
	 * @param host
	 */
	public ChartWidget(Host host) {
		super(null);
		this.host = host;
		this.fm = getFontMetrics(getFont());
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		// Assemble all components
		JLabel lblHost = new JLabel(host.hostString());
		lblTime = new JLabel(CLOCK_FMT.format(new Date()));
		graphBar = new GraphPane(this.host.addr);

		int leftMargin = fm.stringWidth(TIME_N.label) / 2;
		int rightMargin = TICK_SIZE + TICK_GAP + fm.stringWidth("100");
		int bottomMargin = TICK_SIZE + TICK_GAP + fm.getHeight();
		
		layout.setHorizontalGroup(
				layout.createParallelGroup()
					.addGroup(layout.createSequentialGroup()
							.addComponent(lblHost)
							.addGap(GAP, GAP, Integer.MAX_VALUE)
							.addComponent(lblTime))
					.addGroup(layout.createSequentialGroup()
							.addGap(leftMargin)
							.addComponent(graphBar)
							.addGap(rightMargin)));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
							.addComponent(lblHost)
							.addComponent(lblTime))
					.addGap(GAP)
					.addComponent(graphBar)
					.addGap(bottomMargin));
		
		layout.setAutoCreateContainerGaps(true);
		layout.setLayoutStyle(new ChartLayoutStyle());
		
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				onResize();
			}

		});
		
		Scheduler.addClockListener(this);
	}

	/**
	 * Called when the component is resized.
	 * Chances are, the width has changed, and this invalidates the
	 * summary, which is set up for a specific size of graphBar.
	 */
	private void onResize() {
		Rectangle bounds = graphBar.getBounds();
		graphLeft = bounds.x;
		graphRight = bounds.x + bounds.width;
		graphTop = bounds.y;
		graphBottom = bounds.y + bounds.height;
        yt0 = graphBottom; // Top Y of tick marks
        yt1 = yt0 + TICK_SIZE; // Bottom Y of tick marks
        yt2 = yt1 + fm.getAscent(); // Baseline Y of labels
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        g.setColor(getForeground());
		// Paint the X axis
        // Draw "24h" tick and label
        int l = graphLeft;
        g.drawLine(l, yt0, l, yt1);
        int w = fm.stringWidth(TIME_N.label);
        l -= w / 2;
        g.drawString(TIME_N.label, l, yt2);
        l += w;
        // Draw "0" tick and label
        int r = graphRight;
        g.drawLine(r, yt0, r, yt1);
        w = fm.stringWidth(TIME_INTERVALS[0].label);
        r -= w / 2; 
        g.drawString(TIME_INTERVALS[0].label, r, yt2);
        // Draw as many ticks and labels as will fit between l and r.
        for (int n=1; n<TIME_INTERVALS.length-1; n++) {
            SecondsLabel sl = TIME_INTERVALS[n];
            int x = graphLeft + graphBar.time2X(1000L * sl.seconds);
            w = fm.stringWidth(sl.label);
            if (x + w / 2 > r) continue;
            if (x - w / 2 < l) continue;
            g.drawLine(x, yt0, x, yt1);
            g.drawString(sl.label, x - w/2, yt2);
            r = x - w / 2;
        }
		// Paint the Y axis
        for (DurationLabel dl: DURATION_LABELS) {
        	int y = graphTop + graphBar.duration2Y(dl.millis);
        	r = graphRight; 
        	g.drawLine(r, y, r + TICK_SIZE, y);
        	y += fm.getAscent() / 2;
        	g.drawString(dl.label, r + TICK_SIZE + TICK_GAP, y);
        }
	}


	/**
	 * Inner class: Establish a container gap of ChartWidget.GAP .
	 */
	private class ChartLayoutStyle extends LayoutStyle {

		@Override
		public int getContainerGap(JComponent arg0, int arg1, Container arg2) {
			return GAP;
		}

		@Override
		public int getPreferredGap(JComponent component1, JComponent component2, ComponentPlacement type, int position,
				Container parent) {
			return LayoutStyle.getInstance().getPreferredGap(component1, component2, type, position, parent);
		}
		
	}
	
	
	// ========== ClockListener method

	@Override
	public void tick(Date time) {
		lblTime.setText(CLOCK_FMT.format(time));
	}

}