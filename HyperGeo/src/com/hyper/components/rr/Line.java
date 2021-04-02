package com.hyper.components.rr;

import java.awt.Graphics2D;

import org.joml.Vector2i;

import com.hyper.math.Vector2bd;

public class Line extends Drawable2D {
	private final Vector2bd start, finish;
	
	public Line(Vector2bd start, Vector2bd finish) {
		this.start = start;
		this.finish = finish;
	}

	@Override
	public void draw(Graphics2D g, Canvas2D c) {
		Vector2i v1 = c.getGraphicalCoords(start), v2 = c.getGraphicalCoords(finish);
		g.drawLine(v1.x, v1.y, v2.x, v2.y);
	}

}
