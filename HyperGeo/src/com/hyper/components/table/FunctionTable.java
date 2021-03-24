package com.hyper.components.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

public class FunctionTable extends JTable {
	public static final Dimension MINIMUM_SIZE = new Dimension(200, 100);

	public FunctionTable() {
		this.setModel(new FunctionTableModel());
		this.getColumn(FunctionTableModel.COLUMN_3_IDENTIFIER).setCellRenderer(new FunctionTableCellRenderer());
		this.getColumn(FunctionTableModel.COLUMN_3_IDENTIFIER).setCellEditor(new FunctionTableCellEditor(new JTextField()));

		this.setRowHeight(50);
		this.setFont(new Font("Serif", Font.BOLD, 20));

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setMinimumSize(MINIMUM_SIZE);
		this.setColumnMinWidths(20, 40, 140);
	}

	public void addRow() {
		getTableModel().addRow();
	}

	public FunctionTableModel getTableModel() {
		TableModel model = super.getModel();
		if(model instanceof FunctionTableModel)
			return (FunctionTableModel) model;
		else return null;
	}

	public Color getFunctionColor(int function) {
		return getTableModel().getFunctionColor(function);
	}

	private void setColumnMinWidths(int... widths) {
		for (int i = 0; i < widths.length; i++) {
			if (i < columnModel.getColumnCount()) {
				columnModel.getColumn(i).setMinWidth(widths[i]);
			}
			else break;
		}
	}

	public void mousePressed(int x, int y) {
		int col = this.getColumn(FunctionTableModel.COLUMN_3_IDENTIFIER).getModelIndex();
		for(int i = 0; i < this.getRowCount(); i++) {
			if(this.getCellRect(i, col, false).contains(x, y))
				this.editCellAt(i, col);
		}
	}
}
