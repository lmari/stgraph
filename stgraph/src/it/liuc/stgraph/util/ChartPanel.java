/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2022, Luca Mari.
 * 
 * STGraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 * 
 * STGraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with STGraph.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package it.liuc.stgraph.util;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.widget.ChartWidget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.nfunk.jep.type.Tensor;


/** Chart panel: wrapped by / adapted to the STGraph widget protocol (as in the STWidget class)
 * by the ChartWidget(/Dialog/View) class(es).
 * <br>Main features:
 * <br>- it operates on one or more XYSeries, in the seriesCollection list: each series has its own
 * domain and range, and number of elements (thus allowing dynamic, i.e., with growing number of elements, series);
 * <br>- source data are dynamically converted to XYSeries by the ChartWidgetView.paint() method by calling
 * the ChartPanel.addSeries() method; in the case of vector or matrix source data, each column is transformed
 * to a different XYSeries by the same method (as a consequence, at the moment distinct columns of the same matrix
 * are dealt with as distinct series, and no interactions among them is possible);
 *
 *
 * <p>To fix:
 * <br>- strips are not resized when window size changes */
@SuppressWarnings("serial")
public class ChartPanel extends JPanel implements ComponentListener {

	/** The Constant NOTHING. */
	public static final int NOTHING = 0;
	/** The Constant ONLY_AXIS. */
	public static final int ONLY_AXIS = 1;
	/** The Constant GRID. */
	public static final int GRID = 2;
	/** The Constant sensibility threshold. */
	private static final double SENSIBILITY_THRESHOLD = 0.001;
	/** The sizes of the x (horizontal, bottom) strip. */
	private static final transient int[] XSTRIP_SIZES = { 35, 30, 25 };
	/** The sizes of the y (vertical, left) strip. */
	private static final transient int[] YSTRIP_SIZES = { 60, 50, 40 };
	/** The sizes of the line ticks. */
	private static final transient int[] TICK_SIZES = { 4, 3, 2 };
	/** The font sizes of the characters. */
	private static final transient float[] FONT_SIZES = { 10F, 9F, 8F };

	// a bug prevents refreshing strips in some circumstances: functionality temporarily disabled...
	/** The thresholds of window width. */
	//private static final transient int[] WIDTH_THRES = { 350, 250 };
	/** The thresholds of window height. */
	//private static final transient int[] HEIGHT_THRES = { 300, 200 };

	/** The plot area. */
	private JPanel plotArea = new PlotPanel();
	/** The x strip. */
	private JPanel xStrip = new XStripPanel();
	/** The y strip. */
	private JPanel yStrip = new YStripPanel();
	/** The series collection. */
	private transient AbstractList<XYSeries> seriesCollection = new ArrayList<XYSeries>();
	/** The rectangle. */
	private transient Rectangle2D.Double rectangle = new SerializableRectangle(0.0, 0.0, 1.0, 1.0); // default rectangle
	/** The size of the x (horizontal, bottom) strip. */
	private int xStripSize;
	/** The size of the y (vertical, left) strip. */
	private int yStripSize;
	/** The size of the line ticks. */
	private int tickSize;
	/** The font size of the characters. */
	private float fontSize;
	/** The size of the margin left around the plot area. */
	private final int plotMarginSize = 5;
	/** The isometric. */
	private boolean isometric = false;
	/** The auto scale X. */
	private boolean autoScaleX = false;
	/** The auto scale Y. */
	private boolean autoScaleY = false;
	/** The grid step X. */
	private double stepX;
	/** The grid step Y. */
	private double stepY;
	/** The scale X. */
	private double scaleX;
	/** The scale Y. */
	private double scaleY;
	/** The shift X. */
	private double shiftX;
	/** The shift Y. */
	private double shiftY;
	/** The scale X data. */
	private ScaleData scaleXData;
	/** The scale Y data. */
	private ScaleData scaleYData;
	/** The auto legend X. */
	private boolean autoLegendX = true;
	/** The auto legend Y. */
	private boolean autoLegendY = true;
	/** The legend X. */
	private String legendX = null;
	/** The legend Y. */
	private String legendY = null;
	/** The log scale X. */
	private boolean logScaleX = false;
	/** The log scale Y. */
	private boolean logScaleY = false;
	/** The h grid. */
	private int yGrid = ChartPanel.GRID; // default value
	/** The v grid. */
	private int xGrid = ChartPanel.GRID; // default value
	/** The default color. */
	private transient Color[] defaultColor = { Color.RED, Color.BLUE, Color.GREEN, Color.BLACK };
	/** The grid color. */
	private Color gridColor = new Color(220, 220, 220);
	/** The default font. */
	private Font defaultFont = new Font(STGraph.getMyFont(), Font.PLAIN, 10);
	/** The my formatter1. */
	private DecimalFormat myFormatter1 = new DecimalFormat("###0.0###"); //$NON-NLS-1$
	/** The my formatter2. */
	private DecimalFormat myFormatter2 = new DecimalFormat("0.0E0"); //$NON-NLS-1$


	/** Class constructor. */
	public ChartPanel() {
		super(true);
		addComponentListener(this);
		defineSizeClass(true, true);
		setLayout(new BorderLayout());
		add(yStrip, BorderLayout.WEST);
		add(xStrip, BorderLayout.SOUTH);
		add(plotArea, BorderLayout.CENTER);
	}


	/** Define the size class. */
	public void defineSizeClass(final boolean showYStrip, final boolean showXStrip) {
		int sizeClass = 1;
		// a bug prevents refreshing strips in some circumstances: functionality temporarily disabled...
		/*
        int w = getWidth(), h = getHeight();
        if(w == 0 || h == 0) { sizeClass = 2; }
        else if(w < WIDTH_THRES[1] || h < HEIGHT_THRES[1]) { sizeClass = 2; }
        else if(w > WIDTH_THRES[0] && h > HEIGHT_THRES[0]) { sizeClass = 0; }
        else { sizeClass = 1; }
		 */
		xStripSize = XSTRIP_SIZES[sizeClass];
		yStripSize = YSTRIP_SIZES[sizeClass];
		tickSize = TICK_SIZES[sizeClass];
		fontSize = FONT_SIZES[sizeClass];
		if(!showYStrip) { yStripSize = 1; }
		if(!showXStrip) { xStripSize = 1; }

		Dimension d;
		xStrip.setPreferredSize(d = new Dimension(1, xStripSize));
		xStrip.setSize(d);
		yStrip.setPreferredSize(d = new Dimension(yStripSize, 1));
		yStrip.setSize(d);
	}


