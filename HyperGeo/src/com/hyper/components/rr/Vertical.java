package com.hyper.components.rr;

import java.awt.Graphics2D;
import java.math.BigDecimal;

import org.joml.Vector2i;

import com.hyper.math.Vector2bd;

public class Vertical extends Drawable2D {
	private final BigDecimal a;

	public Vertical(BigDecimal x) {
		this.a = x;
	}

	@Override
	public void draw(Graphics2D g, Canvas2D c) {
		BigDecimal[] bounds = c.getGraphicalBounds();
		Vector2i v1 = c.getGraphicalCoords(new Vector2bd(a, bounds[1])), v2 = c.getGraphicalCoords(new Vector2bd(a, bounds[3]));
		g.drawLine(v1.x, v1.y, v2.x, v2.y);
	}
}
