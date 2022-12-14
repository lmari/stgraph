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
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.KnobPanel;
import it.liuc.stgraph.util.STTools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.graph.AttributeMap;
import org.nfunk.jep.type.Tensor;


/** Knob widget. */
@SuppressWarnings("serial")
public class KnobWidget extends STWidget implements InputWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "KNOB";
	/** The Constant ID. */
	public static final String ID = "KnobWidget";
	/** The default min. */
	private static String defaultMin = "0.0";
	/** The default delta. */
	private static String defaultDelta = "1.0";
	/** The default max. */
	private static String defaultMax = "10.0";
	/** The default value. */
	private static String defaultValue = defaultMin;
	/** The Constant PROP_MIN. */
	public static final String PROP_MIN = "min"; // string defined interactively (min value)
	/** The Constant PROP_DELTA. */
	public static final String PROP_DELTA = "delta"; // string defined interactively (delta value)
	/** The Constant PROP_MAX. */
	public static final String PROP_MAX = "max"; // string defined interactively (max value)
	/** The knob. */
	private final KnobPanel knob = new KnobPanel();
	/** The text field. */
	private final JTextField textField = new JTextField(5);


	/** Class constructor. */
	public KnobWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public KnobWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new KnobWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new KnobWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("KNOB.LABEL"); }


	/** Get the knob.
	 * @return knob */
	public final KnobPanel getKnob() { return knob; }


	/** Get the current value of this widget.
	 * @return value */
	@Override public final Object getValue() { return knob.getDecValue(); }


	/** Get the next value of this widget.
	 * @param isFirst the is first
	 * @return value */
	@Override public final Object getNextValue(final boolean isFirst) { return getValue(); }


	/** Set the value for this widget.
	 * @param value the value */
	public final void setValue(final Tensor value) { knob.setDecValue(value); }


	/** Set whether it is possible to interact with this widget.
	 * @param state the state */
	@Override public final void setInteractable(final boolean state) {
		knob.setEnabled(state);
		textField.setEnabled(state);
	}


	/** Set panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.LIGHT_GRAY);

		String val = (String)getProperty(InputWidget.PROP_VALUE);
		String delta = (String)getProperty(KnobWidget.PROP_DELTA);
		String min = (String)getProperty(KnobWidget.PROP_MIN);
		if(val != null && min != null && delta != null) {
			knob.setValue((int)((Double.parseDouble(val) - Double.parseDouble(min)) / Double.parseDouble(delta)));
		}

		Dimension d = new Dimension(20, 25);
		textField.setPreferredSize(d); textField.setMinimumSize(d); textField.setMaximumSize(d);
		textField.setHorizontalAlignment(SwingConstants.RIGHT);

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override
			public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		};
		knob.addMouseListener(ma); frame.addMouseListener(ma);

		KeyAdapter ka = new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if(!knob.isEnabled()) { return; }
				int i = e.getKeyCode(); 
				if(i == KeyEvent.VK_LEFT || i == KeyEvent.VK_UP) {
					knob.setDecValue(Tensor.newScalar(knob.getDecValue().getValue() - knob.getDelta()));
					return;
				}
				if(i == KeyEvent.VK_RIGHT || i == KeyEvent.VK_DOWN ) {
					knob.setDecValue(Tensor.newScalar(knob.getDecValue().getValue() + knob.getDelta()));
					return;
				}
			};
		};
		panel.addKeyListener(ka);

		knob.addChangeListener(new ChangeListener() { 
			public void stateChanged(final ChangeEvent e) {    
				String v = knob.getDecValue().toString();
				textField.setText(v);
				setProperty(InputWidget.PROP_VALUE, v);
				setTitle();
				if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
					try {
						((STGraphExec)graph).computeInteractively();
						((STGraphExec)graph).endExec(true);
					} catch (Exception ex) { }
				} else {
					((STGraphExec)graph).computeInteractivelyOnTheFly();
				}
				panel.requestFocus();
				graph.refreshGraph();
			}
		});

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				double v0;
				try {
					v0 = Double.parseDouble(textField.getText());
				} catch (Exception ex) {
					v0 = knob.getDecValue().getValue();
					textField.setText(knob.getDecValue().toString());
				}
				double vmin = Double.parseDouble((String)getProperty(KnobWidget.PROP_MIN));
				if(v0 < vmin) { v0 = vmin; textField.setText(STTools.BLANK + v0); }
				double vmax = Double.parseDouble((String)getProperty(KnobWidget.PROP_MAX));
				if(v0 > vmax) { v0 = vmax; textField.setText(STTools.BLANK + v0); }
				Tensor v = Tensor.newScalar(v0);
				knob.setDecValue(v);
				setProperty(InputWidget.PROP_VALUE, v);
				setTitle();
				graph.refreshGraph();
			}
		});

		setProperty(KnobWidget.PROP_MIN, defaultMin);
		setProperty(KnobWidget.PROP_DELTA, defaultDelta);
		setProperty(KnobWidget.PROP_MAX, defaultMax);
		setProperty(InputWidget.PROP_VALUE, defaultValue);

		setProps(null, defaultMin, defaultMax, defaultDelta);

		knob.setDecValue(Tensor.newScalar(Double.parseDouble(defaultValue)));

		panel.add(textField, BorderLayout.NORTH);
		panel.add(knob, BorderLayout.CENTER);
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() {
		String w = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(w == null) {
			setProperty(STWidget.PROP_TITLE, STGraphC.getMessage("WIDGET.UNBOUND"));
			setTitle((String)getProperty(STWidget.PROP_TITLE));
		} else {
			setProperty(STWidget.PROP_TITLE, w);
			setTitle((String)getProperty(STWidget.PROP_TITLE) + STTools.COLON + STTools.SPACE + getProperty(InputWidget.PROP_VALUE));
		}
	}


	/** Set the properties of this widget.
	 * @param min the min
	 * @param delta the delta
	 * @param max the max
	 * @param name the name
	 * @return result */
	final boolean setProps(final String name, final String min, final String max, final String delta) {
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
			if(!STTools.isEmpty(min)) {
				setProperty(KnobWidget.PROP_MIN, min);
				knob.setMin(Double.parseDouble(min));
			}
			if(!STTools.isEmpty(max)) {
				setProperty(KnobWidget.PROP_MAX, max);
				knob.setMax(Double.parseDouble(max));
			}
			if(!STTools.isEmpty(delta)) {
				setProperty(KnobWidget.PROP_DELTA, delta);
				knob.setDelta(Double.parseDouble(delta));
			}
			setTitle();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_KNOB_FORMAT")); return false;
		}
		return true;
	}


	/** Check whether the renamed node is referenced by an input widget, and in that case set the new name of the source node accordingly.
	 * @param newName the new name
	 * @param oldName the old name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		String expr  = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(!STTools.isEmpty(expr) && expr.equals(oldName)) { setProperty(InputWidget.PROP_SOURCE_NA, newName); }
		setTitle();
	}


	/** Handle the event of this widget removal. */
	public void handleWidgetRemoval() {
		String name = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(name == null) { return; }
		STNode node = (STNode)getProperty(InputWidget.PROP_SOURCE_OB);
		if(node == null) { return; }
		((ValueNode)node).unbindFromWidget();
	}


	/** Check whether the removed node is referenced by an input widget, and in that case set its source node to <code>null</code>.
	 * @param name the name */
	public final void handleNodeRemoval(final String name) {
		String sname = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(sname != null && sname.equals(name)) {
			setProperty(InputWidget.PROP_SOURCE_NA, null);
			setProperty(InputWidget.PROP_SOURCE_OB, null);
			setProperty(STWidget.PROP_TITLE, null);
			refresh();
		}
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	public final void prepareForSave() {
		String s;
		super.prepareForSave();
		AttributeMap am = getAttributes();
		if((s = (String)am.get(InputWidget.PROP_SOURCE_NA)) != null) {
			dataToSave.put(InputWidget.PROP_SOURCE_NA, s);
			dataToSave.put(InputWidget.PROP_VALUE, getValue());
		}
		if((s = (String)am.get(KnobWidget.PROP_MIN)) != null) { dataToSave.put(KnobWidget.PROP_MIN, s); }
		if((s = (String)am.get(KnobWidget.PROP_MAX)) != null) { dataToSave.put(KnobWidget.PROP_MAX, s); }
		if((s = (String)am.get(KnobWidget.PROP_DELTA)) != null) { dataToSave.put(KnobWidget.PROP_DELTA, s); }
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String name = (String)dataToSave.get(InputWidget.PROP_SOURCE_NA);
		String min = (String)dataToSave.get(KnobWidget.PROP_MIN);
		String max = (String)dataToSave.get(KnobWidget.PROP_MAX);
		String delta = (String)dataToSave.get(KnobWidget.PROP_DELTA);
		setProps(name, min, max, delta);
		String v = (String)dataToSave.get(InputWidget.PROP_VALUE);
		if(!STTools.isEmpty(v)) { setValue(Tensor.newScalar(Double.parseDouble(v))); }
	}

}