	/** Component resized.
	 * @param arg0 the arg0 */
	public final void componentResized(final ComponentEvent arg0) {
		// a bug prevents refreshing strips in some circumstances: functionality temporarily disabled...
		//defineSizeClass();
		repaint();
	}

	
	/** Component moved.
	 * @param arg0 the arg0 */
	public final void componentMoved(final ComponentEvent arg0) { ; }

	
	/** Component shown.
	 * @param arg0 the arg0 */
	public final void componentShown(final ComponentEvent arg0) { ; }

	
	/** Component hidden.
	 * @param arg0 the arg0 */
	public final void componentHidden(final ComponentEvent arg0) { ; }


	/** Add a series to this panel, by specifying its x and y lists.
	 * @param xValues the x values
	 * @param yValues the y values
	 * @param xName the x name
	 * @param yName the y name
	 * @return series */
	public final XYSeries addSeries(final AbstractList<Tensor> xValues, final AbstractList<Tensor> yValues, final String xName, final String yName) {
		XYSeries insertedSeries;
		seriesCollection.add(insertedSeries = new XYSeries(this, xValues, yValues, xName, yName));
		int insertedIndex = seriesCollection.size() - 1;
		getSeries(insertedIndex).setDotsColor(defaultColor[Math.min(insertedIndex, 3)]);
		return insertedSeries;
	}


	/** Add a series to this panel.
	 * <br>The x and y lists can be subsequently specified by calling the setX() and setY() methods on the inserted series.
	 * @return the added series */
	public final XYSeries addSeries() {
		XYSeries addedSeries;
		seriesCollection.add(addedSeries = new XYSeries(this));
		int insertedIndex = seriesCollection.size() - 1;
		getSeries(insertedIndex).setDotsColor(defaultColor[Math.min(insertedIndex, 3)]);
		return addedSeries;
	}


	/** Get the i-th series added to this panel.
	 * @param i the index
	 * @return series */
	public final XYSeries getSeries(final int i) {
		try {
			return seriesCollection.get(i);
		} catch (Exception e) {
			return null;
		}
	}


	/** Get the plot area.
	 * @return area */
	public final JPanel getPlotArea() { return plotArea; }


	/** Get the number of series currently added to this panel.
	 * @return number of series */
	public final int getNumSeries() { return seriesCollection.size(); }


	/** Remove all the series already added to this panel. */
	public final void removeAllSeries() { seriesCollection = new ArrayList<XYSeries>(); }


	/** Set the h grid attribute.
	 * @param hGrid the horizontal grid */
	public final void setHGrid(final int hGrid) { this.yGrid = hGrid; }

	
	/** Get the horizontal grid attribute.
	 * @return hGrid */
	public final int getHGrid() { return yGrid; }

	
	/** Set the vertical grid attribute.
	 * @param vGrid the vertical grid */
	public final void setVGrid(final int vGrid) { this.xGrid = vGrid; }

	
	/** Get the vertical grid attribute.
	 * @return vGrid */
	public final int getVGrid() { return xGrid; }


	/** Set the bounding coordinates of the function space.
	 * @param x0 the x0
	 * @param y0 the y0
	 * @param x1 the x1
	 * @param y1 the y1 */
	public final void setRectangle(final double x0, final double y0, final double x1, final double y1) {
		rectangle.x = x0;
		rectangle.y = y0;
		rectangle.width = x1 - x0;
		rectangle.height = y1 - y0;
	}


	/** Set the bounding X coordinates of the function space.
	 * @param x0 the x0
	 * @param x1 the x1 */
	public final void setRectangleX(final double x0, final double x1) {
		rectangle.x = x0;
		rectangle.width = x1 - x0;
	}


	/** Set the bounding Y coordinates of the function space.
	 * @param y0 the y0
	 * @param y1 the y1 */
	public final void setRectangleY(final double y0, final double y1) {
		rectangle.y = y0;
		rectangle.height = y1 - y0;
	}


	/** Get the bounding coordinates of the function space.
	 * @return bounds */
	public final Rectangle2D.Double getRectangle() { return rectangle; }


	/** Specify whether the chart X-Y dimensions must preserve the drawing X-Y dimensions.
	 * @param isometric the isometric */
	public final void setIsometric(final boolean isometric) { this.isometric = isometric; }

	
	/** Get whether the chart X-Y dimensions preserve the drawing X-Y dimensions.
	 * @return is isometric */
	public final boolean isIsometric() { return isometric; }

	
	/** Specify whether the X dimension of the bounding rectangle of the plot area
	 * must be computed automatically before the plot.
	 * @param autoScaleX the autoscale X */
	public final void setAutoScaleX(final boolean autoScaleX) { this.autoScaleX = autoScaleX; }

	
	/** Get whether the X dimension of the bounding rectangle of the plot area
	 * are computed automatically before the plot.
	 * @return is autoscale X */
	public final boolean isAutoScaleX() { return autoScaleX; }

	
	/** Specify whether the Y dimension of the bounding rectangle of the plot area
	 * must be computed automatically before the plot.
	 * @param autoScaleY the autoscale Y */
	public final void setAutoScaleY(final boolean autoScaleY) { this.autoScaleY = autoScaleY; }

	
	/** Get whether the Y dimension of the bounding rectangle of the plot area
	 * are computed automatically before the plot.
	 * @return is autoscale Y */
	public final boolean isAutoScaleY() { return autoScaleY; }

	
	/** Set the X grid step.
	 * @param stepX the step X */
	public final void setStepX(final double stepX) { this.stepX = stepX; }

	
	/** Get the X grid step.
	 * @return the step X */
	public final double getStepX() { return stepX; }

	
	/** Set the Y grid step.
	 * @param stepY the step Y */
	public final void setStepY(final double stepY) { this.stepY = stepY; }

	
	/** Get the Y grid step.
	 * @return the step Y */
	public final double getStepY() { return stepY; }

	
	/** Specify whether the X legend is automatically set.
	 * @param autoLegendX the auto Legend X */
	public final void setAutoLegendX(final boolean autoLegendX) { this.autoLegendX = autoLegendX; }

	
	/** Get whether the X legend is automatically set.
	 * @return is auto Legend X */
	public final boolean isAutoLegendX() { return autoLegendX; }

	
	/** Specify whether the Y legend is automatically set.
	 * @param autoLegendY the auto Legend Y */
	public final void setAutoLegendY(final boolean autoLegendY) { this.autoLegendY = autoLegendY; }

	
	/** Get whether the Y legend is automatically set.
	 * @return is auto Legend Y */
	public final boolean isAutoLegendY() { return autoLegendY; }

	
	/** Set the X legend.
	 * @param legendX the legend X */
	public final void setLegendX(final String legendX) { this.legendX = legendX; }

	
	/** Get the X legend.
	 * @return the legend X */
	public final String getLegendX() { return STTools.isEmpty(legendX) ? STTools.SPACE : legendX; }

	
	/** Set the Y legend.
	 * @param legendY the legend Y */
	public final void setLegendY(final String legendY) { this.legendY = legendY; }

	
	/** Get the Y legend.
	 * @return the legend Y */
	public final String getLegendY() { return STTools.isEmpty(legendY) ? STTools.SPACE : legendY; }

	
	/** Specify whether the X scale is logarithmic instead of linear.
	 * @param logScaleX the log scale X */
	public final void setLogScaleX(final boolean logScaleX) { this.logScaleX = logScaleX; }

	
	/** Get whether the X scale is logarithmic instead of linear.
	 * @return the log scale X */
	public final boolean isLogScaleX() { return logScaleX; }

	
	/** Specify whether the Y scale is logarithmic instead of linear.
	 * @param logScaleY the log scale Y */
	public final void setLogScaleY(final boolean logScaleY) { this.logScaleY = logScaleY; }

	
	/** Get whether the Y scale is logarithmic instead of linear.
	 * @return the log scale Y */
	public final boolean isLogScaleY() { return logScaleY; }


