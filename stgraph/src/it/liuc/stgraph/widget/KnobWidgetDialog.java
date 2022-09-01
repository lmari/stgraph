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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;


/** NodeDialog handler for the Knob widget. */
@SuppressWarnings("serial")
public class KnobWidgetDialog extends WidgetDialog {
	/** The text max. */
	private STTextField textMax = null;
	/** The text min. */
	private STTextField textMin = null;
	/** The text incr. */
	private STTextField textIncr = null;


	/** Open the dialog, that was been created in a static way.
	 * @param node the node */
	public void open(final STWidget node) {
		this.node = node;
		startInit();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		getTextMin().setEnabled(b); getTextMax().setEnabled(b); getTextIncr().setEnabled(b);
		completeInit();
	}


	/** Fill the dialog fields. */
	@SuppressWarnings("unchecked")
	protected void fill() {
		String p = (String)node.getProperty(InputWidget.PROP_SOURCE_NA); getTextInputVar().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		p = (String)node.getProperty(KnobWidget.PROP_MIN); getTextMin().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		p = (String)node.getProperty(KnobWidget.PROP_MAX); getTextMax().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		p = (String)node.getProperty(KnobWidget.PROP_DELTA); getTextIncr().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
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
			// min
			jContentPane.add(new JLabel(STGraphC.getMessage(KnobWidget.PREFIX + ".MINVALUE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 3, GridBagConstraints.EAST));
			//
			GridConstraints gbcMin2 = new GridConstraints(1, 3);
			gbcMin2.weightx = 1.0;
			gbcMin2.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getTextMin(), gbcMin2);
			// max
			jContentPane.add(new JLabel(STGraphC.getMessage(KnobWidget.PREFIX + ".MAXVALUE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 4, GridBagConstraints.EAST));
			//
			GridConstraints gbcMax2 = new GridConstraints(1, 4);
			gbcMax2.weightx = 1.0;
			gbcMax2.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getTextMax(), gbcMax2);
			// increment
			jContentPane.add(new JLabel(STGraphC.getMessage(KnobWidget.PREFIX + ".INCREMENT") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 5, GridBagConstraints.EAST));
			//
			GridConstraints gbcIncr2 = new GridConstraints(1, 5);
			gbcIncr2.fill = GridBagConstraints.HORIZONTAL;
			gbcIncr2.weightx = 1.0;
			jContentPane.add(getTextIncr(), gbcIncr2);
			// ok-cancel
			GridConstraints gbcButt = new GridConstraints(0, 6);
			gbcButt.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbcButt.gridwidth = 2;
			jContentPane.add(getButtonPanel(), gbcButt);
		}
		return jContentPane;
	}


	/** Initialize textIncr.
	 * @return text */
	STTextField getTextIncr() {
		if(textIncr == null) { textIncr = new STTextField(this, false); }
		return textIncr;
	}


	/** Initialize textMax.
	 * @return text */
	STTextField getTextMax() {
		if(textMax == null) { textMax = new STTextField(this, false); }
		return textMax;
	}


	/** Initialize textMin.
	 * @return text */
	STTextField getTextMin() {
		if(textMin == null) { textMin = new STTextField(this, false); }
		return textMin;
	}


	/** Action for the OK button. */
	protected void okHandler() {
		String name = getTextInputVar().getText();
		String min = getTextMin().getText();
		String max = getTextMax().getText();
		String delta = getTextIncr().getText();
		if(!((KnobWidget)node).setProps(name, min, max, delta)) { return; }
		completeExit();
	}

}
