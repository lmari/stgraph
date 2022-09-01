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

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


/**
 * Widget dialog handler.
 */
@SuppressWarnings("serial")
public abstract class WidgetDialog extends JDialog {
	/** The registered dialogs. */
	@SuppressWarnings("rawtypes")
	public static Vector registeredDialogs = new Vector();
	/** The node. */
	protected STWidget node = null;
	/** The jContentPane. */
	protected JPanel jContentPane = null;
	/** The button panel. */
	protected JPanel buttonPanel = null;
	/** The button ok. */
	protected JButton buttonOk = null;
	/** The button cancel. */
	protected JButton buttonCancel = null;
	/** The text input var. */
	private STTextField textInputVar = null;
	/** The button set input var. */
	JButton buttonSetInputVar = null;
	/** The scroll available vars. */
	private JScrollPane scrollAvailableVars = null;
	/** The list available vars. */
	@SuppressWarnings("rawtypes")
	JList listAvailableVars = null;


	/** Class constructor. */
	@SuppressWarnings("unchecked")
	public WidgetDialog() {
		registeredDialogs.add(this);
		if(STGraphC.getContainer() instanceof JFrame) { setAlwaysOnTop(true); }
		setLocation(STGraphC.getContainer().getX() + 50, STGraphC.getContainer().getY() + 50);
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setModal(true);
		setIconImage(STGraphC.getSystemIcon());
	}


	/** Open method.
	 * @param node the node */
	abstract void open(STWidget node);


	/** Begin the dialog initialization. */
	protected void startInit() {
		setContentPane(getJContentPane());
		setTitle(STGraphC.getMessage("WIDGET.DIALOG.EDITTITLE")); //$NON-NLS-1$
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		getTextInputVar().setEnabled(b); getListAvailableVars().setEnabled(b);
		fill();
	}


	/** Complete the dialog initialization. */
	public void completeInit() {
		getJContentPane().getRootPane().setDefaultButton(getButtonOk());
		pack();
		setVisible(true);
	}


	/** Complete the dialog finalization. */
	public void completeExit() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		STGraph.getSTC().signalCurrentGraphAsModified();
		setVisible(false);
		graph.setSelectionCell(node);
	}


	/** Fill the dialog fields. */
	abstract void fill();


	/** Initialize jContentPane.
	 * @return panel */
	abstract JPanel getJContentPane();


	/** Initialize buttonOk.
	 * @return button */
	protected JButton getButtonOk() {
		if(buttonOk == null) {
			buttonOk = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			buttonOk.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) { okHandler(); }
			});
		}
		return buttonOk;
	}


	/** Action for the OK button. */
	abstract void okHandler();


	/** Initialize buttonCancel.
	 * @return button */    
	protected JButton getButtonCancel() {
		if(buttonCancel == null) {
			buttonCancel = new JButton(STGraphC.getMessage("DIALOG.CANCEL"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-cancel.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			buttonCancel.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) { setVisible(false); }
			});
		}
		return buttonCancel;
	}


	/** Construct and return buttonPanel.
	 * @return panel */
	protected JPanel getButtonPanel() {
		if(buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getButtonOk(), null);
			buttonPanel.add(getButtonCancel(), null);
		}
		return buttonPanel;
	}


	/** Initialize labelInputVar.
	 * @return label */
	JLabel getLabelInputVar() { return new JLabel(STGraphC.getMessage("WIDGET.DIALOG.INPUTVARIABLE") + STTools.COLON); } //$NON-NLS-1$


	/** Initialize labelInputVarConstraints.
	 * @return constraints */
	GridConstraints getLabelInputVarConstraints() {
		GridConstraints gbc = new GridConstraints(0, 0);
		gbc.anchor = GridBagConstraints.NORTHEAST;
		return gbc;
	}


	/** Initialize textInputVar.
	 * @return text */    
	STTextField getTextInputVar() {
		if(textInputVar == null) { textInputVar = new STTextField(this, false); }
		return textInputVar;
	}


	/** Initialize textInputVarConstraints.
	 * @return constraints */
	GridConstraints getTextInputVarConstraints() {
		GridConstraints gbc = new GridConstraints(1, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		return gbc;
	}


	/** Initialize labelAvailableVars.
	 * @return label */    
	JLabel getLabelAvailableVars() { return new JLabel(STGraphC.getMessage("WIDGET.DIALOG.AVAILABLEVARIABLES") + STTools.COLON); } //$NON-NLS-1$


	/** Initialize labelAvailableVarConstraints.
	 * @return constraints */
	GridConstraints getLabelAvailableVarsConstraints() {
		GridConstraints gbc = new GridConstraints(1, 1);
		gbc.anchor = GridBagConstraints.WEST;
		return gbc;
	}


	/** Initialize buttonSetInputVar.
	 * @return button */    
	JButton getButtonSetInputVar() {
		if(buttonSetInputVar == null) {
			buttonSetInputVar = new JButton(STGraphC.getMessage("WIDGET.DIALOG.SETASINPUT") + STTools.COLON); //$NON-NLS-1$
			buttonSetInputVar.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					if(listAvailableVars.getSelectedIndex() == -1) { return; }
					Object obj = listAvailableVars.getSelectedValue();
					getTextInputVar().setText((obj instanceof String) ? (String)obj : ((STNode)obj).getName());
				}
			});
		}
		return buttonSetInputVar;
	}


	/** Initialize buttonSetInputVarConstraints.
	 * @return constraints */
	GridConstraints getButtonSetInputVarConstraints() {
		GridConstraints gbc = new GridConstraints(0, 2);
		gbc.anchor = GridBagConstraints.NORTHEAST;
		return gbc;
	}


	/** Initialize scrollAvailableVars.
	 * @return pane */
	JScrollPane getScrollAvailableVars() {
		if(scrollAvailableVars == null) {
			scrollAvailableVars = new JScrollPane();
			scrollAvailableVars.setViewportView(getListAvailableVars());
		}
		return scrollAvailableVars;
	}


	/** Initialize listAvailableVars.
	 * @return list */
	@SuppressWarnings("rawtypes")
	JList getListAvailableVars() {
		if(listAvailableVars == null) {
			listAvailableVars = new JList();
			listAvailableVars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listAvailableVars.addMouseListener(new MouseAdapter() { 
				public void mouseReleased(final MouseEvent e) {
					if(e.getClickCount() == 2) { buttonSetInputVar.doClick(); }
				}
			});

		}
		return listAvailableVars;
	}


	/** Initialize scrollAvailableVarConstraints.
	 * @return constraints */
	GridConstraints getScrollAvailableVarsConstraints() {
		GridConstraints gbc = new GridConstraints(1, 2);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0; gbc.weighty = 1.0;
		return gbc;
	}

}