	/** Compute the current size of the plot area and the corresponding conversion factors for coordinate transformations. */
	private void normalizeCoordinates() {
		scaleX = (plotArea.getWidth() - 2 * plotMarginSize) / rectangle.getWidth();
		shiftX = plotMarginSize - scaleX * rectangle.getMinX();
		scaleY = -(plotArea.getHeight() - 2 * plotMarginSize) / rectangle.getHeight();
		shiftY = (plotArea.getHeight() - plotMarginSize) - scaleY * rectangle.getMinY();
		if(isIsometric()) {
			double rectRatio = rectangle.getHeight() / rectangle.getWidth();
			double plotRatio = (double)plotArea.getHeight() / (double)plotArea.getWidth();
			if(rectRatio >= plotRatio) {
				scaleX = ((plotArea.getWidth() - 2 * plotMarginSize) / rectangle.getWidth()) / (rectRatio / plotRatio);
				rectangle.width *= rectRatio / plotRatio;
			} else {
				scaleY = (-(plotArea.getHeight() - 2 * plotMarginSize) / rectangle.getHeight()) * (rectRatio / plotRatio);
				rectangle.height /= rectRatio / plotRatio;
			}
		}
	}


	/** Convert a coordinate pair from the function space to the plot area.
	 * @param xValue the x value
	 * @param yValue the y value
	 * @return point */
	private Point transformCoordinates(final Object xValue, final Object yValue) {
		if(xValue == null || yValue == null) { return null; }
		Tensor x = (Tensor)xValue;
		Tensor y = (Tensor)yValue;
		if(!x.isNumber() || !y.isNumber()) { return null; }
		return new Point((int)(scaleX * x.getValue() + shiftX), (int)(scaleY * y.getValue() + shiftY));
	}


	/** Convert a coordinate pair from the function space to the plot area.
	 * @param xValue the x value
	 * @param yValue the y value
	 * @return point */
	/*
    private Point transformCoordinates(final double xValue, final double yValue) {
        if(!STData.isNumber(xValue) || !STData.isNumber(yValue)) { return null; }
        return new Point((int)(scaleX * xValue + shiftX), (int)(scaleY * yValue + shiftY));
    }
	 */


	/** Convert the y coordinate, from the function space to the plot area.
	 * @param value the value
	 * @return yPoint */
	private int transformYCoordinate(final double value) {
		if(!STData.isNumber(value)) { return 0; }
		return (int)(scaleY * value + shiftY);
	}


	/** Convert a coordinate pair, from the plot area to the function space.
	 * @param xValue the x value
	 * @param yValue the y value
	 * @return point */
	private Point.Double antiTransformCoordinates(final int xValue, final int yValue) { return new Point.Double((xValue - shiftX) / scaleX, (yValue - shiftY) / scaleY); }


