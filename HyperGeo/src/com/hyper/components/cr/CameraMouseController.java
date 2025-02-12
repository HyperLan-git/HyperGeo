package com.hyper.components.cr;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.camera.AbstractCameraController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.View;


public class CameraMouseController extends AbstractCameraController implements MouseListener, MouseMotionListener, MouseWheelListener {

	public CameraMouseController(Chart chart) {
		this.updateViewDefault = true;
		register(chart);
		addThread(new CameraThreadController(chart));
	}

	@Override
	public void register(Chart chart) {
		super.register(chart);
		chart.getCanvas().addMouseController(this);
	}

	@Override
	public void dispose() {
		for(Chart c: targets)
			c.getCanvas().removeMouseController(this);
		super.dispose();
	}

	/** Handles toggle between mouse rotation/auto rotation: double-click starts the animated
	 * rotation, while simple click stops it.*/
	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("press !");
		if(handleSlaveThread(e))
			return;

		prevMouse.x  = e.getX();
		prevMouse.y  = e.getY();
	}

	public boolean handleSlaveThread(MouseEvent e) {
		if(isDoubleClick(e)){
			if(threadController!=null){
				threadController.start();
				return true;
			}
		}
		if(threadController!=null)
			threadController.stop();
		return false;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		System.out.println("drag !");
		Coord2d mouse = new Coord2d(e.getX(), e.getY());
		// Rotate
		if(isLeftDown(e)){
			Coord2d move  = mouse.sub(prevMouse).div(10);
			rotate( move );
			for(Chart c: targets) {
				c.updateProjectionsAndRender();
			}
		}
		// Shift
		else if(isRightDown(e)){
			Coord2d move  = mouse.sub(prevMouse).mul(-1.f);
			for(Chart c : targets) {
				View v = c.getView();
				float width = v.getScale().getMax()-v.getScale().getMin();
				BoundingBox3d box = v.getBounds().clone().shift(new Coord3d(move.x/5000*width, -move.y/5000*width, 0));
				v.lookToBox(box);
				c.updateProjectionsAndRender();
			}
		}

		prevMouse = mouse;
	}


	public static boolean isLeftDown(MouseEvent e) {
		return (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0;
	}

	public static boolean isRightDown(MouseEvent e) {
		return (e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0; 
	}

	public static boolean isDoubleClick(MouseEvent e) {
		return (e.getClickCount() > 1);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		stopThreadController();

		float factor = 1.f + Math.clamp((float)e.getPreciseWheelRotation()/100.f, -.99f, 1.f);

		for(Chart c: targets) {
			c.getView().zoomX(factor);
			c.getView().zoomY(factor);
			c.render();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}  
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
}
