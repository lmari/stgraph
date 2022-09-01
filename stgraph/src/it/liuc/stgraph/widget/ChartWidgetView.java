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
package it.liuc.stgraph.widget;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.ChartPanel;
import it.liuc.stgraph.util.STTools;

import java.awt.Graphics;
import java.util.AbstractList;
import java.util.Vector;

import org.jgraph.graph.VertexRenderer;
import org.nfunk.jep.type.Matrix;
import org.nfunk.jep.type.Tensor;


/** View handler for the Chart widget. */
@SuppressWarnings("serial")
public class ChartWidgetView extends WidgetView {
	/** The widget. */
	private ChartWidget widget;


	/** Class constructor.
	 * @param widget the widget */
	public ChartWidgetView(final STWidget widget) {
		super(widget);
		setDefaultWidgetBounds(ChartWidget.PREFIX + ".WIDGETBOUNDS"); //$NON-NLS-1$
		this.widget = (ChartWidget)widget;
		setRenderer(new ChartWidgetRenderer());
	}


	/** Renderer. */
	public class ChartWidgetRenderer extends VertexRenderer {
		/** The graph. */
		private STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		/** The chart. */
		private ChartPanel chart = widget.getChart();
		/** The x data names. */
		private transient String[] xDataNames;
		/** The x data sources. */
		private transient STNode[] xDataSources;
		/** The y data names. */
		private transient String[] yDataNames;
		/** The y data sources. */
		private transient STNode[] yDataSources;
		/** The num series. */
		private int numSeries;
		/** The dots types. */
		private transient String[] dotsTypes;
		/** The series data. */
		private transient String[][] seriesData;


		/** Class constructor: initialize the chart. */
		public ChartWidgetRenderer() { initView(); }


		/** Init view. */
		public final void initView() {
			xDataNames = (String[])widget.getProperty(ChartWidget.PROP_XSOURCE_NA);
			yDataSources = (STNode[])widget.getProperty(ChartWidget.PROP_YSOURCE_OB);
			if(xDataNames != null && yDataSources != null) {
				xDataSources = (STNode[])widget.getProperty(ChartWidget.PROP_XSOURCE_OB);
				yDataNames = (String[])widget.getProperty(ChartWidget.PROP_YSOURCE_NA);
				numSeries = yDataSources.length;
			} else { numSeries = 0; }
			if(chart != null) { //TODO a crazy trick to refresh the chart and execute ChartPanel.defineSizeClass() (and it does not even works good...)
				Integer i1 = (Integer)widget.getProperty(ChartWidget.PROP_TYPEY);
				boolean b1 = i1 == null || i1.intValue() != ChartPanel.NOTHING;
				Integer i2 = (Integer)widget.getProperty(ChartWidget.PROP_TYPEX);
				boolean b2 = i2 == null || i2.intValue() != ChartPanel.NOTHING;
				chart.defineSizeClass(b1, b2);
				chart.setSize(chart.getWidth(), chart.getHeight() + 1);
				chart.setSize(chart.getWidth(), chart.getHeight() - 1);
				chart.componentResized(null);
			}
		}


