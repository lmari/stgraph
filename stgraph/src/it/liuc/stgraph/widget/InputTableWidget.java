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
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.STTools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jgraph.graph.AttributeMap;
import org.nfunk.jep.type.Tensor;


/** InputTable widget. */
@SuppressWarnings("serial")
public class InputTableWidget extends STWidget implements InputWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "INPUTTABLE"; //$NON-NLS-1$
	/** The Constant ID. */
	public static final String ID = "InputTableWidget"; //$NON-NLS-1$
	/** The Constant PROP_ROWS. */
	public static final String PROP_ROWS = "rows"; // string defined interactively (number of rows value) //$NON-NLS-1$
	/** The Constant PROP_COLS. */
	public static final String PROP_COLS = "cols"; // string defined interactively (number of columns value) //$NON-NLS-1$
	/** The Constant PROP_FONTSIZE: font size. */
	public static final String PROP_FONTSIZE = "fontsize"; //$NON-NLS-1$
	/** The Constant PROP_COL_FORMAT: column format. */
	public static final String PROP_COL_FORMAT = "colformat"; //$NON-NLS-1$
	/** The Constant PROP_COL_ALIGNMENT: column alignment. */
	public static final String PROP_COL_ALIGNMENT = "colalignment"; //$NON-NLS-1$
	/** The model. */
	private final MyTableModel model = new MyTableModel();
	/** The table. */
	private final JTable table = new JTable(model);
	/** The scroll pane. */
	private final JScrollPane scrollPane = new JScrollPane(table);


	/** Class constructor. */
	public InputTableWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public InputTableWidget(final Object _userObject) { super(_userObject); }


	/** Return the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new InputTableWidgetView(this); }
		return view;
	}


	/** Return the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new InputTableWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("INPUTTABLE.LABEL"); }


	/** Get the table.
	 * @return table */
	public final JTable getTable() { return table; }


	/** Get the current value of this widget.
	 * @return value */
	@Override public final Object getValue() {
		int r = model.data.getDimensions()[0];
		int c = model.data.getDimensions()[1];
		if(r == 1 && c == 1) { return model.data.getSubTensor(0, 0); }
		if(c == 1) { return model.data.transpose(true).getSubTensor(0); }
		return model.data;
	}


	/** Get the next value of this widget.
	 * @param isFirst the is first
	 * @return value */
	@Override public final Object getNextValue(final boolean isFirst) { return getValue(); }


	/** Set the value for this widget.
	 * @param value the value */
	public final void setValue(final Object value) {
		model.data = new Tensor((String)value);
		if(model.data.getOrder() == 0) {
			Tensor t = Tensor.newMatrix(1, 1);
			t.setScalar(model.data, 0);
			model.data = t;
		}
		if(model.data.getOrder() == 1) {
			int size = model.data.getSize();
			Tensor t = Tensor.newMatrix(size, 1);
			for(int i = 0; i < size; i++) { t.setScalar(model.data.getScalar(i), i); }
			model.data = t;
		}
		model.fireTableStructureChanged();
	}


	/** Set whether it is possible to interact with this widget.
	 * @param state the state */
	@Override public final void setInteractable(final boolean state) {
		table.setEnabled(state);
	}


	/** Set panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBackground(Color.LIGHT_GRAY);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		Object val = getProperty(InputWidget.PROP_VALUE);
		if(val != null) { setValue(val); }

		table.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
		});
		table.addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) { }
			@Override public void keyPressed(KeyEvent e) { }
			@Override public void keyReleased(KeyEvent e) {
				if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
					try {
						((STGraphExec)graph).computeInteractively();
						((STGraphExec)graph).endExec(true);
					} catch (Exception ex) { }
				} else {
					((STGraphExec)graph).computeInteractivelyOnTheFly();
				}
				panel.requestFocus();
			}
		});
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		});
		frame.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		});

		table.setEnabled(true);
		panel.add(scrollPane, BorderLayout.CENTER);
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() {
		String w = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(w == null) {
			setProperty(STWidget.PROP_TITLE, STGraphC.getMessage("WIDGET.UNBOUND")); //$NON-NLS-1$
		} else {
			setProperty(STWidget.PROP_TITLE, w);
		}
		setTitle((String)getProperty(STWidget.PROP_TITLE));
	}


	/** Set the properties of this widget.
	 * @param name the name
	 * @param r the r
	 * @param c the c
	 * @param format the format
	 * @param alignment the alignment
	 * @param fontSize the font size
	 * @return result */
	final boolean setProps(final String name, final int r, final int c, final String format, final int alignment, final int fontSize) {
		try {
			
			if(STTools.isEmpty(name) && (STNode)getProperty(InputWidget.PROP_SOURCE_OB) != null) {
				((ValueNode)getProperty(InputWidget.PROP_SOURCE_OB)).unbindFromWidget();
				setProperty(InputWidget.PROP_SOURCE_OB, null);
				setProperty(InputWidget.PROP_SOURCE_NA, null);
				setTitle();
				return true;
			}
			
			if(!STTools.isEmpty(name)) {
				boolean found = false;
				for(STNode node : STGraph.getSTC().getCurrentGraph().getNodes()) {
					if(node.getName().equals(name)) {
						if(!node.isInput()) { throw new Exception(); } // it should not happen...
						STNode oldnode = (STNode)getProperty(InputWidget.PROP_SOURCE_OB);
						if(oldnode != null) { ((ValueNode)oldnode).unbindFromWidget(); }
						((ValueNode)node).bindToWidget(this);
						found = true;
						break;
					}
				}
				if(!found) { throw new Exception(); } // it should not happen...
			}
			setProperty(InputTableWidget.PROP_ROWS, Integer.valueOf(r));
			setProperty(InputTableWidget.PROP_COLS, Integer.valueOf(c));
			setProperty(InputTableWidget.PROP_COL_FORMAT, format);
			setProperty(InputTableWidget.PROP_COL_ALIGNMENT, Integer.valueOf(alignment));
			setProperty(InputTableWidget.PROP_FONTSIZE, Integer.valueOf(fontSize));
			table.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, fontSize));
			table.setRowHeight(fontSize + 2);
			if(r != model.data.getDimensions()[0] || c != model.data.getDimensions()[1]) {
				model.data = model.data.resize(new int[]{r, c}, true);
				model.fireTableStructureChanged();
			}
			setTitle();
			((InputTableWidgetView.InputTableWidgetRenderer)view.getRenderer()).initView(); // required because input widgets display their value even before the simulation is run...
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_INPUTTABLE_FORMAT")); return false; //$NON-NLS-1$
		}
		return true;
	}


	/** Check whether the renamed node is referenced by an input widget, and in that case set the new name of the source node accordingly.
	 * @param newName the new name
	 * @param oldName the old name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		String expr  = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(!STTools.isEmpty(expr) && expr.equals(oldName)) { setProperty(InputWidget.PROP_SOURCE_NA, newName); }
		setTitle();
	}


	/** Handle the event of this widget removal. */
	public void handleWidgetRemoval() { 
		String name = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(name == null) { return; }
		STNode node = getGraph().getNodeByName(name);
		if(node == null) { return; }
		((ValueNode)node).unbindFromWidget();
		((InputTableWidgetView.InputTableWidgetRenderer)view.getRenderer()).initView();
	}


	/** Check whether the removed node is referenced by an input widget, and in that case set its source node to <code>null</code>.
	 * @param name the name */
	public final void handleNodeRemoval(final String name) {
		String sname = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(sname != null && sname.equals(name)) {
			setProperty(InputWidget.PROP_SOURCE_NA, null);
			setProperty(InputWidget.PROP_SOURCE_OB, null);
			setProperty(STWidget.PROP_TITLE, null);
			refresh();
		}
		((InputTableWidgetView.InputTableWidgetRenderer)view.getRenderer()).initView();
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	public final void prepareForSave() {
		String s;
		Object o;
		super.prepareForSave();
		AttributeMap am = getAttributes();
		if((s = (String)am.get(InputWidget.PROP_SOURCE_NA)) != null) {
			dataToSave.put(InputWidget.PROP_SOURCE_NA, s);
			dataToSave.put(InputWidget.PROP_VALUE, getValue());
		}
		if((o = am.get(InputTableWidget.PROP_ROWS)) != null) { dataToSave.put(InputTableWidget.PROP_ROWS, o); }
		if((o = am.get(InputTableWidget.PROP_COLS)) != null) { dataToSave.put(InputTableWidget.PROP_COLS, o); }
		STTools.mapObj(am, dataToSave, DataTableWidget.PROP_COL_FORMAT);
		STTools.mapObj(am, dataToSave, DataTableWidget.PROP_COL_ALIGNMENT);
		STTools.mapObj(am, dataToSave, InputTableWidget.PROP_FONTSIZE);
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String s;
		int r = 1;
		int c = 1;
		String format = STTools.BLANK;
		int alignment = 0;
		int fontSize = 10;
		String name = (String)dataToSave.get(InputWidget.PROP_SOURCE_NA);
		if((s = (String)dataToSave.get(InputTableWidget.PROP_ROWS)) != null) { r = Integer.parseInt(s); }
		if((s = (String)dataToSave.get(InputTableWidget.PROP_COLS)) != null) { c = Integer.parseInt(s); }
		if((s = (String)dataToSave.get(InputTableWidget.PROP_COL_FORMAT)) != null) { format = s; }
		if((s = (String)dataToSave.get(InputTableWidget.PROP_COL_ALIGNMENT)) != null) { alignment = Integer.parseInt(s); }
		if((s = (String)dataToSave.get(InputTableWidget.PROP_FONTSIZE)) != null) { fontSize = Integer.parseInt(s); }
		setProps(name, r, c, format, alignment, fontSize);
		setValue(dataToSave.get(InputWidget.PROP_VALUE));
	}


	/** Table model class. */
	private class MyTableModel extends AbstractTableModel {
		/** The data. */
		private Tensor data;


		/** Class constructor. */
		MyTableModel() {
			data = Tensor.newMatrix(1, 1);
			data.setScalar(0.0, 0);
		}


		/** Get the current number of rows.
		 * @return num */
		public int getRowCount() { return data.getDimensions()[0]; }


		/** Get the current number of columns.
		 * @return num */
		public int getColumnCount() { return data.getDimensions()[1]; }


		/** Get the value at the given position.
		 * @param row the row index
		 * @param column the column index
		 * @return value */
		public Object getValueAt(final int row, final int column) { return data.getSubTensor(row, column); }


		/** Checks if is cell editable.
		 * @param row the row index
		 * @param column the column index
		 * @return true, if is cell editable */
		public boolean isCellEditable(final int row, final int column) { return true; }


		/** Set the value at.
		 * @param value the value
		 * @param row the row index
		 * @param column the column index */
		public void setValueAt(final Object value, final int row, final int column) {
			Tensor value2 = data.getSubTensor(row, column);
			try {
				data.setSubTensor(Tensor.newScalar(Double.valueOf((String)value).doubleValue()), row, column);
			} catch (Exception ex) {
				data.setSubTensor(value2, row, column);
			}
			fireTableCellUpdated(row, column);
		}

	}

}
