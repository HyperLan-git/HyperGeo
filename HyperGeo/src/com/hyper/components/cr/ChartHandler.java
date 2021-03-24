package com.hyper.components.cr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import org.joml.Vector2i;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.mariuszgromada.math.mxparser.Function;

public class ChartHandler extends JPanel {
	public static final Dimension MINIMUM_SIZE = new Dimension(100, 100);

	public static final int MAX_ALPHA = 200;

	private Pane3D parent;

	private Chart chart;

	private Function[] functions = new Function[0];

	private GraphFunctionColor[] colors = new GraphFunctionColor[0];

	private Range xValues = new Range(-10,10),
			yValues = new Range(-10,10),
			zValues = new Range(0, 100);

	private int xSteps = 50, ySteps = 50;

	public ChartHandler(Pane3D parent) {
		this.parent = parent;
		this.setLayout(new BorderLayout());
		this.setBackground(java.awt.Color.WHITE);
		this.chart = AWTChartComponentFactory.chart(Quality.Nicest, IChartComponentFactory.Toolkit.newt);
		chart.setAxeDisplayed(true);
		Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(xValues, xSteps, yValues, ySteps), new FunctionMapper());
		surface.setColor(Color.BLACK);
		chart.getScene().getGraph().add(surface);
		chart.addController(new CameraMouseController(chart));
		setChartZScale(zValues);
		this.add(getChartComponent(), BorderLayout.CENTER);

		chart.getAxeLayout().setXAxeLabel("X");
		chart.getAxeLayout().setYAxeLabel("Y");
		chart.getAxeLayout().setZAxeLabel("Z");
	}

	public Chart getChart() {
		return chart;
	}

	public Component getChartComponent() {
		Component canvas = (Component) chart.getCanvas();
		return canvas;
	}

	public void setFunctions(Function[] functions) {
		this.functions = functions;
		colors = new GraphFunctionColor[functions.length];
		for(int i = 0; i < colors.length; i++) {
			java.awt.Color awtColor = this.parent.getFunctionPanel().getTable().getFunctionColor(i);
			colors[i] = new GraphFunctionColor(new Color(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(),
					(awtColor.getAlpha() < MAX_ALPHA)?awtColor.getAlpha():MAX_ALPHA));
		}
		updateGraph();
	}

	public Range getVWinX() {
		return xValues;
	}

	public void setVWinX(Range xRange) {
		this.xValues = xRange;
	}

	public Range getVWinY() {
		return yValues;
	}

	public void setVWinY(Range yRange) {
		this.yValues = yRange;
	}

	public Range getVWinZ() {
		return zValues;
	}

	public void setVWinZ(Range zRange) {
		this.zValues = zRange;
	}
	
	private void setChartZScale(Range range) {
		chart.setScale(range);
	}

	/**
	 * @see #setStepping()
	 * X represents the number of steps between xMin and xMax ie. the number of points drawn in the X direction
	 * Y represents the number of steps between yMin and yMax ie. the number of points drawn in the Y direction
	 * @return A Vector2i of coordinates (xSteps,ySteps)
	 */
	public Vector2i getStepping() {
		return new Vector2i(xSteps, ySteps);
	}

	/**
	 * @see #getStepping()
	 * @param xSteps the number of steps between xMin and xMax ie. the number of points drawn in the X direction
	 * @param ySteps the number of steps between yMin and yMax ie. the number of points drawn in the Y direction
	 */
	public void setStepping(int xSteps, int ySteps) {
		this.xSteps = xSteps;
		this.ySteps = ySteps;
	}

	public void updateGraph() {
		setChartZScale(zValues);
		List<AbstractDrawable> list = chart.getScene().getGraph().getAll();
		synchronized(list) {
			list.clear();

			for(int i = 0; i < functions.length; i++) {
				Function f = functions[i];
				if(f == null || !f.checkSyntax()) continue;

				Mapper m = new FunctionMapper(f);
				Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(xValues, xSteps, yValues, ySteps), m);
				surface.setColorMapper(colors[i].generateColorMapper());
				surface.setWireframeColor(Color.BLACK);
				/*surface.setColor(Color.BLACK);
				surface.setWireframeDisplayed(true);*/
				list.add(surface);
			}
		}
		chart.render();
	}

	@Override
	public Dimension getMinimumSize() {
		return MINIMUM_SIZE;
	}

	@Override
	public void setSize(int width, int height) {
		if(height > width) height = width;
		if(height < width) width = height;
		super.setSize(width, height);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		if(height > width) height = width;
		if(height < width) width = height;
		super.setBounds(x, y, width, height);
	}
}
