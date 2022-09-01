/*******************************************************************************
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
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STFileFilter5;
import it.liuc.stgraph.util.STTextField;
import it.liuc.stgraph.util.STTools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import javax.swing.JCheckBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/** NodeDialog handler for the Chart widget. */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ChartWidgetDialog extends WidgetDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The text title. */
	private STTextField textTitle = null;
	/** The list series. */
	private JList<Object> listSeries = null;
	/** The list series model. */
	private DefaultListModel listSeriesModel = new DefaultListModel();
	/** The scroll series. */
	private JScrollPane scrollSeries = null;
	/** The button up. */
	private JButton buttonUp = null;
	/** The button down. */
	private JButton buttonDown = null;
	/** The button del. */
	private JButton buttonDel = null;
	/** The button add XY. */
	private JButton buttonAddXY = null;
	/** The scroll var X. */
	private JScrollPane scrollVarX = null;
	/** The list var X. */
	private JList listVarX = null;
	/** The scroll var Y. */
	private JScrollPane scrollVarY = null;
	/** The list var Y. */
	private JList listVarY = null;
	/** The scaling panel. */
	private JPanel scalingPanel = null;
	/** The check isometric. */
	private JCheckBox checkIsometric = null;
	/** The type X. */
	private JComboBox typeX = null;
	/** The check auto X. */
	private JCheckBox checkAutoX = null;
	/** The text min X. */
	private STTextField textMinX = null;
	/** The text step X. */
	private STTextField textStepX = null;
	/** The text max X. */
	private STTextField textMaxX = null;
	/** The type Y. */
	private JComboBox typeY = null;
	/** The check auto Y. */
	private JCheckBox checkAutoY = null;
	/** The text min Y. */
	private STTextField textMinY = null;
	/** The text step Y. */
	private STTextField textStepY = null;
	/** The text max Y. */
	private STTextField textMaxY = null;
	/** The legend auto X. */
	private JCheckBox checkAutoLegendX = null;
	/** The text legend X. */
	private STTextField textLegendX = null;
	/** The legend auto Y. */
	private JCheckBox checkAutoLegendY = null;
	/** The text legend Y. */
	private STTextField textLegendY = null;
	/** The logscale X. */
	private JCheckBox checkLogscaleX = null;
	/** The logscale Y. */
	private JCheckBox checkLogscaleY = null;
	/** The conf series panel. */
	private JPanel confSeriesPanel = null;
	/** The conf dots series panel. */
	private JPanel confDotsSeriesPanel = null;
	/** The conf line series panel. */
	private JPanel confLineSeriesPanel = null;
	/** The conf bars series panel. */
	private JPanel confBarsSeriesPanel = null;

	// series related components
	// dots related components
	/** The check of show dots. */
	private JCheckBox checkShowDots = null;
	/** The list of dots styles. */
	private JComboBox listDotsStyles = null;
	/** The label of dots size (if style != sprite). */
	private JLabel labelDotsSize = null;
	/** The text of dots size (if style != sprite). */
	private STTextField textDotsSize = null;
	/** The label of dots colors (if style != sprite). */
	private JLabel labelDotsColors = null;
	/** The list of dots colors (if style != sprite). */
	private JComboBox listDotsColors = null;
	/** The label of dots x scale (if style == sprite). */
	private JLabel labelDotsXScale = null;
	/** The text of dots x scale (if style == sprite). */
	private STTextField textDotsXScale = null;
	/** The label of dots y scale (if style == sprite). */
	private JLabel labelDotsYScale = null;
	/** The text of dots y scale (if style == sprite). */
	private STTextField textDotsYScale = null;
	/** The label of dots angle (if style == sprite). */
	private JLabel labelDotsAngle = null;
	/** The text of dots angle (if style == sprite). */
	private STTextField textDotsAngle = null;
	/** The check of last dot only. */
	private JCheckBox checkDotsLastOnly = null;
	/** The check of highlight last dot. */
	private JCheckBox checkDotsHiLast = null;
	// line related components
	/** The check of show line. */
	private JCheckBox checkShowLine = null;
	/** The label of line styles. */
	private JLabel labelLineStyles = null;
	/** The list of line styles. */
	private JComboBox listLineStyles = null;
	/** The text of line width. */
	private STTextField textLineWidth = null;
	/** The list of line colors. */
	private JComboBox listLineColors = null;
	// bars related components
	/** The check of stemmed. */
	private JCheckBox checkShowBars = null;
	/** The text of bars width. */
	private STTextField textBarsWidth = null;
	/** The list of bars colors. */
	private JComboBox listBarsColors = null;

	// series related data structures
	// dots related structures
	/** The dots type list. */
	private ArrayList<String> dotsTypeList = new ArrayList<String>();

	/** The show dots list (false if undefined). */
	private ArrayList<Boolean> showDotsList = new ArrayList<Boolean>();
	/** The dots style list: DOTSTYLE_VALUES, then DEFSPRITE_VALUES, then sprite_filenames (first element if undefined). */
	private ArrayList<String> dotsStyleList = new ArrayList<String>();
	/** The dots size list: #value or node_name (#1 if undefined). */
	private ArrayList<String> dotsSizeList = new ArrayList<String>();
	/** The dots color list: COLOR_VALUES (next element if undefined). */
	private ArrayList<String> dotsColorList = new ArrayList<String>();
	/** The dots x scale list: #value or node name (#1 if undefined). */
	private ArrayList<String> dotsXScaleList = new ArrayList<String>();
	/** The dots y scale list: #value or node name (#1 if undefined). */
	private ArrayList<String> dotsYScaleList = new ArrayList<String>();
	/** The dots angle list: #value or node name (#0 if undefined). */
	private ArrayList<String> dotsAngleList = new ArrayList<String>();
	/** The last dot only list (false if undefined). */
	private ArrayList<Boolean> dotsLastOnlyList = new ArrayList<Boolean>();
	/** The highlight last dot list (false if undefined). */
	private ArrayList<Boolean> dotsHiLastList = new ArrayList<Boolean>();
	// line related structures
	/** The show line list (true if undefined). */
	private ArrayList<Boolean> showLineList = new ArrayList<Boolean>();
	/** The line style list: LINESTYLE_VALUES (first element if undefined). */
	private ArrayList<String> lineStyleList = new ArrayList<String>();
	/** The line width list: value or node name (#1 if undefined). */
	private ArrayList<String> lineWidthList = new ArrayList<String>();
	/** The line color list: COLOR_VALUES (next element if undefined). */
	private ArrayList<String> lineColorList = new ArrayList<String>();
	// line related structures
	/** The show bars list (false if undefined). */
	private ArrayList<Boolean> showBarsList = new ArrayList<Boolean>();
	/** The bars width list: value or node name (#1 if undefined). */
	private ArrayList<String> barsWidthList = new ArrayList<String>();
	/** The bars color list: COLOR_VALUES (next element if undefined). */
	private ArrayList<String> barsColorList = new ArrayList<String>();

	// default values
	private String dotsTypeDef = ChartWidget.DOT;

	private Boolean showDotsDef = Boolean.valueOf(false);
	private String dotsStyleDef = ChartWidget.DOTSTYLE_VALUES[0]; 
	private String dotsSizeDef = STTools.SHARP + 1;
	private String dotsXScaleDef = STTools.SHARP + 1;
	private String dotsYScaleDef = STTools.SHARP + 1;
	private String dotsAngleDef = STTools.SHARP + 0;
	private Boolean dotsLastOnlyDef = Boolean.valueOf(false);
	private Boolean dotsHiLastDef = Boolean.valueOf(false);
	private Boolean showLineDef = Boolean.valueOf(true);
	private String lineStyleDef = ChartWidget.LINESTYLE_VALUES[0]; 
	private String lineWidthDef = STTools.SHARP + 1;
	private Boolean showBarsDef = Boolean.valueOf(false);
	private String barsWidthDef = STTools.SHARP + 1;

	private int currSelIndex = -1;

	static final Dimension FIELD_DIM = new Dimension(150, 20);
	static final Dimension FIELD_DIM2 = new Dimension(50, 20);


	/** Open the dialog, that was been created in a static way.
	 * @param node the node */
	public final void open(final STWidget node) {
		this.node = node;
		startInit();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		boolean bb = listSeriesModel != null && listSeriesModel.getSize() > 0;

		switchSeriesEnablings(b && bb);

		textTitle.setEnabled(b); listSeries.setEnabled(b);
		listVarX.setEnabled(b); listVarY.setEnabled(b);
		buttonAddXY.setEnabled(b); buttonUp.setEnabled(b); buttonDown.setEnabled(b); buttonDel.setEnabled(b); 
		typeX.setEnabled(b);
		typeY.setEnabled(b);
		checkIsometric.setEnabled(b);
		boolean b2 = !checkIsometric.isSelected();
		checkAutoX.setEnabled(b && b2); checkAutoY.setEnabled(b && b2);
		checkLogscaleX.setEnabled(b && b2); checkLogscaleY.setEnabled(b && b2);
		bb = b && b2 && !checkAutoX.isSelected();
		if(!bb) {
			textMinX.setBackground(Color.LIGHT_GRAY); textStepX.setBackground(Color.LIGHT_GRAY); textMaxX.setBackground(Color.LIGHT_GRAY);
		} else {
			textMinX.setBackground(Color.WHITE); textStepX.setBackground(Color.WHITE); textMaxX.setBackground(Color.WHITE);
		}
		textMinX.setEnabled(bb); textStepX.setEnabled(bb); textMaxX.setEnabled(bb);
		bb = b && b2 && !checkAutoY.isSelected();
		if(!bb) {
			textMinY.setBackground(Color.LIGHT_GRAY); textStepY.setBackground(Color.LIGHT_GRAY); textMaxY.setBackground(Color.LIGHT_GRAY);
		} else {
			textMinY.setBackground(Color.WHITE); textStepY.setBackground(Color.WHITE); textMaxY.setBackground(Color.WHITE);
		}
		textMinY.setEnabled(bb); textStepY.setEnabled(bb); textMaxY.setEnabled(bb);
		checkAutoLegendX.setEnabled(b); textLegendX.setEnabled(b && !checkAutoLegendX.isSelected());
		checkAutoLegendY.setEnabled(b); textLegendY.setEnabled(b && !checkAutoLegendY.isSelected());
		textLegendX.setBackground(checkAutoLegendX.isSelected() ? Color.LIGHT_GRAY : Color.WHITE);
		textLegendY.setBackground(checkAutoLegendY.isSelected() ? Color.LIGHT_GRAY : Color.WHITE);
		checkLogscaleX.setEnabled(b); checkLogscaleY.setEnabled(b);

		buttonOk.setEnabled(b);
		completeInit();
	}


	/** Switch the enabled state of the series related components.
	 * @param b switch */
	private final void switchSeriesEnablings(final boolean b) {
		// series related openings
		// dots related openings
		checkShowDots.setEnabled(b);
		listDotsStyles.setEnabled(b);
		textDotsSize.setEnabled(b);
		listDotsColors.setEnabled(b);
		textDotsXScale.setEnabled(b);
		textDotsYScale.setEnabled(b);
		textDotsAngle.setEnabled(b);
		checkDotsLastOnly.setEnabled(b);
		checkDotsHiLast.setEnabled(b);
		// line related openings
		checkShowLine.setEnabled(b);
		listLineStyles.setEnabled(b);
		textLineWidth.setEnabled(b);
		listLineColors.setEnabled(b);
		// bars related openings
		checkShowBars.setEnabled(b);
		textBarsWidth.setEnabled(b);
		listBarsColors.setEnabled(b);
	}


	/** Fill the dialog fields. */
	protected void fill() {
		STGraphC stc = STGraph.getSTC();
		String p = (String)node.getProperty(STWidget.PROP_TITLE); getTextTitle().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		String[] p1 = (String[])node.getProperty(ChartWidget.PROP_XSOURCE_NA);
		String[] p2 = (String[])node.getProperty(ChartWidget.PROP_YSOURCE_NA);
		listSeriesModel.clear();

		if(p1 != null && p1.length > 0) {
			// series related fillings
			for(int i = 0; i < p1.length; i++) { listSeriesModel.addElement(p1[i] + ", " + p2[i]); } //$NON-NLS-1$

			// dots related fillings
			dotsTypeList = new ArrayList<String>();
			String[] dotsTypes = (String[])node.getProperty(ChartWidget.PROP_DOTSTYPE);
			if(dotsTypes != null) {
				for(int i = 0; i < p1.length; i++) { dotsTypeList.add(dotsTypes[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { dotsTypeList.add(dotsTypeDef); }
			}

			showDotsList = new ArrayList<Boolean>();
			String[] showDots = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[0]); //ChartWidget.PROP_SHOWDOTS
			if(showDots != null) {
				for(int i = 0; i < p1.length; i++) { showDotsList.add(Boolean.valueOf(showDots[i])); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { showDotsList.add(showDotsDef); }
			}

			dotsStyleList = new ArrayList<String>();
			String[] dotsStyles = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[1]); //ChartWidget.PROP_DOTS_STYLES
			if(dotsStyles != null) {
				for(int i = 0; i < p1.length; i++) { dotsStyleList.add(dotsStyles[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { dotsStyleList.add(dotsStyleDef); }
			}

			dotsSizeList = new ArrayList<String>();
			String[] dotsSizes = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[2]); //ChartWidget.PROP_DOTS_SIZES
			if(dotsSizes != null) {
				for(int i = 0; i < p1.length; i++) { dotsSizeList.add(dotsSizes[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { dotsSizeList.add(dotsSizeDef); }
			}

			dotsColorList = new ArrayList<String>();
			String[] dotsColors = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[3]); //ChartWidget.PROP_DOTS_COLORS
			if(dotsColors != null) {
				for(int i = 0; i < p1.length; i++) { dotsColorList.add(dotsColors[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { dotsColorList.add(ChartWidget.COLOR_VALUES[Math.min(i, ChartWidget.COLOR_VALUES.length - 1)]); }
			}

			dotsXScaleList = new ArrayList<String>();
			String[] dotsXScales = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[4]); //ChartWidget.PROP_DOTS_XSCALES
			if(dotsXScales != null) {
				for(int i = 0; i < p1.length; i++) { dotsXScaleList.add(dotsXScales[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { dotsXScaleList.add(dotsXScaleDef); }
			}

			dotsYScaleList = new ArrayList<String>();
			String[] dotsYScales = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[5]); //ChartWidget.PROP_DOTS_YSCALES
			if(dotsYScales != null) {
				for(int i = 0; i < p1.length; i++) { dotsYScaleList.add(dotsYScales[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { dotsYScaleList.add(dotsYScaleDef); }
			}

			dotsAngleList = new ArrayList<String>();
			String[] dotsAngles = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[6]); //ChartWidget.PROP_DOTS_ANGLES
			if(dotsAngles != null) {
				for(int i = 0; i < p1.length; i++) { dotsAngleList.add(dotsAngles[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { dotsAngleList.add(dotsAngleDef); }
			}

			dotsLastOnlyList = new ArrayList<Boolean>();
			String[] dotslastOnly = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[7]); //ChartWidget.PROP_DOTS_LASTONLY
			if(dotslastOnly != null) {
				for(int i = 0; i < p1.length; i++) { dotsLastOnlyList.add(Boolean.valueOf(dotslastOnly[i])); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { dotsLastOnlyList.add(dotsLastOnlyDef); }
			}

			dotsHiLastList = new ArrayList<Boolean>();
			String[] dotsHiLast = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[8]); //ChartWidget.PROP_DOTS_HILAST
			if(dotsHiLast != null) {
				for(int i = 0; i < p1.length; i++) { dotsHiLastList.add(Boolean.valueOf(dotsHiLast[i])); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { dotsHiLastList.add(dotsHiLastDef); }
			}

			// line related fillings
			showLineList = new ArrayList<Boolean>();
			String[] showLine = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[9]); //ChartWidget.PROP_SHOWLINE
			if(showLine != null) {
				for(int i = 0; i < p1.length; i++) { showLineList.add(Boolean.valueOf(showLine[i])); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { showLineList.add(showLineDef); }
			}

			lineStyleList = new ArrayList<String>();
			String[] lineStyles = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[10]); //ChartWidget.PROP_LINE_STYLES
			if(lineStyles != null) {
				for(int i = 0; i < p1.length; i++) { lineStyleList.add(lineStyles[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { lineStyleList.add(lineStyleDef); }
			}

			lineWidthList = new ArrayList<String>();
			String[] lineWidth = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[11]); //ChartWidget.PROP_LINE_WIDTHS
			if(lineWidth != null) {
				for(int i = 0; i < p1.length; i++) { lineWidthList.add(lineWidth[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { lineWidthList.add(lineWidthDef); }
			}

			lineColorList = new ArrayList<String>();
			String[] lineColors = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[12]); //ChartWidget.PROP_LINE_COLORS
			if(lineColors != null) {
				for(int i = 0; i < p1.length; i++) { lineColorList.add(lineColors[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { lineColorList.add(ChartWidget.COLOR_VALUES[Math.min(i, ChartWidget.COLOR_VALUES.length - 1)]); }
			}

			// bars related fillings
			showBarsList = new ArrayList<Boolean>();
			String[] showBars = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[13]); //ChartWidget.PROP_SHOWBARS
			if(showBars != null) {
				for(int i = 0; i < p1.length; i++) { showBarsList.add(Boolean.valueOf(showBars[i])); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { showBarsList.add(showBarsDef); }
			}

			barsWidthList = new ArrayList<String>();
			String[] barsWidth = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[14]); //ChartWidget.PROP_BARS_WIDTHS
			if(barsWidth != null) {
				for(int i = 0; i < p1.length; i++) { barsWidthList.add(barsWidth[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { barsWidthList.add(barsWidthDef); }
			}

			barsColorList = new ArrayList<String>();
			String[] barsColors = (String[])node.getProperty(ChartWidget.PROP_SERIESDATA[15]); //ChartWidget.PROP_BARS_COLORS
			if(barsColors != null) {
				for(int i = 0; i < p1.length; i++) { barsColorList.add(barsColors[i]); }
			} else { // default
				for(int i = 0; i < p1.length; i++) { barsColorList.add(ChartWidget.COLOR_VALUES[Math.min(i, ChartWidget.COLOR_VALUES.length - 1)]); }
			}
		}

		getListSeries().setModel(listSeriesModel);
		if(listSeriesModel.getSize() > 0) { getListSeries().setSelectedIndex(0); }
		visibilitySwitcher();

		getListVarY().setListData(STTools.getNamesFromNodeList(stc.getCurrentGraph().getOutputNodeList()));
		String[] further = { "vTime" , "vIndex" }; //$NON-NLS-1$ //$NON-NLS-2$
		getListVarX().setListData(STTools.getNamesFromNodeList(stc.getCurrentGraph().getOutputNodeList(), further));

		Boolean b = (Boolean)node.getProperty(ChartWidget.PROP_ISOMETRIC);
		boolean bb = (b == null) ? false : b.booleanValue();
		getCheckIsometric().setSelected(bb);

		Integer i = (Integer)node.getProperty(ChartWidget.PROP_TYPEX);
		int ii = (i == null) ? 2 : i.intValue();
		typeX.setSelectedIndex(ii);
		i = (Integer)node.getProperty(ChartWidget.PROP_TYPEY);
		ii = (i == null) ? 2 : i.intValue();
		typeY.setSelectedIndex(ii);

		b = (Boolean)node.getProperty(ChartWidget.PROP_AUTOAXISX);
		bb = (b == null) ? true : b.booleanValue();
		getCheckAutoX().setSelected(bb);

		b = (Boolean)node.getProperty(ChartWidget.PROP_AUTOAXISY);
		bb = (b == null) ? true : b.booleanValue();
		getCheckAutoY().setSelected(bb);

		Double d = (Double)node.getProperty(ChartWidget.PROP_MINX); String dd = (d == null) ? "0.0" : d.toString(); textMinX.setText(dd); //$NON-NLS-1$
		d = (Double)node.getProperty(ChartWidget.PROP_STEPX); dd = (d == null) ? "0.2" : d.toString(); textStepX.setText(dd); //$NON-NLS-1$
		d = (Double)node.getProperty(ChartWidget.PROP_MAXX); dd = (d == null) ? "1.0" : d.toString(); textMaxX.setText(dd); //$NON-NLS-1$
		d = (Double)node.getProperty(ChartWidget.PROP_MINY); dd = (d == null) ? "0.0" : d.toString(); textMinY.setText(dd); //$NON-NLS-1$
		d = (Double)node.getProperty(ChartWidget.PROP_STEPY); dd = (d == null) ? "0.2" : d.toString(); textStepY.setText(dd); //$NON-NLS-1$
		d = (Double)node.getProperty(ChartWidget.PROP_MAXY); dd = (d == null) ? "1.0" : d.toString(); textMaxY.setText(dd); //$NON-NLS-1$

		b = (Boolean)node.getProperty(ChartWidget.PROP_AUTOLEGENDX);
		bb = (b == null) ? true : b.booleanValue();
		getCheckAutoLegendX().setSelected(bb);
		p = (String)node.getProperty(ChartWidget.PROP_LEGENDX); p = (p == null) ? STTools.BLANK : p; getTextLegendX().setText(p);
		textLegendX.setEnabled(!bb);

		b = (Boolean)node.getProperty(ChartWidget.PROP_AUTOLEGENDY);
		bb = (b == null) ? true : b.booleanValue();
		getCheckAutoLegendY().setSelected(bb);
		p = (String)node.getProperty(ChartWidget.PROP_LEGENDY); p = (p == null) ? STTools.BLANK : p; getTextLegendY().setText(p);
		textLegendY.setEnabled(!bb);

		b = (Boolean)node.getProperty(ChartWidget.PROP_LOGSCALEX);
		bb = (b == null) ? false : b.booleanValue();
		getCheckLogscaleX().setSelected(bb);

		b = (Boolean)node.getProperty(ChartWidget.PROP_LOGSCALEY);
		bb = (b == null) ? false : b.booleanValue();
		getCheckLogscaleY().setSelected(bb);

		setListSeriesModel();
	}


	/** Set list series model. */
	private void setListSeriesModel() {
		boolean b = listSeriesModel.getSize() > 0;
		int selItem = getListSeries().getSelectedIndex();
		if(selItem != -1) {
			String s;
			String t;
			int x;

			// dots related settings
			checkShowDots.setSelected(showDotsList.get(selItem).booleanValue());

			t = dotsTypeList.get(selItem);
			s = dotsStyleList.get(selItem);
			if(t.equals(ChartWidget.DOT)) {
				listDotsStyles.setSelectedIndex(((x = seekValue(ChartWidget.DOTSTYLE_VALUES, s)) != -1) ? x : 0);
			} else if(t.equals(ChartWidget.SYSTEM_SPRITE)) {
				listDotsStyles.setSelectedIndex((((x = seekValue(ChartWidget.DEFSPRITE_VALUES, s)) != -1) ? x : 0) + ChartWidget.DOTSTYLE_VALUES.length);
			} else {
				listDotsStyles.setSelectedItem(s);
			}

			s = dotsSizeList.get(selItem); textDotsSize.setText(s.startsWith(STTools.SHARP) ? s.substring(1) : s);
			s = dotsColorList.get(selItem); listDotsColors.setSelectedIndex((s.startsWith(STTools.UNDERSCORE + STTools.UNDERSCORE) && (x = seekValue(ChartWidget.COLOR_VALUES, s)) != -1) ? x : 0);
			s = dotsXScaleList.get(selItem); textDotsXScale.setText(s.startsWith(STTools.SHARP) ? s.substring(1) : s);
			s = dotsYScaleList.get(selItem); textDotsYScale.setText(s.startsWith(STTools.SHARP) ? s.substring(1) : s);
			s = dotsAngleList.get(selItem); textDotsAngle.setText(s.startsWith(STTools.SHARP) ? s.substring(1) : s);

			checkDotsLastOnly.setSelected(dotsLastOnlyList.get(selItem).booleanValue());
			checkDotsHiLast.setSelected(dotsHiLastList.get(selItem).booleanValue());

			// line related settings
			checkShowLine.setSelected(showLineList.get(selItem).booleanValue());
			s = lineStyleList.get(selItem); listLineStyles.setSelectedIndex((s.startsWith(STTools.UNDERSCORE + STTools.UNDERSCORE) && (x = seekValue(ChartWidget.LINESTYLE_VALUES, s)) != -1) ? x : 0);
			s = lineWidthList.get(selItem); textLineWidth.setText(s.startsWith(STTools.SHARP) ? s.substring(1) : s);
			s = lineColorList.get(selItem); listLineColors.setSelectedIndex((s.startsWith(STTools.UNDERSCORE + STTools.UNDERSCORE) && (x = seekValue(ChartWidget.COLOR_VALUES, s)) != -1) ? x : 0);

			// dots related settings
			checkShowBars.setSelected(showBarsList.get(selItem).booleanValue());
			s = barsWidthList.get(selItem); textBarsWidth.setText(s.startsWith(STTools.SHARP) ? s.substring(1) : s);
			s = barsColorList.get(selItem); listBarsColors.setSelectedIndex((s.startsWith(STTools.UNDERSCORE + STTools.UNDERSCORE) && (x = seekValue(ChartWidget.COLOR_VALUES, s)) != -1) ? x : 0);
		}
		switchSeriesEnablings(b);
		visibilitySwitcher();
	}


	/** Helper for list updatings.
	 * @param values the list
	 * @param key the key
	 * @return the index */
	private int seekValue(final String[] values, final String key) {
		for(int i = 0; i < values.length; i++) {
			if(values[i].equals(key)) { return i; }
		}
		return -1;
	}


	/** Get the jContentPane.
	 * @return panel */
	protected JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			// chart title
			jContentPane.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".CHARTTITLE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcTextTitle = new GridConstraints(1, 0);
			gbcTextTitle.fill = GridBagConstraints.HORIZONTAL;
			gbcTextTitle.weightx = 1.0; gbcTextTitle.gridwidth = 2;
			jContentPane.add(getTextTitle(), gbcTextTitle);
			// series list
			jContentPane.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".SERIESLIST") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcScrollSeries = new GridConstraints(1, 1);
			gbcScrollSeries.fill = GridBagConstraints.BOTH;
			gbcScrollSeries.weightx = 1.0; gbcScrollSeries.gridwidth = 2;
			gbcScrollSeries.weighty = 1.0; gbcScrollSeries.gridheight = 4;
			jContentPane.add(getScrollSeries(), gbcScrollSeries);
			// up
			jContentPane.add(getButtonUp(), new GridConstraints(0, 2, GridBagConstraints.NORTHEAST));
			// down
			jContentPane.add(getButtonDown(), new GridConstraints(0, 3, GridBagConstraints.NORTHEAST));
			// del
			jContentPane.add(getButtonDel(), new GridConstraints(0, 4, GridBagConstraints.NORTHEAST));
			// series configuration panel
			GridConstraints gbcConf = new GridConstraints(3, 1, GridBagConstraints.NORTHWEST);
			gbcConf.weightx = 1.0;
			gbcConf.gridheight = 4;
			gbcConf.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getConfSeriesPanel(), gbcConf);
			// available X variables
			jContentPane.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".AVAVARX") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(1, 5, GridBagConstraints.WEST));
			//
			GridConstraints gbcScrollVarX = new GridConstraints(1, 6);
			gbcScrollVarX.fill = GridBagConstraints.BOTH;
			gbcScrollVarX.weightx = 1.0; gbcScrollVarX.weighty = 1.0;
			jContentPane.add(getScrollVarX(), gbcScrollVarX);
			// available Y variables
			jContentPane.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".AVAVARY") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(2, 5, GridBagConstraints.WEST));
			//
			GridConstraints gbcScrollVarY = new GridConstraints(2, 6);
			gbcScrollVarY.fill = GridBagConstraints.BOTH;
			gbcScrollVarY.weightx = 1.0; gbcScrollVarY.weighty = 1.0;
			jContentPane.add(getScrollVarY(), gbcScrollVarY);
			// add series
			jContentPane.add(getButtonAddXY(), new GridConstraints(0, 6, GridBagConstraints.NORTHEAST));
			// scaling panel
			GridConstraints gbcScaling = new GridConstraints(3, 6, GridBagConstraints.NORTH);
			gbcScaling.weightx = 1.0;
			gbcScaling.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getScalingPanel(), gbcScaling);
			// ok-cancel buttons
			GridConstraints gbcButt = new GridConstraints(0, 7);
			gbcButt.fill = GridBagConstraints.HORIZONTAL;
			gbcButt.gridwidth = 10;
			jContentPane.add(getButtonPanel(), gbcButt);
		}
		return jContentPane;
	}


	/** Get the scrollVarX.
	 * @return pane */    
	private JScrollPane getScrollVarX() {
		if(scrollVarX == null) {
			scrollVarX = new JScrollPane();
			scrollVarX.setViewportView(getListVarX());
		}
		return scrollVarX;
	}


	/** Get the listVarX.
	 * @return list */
	private JList getListVarX() {
		if(listVarX == null) {
			listVarX = new JList();
			listVarX.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listVarX.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) { if(e.getClickCount() == 2) { buttonAddXY.doClick(); } } 
			});
		}
		return listVarX;
	}


	/** Get the scrollVarY.
	 * @return pane */    
	private JScrollPane getScrollVarY() {
		if(scrollVarY == null) {
			scrollVarY = new JScrollPane();
			scrollVarY.setViewportView(getListVarY());
		}
		return scrollVarY;
	}


	/** Get the listVarY.
	 * @return list */
	private JList getListVarY() {
		if(listVarY == null) {
			listVarY = new JList();
			listVarY.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listVarY.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) { if(e.getClickCount() == 2) { buttonAddXY.doClick(); } }
			});
		}
		return listVarY;
	}


	/** Get the scrollSeries.
	 * @return pane */    
	private JScrollPane getScrollSeries() {
		if(scrollSeries == null) {
			scrollSeries = new JScrollPane();
			scrollSeries.setViewportView(getListSeries());
		}
		return scrollSeries;
	}


	/** Get the listSeries.
	 * @return list */
	JList getListSeries() {
		if(listSeries == null) {
			listSeries = new JList();
			listSeries.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(final ListSelectionEvent e) {
					if(currSelIndex != -1 && (currSelIndex != getSelIndex())) {
						dotsSizeUpdate(currSelIndex);
						dotsXScaleUpdate(currSelIndex);
						dotsYScaleUpdate(currSelIndex);
						dotsAngleUpdate(currSelIndex);
						lineWidthUpdate(currSelIndex);
						barsWidthUpdate(currSelIndex);
					}
					setListSeriesModel();
					currSelIndex = getSelIndex();
				}
			});
		}
		return listSeries;
	}


	/** Get the buttonAddXY.
	 * @return button */    
	private JButton getButtonAddXY() {
		if(buttonAddXY == null) {
			buttonAddXY = new JButton(STGraphC.getMessage(ChartWidget.PREFIX + ".ADDTOSERIES") + STTools.COLON); //$NON-NLS-1$
			buttonAddXY.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {    
					if(listVarX.getSelectedIndex() == -1 || listVarY.getSelectedIndex() == -1) { return; }
					listSeriesModel.addElement((String)listVarX.getSelectedValue() + ", " + (String)listVarY.getSelectedValue()); //$NON-NLS-1$
					int last = listSeriesModel.getSize() - 1;
					dotsTypeList.add(dotsTypeDef);
					showDotsList.add(showDotsDef);
					dotsStyleList.add(dotsStyleDef);
					dotsSizeList.add(dotsSizeDef);
					dotsColorList.add(ChartWidget.COLOR_VALUES[Math.min(last, ChartWidget.COLOR_VALUES.length - 1)]);
					dotsXScaleList.add(dotsXScaleDef);
					dotsYScaleList.add(dotsYScaleDef);
					dotsAngleList.add(dotsAngleDef);
					dotsLastOnlyList.add(dotsLastOnlyDef);
					dotsHiLastList.add(dotsHiLastDef);
					showLineList.add(showLineDef);
					lineStyleList.add(lineStyleDef);
					lineWidthList.add(lineWidthDef);
					lineColorList.add(ChartWidget.COLOR_VALUES[Math.min(last, ChartWidget.COLOR_VALUES.length - 1)]);
					showBarsList.add(showBarsDef);
					barsWidthList.add(barsWidthDef);
					barsColorList.add(ChartWidget.COLOR_VALUES[Math.min(last, ChartWidget.COLOR_VALUES.length - 1)]);

					getListSeries().setSelectedIndex(last);
					getListSeries().ensureIndexIsVisible(last);

					switchSeriesEnablings(true);
				}
			});
		}
		return buttonAddXY;
	}


	/** Get the buttonDel.
	 * @return button */
	private JButton getButtonDel() {
		if(buttonDel == null) {
			buttonDel = new JButton(STGraphC.getMessage(ChartWidget.PREFIX + ".DEL")); //$NON-NLS-1$
			buttonDel.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {    
					int index = getListSeries().getSelectedIndex();
					if(index == -1) { return; }
					listSeriesModel.remove(index);

					dotsTypeList.remove(index);
					showDotsList.remove(index);
					dotsStyleList.remove(index);
					dotsSizeList.remove(index);
					dotsColorList.remove(index);
					dotsXScaleList.remove(index);
					dotsYScaleList.remove(index);
					dotsAngleList.remove(index);
					dotsLastOnlyList.remove(index);
					dotsHiLastList.remove(index);
					showLineList.remove(index);
					lineStyleList.remove(index);
					lineWidthList.remove(index);
					lineColorList.remove(index);
					showBarsList.remove(index);
					barsWidthList.remove(index);
					barsColorList.remove(index);

					int size = listSeriesModel.getSize();
					if(size == 0) {
						switchSeriesEnablings(false);
						return;
					}
					if(index == listSeriesModel.getSize()) { index--; } // removed item in last position
					getListSeries().setSelectedIndex(index);
					getListSeries().ensureIndexIsVisible(index);
				}
			});
		}
		return buttonDel;
	}


	/** Helper.
	 * @param index the index
	 * @param index1 the index1 */
	private final void swapper(int index, int index1) {
		swapperHelper(dotsTypeList, index, index1);
		swapperHelper(showDotsList, index, index1);
		swapperHelper(dotsStyleList, index, index1);
		swapperHelper(dotsSizeList, index, index1);
		swapperHelper(dotsColorList, index, index1);
		swapperHelper(dotsXScaleList, index, index1);
		swapperHelper(dotsYScaleList, index, index1);
		swapperHelper(dotsAngleList, index, index1);
		swapperHelper(dotsLastOnlyList, index, index1);
		swapperHelper(dotsHiLastList, index, index1);
		swapperHelper(showLineList, index, index1);
		swapperHelper(lineStyleList, index, index1);
		swapperHelper(lineWidthList, index, index1);
		swapperHelper(lineColorList, index, index1);
		swapperHelper(showBarsList, index, index1);
		swapperHelper(barsWidthList, index, index1);
		swapperHelper(barsColorList, index, index1);
	}


	/** Helper.
	 * @param list the list
	 * @param index the index
	 * @param index1 the index1 */
	private final void swapperHelper(ArrayList list, int index, int index1) {
		Object o = list.get(index);
		list.set(index, list.get(index1));
		list.set(index1, o);
		listSeries.getListSelectionListeners()[0].valueChanged(null);
	}


	/** Get the buttonUp.
	 * @return button */    
	private JButton getButtonUp() {
		if(buttonUp == null) {
			buttonUp = new JButton(" ^  "); //$NON-NLS-1$
			buttonUp.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					int index = getListSeries().getSelectedIndex();
					int size = listSeriesModel.getSize();
					if(size == 0 || index == -1 || index == 0) { return; }

					int index1 = index - 1;
					Object o = listSeriesModel.getElementAt(index);
					listSeriesModel.set(index, listSeriesModel.get(index1));
					listSeriesModel.set(index1, o);

					swapper(index, index1);

					getListSeries().setSelectedIndex(index1);
					getListSeries().ensureIndexIsVisible(index1);
					setListSeriesModel();
				}
			});
		}
		return buttonUp;
	}


	/** Get the buttonDown.
	 * @return button */    
	private JButton getButtonDown() {
		if(buttonDown == null) {
			buttonDown = new JButton(" v  "); //$NON-NLS-1$
			buttonDown.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {    
					int index = getListSeries().getSelectedIndex();
					int size = listSeriesModel.getSize();
					if(size == 0 || index == -1 || index == size - 1) { return; }

					int index1 = index + 1;
					Object o = listSeriesModel.getElementAt(index);
					listSeriesModel.set(index, listSeriesModel.get(index1));
					listSeriesModel.set(index1, o);

					swapper(index, index1);

					getListSeries().setSelectedIndex(index1);
					getListSeries().ensureIndexIsVisible(index1);
					setListSeriesModel();
				}
			});
		}
		return buttonDown;
	}


	/** Get the textTitle.
	 * @return text */
	private STTextField getTextTitle() {
		if(textTitle == null) { textTitle = new STTextField(this, false); }
		textTitle.setToolTipText(STGraphC.getMessage(ChartWidget.PREFIX + ".CHARTTITLE.TOOLTIP")); //$NON-NLS-1$
		return textTitle;
	}


	/** Get the scalingPanel.
	 * @return panel */
	private JPanel getScalingPanel() {
		if(scalingPanel == null) {
			scalingPanel = new JPanel(new GridBagLayout());
			scalingPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

			JPanel xPanel = new JPanel(new GridBagLayout());
			xPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			// axis X
			xPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".AXISX")), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.NORTH));
			// type of axis X
			GridConstraints gbcTypeX = new GridConstraints(1, 0, GridBagConstraints.WEST);
			gbcTypeX.gridwidth = 3;
			xPanel.add(getTypeX(), gbcTypeX);
			// check log scale X
			GridConstraints gbcCheckLogscaleX = new GridConstraints(6, 0, GridBagConstraints.EAST);
			gbcCheckLogscaleX.gridwidth = 2;
			xPanel.add(getCheckLogscaleX(), gbcCheckLogscaleX);
			// scale X
			xPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".SCALE")), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.NORTH));
			// check auto X
			xPanel.add(getCheckAutoX(), new GridConstraints(1, 1, GridBagConstraints.WEST));
			// min X
			xPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".MIN") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(2, 1, GridBagConstraints.WEST));
			//
			GridConstraints gbcTextMinX = new GridConstraints(3, 1, GridBagConstraints.WEST);
			gbcTextMinX.weightx = 1.0;
			gbcTextMinX.fill = GridBagConstraints.HORIZONTAL;
			xPanel.add(getTextMinX(), gbcTextMinX);
			// step X
			xPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".STEP") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(4, 1, GridBagConstraints.WEST));
			//
			GridConstraints gbcTextStepX = new GridConstraints(5, 1, GridBagConstraints.WEST);
			gbcTextStepX.weightx = 1.0;
			gbcTextStepX.fill = GridBagConstraints.HORIZONTAL;
			xPanel.add(getTextStepX(), gbcTextStepX);
			// max X
			xPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".MAX") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(6, 1, GridBagConstraints.WEST));
			//
			GridConstraints gbcTextMaxX = new GridConstraints(7, 1, GridBagConstraints.WEST);
			gbcTextMaxX.weightx = 1.0;
			gbcTextMaxX.fill = GridBagConstraints.HORIZONTAL;
			xPanel.add(getTextMaxX(), gbcTextMaxX);
			// legend X
			xPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".LEGEND") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 2, GridBagConstraints.WEST));
			// check auto legend X
			xPanel.add(getCheckAutoLegendX(), new GridConstraints(1, 2, GridBagConstraints.NORTH));
			//
			GridConstraints gbcTextLegendX = new GridConstraints(2, 2, GridBagConstraints.WEST);
			gbcTextLegendX.gridwidth = 5;
			gbcTextLegendX.weightx = 1.0;
			gbcTextLegendX.fill = GridBagConstraints.HORIZONTAL;
			xPanel.add(getTextLegendX(), gbcTextLegendX);

			scalingPanel.add(xPanel, new GridConstraints(0, 0));

			JPanel yPanel = new JPanel(new GridBagLayout());
			yPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			// axis Y
			yPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".AXISY")), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.NORTH));
			// type of axis Y
			GridConstraints gbcTypeY = new GridConstraints(1, 0, GridBagConstraints.WEST);
			gbcTypeY.gridwidth = 3;
			yPanel.add(getTypeY(), gbcTypeY);
			// check log scale Y
			GridConstraints gbcCheckLogscaleY = new GridConstraints(6, 0, GridBagConstraints.EAST);
			gbcCheckLogscaleY.gridwidth = 2;
			yPanel.add(getCheckLogscaleY(), gbcCheckLogscaleY);
			// scale Y
			yPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".SCALE")), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.NORTH));
			// check auto Y
			yPanel.add(getCheckAutoY(), new GridConstraints(1, 1, GridBagConstraints.WEST));
			// min Y
			yPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".MIN") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(2, 1, GridBagConstraints.WEST));
			//
			GridConstraints gbcTextMinY = new GridConstraints(3, 1, GridBagConstraints.WEST);
			gbcTextMinY.weightx = 1.0;
			gbcTextMinY.fill = GridBagConstraints.HORIZONTAL;
			yPanel.add(getTextMinY(), gbcTextMinY);
			// step Y
			yPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".STEP") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(4, 1, GridBagConstraints.WEST));
			//
			GridConstraints gbcTextStepY = new GridConstraints(5, 1, GridBagConstraints.WEST);
			gbcTextStepY.weightx = 1.0;
			gbcTextStepY.fill = GridBagConstraints.HORIZONTAL;
			yPanel.add(getTextStepY(), gbcTextStepY);
			// max Y
			yPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".MAX") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(6, 1, GridBagConstraints.WEST));
			//
			GridConstraints gbcTextMaxY = new GridConstraints(7, 1, GridBagConstraints.WEST);
			gbcTextMaxY.weightx = 1.0;
			gbcTextMaxY.fill = GridBagConstraints.HORIZONTAL;
			yPanel.add(getTextMaxY(), gbcTextMaxY);
			// legend Y
			yPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".LEGEND") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 2, GridBagConstraints.WEST));
			// check auto legend Y
			yPanel.add(getCheckAutoLegendY(), new GridConstraints(1, 2, GridBagConstraints.NORTH));
			//
			GridConstraints gbcTextLegendY = new GridConstraints(2, 2, GridBagConstraints.WEST);
			gbcTextLegendY.gridwidth = 5;
			gbcTextLegendY.weightx = 1.0;
			gbcTextLegendY.fill = GridBagConstraints.HORIZONTAL;
			yPanel.add(getTextLegendY(), gbcTextLegendY);

			scalingPanel.add(yPanel, new GridConstraints(0, 1));

			// check isometric
			scalingPanel.add(getCheckIsometric(), new GridConstraints(0, 2, GridBagConstraints.WEST));
		}
		return scalingPanel;
	}


	/** Get the checkIsometric.
	 * @return checkbox */
	private JCheckBox getCheckIsometric() {
		if(checkIsometric == null) {
			checkIsometric = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".ISOMETRIC")); //$NON-NLS-1$
			checkIsometric.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					boolean notSel = !checkIsometric.isSelected();
					checkAutoX.setEnabled(notSel);
					checkAutoY.setEnabled(notSel);
					if(!notSel) {
						checkLogscaleX.setSelected(false);
						checkLogscaleY.setSelected(false);
					}
					checkLogscaleX.setEnabled(notSel);
					checkLogscaleY.setEnabled(notSel);
					notSel = !checkIsometric.isSelected() && !checkAutoX.isSelected();
					if(!notSel) {
						textMinX.setBackground(Color.LIGHT_GRAY); textStepX.setBackground(Color.LIGHT_GRAY); textMaxX.setBackground(Color.LIGHT_GRAY);
					} else {
						textMinX.setBackground(Color.WHITE); textStepX.setBackground(Color.WHITE); textMaxX.setBackground(Color.WHITE);
					}
					textMinX.setEnabled(notSel); textStepX.setEnabled(notSel); textMaxX.setEnabled(notSel);
					notSel = !checkIsometric.isSelected() && !checkAutoY.isSelected();
					if(!notSel) {
						textMinY.setBackground(Color.LIGHT_GRAY); textStepY.setBackground(Color.LIGHT_GRAY); textMaxY.setBackground(Color.LIGHT_GRAY);
					} else {
						textMinY.setBackground(Color.WHITE); textStepY.setBackground(Color.WHITE); textMaxY.setBackground(Color.WHITE);
					}
					textMinY.setEnabled(notSel); textStepY.setEnabled(notSel); textMaxY.setEnabled(notSel);
				}
			});
		}
		return checkIsometric;
	}


	/** Get the typeX.
	 * @return combobox */
	private JComboBox getTypeX() {
		if(typeX == null) {
			typeX = new JComboBox(STGraphC.getMessage(ChartWidget.PREFIX + ".TYPES").split(STTools.COMMA)); //$NON-NLS-1$
			typeX.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {

				}
			});
		}
		return typeX;
	}


	/** Get the checkAutoX.
	 * @return checkbox */
	private JCheckBox getCheckAutoX() {
		if(checkAutoX == null) {
			checkAutoX = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".AUTOSCALE")); //$NON-NLS-1$
			checkAutoX.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					boolean notSel = !checkAutoX.isSelected();
					if(!notSel) {
						textMinX.setBackground(Color.LIGHT_GRAY); textStepX.setBackground(Color.LIGHT_GRAY); textMaxX.setBackground(Color.LIGHT_GRAY);
					} else {
						textMinX.setBackground(Color.WHITE); textStepX.setBackground(Color.WHITE); textMaxX.setBackground(Color.WHITE);
					}
					textMinX.setEnabled(notSel); textStepX.setEnabled(notSel); textMaxX.setEnabled(notSel);
				}
			});
		}
		return checkAutoX;
	}


	/** Get the textMinX.
	 * @return text */
	private STTextField getTextMinX() {
		if(textMinX == null) {
			textMinX = new STTextField(this, false);
			textMinX.setPreferredSize(new Dimension(50, 21));
		}
		return textMinX;
	}


	/** Get the textStepX.
	 * @return text */
	private STTextField getTextStepX() {
		if(textStepX == null) {
			textStepX = new STTextField(this, false);
			textStepX.setPreferredSize(new Dimension(50, 21));
		}
		return textStepX;
	}


	/** Get the textMaxX.
	 * @return text */    
	private STTextField getTextMaxX() {
		if(textMaxX == null) {
			textMaxX = new STTextField(this, false);
			textMaxX.setPreferredSize(new Dimension(50, 21));
		}
		return textMaxX;
	}


	/** Get the typeY.
	 * @return combobox */
	private JComboBox getTypeY() {
		if(typeY == null) {
			typeY = new JComboBox(STGraphC.getMessage(ChartWidget.PREFIX + ".TYPES").split(STTools.COMMA)); //$NON-NLS-1$
			typeY.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {

				}
			});
		}
		return typeY;
	}


	/** Get the checkAutoY.
	 * @return checkbox */
	private JCheckBox getCheckAutoY() {
		if(checkAutoY == null) {
			checkAutoY = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".AUTOSCALE")); //$NON-NLS-1$
			checkAutoY.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					boolean notSel = !checkAutoY.isSelected();
					if(!notSel) {
						textMinY.setBackground(Color.LIGHT_GRAY); textStepY.setBackground(Color.LIGHT_GRAY); textMaxY.setBackground(Color.LIGHT_GRAY);
					} else {
						textMinY.setBackground(Color.WHITE); textStepY.setBackground(Color.WHITE); textMaxY.setBackground(Color.WHITE);
					}
					textMinY.setEnabled(notSel); textStepY.setEnabled(notSel); textMaxY.setEnabled(notSel);
				}
			});
		}
		return checkAutoY;
	}


	/** Get the textMinY.
	 * @return text */
	private STTextField getTextMinY() {
		if(textMinY == null) {
			textMinY = new STTextField(this, false);
			textMinY.setPreferredSize(new Dimension(50, 21));
		}
		return textMinY;
	}


	/** Get the textStepY.
	 * @return text */
	private STTextField getTextStepY() {
		if(textStepY == null) {
			textStepY = new STTextField(this, false);
			textStepY.setPreferredSize(new Dimension(50, 21));
		}
		return textStepY;
	}


	/** Get the textMaxY.
	 * @return text */
	private STTextField getTextMaxY() {
		if(textMaxY == null) {
			textMaxY = new STTextField(this, false);
			textMaxY.setPreferredSize(new Dimension(50, 21));
		}
		return textMaxY;
	}


	/** Get the checkAutoLegendX.
	 * @return checkbox */
	private JCheckBox getCheckAutoLegendX() {
		if(checkAutoLegendX == null) {
			checkAutoLegendX = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".AUTOLEGEND")); //$NON-NLS-1$
			checkAutoLegendX.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					textLegendX.setEnabled(!checkAutoLegendX.isSelected());
					textLegendX.setBackground(checkAutoLegendX.isSelected() ? Color.LIGHT_GRAY : Color.WHITE);
				}
			});
		}
		return checkAutoLegendX;
	}


	/** Get the textLegendX.
	 * @return text */    
	private STTextField getTextLegendX() {
		if(textLegendX == null) {
			textLegendX = new STTextField(this, false);
			textLegendX.setPreferredSize(new Dimension(50, 21));
		}
		return textLegendX;
	}


	/** Get the checkAutoLegendY.
	 * @return checkbox */
	private JCheckBox getCheckAutoLegendY() {
		if(checkAutoLegendY == null) {
			checkAutoLegendY = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".AUTOLEGEND")); //$NON-NLS-1$
			checkAutoLegendY.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					textLegendY.setEnabled(!checkAutoLegendY.isSelected());
					textLegendY.setBackground(checkAutoLegendY.isSelected() ? Color.LIGHT_GRAY : Color.WHITE);
				}
			});
		}
		return checkAutoLegendY;
	}


	/** Get the textLegendY.
	 * @return text */    
	private STTextField getTextLegendY() {
		if(textLegendY == null) {
			textLegendY = new STTextField(this, false);
			textLegendY.setPreferredSize(new Dimension(50, 21));
		}
		return textLegendY;
	}


	/** Get the checkLogscaleX.
	 * @return checkbox */
	private JCheckBox getCheckLogscaleX() {
		if(checkLogscaleX == null) {
			checkLogscaleX = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".LOGSCALE")); //$NON-NLS-1$
		}
		return checkLogscaleX;
	}


	/** Get the checkLogscaleY.
	 * @return checkbox */
	private JCheckBox getCheckLogscaleY() {
		if(checkLogscaleY == null) {
			checkLogscaleY = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".LOGSCALE")); //$NON-NLS-1$
		}
		return checkLogscaleY;
	}


	/** Get the confSeriesPanel.
	 * @return panel */
	private JPanel getConfSeriesPanel() {
		if(confSeriesPanel == null) {
			confSeriesPanel = new JPanel();
			confSeriesPanel.setLayout(new GridBagLayout());
			confSeriesPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

			confSeriesPanel.add(getCheckShowDots(), new GridConstraints(0, 0, GridBagConstraints.WEST));
			confSeriesPanel.add(getConfDotsSeriesPanel(), new GridConstraints(0, 1, GridBagConstraints.WEST));
			confSeriesPanel.add(getCheckShowLine(), new GridConstraints(0, 2, GridBagConstraints.WEST));
			confSeriesPanel.add(getConfLineSeriesPanel(), new GridConstraints(0, 3, GridBagConstraints.WEST));
			confSeriesPanel.add(getCheckShowBars(), new GridConstraints(0, 4, GridBagConstraints.WEST));
			confSeriesPanel.add(getConfBarsSeriesPanel(), new GridConstraints(0, 5, GridBagConstraints.WEST));
		}
		return confSeriesPanel;
	}


	/** Get the confDotsSeriesPanel.
	 * @return panel */
	private JPanel getConfDotsSeriesPanel() {
		if(confDotsSeriesPanel == null) {
			confDotsSeriesPanel = new JPanel();
			confDotsSeriesPanel.setLayout(new GridBagLayout());
			confDotsSeriesPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

			confDotsSeriesPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".DOTSSTYLE") + STTools.COLON), new GridConstraints(0, 0, GridBagConstraints.WEST)); //$NON-NLS-1$
			GridConstraints gcds = new GridConstraints(1, 0, GridBagConstraints.WEST);
			gcds.gridwidth = 5;
			confDotsSeriesPanel.add(getListDotsStyles(), gcds);

			confDotsSeriesPanel.add(labelDotsSize = new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".DOTSSIZE") + STTools.COLON), new GridConstraints(0, 1, GridBagConstraints.WEST)); //$NON-NLS-1$
			confDotsSeriesPanel.add(getTextDotsSize(), new GridConstraints(1, 1, GridBagConstraints.WEST));

			confDotsSeriesPanel.add(labelDotsColors = new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".DOTSCOLOR") + STTools.COLON), new GridConstraints(2, 1, GridBagConstraints.WEST)); //$NON-NLS-1$
			confDotsSeriesPanel.add(getListDotsColors(), new GridConstraints(3, 1, GridBagConstraints.WEST));

			confDotsSeriesPanel.add(labelDotsXScale = new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".DOTSXSCALE") + STTools.COLON), new GridConstraints(0, 2, GridBagConstraints.WEST)); //$NON-NLS-1$
			confDotsSeriesPanel.add(getTextDotsXScale(), new GridConstraints(1, 2, GridBagConstraints.WEST));

			confDotsSeriesPanel.add(labelDotsYScale = new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".DOTSYSCALE") + STTools.COLON), new GridConstraints(2, 2, GridBagConstraints.WEST)); //$NON-NLS-1$
			confDotsSeriesPanel.add(getTextDotsYScale(), new GridConstraints(3, 2, GridBagConstraints.WEST));

			confDotsSeriesPanel.add(labelDotsAngle = new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".DOTSANGLE") + STTools.COLON), new GridConstraints(4, 2, GridBagConstraints.WEST)); //$NON-NLS-1$
			confDotsSeriesPanel.add(getTextDotsAngle(), new GridConstraints(5, 2, GridBagConstraints.WEST));

			GridConstraints gcdl = new GridConstraints(0, 5, GridBagConstraints.WEST);
			gcdl.gridwidth = 6;
			confDotsSeriesPanel.add(getCheckDotsLastOnly(), gcdl);

			GridConstraints gcdh = new GridConstraints(0, 6, GridBagConstraints.WEST);
			gcdh.gridwidth = 6;
			confDotsSeriesPanel.add(getCheckDotsHiLast(), gcdh);
		}
		return confDotsSeriesPanel;
	}


	/** Get the confLineSeriesPanel.
	 * @return panel */
	private JPanel getConfLineSeriesPanel() {
		if(confLineSeriesPanel == null) {
			confLineSeriesPanel = new JPanel();
			confLineSeriesPanel.setLayout(new GridBagLayout());
			confLineSeriesPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

			confLineSeriesPanel.add(labelLineStyles = new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".LINESTYLE") + STTools.COLON), new GridConstraints(0, 0, GridBagConstraints.WEST)); //$NON-NLS-1$
			GridConstraints gc1 = new GridConstraints(1, 0, GridBagConstraints.WEST);
			gc1.gridwidth = 3;
			confLineSeriesPanel.add(getListLineStyles(), gc1);

			confLineSeriesPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".LINEWIDTH") + STTools.COLON), new GridConstraints(0, 1, GridBagConstraints.WEST)); //$NON-NLS-1$
			confLineSeriesPanel.add(getTextLineWidth(), new GridConstraints(1, 1, GridBagConstraints.WEST));

			confLineSeriesPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".LINECOLOR") + STTools.COLON), new GridConstraints(2, 1, GridBagConstraints.WEST)); //$NON-NLS-1$
			confLineSeriesPanel.add(getListLineColors(), new GridConstraints(3, 1, GridBagConstraints.WEST));

		}
		return confLineSeriesPanel;
	}


	/** Get the confBarsSeriesPanel.
	 * @return panel */
	private JPanel getConfBarsSeriesPanel() {
		if(confBarsSeriesPanel == null) {
			confBarsSeriesPanel = new JPanel();
			confBarsSeriesPanel.setLayout(new GridBagLayout());
			confBarsSeriesPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

			confBarsSeriesPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".BARSWIDTH") + STTools.COLON), new GridConstraints(0, 0, GridBagConstraints.WEST)); //$NON-NLS-1$
			confBarsSeriesPanel.add(getTextBarsWidth(), new GridConstraints(1, 0, GridBagConstraints.WEST));

			confBarsSeriesPanel.add(new JLabel(STGraphC.getMessage(ChartWidget.PREFIX + ".BARSCOLOR") + STTools.COLON), new GridConstraints(2, 0, GridBagConstraints.WEST)); //$NON-NLS-1$
			confBarsSeriesPanel.add(getListBarsColors(), new GridConstraints(3, 0, GridBagConstraints.WEST));

		}
		return confBarsSeriesPanel;
	}


	/** Helper for the component listeners.
	 * @return selected index */
	private int getSelIndex() {
		int selIndex = listSeries.getSelectedIndex();
		if(selIndex == -1) { selIndex = 0; }
		return selIndex;
	}


	/** Helper for the text component listeners.
	 * @param t text
	 * @param def default
	 * @return text */
	private String fixText(final String t, final String def) {
		try {
			double x = Double.parseDouble(t);
			if(x >= 0) { return STTools.SHARP + t; }
		} catch (Exception e) { ; }
		STNode node = STGraph.getSTC().getCurrentGraph().getNodeByName(t); 
		if(node != null && node.isOutput()) { return t; } 
		return def;
	}


	/** Get the checkShowDots.
	 * @return checkbox */
	private JCheckBox getCheckShowDots() {
		if(checkShowDots == null) {
			checkShowDots = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".SHOWDOTS")); //$NON-NLS-1$
			checkShowDots.addActionListener(new ActionListener() { 
				@Override public void actionPerformed(final ActionEvent e) {
					if(listSeriesModel.getSize() > 0) {
						showDotsList.set(getSelIndex(), Boolean.valueOf(checkShowDots.isSelected()));
						visibilitySwitcher();	
					}
				}
			});
		}
		return checkShowDots;
	}


	/** Get the listDotsStyles.
	 * @return combobox */
	private JComboBox getListDotsStyles() {
		if(listDotsStyles == null) {
			listDotsStyles = new JComboBox();
			listDotsStyles.addActionListener(new ActionListener() { 
				@Override public void actionPerformed(final ActionEvent e) {    
					if(listSeriesModel.getSize() > 0) {
						int x = listDotsStyles.getSelectedIndex();
						if(x < ChartWidget.DOTSTYLE_VALUES.length) {
							dotsStyleList.set(getSelIndex(), ChartWidget.DOTSTYLE_VALUES[x]);
							dotsTypeList.set(getSelIndex(), ChartWidget.DOT);
						} else if(x < ChartWidget.DOTSTYLE_VALUES.length + ChartWidget.DEFSPRITE_VALUES.length) {
							dotsStyleList.set(getSelIndex(), ChartWidget.DEFSPRITE_VALUES[x - ChartWidget.DOTSTYLE_VALUES.length]);
							dotsTypeList.set(getSelIndex(), ChartWidget.SYSTEM_SPRITE);
						} else {
							dotsStyleList.set(getSelIndex(), (String)listDotsStyles.getSelectedItem());
							dotsTypeList.set(getSelIndex(), ChartWidget.USER_SPRITE);
						}
						visibilitySwitcher();
					}
				}
			});
		}
		listDotsStyles.removeAllItems();
		for(String s : STGraphC.getMessage(ChartWidget.PREFIX + ".DOTSTYLES").split(STTools.COMMA)) { listDotsStyles.addItem(s); } //$NON-NLS-1$
		for(String s : STGraphC.getMessage(ChartWidget.PREFIX + ".DEFSPRITES").split(STTools.COMMA)) { listDotsStyles.addItem(s); } //$NON-NLS-1$
		if(STGraph.getSTC().getCurrentGraph().getFileName() != null) {
			String curDir = STGraph.getSTC().getCurrentGraph().contextName;
			File[] files = (new File(curDir)).listFiles(new STFileFilter5());
			if(files != null && files.length > 0) {
				Arrays.sort(files);
				for(int i = 0; i < files.length; i++) { listDotsStyles.addItem(files[i].getName()); }
			}
		}
		return listDotsStyles;
	}


	/** Get the textDotsSize.
	 * @return textfield */
	private STTextField getTextDotsSize() {
		if(textDotsSize == null) {
			textDotsSize = new STTextField(this, false);
			textDotsSize.setMinimumSize(FIELD_DIM); textDotsSize.setMaximumSize(FIELD_DIM); textDotsSize.setPreferredSize(FIELD_DIM);
			textDotsSize.addFocusListener(new FocusListener() { 
				@Override public void focusGained(FocusEvent e) { ; }
				@Override public void focusLost(FocusEvent e) { dotsSizeUpdate(getSelIndex()); }
			});
			textDotsSize.addKeyListener(new KeyListener() {
				@Override public void keyTyped(KeyEvent e) { ; }
				@Override public void keyReleased(KeyEvent e) { ; }
				@Override public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ENTER) { dotsSizeUpdate(getSelIndex()); } }
			});
		}
		return textDotsSize;
	}

	/** Helper.
	 * @param i the index */
	private void dotsSizeUpdate(final int i) { if(listSeriesModel.getSize() > 0) { dotsSizeList.set(i, fixText(textDotsSize.getText(), dotsSizeDef)); } }


	/** Get the listDotsColors.
	 * @return combobox */
	private JComboBox getListDotsColors() {
		if(listDotsColors == null) {
			listDotsColors = new JComboBox();
			for(String s : STGraphC.getMessage(ChartWidget.PREFIX + ".COLORNAMES").split(STTools.COMMA)) { listDotsColors.addItem(s); } //$NON-NLS-1$
			listDotsColors.addActionListener(new ActionListener() { 
				@Override public void actionPerformed(final ActionEvent e) {
					if(listSeriesModel.getSize() > 0) { dotsColorList.set(getSelIndex(), ChartWidget.COLOR_VALUES[listDotsColors.getSelectedIndex()]); }
				}
			});
		}
		return listDotsColors;
	}


	/** Get the textDotsXScale.
	 * @return textfield */
	private STTextField getTextDotsXScale() {
		if(textDotsXScale == null) {
			textDotsXScale = new STTextField(this, false);
			textDotsXScale.setMinimumSize(FIELD_DIM2); textDotsXScale.setMaximumSize(FIELD_DIM2); textDotsXScale.setPreferredSize(FIELD_DIM2);
			textDotsXScale.addFocusListener(new FocusListener() { 
				@Override public void focusGained(FocusEvent e) { ; }
				@Override public void focusLost(FocusEvent e) { dotsXScaleUpdate(getSelIndex()); }
			});
			textDotsXScale.addKeyListener(new KeyListener() {
				@Override public void keyTyped(KeyEvent e) { ; }
				@Override public void keyReleased(KeyEvent e) { ; }
				@Override public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ENTER) { dotsXScaleUpdate(getSelIndex()); } }
			});
		}
		return textDotsXScale;
	}

	/** Helper.
	 * @param i the index */
	private void dotsXScaleUpdate(final int i) { if(listSeriesModel.getSize() > 0) { dotsXScaleList.set(i, fixText(textDotsXScale.getText(), dotsXScaleDef)); } }


	/** Get the textDotsYScale.
	 * @return textfield */
	private STTextField getTextDotsYScale() {
		if(textDotsYScale == null) {
			textDotsYScale = new STTextField(this, false);
			textDotsYScale.setMinimumSize(FIELD_DIM2); textDotsYScale.setMaximumSize(FIELD_DIM2); textDotsYScale.setPreferredSize(FIELD_DIM2);
			textDotsYScale.addFocusListener(new FocusListener() { 
				@Override public void focusGained(FocusEvent e) { ; }
				@Override public void focusLost(FocusEvent e) { dotsYScaleUpdate(getSelIndex()); }
			});
			textDotsYScale.addKeyListener(new KeyListener() {
				@Override public void keyTyped(KeyEvent e) { ; }
				@Override public void keyReleased(KeyEvent e) { ; }
				@Override public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ENTER) { dotsYScaleUpdate(getSelIndex()); } }
			});
		}
		return textDotsYScale;
	}

	/** Helper.
	 * @param i the index */
	private void dotsYScaleUpdate(final int i) { if(listSeriesModel.getSize() > 0) { dotsYScaleList.set(i, fixText(textDotsYScale.getText(), dotsYScaleDef)); } }


	/** Get the textDotsAngle.
	 * @return textfield */
	private STTextField getTextDotsAngle() {
		if(textDotsAngle == null) {
			textDotsAngle = new STTextField(this, false);
			textDotsAngle.setMinimumSize(FIELD_DIM2); textDotsAngle.setMaximumSize(FIELD_DIM2); textDotsAngle.setPreferredSize(FIELD_DIM2);
			textDotsAngle.addFocusListener(new FocusListener() { 
				@Override public void focusGained(FocusEvent e) { ; }
				@Override public void focusLost(FocusEvent e) { dotsAngleUpdate(getSelIndex()); }
			});
			textDotsAngle.addKeyListener(new KeyListener() {
				@Override public void keyTyped(KeyEvent e) { ; }
				@Override public void keyReleased(KeyEvent e) { ; }
				@Override public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ENTER) { dotsAngleUpdate(getSelIndex()); } }
			});
		}
		return textDotsAngle;
	}

	/** Helper.
	 * @param i the index */
	private void dotsAngleUpdate(final int i) { if(listSeriesModel.getSize() > 0) { dotsAngleList.set(i, fixText(textDotsAngle.getText(), dotsAngleDef)); } }


	/** Get the checkDotsLastOnly.
	 * @return checkbox */
	private JCheckBox getCheckDotsLastOnly() {
		if(checkDotsLastOnly == null) {
			checkDotsLastOnly = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".DOTSLASTONLY")); //$NON-NLS-1$
			checkDotsLastOnly.addActionListener(new ActionListener() { 
				@Override public void actionPerformed(final ActionEvent e) {
					if(listSeriesModel.getSize() > 0) { dotsLastOnlyList.set(getSelIndex(), Boolean.valueOf(checkDotsLastOnly.isSelected())); }
				}
			});
		}
		return checkDotsLastOnly;
	}


	/** Get the checkDotsHiLast.
	 * @return checkbox */
	private JCheckBox getCheckDotsHiLast() {
		if(checkDotsHiLast == null) {
			checkDotsHiLast = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".DOTSHILAST")); //$NON-NLS-1$
			checkDotsHiLast.addActionListener(new ActionListener() { 
				@Override public void actionPerformed(final ActionEvent e) {
					if(listSeriesModel.getSize() > 0) { dotsHiLastList.set(getSelIndex(), Boolean.valueOf(checkDotsHiLast.isSelected())); }
				}
			});
		}
		return checkDotsHiLast;
	}


	/** Get the checkShowLine.
	 * @return checkbox */
	private JCheckBox getCheckShowLine() {
		if(checkShowLine == null) {
			checkShowLine = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".SHOWLINE")); //$NON-NLS-1$
			checkShowLine.addActionListener(new ActionListener() { 
				@Override public void actionPerformed(final ActionEvent e) {
					if(listSeriesModel.getSize() > 0) {
						showLineList.set(getSelIndex(), Boolean.valueOf(checkShowLine.isSelected()));
						visibilitySwitcher();
					}
				}
			});
		}
		return checkShowLine;
	}


	/** Get the listLineStyles.
	 * @return combobox */
	private JComboBox getListLineStyles() {
		if(listLineStyles == null) {
			listLineStyles = new JComboBox();
			for(String s : STGraphC.getMessage(ChartWidget.PREFIX + ".LINESTYLES").split(STTools.COMMA)) { listLineStyles.addItem(s); } //$NON-NLS-1$
			listLineStyles.addActionListener(new ActionListener() {
				@Override public void actionPerformed(final ActionEvent e) {
					if(listSeriesModel.getSize() > 0) { lineStyleList.set(getSelIndex(), ChartWidget.LINESTYLE_VALUES[listLineStyles.getSelectedIndex()]); }
				}
			});
		}
		return listLineStyles;
	}


	/** Get the textLineWidth.
	 * @return textfield */
	private STTextField getTextLineWidth() {
		if(textLineWidth == null) {
			textLineWidth = new STTextField(this, false);
			textLineWidth.setMinimumSize(FIELD_DIM); textLineWidth.setMaximumSize(FIELD_DIM); textLineWidth.setPreferredSize(FIELD_DIM);
			textLineWidth.addFocusListener(new FocusListener() { 
				@Override public void focusGained(FocusEvent e) { ; }
				@Override public void focusLost(FocusEvent e) { lineWidthUpdate(getSelIndex()); }
			});
			textLineWidth.addKeyListener(new KeyListener() {
				@Override public void keyTyped(KeyEvent e) { ; }
				@Override public void keyReleased(KeyEvent e) { ; }
				@Override public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ENTER) { lineWidthUpdate(getSelIndex()); } }
			});
		}
		return textLineWidth;
	}

	/** Helper.
	 * @param i the index */
	private void lineWidthUpdate(final int i) { if(listSeriesModel.getSize() > 0) { lineWidthList.set(i, fixText(textLineWidth.getText(), lineWidthDef)); } }


	/** Get the listLineColors.
	 * @return combobox */
	private JComboBox getListLineColors() {
		if(listLineColors == null) {
			listLineColors = new JComboBox();
			for(String s : STGraphC.getMessage(ChartWidget.PREFIX + ".COLORNAMES").split(STTools.COMMA)) { listLineColors.addItem(s); } //$NON-NLS-1$
			listLineColors.addActionListener(new ActionListener() { 
				@Override public void actionPerformed(final ActionEvent e) {
					if(listSeriesModel.getSize() > 0) { lineColorList.set(getSelIndex(), ChartWidget.COLOR_VALUES[listLineColors.getSelectedIndex()]); }
				}
			});
		}
		return listLineColors;
	}


	/** Get the checkShowBars.
	 * @return checkbox */
	private JCheckBox getCheckShowBars() {
		if(checkShowBars == null) {
			checkShowBars = new JCheckBox(STGraphC.getMessage(ChartWidget.PREFIX + ".SHOWBARS")); //$NON-NLS-1$
			checkShowBars.addActionListener(new ActionListener() { 
				@Override public void actionPerformed(final ActionEvent e) {
					if(listSeriesModel.getSize() > 0) {
						showBarsList.set(getSelIndex(), Boolean.valueOf(checkShowBars.isSelected()));
						visibilitySwitcher();
					}
				}
			});
		}
		return checkShowBars;
	}


	/** Get the textBarsWidth.
	 * @return textfield */
	private STTextField getTextBarsWidth() {
		if(textBarsWidth == null) {
			textBarsWidth = new STTextField(this, false);
			textBarsWidth.setMinimumSize(FIELD_DIM); textBarsWidth.setMaximumSize(FIELD_DIM); textBarsWidth.setPreferredSize(FIELD_DIM);
			textBarsWidth.addFocusListener(new FocusListener() { 
				@Override public void focusGained(FocusEvent e) { ; }
				@Override public void focusLost(FocusEvent e) { barsWidthUpdate(getSelIndex()); }
			});
			textBarsWidth.addKeyListener(new KeyListener() {
				@Override public void keyTyped(KeyEvent e) { ; }
				@Override public void keyReleased(KeyEvent e) { ; }
				@Override public void keyPressed(KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ENTER) { barsWidthUpdate(getSelIndex()); } }
			});
		}
		return textBarsWidth;
	}

	/** Helper.
	 * @param i the index */
	private void barsWidthUpdate(final int i) { if(listSeriesModel.getSize() > 0) { barsWidthList.set(i, fixText(textBarsWidth.getText(), barsWidthDef)); } }


	/** Get the listBarsColors.
	 * @return combobox */
	private JComboBox getListBarsColors() {
		if(listBarsColors == null) {
			listBarsColors = new JComboBox();
			for(String s : STGraphC.getMessage(ChartWidget.PREFIX + ".COLORNAMES").split(STTools.COMMA)) { listBarsColors.addItem(s); } //$NON-NLS-1$
			listBarsColors.addActionListener(new ActionListener() { 
				@Override public void actionPerformed(final ActionEvent e) {
					if(listSeriesModel.getSize() > 0) { barsColorList.set(getSelIndex(), ChartWidget.COLOR_VALUES[listBarsColors.getSelectedIndex()]); }
				}
			});
		}
		return listBarsColors;
	}


	/** Helper to switch the visibility of panels and single components. */
	private void visibilitySwitcher() {
		//
		checkDotsHiLast.setVisible(false);
		labelLineStyles.setVisible(false);
		listLineStyles.setVisible(false);
		//
		boolean b = checkShowDots.isSelected();
		confDotsSeriesPanel.setVisible(b);
		if(b) {
			boolean bb = listDotsStyles.getSelectedIndex() < ChartWidget.DOTSTYLE_VALUES.length;
			labelDotsSize.setVisible(bb); textDotsSize.setVisible(bb);
			labelDotsColors.setVisible(bb); listDotsColors.setVisible(bb);
			labelDotsXScale.setVisible(!bb); textDotsXScale.setVisible(!bb);
			labelDotsYScale.setVisible(!bb); textDotsYScale.setVisible(!bb);
			labelDotsAngle.setVisible(!bb); textDotsAngle.setVisible(!bb);
		}
		confLineSeriesPanel.setVisible(checkShowLine.isSelected());
		confBarsSeriesPanel.setVisible(checkShowBars.isSelected());
		pack();
	}


	/** Action for the OK button. */
	protected void okHandler() {
		String[] xNames = null;
		String[] yNames = null;
		String[] dotsTypes = null;
		String[][] seriesData = null;
		int numSeries = listSeriesModel.getSize();
		if(numSeries > 0) {
			xNames = new String[numSeries];
			yNames = new String[numSeries];
			dotsTypes = new String[numSeries];
			seriesData = new String[ChartWidget.PROP_SERIESDATA.length][];
			for(int i = 0; i < ChartWidget.PROP_SERIESDATA.length; i++) { seriesData[i] = new String[numSeries]; }
			String[] t;
			for(int i = 0; i < numSeries; i++) {
				t = ((String)listSeriesModel.get(i)).split(", "); //$NON-NLS-1$
				xNames[i] = t[0];
				yNames[i] = t[1];
				dotsTypes[i] = dotsTypeList.get(i);
				seriesData[0][i] = showDotsList.get(i).toString();
				seriesData[1][i] = dotsStyleList.get(i);
				seriesData[2][i] = dotsSizeList.get(i);
				seriesData[3][i] = dotsColorList.get(i);
				seriesData[4][i] = dotsXScaleList.get(i);
				seriesData[5][i] = dotsYScaleList.get(i);
				seriesData[6][i] = dotsAngleList.get(i);
				seriesData[7][i] = dotsLastOnlyList.get(i).toString();
				seriesData[8][i] = dotsHiLastList.get(i).toString();
				seriesData[9][i] = showLineList.get(i).toString();
				seriesData[10][i] = lineStyleList.get(i);
				seriesData[11][i] = lineWidthList.get(i);
				seriesData[12][i] = lineColorList.get(i);
				seriesData[13][i] = showBarsList.get(i).toString();
				seriesData[14][i] = barsWidthList.get(i);
				seriesData[15][i] = barsColorList.get(i);
			}
		}

		double minX = 0.0;
		double stepX = 0.0;
		double maxX = 1.0;
		try { minX = Double.parseDouble(textMinX.getText()); } catch (Exception ex) { textMinX.setText("0.0"); } //$NON-NLS-1$
		try { stepX = Double.parseDouble(textStepX.getText()); } catch (Exception ex) { textStepX.setText("0.2"); } //$NON-NLS-1$
		try { maxX = Double.parseDouble(textMaxX.getText()); } catch (Exception ex) { textMaxX.setText("1.0"); } //$NON-NLS-1$
		if(minX >= maxX) {
			textMinX.setText("0.0"); textStepX.setText("0.2"); textMaxX.setText("1.0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			minX = 0.0; stepX = 0.2; maxX = 1.0;
		}
		double minY = 0.0;
		double stepY = 0.0;
		double maxY = 0.0;
		try { minY = Double.parseDouble(textMinY.getText()); } catch (Exception ex) { textMinY.setText("0.0"); } //$NON-NLS-1$
		try { stepY = Double.parseDouble(textStepY.getText()); } catch (Exception ex) { textStepY.setText("0.2"); } //$NON-NLS-1$
		try { maxY = Double.parseDouble(textMaxY.getText()); } catch (Exception ex) { textMaxY.setText("0.0"); } //$NON-NLS-1$
		if(minY >= maxY) {
			textMinY.setText("0.0"); textStepY.setText("0.2"); textMaxY.setText("1.0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			minY = 0.0; stepY = 0.2; maxY = 1.0;
		}
		if(!((ChartWidget)node).setProps(getTextTitle().getText(), numSeries, xNames, yNames, dotsTypes, seriesData, getCheckIsometric().isSelected(), getTypeX().getSelectedIndex(), getCheckAutoX().isSelected(), minX, stepX, maxX, getTypeY().getSelectedIndex(), getCheckAutoY().isSelected(), minY, stepY, maxY, getCheckAutoLegendX().isSelected(), textLegendX.getText(), getCheckAutoLegendY().isSelected(), textLegendY.getText(), getCheckLogscaleX().isSelected(), getCheckLogscaleY().isSelected())) { return; }
		((ChartWidgetView.ChartWidgetRenderer)((ChartWidget)node).view.getRenderer()).initView();
		completeExit();
	}

}
