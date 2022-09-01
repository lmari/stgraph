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
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STTextField;
import it.liuc.stgraph.util.STTools;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/** NodeDialog handler for the OutputText widget. */
@SuppressWarnings("serial")
public class OutputTextWidgetDialog extends WidgetDialog {
	/** The scroll var. */
	private JScrollPane scrollVar = null;
	/** The list var. */
	@SuppressWarnings("rawtypes")
	private JList listVar = null;
	/** The text output var. */
	private STTextField textOutputVar = null;
	/** The button set output var. */
	private JButton buttonSetOutputVar = null;
	/** The font size. */
	private STTextField fontSize = null;


	/** Open the dialog, that was been created in a static way.
	 * @param node the node */
	public void open(final STWidget node) {
		this.node = node;
		startInit();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		getTextOutputVar().setEnabled(b); getListVar().setEnabled(b);
		getFontSize().setEnabled(b);
		completeInit();
	}


	/** Fill the dialog fields. */
	@SuppressWarnings("unchecked")
	protected void fill() {
		String p = (String)node.getProperty(OutputTextWidget.PROP_SOURCE_NA); getTextOutputVar().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		getListVar().setListData(STTools.getNamesFromNodeList(STGraph.getSTC().getCurrentGraph().getOutputNodeList()));
		Integer d = (Integer)node.getProperty(DataTableWidget.PROP_FONTSIZE); String dd = (d == null) ? "10" : d.toString(); getFontSize().setText(dd);
	}


	/** Initialize jContentPane.
	 * @return panel */
	protected JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			// output variable
			jContentPane.add(new JLabel(STGraphC.getMessage(OutputTextWidget.PREFIX + ".OUTPUTVARIABLE") + STTools.COLON),
					new GridConstraints(0, 0, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcOut2 = new GridConstraints(1, 0);
			gbcOut2.fill = GridBagConstraints.HORIZONTAL;
			gbcOut2.weightx = 1.0;
			jContentPane.add(getTextOutputVar(), gbcOut2);
			// available variables
			jContentPane.add(new JLabel(STGraphC.getMessage(OutputTextWidget.PREFIX + ".AVAILABLEVARIABLES") + STTools.COLON),
					new GridConstraints(1, 1, GridBagConstraints.WEST));
			//
			jContentPane.add(getButtonSetOutputVar(), new GridConstraints(0, 2, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcVar2 = new GridConstraints(1, 2);
			gbcVar2.fill = GridBagConstraints.BOTH;
			gbcVar2.weightx = 1.0; gbcVar2.weighty = 1.0;
			jContentPane.add(getScrollVar(), gbcVar2);
			// font size
			jContentPane.add(new JLabel(STGraphC.getMessage(OutputTextWidget.PREFIX + ".FONTSIZE") + STTools.COLON),
					new GridConstraints(0, 3, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcSize2 = new GridConstraints(1, 3);
			gbcSize2.anchor = GridBagConstraints.WEST;
			jContentPane.add(getFontSize(), gbcSize2);
			// ok-cancel
			GridConstraints gbcButt = new GridConstraints(0, 4);
			gbcButt.fill = GridBagConstraints.HORIZONTAL;
			gbcButt.gridwidth = 2;
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
	@SuppressWarnings("rawtypes")
	private JList getListVar() {
		if(listVar == null) {
			listVar = new JList();
			listVar.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

			listVar.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) { if(e.getClickCount() == 2) { buttonSetOutputVar.doClick(); } }
			});
		}
		return listVar;
	}


	/** Initialize textOutputVar.
	 * @return text */
	STTextField getTextOutputVar() {
		if(textOutputVar == null) { textOutputVar = new STTextField(this, false); }
		return textOutputVar;
	}


	/** Initialize buttonSetOutputVar.
	 * @return button */
	private JButton getButtonSetOutputVar() {
		if(buttonSetOutputVar == null) {
			buttonSetOutputVar = new JButton();
			buttonSetOutputVar.setText(STGraphC.getMessage(OutputTextWidget.PREFIX + ".SETASOUTPUT") + STTools.COLON); // Set as output variable
			buttonSetOutputVar.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					if(listVar.getSelectedIndex() == -1) { return; }
					Object obj = listVar.getSelectedValue();
					getTextOutputVar().setText((obj instanceof String) ? (String)obj : ((STNode)obj).getName());
				}
			});
		}
		return buttonSetOutputVar;
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
		String name = getTextOutputVar().getText();
		int fontSize = 10; // just a default...
		try {
			String d = getFontSize().getText();
			int i = Integer.parseInt(d);
			fontSize = i < 5 ? 10 : i;
		} catch (Exception e) { ; }
		if(!((OutputTextWidget)node).setProps(name, fontSize)) { return; }
		((OutputTextWidgetView.OutputTextWidgetRenderer)((OutputTextWidget)node).view.getRenderer()).initView();
		completeExit();
	}

}