		/** Paint method.
		 * @param g the g */
		@SuppressWarnings("rawtypes")
		@Override public final void paint(final Graphics g) {
			if(chart == null) { return; }
			if(widget.frame == null) { return; }

			//System.err.println(System.currentTimeMillis() + " " + graph.getState());
			//if(graph.getState() == STGraphImpl.STATE_PAUSED) { return; }
			//System.err.println("Continua...");

			Boolean im = (Boolean)widget.getProperty(ChartWidget.PROP_ISOMETRIC);
			chart.setIsometric((im != null) && im.booleanValue());

			Integer tx = (Integer)widget.getProperty(ChartWidget.PROP_TYPEX);
			if(tx == null) {
				chart.setVGrid(2);
			} else {
				chart.setVGrid(tx.intValue());
			}
			Boolean ax = (Boolean)widget.getProperty(ChartWidget.PROP_AUTOAXISX);
			boolean isAutoX = (ax == null) || ax.booleanValue();
			if(!isAutoX) {
				chart.setAutoScaleX(false);
				chart.setRectangleX(((Number)widget.getProperty(ChartWidget.PROP_MINX)).doubleValue(), ((Number)widget.getProperty(ChartWidget.PROP_MAXX)).doubleValue());
				chart.setStepX(((Number)widget.getProperty(ChartWidget.PROP_STEPX)).doubleValue());
			} else { chart.setAutoScaleX(true); }

			Integer ty = (Integer)widget.getProperty(ChartWidget.PROP_TYPEY);
			if(ty == null) {
				chart.setHGrid(2);
			} else {
				chart.setHGrid(ty.intValue());
			}
			Boolean ay = (Boolean)widget.getProperty(ChartWidget.PROP_AUTOAXISY);
			boolean isAutoY = (ay == null) || ay.booleanValue();
			if(!isAutoY) {
				chart.setAutoScaleY(false);
				chart.setRectangleY(((Number)widget.getProperty(ChartWidget.PROP_MINY)).doubleValue(), ((Number)widget.getProperty(ChartWidget.PROP_MAXY)).doubleValue());
				chart.setStepY(((Number)widget.getProperty(ChartWidget.PROP_STEPY)).doubleValue());
			} else { chart.setAutoScaleY(true); }

			Boolean lx = (Boolean)widget.getProperty(ChartWidget.PROP_AUTOLEGENDX);
			boolean isAutoLegendX = (lx == null) || lx.booleanValue();
			if(!isAutoLegendX) {
				chart.setAutoLegendX(false);
				chart.setLegendX((String)widget.getProperty(ChartWidget.PROP_LEGENDX));
			} else { chart.setAutoLegendX(true); }

			Boolean ly = (Boolean)widget.getProperty(ChartWidget.PROP_AUTOLEGENDY);
			boolean isAutoLegendY = (ly == null) || ly.booleanValue();
			if(!isAutoLegendY) {
				chart.setAutoLegendY(false);
				chart.setLegendY((String)widget.getProperty(ChartWidget.PROP_LEGENDY));
			} else { chart.setAutoLegendY(true); }

			Boolean lsx = (Boolean)widget.getProperty(ChartWidget.PROP_LOGSCALEX);
			chart.setLogScaleX((lsx != null) && lsx.booleanValue());

			Boolean lsy = (Boolean)widget.getProperty(ChartWidget.PROP_LOGSCALEY);
			chart.setLogScaleY((lsy != null) && lsy.booleanValue());

			dotsTypes = (String[])widget.getProperty(ChartWidget.PROP_DOTSTYPE);

			seriesData = new String[ChartWidget.PROP_SERIESDATA.length][];
			for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { seriesData[i] = (String[])widget.getProperty(ChartWidget.PROP_SERIESDATA[i]); }

			chart.removeAllSeries();

			STNode yNode;
			for(int i = 0; i < numSeries; i++) {
				try {
					yNode = yDataSources[i];
					if(yNode.isScalar()) {
						createSeries((Vector)yNode.getValueHistory(), i, 0, true);
					} else if(yNode.isVector()) {
						if(!yNode.isVectorOutput() || Boolean.parseBoolean(seriesData[7][i])) { //dotsLastOnly
							createSeries(((Tensor)yNode.getValue()).getVector(), i, 0, true);
						} else {
							Matrix m  = (Matrix)yNode.getValueHistory();
							for(int j = 0; j < m.getRowCount(); j++) { createSeries(m.getRow(j), i, j, j == 0); }
						}
					} else if(yNode.isMatrix()) {
						Tensor m  = (Tensor)yNode.getValue();
						for(int j = 0; j < m.getDimensions()[0]; j++) { createSeries(m.getSubTensor(j).getVector(), i, j, j == 0); }
					}
				} catch (Exception e) { ; }
			}
			if(g != null) { widget.frame.paint(g); }
		}


