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
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STTextField;
import it.liuc.stgraph.util.STTools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/** NodeDialog handler for the DataTable widget. */
@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public class DataTableWidgetDialog extends WidgetDialog {
	/** The list series model. */
	private DefaultListModel listSeriesModel = new DefaultListModel();
	/** The scroll var. */
	private JScrollPane scrollVar = null;
	/** The list var. */
	private JList listVar = null;
	/** The scroll series. */
	private JScrollPane scrollSeries = null;
	/** The list series. */
	private JList listSeries = null;
	/** The button up. */
	private JButton buttonUp = null;
	/** The button down. */
	private JButton buttonDown = null;
	/** The button del. */
	private JButton buttonDel = null;
	/** The button add series. */
	private JButton buttonAddSeries = null;
	/** The conf global panel. */
	private JPanel confGlobalPanel = null;
	/** The check auto widths. */
	private JCheckBox checkAutoWidths = null;
	/** The font size. */
	private STTextField fontSize = null;
	/** The check last. */
	private JCheckBox checkLast = null;
	/** The conf series panel. */
	private JPanel confSeriesPanel = null;
	/** The list conf formats. */
	private JComboBox listConfFormats = null;
	/** The format list. */
	private ArrayList formatList = null;
	/** The Constant INITIAL_FORMATS. */
	static final String[] INITIAL_FORMATS = { "###0.0###", "0.0", "0" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	/** The Constant DEFAULT_FORMAT. */
	static final String DEFAULT_FORMAT = "###0.0###"; //$NON-NLS-1$
	/** The list conf alignments. */
	private JComboBox listConfAlignments = null;
	/** The alignment list. */
	private ArrayList alignmentList = null;
	/** The Constant DEFAULT_ALIGNMENTS . */
	static final int DEFAULT_ALIGNMENTS = 0;


	/** Open the dialog, that was been created in a static way.
	 * @param node the node */
	public void open(final STWidget node) {
		this.node = node;
		startInit();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		boolean bb = listSeriesModel != null && listSeriesModel.getSize() > 0;
		listSeries.setEnabled(b); listVar.setEnabled(b); checkLast.setEnabled(b);
		checkAutoWidths.setEnabled(b); fontSize.setEnabled(b);
		listConfFormats.setEnabled(b && bb); listConfAlignments.setEnabled(b && bb);
		if(((DataTableWidget)node).getTable() != null) { completeInit(); }
	}


	/** Fill the dialog fields. */
	protected void fill() {
		String[] p = (String[])node.getProperty(DataTableWidget.PROP_SOURCE_NA);
		listSeriesModel.clear();
		if(p != null && p.length > 0) {
			for(int i = 0; i < p.length; i++) { listSeriesModel.addElement(p[i].trim()); }

			formatList = new ArrayList();
			String[] formats = (String[])node.getProperty(DataTableWidget.PROP_COL_FORMAT);
			if(formats != null) {
				for(int i = 0; i < p.length; i++) { formatList.add(formats[i]); }
			} else { // default
				for(int i = 0; i < p.length; i++) { formatList.add(DEFAULT_FORMAT); }
			}

			alignmentList = new ArrayList();
			int[] aligns = (int[])node.getProperty(DataTableWidget.PROP_COL_ALIGNMENT);
			if(aligns != null) {
				for(int i = 0; i < p.length; i++) { alignmentList.add(Integer.valueOf(aligns[i])); }
			} else { // default
				for(int i = 0; i < p.length; i++) { alignmentList.add(Integer.valueOf(DEFAULT_ALIGNMENTS)); }
			}
		}
		getListSeries().setModel(listSeriesModel);
		if(listSeriesModel.getSize() > 0) { getListSeries().setSelectedIndex(0); }

		String[] further = { "vTime" , "vIndex" }; //$NON-NLS-1$ //$NON-NLS-2$
		getListVar().setListData(STTools.getNamesFromNodeList(STGraph.getSTC().getCurrentGraph().getOutputNodeList(), further));

		Boolean b = (Boolean)node.getProperty(DataTableWidget.PROP_LAST_ONLY);
		boolean bb = (b == null) ? false : b.booleanValue();
		getCheckLast().setSelected(bb);

		b = (Boolean)node.getProperty(DataTableWidget.PROP_AUTOWIDTH);
		bb = (b == null) ? true : b.booleanValue();
		getCheckAutoWidths().setSelected(bb);

		Integer d = (Integer)node.getProperty(DataTableWidget.PROP_FONTSIZE); String dd = (d == null) ? "10" : d.toString(); getFontSize().setText(dd); //$NON-NLS-1$

		setListConfModel();
	}


	/** Set list conf model. */
	void setListConfModel() {
		boolean b = listSeriesModel.getSize() > 0;
		listConfFormats.setEnabled(b);
		listConfAlignments.setEnabled(b);        
		int selItem = getListSeries().getSelectedIndex();
		if(selItem != -1) {
			String f = (String)formatList.get(selItem);
			int i;
			for(i = 0; i < INITIAL_FORMATS.length && !f.equals(INITIAL_FORMATS[i]); i++) { ; }
			if(i != INITIAL_FORMATS.length) { listConfFormats.setSelectedIndex(i); }
			else { listConfFormats.setSelectedItem(f); }
			listConfAlignments.setSelectedIndex(((Integer)alignmentList.get(selItem)).intValue());
		}
	}


	/** Initialize jContentPane.
	 * @return panel */
	protected JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			// series list
			jContentPane.add(new JLabel(STGraphC.getMessage(DataTableWidget.PREFIX + ".SERIESLIST") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.NORTHEAST));
			//
			jContentPane.add(getButtonUp(), new GridConstraints(0, 1, GridBagConstraints.NORTHEAST));
			//
			jContentPane.add(getButtonDown(), new GridConstraints(0, 2, GridBagConstraints.NORTHEAST));
			//
			jContentPane.add(getButtonDel(), new GridConstraints(0, 3, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcSeries2 = new GridConstraints(1, 0);
			gbcSeries2.gridheight = 4;
			gbcSeries2.weightx = 1.0; gbcSeries2.weighty = 1.0;
			gbcSeries2.fill = GridBagConstraints.BOTH;
			jContentPane.add(getScrollSeries(), gbcSeries2);
			// series configuration panel
			GridConstraints gbcSConf = new GridConstraints(3, 1, GridBagConstraints.NORTH);
			gbcSConf.weightx = 1.0;
			gbcSConf.gridheight = 4;
			gbcSConf.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getConfSeriesPanel(), gbcSConf);
			// available variables
			jContentPane.add(new JLabel(STGraphC.getMessage(DataTableWidget.PREFIX + ".AVAILABLEVARIABLES") + STTools.COLON),  //$NON-NLS-1$
					new GridConstraints(1, 4, GridBagConstraints.WEST));
			//
			GridConstraints gbcVar2 = new GridConstraints(1, 5);
			gbcVar2.fill = GridBagConstraints.BOTH;
			gbcVar2.gridwidth = 1;
			gbcVar2.weightx = 1.0; gbcVar2.weighty = 1.0;
			jContentPane.add(getScrollVar(), gbcVar2);
			//
			jContentPane.add(getButtonAddSeries(), new GridConstraints(0, 5, GridBagConstraints.NORTHEAST));
			// global configuration panel
			GridConstraints gbcGConf = new GridConstraints(3, 5, GridBagConstraints.NORTH);
			gbcGConf.weightx = 1.0;
			gbcGConf.gridheight = 4;
			gbcGConf.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getConfGlobalPanel(), gbcGConf);
			// ok-cancel
			GridConstraints gbcButt = new GridConstraints(0, 6);
			gbcButt.gridwidth = 2;
			gbcButt.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getButtonPanel(), gbcButt);
		}
		return jContentPane;
	}


	/** Initialize scrollVar.
	 * @return pane */    
	private JScrollPane getScrollVar() {
		if(scrollVar == null) {
			scrollVar = new JScrollPane();
			scrollVar.setViewportView(getListVar());
		}
		return scrollVar;
	}


	/** Initialize listVar.
	 * @return list */
	private JList getListVar() {
		if(listVar == null) {
			listVar = new JList();
			listVar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			listVar.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) { if(e.getClickCount() == 2) { buttonAddSeries.doClick(); } }
			});
		}
		return listVar;
	}


	/** Initialize scrollSeries.
	 * @return pane */
	private JScrollPane getScrollSeries() {
		if(scrollSeries == null) {
			scrollSeries = new JScrollPane();
			scrollSeries.setViewportView(getListSeries());
		}
		return scrollSeries;
	}


	/** Initialize listSeries.
	 * @return list */
	JList getListSeries() {
		if(listSeries == null) {
			listSeries = new JList();
			listSeries.addListSelectionListener(new ListSelectionListener() { 
				public void valueChanged(final ListSelectionEvent e) { setListConfModel(); }
			});
		}
		return listSeries;
	}


	/** Initialize buttonUp.
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

					o = formatList.get(index);
					formatList.set(index, formatList.get(index1));
					formatList.set(index1, o);

					o = alignmentList.get(index);
					alignmentList.set(index, alignmentList.get(index1));
					alignmentList.set(index1, o);

					getListSeries().setSelectedIndex(index1);
					getListSeries().ensureIndexIsVisible(index1);
					setListConfModel();
				}
			});
		}
		return buttonUp;
	}


	/** Initialize buttonDown.
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

					o = formatList.get(index);
					formatList.set(index, formatList.get(index1));
					formatList.set(index1, o);

					o = alignmentList.get(index);
					alignmentList.set(index, alignmentList.get(index1));
					alignmentList.set(index1, o);

					getListSeries().setSelectedIndex(index1);
					getListSeries().ensureIndexIsVisible(index1);
					setListConfModel();
				}
			});
		}
		return buttonDown;
	}


	/** Initialize buttonDel.
	 * @return button */    
	private JButton getButtonDel() {
		if(buttonDel == null) {
			buttonDel = new JButton(STGraphC.getMessage(DataTableWidget.PREFIX + ".DEL")); //$NON-NLS-1$
			buttonDel.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {    
					int index = getListSeries().getSelectedIndex();
					if(index == -1) { return; }
					listSeriesModel.remove(index);
					formatList.remove(index);
					alignmentList.remove(index);
					int size = listSeriesModel.getSize();
					if(size == 0) {
						listConfFormats.setEnabled(false);
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


	/** Initialize buttonAddSeries.
	 * @return button */
	private JButton getButtonAddSeries() {
		if(buttonAddSeries == null) {
			buttonAddSeries = new JButton(STGraphC.getMessage(DataTableWidget.PREFIX + ".ADDTOSERIES")); //$NON-NLS-1$
			buttonAddSeries.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if(listVar.getSelectedIndex() == -1) { return; }
					Object obj = listVar.getSelectedValue();
					String sel = (obj instanceof String) ? (String)obj : ((STNode)obj).getName();
					listSeriesModel.addElement(sel);
					int last = listSeriesModel.getSize() - 1;
					if(formatList == null) { formatList = new ArrayList(); }
					formatList.add(DEFAULT_FORMAT);
					if(alignmentList == null) { alignmentList = new ArrayList(); }
					alignmentList.add(Integer.valueOf(DEFAULT_ALIGNMENTS));
					getListSeries().setSelectedIndex(last);
					getListSeries().ensureIndexIsVisible(last);
					listConfFormats.setEnabled(true);
				}
			});
		}
		return buttonAddSeries;
	}


	/** Initialize confGlobalPanel.
	 * @return panel */
	private JPanel getConfGlobalPanel() {
		if(confGlobalPanel == null) {
			confGlobalPanel = new JPanel();
			confGlobalPanel.setLayout(new GridBagLayout());
			confGlobalPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			// font size
			confGlobalPanel.add(new JLabel(STGraphC.getMessage(DataTableWidget.PREFIX + ".FONTSIZE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.NORTHEAST));
			//
			confGlobalPanel.add(getFontSize(), new GridConstraints(1, 1, GridBagConstraints.WEST));
			// auto widths?
			GridConstraints gbcAutoWidths = new GridConstraints(0, 2, GridBagConstraints.WEST);
			gbcAutoWidths.gridwidth = 2;
			confGlobalPanel.add(getCheckAutoWidths(), gbcAutoWidths);
			// only last value?
			GridConstraints gbcLast = new GridConstraints(0, 3, GridBagConstraints.WEST);
			gbcLast.gridwidth = 2;
			confGlobalPanel.add(getCheckLast(), gbcLast);
		}
		return confGlobalPanel;
	}


	/** Initialize checkLast.
	 * @return checkbox */
	protected JCheckBox getCheckLast() {
		if(checkLast == null) {
			checkLast = new JCheckBox(STGraphC.getMessage(DataTableWidget.PREFIX + ".LASTONLY") + "?"); //$NON-NLS-1$ //$NON-NLS-2$
			checkLast.addKeyListener(new KeyAdapter() { public void keyReleased(final KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; } }
			});
		}
		return checkLast;
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


	/** Initialize confSeriesPanel.
	 * @return panel */
	private JPanel getConfSeriesPanel() {
		if(confSeriesPanel == null) {
			confSeriesPanel = new JPanel();
			confSeriesPanel.setLayout(new GridBagLayout());
			confSeriesPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			// list of formats
			confSeriesPanel.add(new JLabel(STGraphC.getMessage(DataTableWidget.PREFIX + ".FORMAT") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.NORTHEAST));
			//
			confSeriesPanel.add(getListConfFormats(), new GridConstraints(1, 1, GridBagConstraints.WEST));
			// list of alignments
			confSeriesPanel.add(new JLabel(STGraphC.getMessage(DataTableWidget.PREFIX + ".ALIGNMENT") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 2, GridBagConstraints.NORTHEAST));
			//
			confSeriesPanel.add(getListConfAlignments(), new GridConstraints(1, 2, GridBagConstraints.WEST));
		}
		return confSeriesPanel;
	}


	/** Initialize autoWidths.
	 * @return checkbox */
	protected JCheckBox getCheckAutoWidths() {
		if(checkAutoWidths == null) {
			checkAutoWidths = new JCheckBox(STGraphC.getMessage(DataTableWidget.PREFIX + ".AUTOWIDTHS") + "?"); //$NON-NLS-1$ //$NON-NLS-2$
			checkAutoWidths.addKeyListener(new KeyAdapter() { public void keyReleased(final KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; } }
			});
		}
		return checkAutoWidths;
	}


	/** Initialize listConfFormats.
	 * @return format */
	private JComboBox getListConfFormats() {
		if(listConfFormats == null) {
			listConfFormats = new JComboBox(INITIAL_FORMATS);
			listConfFormats.setEditable(true);
			listConfFormats.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) { formatItemChanger(); }
			});
			listConfFormats.addItemListener(new ItemListener() { 
				public void itemStateChanged(final ItemEvent e) { formatItemChanger(); }
			});
		}
		return listConfFormats;
	}


	/** Helper for the <code>listConfFormats</code> combo. */
	private void formatItemChanger() {
		int selItem = listSeries.getSelectedIndex();
		if(selItem == -1) { selItem = 0; }
		Object newFormat = listConfFormats.getSelectedItem();
		try {
			new DecimalFormat(newFormat.toString());
			formatList.set(selItem, newFormat);
		} catch (Exception e) {
			formatList.set(selItem, DEFAULT_FORMAT);
			listConfFormats.setSelectedItem(DEFAULT_FORMAT);
		}
	}


	/** Initialize listConfAlignments.
	 * @return alignment */
	private JComboBox getListConfAlignments() {
		if(listConfAlignments == null) {
			listConfAlignments = new JComboBox(STGraphC.getMessage(DataTableWidget.PREFIX + ".ALIGNMENTS").split(STTools.COMMA)); //$NON-NLS-1$
			listConfAlignments.setEditable(false);
			listConfAlignments.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {    
					int selItem = listSeries.getSelectedIndex();
					if(selItem == -1) { selItem = 0; }
					alignmentList.set(selItem, Integer.valueOf(listConfAlignments.getSelectedIndex()));
				}
			});
		}
		return listConfAlignments;
	}


	/** Action for the OK button. */
	protected void okHandler() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		int numSeries = listSeriesModel.getSize();
		if(numSeries == 0) {
			node.setProperty(DataTableWidget.PROP_SOURCE_OB, null);
			node.setProperty(DataTableWidget.PROP_SOURCE_NA, null);
			graph.setAsModified(true);
			setVisible(false);
			graph.setSelectionCell(node);
			((DataTableWidgetView.DataTableWidgetRenderer)((DataTableWidget)node).view.getRenderer()).initView();
			return;
		}
		String[] names = new String[numSeries];
		String[] formats = new String[numSeries];
		int[] aligns = new int[numSeries];
		for(int i = 0; i < numSeries; i++) {
			names[i] = (String)listSeriesModel.get(i);
			formats[i] = (String)formatList.get(i);
			aligns[i] = ((Integer)alignmentList.get(i)).intValue();
		}
		int fontSize = 10; // just a default...
		try {
			String d = getFontSize().getText();
			int i = Integer.parseInt(d);
			fontSize = i < 5 ? 10 : i;
		} catch (Exception e) { ; }
		if(!((DataTableWidget)node).setProps(names, formats, aligns, getCheckAutoWidths().isSelected(), getCheckLast().isSelected(), fontSize)) { return; }
		((DataTableWidgetView.DataTableWidgetRenderer)((DataTableWidget)node).view.getRenderer()).initView();
		completeExit();
	}

}
