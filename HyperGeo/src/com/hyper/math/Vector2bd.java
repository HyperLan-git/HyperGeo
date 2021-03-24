package com.hyper.math;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.BigDecimal.*;

public class Vector2bd {
	protected BigDecimal x = ZERO, y = ZERO;

	/**
	 * Initialized as the origin (0, 0)
	 */
	public Vector2bd() {}

	public Vector2bd(Vector2bd other) {
		this.set(other);
	}

	public Vector2bd(BigDecimal x, BigDecimal y) {
		this.set(x, y);
	}

	public BigDecimal x() {
		return x;
	}
	
	public BigDecimal y() {
		return y;
	}

	public void set(BigDecimal x, BigDecimal y) {
		this.x = x;
		this.y = y;
	}

	public Vector2bd set(Vector2bd other) {
		this.x = other.x;
		this.y = other.y;
		return this;
	}

	public Vector2bd mul(Matrix2bd matrix) {
		return mul(matrix, this);
	}

	public Vector2bd mul(Matrix2bd matrix, Vector2bd dest) {
		if(matrix.hasProperty(Matrix2bd.PROPERTY_IDENTITY)) {
			dest.set(this);
			return dest;
		}
		if(matrix.hasProperty(Matrix2bd.PROPERTY_SCALER)) {
			dest.set(this).scale(matrix.m00);
			return dest;
		}
		dest.x = matrix.m00().multiply(this.x).add(matrix.m10().multiply(this.y));
		dest.y = matrix.m01().multiply(this.x).add(matrix.m11().multiply(this.y));
		return dest;
	}

	public Vector2bd scale(BigDecimal scalar) {
		return scale(scalar, this);
	}

	public Vector2bd scale(BigDecimal scalar, Vector2bd dest) {
		dest.x = this.x.multiply(scalar);
		dest.y = this.y.multiply(scalar);
		return dest;
	}

	public Vector2bd add(Vector2bd other) {
		return add(other, this);
	}
	
	public Vector2bd add(Vector2bd other, Vector2bd dest) {
		dest.x = this.x.add(other.x);
		dest.y = this.y.add(other.y);
		return dest;
	}
	
	public Vector2bd sub(Vector2bd other) {
		return sub(other, this);
	}
	
	public Vector2bd sub(Vector2bd other, Vector2bd dest) {
		dest.x = this.x.subtract(other.x);
		dest.y = this.y.subtract(other.y);
		return dest;
	}
	
	public BigDecimal dot(Vector2bd other) {
		return x.multiply(other.x).add(y.multiply(other.y));
	}
	
	public BigDecimal lengthSquared() {
		return x.multiply(x).multiply(y).multiply(y);
	}
	
	public BigDecimal length(MathContext c) {
		return lengthSquared().sqrt(c);
	}

	public Vector2bd normalize(MathContext c) {
		if(lengthSquared().signum() == 0)
			return null;
		this.scale(ONE.divide(length(c), c));
		return this;
	}


	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
