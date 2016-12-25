package com.smotricz.pinger;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Created by carl on 17.12.2016.
 */
public class ChartPane extends JPanel implements Hosts.UpdateListener {

    private static final String EXPL = "EXPL";
    private static final String CHARTS = "CHARTS";

    private JPanel charts = new JPanel();
    private CardLayout cl = new CardLayout();
    private Box widgetBox;

    /**
     * Constructor.
     * @param model
     */
    public ChartPane() {
        setLayout(cl);
        String msg = "<html>No hosts set up (or all hidden)!<br/>Add one or more hosts on the host pane to start pinging.</html>";
        JLabel lblMsg = new JLabel(msg);
        lblMsg.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createEtchedBorder(),
        		BorderFactory.createEmptyBorder(8,  8,  8,  8)));
        JPanel explanation = new JPanel();
        GroupLayout layout = new GroupLayout(explanation);
        explanation.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(lblMsg,  GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(lblMsg,  GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        add(explanation, EXPL);

        widgetBox = new Box(BoxLayout.Y_AXIS);
        widgetBox.setBorder(BorderFactory.createEtchedBorder());
        
        layout = new GroupLayout(charts);
        charts.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(widgetBox,  GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(widgetBox,  GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        add(charts, CHARTS);

        Hosts.addListener(this);

    }

    private void updateHostList(Iterator<Host> hostIterator) {
    	boolean anyHostVisible = false;
    	
        // Remove previous chart widgets
        for (Component comp: widgetBox.getComponents()) {
            widgetBox.remove(comp);
        }
        
        // Create widgets for all valid and visible hosts
    	while (hostIterator.hasNext()) {
    		Host host = hostIterator.next();
    		if (host.isValid() && !host.hidden) {
    			anyHostVisible = true;
                widgetBox.add(new ChartWidget(host));
    		}
    	}
    	
    	// Display charts or an explanation.
        if (anyHostVisible) {
            cl.show(this, CHARTS);
        } else {
            cl.show(this, EXPL);
        }
    }

    // ===== Hosts.UpdateListener method.
    
	@Override
	public void hostsUpdated(Iterator<Host> iterator) {
		Scheduler.clearListeners();
        updateHostList(iterator);
        revalidate();
	}

}
