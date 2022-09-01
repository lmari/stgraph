/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2018, Luca Mari.
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
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.ChartPanel;
import it.liuc.stgraph.util.STTools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;


/** Chart widget. */
public class ChartWidget extends STWidget {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The constant PREFIX. */
	public static final String PREFIX = "CHART"; //$NON-NLS-1$
	/** The constant ID. */
	public static final String ID = "ChartWidget"; //$NON-NLS-1$
	/** The constant PROP_XSOURCE_OB: array of x nodes. */
	public static final String PROP_XSOURCE_OB = "xsourceob"; //$NON-NLS-1$
	/** The constant PROP_XSOURCE_NA: array of x node names. */
	public static final String PROP_XSOURCE_NA = "xsourcena"; //$NON-NLS-1$
	/** The constant PROP_YSOURCE_OB: array of y nodes. */
	public static final String PROP_YSOURCE_OB = "ysourceob"; //$NON-NLS-1$
	/** The constant PROP_YSOURCE_NA: array of y node names. */
	public static final String PROP_YSOURCE_NA = "ysourcena"; //$NON-NLS-1$
	/** The constant PROP_DOTSTYPE: array of dots types. */
	public static final String PROP_DOTSTYPE = "dotstype"; //$NON-NLS-1$

	public static final String DOT = "dot"; //$NON-NLS-1$
	public static final String SYSTEM_SPRITE = "systemsprite"; //$NON-NLS-1$
	public static final String USER_SPRITE = "usersprite"; //$NON-NLS-1$

	public static final String[] PROP_SERIESDATA = new String[] { "showdots", "dotsstyles", "dotssizes", "dotscolors", 
		"dotsxscales", "dotsyscales", "dotsangles", "dotslastonly", "dotshilast",
		"showline", "linestyles", "linewidths", "linecolors", "showbars", "barswidths", "barscolors" };

	/** The constant DOTSTYLE_VALUES (they must orderly correspond to the CHART.DOTSTYLES in dialogs_*.properties). */
	public static final String[] DOTSTYLE_VALUES = new String[] { "__style1", "__style2", "__style3", "__style4" };
	/** The constant DEFSPRITE_VALUES (they must orderly correspond to the CHART.DEFSPRITES in dialogs_*.properties). */
	public static final String[] DEFSPRITE_VALUES = new String[] { "__rdot.png", "__bdot.png" };
	/** The constant LINESTYLES_VALUES (they must orderly correspond to the CHART.LINESTYLES in dialogs_*.properties). */
	public static final String[] LINESTYLE_VALUES = new String[] { "__style1", "__style2", "__style3" };
	/** The constant COLOR_VALUES (they must orderly correspond to the CHART.COLORNAMES in dialogs_*.properties). */
	public static final String[] COLOR_VALUES = new String[] { "__RED", "__GREEN", "__BLUE", "__BLACK", "__CYAN", "__MAGENTA", "__LIGHT_GRAY", "__GRAY", "__DARK_GRAY" };
	/** The constant COLOR_CODES (they must orderly correspond to the CHART.COLORNAMES in dialogs_*.properties). */
	public static final Color[] COLOR_CODES = { Color.RED, Color.GREEN, Color.BLUE, Color.BLACK, Color.CYAN, Color.MAGENTA, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY };

