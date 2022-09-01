/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2022, Luca Mari.
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
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.STTools;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import org.jgraph.graph.AttributeMap;
import org.nfunk.jep.type.Tensor;


/** InputText widget. */
@SuppressWarnings("serial")
public class InputTextWidget extends STWidget implements InputWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "INPUTTEXT";
	/** The Constant ID. */
	public static final String ID = "InputTextWidget";
	/** The choice array. */
	private JRadioButton[] choice = null;
	/** The choices. */
	private ButtonGroup choices = null;
	/** The direct map of choices. */
	private HashMap<Integer, String> dmap = null;
	/** The inverse map of choices. */
	private HashMap<String, Integer> imap = null;
	/** The node. */
	private STNode node = null;


	/** Class constructor. */
	public InputTextWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public InputTextWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new InputTextWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new InputTextWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("INPUTTEXT.LABEL"); }


	/** Get the value currently set by this widget.
	 * @return value */
	@Override public final Object getValue() {
		Object defResult = Tensor.newScalar(0);
		if(choices.getButtonCount() == 0) { return defResult; }
		for(int i = 0; i < choices.getButtonCount(); i++) {
			if(choice[i].isSelected()) { return Tensor.newScalar(imap.get(choice[i].getText()).intValue()); }
		}
		return defResult;
	}


	/** Get the next value of this widget.
	 * @param isFirst the is first
	 * @return value */
	@Override public final Object getNextValue(final boolean isFirst) { return getValue(); }


	/** Set the current value of this widget.
	 * @param value the value */
	public final void setValue(final Tensor value) {
		if(dmap == null) { return; }
		String s = dmap.get(Integer.valueOf((int)value.getValue()));
		for(int i = 0; i < choices.getButtonCount(); i++) {
			if(choice[i].getText().equals(s)) {
				choice[i].setSelected(true);
			} else {
				choice[i].setSelected(false);
			}
		}
	}


	/** Set whether it is possible to interact with this widget.
	 * @param state the state */
	@Override public final void setInteractable(final boolean state) {
		if(choice != null) {
			for(JRadioButton c : choice) { c.setEnabled(state); }
		}
	}


	/** Set the radio group. */
	private void setRadioGroup() {
		choices = new ButtonGroup();
		panel.removeAll();
		if((node = (STNode)getProperty(InputWidget.PROP_SOURCE_OB)) != null) {
			dmap = node.getInputTextCProperty();
			imap = new LinkedHashMap<String, Integer>();
			choice = new JRadioButton[dmap.keySet().size()];
			int j = 0;
			for(Integer i : dmap.keySet()) {
				imap.put(dmap.get(i), i);
				choice[j] = new JRadioButton(dmap.get(i));
				choice[j].addMouseListener(new MouseAdapter() {
					@Override public void mousePressed(final MouseEvent e) { 
						if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); }
					}
					@Override public void mouseReleased(final MouseEvent e) {
						if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
						if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
							try {
								((STGraphExec)graph).computeInteractively();
								((STGraphExec)graph).endExec(true);
							} catch (Exception ex) { }
						} else {
							((STGraphExec)graph).computeInteractivelyOnTheFly();
							graph.refreshGraph();
						}
					}
				});
				choices.add(choice[j]);
				panel.add(choice[j]);
				j++;
			}
			choice[0].setSelected(true);
		}
	}


	/** Set the panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { 
				if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); }
			}
			@Override public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		});
		setRadioGroup();
		setTitle();
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() {
		String w = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(w == null) {
			setProperty(STWidget.PROP_TITLE, STGraphC.getMessage("WIDGET.UNBOUND"));
		} else {
			setProperty(STWidget.PROP_TITLE, w);
		}
		setTitle((String)getProperty(STWidget.PROP_TITLE));
	}


	/** Resize the contents of the frame of this widget. */
	public void setSize() { ; } // directly implemented by the layout manager


	/** Set the properties of this widget.
	 * @param name the name
	 * @return result */
	final boolean setProps(final String name) {
		try {
			if(STTools.isEmpty(name) && (STNode)getProperty(InputWidget.PROP_SOURCE_OB) != null) {
				((ValueNode)getProperty(InputWidget.PROP_SOURCE_OB)).unbindFromWidget();
				setProperty(InputWidget.PROP_SOURCE_OB, null);
				setProperty(InputWidget.PROP_SOURCE_NA, null);
				setTitle();
				return true;
			}
			if(!STTools.isEmpty(name)) {
				boolean found = false;
				for(STNode node : STGraph.getSTC().getCurrentGraph().getNodes()) {
					if(node.getName().equals(name)) {
						if(!node.isInput()) { throw new Exception(); } // it should not happen...
						STNode oldnode = (STNode)getProperty(InputWidget.PROP_SOURCE_OB);
						if(oldnode != null) { ((ValueNode)oldnode).unbindFromWidget(); }
						((ValueNode)node).bindToWidget(this);
						this.node = (ValueNode)node;
						found = true;
						break;
					}
				}
				if(!found) { throw new Exception(); } // it should not happen...
			}
			setTitle();
			setRadioGroup();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_INPUTTEXT_FORMAT")); return false;
		}
		return true;
	}


	/** Check whether the renamed node is referenced by the toggle button, and in that case set the new name accordingly.
	 * @param oldName the old name
	 * @param newName the new name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		String expr  = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(!STTools.isEmpty(expr) && expr.equals(oldName)) { setProperty(InputWidget.PROP_SOURCE_NA, newName); }
		setTitle();
	}


	/** Handle the event of this widget removal. */
	public void handleWidgetRemoval() { 
		String name = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(name == null) { return; }
		STNode node = getGraph().getNodeByName(name);
		if(node == null) { return; }
		((ValueNode)node).unbindFromWidget();
	}


	/** Check whether the removed node is referenced by the toggle button, and in that case set the name to <code>null</code>.
	 * @param name the name */
	public final void handleNodeRemoval(final String name) {
		String sname = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(sname != null && sname.equals(name)) {
			setProperty(InputWidget.PROP_SOURCE_NA, null);
			setProperty(InputWidget.PROP_SOURCE_OB, null);
		}
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	public final void prepareForSave() {
		String s;
		super.prepareForSave();
		AttributeMap am = getAttributes();
		if((s = (String)am.get(InputWidget.PROP_SOURCE_NA)) != null) { dataToSave.put(InputWidget.PROP_SOURCE_NA, s); }
		dataToSave.put(InputWidget.PROP_VALUE, getValue());
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String name = (String)dataToSave.get(InputWidget.PROP_SOURCE_NA);
		setProps(name);
		setValue(Tensor.newScalar(Double.parseDouble((String)dataToSave.get(InputWidget.PROP_VALUE))));
	}

}
