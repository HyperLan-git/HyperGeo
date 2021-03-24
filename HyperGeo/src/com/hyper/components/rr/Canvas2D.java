package com.hyper.components.rr;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.mariuszgromada.math.mxparser.Function;

import com.hyper.math.Matrix2bd;
import com.hyper.math.Vector2bd;

public class Canvas2D extends Canvas {
	public static final Dimension MINIMUM_SIZE = new Dimension(200, 200);
	public static final Vector2i NULL = new Vector2i();
	public static final BigDecimal TWO = new BigDecimal(2), THREE = new BigDecimal(3),
			MIN = ONE.scaleByPowerOfTen(-1), MAX = ONE.scaleByPowerOfTen(3);
	public static final MathContext CONTEXT = new MathContext(10, RoundingMode.HALF_UP);
	public static final int DEFAULT_GRID_NUMBER = 20;
	public static final double TRANSFORM_TIME = 5000;
	public static final DecimalFormat NORMAL_GRAPH_FORMAT = new DecimalFormat("##0.##");
	public static final DecimalFormat BIG_SMALL_GRAPH_FORMAT = new DecimalFormat("##0.##E0");

	public static final String graphFormat(BigDecimal x) {
		BigDecimal abs = x.abs();
		if(ZERO.compareTo(x) == 0) return "0";
		if(abs.compareTo(MIN) == -1 || abs.compareTo(MAX) == 1) return BIG_SMALL_GRAPH_FORMAT.format(x);
		return NORMAL_GRAPH_FORMAT.format(x);
	}

	private Pane2D parent;

	private Dimension offDimension = new Dimension();
	private Image offImage;
	private Graphics offGraphics;

	private BigDecimal yMin = new BigDecimal(-6), yMax = new BigDecimal(6),
			xMin = new BigDecimal(-11), xMax = new BigDecimal(11),
			gridX = new BigDecimal(1), gridY = new BigDecimal(1);

	private Vector2i lastMousePos = NULL;
	private Function[] functions;
	private boolean showGrid = true;

	private long lastTransformTime = 0;

	private Matrix2bd targetTransform = new Matrix2bd(), lastTransform = new Matrix2bd(),
			currTransform = new Matrix2bd(), currTransformInverted = new Matrix2bd();

	public Canvas2D(Pane2D pane2d) {
		this.setMinimumSize(MINIMUM_SIZE);
		this.functions = new Function[0];
		this.parent = pane2d;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		if(offImage != null)
			g.drawImage(offImage, 0, 0, this);
		updateOffscreenImage();
		g.drawImage(offImage, 0, 0, this);
	}

	public void drawAll(Graphics2D g) {
		BigDecimal gridX = this.gridX,
				gridY = this.gridY;
		g.setStroke(new BasicStroke(1));
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, getWidth(), getHeight());
		updateTransform();
		//Draw the scale
		Vector2i gPos = NULL, gPos2 = NULL,
				gPos3 = NULL;
		g.setColor(Color.BLACK);
		for(BigDecimal x = ZERO; gPos.x <= this.getWidth(); x = x.add(gridX)) {
			if(x.compareTo(xMin.subtract(gridX.multiply(TWO))) == -1) continue;
			gPos = getGraphicalCoords(x, ZERO);
			gPos3 = getGraphicalCoords(x, ZERO, false);
			if(gPos3.x < 0) continue;
			if(gPos.y < 20) gPos.y = 20;
			if(gPos.y > this.getHeight()) gPos.y = this.getHeight();
			g.drawString(graphFormat(x), gPos.x() + 5, gPos.y() - 5);
		}
		gPos = NULL;
		for(BigDecimal x = ZERO; gPos.x >= 0; x = x.subtract(gridX)) {
			if(x.compareTo(xMax.add(gridX.multiply(TWO))) == 1) continue;
			gPos = getGraphicalCoords(x, ZERO);
			gPos3 = getGraphicalCoords(x, ZERO, false);
			if(gPos3.x > this.getWidth()) continue;
			if(gPos.y < 20) gPos.y = 20;
			if(gPos.y > this.getHeight()) gPos.y = this.getHeight();
			g.drawString(graphFormat(x), gPos.x() + 5, gPos.y() - 5);
		}

