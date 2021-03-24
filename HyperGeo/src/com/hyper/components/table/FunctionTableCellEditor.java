package com.hyper.components.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JColorChooser;
import javax.swing.JTable;
import javax.swing.JTextField;

public class FunctionTableCellEditor extends DefaultCellEditor {
	private FunctionColorButton button = new FunctionColorButton();

	public FunctionTableCellEditor(JTextField textField) {
		super(textField);
	}

	@Override
	public Object getCellEditorValue() {
		return button.getColor();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		button.setColor(((FunctionTable) table).getFunctionColor(row));
		Color c = JColorChooser.showDialog(button, "Choose color", button.getColor());
		if(c != null)
			button.setColor(c);
		table.getModel().setValueAt(c, row, 2);
		FunctionTableCellEditor.this.fireEditingStopped();
		return button;
	}
	
	@Override
	public int getClickCountToStart() {
		return 100;
	}
}