	/** The constant PROP_ISOMETRIC: boolean: is the chart isometric?. */
	public static final String PROP_ISOMETRIC = "isometric"; //$NON-NLS-1$
	/** The constant PROP_TYPEX: integer: type for the x axis. */
	public static final String PROP_TYPEX = "typex"; //$NON-NLS-1$
	/** The constant PROP_TYPEY: integer: type for the y axis. */
	public static final String PROP_TYPEY = "typey"; //$NON-NLS-1$
	/** The constant PROP_AUTOAXISX: boolean: is the x axis autoscaled?. */
	public static final String PROP_AUTOAXISX = "autoaxisx"; //$NON-NLS-1$
	/** The constant PROP_AUTOAXISY: boolean: is the y axis autoscaled?. */
	public static final String PROP_AUTOAXISY = "autoaxisy"; //$NON-NLS-1$
	/** The constant PROP_MINX: double: min value of x axis. */
	public static final String PROP_MINX = "minx"; //$NON-NLS-1$
	/** The constant PROP_STEPX: double: step value of x axis. */
	public static final String PROP_STEPX = "stepx"; //$NON-NLS-1$
	/** The constant PROP_MAXX: double: max value of x axis. */
	public static final String PROP_MAXX = "maxx"; //$NON-NLS-1$
	/** The constant PROP_MINY: double: min value of y axis. */
	public static final String PROP_MINY = "miny"; //$NON-NLS-1$
	/** The constant PROP_STEPY: double: step value of y axis. */
	public static final String PROP_STEPY = "stepy"; //$NON-NLS-1$
	/** The constant PROP_MAXY: double: max value of y axis. */
	public static final String PROP_MAXY = "maxy"; //$NON-NLS-1$
	/** The constant PROP_AUTOLEGENDX: boolean: has the x axis an autolegend?. */
	public static final String PROP_AUTOLEGENDX = "autolegendx"; //$NON-NLS-1$
	/** The constant PROP_AUTOLEGENDY: boolean: has the y axis an autolegend?. */
	public static final String PROP_AUTOLEGENDY = "autolegendy"; //$NON-NLS-1$
	/** The constant PROP_LEGENDX: text: legend of x axis. */
	public static final String PROP_LEGENDX = "legendx"; //$NON-NLS-1$
	/** The constant PROP_LEGENDY: text: legend of y axis. */
	public static final String PROP_LEGENDY = "legendy"; //$NON-NLS-1$
	/** The constant PROP_LOGSCALEX: boolean: has the x axis a log scale?. */
	public static final String PROP_LOGSCALEX = "logscalex"; //$NON-NLS-1$
	/** The constant PROP_LOGSCALEY: boolean: has the y axis a log scale?. */
	public static final String PROP_LOGSCALEY = "logscaley"; //$NON-NLS-1$

	/** The chart. */
	private final ChartPanel chart = new ChartPanel();


