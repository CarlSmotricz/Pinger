package com.smotricz.pinger;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * The pane where logger messages go.
 * Features a scrolling table of messages and a "Clear" button.
 */
@SuppressWarnings("serial")
public class HostPane extends JPanel implements Hosts.UpdateListener {

	public final static HostPane instance = new HostPane();
	
	private Box widgetBox;
	private boolean added = false;
	
	/** Constructor. */
	public HostPane() {
		
		// Set up the host widget container pane.
		widgetBox = new Box(BoxLayout.Y_AXIS);
		widgetBox.setBorder(BorderFactory.createEtchedBorder());
		
		// Set up the "add" button.
		JButton btnAdd = new JButton(new AbstractAction("Add host") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Hosts.add();
				added = true;
			}
		});
		
		// Lay everything out.
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(widgetBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnAdd));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(widgetBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(btnAdd));
		
		// Bind to the host list.
		Hosts.addListener(this);
	}
	

	// ===== Hosts.UpdateListener method
	
	@Override
	public void hostsUpdated(Iterator<Host> iterator) {
		for (Component comp: widgetBox.getComponents()) {
			widgetBox.remove(comp);
		}
		HostWidget firstWidget = null;
		HostWidget lastWidget = null;
		while (iterator.hasNext()) {
			Host h = iterator.next();
			HostWidget hw = new HostWidget(h, this);
			lastWidget = hw;
			if (firstWidget == null) firstWidget = hw;
			widgetBox.add(hw);
		}
		if (firstWidget != null) {
			firstWidget.markFirst();
			lastWidget.markLast();
			if (added) {
				lastWidget.focusOnName();
				added = false;
			}
		}
		widgetBox.add(Box.createGlue());
		widgetBox.revalidate();
	}
	
}