		/** Create a series, by guaranteeing that both the x and the y components are vectors.
		 * @param values the vector of y values
		 * @param i the index of the series
		 * @param subi the index of the sub-series (in case of x vector output)
		 * @param withYTitle should the series have a title for the y axis? */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void createSeries(final Vector values, final int i, final int subi, final boolean withYTitle) {
			ChartPanel.XYSeries series = widget.getChart().addSeries();
			series.setY(values);
			series.setYName(withYTitle ? yDataNames[i] : null);

			if(xDataNames[i].equals("vTime")) { //$NON-NLS-1$
				series.setX(graph.vTime.getVector());
				series.setXName(STTools.setAlternateText("vTime")); //$NON-NLS-1$
			} else if(xDataNames[i].equals("vIndex")) { //$NON-NLS-1$
				series.setX(graph.vIndex.getVector());
				series.setXName(STTools.setAlternateText("vIndex")); //$NON-NLS-1$
			} else {
				if(xDataSources[i].isScalar()) {
					series.setX((Vector)xDataSources[i].getValueHistory());
				} else if(xDataSources[i].isVector()) {
					if(!xDataSources[i].isVectorOutput()) {
						series.setX(((Tensor)xDataSources[i].getValue()).getVector());
					} else {
						Matrix m  = (Matrix)xDataSources[i].getValueHistory();
						series.setX(m.getRow(subi));
					}
				}
				series.setXName(xDataNames[i]);
			}

			// 0 showDots  1 dotsStyle  2 dotsSize  3 dotsColor  4 dotsXScale  5 dotsYScale  6 dotsAngle
			// 7 dotsLastOnly  8 dotsHiLast  9 showLine  10 lineStyle  11 lineWidth  12 lineColor
			// 13 showBars	14 barsWidth	15 barsColor
			series.setDotsType(dotsTypes[i]);
			series.setShowDots(Boolean.parseBoolean(seriesData[0][i]));
			series.setDotsStyle(seriesData[1][i]);
			if(dotsTypes[i].equals(ChartWidget.DOT)) {
				series.setDotsSize(genList(seriesData[2][i], Boolean.parseBoolean(seriesData[7][i])));
				series.setDotsColor(ChartWidget.COLOR_CODES[seekValue(ChartWidget.COLOR_VALUES, seriesData[3][i])]);
			} else {
				series.setDotsXScale(genList(seriesData[4][i], Boolean.parseBoolean(seriesData[7][i])));
				series.setDotsYScale(genList(seriesData[5][i], Boolean.parseBoolean(seriesData[7][i])));
				series.setDotsAngle(genList(seriesData[6][i], Boolean.parseBoolean(seriesData[7][i])));
				series.setSprite(seriesData[1][i]);
			}
			series.setDotsLastOnly(Boolean.parseBoolean(seriesData[7][i]));
			series.setDotsHiLast(Boolean.parseBoolean(seriesData[8][i]));
			series.setShowLine(Boolean.parseBoolean(seriesData[9][i]));
			series.setLineStyle(seriesData[10][i]);
			series.setLineWidth(genList(seriesData[11][i], Boolean.parseBoolean(seriesData[7][i])));
			series.setLineColor(ChartWidget.COLOR_CODES[seekValue(ChartWidget.COLOR_VALUES, seriesData[12][i])]);
			series.setShowBars(Boolean.parseBoolean(seriesData[13][i]));
			series.setBarsWidth(genList(seriesData[14][i], Boolean.parseBoolean(seriesData[7][i])));
			series.setBarsColor(ChartWidget.COLOR_CODES[seekValue(ChartWidget.COLOR_VALUES, seriesData[15][i])]);
		}


		/** Helper.
		 * @param s
		 * @param lastOnly
		 * @return */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private AbstractList genList(final String s, final boolean lastOnly) {
			if(s.startsWith(STTools.SHARP)) {
				Vector result = new Vector(1);
				result.add(Tensor.newScalar(Double.parseDouble(s.substring(1))));
				return result; 
			}
			STNode node = STGraph.getSTC().getCurrentGraph().getNodeByName(s);
			if(node.isScalar()) { return (Vector)node.getValueHistory(); }
			if(node.isVector()) {
				if(!node.isVectorOutput() || lastOnly) { return ((Tensor)node.getValue()).getVector(); }
				Matrix m  = (Matrix)node.getValueHistory();
				//for(int j = 0; j < m.getRowCount(); j++) { series.setScaleX(m.getRow(j)); } // what does it mean?
				return m.getRow(0);
			}
			if(node.isMatrix()) {
				Tensor m  = (Tensor)node.getValue();
				//for(int j = 0; j < m.getDimensions()[0]; j++) { series.setScaleX(m.getSubTensor(j).getVector()); } // what does it mean?
				return m.getSubTensor(0).getVector();
			}
			return null;
		}


		/** Helper.
		 * @param values the list
		 * @param key the key
		 * @return the index */
		private int seekValue(final String[] values, final String key) {
			for(int i = 0; i < values.length; i++) {
				if(values[i].equals(key)) { return i; }
			}
			return 0;
		}

	}

}
