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
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STTools;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;


/** NodeDialog handler for the InputText widget. */
@SuppressWarnings("serial")
public class InputTextWidgetDialog extends WidgetDialog {

	/** Open the dialog, that was been created in a static way.
	 * @param node the node */
	public void open(final STWidget node) {
		this.node = node;
		startInit();
		completeInit();
	}


	/** Fill the dialog fields. */
	@SuppressWarnings("unchecked")
	protected void fill() {
		String p = (String)node.getProperty(InputWidget.PROP_SOURCE_NA); getTextInputVar().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
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
			// ok-cancel
			GridConstraints gbcButt = new GridConstraints(0, 4);
			gbcButt.fill = GridBagConstraints.HORIZONTAL;
			gbcButt.gridwidth = 2;
			jContentPane.add(getButtonPanel(), gbcButt);
		}
		return jContentPane;
	}


	/** Action for the OK button. */
	protected void okHandler() {
		String name = getTextInputVar().getText();
		if(!((InputTextWidget)node).setProps(name)) { return; }
		completeExit();
	}

}
