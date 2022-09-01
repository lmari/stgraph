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
import it.liuc.stgraph.util.STTools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;

import eu.hansolo.steelseries.gauges.Radial;


/** Gauge widget. */
@SuppressWarnings("serial")
public class GaugeWidget extends STWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "GAUGE"; //$NON-NLS-1$
	/** The Constant ID. */
	public static final String ID = "GaugeWidget"; //$NON-NLS-1$
	/** The Constant PROP_SOURCE_OB. */
	public static final String PROP_SOURCE_OB = "sourceob";	// reference to node //$NON-NLS-1$
	/** The Constant PROP_SOURCE_NA. */
	public static final String PROP_SOURCE_NA = "sourcena"; // node name //$NON-NLS-1$
	/** The Constant PROP_MIN. */
	public static final String PROP_MIN = "min"; // string defined interactively (min value) //$NON-NLS-1$
	/** The Constant PROP_MAX. */
	public static final String PROP_MAX = "max"; // string defined interactively (max value) //$NON-NLS-1$
	/** The default min. */
	private static String defaultMin = "0.0"; //$NON-NLS-1$
	/** The default max. */
	private static String defaultMax = "10.0"; //$NON-NLS-1$
	/** The gauge. */
	private final Radial gauge = new Radial();


	/** Class constructor. */
	public GaugeWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public GaugeWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new GaugeWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new GaugeWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("GAUGE.LABEL"); } //$NON-NLS-1$


	/** Get the gauge.
	 * @return gauge */
	public final Radial getGauge() { return gauge; }


	/** Set panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);

		gauge.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override
			public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		});

		setProperty(GaugeWidget.PROP_MIN, defaultMin);
		setProperty(GaugeWidget.PROP_MAX, defaultMax);

		panel.add(gauge, BorderLayout.CENTER);
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() {
		String w = (String)getProperty(GaugeWidget.PROP_SOURCE_NA);
		if(w != null) { setTitle(w + STTools.COLON + STTools.SPACE + gauge.getValue()); }
	}


	/** Set the properties of this widget.
	 * @param name the name
	 * @param min the min
	 * @param max the max
	 * @return result */
	final boolean setProps(final String name, final String min, final String max) {
		try {
			STNode[] nodes = STGraph.getSTC().getCurrentGraph().getNodes();
			STNode node = null;
			boolean found = false;
			for(int i = 0; i < nodes.length; i++) {
				node = nodes[i];
				if(node.getName().equals(name)) {
					if(!node.isOutput()) { throw new Exception(); }
					found = true;
					break;
				}
			}
			if(!found) { throw new Exception(); }
			if(!STTools.isEmpty(min)) {
				setProperty(GaugeWidget.PROP_MIN, min);
				gauge.setMinValue(Double.parseDouble(min));
			}
			if(!STTools.isEmpty(max)) {
				setProperty(GaugeWidget.PROP_MAX, max);
				gauge.setMaxValue(Double.parseDouble(max));
			}
			gauge.setLcdDecimals(2);
			setProperty(GaugeWidget.PROP_SOURCE_OB, node);
			setProperty(GaugeWidget.PROP_SOURCE_NA, name);
			setTitle();
			((GaugeWidgetView.GaugeWidgetRenderer)view.getRenderer()).initView();
			refresh();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_GAUGE_FORMAT")); return false; //$NON-NLS-1$
		}
		return true;
	}


	/** Check whether the renamed node is referenced by the toggle indicator, and in that case set the new name accordingly.
	 * @param oldName the old name
	 * @param newName the new name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		String expr  = (String)getProperty(GaugeWidget.PROP_SOURCE_NA);
		if(!STTools.isEmpty(expr) && expr.equals(oldName)) { setProperty(GaugeWidget.PROP_SOURCE_NA, newName); }
		((GaugeWidgetView.GaugeWidgetRenderer)view.getRenderer()).initView();
		setTitle();
	}


	/** Handle the event of this widget removal. */
	public void handleWidgetRemoval() { ; }


	/** Check whether the removed node is referenced by the toggle indicator, and in that case set the name to <code>null</code>.
	 * @param name the name */
	public final void handleNodeRemoval(final String name) {
		String sname = (String)getProperty(GaugeWidget.PROP_SOURCE_NA);
		if(sname != null && sname.equals(name)) {
			setProperty(GaugeWidget.PROP_SOURCE_NA, null);
			setProperty(GaugeWidget.PROP_SOURCE_OB, null);
		}
		((GaugeWidgetView.GaugeWidgetRenderer)view.getRenderer()).initView();
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	public final void prepareForSave() {
		String s;
		super.prepareForSave();
		AttributeMap am = getAttributes();
		if((s = (String)am.get(GaugeWidget.PROP_SOURCE_NA)) != null) { dataToSave.put(GaugeWidget.PROP_SOURCE_NA, s); }
		if((s = (String)am.get(GaugeWidget.PROP_MIN)) != null) { dataToSave.put(GaugeWidget.PROP_MIN, s); }
		if((s = (String)am.get(GaugeWidget.PROP_MAX)) != null) { dataToSave.put(GaugeWidget.PROP_MAX, s); }
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String name = (String)dataToSave.get(GaugeWidget.PROP_SOURCE_NA);
		if(name == null) { return; }
		String min = (String)dataToSave.get(GaugeWidget.PROP_MIN);
		String max = (String)dataToSave.get(GaugeWidget.PROP_MAX);
		setProps(name, min, max);
	}

}
