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
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.STTools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.graph.AttributeMap;
import org.nfunk.jep.type.Tensor;


/** Slider widget. */
@SuppressWarnings("serial")
public class SliderWidget extends STWidget implements InputWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "SLIDER";
	/** The Constant ID. */
	public static final String ID = "SliderWidget";
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
	/** The Constant PROP_ORIENT. */
	public static final String PROP_ORIENT = "0"; // string defined interactively (orientation: 0=horizontal; 1=vertical)
	/** The slider. */
	private final DecSlider slider = new DecSlider();
	/** The text field. */
	private final JTextField textField = new JTextField(5);


	/** Class constructor. */
	public SliderWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public SliderWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new SliderWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new SliderWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("SLIDER.LABEL"); }


	/** Get the slider.
	 * @return slider */
	public final DecSlider getSlider() { return slider; }


	/** Get the current value of this widget.
	 * @return value */
	@Override public final Object getValue() { return slider.getDecValue(); }


	/** get the next value of this widget.
	 * @param isFirst the is first
	 * @return value */
	@Override public final Object getNextValue(final boolean isFirst) { return getValue(); }


	/** Set the value for this widget.
	 * @param value the value */
	public final void setValue(final Tensor value) { slider.setDecValue(value); }


	/** Set whether it is possible to interact with this widget.
	 * @param state the state */
	@Override public final void setInteractable(final boolean state) {
		slider.setEnabled(state);
		textField.setEnabled(state);
	}


	/** Set panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		String orient = (String)getProperty(SliderWidget.PROP_ORIENT);
		if(orient == null || orient.equals("0")) {
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			slider.setOrientation(SwingConstants.HORIZONTAL);
			setProperty(SliderWidget.PROP_ORIENT, "0");
		} else {
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			slider.setOrientation(SwingConstants.VERTICAL);
			setProperty(SliderWidget.PROP_ORIENT, "1");
		}
		panel.setBackground(Color.LIGHT_GRAY);

		String val = (String)getProperty(InputWidget.PROP_VALUE);
		String delta = (String)getProperty(SliderWidget.PROP_DELTA);
		String min = (String)getProperty(SliderWidget.PROP_MIN);
		if(val != null && min != null && delta != null) {
			slider.setValue((int)((Double.parseDouble(val) - Double.parseDouble(min)) / Double.parseDouble(delta)));
		}

		Dimension d = new Dimension(40, 25);
		textField.setPreferredSize(d); textField.setMinimumSize(d); textField.setMaximumSize(d);
		if(orient == null || orient.equals("0")) {
			textField.setHorizontalAlignment(SwingConstants.RIGHT);
		} else {
			textField.setHorizontalAlignment(SwingConstants.CENTER);
		}

		MouseAdapter ma = new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
		};
		slider.addMouseListener(ma); frame.addMouseListener(ma);

		KeyAdapter ka = new KeyAdapter() {
			@Override public void keyPressed(final KeyEvent e) {
				int i = e.getKeyCode(); 
				if(i == KeyEvent.VK_LEFT || i == KeyEvent.VK_UP) {
					slider.setDecValue(Tensor.newScalar(slider.getDecValue().getValue() - slider.getDelta()));
					return;
				}
				if(i == KeyEvent.VK_RIGHT || i == KeyEvent.VK_DOWN ) {
					slider.setDecValue(Tensor.newScalar(slider.getDecValue().getValue() + slider.getDelta()));
					return;
				}
			};
		};
		panel.addKeyListener(ka);

		slider.addChangeListener(new ChangeListener() { 
			public void stateChanged(final ChangeEvent e) {    
				String v = slider.getDecValue().toString();
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
					v0 = slider.getDecValue().getValue();
					textField.setText(slider.getDecValue().toString());
				}
				double vmin = Double.parseDouble((String)getProperty(SliderWidget.PROP_MIN));
				if(v0 < vmin) { v0 = vmin; textField.setText(STTools.BLANK + v0); }
				double vmax = Double.parseDouble((String)getProperty(SliderWidget.PROP_MAX));
				if(v0 > vmax) { v0 = vmax; textField.setText(STTools.BLANK + v0); }
				double vdelta = Double.parseDouble((String)getProperty(SliderWidget.PROP_DELTA));
				if(vmax > 0.0 && vmin <= 0.0 && Math.abs(v0) < vdelta) { v0 = 0.0; textField.setText(STTools.BLANK + v0); }
				Tensor v = Tensor.newScalar(v0);
				slider.setDecValue(v);
				setProperty(InputWidget.PROP_VALUE, v);
				setTitle();
				graph.refreshGraph();
			}
		});

		setProperty(SliderWidget.PROP_MIN, defaultMin);
		setProperty(SliderWidget.PROP_DELTA, defaultDelta);
		setProperty(SliderWidget.PROP_MAX, defaultMax);
		setProperty(InputWidget.PROP_VALUE, defaultValue);

		slider.setParameters(Double.parseDouble(defaultMin), Double.parseDouble(defaultMax), Double.parseDouble(defaultDelta));
		slider.setDecValue(Tensor.newScalar(Double.parseDouble(defaultValue)));

		panel.add(slider);
		panel.add(textField);
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
	 * @param name the name
	 * @param min the min
	 * @param max the max
	 * @param delta the delta
	 * @param orient the orientation
	 * @return result */
	final boolean setProps(final String name, final String min, final String max, final String delta, final String orient) {
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
				setProperty(SliderWidget.PROP_MIN, min);
				slider.setMin(Double.parseDouble(min));
			}
			if(!STTools.isEmpty(max)) {
				setProperty(SliderWidget.PROP_MAX, max);
				slider.setMax(Double.parseDouble(max));
			}
			if(!STTools.isEmpty(delta)) {
				setProperty(SliderWidget.PROP_DELTA, delta);
				slider.setDelta(Double.parseDouble(delta));
			}
			if(!STTools.isEmpty(orient)) {
				setProperty(SliderWidget.PROP_ORIENT, orient);
				slider.setOrientation(orient.equals("0") ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL);
				panel.setLayout(new BoxLayout(panel, orient.equals("0") ? BoxLayout.LINE_AXIS : BoxLayout.Y_AXIS));
			}
			if(!STTools.isEmpty(min) && !STTools.isEmpty(max) && !STTools.isEmpty(delta)) { slider.setParameters(slider.getMin(), slider.getMax(), slider.getDelta()); }
			setTitle();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_SLIDER_FORMAT")); return false;
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
		STNode node = (STNode)getProperty(InputWidget.PROP_SOURCE_OB); //getGraph().getNodeByName(name);
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
		if((s = (String)am.get(SliderWidget.PROP_MIN)) != null) { dataToSave.put(SliderWidget.PROP_MIN, s); }
		if((s = (String)am.get(SliderWidget.PROP_MAX)) != null) { dataToSave.put(SliderWidget.PROP_MAX, s); }
		if((s = (String)am.get(SliderWidget.PROP_DELTA)) != null) { dataToSave.put(SliderWidget.PROP_DELTA, s); }
		if((s = (String)am.get(SliderWidget.PROP_ORIENT)) != null) { dataToSave.put(SliderWidget.PROP_ORIENT, s); }
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String name = (String)dataToSave.get(InputWidget.PROP_SOURCE_NA);
		String min = (String)dataToSave.get(SliderWidget.PROP_MIN);
		String max = (String)dataToSave.get(SliderWidget.PROP_MAX);
		String delta = (String)dataToSave.get(SliderWidget.PROP_DELTA);
		String orient = (String)dataToSave.get(SliderWidget.PROP_ORIENT);
		setProps(name, min, max, delta, orient);
		String v = (String)dataToSave.get(InputWidget.PROP_VALUE);
		if(!STTools.isEmpty(v)) { setValue(Tensor.newScalar(Double.parseDouble(v))); }
	}


	/** DecSlider class. */
	class DecSlider extends JSlider {
		/** The min. */
		private double min;
		/** The max. */
		private double max;
		/** The delta. */
		private double delta;
		/** The dec number. */
		private int decNumber;


		/** Set the slider parameters.
		 * @param min the min
		 * @param max the max
		 * @param delta the delta */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public final void setParameters(final double min, final double max, final double delta) {
			this.min = min;
			this.max = max;
			this.delta = delta;
			int numSteps = (int)((max - min) / delta);
			String t = Double.toString(delta);
			int tt = t.indexOf(STTools.DOT); // determine the number of decimal digits for rounding
			decNumber = (tt != -1) ? t.substring(tt).length() : 0;
			setMinimum(0);
			setMaximum(numSteps);
			setMajorTickSpacing(numSteps);
			Hashtable labelTable = new Hashtable();
			labelTable.put(Integer.valueOf(0), new JLabel(Double.toString(min)));
			labelTable.put(Integer.valueOf(numSteps), new JLabel(Double.toString(max)));
			setLabelTable(labelTable);
		}


		/** Set the value of this widget.
		 * @param value the value */ // +delta/2 is for avoiding stupid problems with int-rounded doubles...  
		public final void setDecValue(final Tensor value) { super.setValue((int)((delta / 2) + ((value.getValue() - min) / delta))); }


		/** Return the rounded value of this widget.
		 * @return value */
		public final Tensor getDecValue() { return Tensor.newScalar(STData.round(min + super.getValue() * delta, decNumber)); }


		/** Set the delta.
		 * @param delta the delta to set */
		public final void setDelta(final double delta) { this.delta = delta; }


		/** Get the delta.
		 * @return the delta */
		public final double getDelta() { return delta; }


		/** Set the max.
		 * @param max the max to set */
		public final void setMax(final double max) { this.max = max; }


		/** Get the max.
		 * @return the max */
		public final double getMax() { return max; }


		/** Set the min.
		 * @param min the min to set */
		public final void setMin(final double min) { this.min = min; }


		/** Get the min.
		 * @return the min */
		public final double getMin() { return min; }

	}

}