		gPos = NULL;
		for(BigDecimal y = ZERO; gPos.y >= 0; y = y.add(gridY)) {
			if(y.compareTo(yMin.subtract(gridY.multiply(TWO))) == -1) continue;
			gPos = getGraphicalCoords(ZERO, y);
			gPos3 = getGraphicalCoords(ZERO, y, false);
			if(gPos3.y-5 > this.getHeight()) continue;
			if(gPos.x < 0) gPos.x = 0;
			if(gPos.x > this.getWidth()-25) gPos.x = this.getWidth()-25;
			g.drawString(graphFormat(y), gPos.x() + 5, gPos.y() - 5);
		}
		gPos = NULL;
		for(BigDecimal y = ZERO; gPos.y <= this.getHeight(); y = y.subtract(gridY)) {
			if(y.compareTo(yMax.add(gridY.multiply(TWO))) == 1) continue;
			gPos = getGraphicalCoords(ZERO, y);
			gPos3 = getGraphicalCoords(ZERO, y, false);
			if(gPos3.y < 0) continue;
			if(gPos.x < 0) gPos.x = 0;
			if(gPos.x > this.getWidth()-25) gPos.x = this.getWidth()-25;
			g.drawString(graphFormat(y), gPos.x() + 5, gPos.y() - 5);
		}

