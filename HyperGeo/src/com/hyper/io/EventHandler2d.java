package com.hyper.io;

import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JSplitPane;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import org.mariuszgromada.math.mxparser.Function;

import com.hyper.components.rr.Canvas2D;
import com.hyper.components.rr.FunctionPanel;
import com.hyper.components.rr.FunctionUpdateListener;

public class EventHandler2d implements MouseListener, MouseWheelListener, MouseMotionListener, HierarchyBoundsListener, CellEditorListener, PropertyChangeListener {
	private Canvas2D canvas;
	private FunctionPanel functions;
	private JSplitPane content;
	
	private List<FunctionUpdateListener> listeners = new ArrayList<>();

	public EventHandler2d(Canvas2D canvas, FunctionPanel functions, JSplitPane content) {
		this.canvas = canvas;
		this.functions = functions;
		this.content = content;

		canvas.addMouseListener(this);
		canvas.addMouseWheelListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addHierarchyBoundsListener(this);

		//Default editor
		functions.getTable().getCellEditor(0, 0).addCellEditorListener(this);
		//Color button editor
		functions.getTable().getCellEditor(0, 2).addCellEditorListener(this);

		content.addPropertyChangeListener(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getSource().equals(canvas))
			canvas.mousePressed(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getSource().equals(canvas))
			canvas.mouseReleased(e.getX(), e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		canvas.mouseDragged(e.getX(), e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		canvas.zoom((Math.exp(e.getPreciseWheelRotation())-1.0)/5.0);
	}
	
	@Override
	public void ancestorMoved(HierarchyEvent e) {}

	@Override
	public void ancestorResized(HierarchyEvent e) {
		canvas.update();
	}

	@Override
	public void editingStopped(ChangeEvent e) {
		System.out.println("The user finished editing.");
		Function[] f = functions.read();
		canvas.setFunctions(f);
		for(FunctionUpdateListener listener : listeners) listener.onFunctionUpdate(f);
	}

	@Override
	public void editingCanceled(ChangeEvent e) {
		System.out.println("The user canceled editing.");
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if((e.getSource() == content) && (e.getPropertyName() == JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY))
			canvas.update();
	}

	public void registerFunctionListener(FunctionUpdateListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeFunctionListener(FunctionUpdateListener listener) {
		this.listeners.remove(listener);
	}
}
