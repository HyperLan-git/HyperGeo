package com.hyper.components.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import com.hyper.components.rr.FunctionPanel;

public class FunctionTableListener implements CellEditorListener, MouseListener {
	private FunctionPanel functions;

	public FunctionTableListener(FunctionPanel functions) {
		this.functions = functions;

		//Default editor
		functions.getTable().getCellEditor(0, 0).addCellEditorListener(this);
		//Color button editor
		functions.getTable().getCellEditor(0, 2).addCellEditorListener(this);

		functions.getTable().addMouseListener(this);
	}
	@Override
	public void editingStopped(ChangeEvent e) {
		boolean isFull = true;
		for(int row = 0; row < functions.getTable().getRowCount(); row++) {
			String funcName = (String) functions.getTable().getValueAt(row, 0);
			if(funcName == null || funcName.isEmpty())
				isFull = false;
		}

		if(isFull)
			functions.getTable().addRow();

		functions.revalidate();
	}

	@Override
	public void editingCanceled(ChangeEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource().equals(functions.getTable()))
			functions.mousePressed(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

}
