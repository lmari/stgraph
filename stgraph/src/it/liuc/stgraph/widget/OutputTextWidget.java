/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2021, Luca Mari.
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
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.nfunk.jep.type.Tensor;


/** OutputText widget. */
@SuppressWarnings("serial")
public class OutputTextWidget extends STWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "OUTPUTTEXT";
	/** The Constant ID. */
	public static final String ID = "OutputTextWidget";
	/** The Constant PROP_SOURCE_OB. */
	public static final String PROP_SOURCE_OB = "sourceob";	// reference to node
	/** The Constant PROP_SOURCE_NA. */
	public static final String PROP_SOURCE_NA = "source"; // node name
	/** The Constant PROP_FONTSIZE: font size. */
	public static final String PROP_FONTSIZE = "fontsize";
	/** The indicator. */
	private final JLabel indicator = new JLabel();


	/** Class constructor. */
	public OutputTextWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public OutputTextWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new OutputTextWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new OutputTextWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("OUTPUTTEXT.LABEL"); }


	/** Get the indicator.
	 * @return indicator */
	public final JLabel getIndicator() { return indicator; }


	/** Set panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.WHITE);

		indicator.setHorizontalAlignment(SwingConstants.CENTER);
		indicator.setHorizontalTextPosition(SwingConstants.CENTER);
		indicator.setVerticalTextPosition(SwingConstants.BOTTOM);

		STNode datasource = (STNode)getProperty(OutputTextWidget.PROP_SOURCE_OB);
		Tensor v = null;
		if(datasource == null || (v = (Tensor)datasource.getValue()) == null) {
			indicator.setText(STTools.BLANK);
		} else {
			indicator.setText(datasource.getOutputText((int)v.getValue()));
		}

		indicator.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		});

		panel.add(indicator, BorderLayout.CENTER);
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() {
		String w = (String)getProperty(OutputTextWidget.PROP_SOURCE_NA);
		if(w != null) { setTitle(w); }
	}


	/** Set the properties of this widget.
	 * @param name the name
	 * @param fontSize the font size
	 * @return result */
	final boolean setProps(final String name, final int fontSize) {
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
			setProperty(OutputTextWidget.PROP_SOURCE_OB, node);
			setProperty(OutputTextWidget.PROP_SOURCE_NA, name);
			setProperty(OutputTextWidget.PROP_FONTSIZE, Integer.valueOf(fontSize));
			indicator.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, fontSize));
			setTitle();
			((OutputTextWidgetView.OutputTextWidgetRenderer)view.getRenderer()).initView();
			refresh();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_OUTPUTTEXT_FORMAT")); return false;
		}
		return true;
	}


	/** Check whether the renamed node is referenced by the toggle indicator, and in that case set the new name accordingly.
	 * @param oldName the old name
	 * @param newName the new name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		String expr  = (String)getProperty(OutputTextWidget.PROP_SOURCE_NA);
		if(!STTools.isEmpty(expr) && expr.equals(oldName)) { setProperty(OutputTextWidget.PROP_SOURCE_NA, newName); }
		((OutputTextWidgetView.OutputTextWidgetRenderer)view.getRenderer()).initView();
		setTitle();
	}


	/** Handle the event of this widget removal. */
	public void handleWidgetRemoval() { ; }


	/** Check whether the removed node is referenced by the toggle indicator, and in that case set the name to <code>null</code>.
	 * @param name the name */
	public final void handleNodeRemoval(final String name) {
		String sname = (String)getProperty(OutputTextWidget.PROP_SOURCE_NA);
		if(sname != null && sname.equals(name)) {
			setProperty(OutputTextWidget.PROP_SOURCE_NA, null);
			setProperty(OutputTextWidget.PROP_SOURCE_OB, null);
		}
		((OutputTextWidgetView.OutputTextWidgetRenderer)view.getRenderer()).initView();
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	public final void prepareForSave() {
		String s;
		super.prepareForSave();
		AttributeMap am = getAttributes();
		if((s = (String)am.get(OutputTextWidget.PROP_SOURCE_NA)) != null) { dataToSave.put(OutputTextWidget.PROP_SOURCE_NA, s); }
		STTools.mapObj(am, dataToSave, OutputTextWidget.PROP_FONTSIZE);
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String s;
		int fontSize = 10;
		String name = (String)dataToSave.get(OutputTextWidget.PROP_SOURCE_NA); // master property: this is assumed to be present
		if(name == null) { return; }
		if((s = (String)dataToSave.get(DataTableWidget.PROP_FONTSIZE)) != null) { fontSize = Integer.parseInt(s); }
		setProps(name, fontSize);
	}

}
