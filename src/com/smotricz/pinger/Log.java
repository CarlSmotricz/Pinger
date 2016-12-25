package com.smotricz.pinger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * A butt-simple logger providing a TableModel for display.
 */
public class Log {

	private static boolean TO_TABLE = true;
	private static boolean TO_CONSOLE = false;
	
	private static List<Log.Message> messages = new ArrayList<Log.Message>();
	public static AbstractTableModel tableModel = new LogTableModel();

	/** Log the given message, either a text string or a printf()-style format/args. */
	public static void log(String fmt, Object... args) {
		String text;
		if (args.length == 0) {
			text = fmt;
		} else {
			text = String.format(fmt, args);
		}
		if (TO_TABLE) {
			int row = messages.size();
			messages.add(new Log.Message(text));
			tableModel.fireTableRowsInserted(row, row);
		}
		if (TO_CONSOLE) {
			System.err.println(text);
		}
	}

	/** Remove all messages from the message log/table. */
	public static void clear() {
		if (!messages.isEmpty()) {
			int lastRow = messages.size() - 1;
			messages.clear();
			tableModel.fireTableRowsDeleted(0, lastRow);
		}
	}

	/** Inner class: Log message. */
	private static class Message {
		
		public final Date time;
		public final String text;
		
		private Message(Date time, String text) {
			this.time = time;
			this.text = text;
		}
		
		private Message(String text) {
			this(new Date(), text);
		}
		
	}
	
	/** Inner class: Log table model. */
	@SuppressWarnings("serial")
	private static class LogTableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			return messages.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return messages.get(rowIndex).time;
			case 1:
				return messages.get(rowIndex).text;
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Date.class;
			case 1:
				return String.class;
			default:
				return Object.class;
			}
		}

		@Override
		public void fireTableRowsInserted(int firstRow, int lastRow) {
			// TODO Auto-generated method stub
			super.fireTableRowsInserted(firstRow, lastRow);
		}
		
	}
	
}
