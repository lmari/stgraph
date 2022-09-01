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
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STTools;

import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jgraph.graph.VertexRenderer;
import org.nfunk.jep.type.Matrix;
import org.nfunk.jep.type.Tensor;


/** View handler for the DataTable widget.
 * The cases (currently) dealt with as the following:
 * <ul>
 * <li>scalar data source: its values are visualized as they are cumulated in a vector during the simulation</li>
 * <li>scalar data source / last value only: its last computed value (a single scalar) is visualized</li>
 * <li>vector data source / last value only or not vector output: its last computed value (a vector) is visualized</li>
 * <li>vector data source / not last value only or vector output: its values are visualized as they are cumulated in a matrix during the simulation</li>
 * <li>vTime or vIndex vector: its values are visualized as they are cumulated in a vector during the simulation</li>
 * <li>vTime or vIndex vector / last value only: its last computed value (a single scalar) is visualized</li>
 * <li>matrix data source: its last computed value (a matrix) is visualized</li>
 * </ul> */
@SuppressWarnings("serial")
public class DataTableWidgetView extends WidgetView {
	/** The widget. */
	private DataTableWidget widget;
	/** The Constant DEFAULT_FORMAT. */
	static final String DEFAULT_FORMAT = "###0.0###"; //$NON-NLS-1$


	/** Class constructor.
	 * @param _widget the widget */
	public DataTableWidgetView(final STWidget _widget) {
		super(_widget);
		setDefaultWidgetBounds(DataTableWidget.PREFIX + ".WIDGETBOUNDS"); //$NON-NLS-1$
		widget = (DataTableWidget)_widget;
		setRenderer(new DataTableWidgetRenderer());
	}


	/** Renderer. */
	public class DataTableWidgetRenderer extends VertexRenderer {
		/** The graph. */
		private STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		/** The series names. */
		private transient String[] seriesNames = null;
		/** The x series names. */
		private transient String[] xSeriesNames = null;
		/** The formats. */
		private transient String[] formats = null;
		/** The alignments. */
		private transient int[] alignments = null;
		/** The num series. */
		private int numSeries = 0;
		/** The datasource. */
		private transient STNode[] datasource = null;
		/** The model. */
		private DefaultTableModel model = null;
		/** The last value only. */
		private boolean lastValueOnly;
		/** The is manual resizing. */
		private boolean isManualResizing = true;
		/** The num columns. */
		private int numColumns = 0;


		/** Class constructor: initialize the table. */
		public DataTableWidgetRenderer() { initView(); }


		/** Init view. */
		public final void initView() {
			String title = STTools.BLANK;
			seriesNames = ((String[])widget.getProperty(DataTableWidget.PROP_SOURCE_NA));
			if(seriesNames != null) {
				numSeries = seriesNames.length;
				xSeriesNames = new String[numSeries];
				for(int i = 0; i < numSeries; i++) { title += (xSeriesNames[i] = STTools.setAlternateText(seriesNames[i])) + STTools.SPACE; }
				datasource = (STNode[])widget.getProperty(DataTableWidget.PROP_SOURCE_OB);
				formats = (String[])widget.getProperty(DataTableWidget.PROP_COL_FORMAT);
				alignments = (int[])widget.getProperty(DataTableWidget.PROP_COL_ALIGNMENT);
			}
			widget.setTitle(title);
			Boolean lvo = (Boolean)widget.getProperty(DataTableWidget.PROP_LAST_ONLY);
			lastValueOnly = (lvo != null) && lvo.booleanValue();
			widget.getTable().getColumnModel().addColumnModelListener(new TableColumnModelListener() {
				public void columnAdded(final TableColumnModelEvent e) { }
				public void columnMoved(final TableColumnModelEvent e) { }
				public void columnRemoved(final TableColumnModelEvent e) { }
				public void columnSelectionChanged(final ListSelectionEvent e) { }
				public void columnMarginChanged(final ChangeEvent e) {
					if(isManualResizing) {
						try {
							TableColumnModel t = widget.getTable().getColumnModel();
							TableColumn c = null;
							widget.setWidths(new int[numColumns]);
							for(int i = 0; i < numColumns; i++) {
								if((c = t.getColumn(i)) != null) { widget.getWidths()[i] = c.getWidth(); }
							}
							widget.setProperty(DataTableWidget.PROP_COL_WIDTH, widget.getWidths());
						} catch (Exception ex) { ; }
					}
				}
			});
		}


