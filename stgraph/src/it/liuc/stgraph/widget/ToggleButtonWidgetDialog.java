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
import it.liuc.stgraph.util.STTools;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


/** NodeDialog handler for the ToggleButton widget. */
@SuppressWarnings("serial")
public class ToggleButtonWidgetDialog extends WidgetDialog {
	/** The conf action panel. */
	private JPanel confActionPanel = null;
	/** The action1. */
	private JRadioButton action1 = null;
	/** The action2. */
	private JRadioButton action2 = null;
	/** The actions. */
	private ButtonGroup actions = null;


	/** Open the dialog, that was been created in a static way.
	 * @param node the node */
	public void open(final STWidget node) {
		this.node = node;
		startInit();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		getConfActionPanel().setEnabled(b);
		completeInit();
	}


	/** Fill the dialog fields. */
	@SuppressWarnings("unchecked")
	protected void fill() {
		String p = (String)node.getProperty(InputWidget.PROP_SOURCE_NA); getTextInputVar().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		Integer i = (Integer)node.getProperty(ToggleButtonWidget.PROP_ACTION);
		int action = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
		switch(action) {
		case 1: action1.setSelected(true); break;
		case 2: action2.setSelected(true); break;
		default:
		}
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
			// actions
			jContentPane.add(getConfActionPanel(), new GridConstraints(1, 3));
			// ok-cancel
			GridConstraints gbcButt = new GridConstraints(0, 4);
			gbcButt.fill = GridBagConstraints.HORIZONTAL;
			gbcButt.gridwidth = 2;
			jContentPane.add(getButtonPanel(), gbcButt);
		}
		return jContentPane;
	}


	/** Gets the conf action panel.
	 * @return the conf action panel */
	private JPanel getConfActionPanel() {
		if(confActionPanel == null) {
			confActionPanel = new JPanel();
			confActionPanel.setLayout(new GridBagLayout());
			confActionPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

			action1 = new JRadioButton(STGraphC.getMessage(ToggleButtonWidget.PREFIX + ".ACTION1")); //$NON-NLS-1$
			action2 = new JRadioButton(STGraphC.getMessage(ToggleButtonWidget.PREFIX + ".ACTION2")); //$NON-NLS-1$
			actions = new ButtonGroup();
			actions.add(action1);
			actions.add(action2);

			GridConstraints gbcAction1 = new GridConstraints(0, 0);
			gbcAction1.anchor = GridBagConstraints.WEST;
			confActionPanel.add(action1, gbcAction1);

			GridConstraints gbcAction2 = new GridConstraints(0, 1);
			gbcAction2.anchor = GridBagConstraints.WEST;
			confActionPanel.add(action2, gbcAction2);
		}
		return confActionPanel;
	}


	/** Action for the OK button. */
	protected void okHandler() {
		String name = getTextInputVar().getText();
		int action = 1;
		if(action2.isSelected()) { action = 2; }
		if(!((ToggleButtonWidget)node).setProps(name, action)) { return; }
		completeExit();
	}

}
