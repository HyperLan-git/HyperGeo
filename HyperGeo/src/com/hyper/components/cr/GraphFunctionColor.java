package com.hyper.components.cr;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.maths.Coord3d;

public class GraphFunctionColor {
	private Color plainColor;

	private ColorMode colorMode;

	public GraphFunctionColor(Color color) {
		if(color.a == 0) colorMode = ColorMode.RAINBOW;
		else colorMode = ColorMode.PLAIN;
		this.plainColor = color;
	}

	public ColorMode getColorMode() {
		return this.colorMode;
	}

	public Color getPlainColor() {
		return this.plainColor;
	}

	public ColorMapper generateColorMapper() {
		switch(colorMode) {
		case PLAIN:
			return new PlainMapper(plainColor);
		case RAINBOW:
			return new RainbowMapper();
		default:
			return null;
		}
	}


	public static class PlainMapper extends ColorMapper {
		private Color color;

		public PlainMapper(Color c) {
			this.color = c;
		}

		@Override
		public Color getColor(Coord3d coord) {
			return color;
		}
	}

	public static class RainbowMapper extends ColorMapper {
		@Override
		public Color getColor(Coord3d coord) {
			java.awt.Color color = new java.awt.Color(java.awt.Color.HSBtoRGB((float) (Math.atan2(coord.y, coord.x)*0.5/Math.PI), 1, 1));
			return new Color(color.getRed(), color.getGreen(), color.getBlue());
		}
	}

	public static enum ColorMode {
		PLAIN,
		RAINBOW;
	}
}
