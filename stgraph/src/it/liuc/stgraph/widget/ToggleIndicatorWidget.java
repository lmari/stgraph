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

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.nfunk.jep.type.Tensor;

import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.tools.LedColor;


/** ToggleIndicator widget. */
@SuppressWarnings("serial")
public class ToggleIndicatorWidget extends STWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "TOGGLEINDICATOR"; //$NON-NLS-1$
	/** The Constant ID. */
	public static final String ID = "ToggleIndicatorWidget"; //$NON-NLS-1$
	/** The Constant PROP_SOURCE_OB. */
	public static final String PROP_SOURCE_OB = "sourceob";	// reference to node //$NON-NLS-1$
	/** The Constant PROP_SOURCE_NA. */
	public static final String PROP_SOURCE_NA = "source"; // node name //$NON-NLS-1$
	/** The Constant PROP_COLOR. */
	public static final String PROP_COLOR = "color"; // string defined interactively (orientation: 0=red; 1=green) //$NON-NLS-1$
	/** The indicator. */
	private final Led indicator = new Led();


	/** Class constructor. */
	public ToggleIndicatorWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public ToggleIndicatorWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new ToggleIndicatorWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new ToggleIndicatorWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("TOGGLEINDICATOR.LABEL"); } //$NON-NLS-1$


	/** Get the indicator.
	 * @return indicator */
	public final Led getIndicator() { return indicator; }


	/** Set panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);

		indicator.setHorizontalAlignment(SwingConstants.CENTER);

		STNode datasource = (STNode)getProperty(ToggleIndicatorWidget.PROP_SOURCE_OB);
		Tensor v = null;
		if(datasource == null || (v = (Tensor)datasource.getValue()) == null || v.getValue() == 0.0) {
			indicator.setLedOn(false);
		} else {
			indicator.setLedOn(true);
		}

		indicator.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override
			public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		});

		panel.add(indicator, BorderLayout.CENTER);
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() {
		String w = (String)getProperty(ToggleIndicatorWidget.PROP_SOURCE_NA);
		if(w != null) { setTitle(w); }
	}


	/** Set the properties of this widget.
	 * @param name the name
	 * @param color the color
	 * @return result */
	final boolean setProps(final String name, final String color) {
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
			setProperty(ToggleIndicatorWidget.PROP_SOURCE_OB, node);
			setProperty(ToggleIndicatorWidget.PROP_SOURCE_NA, name);

			if(!STTools.isEmpty(color)) {
				setProperty(ToggleIndicatorWidget.PROP_COLOR, color);
				indicator.setLedColor(color.equals("0") ? LedColor.RED_LED : LedColor.GREEN_LED); //$NON-NLS-1$
			}
			setTitle();
			((ToggleIndicatorWidgetView.ToggleIndicatorWidgetRenderer)view.getRenderer()).initView();
			refresh();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_TOGGLEINDICATOR_FORMAT")); return false; //$NON-NLS-1$
		}
		return true;
	}


	/** Check whether the renamed node is referenced by the toggle indicator, and in that case set the new name accordingly.
	 * @param oldName the old name
	 * @param newName the new name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		String expr  = (String)getProperty(ToggleIndicatorWidget.PROP_SOURCE_NA);
		if(!STTools.isEmpty(expr) && expr.equals(oldName)) { setProperty(ToggleIndicatorWidget.PROP_SOURCE_NA, newName); }
		((ToggleIndicatorWidgetView.ToggleIndicatorWidgetRenderer)view.getRenderer()).initView();
		setTitle();
	}


	/** Handle the event of this widget removal. */
	public void handleWidgetRemoval() { ; }


	/** Check whether the removed node is referenced by the toggle indicator, and in that case set the name to <code>null</code>.
	 * @param name the name */
	public final void handleNodeRemoval(final String name) {
		String sname = (String)getProperty(ToggleIndicatorWidget.PROP_SOURCE_NA);
		if(sname != null && sname.equals(name)) {
			setProperty(ToggleIndicatorWidget.PROP_SOURCE_NA, null);
			setProperty(ToggleIndicatorWidget.PROP_SOURCE_OB, null);
		}
		((ToggleIndicatorWidgetView.ToggleIndicatorWidgetRenderer)view.getRenderer()).initView();
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	public final void prepareForSave() {
		String s;
		super.prepareForSave();
		AttributeMap am = getAttributes();
		if((s = (String)am.get(ToggleIndicatorWidget.PROP_SOURCE_NA)) != null) { dataToSave.put(ToggleIndicatorWidget.PROP_SOURCE_NA, s); }
		if((s = (String)am.get(ToggleIndicatorWidget.PROP_COLOR)) != null) { dataToSave.put(ToggleIndicatorWidget.PROP_COLOR, s); }
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String name = (String)dataToSave.get(ToggleIndicatorWidget.PROP_SOURCE_NA); // master property: this is assumed to be present
		if(name == null) { return; }
		String color = (String)dataToSave.get(ToggleIndicatorWidget.PROP_COLOR);
		if(color == null) { color = "0"; } // just as a default //$NON-NLS-1$
		setProps(name, color);
	}

}
