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
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STTextField;
import it.liuc.stgraph.util.STTools;

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


/** NodeDialog handler for the Matrix viewer widget. */
@SuppressWarnings("serial")
public class MatrixViewerWidgetDialog extends WidgetDialog {
	/** The scroll var. */
	private JScrollPane scrollVar = null;
	/** The list var. */
	@SuppressWarnings("rawtypes")
	private JList listVar = null;
	/** The text output var. */
	private STTextField textOutputVar = null;
	/** The button set output var. */
	private JButton buttonSetOutputVar = null;


	/** Open the dialog, that was been created in a static way.
	 * @param node the node */
	public void open(final STWidget node) {
		this.node = node;
		startInit();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		getTextOutputVar().setEnabled(b); getListVar().setEnabled(b);
		completeInit();
	}


	/** Fill the dialog fields. */
	@SuppressWarnings("unchecked")
	protected void fill() {
		MatrixViewerWidget n = (MatrixViewerWidget)node;
		String p = n.getSourceName(); getTextOutputVar().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		getListVar().setListData(STTools.getNamesFromNodeList(STGraph.getSTC().getCurrentGraph().getOutputNodeList()));
	}


	/** Initialize jContentPane.
	 * @return panel */
	protected JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			// output variable
			jContentPane.add(new JLabel(STGraphC.getMessage(MatrixViewerWidget.PREFIX + ".OUTPUTVARIABLE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcOut2 = new GridConstraints(1, 0);
			gbcOut2.fill = GridBagConstraints.HORIZONTAL;
			gbcOut2.weightx = 1.0;
			jContentPane.add(getTextOutputVar(), gbcOut2);
			// available variables
			jContentPane.add(new JLabel(STGraphC.getMessage(MatrixViewerWidget.PREFIX + ".AVAILABLEVARIABLES") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(1, 1, GridBagConstraints.WEST));
			//
			jContentPane.add(getButtonSetOutputVar(), new GridConstraints(0, 2, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcVar2 = new GridConstraints(1, 2);
			gbcVar2.fill = GridBagConstraints.BOTH;
			gbcVar2.weightx = 1.0; gbcVar2.weighty = 1.0;
			jContentPane.add(getScrollVar(), gbcVar2);
			// message
			GridConstraints gbcMsg = new GridConstraints(0, 3);
			gbcMsg.gridwidth = 2;
			jContentPane.add(new JLabel(STGraphC.getMessage(MatrixViewerWidget.PREFIX + ".MESSAGE")), gbcMsg); //$NON-NLS-1$
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
			buttonSetOutputVar.setText(STGraphC.getMessage(MatrixViewerWidget.PREFIX + ".SETASOUTPUT") + STTools.COLON); // Set as output variable //$NON-NLS-1$
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


	/** Action for the OK button. */
	protected void okHandler() {
		String name = getTextOutputVar().getText();
		if(!((MatrixViewerWidget)node).setProps(name)) { return; }
		completeExit();
	}

}