	/** Paint the component.
	 * @param g the g */
	@Override
	public final void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if(isIsometric() || isAutoScaleX() || isAutoScaleY()) {
			try {
				double minX = Double.MAX_VALUE;
				double minY = Double.MAX_VALUE;
				double maxX = Double.MIN_VALUE;
				double maxY = Double.MIN_VALUE;
				Tensor ttemp;
				double temp;
				List<Tensor> lx;
				List<Tensor> ly;

				if(isLogScaleX()) { // convert the X values of all series to log scale
					AbstractList<Tensor> llx;
					for(XYSeries series : seriesCollection) {
						if((lx = series.getX()) != null) {
							llx = new Vector<Tensor>();
							Iterator<Tensor> xValuesI = lx.iterator();
							while(xValuesI.hasNext()) {
								ttemp = (Tensor)xValuesI.next();
								if(ttemp != null && ttemp.isNumber()) {
									temp = ttemp.getValue();
									llx.add(Tensor.newScalar((temp > 0) ? Math.log10(temp) : 0.0));
								}
							}
							series.setX(llx);
						}
					}
				}
				if(isLogScaleY()) { // convert the Y values of all series to log scale
					AbstractList<Tensor> lly;
					for(XYSeries series : seriesCollection) {
						if((ly = series.getY()) != null) {
							lly = new Vector<Tensor>();
							Iterator<Tensor> yValuesI = ly.iterator();
							while(yValuesI.hasNext()) {
								ttemp = (Tensor)yValuesI.next();
								if(ttemp != null && ttemp.isNumber()) {
									temp = ttemp.getValue();
									lly.add(Tensor.newScalar((temp > 0) ? Math.log10(temp) : 0.0));
								}
							}
							series.setY(lly);
						}
					}
				}

				for(XYSeries series : seriesCollection) { // search "absolute" min and max values for both x and y vectors in all series
					if(((lx = series.getX()) != null) && ((ly = series.getY()) != null)) {
						Iterator<Tensor> xValuesI = lx.iterator();
						Iterator<Tensor> yValuesI = ly.iterator();
						while(xValuesI.hasNext() && yValuesI.hasNext()) {
							ttemp = (Tensor)xValuesI.next();
							if(ttemp != null && ttemp.isNumber()) {
								temp = ttemp.getValue();
								if(temp < minX) { minX = temp; }
								if(temp > maxX) { maxX = temp; }
							}
							ttemp = (Tensor)yValuesI.next();
							if(ttemp != null && ttemp.isNumber()) {
								temp = ttemp.getValue();
								if(temp < minY) { minY = temp; }
								if(temp > maxY) { maxY = temp; }
							}
						}
					}
				}
				if(maxX < minX) {
					minX = 0.0; maxX = 1.0; 
				} else {
					if(isIsometric() || isAutoScaleX()) {
						if((maxX - minX) < SENSIBILITY_THRESHOLD) { minX -= 0.5; maxX += 0.5; }
						setRectangleX(minX, maxX);
					}
				}
				if(maxY < minY) {
					minY = 0.0; maxY = 1.0; 
				} else {
					if(isIsometric() || isAutoScaleY()) {
						if((maxY - minY) < SENSIBILITY_THRESHOLD) { minY -= 0.5; maxY += 0.5; }
						setRectangleY(minY, maxY);
					}
				}
			} catch (Exception e) { ; }
		}
		normalizeCoordinates();
		scaleXData = setScale(rectangle.getMinX(), rectangle.getMaxX(), isAutoScaleX() ? 0.0 : getStepX());
		scaleYData = setScale(rectangle.getMinY(), rectangle.getMaxY(), isAutoScaleY() ? 0.0 : getStepY());
	}


	/** Scale factor data. */
	private class ScaleData implements Serializable {
		/** The first point. */
		private int firstPoint;
		/** The scale factor. */
		private double scaleFactor;
		/** The num step. */
		private int numSteps;


		/** Class constructor.
		 * @param firstPoint the first point
		 * @param scaleFactor the scale factor
		 * @param numStep the num step */
		ScaleData(final int firstPoint, final double scaleFactor, final int numStep) {
			this.firstPoint = firstPoint;
			this.scaleFactor = scaleFactor;
			this.numSteps = numStep;
		}
	}


	/** Set the scale factors.
	 * @param theMin the min value
	 * @param theMax the max value
	 * @param theStep the step value (0.0 if it must be computed, i.e., for autoscale)
	 * @return scale data */
	private ScaleData setScale(final double theMin, final double theMax, final double theStep) {
		int s1;
		double k;
		int numStep;
		if(theStep == 0.0) {
			int ord = (int)Math.round(Math.log10(theMax - theMin)); // order of magnitude of the range
			k = Math.pow(10, ord); // estimated increment value
			// first guess
			s1 = (int)(theMin / k); if(theMin > 0 && theMin != (int)theMin) { s1++; }
			int s2 = (int)(theMax / k); if(theMax < 0 && theMax != (int)theMax) { s2--; }
			numStep = 1 + s2 - s1;
			if(numStep < 4) { // second guess, if required...
				k /= 10;
				s1 = (int)(theMin / k); if(theMin > 0 && theMin != (int)theMin) { s1++; }
				s2 = (int)(theMax / k); if(theMax < 0 && theMax != (int)theMax) { s2--; }
				numStep = 1 + s2 - s1;
			}
			while(numStep > 10) { // third guess, if required...
				k *= 2;
				s1 = (int)(theMin / k); if(theMin > 0 && theMin != (int)theMin) { s1++; }
				s2 = (int)(theMax / k); if(theMax < 0 && theMax != (int)theMax) { s2--; }
				numStep = 1 + s2 - s1;
			}
		} else {
			k = theStep; // specified increment value
			s1 = (int)(theMin / k); if(theMin > 0 && theMin != (int)theMin) { s1++; }
			int s2 = (int)(theMax / k); if(theMax < 0 && theMax != (int)theMax) { s2--; }
			numStep = 1 + s2 - s1;
		}
		return new ScaleData(s1, k, numStep);
	}


	/** Draw an horizontal dotted line.
	 * @param g the g
	 * @param x0 the x0
	 * @param x1 the x1
	 * @param y0 the y0 */
	private void drawHDottedLine(final Graphics g, final int x0, final int x1, final int y0) {
		int x = x0;
		while(x <= x1) {
			g.drawLine(x, y0, x + 2, y0);
			x += 6;
		}
	}


	/** Draw a vertical dotted line.
	 * @param g the g
	 * @param y0 the y0
	 * @param y1 the y1
	 * @param x0 the x0 */
	private void drawVDottedLine(final Graphics g, final int y0, final int y1, final int x0) {
		int y = y0;
		while(y <= y1) {
			g.drawLine(x0, y, x0, y + 2);
			y += 6;
		}
	}


	/** Format a number.
	 * @param num the num
	 * @return result */
	private String formatNumber(final double num) {
		double num2 = num;
		double absnum = Math.abs(num2);
		if(absnum < 0.00001) { absnum = 0.0; num2 = 0.0; }
		return (absnum == 0.0 || (absnum >= 0.01 && absnum < 1000)) ? myFormatter1.format(num2) : myFormatter2.format(num2);
	}


	/** Plot panel. */
	private class PlotPanel extends JPanel implements MouseMotionListener {

		/** Class constructor. */
		PlotPanel() { addMouseMotionListener(this); }


		/** Mouse moved.
		 * @param e the event */
		public void mouseMoved(final MouseEvent e) { 
			Point.Double p = antiTransformCoordinates(e.getX(), e.getY());
			setToolTipText(formatNumber(p.getX()) + "," + formatNumber(p.getY())); //$NON-NLS-1$
		}


		/** Mouse dragged.
		 * @param e the event */
		public void mouseDragged(final MouseEvent e) { ; } 


		/** Paint.
		 * @param g the g */
		@Override
		public void paint(final Graphics g) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			if(rectangle == null || scaleXData == null || scaleYData == null) { return; }
			if(yGrid == ChartPanel.ONLY_AXIS) {
				/*
                g.setColor(Color.LIGHT_GRAY);
                Point p1 = transformCoordinates(rectangle.getMinX(), 0.0);
                Point p2 = transformCoordinates(rectangle.getMaxX(), 0.0);
                drawHDottedLine(g, p1.x, p2.x, p2.y);
				 */
			} else if(yGrid == ChartPanel.GRID && rectangle.getMinY() < rectangle.getMaxY()) {
				g.setColor(gridColor);
				int s1 = scaleYData.firstPoint;
				double k = scaleYData.scaleFactor;
				int numStep = scaleYData.numSteps;
				double ss1 = s1 * k;
				double lScale = (getHeight() - (2 * plotMarginSize)) / rectangle.getHeight();
				double lShift = plotMarginSize - lScale * rectangle.getMinY();
				double pos = getHeight() - (lScale * ss1 + lShift);
				double deltaPos = lScale * k;
				int ppos;
				for(int i = 0; i < numStep; i++) {
					ppos = (int)pos;
					drawHDottedLine(g, 0, getWidth(), ppos);
					pos -= deltaPos;
				}
			}
			if(xGrid == ChartPanel.ONLY_AXIS) {
				/*
                g.setColor(Color.LIGHT_GRAY);
                Point p1 = transformCoordinates(0.0, rectangle.getMinY());
                Point p2 = transformCoordinates(0.0, rectangle.getMaxY());
                drawVDottedLine(g, p1.y, p2.y, p1.x);
				 */
			} else if(xGrid == ChartPanel.GRID && rectangle.getMinX() < rectangle.getMaxX()) {
				g.setColor(gridColor);
				int s1 = scaleXData.firstPoint;
				double k = scaleXData.scaleFactor;
				int numStep = scaleXData.numSteps;
				double ss1 = s1 * k;
				double lScale = (getWidth() - (2 * plotMarginSize)) / rectangle.getWidth();
				double lShift = plotMarginSize - lScale * rectangle.getMinX();
				double pos = lScale * ss1 + lShift;
				double deltaPos = lScale * k;
				int ppos;
				for(int i = 0; i < numStep; i++) {
					ppos = (int)pos;
					drawVDottedLine(g, 0, getHeight(), ppos);
					pos += deltaPos;
				}
			}
			for(XYSeries series : seriesCollection) { series.plot(g); }
		}
	}


	/** X (i.e., horizontal, bottom) panel. */
	class XStripPanel extends JPanel {

		/** Paint.
		 * @param gg the gg */
		@Override
		public void paint(final Graphics gg) {
			if(scaleXData == null) { return; } // protection against incomplete initialization
			TextLayout layout;
			Graphics2D g = (Graphics2D)gg;
			FontRenderContext frc = g.getFontRenderContext();
			g.setColor(new Color(0.9F, 0.9F, 0.9F));
			g.fillRect(0, 0, getWidth(), getHeight());
			// draw the scale
			g.setColor(Color.GRAY);
			if((xGrid == ChartPanel.ONLY_AXIS || xGrid == ChartPanel.GRID) && rectangle.getMinX() < rectangle.getMaxX()) {
				int s1 = scaleXData.firstPoint;
				double k = scaleXData.scaleFactor;
				int numSteps = scaleXData.numSteps;
				double ss1 = s1 * k;
				double lScale = ((getWidth() - yStripSize) - 2 * plotMarginSize) / rectangle.getWidth();
				double lShift = yStripSize + plotMarginSize - lScale * rectangle.getMinX();
				double pos = lScale * ss1 + lShift;
				double deltaPos = lScale * k;
				int ppos;
				String n = !isLogScaleX() ? STTools.BLANK : "10^"; //$NON-NLS-1$
				for(int i = 0; i < numSteps; i++) {
					ppos = (int)pos;
					g.drawLine(ppos, 0, ppos, tickSize);
					layout = new TextLayout(n + formatNumber(ss1), defaultFont.deriveFont(fontSize), frc);
					layout.draw(g, (float)(ppos - layout.getBounds().getWidth() / 2), tickSize + fontSize); // centered
					pos += deltaPos;
					ss1 += k;
				}
			}
			// draw the legend
			gg.setFont(defaultFont.deriveFont(fontSize));
			if(!autoLegendX) {
				g.setColor(Color.GRAY);
				layout = new TextLayout(getLegendX(), defaultFont.deriveFont(fontSize), frc);
				layout.draw(g, (float)(xStripSize + (getWidth() - layout.getBounds().getWidth()) / 2), xStripSize - 5); // horizontally centered
			} else {
				String name = null;
				if(getNumSeries() == 1) {
					if((name = getSeries(0).getXName()) != null) {
						g.setColor(Color.GRAY);
						layout = new TextLayout(name, defaultFont.deriveFont(fontSize), frc);
						layout.draw(g, (float)(xStripSize + (getWidth() - layout.getBounds().getWidth()) / 2), xStripSize - 5); // horizontally centered
					}
				} else if(getNumSeries() > 1) {
					String name2 = STTools.BLANK;
					boolean allTheSame = true;
					if(getSeries(0).getXName() == null) { getSeries(0).setXName(STTools.BLANK); }
					name = getSeries(0).getXName();
					for(int i = 0; i < getNumSeries(); i++) {
						if((name2 = getSeries(i).getXName()) != null) { allTheSame = allTheSame && (name.equals(name2)); }
					}
					if(allTheSame) {
						g.setColor(Color.GRAY);
						layout = new TextLayout(name, defaultFont.deriveFont(fontSize), frc);
						layout.draw(g, (float)(xStripSize + (getWidth() - layout.getBounds().getWidth()) / 2), xStripSize - 5); // horizontally centered
					} else {
						TextLayout[] layout2 = new TextLayout[getNumSeries()];
						int size = 0;
						for(int i = 0; i < getNumSeries(); i++) {
							if((name = getSeries(i).getXName()) != null) {
								layout2[i] = new TextLayout(name, defaultFont.deriveFont(fontSize), frc);
								size += layout2[i].getBounds().getWidth() + 15;
							}
						}

						ArrayList<Color> legendColors = new ArrayList<Color>();
						XYSeries series;
						for(int i = 0; i < getNumSeries(); i++) {
							series = getSeries(i);
							if(series.isShowLine()) {
								legendColors.add(series.getLineColor());
							} else if(series.isShowDots()) {
								legendColors.add(series.getDotsColor());
							} else if(series.isShowBars()) {
								legendColors.add(series.getBarsColor());
							}
						}

						float xPos = xStripSize + (getWidth() - size) / 2;
						for(int i = 0; i < getNumSeries(); i++) {
							if((name = getSeries(i).getXName()) != null) {
								//g.setColor(getSeries(i).getDotsColor());
								g.setColor(legendColors.get(i));
								layout2[i].draw(g, xPos, xStripSize - 5); // horizontally centered
								xPos += layout2[i].getBounds().getWidth() + 15;
							}
						}
					}
				}
			}
		}
	}


	/** Y (i.e., vertical, left) panel. */
	class YStripPanel extends JPanel {

		/** Paint.
		 * @param gg the gg */
		@Override
		public void paint(final Graphics gg) {
			if(scaleYData == null) { return; } // protection against incomplete initialization
			TextLayout layout;
			Graphics2D g = (Graphics2D)gg;
			FontRenderContext frc = g.getFontRenderContext();
			// draw the background
			g.setColor(new Color(0.9F, 0.9F, 0.9F));
			g.fillRect(0, 0, getWidth(), getHeight());
			// draw the scale: (int)(scaleY * value + shiftY);
			g.setColor(Color.GRAY);
			if(yGrid == ChartPanel.ONLY_AXIS || yGrid == ChartPanel.GRID) {
				int s1 = scaleYData.firstPoint;
				double k = scaleYData.scaleFactor;
				int numSteps = scaleYData.numSteps;
				double ss1 = s1 * k;
				double lScale = (getHeight() - (2 * plotMarginSize)) / rectangle.getHeight();
				double lShift = plotMarginSize - lScale * rectangle.getMinY();
				double pos = getHeight() - (lScale * ss1 + lShift);
				double deltaPos = lScale * k;
				int ppos;
				String n = !logScaleY ? STTools.BLANK : "10^"; //$NON-NLS-1$
				for(int i = 0; i < numSteps; i++) {
					ppos = (int)pos;
					g.drawLine(yStripSize - tickSize, ppos, yStripSize, ppos);
					layout = new TextLayout(n + formatNumber(ss1), defaultFont.deriveFont(fontSize), frc);
					layout.draw(g, (float)(yStripSize - (fontSize + layout.getBounds().getWidth())), ppos + tickSize); // right aligned
					pos -= deltaPos;
					ss1 += k;
				}
			}
			// draw the legend
			gg.setFont(defaultFont.deriveFont(fontSize));
			g.rotate(-Math.toRadians(90));
			if(!autoLegendY) {
				g.setColor(Color.GRAY);
				layout = new TextLayout(getLegendY(), defaultFont.deriveFont(fontSize), frc);
				layout.draw(g, (float)(-((getHeight() + layout.getBounds().getWidth()) / 2)), 10f); // vertically centered
			} else {
				String name = null;
				if(getNumSeries() == 1) {
					if((name = getSeries(0).getYName()) != null) {
						g.setColor(Color.GRAY);
						layout = new TextLayout(name, defaultFont.deriveFont(fontSize), frc);
						layout.draw(g, (float)(-((getHeight() + layout.getBounds().getWidth()) / 2)), 10f); // vertically centered
					}
				} else if(getNumSeries() > 1) {
					TextLayout[] layout2 = new TextLayout[getNumSeries()];
					int size = 0;
					for(int i = 0; i < getNumSeries(); i++) {
						if((name = getSeries(i).getYName()) != null) {
							layout2[i] = new TextLayout(name, defaultFont.deriveFont(fontSize), frc);
							size += layout2[i].getBounds().getWidth() + 15;
						}
					}

					ArrayList<Color> legendColors = new ArrayList<Color>();
					XYSeries series;
					for(int i = 0; i < getNumSeries(); i++) {
						series = getSeries(i);
						if(series.isShowLine()) {
							legendColors.add(series.getLineColor());
						} else if(series.isShowDots()) {
							legendColors.add(series.getDotsColor());
						} else if(series.isShowBars()) {
							legendColors.add(series.getBarsColor());
						}
					}

					float yPos = -((getHeight() + size) / 2);
					for(int i = 0; i < getNumSeries(); i++) {
						if((name = getSeries(i).getYName()) != null) {
							//g.setColor(getSeries(i).getDotsColor());
							g.setColor(legendColors.get(i));
							layout2[i].draw(g, yPos, 10f); // vertically centered
							yPos += layout2[i].getBounds().getWidth() + 15;
						}
					}
				}
			}
			g.rotate(Math.toRadians(90));
		}
	}


	/** XYSeries class. */
	public class XYSeries implements Serializable {
		/** The panel. */
		private ChartPanel panel;
		/** The x. */
		private transient AbstractList<Tensor> x;
		/** The y. */
		private transient AbstractList<Tensor> y;
		/** The x name. */
		private String xName;
		/** The y name. */
		private String yName;
		/** The dots type. */
		private String dotsType;

		/** The show dots. */
		private boolean showDots;
		/** The dots style. */
		private String dotsStyle;
		/** The dots size. */
		private transient AbstractList<?> dotsSize;
		/** The dots color. */
		private Color dotsColor;
		/** The sprite scale along X axis. */
		private transient AbstractList<?> dotsXScale;
		/** The sprite scale along Y axis. */
		private transient AbstractList<?> dotsYScale;
		/** The sprite angle. */
		private transient AbstractList<?> dotsAngle;
		/** The last point only. */
		private boolean dotsLastOnly;
		/** The highlight last point. */
		private boolean dotsHiLast;
		/** The show line. */
		private boolean showLine;
		/** The line style. */
		private String lineStyle;
		/** The line width. */
		private transient AbstractList<?> lineWidth;
		/** The line color. */
		private Color lineColor;
		/** The show bars. */
		private boolean showBars;
		/** The bars width. */
		private transient AbstractList<?> barsWidth;
		/** The bars color. */
		private Color barsColor;

		/** The sprite. */
		private Image sprite = null;
		/** The sprite width. */
		private int spriteW;
		/** The sprite height. */
		private int spriteH;
		/** The sprite half width. */
		private int spriteW2;
		/** The sprite half height. */
		private int spriteH2;


		/** Class constructor.
		 * @param panel the panel */
		public XYSeries(final ChartPanel panel) { this.panel = panel; }


		/** Class constructor.
		 * @param panel the panel
		 * @param x the x
		 * @param y the y
		 * @param xName the x name
		 * @param yName the y name */
		public XYSeries(final ChartPanel panel, final AbstractList<Tensor> x, final AbstractList<Tensor> y, final String xName, final String yName) {
			this(panel);
			this.x = x;
			this.y = y;
			this.xName = xName;
			this.yName = yName;
		}


		/** Set x.
		 * @param x the x */
		public final void setX(final AbstractList<Tensor> x) { this.x = x; }

		
		/** Get x.
		 * @return x */
		public final AbstractList<Tensor> getX() { return x; }

		
		/** Set y.
		 * @param y the y */
		public final void setY(final AbstractList<Tensor> y) { this.y = y; }

		
		/** Get y.
		 * @return y */
		public final AbstractList<Tensor> getY() { return y; }

		
		/** Set x name.
		 * @param xName the x name */
		public final void setXName(final String xName) { this.xName = xName; }

		
		/** Get x name.
		 * @return xName */
		public final String getXName() { return xName; }

		
		/** Set y name.
		 * @param yName the y name */
		public final void setYName(final String yName) { this.yName = yName; }

		
		/** Get y name.
		 * @return yName */
		public final String getYName() { return yName; }

		
		/** Set dots type.
		 * @param type the type */
		public final void setDotsType(final String type) { this.dotsType = type; }

		
		/** Get dots type.
		 * @return type */
		public final String getDotsType() { return dotsType; }

		
		/** Set show dots.
		 * @param show the show */
		public final void setShowDots(final boolean show) { this.showDots = show; }

		
		/** Is show dots.
		 * @return show */
		public final boolean isShowDots() { return showDots; }

		
		/** Set dots style.
		 * @param style the style */
		public final void setDotsStyle(final String style) { this.dotsStyle = style; }

		
		/** Get dots style.
		 * @return style */
		public final String getDotsStyle() { return dotsStyle; }

		
		/** Set dots size.
		 * @param size the size */
		public final void setDotsSize(final AbstractList<?> size) { this.dotsSize = size; }

		
		/** Get dots size.
		 * @return size */
		public final AbstractList<?> getDotsSize() { return dotsSize; }

		
		/** Set dots color.
		 * @param color the color */
		public final void setDotsColor(final Color color) { this.dotsColor = color; }

		
		/** Get dots color.
		 * @return color */
		public final Color getDotsColor() { return dotsColor; }

		
		/** Set the sprite scale along the X axis.
		 * @param scale the scale */
		public final void setDotsXScale(final AbstractList<?> scale) { this.dotsXScale = scale; }

		
		/** Get the sprite scale along the X axis.
		 * @return scale */
		public final AbstractList<?> getDotsXScale() { return dotsXScale; }

		
		/** Set the sprite scale along the Y axis.
		 * @param scale the scale */
		public final void setDotsYScale(final AbstractList<?> scale) { this.dotsYScale = scale; }

		
		/** Get the sprite scale along the Y axis.
		 * @return scale */
		public final AbstractList<?> getDotsYScale() { return dotsYScale; }

		
		/** Set the sprite angle.
		 * @param angle the angle */
		public final void setDotsAngle(final AbstractList<?> angle) { this.dotsAngle = angle; }

		
		/** Get the sprite angle.
		 * @return angle */
		public final AbstractList<?> getDotsAngle() { return dotsAngle; }

		
		/** Set dots last only.
		 * @param lastOnly the last only */
		public final void setDotsLastOnly(final boolean lastOnly) { this.dotsLastOnly = lastOnly; }

		
		/** Is dots last only.
		 * @return lastOnly */
		public final boolean isDotsLastOnly() { return dotsLastOnly; }

		
		/** Set dots highlight last.
		 * @param hiLast the hi last
		 */
		public final void setDotsHiLast(final boolean hiLast) { this.dotsHiLast = hiLast; }

		
		/** Is dots highlight last.
		 * @return hiLast */
		public final boolean isDotsHiLast() { return dotsHiLast; }

		
		/** Set show line.
		 * @param show the show */
		public final void setShowLine(final boolean show) { this.showLine = show; }

		
		/** Is show line.
		 * @return show */
		public final boolean isShowLine() { return showLine; }

		
		/** Set line style.
		 * @param style the style */
		public final void setLineStyle(final String style) { this.lineStyle = style; }

		
		/** Get line style.
		 * @return style */
		public final String getLineStyle() { return lineStyle; }

		
		/** Set line width.
		 * @param width the width */
		public final void setLineWidth(final AbstractList<?> width) { this.lineWidth = width; }

		
		/** Get line width.
		 * @return width */
		public final AbstractList<?> getLineWidth() { return lineWidth; }

		
		/** Set line color.
		 * @param color the color */
		public final void setLineColor(final Color color) { this.lineColor = color; }

		
		/** Get line color.
		 * @return color */
		public final Color getLineColor() { return lineColor; }

		
		/** Set show bars.
		 * @param show the show */
		public final void setShowBars(final boolean show) { this.showBars = show; }

		
		/** Is show bars.
		 * @return show */
		public final boolean isShowBars() { return showBars; }

		
		/** Set bars width.
		 * @param width the width */
		public final void setBarsWidth(final AbstractList<?> width) { this.barsWidth = width; }

		
		/** Get bars width.
		 * @return width */
		public final AbstractList<?> getBarsWidth() { return barsWidth; }

		
		/** Set bars color.
		 * @param color the color */
		public final void setBarsColor(final Color color) { this.barsColor = color; }

		
		/** Get bars color.
		 * @return color */
		public final Color getBarsColor() { return barsColor; }


		/** Set the sprite.
		 * @param id the sprite id */
		public final void setSprite(final String id) {
			if(dotsType.equals(ChartWidget.DOT)) { return; }
			if(dotsType.equals(ChartWidget.SYSTEM_SPRITE)) {
				sprite = new ImageIcon(STGraph.getSTC().getBaseIcon(id.substring(2))).getImage();
			} else {
				String fileName = STGraph.getSTC().getCurrentGraph().contextName;
				if(fileName != null) { fileName += System.getProperty("file.separator") + id; } //$NON-NLS-1$
				if(fileName != null && new File(fileName).canRead()) {
					sprite = new ImageIcon(fileName).getImage();
				} else { // a default sprite...
					sprite = new ImageIcon(STGraph.getSTC().getBaseIcon("dot.png")).getImage(); //$NON-NLS-1$
				}
			}
			spriteW = sprite.getWidth(null);
			spriteH = sprite.getHeight(null);
			spriteW2 = spriteW / 2;
			spriteH2 = spriteH / 2;
		}


		/** Plot.
		 * @param g the g */
		public final void plot(final Graphics g) {
			if(x == null || y == null) { return; }
			int num = Math.min(x.size(), y.size());
			if(num == 0) { return; }
			Point p0, p;
			if(isIsometric()) { normalizeCoordinates(); }

			// first point
			p0 = panel.transformCoordinates(x.get(0), y.get(0));
			if(showBars) { drawBar(g, p0, 0); }
			if(showDots && (num == 1 || !dotsLastOnly)) { drawPoint(g, p0, 0); }

			if(num == 1) { return; }

			// further points
			for(int i = 1; i < num; i++) {
				p = panel.transformCoordinates(x.get(i), y.get(i));
				if(showBars) { drawBar(g, p, i); }
				if(showLine) { drawLine(g, p0, p, i);}
				if(showDots && !dotsLastOnly) {
					drawPoint(g, p, i);
					if(showLine) { drawPoint(g, p0, i - 1); } // so to guarantee that lines do not hide points...
				}
				p0 = p;
			}

			// last point only
			if(showDots && dotsLastOnly) {
				int last = num - 1;
				p = panel.transformCoordinates(x.get(last), y.get(last));
				drawPoint(g, p, last);
			}
		}


		/** Draw point, either dot or sprite.
		 * @param g the graphics
		 * @param p the point position
		 * @param index the index */
		private void drawPoint(final Graphics g, final Point p, final int index) {
			if(p == null) { return; }
			if(dotsType.equals(ChartWidget.DOT)) { // dot
				g.setColor(dotsColor);
				int size = (int)get(dotsSize, index);
				int size2 = size / 2;
				if(dotsStyle.equals(ChartWidget.DOTSTYLE_VALUES[0])) { // circle
					g.fillOval(p.x - size2, p.y - size2, size, size);
				} else if(dotsStyle.equals(ChartWidget.DOTSTYLE_VALUES[1])) { // square
					g.fillRect(p.x - size2, p.y - size2, size, size);
				} else if(dotsStyle.equals(ChartWidget.DOTSTYLE_VALUES[2])) { // rhombus
					g.fillPolygon(new int[]{p.x, p.x + size2, p.x, p.x - size2}, new int[]{p.y - size2, p.y, p.y + size2, p.y}, 4);
				} else if(dotsStyle.equals(ChartWidget.DOTSTYLE_VALUES[3])) { // cross
					g.drawLine(p.x - size2, p.y, p.x + size2, p.y); g.drawLine(p.x, p.y - size2, p.x, p.y + size2);
				} else { // just as default
					g.fillOval(p.x - size2, p.y - size2, size, size);
				}
			} else { // sprite
				double xScale = get(dotsXScale, index);
				double yScale = get(dotsYScale, index);
				double angle = get(dotsAngle, index);
				Graphics2D g2d = (Graphics2D)g;
				AffineTransform origXform = null;
				if(angle != 0) {
					origXform = g2d.getTransform();
					AffineTransform newXform = (AffineTransform)(origXform.clone());
					newXform.rotate(-angle, p.x, p.y);
					g2d.setTransform(newXform);
				}
				if(xScale == 1 && yScale == 1) {
					g2d.drawImage(sprite, p.x - spriteW2, p.y - spriteH2, null);
				} else {
					int currentW = (int)(spriteW * xScale);
					int currentH = (int)(spriteH * yScale);
					g2d.drawImage(sprite, p.x - currentW / 2, p.y - currentH / 2, currentW, currentH, null);
				}
				if(angle != 0) { g2d.setTransform(origXform); }
			}
		}


		/** Draw line.
		 * @param g the graphics
		 * @param p0 the first point position
		 * @param p the second point position
		 * @param index the index */
		private void drawLine(final Graphics g, final Point p0, final Point p, final int index) {
			if(p0 == null || p == null) { return; }
			g.setColor(lineColor);
			int width = (int)get(lineWidth, index);
			if(width == 1) {
				g.drawLine(p0.x, p0.y, p.x, p.y);
				return;
			}
			int width2 = width / 2;
			if(Math.abs(p0.x - p.x) < Math.abs(p0.y - p.y)) { // a trick to avoid computing angles for faster execution...
				g.fillPolygon(new int[]{p0.x - width2, p.x - width2, p.x + width, p0.x + width}, new int[]{p0.y, p.y, p.y, p0.y}, 4);
			} else {
				g.fillPolygon(new int[]{p0.x, p.x, p.x, p0.x}, new int[]{p0.y - width2, p.y - width2, p.y + width, p0.y + width}, 4);
			}
		}


		/** Draw bar.
		 * @param g the graphics
		 * @param p the second point position
		 * @param index the index */
		private void drawBar(final Graphics g, final Point p, final int index) {
			if(p == null) { return; }
			g.setColor(barsColor);
			int width = (int)get(barsWidth, index);
			if(width == 1) {
				g.drawLine(p.x, panel.transformYCoordinate(0.0), p.x, p.y);
				return;
			}
			int width2 = width / 2;
			int yy = panel.transformYCoordinate(0.0);
			g.fillPolygon(new int[]{p.x - width2, p.x - width2, p.x + width, p.x + width}, new int[]{p.y, yy, yy, p.y}, 4);
		}


		/** Helper.
		 * @param v the vector
		 * @param index the index
		 * @return value */
		private double get(final AbstractList<?> v, final int index) {
			if(v == null || v.size() == 0) { return 0.0; }
			return v.size() == 1 ? ((Tensor)v.get(0)).getValue() : ((Tensor)v.get(index)).getValue();
		}


		/** toString method.
		 * @return the string */
		@Override public final String toString() {
			StringBuffer ret = new StringBuffer();
			Iterator<Tensor> xValuesI = x.iterator();
			Iterator<Tensor> yValuesI = y.iterator();
			ret.append(STTools.OPENV);
			while(yValuesI.hasNext()) { ret.append(STTools.OPENP + xValuesI.next() + STTools.COMMA + yValuesI.next() + STTools.CLOSEP + STTools.SPACE); }
			ret.append(STTools.CLOSEV);
			return ret.toString();
		}

	}

}
