package com.hyper.components.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class FunctionTableCellRenderer extends DefaultTableCellRenderer {
	private FunctionColorButton instance = new FunctionColorButton();

	public FunctionTableCellRenderer() {
		this.setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if(value instanceof Color) {
			instance.setColor((Color) value);
			return instance;
		}
		if(value instanceof Component) {
			return (Component) value;
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
