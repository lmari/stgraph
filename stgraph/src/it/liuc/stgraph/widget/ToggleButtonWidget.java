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

import it.liuc.stgraph.STConfigurator;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.fun.STParserTools;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.STTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.graph.AttributeMap;
import org.nfunk.jep.type.Tensor;


/** ToggleButton widget. */
@SuppressWarnings("serial")
public class ToggleButtonWidget extends STWidget implements InputWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "TOGGLEBUTTON";
	/** The Constant ID. */
	public static final String ID = "ToggleButtonWidget";
	/** The icon off. */
	private static ImageIcon iconOff;
	/** The icon on. */
	private static ImageIcon iconOn;
	/** The Constant PROP_ACTION. */
	public static final String PROP_ACTION = "action"; // int: action 	
	/** The button. */
	private final JToggleButton button = new JToggleButton();

	static {
		Properties props = STConfigurator.getProperties();
		iconOff = new ImageIcon(STGraph.class.getClassLoader().getResource("icons/base/" + props.getProperty(ToggleButtonWidget.PREFIX + ".ICONOFF"))); //$NON-NLS-2$
		iconOn = new ImageIcon(STGraph.class.getClassLoader().getResource("icons/base/" + props.getProperty(ToggleButtonWidget.PREFIX + ".ICONON"))); //$NON-NLS-2$
	}


	/** Class constructor. */
	public ToggleButtonWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public ToggleButtonWidget(final Object _userObject) { super(_userObject); }


	/** Return the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new ToggleButtonWidgetView(this); }
		return view;
	}


	/** Return the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new ToggleButtonWidgetDialog(); }
		return dialog;
	}


	/** Return the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("TOGGLEBUTTON.LABEL"); }


	/** Return the value currently set by this widget.
	 * @return value */
	@Override public final Object getValue() { return button.isSelected() ? STParserTools.TRUE : STParserTools.FALSE; }


	/** Return the next value of this widget.
	 * @param isFirst the is first
	 * @return value */
	@Override public final Object getNextValue(final boolean isFirst) { return getValue(); }


	/** Set the current value of this widget.
	 * @param value the value */
	public final void setValue(final Tensor value) { button.setSelected(value.equals(STParserTools.TRUE)); }


	/** Set whether it is possible to interact with this widget.
	 * @param state the state */
	@Override public final void setInteractable(final boolean state) {
		button.setEnabled(state);
	}

	
	/** Set panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BorderLayout());

		button.setIcon(iconOff);
		button.setSelectedIcon(iconOn);
		////button.setText(STGraphC.getMessage("SYSTEM.ISFALSE"));
		button.setHorizontalAlignment(SwingConstants.CENTER);
		////button.setHorizontalTextPosition(SwingConstants.CENTER);
		////button.setVerticalTextPosition(SwingConstants.BOTTOM);
		button.setRolloverEnabled(false);

		panel.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				Dimension d = panel.getSize();
				int m = Math.min(d.width, d.height) - 10;
				//int w = d.width - 10;
				//int h = d.height - 10;
				iconOn = new ImageIcon(iconOn.getImage().getScaledInstance(m, m, Image.SCALE_SMOOTH));  
				iconOff = new ImageIcon(iconOff.getImage().getScaledInstance(m, m, Image.SCALE_SMOOTH));  
				button.setIcon(iconOff);
				button.setSelectedIcon(iconOn);
			}

			public void componentShown(ComponentEvent e) { ; }
			public void componentMoved(ComponentEvent e) { ; }
			public void componentHidden(ComponentEvent e) { ; }
		});

		button.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { 
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(ToggleButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Boolean v = (Boolean)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { button.setSelected(v.booleanValue()); }
					} else if(theAction == 2) {
						button.setSelected(true);
					}
				} else if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); }
			}

			@Override public void mouseReleased(final MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(ToggleButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Boolean v = (Boolean)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { button.setSelected(v.booleanValue()); }
					} else if(theAction == 2) { button.setSelected(false); }
					if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
						try {
							((STGraphExec)graph).computeInteractively();
							((STGraphExec)graph).endExec(true);
						} catch (Exception ex) { }
					} else {
						((STGraphExec)graph).computeInteractivelyOnTheFly();
					}
				} else if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus();
				graph.refreshGraph();
			}

		});

		button.addChangeListener(new ChangeListener() { 
			public void stateChanged(final ChangeEvent e) {
				Boolean vv = Boolean.valueOf(button.isSelected());
				setProperty(InputWidget.PROP_VALUE, vv);
				////if(vv.booleanValue()) { button.setText(STGraphC.getMessage("SYSTEM.ISTRUE")); }
				////else { button.setText(STGraphC.getMessage("SYSTEM.ISFALSE")); }
				setTitle();
				graph.refreshGraph();
			}
		});

		panel.add(button, BorderLayout.CENTER);
		setTitle();
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() {
		String w = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(w == null) {
			setProperty(STWidget.PROP_TITLE, STGraphC.getMessage("WIDGET.UNBOUND"));
			setTitle((String)getProperty(STWidget.PROP_TITLE));
		} else {
			setProperty(STWidget.PROP_TITLE, w);
			setTitle((String)getProperty(STWidget.PROP_TITLE) + " - " + button.isSelected());
		}
	}


	/** Resize the contents of the frame of this widget. */
	public void setSize() { ; } // directly implemented by the layout manager


	/** Set the properties of this widget.
	 * @param name the name
	 * @param action the action
	 * @return result */
	@SuppressWarnings("removal")
	final boolean setProps(final String name, final int action) {
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
			setProperty(ToggleButtonWidget.PROP_ACTION, new Integer(action));
			if(action == 2) { button.setSelected(false); }
			else if(action == 3) { button.setSelected(true); }
			setTitle();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_TOGGLEBUTTON_FORMAT")); return false;
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
		Integer i;
		super.prepareForSave();
		AttributeMap am = getAttributes();
		if((s = (String)am.get(InputWidget.PROP_SOURCE_NA)) != null) { dataToSave.put(InputWidget.PROP_SOURCE_NA, s); }
		dataToSave.put(InputWidget.PROP_VALUE, getValue());
		if((i = ((Integer)am.get(ToggleButtonWidget.PROP_ACTION))) != null) { dataToSave.put(ToggleButtonWidget.PROP_ACTION, i.toString()); }
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String name = (String)dataToSave.get(InputWidget.PROP_SOURCE_NA);
		String i = (String)dataToSave.get(ToggleButtonWidget.PROP_ACTION);
		int action = (i != null) ? Integer.parseInt(i) : 1; // with a default value for backward compatibility...
		setProps(name, action);
		setValue(Tensor.newScalar(Double.parseDouble((String)dataToSave.get(InputWidget.PROP_VALUE))));
	}

}
