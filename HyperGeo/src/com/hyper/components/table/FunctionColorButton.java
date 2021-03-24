package com.hyper.components.table;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

public class FunctionColorButton extends JButton {
	public static final String COLOR_CHANGED_PROPERTY = "color_changed";
	private Color currentColor = Color.RED;
	
	public FunctionColorButton() {
		super("        Change color");
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		int startX = this.getWidth()/2-50,
				startY = this.getHeight()/2-10;
		GradientPaint gradient = new GradientPaint(0, startY-3, Color.WHITE, 0, startY+20, currentColor);
		
		g2.setPaint(gradient);
		g2.fillRect(startX, startY, 20, 20);
	}

	public Color getColor() {
		return currentColor;
	}

	public void setColor(Color c) {
		Color oldValue = currentColor;
		this.currentColor = new Color(c.getRGB());
		firePropertyChange(COLOR_CHANGED_PROPERTY, oldValue, c);
	}
}
