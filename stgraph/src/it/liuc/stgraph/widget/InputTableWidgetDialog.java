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
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STTextField;
import it.liuc.stgraph.util.STTools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


/** NodeDialog handler for the InputTable widget.  */
@SuppressWarnings("serial")
public class InputTableWidgetDialog extends WidgetDialog {
	/** The conf global panel. */
	private JPanel confGlobalPanel = null;
	/** The text rows. */
	private STTextField textRows = null;
	/** The text cols. */
	private STTextField textCols = null;
	/** The font size. */
	private STTextField fontSize = null;
	/** The format. */
	@SuppressWarnings("rawtypes")
	private JComboBox listConfFormats = null;
	/** The Constant INITIAL_FORMATS. */
	static final String[] INITIAL_FORMATS = { "###0.0###", "0.0", "0" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	/** The Constant DEFAULT_FORMAT. */
	static final String DEFAULT_FORMAT = "###0.0###"; //$NON-NLS-1$
	/** The list conf alignments. */
	@SuppressWarnings("rawtypes")
	private JComboBox listConfAlignments = null;
	/** The Constant ALIGNMENTS. */
	static final String[] ALIGNMENTS = { "right", "center", "left" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	/** The Constant DEFAULT_ALIGNMENT. */
	static final int DEFAULT_ALIGNMENT = 0;


	/** Open the dialog, that was been created in a static way.
	 * @param _node the _node */
	public void open(final STWidget _node) {
		node = _node;
		startInit();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		textRows.setEnabled(b); textCols.setEnabled(b); fontSize.setEnabled(b);
		listConfFormats.setEnabled(b); listConfAlignments.setEnabled(b);
		completeInit();
	}


	/** Fill the dialog fields. */
	@SuppressWarnings("unchecked")
	protected void fill() {
		String p = (String)node.getProperty(InputWidget.PROP_SOURCE_NA); getTextInputVar().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		Integer i = (Integer)node.getProperty(InputTableWidget.PROP_ROWS); textRows.setText((i == null) ? "1" : i.toString()); //$NON-NLS-1$
		i = (Integer)node.getProperty(InputTableWidget.PROP_COLS); textCols.setText((i == null) ? "1" : i.toString()); //$NON-NLS-1$

		p = (String)node.getProperty(InputTableWidget.PROP_COL_FORMAT);
		if(STTools.isEmpty(p)) {
			listConfFormats.setSelectedItem(DEFAULT_FORMAT);
		} else {
			int j;
			for(j = 0; j < INITIAL_FORMATS.length && !p.equals(INITIAL_FORMATS[j]); j++) { ; }
			if(j != INITIAL_FORMATS.length) { listConfFormats.setSelectedIndex(j); }
			else { listConfFormats.setSelectedItem(p); }
		}

		i = (Integer)node.getProperty(InputTableWidget.PROP_COL_ALIGNMENT);
		listConfAlignments.setSelectedIndex((i != null) ? i.intValue() : DEFAULT_ALIGNMENT);

		i = (Integer)node.getProperty(InputTableWidget.PROP_FONTSIZE); fontSize.setText((i == null) ? "10" : i.toString()); //$NON-NLS-1$
		getListAvailableVars().setListData(STTools.getNamesFromNodeList(STGraph.getSTC().getCurrentGraph().getUnboundInputNodeList()));
	}


	/** Initialize jContentPane.
	 * @return panel */
	protected JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			// input variable
			jContentPane.add(getLabelInputVar(), getLabelInputVarConstraints());
			jContentPane.add(getTextInputVar(), getTextInputVarConstraints());
			// available variables
			jContentPane.add(getLabelAvailableVars(), getLabelAvailableVarsConstraints());
			jContentPane.add(getButtonSetInputVar(), getButtonSetInputVarConstraints());
			jContentPane.add(getScrollAvailableVars(), getScrollAvailableVarsConstraints());
			// global configuration panel
			GridConstraints gbcGConf = new GridConstraints(3, 2, GridBagConstraints.NORTH);
			gbcGConf.weightx = 1.0;
			gbcGConf.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getConfGlobalPanel(), gbcGConf);
			// ok-cancel
			GridConstraints gbcButt = new GridConstraints(0, 7);
			gbcButt.fill = GridBagConstraints.HORIZONTAL;
			gbcButt.gridwidth = 3;
			jContentPane.add(getButtonPanel(), gbcButt);
		}
		return jContentPane;
	}


	/** Initialize confGlobalPanel.
	 * @return panel */
	private JPanel getConfGlobalPanel() {
		if(confGlobalPanel == null) {
			confGlobalPanel = new JPanel();
			confGlobalPanel.setLayout(new GridBagLayout());
			confGlobalPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			// rows
			JLabel labRows = new JLabel(STGraphC.getMessage(InputTableWidget.PREFIX + ".ROWNUMBER") + STTools.COLON); //$NON-NLS-1$
			GridConstraints gbcRows = new GridConstraints(0, 0);
			gbcRows.anchor = GridBagConstraints.NORTHEAST;
			confGlobalPanel.add(labRows, gbcRows);
			//
			GridConstraints gbcRows2 = new GridConstraints(1, 0);
			gbcRows2.anchor = GridBagConstraints.WEST;
			confGlobalPanel.add(getTextRows(), gbcRows2);
			// cols
			JLabel labCols = new JLabel(STGraphC.getMessage(InputTableWidget.PREFIX + ".COLNUMBER") + STTools.COLON); //$NON-NLS-1$
			GridConstraints gbcCols = new GridConstraints(0, 1);
			gbcCols.anchor = GridBagConstraints.NORTHEAST;
			confGlobalPanel.add(labCols, gbcCols);
			//
			GridConstraints gbcCols2 = new GridConstraints(1, 1);
			gbcCols2.anchor = GridBagConstraints.WEST;
			confGlobalPanel.add(getTextCols(), gbcCols2);
			// font size
			JLabel labSize = new JLabel(STGraphC.getMessage(InputTableWidget.PREFIX + ".FONTSIZE") + STTools.COLON); //$NON-NLS-1$
			GridConstraints gbcSize = new GridConstraints(0, 2);
			gbcSize.anchor = GridBagConstraints.NORTHWEST;
			confGlobalPanel.add(labSize, gbcSize);
			//
			GridConstraints gbcSize2 = new GridConstraints(1, 2);
			gbcSize2.anchor = GridBagConstraints.WEST;
			confGlobalPanel.add(getFontSize(), gbcSize2);
			// list of formats
			GridConstraints gbcLabFormats = new GridConstraints(0, 3);
			gbcLabFormats.anchor = GridBagConstraints.NORTHEAST;
			JLabel labFormats = new JLabel(STGraphC.getMessage(InputTableWidget.PREFIX + ".FORMAT") + STTools.COLON); //$NON-NLS-1$
			confGlobalPanel.add(labFormats, gbcLabFormats);
			//
			GridConstraints gbcFormats = new GridConstraints(1, 3);
			gbcFormats.anchor = GridBagConstraints.WEST;
			confGlobalPanel.add(getListConfFormats(), gbcFormats);
			// list of alignments
			GridConstraints gbcLabAlignments = new GridConstraints(0, 4);
			gbcLabAlignments.anchor = GridBagConstraints.NORTHEAST;
			JLabel labAlignments = new JLabel(STGraphC.getMessage(InputTableWidget.PREFIX + ".ALIGNMENT") + STTools.COLON); //$NON-NLS-1$
			confGlobalPanel.add(labAlignments, gbcLabAlignments);
			//
			GridConstraints gbcAlignments = new GridConstraints(1, 4);
			gbcAlignments.anchor = GridBagConstraints.WEST;
			confGlobalPanel.add(getListConfAlignments(), gbcAlignments);
		}
		return confGlobalPanel;
	}


	/** Initialize textRows.
	 * @return text */
	STTextField getTextRows() {
		if(textRows == null) {
			textRows = new STTextField(this, false);
			textRows.setPreferredSize(new Dimension(30, 21));
		}
		return textRows;
	}


	/** Initialize textCols.
	 * @return text */
	STTextField getTextCols() {
		if(textCols == null) {
			textCols = new STTextField(this, false);
			textCols.setPreferredSize(new Dimension(30, 21));
		}
		return textCols;
	}


	/** Initialize listConfFormats.
	 * @return format */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getListConfFormats() {
		if(listConfFormats == null) {
			listConfFormats = new JComboBox(INITIAL_FORMATS);
			listConfFormats.setEditable(true);
		}
		return listConfFormats;
	}


	/** Initialize listConfAlignments.
	 * @return alignment */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getListConfAlignments() {
		if(listConfAlignments == null) {
			listConfAlignments = new JComboBox(ALIGNMENTS);
			listConfAlignments.setEditable(false);
		}
		return listConfAlignments;
	}


	/** Initialize fontSize.
	 * @return text */    
	private STTextField getFontSize() {
		if(fontSize == null) {
			fontSize = new STTextField(this, false);
			fontSize.setPreferredSize(new Dimension(30, 21));
		}
		return fontSize;
	}


	/** Action for the OK button. */
	protected void okHandler() {
		String name = getTextInputVar().getText();
		int r = 1;
		int c = 1;
		try { r = Integer.parseInt(getTextRows().getText()); } catch (Exception ex) { getTextRows().setText("1"); } //$NON-NLS-1$
		try { c = Integer.parseInt(getTextCols().getText()); } catch (Exception ex) { getTextCols().setText("1"); } //$NON-NLS-1$
		int fontSize = 10; // just a default...
		try {
			String d = getFontSize().getText();
			int i = Integer.parseInt(d);
			fontSize = i < 5 ? 10 : i;
		} catch (Exception e) { ; }
		Object format = listConfFormats.getSelectedItem();
		try {
			new DecimalFormat(format.toString());
		} catch (Exception e) {
			format = DEFAULT_FORMAT;
		}
		if(!((InputTableWidget)node).setProps(name, r, c, (String)format, getListConfAlignments().getSelectedIndex(), fontSize)) { return; }
		((InputTableWidgetView.InputTableWidgetRenderer)((InputTableWidget)node).view.getRenderer()).initView();
		completeExit();
	}

}
