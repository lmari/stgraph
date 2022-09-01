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
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STTools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;


/** DataTable widget. */
@SuppressWarnings("serial")
public class DataTableWidget extends STWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "DATATABLE"; //$NON-NLS-1$
	/** The Constant ID. */
	public static final String ID = "DataTableWidget"; //$NON-NLS-1$
	/** The Constant PROP_SOURCE_OB: array of nodes. */
	public static final String PROP_SOURCE_OB = "sourceob"; //$NON-NLS-1$
	/** The Constant PROP_SOURCE_NA: array of node names. */
	public static final String PROP_SOURCE_NA = "sourcena"; //$NON-NLS-1$
	/** The Constant PROP_LAST_ONLY: check whether only last values should be displayed. */
	public static final String PROP_LAST_ONLY = "lastonly"; //$NON-NLS-1$
	/** The Constant PROP_COL_FORMAT: array of column formats. */
	public static final String PROP_COL_FORMAT = "colformat"; //$NON-NLS-1$
	/** The Constant PROP_COL_ALIGNMENT: array of column alignments. */
	public static final String PROP_COL_ALIGNMENT = "colalignment"; //$NON-NLS-1$
	/** The Constant PROP_AUTOWIDTH: check whether the column widths should be set automatically. */
	public static final String PROP_AUTOWIDTH = "autowidth"; //$NON-NLS-1$
	/** The widths. */
	private transient int[] widths;
	/** The Constant PROP_COL_WIDTH: array of column widths. */
	public static final String PROP_COL_WIDTH = "colwidth"; //$NON-NLS-1$
	/** The Constant PROP_FONTSIZE: font size. */
	public static final String PROP_FONTSIZE = "fontsize"; //$NON-NLS-1$
	/** The scroller. */
	private final JScrollPane scroller = new JScrollPane();
	/** The table. */
	private final JTable table = new JTable();


	/** Class constructor. */
	public DataTableWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the user object */
	public DataTableWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new DataTableWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new DataTableWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("DATATABLE.LABEL"); } //$NON-NLS-1$


	/** Set the widths.
	 * @param widths the widths  */
	public final void setWidths(final int[] widths) { this.widths = widths; }


	/** Get the widths.
	 * @return widths */
	public final int[] getWidths() { return widths; }


	/** Get the scroller.
	 * @return scroller */
	public final JScrollPane getScroller() { return scroller; }


	/** Get the table.
	 * @return table */
	public final JTable getTable() { return table; }


	/** Set panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BorderLayout());

		scroller.addMouseListener(new MouseAdapter() { 
			@Override
			public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override
			public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		});

		table.addMouseListener(new MouseAdapter() { 
			@Override
			public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override
			public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		});

		table.setEnabled(false);
		table.setBackground(new Color(240, 240, 240));
		table.setSelectionForeground(Color.BLUE);
		table.setSelectionBackground(Color.WHITE);
		panel.add(scroller, BorderLayout.CENTER);
	}


	/** Set the title to be displayed in the frame of this widget. */
	public void setTitle() { ; } // complex procedure, directly implemented in the setProps() method, where it is only used


	/** Set the properties of this widget.
	 * @param names the names
	 * @param formats the formats
	 * @param alignments the alignments
	 * @param autoWidths the auto widths
	 * @param lastOnly the last only
	 * @param fontSize the font size
	 * @return result */
	final boolean setProps(final String[] names, final String[] formats, final int[] alignments, final boolean autoWidths, final boolean lastOnly, final int fontSize) {
		try {
			String title = STTools.BLANK;
			int numSeries = (names == null) ? 0 : names.length;
			STNode[] series = null;
			if(numSeries > 0) { series = new STNode[numSeries]; }
			STNode[] nodes = STGraph.getSTC().getCurrentGraph().getNodes();
			for(int j = 0; j < numSeries; j++) {
				title += STTools.setAlternateText(names[j]) + STTools.SPACE;
				if(!names[j].equals("vIndex") && !names[j].equals("vTime")) { //$NON-NLS-1$ //$NON-NLS-2$
					boolean found = false;
					for(int i = 0; i < nodes.length; i++) {
						String name = nodes[i].getName();
						if(name.equals(names[j])) {
							if(!nodes[i].isOutput()) { throw new Exception(); }
							series[j] = nodes[i];
							found = true;
							break;
						}
					}
					if(!found) { throw new Exception(); }
				}
			}
			setTitle(title);
			setProperty(DataTableWidget.PROP_SOURCE_OB, series);
			setProperty(DataTableWidget.PROP_SOURCE_NA, names);
			setProperty(DataTableWidget.PROP_COL_FORMAT, formats);
			setProperty(DataTableWidget.PROP_COL_ALIGNMENT, alignments);
			setProperty(DataTableWidget.PROP_AUTOWIDTH, Boolean.valueOf(autoWidths));
			if(autoWidths) {
				table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			} else {
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
			setProperty(DataTableWidget.PROP_LAST_ONLY, Boolean.valueOf(lastOnly));
			setProperty(DataTableWidget.PROP_FONTSIZE, Integer.valueOf(fontSize));
			table.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, fontSize));
			table.setRowHeight(fontSize + 2);
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_TABLE_FORMAT")); return false; //$NON-NLS-1$
		}
		return true;
	}


	/** Check whether the renamed node is referenced by the table, and in that case set the new name accordingly.
	 * @param newName the new name
	 * @param oldName the old name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		String[] names = (String[])getProperty(DataTableWidget.PROP_SOURCE_NA);
		if(names != null) {
			for(int j = 0; j < names.length; j++) {
				if(names[j].equals(oldName)) { names[j] = newName; }
			}
			setProperty(DataTableWidget.PROP_SOURCE_NA, names);
		}
		((DataTableWidgetView.DataTableWidgetRenderer)view.getRenderer()).initView();
	}


	/** Handle the event of this widget removal. */
	public void handleWidgetRemoval() { ; }


	/** Check whether the removed node is referenced by the graph, and in that case set the name to <code>null</code>.
	 * @param name the name */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void handleNodeRemoval(final String name) {
		String[] names = (String[])getProperty(DataTableWidget.PROP_SOURCE_NA);
		STNode[] series = (STNode[])getProperty(DataTableWidget.PROP_SOURCE_OB);
		if(names != null) {
			ArrayList newNames = new ArrayList();
			ArrayList newSeries = new ArrayList();
			for(int j = 0; j < names.length; j++) {
				if(!names[j].equals(name)) {
					newNames.add(names[j]);
					if(!names[j].equals("vIndex") && !names[j].equals("vTime")) { //$NON-NLS-1$ //$NON-NLS-2$
						newSeries.add(series[j]);
					} else {
						newSeries.add(null);
					}
				}
			}
			if(newNames.size() > 0) {
				String[] newNames2 = new String[newNames.size()];
				STNode[] newSeries2 = new STNode[newNames.size()];
				for(int i = 0; i < newNames.size(); i++) {
					newNames2[i] = (String)newNames.get(i);
					newSeries2[i] = (STNode)newSeries.get(i);
				}
				setProperty(DataTableWidget.PROP_SOURCE_NA, newNames2);
				setProperty(DataTableWidget.PROP_SOURCE_OB, newSeries2);
			} else  {
				setProperty(DataTableWidget.PROP_SOURCE_NA, null);
				setProperty(DataTableWidget.PROP_SOURCE_OB, null);
			}
		}
		((DataTableWidgetView.DataTableWidgetRenderer)view.getRenderer()).initView();
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	public final void prepareForSave() {
		super.prepareForSave();
		AttributeMap am = getAttributes();
		STTools.mapObjArray(am, dataToSave, DataTableWidget.PROP_SOURCE_NA);
		STTools.mapObjArray(am, dataToSave, DataTableWidget.PROP_COL_FORMAT);
		STTools.mapIntArray(am, dataToSave, DataTableWidget.PROP_COL_ALIGNMENT);
		STTools.mapObj(am, dataToSave, DataTableWidget.PROP_AUTOWIDTH);
		STTools.mapIntArray(am, dataToSave, DataTableWidget.PROP_COL_WIDTH);
		STTools.mapObj(am, dataToSave, DataTableWidget.PROP_LAST_ONLY);
		STTools.mapObj(am, dataToSave, DataTableWidget.PROP_FONTSIZE);
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		int numSeries;
		String[] names = null;
		boolean lastOnly = false;
		boolean autoWidths = true;
		int fontSize = 10;
		String s = (String)dataToSave.get(DataTableWidget.PROP_SOURCE_NA); // master property: this is assumed to be present
		if(s == null) { return; }
		names = s.split(","); //$NON-NLS-1$
		numSeries = names.length;
		String[] formats = STTools.putStringArray(dataToSave.get(DataTableWidget.PROP_COL_FORMAT), numSeries, DataTableWidgetDialog.DEFAULT_FORMAT);
		int[] alignments = STTools.putIntArray(dataToSave.get(DataTableWidget.PROP_COL_ALIGNMENT), numSeries, DataTableWidgetDialog.DEFAULT_ALIGNMENTS);
		if((s = (String)dataToSave.get(DataTableWidget.PROP_AUTOWIDTH)) != null) { autoWidths = Boolean.valueOf(s).booleanValue(); }
		widths = STTools.putIntArray(dataToSave.get(DataTableWidget.PROP_COL_WIDTH), numSeries, 50);
		if((s = (String)dataToSave.get(DataTableWidget.PROP_LAST_ONLY)) != null) { lastOnly = Boolean.valueOf(s).booleanValue(); }
		if((s = (String)dataToSave.get(DataTableWidget.PROP_FONTSIZE)) != null) { fontSize = Integer.parseInt(s); }
		setProps(names, formats, alignments, autoWidths, lastOnly, fontSize);
	}

}
