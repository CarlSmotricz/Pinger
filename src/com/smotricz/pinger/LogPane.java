package com.smotricz.pinger;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * The pane where logger messages go.
 * Features a scrolling table of messages and a "Clear" button.
 */
@SuppressWarnings("serial")
public class LogPane extends JPanel {

	private final static SimpleDateFormat ISO_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public final static LogPane instance = new LogPane();
	
	/** Constructor. */
	public LogPane() {
		
		// Set up the log message table.
		JTable table = new JTable(Log.tableModel);
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new IsoDateRenderer());
		int dateColumnWidth = table.getFontMetrics(table.getFont()).stringWidth(ISO_SDF.format(new Date()) + "  ");
		tcm.getColumn(0).setPreferredWidth(dateColumnWidth);
		tcm.getColumn(0).setMinWidth(dateColumnWidth);
		tcm.getColumn(0).setMaxWidth(dateColumnWidth);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setTableHeader(null);
		JScrollPane scroller = new JScrollPane(table);
		
		// Set up the "clear" button.
		JButton btnClear = new JButton(new AbstractAction("Clear") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Log.clear();
			}
		});
		
		// Lay everything out.
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(scroller)
				.addComponent(btnClear));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(scroller)
				.addComponent(btnClear));
	}

	/**
	 * Renderer that renders a timestamp in ISO format.
	 */
	private class IsoDateRenderer extends DefaultTableCellRenderer {

		@Override
		public void setValue(Object value) {
			if (value == null) {
				setText("");
			} else {
				setText(ISO_SDF.format(value) + " ");
			}
		}
	}
	
}