		//Grid
		if(showGrid) {
			if(currTransform.hasProperty(Matrix2bd.PROPERTY_SCALER) || currTransform.det().signum() == 0) {
				//The screen has a square shape
				g.setColor(Color.GRAY);
				BigDecimal startX = xMin.subtract(xMin.remainder(gridX)).subtract(gridX);
				BigDecimal startY = yMin.subtract(yMin.remainder(gridY)).subtract(gridY);
				for(BigDecimal x = startX; gPos.x <= this.getWidth(); x = x.add(this.gridX)) {
					gPos = getGraphicalCoords(x, yMin);
					gPos2 = getGraphicalCoords(x, yMax);
					gPos3 = getGraphicalCoords(x, yMin, false);
					if(gPos3.x > this.getWidth()) continue;
					g.drawLine(gPos.x(), gPos.y(), gPos2.x(), gPos2.y());
				}
				for(BigDecimal y = startY; gPos.y >= 0; y = y.add(this.gridY)) {
					gPos = getGraphicalCoords(xMin, y);
					gPos2 = getGraphicalCoords(xMax, y);
					gPos3 = getGraphicalCoords(xMin, y, false);
					if(gPos3.y > this.getHeight()) continue;
					g.drawLine(gPos.x(), gPos.y(), gPos2.x(), gPos2.y());
				}
			} else {
				g.setColor(Color.GRAY);
				Vector2bd nwCorner = fromGraphicalCoords(new Vector2i(0, 0)), neCorner = fromGraphicalCoords(new Vector2i(this.getWidth(), 0)),
						swCorner = fromGraphicalCoords(new Vector2i(0, this.getHeight())), seCorner = fromGraphicalCoords(new Vector2i(this.getWidth(), this.getHeight()), true);
				BigDecimal xMin = nwCorner.x().min(neCorner.x()).min(swCorner.x()).min(seCorner.x()),
						xMax =  nwCorner.x().max(neCorner.x()).max(swCorner.x()).max(seCorner.x()),
						yMin = nwCorner.y().min(neCorner.y()).min(swCorner.y()).min(seCorner.y()),
						yMax = nwCorner.y().max(neCorner.y()).max(swCorner.y()).max(seCorner.y());
				//XXX System.out.println("yMin = " + seCorner.y());
				BigDecimal startX = xMin.subtract(xMin.remainder(gridX)).subtract(gridX.multiply(TWO)),
						startY = yMin.subtract(yMin.remainder(gridY)).subtract(gridY.multiply(TWO));
				for(BigDecimal x = startX; gPos.x <= this.getWidth() || gPos2.x <= this.getWidth(); x = x.add(this.gridX)) {
					gPos = getGraphicalCoords(x, yMin);
					gPos2 = getGraphicalCoords(x, yMax);
					g.drawLine(gPos.x(), gPos.y(), gPos2.x(), gPos2.y());
				}
				for(BigDecimal y = startY; gPos.y >= 0 || gPos2.y >= 0; y = y.add(this.gridY)) {
					gPos = getGraphicalCoords(xMin, y);
					gPos2 = getGraphicalCoords(xMax, y);
					g.drawLine(gPos.x(), gPos.y(), gPos2.x(), gPos2.y());
				}
			}
		}
		//Draw the axes
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(3));
		if(xMin.signum() == -1 && xMax.signum() == 1) {
			Vector2i pos = getGraphicalCoords(ZERO, yMin),
					pos2 = getGraphicalCoords(ZERO, yMax);
			g.drawLine(pos.x(), pos.y(), pos2.x(), pos2.y());
		}
		if(yMin.signum() == -1 && yMax.signum() == 1) {
			Vector2i pos = getGraphicalCoords(xMin, ZERO),
					pos2 = getGraphicalCoords(xMax, ZERO);
			g.drawLine(pos.x(), pos.y(), pos2.x(), pos2.y());
		}

		if(functions != null)
			drawFunctions(g);
	}

	private void drawFunctions(Graphics2D g) {
		for(int i = 0; i < functions.length; i++) {
			Function f = functions[i];
			if(f == null || !f.checkSyntax()) continue;
			double increment = Math.abs(xMax.doubleValue()-xMin.doubleValue())/this.getWidth();
			Vector2d pos = new Vector2d(xMin.doubleValue(), getFunctionValueAt(f, xMin.doubleValue()));
			for(double x = this.xMin.doubleValue(); x < this.xMax.doubleValue(); x += increment) {
				Vector2d lastPos = pos;
				pos = new Vector2d(x, getFunctionValueAt(f, x));
				if((!Double.isFinite(pos.y)) || (!Double.isFinite(lastPos.y))) continue;
				Vector2i gPos = getGraphicalCoords(pos),
						gNextPos = getGraphicalCoords(lastPos);

				g.setColor(parent.getFunctionPanel().getTable().getFunctionColor(i));
				g.setStroke(new BasicStroke(1));
				g.drawLine(gPos.x, gPos.y, gNextPos.x, gNextPos.y);
			}
		}
	}


	private double getFunctionValueAt(Function f, double x) {
		if(f == null || !f.checkSyntax()) return Double.NaN;
		return f.calculate(x);
	}

	public void update() {
		repaint();
	}

	private void updateOffscreenImage() {
		Dimension d = Canvas2D.this.getSize();
		if (d.width > 0 && (offGraphics == null || d.width != offDimension.width || d.height != offDimension.height)) {
			offDimension = d;
			offImage = createImage(d.width, d.height);
			offGraphics = offImage.getGraphics();
		}
		if(offGraphics != null)
			drawAll((Graphics2D)offGraphics);
	}

	public void updateTransform() {
		long currTime = System.currentTimeMillis();
		double diff = (double) (currTime-lastTransformTime);
		if(diff > TRANSFORM_TIME) {
			this.currTransform = this.targetTransform;
		} else {
			BigDecimal progress = new BigDecimal(diff/TRANSFORM_TIME),
					progressToTarget = ONE.subtract(progress);
			Matrix2bd temp = new Matrix2bd();
			this.targetTransform.scale(progress, temp);
			this.lastTransform.scale(progressToTarget, currTransform).add(temp);
			this.currTransform.checkProperties();
			this.currTransform.invert(CONTEXT, currTransformInverted);
			new Thread() {
				public void run() {
					try {
						Thread.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Canvas2D.this.repaint();
				}
			}.start();
		}
	}

	public Vector2i getGraphicalCoords(double x, double y) {
		return getGraphicalCoords(new BigDecimal(x), new BigDecimal(y));
	}

	public Vector2i getGraphicalCoords(Vector2d pos) {
		return getGraphicalCoords(new BigDecimal(pos.x), new BigDecimal(pos.y));
	}

	public Vector2i getGraphicalCoords(Vector2bd pos) {
		return getGraphicalCoords(pos, true);
	}

	public Vector2i getGraphicalCoords(BigDecimal x, BigDecimal y) {
		return getGraphicalCoords(x, y, true);
	}

	public Vector2i getGraphicalCoords(BigDecimal x, BigDecimal y, boolean transform) {
		return getGraphicalCoords(new Vector2bd(x, y), transform);
	}

	public Vector2i getGraphicalCoords(Vector2bd pos, boolean transform) {
		BigDecimal width = xMax.subtract(xMin),
				height = yMax.subtract(yMin);

		if(transform)
			pos.mul(currTransform);

		return new Vector2i(pos.x().subtract(xMin).multiply(new BigDecimal(this.getWidth()), CONTEXT).divide(width, CONTEXT).intValue(), 
				pos.y().subtract(yMax).negate().multiply(new BigDecimal(this.getHeight(), CONTEXT)).divide(height, CONTEXT).intValue());
	}

	public Vector2bd fromGraphicalCoords(Vector2i pos) {
		return fromGraphicalCoords(pos, true);
	}

	public Vector2bd fromGraphicalCoords(Vector2i pos, boolean transform) {
		BigDecimal width = xMax.subtract(xMin),
				height = yMax.subtract(yMin);
		Vector2bd result = new Vector2bd(new BigDecimal(pos.x), new BigDecimal(pos.y));

		if(transform)
			result.mul(currTransformInverted);

		result.set(new Vector2bd(new BigDecimal(pos.x).multiply(width, CONTEXT).divide(new BigDecimal(this.getWidth()), CONTEXT).add(xMin),
				new BigDecimal(pos.y).multiply(height, CONTEXT).divide(new BigDecimal(this.getHeight()), CONTEXT).negate().add(yMax)));

		return result;
	}

	public void zoom(double amount) {
		System.out.println("Zoomed by : " + amount);
		amount += 1;
		if(amount < 1 && xMax.subtract(xMin).compareTo(ONE.scaleByPowerOfTen(-3)) < 1) return;
		if(amount > 1 && xMax.subtract(xMin).compareTo(ONE.scaleByPowerOfTen(300)) > -1) return;
		BigDecimal width = xMax.subtract(xMin),
				height = yMax.subtract(yMin),
				midX = xMin.add(xMax).divide(TWO),
				midY = yMin.add(yMax).divide(TWO),
				halfNewHeight = height.multiply(new BigDecimal(amount), CONTEXT).divide(TWO),
				halfNewWidth = width.multiply(new BigDecimal(amount), CONTEXT).divide(TWO);
		xMin = midX.subtract(halfNewWidth, CONTEXT);
		xMax = midX.add(halfNewWidth, CONTEXT);
		yMin = midY.subtract(halfNewHeight, CONTEXT);
		yMax = midY.add(halfNewHeight, CONTEXT);
		System.out.println("yMin=" + yMin);
		System.out.println("yMax=" + yMax);
		System.out.println("xMin=" + xMin);
		System.out.println("xMax=" + xMax);
		setGridStepping(gridX, gridY);
		/*gridX = (float) HyperGeo.roundOff(gridX*amount, graphicalPrecision);
		System.out.println("gridX=" + gridX);
		gridY = (float) HyperGeo.roundOff(gridY*amount, graphicalPrecision);*/
		System.out.println("gridY=" + gridY);
		this.repaint();
	}

	public void mousePressed(int x, int y) {
		lastMousePos = new Vector2i(x, y);
	}

	public void mouseReleased(int x, int y) {
		lastMousePos = NULL;
	}

	public void mouseDragged(int x, int y) {
		if(lastMousePos == NULL)
			lastMousePos = new Vector2i(x, y);
		else {
			Vector2bd move = fromGraphicalCoords(lastMousePos, false),
					pos = fromGraphicalCoords(new Vector2i(x, y), false);
			move.set(move.x().subtract(pos.x()), move.y().subtract(pos.y()));
			this.xMin = xMin.add(move.x());
			this.xMax = xMax.add(move.x());
			this.yMin = yMin.add(move.y());
			this.yMax = yMax.add(move.y());
			repaint();
			lastMousePos = new Vector2i(x, y);
		}
	}

	public void setFunctions(Function[] functions) {
		this.functions = functions;

		this.repaint();
	}

	/**
	 * @see #getVWin()
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 */
	public void setVWin(BigDecimal xMin, BigDecimal xMax, BigDecimal yMin, BigDecimal yMax) {
		if(xMin.compareTo(xMax) == 1) {
			BigDecimal temp = xMin;
			xMin = xMax;
			xMax = temp;
		}

		if(yMin.compareTo(yMax) == 1) {
			BigDecimal temp = yMin;
			yMin = yMax;
			yMax = temp;
		}

		this.xMax = xMax;
		this.xMin = xMin;
		this.yMin = yMin;
		this.yMax = yMax;

		setGridStepping(xMax.subtract(xMin).divide(new BigDecimal(DEFAULT_GRID_NUMBER), CONTEXT),
				yMax.subtract(yMin).divide(new BigDecimal(DEFAULT_GRID_NUMBER), CONTEXT));
	}


	/**
	 * @see #setVWin(float, float, float, float)
	 * @return A vector of 4 {@link java.math.BigDecimal}, with in order : xMin, xMax, yMin, yMax
	 */
	public BigDecimal[] getVWin() {
		return new BigDecimal[] {xMin, xMax, yMin, yMax};
	}

	public boolean showsGrid() {
		return showGrid;
	}

	public void showGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	/**
	 * @see #setGridStepping(float, float)
	 * @return A Vector2f with (gridX, gridY) as componenents
	 */
	public BigDecimal[] getGridStepping() {
		return new BigDecimal[] {this.gridX, this.gridY};
	}

	public void setTransform(Matrix2bd transform) {
		this.lastTransform.set(targetTransform);
		this.targetTransform.set(transform);
		this.lastTransformTime = System.currentTimeMillis();

		System.out.println(lastTransform);
		System.out.println(targetTransform);

		Matrix2bd invert = new Matrix2bd();
		targetTransform.invert(CONTEXT, invert);
		System.out.println("invert = " + invert);
		repaint();
	}

	public Matrix2bd getTransform() {
		return targetTransform;
	}

	/**
	 * @see #getGridStepping()
	 * @param gridX X axis scale stepping
	 * @param gridY Y axis scale stepping
	 */
	public void setGridStepping(BigDecimal gridX, BigDecimal gridY) {
		BigDecimal width = xMax.subtract(xMin);
		while(width.divide(gridX, CONTEXT).compareTo(TWO.scaleByPowerOfTen(1)) == 1) {
			gridX = gridX.scaleByPowerOfTen(1);
			gridY = gridY.scaleByPowerOfTen(1);
		}
		while(gridX.divide(width, CONTEXT).compareTo(ONE.divide(THREE, CONTEXT)) == 1) {
			gridX = gridX.scaleByPowerOfTen(-1);
			gridY = gridY.scaleByPowerOfTen(-1);
		}
		this.gridX = gridX;
		this.gridY = gridY;
	}
}
