package com.hyper.components.rr;

import java.awt.Graphics2D;
import java.math.BigDecimal;

import org.joml.Vector2d;
import org.joml.Vector2i;

import com.hyper.math.Vector2bd;

public class EndlessLine extends Drawable2D {
	private final BigDecimal a, b;
	
	public EndlessLine(BigDecimal a, BigDecimal b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public void draw(Graphics2D g, Canvas2D c) {
		Vector2i v1 = c.getGraphicalCoords(new Vector2bd(BigDecimal.ZERO, b)), v2 = c.getGraphicalCoords(BigDecimal.ONE, a);
		Vector2d d = new Vector2d(v2).mul(1.0/v2.length());
		d.mul(c.getGridStepping()[0].doubleValue()*100);
		g.drawLine(v1.x - (int)d.x, v1.y - (int)d.y, v1.x + (int)d.x, v1.y + (int)d.y);
	}

}
