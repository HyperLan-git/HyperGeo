package com.hyper.io;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import org.mariuszgromada.math.mxparser.Function;

import com.hyper.components.cr.ChartHandler;
import com.hyper.components.rr.FunctionPanel;

public class EventHandler3d implements CellEditorListener {
	private ChartHandler chart;
	private FunctionPanel functions;

	public EventHandler3d(ChartHandler chart, FunctionPanel functions) {
		this.chart = chart;
		this.functions = functions;

		//Default editor
		functions.getTable().getCellEditor(0, 0).addCellEditorListener(this);
		//Color button editor
		functions.getTable().getCellEditor(0, 2).addCellEditorListener(this);
	}

	@Override
	public void editingStopped(ChangeEvent e) {
		System.out.println("The user finished editing.");
		Function[] functions = this.functions.read();
		if(functions != null)
			chart.setFunctions(functions);
	}

	@Override
	public void editingCanceled(ChangeEvent e) {
		System.out.println("The user cancelled editing.");
	}
}
