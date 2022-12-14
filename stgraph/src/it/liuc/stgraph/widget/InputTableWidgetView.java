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
import it.liuc.stgraph.util.STTools;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import org.jgraph.graph.VertexRenderer;
import org.nfunk.jep.type.Tensor;


/** View handler for the InputTable widget. */
@SuppressWarnings("serial")
public class InputTableWidgetView extends WidgetView {
	/** The widget. */
	private InputTableWidget widget;
	/** The Constant DEFAULT_FORMAT. */
	static final String DEFAULT_FORMAT = "###0.0###"; //$NON-NLS-1$
	/** The formatter. */
	private DecimalFormat myFormatter = null;
	/** The default table row height. */
	private int defRowHeight;


	/** Class constructor.
	 * @param _widget the widget */
	public InputTableWidgetView(final STWidget _widget) {
		super(_widget);
		setDefaultWidgetBounds(InputTableWidget.PREFIX + ".WIDGETBOUNDS"); //$NON-NLS-1$
		widget = (InputTableWidget)_widget;
		setRenderer(new InputTableWidgetRenderer());
	}


	/** Renderer. */
	public class InputTableWidgetRenderer extends VertexRenderer {


		/** Class constructor: initialize the table. */
		public InputTableWidgetRenderer() { initView(); }


		/** Init view. */
		public final void initView() {
			String format = (String)widget.getProperty(InputTableWidget.PROP_COL_FORMAT);
			myFormatter = new DecimalFormat(format != null ? format : DEFAULT_FORMAT);

			Integer p = (Integer)widget.getProperty(InputTableWidget.PROP_COL_ALIGNMENT);
			int alignment = (p == null) ? 0 : p.intValue();
			JTable table = widget.getTable();
			table.setDefaultRenderer(table.getColumnClass(0), new FormatRenderer(alignment));
			table.setTableHeader(null);
			TableColumnModel tcm = table.getColumnModel();
			for(int i = 0; i < table.getColumnCount(); i++) { tcm.getColumn(i).setCellEditor(new MyTableCellEditor()); }			
		}


		/** Paint method.
		 * @param g the g */
		@Override
		public final void paint(final Graphics g) {
			if(g == null) { return; }
			widget.setTitle();
			widget.frame.paint(g);
		}
	}


	/** The Class FormatRenderer. */
	private class FormatRenderer extends DefaultTableCellRenderer {


		/** The constructor.
		 * @param a the a */
		FormatRenderer(final int a) {
			switch(a) {
			case 0: setHorizontalAlignment(SwingConstants.RIGHT); break;
			case 1: setHorizontalAlignment(SwingConstants.CENTER); break;
			case 2: setHorizontalAlignment(SwingConstants.LEFT); break;
			default: break;
			}
		}


		/** Set value.
		 * @param value the value */
		@Override
		public void setValue(Object value) {
			if(value != null && value instanceof Tensor && ((Tensor)value).getSize() >= 0) { value = myFormatter.format(((Tensor)value).getValue()); }
			super.setValue(value);
		}

	}


	/** The Class MyTableCellEditor. */
	public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		// This is the component that handles the editing of a cell.
		JComponent component = new JTextField();
		
		// This method is called when a cell is edited by the user.
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int colIndex) {
			try {
				defRowHeight = table.getRowHeight();
				table.setRowHeight(rowIndex, defRowHeight + 20);	 // make it larger so to have it readable...
				component.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, ((Integer)widget.getProperty(InputTableWidget.PROP_FONTSIZE)).intValue()));
			} catch (Exception e) { ; }
			double val = ((Tensor)value).getValue();
			if(val == 0.0) {
				((JTextField)component).setText(STTools.BLANK);
			} else {
				((JTextField)component).setText(STTools.BLANK + val);
			}
			return component;
		}

		// This method is called when editing is completed.
		// It must return the new value to be stored in the cell.
		public Object getCellEditorValue() {
			widget.getTable().setRowHeight(defRowHeight);
			return ((JTextField)component).getText();
		}
	}

}
