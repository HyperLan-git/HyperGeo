package com.hyper.math;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @author Youness
 * Don't forget to use {@link #checkProperties()} after calculations to speed up future ones,
 * especially if working with {@link com.hyper.math.Vector2bd}
 */
public class Matrix2bd {
	public static final int PROPERTY_IDENTITY = 1<<0, PROPERTY_SCALER = 1<<1;

	protected BigDecimal m00 = ONE, m01 = ZERO,
			m10 = ZERO, m11 = ONE;

	protected int properties = PROPERTY_IDENTITY;

	/**
	 * Initialized as identity matrix</br>
	 * <code>
	 * ┌─────┐</br>
	 * │ 1 0 │</br>
	 * │ 0 1 │</br>
	 * └─────┘</br>
	 * </code>
	 */
	public Matrix2bd() {}

	/**
	 * @param m00 the coefficient at line 1 and column 1
	 * @param m01 the coefficient at line 1 and column 2
	 * @param m10 the coefficient at line 2 and column 1
	 * @param m11 the coefficient at line 2 and column 2
	 */
	public Matrix2bd(BigDecimal m00, BigDecimal m01, BigDecimal m10, BigDecimal m11) {
		this.m00 = m00;
		this.m01 = m01;
		this.m10 = m10;
		this.m11 = m11;
		checkProperties();
	}

	public Matrix2bd(Matrix2bd other) {
		set(other);
	}

	public Matrix2bd add(Matrix2bd other) {
		return this.add(other, this);
	}

	public Matrix2bd add(Matrix2bd other, Matrix2bd dest) {
		dest.m00 = m00.add(other.m00);
		dest.m01 = m01.add(other.m01);
		dest.m10 = m10.add(other.m10);
		dest.m11 = m11.add(other.m11);
		dest.properties = 0;
		return dest;
	}


	public Matrix2bd sub(Matrix2bd other) {
		return this.sub(other, this);
	}

	public Matrix2bd sub(Matrix2bd other, Matrix2bd dest) {
		dest.m00 = m00.subtract(other.m00);
		dest.m01 = m01.subtract(other.m01);
		dest.m10 = m10.subtract(other.m10);
		dest.m11 = m11.subtract(other.m11);
		dest.properties = 0;
		return dest;
	}

	public Matrix2bd mul(Matrix2bd other) {
		return this.mul(other, this);
	}

	public Matrix2bd mul(Matrix2bd other, Matrix2bd dest) {
		dest.m00 = m00.multiply(other.m00).add(m01.multiply(other.m10));
		dest.m01 = m00.multiply(other.m01).add(m01.multiply(other.m11));
		dest.m10 = m10.multiply(other.m01).add(m11.multiply(other.m10));
		dest.m11 = m10.multiply(other.m00).add(m11.multiply(other.m11));
		dest.properties = 0;
		return dest;
	}


	public Matrix2bd scale(BigDecimal scalar) {
		return this.scale(scalar, this);
	}

	public Matrix2bd scale(BigDecimal scalar, Matrix2bd dest) {
		dest.m00 = m00.multiply(scalar);
		dest.m01 = m01.multiply(scalar);
		dest.m10 = m10.multiply(scalar);
		dest.m11 = m11.multiply(scalar);
		dest.properties = 0;
		return dest;
	}


	public BigDecimal det() {
		if(hasProperty(PROPERTY_IDENTITY)) return ONE;
		if(hasProperty(PROPERTY_SCALER)) return m00.multiply(m00);
		return m00.multiply(m11).subtract(m01.multiply(m10));
	}

	public Matrix2bd transpose() {
		return new Matrix2bd(m00, m10, m01, m11);
	}

	public Matrix2bd transpose(Matrix2bd dest) {
		if(hasProperty(PROPERTY_IDENTITY)) {
			dest.set(new Matrix2bd());
			dest.properties = this.properties;
			return dest;
		}
		if(hasProperty(PROPERTY_SCALER)) {
			dest.set(new Matrix2bd().scale(this.m00));
			dest.properties = this.properties;
			return dest;
		}
		BigDecimal temp = m01;
		dest.m00 = m00;
		dest.m01 = m10;
		dest.m10 = temp;
		dest.m11 = m11;
		return dest;
	}

	public Matrix2bd adjugate() {
		return adjugate(this);
	}

	public Matrix2bd adjugate(Matrix2bd dest) {
		BigDecimal temp = m00;
		dest.m00 = m11;
		dest.m01 = m01.negate();
		dest.m10 = m10.negate();
		dest.m11 = temp;
		dest.properties = 0;
		return dest;
	}


	public Matrix2bd invert(MathContext context) {
		return this.invert(context, this);
	}

	public Matrix2bd invert(MathContext context, Matrix2bd dest) {
		if(this.hasProperty(PROPERTY_IDENTITY)) {
			dest.set(new Matrix2bd());
			return dest;
		}
		if(this.hasProperty(PROPERTY_SCALER)) {
			dest.set(new Matrix2bd().scale(ONE.divide(this.det(), context)));
			return dest;
		}
		BigDecimal det = det();
		if(det.signum() == 0) 
			return null;

		return this.transpose(dest).adjugate().scale(ONE.divide(det, context));
	}


	public void set(Matrix2bd other) {
		this.m00 = other.m00;
		this.m01 = other.m01;
		this.m10 = other.m10;
		this.m11 = other.m11;
		this.properties = other.properties;
	}

	public BigDecimal m00() {
		return m00;
	}

	public BigDecimal m10() {
		return m10;
	}

	public BigDecimal m01() {
		return m01;
	}

	public BigDecimal m11() {
		return m11;
	}

	public void m00(BigDecimal m00) {
		this.m00 = m00;
	}

	public void m10(BigDecimal m10) {
		this.m10 = m10;
	}

	public void m01(BigDecimal m01) {
		this.m01 = m01;
	}

	public void m11(BigDecimal m11) {
		this.m11 = m11;
	}

	public Matrix2bd checkProperties() {
		properties = 0;

		if(m00.intValue() == 1 && m11.intValue() == 1 && m01.signum() == 0 && m10.signum() == 0) properties |= PROPERTY_IDENTITY;
		if(m00.compareTo(m11) == 0 && m01.signum() == 0 && m10.signum() == 0) properties |= PROPERTY_SCALER;

		return this;
	}

	public boolean hasProperty(int property) {
		return (this.properties & property) != 0;
	}

	@Override
	public String toString() {
		return "{" + m00.stripTrailingZeros() + ", " + m01.stripTrailingZeros() + ", "
				+ m10.stripTrailingZeros() + ", " + m11.stripTrailingZeros() + "}";
	}
}
