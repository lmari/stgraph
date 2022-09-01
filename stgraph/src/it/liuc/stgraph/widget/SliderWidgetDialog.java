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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


/** NodeDialog handler for the Slider widget. */
@SuppressWarnings("serial")
public class SliderWidgetDialog extends WidgetDialog {
	/** The text max. */
	private STTextField textMax = null;
	/** The text min. */
	private STTextField textMin = null;
	/** The text incr. */
	private STTextField textIncr = null;
	/** The list conf styles. */
	@SuppressWarnings("rawtypes")
	private JComboBox listOrient = null;


	/** Open the dialog, that was been created in a static way.
	 * @param node the node */
	public void open(final STWidget node) {
		this.node = node;
		startInit();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		getTextMin().setEnabled(b); getTextMax().setEnabled(b); getTextIncr().setEnabled(b);
		getListOrient().setEnabled(b);
		completeInit();
	}


	/** Fill the dialog fields. */
	@SuppressWarnings("unchecked")
	protected void fill() {
		String p = (String)node.getProperty(InputWidget.PROP_SOURCE_NA); getTextInputVar().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		p = (String)node.getProperty(SliderWidget.PROP_MIN); getTextMin().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		p = (String)node.getProperty(SliderWidget.PROP_MAX); getTextMax().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		p = (String)node.getProperty(SliderWidget.PROP_DELTA); getTextIncr().setText(STTools.isEmpty(p) ? STTools.BLANK : p);
		p = (String)node.getProperty(SliderWidget.PROP_ORIENT); getListOrient().setSelectedIndex(STTools.isEmpty(p) || p.equals("0") ? 0 : 1); //$NON-NLS-1$
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
			jContentPane.add(new JLabel(STGraphC.getMessage(SliderWidget.PREFIX + ".MINVALUE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 3, GridBagConstraints.EAST));
			//
			GridConstraints gbcMin2 = new GridConstraints(1, 3);
			gbcMin2.weightx = 1.0;
			gbcMin2.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getTextMin(), gbcMin2);
			// max
			jContentPane.add(new JLabel(STGraphC.getMessage(SliderWidget.PREFIX + ".MAXVALUE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 4, GridBagConstraints.EAST));
			//
			GridConstraints gbcMax2 = new GridConstraints(1, 4);
			gbcMax2.weightx = 1.0;
			gbcMax2.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getTextMax(), gbcMax2);
			// increment
			jContentPane.add(new JLabel(STGraphC.getMessage(SliderWidget.PREFIX + ".INCREMENT") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 5, GridBagConstraints.EAST));
			//
			GridConstraints gbcIncr2 = new GridConstraints(1, 5);
			gbcIncr2.fill = GridBagConstraints.HORIZONTAL;
			gbcIncr2.weightx = 1.0;
			jContentPane.add(getTextIncr(), gbcIncr2);
			// orientation
			jContentPane.add(new JLabel(STGraphC.getMessage(SliderWidget.PREFIX + ".ORIENTATION") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 6, GridBagConstraints.EAST));
			//
			GridConstraints gbcOrient2 = new GridConstraints(1, 6);
			gbcOrient2.fill = GridBagConstraints.HORIZONTAL;
			gbcOrient2.weightx = 1.0;
			jContentPane.add(getListOrient(), gbcOrient2);
			// ok-cancel
			GridConstraints gbcButt = new GridConstraints(0, 7);
			gbcButt.fill = GridBagConstraints.HORIZONTAL;
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


	/** Get the listOrient.
	 * @return combobox */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox getListOrient() {
		if(listOrient == null) {
			String[] styles = { STGraphC.getMessage(SliderWidget.PREFIX + ".ORIENT.HORIZ"), STGraphC.getMessage(SliderWidget.PREFIX + ".ORIENT.VERT") }; //$NON-NLS-1$ //$NON-NLS-2$
			listOrient = new JComboBox(styles);
		}
		return listOrient;
	}


	/** Action for the OK button. */
	protected void okHandler() {
		String name = getTextInputVar().getText();
		String min = getTextMin().getText();
		String max = getTextMax().getText();
		String delta = getTextIncr().getText();
		String orient = getListOrient().getSelectedIndex() == 0 ? "0" : "1"; //$NON-NLS-1$ //$NON-NLS-2$
		if(!((SliderWidget)node).setProps(name, min, max, delta, orient)) { return; }
		completeExit();
	}

}
