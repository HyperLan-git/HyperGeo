package com.hyper.components.table;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class FunctionTableModel extends AbstractTableModel {
	public static final String COLUMN_1_IDENTIFIER = "Function name",
			COLUMN_2_IDENTIFIER = "Function definition",
			COLUMN_3_IDENTIFIER = "Function draw color";

	public static final Color[] DEFAULT = {Color.RED, Color.BLUE, Color.DARK_GRAY};

	private ArrayList<Object[]> data = new ArrayList<>();

	public FunctionTableModel() {
		addRow();
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0:
			return COLUMN_1_IDENTIFIER;
		case 1:
			return COLUMN_2_IDENTIFIER;
		case 2:
			return COLUMN_3_IDENTIFIER;
		default:
			return "";
		}
	}

	public Color getFunctionColor(int function) {
		if(function < 0 || function >= getRowCount()) return Color.RED;
		return (Color) data.get(function)[2];
	}

	void addRow() {
		if(data.size() < DEFAULT.length)
			data.add(new Object[] {new String(), new String(), DEFAULT[data.size()]});
		else
			data.add(new Object[] {new String(), new String(), new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255))});
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return data.get(0)[columnIndex].getClass();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return data.get(row)[column];
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data.get(rowIndex)[columnIndex] = aValue;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}
}