	/** Class constructor. */
	public ChartWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public ChartWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() { 
		if(view == null) { view = new ChartWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new ChartWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("CHART.LABEL"); } //$NON-NLS-1$


	/** Get the chart.
	 * @return chart */
	public final ChartPanel getChart() { return chart; }


	/** Set panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);

		chart.getPlotArea().addMouseListener(new MouseAdapter() { // required because the MouseMotionListener on the plotArea prevents the inheritance...
			@Override
			public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override
			public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		});

		panel.add(chart, BorderLayout.CENTER);
		setTitle();
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() { setTitle((String) getProperty(STWidget.PROP_TITLE)); }


	/** Set the properties of this widget.
	 * @param title the title
	 * @param numSeries the number of series
	 * @param xNames the x names
	 * @param yNames the y names
	 * @param dotsTypes the dots types
	 * @param seriesData the series data
	 * @param isometric the isometric
	 * @param typeX the type X
	 * @param autoX the auto X
	 * @param minX the min X
	 * @param stepX the step X
	 * @param maxX the max X
	 * @param typeY the type Y
	 * @param autoY the auto Y
	 * @param minY the min Y
	 * @param stepY the step Y
	 * @param maxY the max Y
	 * @param autoLegendX the auto legend X
	 * @param legendX the legend X
	 * @param autoLegendY the auto legend Y
	 * @param legendY the legend Y
	 * @param logscaleX the log scale X
	 * @param logscaleY the log scale Y
	 * @return result */
	final boolean setProps(final String title, final int numSeries, final String[] xNames, final String[] yNames, final String[] dotsTypes, final String[][] seriesData, final boolean isometric, final int typeX, final boolean autoX, final double minX, final double stepX, final double maxX, final int typeY, final boolean autoY, final double minY, final double stepY, final double maxY, final boolean autoLegendX, final String legendX, final boolean autoLegendY, final String legendY, final boolean logscaleX, final boolean logscaleY) {
		try {
			if(numSeries == 0) {
				setTitle();
				setProperty(ChartWidget.PROP_XSOURCE_NA, null);
				setProperty(ChartWidget.PROP_XSOURCE_OB, null);
				setProperty(ChartWidget.PROP_YSOURCE_NA, null);
				setProperty(ChartWidget.PROP_YSOURCE_OB, null);
				setProperty(ChartWidget.PROP_DOTSTYPE, null);
				for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { setProperty(ChartWidget.PROP_SERIESDATA[i], null); }
				return true;
			}
			STNode[] xSeries = new STNode[numSeries];
			STNode[] ySeries = new STNode[numSeries];
			STNode[] nodes = STGraph.getSTC().getCurrentGraph().getNodes();
			for(int i = 0; i < numSeries; i++) {
				boolean xFound = false;
				boolean yFound = false;
				if("vTime".equals(xNames[i]) || "vIndex".equals(xNames[i])) { //$NON-NLS-1$ //$NON-NLS-2$
					xSeries[i] = null;
					xFound = true;
				}
				for(int j = 0; j < nodes.length; j++) {
					String name = nodes[j].getName();
					if(!xFound && xNames[i].equals(name) && nodes[j].isOutput()) {
						xSeries[i] = nodes[j];
						xFound = true;
					}
					if(!yFound && yNames != null && yNames[i].equals(name) && nodes[j].isOutput()) {
						ySeries[i] = nodes[j];
						yFound = true;
					}
				}
			}
			setProperty(STWidget.PROP_TITLE, title);
			setProperty(ChartWidget.PROP_XSOURCE_NA, xNames);
			setProperty(ChartWidget.PROP_XSOURCE_OB, xSeries);
			setProperty(ChartWidget.PROP_YSOURCE_NA, yNames);
			setProperty(ChartWidget.PROP_YSOURCE_OB, ySeries);
			setProperty(ChartWidget.PROP_DOTSTYPE, dotsTypes);
			for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { setProperty(ChartWidget.PROP_SERIESDATA[i], seriesData[i]); }
			setProperty(ChartWidget.PROP_ISOMETRIC, Boolean.valueOf(isometric));
			setProperty(ChartWidget.PROP_TYPEX, Integer.valueOf(typeX));
			setProperty(ChartWidget.PROP_AUTOAXISX, Boolean.valueOf(autoX));
			setProperty(ChartWidget.PROP_MINX, Double.valueOf(minX));
			setProperty(ChartWidget.PROP_STEPX, Double.valueOf(stepX));
			setProperty(ChartWidget.PROP_MAXX, Double.valueOf(maxX));
			setProperty(ChartWidget.PROP_TYPEY, Integer.valueOf(typeY));
			setProperty(ChartWidget.PROP_AUTOAXISY, Boolean.valueOf(autoY));
			setProperty(ChartWidget.PROP_MINY, Double.valueOf(minY));
			setProperty(ChartWidget.PROP_STEPY, Double.valueOf(stepY));
			setProperty(ChartWidget.PROP_MAXY, Double.valueOf(maxY));
			setProperty(ChartWidget.PROP_AUTOLEGENDX, Boolean.valueOf(autoLegendX));
			setProperty(ChartWidget.PROP_LEGENDX, legendX);
			setProperty(ChartWidget.PROP_AUTOLEGENDY, Boolean.valueOf(autoLegendY));
			setProperty(ChartWidget.PROP_LEGENDY, legendY);
			setProperty(ChartWidget.PROP_LOGSCALEX, Boolean.valueOf(logscaleX));
			setProperty(ChartWidget.PROP_LOGSCALEY, Boolean.valueOf(logscaleY));
			((ChartWidgetView.ChartWidgetRenderer)view.getRenderer()).initView();
			setTitle();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_CHART_FORMAT")); //$NON-NLS-1$
			return false;
		}
		return true;
	}


	/** Check whether the renamed node is referenced by the graph, and in that case set the new name accordingly.
	 * @param newName the new name
	 * @param oldName the old name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		final String[] xNames = (String[])getProperty(ChartWidget.PROP_XSOURCE_NA);
		final String[] yNames = (String[])getProperty(ChartWidget.PROP_YSOURCE_NA);
		final String[][] seriesData = new String[ChartWidget.PROP_SERIESDATA.length][];
		for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { seriesData[i] = (String[])getProperty(ChartWidget.PROP_SERIESDATA[i]); }
		if(yNames != null) {
			for(int j = 0; j < yNames.length; j++) {
				if(oldName.equals(xNames[j])) { xNames[j] = newName; }
				if(oldName.equals(yNames[j])) { yNames[j] = newName; }
				for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) {
					try {
						if(oldName.equals(seriesData[i][j])) { seriesData[i][j] = newName; }
					} catch (Exception e) { ; }
				}
			}
			setProperty(ChartWidget.PROP_XSOURCE_NA, xNames);
			setProperty(ChartWidget.PROP_YSOURCE_NA, yNames);
			for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { setProperty(ChartWidget.PROP_SERIESDATA[i], seriesData[i]); }
		}
		((ChartWidgetView.ChartWidgetRenderer)view.getRenderer()).initView();
	}


	/** Handle the event of this widget removal. */
	public void handleWidgetRemoval() { ; }


	/** Check whether the removed node is referenced by the graph, and in that case set the name to <code>null</code>.
	 * @param name the name */
	@SuppressWarnings("unchecked")
	public final void handleNodeRemoval(final String name) {
		String[] xNames = (String[])getProperty(ChartWidget.PROP_XSOURCE_NA);
		STNode[] xSeries = (STNode[])getProperty(ChartWidget.PROP_XSOURCE_OB);
		String[] yNames = (String[])getProperty(ChartWidget.PROP_YSOURCE_NA);
		STNode[] ySeries = (STNode[])getProperty(ChartWidget.PROP_YSOURCE_OB);
		String[] dotsTypes = (String[])getProperty(ChartWidget.PROP_DOTSTYPE);
		final String[][] seriesData = new String[ChartWidget.PROP_SERIESDATA.length][];
		for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { seriesData[i] = (String[])getProperty(ChartWidget.PROP_SERIESDATA[i]); }
		if(yNames != null) {
			ArrayList<String> newXNames = new ArrayList<String>();
			ArrayList<STNode> newXSeries = new ArrayList<STNode>();
			ArrayList<String> newYNames = new ArrayList<String>();
			ArrayList<STNode> newYSeries = new ArrayList<STNode>();
			ArrayList<String> newDotsTypes = new ArrayList<String>();
			ArrayList<ArrayList<?>> newSeriesData = new ArrayList<ArrayList<?>>();
			for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { newSeriesData.add(new ArrayList<Object>()); }
			for(int j = 0; j < yNames.length; j++) {
				if(!name.equals(xNames[j]) && !name.equals(yNames[j])) {
					newXNames.add(xNames[j]);
					if(!xNames[j].equals("vIndex") && !xNames[j].equals("vTime")) { newXSeries.add(xSeries[j]); } //$NON-NLS-1$ //$NON-NLS-2$
					else { newXSeries.add(null); }
					newYNames.add(yNames[j]);
					if(!yNames[j].equals("vIndex") && !yNames[j].equals("vTime")) { newYSeries.add(ySeries[j]); } //$NON-NLS-1$ //$NON-NLS-2$
					else { newYSeries.add(null); }
					newDotsTypes.add(dotsTypes[j]);
					for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { ((ArrayList<String>)newSeriesData.get(i)).add(seriesData[i][j]); }
				}
			}
			int size = newYNames.size();
			if(size > 0) {
				String[] newXNames2 = new String[size];
				STNode[] newXSeries2 = new STNode[size];
				String[] newYNames2 = new String[size];
				STNode[] newYSeries2 = new STNode[size];
				String[] newDotsTypes2 = new String[size];
				String[][] newSeriesData2 = new String[ChartWidget.PROP_SERIESDATA.length][size];
				for(int j = 0; j < newYNames.size(); j++) {
					newXNames2[j] = (String)newXNames.get(j); newXSeries2[j] = (STNode)newXSeries.get(j);
					newYNames2[j] = (String)newYNames.get(j); newYSeries2[j] = (STNode)newYSeries.get(j);
					newDotsTypes2[j] = (String)newDotsTypes.get(j); 
					for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { newSeriesData2[i][j] = (String)((ArrayList<?>)newSeriesData.get(i)).get(j); } 
				}
				setProperty(ChartWidget.PROP_XSOURCE_NA, newXNames2); setProperty(ChartWidget.PROP_XSOURCE_OB, newXSeries2);
				setProperty(ChartWidget.PROP_YSOURCE_NA, newYNames2); setProperty(ChartWidget.PROP_YSOURCE_OB, newYSeries2);
				setProperty(ChartWidget.PROP_DOTSTYPE, newDotsTypes2); 
				for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { setProperty(ChartWidget.PROP_SERIESDATA[i], newSeriesData2[i]); }
			} else  {
				setProperty(ChartWidget.PROP_XSOURCE_NA, null); setProperty(ChartWidget.PROP_XSOURCE_OB, null);
				setProperty(ChartWidget.PROP_YSOURCE_NA, null); setProperty(ChartWidget.PROP_YSOURCE_OB, null);
				setProperty(ChartWidget.PROP_DOTSTYPE, null); 
				for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { setProperty(ChartWidget.PROP_SERIESDATA[i], null); }
			}
		}
		((ChartWidgetView.ChartWidgetRenderer)view.getRenderer()).initView();
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	public final void prepareForSave() {
		super.prepareForSave();
		Boolean b;
		AttributeMap am = getAttributes();
		STTools.mapObjArray(am, dataToSave, ChartWidget.PROP_XSOURCE_NA);
		STTools.mapObjArray(am, dataToSave, ChartWidget.PROP_YSOURCE_NA);
		STTools.mapObjArray(am, dataToSave, ChartWidget.PROP_DOTSTYPE);
		for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { STTools.mapObjArray(am, dataToSave, ChartWidget.PROP_SERIESDATA[i]); }
		STTools.mapObj(am, dataToSave, ChartWidget.PROP_ISOMETRIC);
		STTools.mapObj(am, dataToSave, ChartWidget.PROP_TYPEX);
		STTools.mapObj(am, dataToSave, ChartWidget.PROP_AUTOAXISX);
		if((b = (Boolean) am.get(ChartWidget.PROP_AUTOAXISX)) != null) {
			if(!b.booleanValue()) {
				STTools.mapObj(am, dataToSave, ChartWidget.PROP_MINX);
				STTools.mapObj(am, dataToSave, ChartWidget.PROP_STEPX);
				STTools.mapObj(am, dataToSave, ChartWidget.PROP_MAXX);
			}
		}
		STTools.mapObj(am, dataToSave, ChartWidget.PROP_TYPEY);
		STTools.mapObj(am, dataToSave, ChartWidget.PROP_AUTOAXISY);
		if((b = (Boolean) am.get(ChartWidget.PROP_AUTOAXISY)) != null) {
			if(!b.booleanValue()) {
				STTools.mapObj(am, dataToSave, ChartWidget.PROP_MINY);
				STTools.mapObj(am, dataToSave, ChartWidget.PROP_STEPY);
				STTools.mapObj(am, dataToSave, ChartWidget.PROP_MAXY);
			}
		}
		STTools.mapObj(am, dataToSave, ChartWidget.PROP_AUTOLEGENDX);
		if((b = (Boolean) am.get(ChartWidget.PROP_AUTOLEGENDX)) != null) {
			if(!b.booleanValue()) {
				STTools.mapObj(am, dataToSave, ChartWidget.PROP_LEGENDX);
			}
		}
		STTools.mapObj(am, dataToSave, ChartWidget.PROP_AUTOLEGENDY);
		if((b = (Boolean) am.get(ChartWidget.PROP_AUTOLEGENDY)) != null) {
			if(!b.booleanValue()) {
				STTools.mapObj(am, dataToSave, ChartWidget.PROP_LEGENDY);
			}
		}
		STTools.mapObj(am, dataToSave, ChartWidget.PROP_LOGSCALEX);
		STTools.mapObj(am, dataToSave, ChartWidget.PROP_LOGSCALEY);
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();

		if((String)dataToSave.get(ChartWidget.PROP_DOTSTYPE) == null) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR.OLDCHARTFORMAT")); //$NON-NLS-1$
			return;
		}

		String s;
		String title = STTools.BLANK;
		int numSeries = 0;
		String[] xNames = null;
		String[] yNames = null;
		String[] dotsTypes = null;
		final String[][] seriesData = new String[ChartWidget.PROP_SERIESDATA.length][];
		boolean isometric = false;
		int typeX = 2;
		int typeY = 2;
		boolean autoX = true;
		boolean autoY = true;
		double minX = 0.0;
		double stepX = 0.0;
		double maxX = 0.0;
		double minY = 0.0;
		double stepY = 0.0;
		double maxY = 0.0;
		boolean autoLegendX = true;
		String legendX = null;
		boolean autoLegendY = true;
		String legendY = null;
		boolean logscaleX = false;
		boolean logscaleY = false;
		if((s = (String)dataToSave.get(STWidget.PROP_TITLE)) != null) { title = s; }
		if((s = (String)dataToSave.get(ChartWidget.PROP_XSOURCE_NA)) != null) {
			xNames = s.split(STTools.COMMA);
			numSeries = xNames.length;
			yNames = ((String)dataToSave.get(ChartWidget.PROP_YSOURCE_NA)).split(STTools.COMMA);
			dotsTypes = ((String)dataToSave.get(ChartWidget.PROP_DOTSTYPE)).split(STTools.COMMA);
			for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) {
				if((s = (String)dataToSave.get(ChartWidget.PROP_SERIESDATA[i])) != null) {
					String[] s2 = s.split(STTools.COMMA);
					seriesData[i] = new String[numSeries];
					for(int j = 0; j < numSeries; j++) { seriesData[i][j] = s2[j]; }
				}
			}
		}
		if((s = (String)dataToSave.get(ChartWidget.PROP_ISOMETRIC)) != null) { isometric = Boolean.valueOf(s).booleanValue(); }
		if((s = (String)dataToSave.get(ChartWidget.PROP_TYPEX)) != null) { typeX = Integer.parseInt(s); }
		if((s = (String)dataToSave.get(ChartWidget.PROP_AUTOAXISX)) != null) { autoX = Boolean.valueOf(s).booleanValue(); }
		if(!autoX) {
			if((s = (String)dataToSave.get(ChartWidget.PROP_MINX)) != null) { minX = Double.parseDouble(s); }
			if((s = (String)dataToSave.get(ChartWidget.PROP_STEPX)) != null) { stepX = Double.parseDouble(s); }
			if((s = (String)dataToSave.get(ChartWidget.PROP_MAXX)) != null) { maxX = Double.parseDouble(s); }
		}
		if((s = (String)dataToSave.get(ChartWidget.PROP_TYPEY)) != null) { typeY = Integer.parseInt(s); }
		if((s = (String)dataToSave.get(ChartWidget.PROP_AUTOAXISY)) != null) { autoY = Boolean.valueOf(s).booleanValue(); }
		if(!autoY) {
			if((s = (String)dataToSave.get(ChartWidget.PROP_MINY)) != null) { minY = Double.parseDouble(s); }
			if((s = (String)dataToSave.get(ChartWidget.PROP_STEPY)) != null) { stepY = Double.parseDouble(s); }
			if((s = (String)dataToSave.get(ChartWidget.PROP_MAXY)) != null) { maxY = Double.parseDouble(s); }
		}
		if((s = (String)dataToSave.get(ChartWidget.PROP_AUTOLEGENDX)) != null) { autoLegendX = Boolean.valueOf(s).booleanValue(); }
		if(!autoLegendX) {
			if((s = (String)dataToSave.get(ChartWidget.PROP_LEGENDX)) != null) { legendX = s; }
		}
		if((s = (String)dataToSave.get(ChartWidget.PROP_AUTOLEGENDY)) != null) { autoLegendY = Boolean.valueOf(s).booleanValue(); }
		if(!autoLegendY) {
			if((s = (String)dataToSave.get(ChartWidget.PROP_LEGENDY)) != null) { legendY = s; }
		}
		if((s = (String)dataToSave.get(ChartWidget.PROP_LOGSCALEX)) != null) { logscaleX = Boolean.valueOf(s).booleanValue(); }
		if((s = (String)dataToSave.get(ChartWidget.PROP_LOGSCALEY)) != null) { logscaleY = Boolean.valueOf(s).booleanValue(); }
		setProps(title, numSeries, xNames, yNames, dotsTypes, seriesData, isometric, typeX, autoX, minX, stepX, maxX, typeY, autoY, minY, stepY, maxY, autoLegendX, legendX, autoLegendY, legendY, logscaleX, logscaleY);
	}

}
