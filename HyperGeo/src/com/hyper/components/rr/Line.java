package com.hyper.components.rr;

import java.awt.Graphics2D;

import org.joml.Vector2i;

import com.hyper.math.Vector2bd;

public class Line extends Drawable2D {
	private final Vector2bd a, b;
	
	public Line(Vector2bd a, Vector2bd b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public void draw(Graphics2D g, Canvas2D c) {
		Vector2i v1 = c.getGraphicalCoords(a), v2 = c.getGraphicalCoords(b);
		g.drawLine(v1.x, v1.y, v2.x, v2.y);
	}

}