		/** Paint method.
		 * @param g the g */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public final void paint(final Graphics g) {
			JTable table = widget.getTable();
			if(seriesNames == null) { initView(); }
			int maxPoints = 0;
			if(seriesNames == null || datasource == null) { // null or invalid data
				table.setModel(model = new DefaultTableModel());
			} else {
				int[] numPoints = new int[numSeries];
				numColumns = numSeries;
				for(int i = 0; i < numSeries; i++) { // find the number of points / rows for each series / column
					if(seriesNames[i].equals("vIndex") || seriesNames[i].equals("vTime")) { //$NON-NLS-1$ //$NON-NLS-2$
						if(lastValueOnly || graph.getTimeFrame() == STGraphImpl.TIMEFRAME_PLAYMODE) { numPoints[i] = 1; }  
						else { numPoints[i] = (graph.currStep == -1) ? graph.vIndex.getSize() : 1 + graph.currStep; }
					} else if(datasource[i] == null) {
						numPoints[i] = 0;
					} else if(datasource[i].isScalar()) {
						Vector v = null;
						Object o = datasource[i].getValueHistory();
						if(o != null) {
							v = o instanceof Vector ? (Vector)o : ((Tensor)o).getVector();
						}
						if(lastValueOnly) { numPoints[i] = (v == null) ? 0 : 1; }
						else { numPoints[i] = (v == null) ? 0 : v.size(); }
					} else if(datasource[i].isVector()) {
						if(lastValueOnly || !datasource[i].isVectorOutput()) {
							numPoints[i] = ((Tensor)(datasource[i].getValue())).getSize();
						} else {
							Matrix m  = (Matrix)datasource[i].getValueHistory();
							if(m != null) {
								numPoints[i] = m.getRowCount();
								numColumns += (m.getColumnCount() - 1);
							}
						}
					} else if(datasource[i].isMatrix()) {
						Tensor m  = (Tensor)datasource[i].getValue();
						if(m != null && m.getSize() != -1) {
							numPoints[i] = m.getDimensions()[0];
							numColumns += (m.getDimensions()[1] - 1);
						}
					}
					if(numPoints[i] > maxPoints) { maxPoints = numPoints[i]; }
				}

				String[] ySeriesNames = new String[numColumns]; // update the column names
				int ii = 0;
				for(int i = 0; i < numSeries; i++) {
					if(seriesNames[i].equals("vIndex") || seriesNames[i].equals("vTime")) { //$NON-NLS-1$ //$NON-NLS-2$
						ySeriesNames[ii] = xSeriesNames[i];
						ii++;
					} else if(datasource[i].isVector() && !lastValueOnly && datasource[i].isVectorOutput()) {
						Matrix m  = (Matrix)datasource[i].getValueHistory();
						if(m != null) {
							for(int j = 0; j < m.getColumnCount(); j++) {
								ySeriesNames[ii] = xSeriesNames[i] + "[" + j + "]"; //$NON-NLS-1$ //$NON-NLS-2$
								ii++;
							}
						}
					} else if(datasource[i].isMatrix()) {
						Tensor m  = (Tensor)datasource[i].getValue();
						for(int j = 0; j < m.getDimensions()[1]; j++) {
							ySeriesNames[ii] = xSeriesNames[i] + "[" + j + "]"; //$NON-NLS-1$ //$NON-NLS-2$
							ii++;
						}
					} else {
						ySeriesNames[ii] = xSeriesNames[i];
						ii++;
					}
				}

				table.setModel(model = new DefaultTableModel(new Object[maxPoints][numColumns], ySeriesNames));
				TableColumnModel tcm = table.getColumnModel();

				if(numColumns == 1) {
					table.setTableHeader(null);
				} else {
					table.setTableHeader(new JTableHeader(tcm));
				}

				ii = 0;
				FormatRenderer[] r = new FormatRenderer[numSeries];
				for(int i = 0; i < numSeries; i++) {
					DecimalFormat myFormatter = new DecimalFormat(formats != null ? formats[i] : DEFAULT_FORMAT);
					r[i] = new FormatRenderer(alignments[i]);
					Vector v = null;
					if(seriesNames[i].equals("vIndex")) { //$NON-NLS-1$
						if(lastValueOnly) {
							v = new Vector(1);
							v.add(Tensor.newScalar(graph.currStep));
						} else {
							v = graph.vIndex.getVector();
						}
						tcm.getColumn(ii).setCellRenderer(r[i]);
					}
					else if(seriesNames[i].equals("vTime")) { //$NON-NLS-1$
						if(lastValueOnly) {
							v = new Vector(1);
							v.add(Tensor.newScalar(graph.currTime));
						} else {
							v = graph.vTime.getVector();
						}
						tcm.getColumn(ii).setCellRenderer(r[i]);
					} else if(datasource[i] != null) {
						if(datasource[i].isScalar()) {
							if(lastValueOnly && numPoints[i] == 1) {
								v = new Vector(1);
								v.add(datasource[i].getValue());
							} else {
								Object o = datasource[i].getValueHistory();
								if(o != null) {
									v = o instanceof Vector ? (Vector)o : ((Tensor)o).getVector();
								}
							}
							tcm.getColumn(ii).setCellRenderer(r[i]);
						} else if(datasource[i].isVector()) {
							if(lastValueOnly || !datasource[i].isVectorOutput()) {
								v = ((Tensor)datasource[i].getValue()).getVector();
								tcm.getColumn(ii).setCellRenderer(r[i]);
							} else {
								Matrix m  = (Matrix)datasource[i].getValueHistory();
								if(m != null) {
									for(int j = 0; j < m.getColumnCount(); j++) {
										v = ((Matrix)datasource[i].getValueHistory()).getColumn(j);
										Tensor d = null;
										for(int k = 0; k < numPoints[i]; k++) {
											try {
												d = (Tensor)v.get(k);
												String s = !d.isNumber() ? null : myFormatter.format(d.getValue());
												model.setValueAt(s, k, ii);
											} catch (Exception e) { }
										}
										tcm.getColumn(ii).setCellRenderer(r[i]);
										ii++;
									}
								}
							}
						} else if(datasource[i].isMatrix()) {
							Tensor m  = (Tensor)datasource[i].getValue();
							for(int j = 0; j < m.getDimensions()[1]; j++) {
								Tensor t = m.getColumn(j);
								Tensor d = null;
								for(int k = 0; k < numPoints[i]; k++) {
									try {
										d = t.getScalar(k);
										String s = !d.isNumber() ? null : myFormatter.format(d.getValue());
										model.setValueAt(s, k, ii);
									} catch (Exception e) { ; }
								}
								tcm.getColumn(ii).setCellRenderer(r[i]);
								ii++;
							}
						}
					}
					if(seriesNames[i].equals("vIndex") || seriesNames[i].equals("vTime") || datasource[i].isScalar() || (datasource[i].isVector() && (lastValueOnly || !datasource[i].isVectorOutput()))) { //$NON-NLS-1$ //$NON-NLS-2$
						Tensor d = null;
						for(int j = 0; j < numPoints[i]; j++) {
							if(v != null) {
								d = (Tensor)v.get(j);
								String s = !d.isNumber() ? null : myFormatter.format(d.getValue());
								model.setValueAt(s, j, ii);
							}
						}
						ii++;
					}
				}

				if(widget.getWidths() != null) {
					isManualResizing = false;
					TableColumn c = null;
					try {
						for(int i = 0; i < numColumns; i++) {
							if((c = tcm.getColumn(i)) != null) { c.setPreferredWidth(widget.getWidths()[i]); }
						}
					} catch (Exception e) { ; }
				}
				isManualResizing = true;
			}

			if(maxPoints > 0 && g != null) {
				widget.getScroller().setViewportView(table);
				if(!lastValueOnly) { 
					table.changeSelection(maxPoints - 1, 0, false, false);
				} else {
					table.changeSelection(0, 0, false, false);
				}
				widget.frame.paint(g);
			}
		}
	}


	/** The Class FormatRenderer. */
	private class FormatRenderer extends DefaultTableCellRenderer {


		/** The Constructor.
		 * @param a the a */
		FormatRenderer(final int a) {
			switch(a) {
			case 0: setHorizontalAlignment(SwingConstants.RIGHT); break;
			case 1: setHorizontalAlignment(SwingConstants.CENTER); break;
			case 2: setHorizontalAlignment(SwingConstants.LEFT); break;
			default: break;
			}
		}


		/** Set the value.
		 * @param obj the obj */
		@Override
		protected void setValue(final Object obj) { setText(obj == null ? STTools.BLANK : obj.toString()); }

	}

}
