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
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STTools;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jgraph.graph.AttributeMap;
import org.nfunk.jep.type.Tensor;


/** FourButton widget. */
@SuppressWarnings("serial")
public class FourButtonWidget extends STWidget implements InputWidget {
	/** The Constant PREFIX. */
	public static final String PREFIX = "FOURBUTTON"; //$NON-NLS-1$
	/** The Constant ID. */
	public static final String ID = "FourButtonWidget"; //$NON-NLS-1$
	/** The icon up. */
	private static ImageIcon iconUp;
	/** The icon down. */
	private static ImageIcon iconDown;
	/** The icon left. */
	private static ImageIcon iconLeft;
	/** The icon right. */
	private static ImageIcon iconRight;
	/** The Constant PROP_ACTION. */
	public static final String PROP_ACTION = "action"; // int: action //$NON-NLS-1$ 	
	/** The button up. */
	private final JToggleButton buttonUp = new JToggleButton();
	/** The button down. */
	private final JToggleButton buttonDown = new JToggleButton();
	/** The button left. */
	private final JToggleButton buttonLeft = new JToggleButton();
	/** The button right. */
	private final JToggleButton buttonRight = new JToggleButton();

	static {
		Properties props = STConfigurator.getProperties();
		iconUp = new ImageIcon(STGraph.class.getClassLoader().getResource("icons/base/" + props.getProperty(FourButtonWidget.PREFIX + ".ICONUP"))); //$NON-NLS-1$ //$NON-NLS-2$
		iconDown = new ImageIcon(STGraph.class.getClassLoader().getResource("icons/base/" + props.getProperty(FourButtonWidget.PREFIX + ".ICONDOWN"))); //$NON-NLS-1$ //$NON-NLS-2$
		iconLeft = new ImageIcon(STGraph.class.getClassLoader().getResource("icons/base/" + props.getProperty(FourButtonWidget.PREFIX + ".ICONLEFT"))); //$NON-NLS-1$ //$NON-NLS-2$
		iconRight = new ImageIcon(STGraph.class.getClassLoader().getResource("icons/base/" + props.getProperty(FourButtonWidget.PREFIX + ".ICONRIGHT"))); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/** Class constructor. */
	public FourButtonWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public FourButtonWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new FourButtonWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new FourButtonWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("FOURBUTTON.LABEL"); } //$NON-NLS-1$


	/** Get the value currently set by this widget.
	 * @return value */
	@Override public final Object getValue() {
		if(buttonUp.isSelected()) { return Tensor.newScalar(1); }
		if(buttonRight.isSelected()) { return Tensor.newScalar(2); }
		if(buttonDown.isSelected()) { return Tensor.newScalar(3); }
		if(buttonLeft.isSelected()) { return Tensor.newScalar(4); }
		return Tensor.newScalar(0);
	}


	/** Get the next value of this widget.
	 * @param isFirst the is first
	 * @return value */
	@Override public final Object getNextValue(final boolean isFirst) { return getValue(); }


	/** Set the current value of this widget.
	 * @param value the value */
	public final void setValue(final Tensor value) { ; }


	/** Set whether it is possible to interact with this widget.
	 * @param state the state */
	@Override public final void setInteractable(final boolean state) {
		buttonUp.setEnabled(state);
		buttonDown.setEnabled(state);
		buttonLeft.setEnabled(state);
		buttonRight.setEnabled(state);
	}


	/** Set the panel.
	 * @param bounds the bounds */
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new GridBagLayout());

		buttonUp.setIcon(iconUp);
		buttonDown.setIcon(iconDown);
		buttonLeft.setIcon(iconLeft);
		buttonRight.setIcon(iconRight);

		panel.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				Dimension d = panel.getSize();
				int w = d.width / 6;
				int h = d.height / 6;
				buttonUp.setIcon(new ImageIcon(iconUp.getImage().getScaledInstance(h, h, Image.SCALE_SMOOTH)));
				buttonDown.setIcon(new ImageIcon(iconDown.getImage().getScaledInstance(h, h, Image.SCALE_SMOOTH)));
				buttonLeft.setIcon(new ImageIcon(iconLeft.getImage().getScaledInstance(w, w, Image.SCALE_SMOOTH)));
				buttonRight.setIcon(new ImageIcon(iconRight.getImage().getScaledInstance(w, w, Image.SCALE_SMOOTH)));
			}

			public void componentShown(ComponentEvent e) { ; }
			public void componentMoved(ComponentEvent e) { ; }
			public void componentHidden(ComponentEvent e) { ; }
		});

		buttonUp.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { 
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(FourButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Tensor v = (Tensor)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { buttonUp.setSelected(v.getValue() == 1); buttonDown.setSelected(false); buttonLeft.setSelected(false); buttonRight.setSelected(false); }
					} else { buttonUp.setSelected(true); buttonDown.setSelected(false); buttonLeft.setSelected(false); buttonRight.setSelected(false); }
				} else if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); }
			}

			@Override public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(FourButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Tensor v = (Tensor)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { buttonUp.setSelected(v.getValue() == 1); buttonDown.setSelected(false); buttonLeft.setSelected(false); buttonRight.setSelected(false); }
						if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
							try {
								((STGraphExec)graph).computeInteractively();
								((STGraphExec)graph).endExec(true);
							} catch (Exception ex) { }
						} else {
							((STGraphExec)graph).computeInteractivelyOnTheFly();
						}
					} else { buttonUp.setSelected(false); buttonDown.setSelected(false); buttonLeft.setSelected(false); buttonRight.setSelected(false); }
				} else if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
				graph.refreshGraph();
			}
		});

		buttonDown.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { 
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(FourButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Tensor v = (Tensor)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { buttonDown.setSelected(v.getValue() == 3); buttonUp.setSelected(false); buttonLeft.setSelected(false); buttonRight.setSelected(false); }
					} else { buttonDown.setSelected(true); buttonUp.setSelected(false); buttonLeft.setSelected(false); buttonRight.setSelected(false); }
				} else if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); }
			}

			@Override public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(FourButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Tensor v = (Tensor)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { buttonDown.setSelected(v.getValue() == 3); buttonUp.setSelected(false); buttonLeft.setSelected(false); buttonRight.setSelected(false); }
						if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
							try {
								((STGraphExec)graph).computeInteractively();
								((STGraphExec)graph).endExec(true);
							} catch (Exception ex) { }
						} else {
							((STGraphExec)graph).computeInteractivelyOnTheFly();
						}
					} else { buttonDown.setSelected(false); buttonUp.setSelected(false); buttonLeft.setSelected(false); buttonRight.setSelected(false); }
				} else if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
				graph.refreshGraph();
			}
		});

		buttonLeft.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { 
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(FourButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Tensor v = (Tensor)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { buttonLeft.setSelected(v.getValue() == 4); buttonUp.setSelected(false); buttonDown.setSelected(false); buttonRight.setSelected(false); }
					} else { buttonLeft.setSelected(true); buttonUp.setSelected(false); buttonDown.setSelected(false); buttonRight.setSelected(false); }
				} else if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); }
			}

			@Override public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(FourButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Tensor v = (Tensor)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { buttonLeft.setSelected(v.getValue() == 4); buttonUp.setSelected(false); buttonDown.setSelected(false); buttonRight.setSelected(false); }
						if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
							try {
								((STGraphExec)graph).computeInteractively();
								((STGraphExec)graph).endExec(true);
							} catch (Exception ex) { }
						} else {
							((STGraphExec)graph).computeInteractivelyOnTheFly();
						}
					} else { buttonLeft.setSelected(false); buttonUp.setSelected(false); buttonDown.setSelected(false); buttonRight.setSelected(false); }
				} else if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
				graph.refreshGraph();
			}
		});

		buttonRight.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(final MouseEvent e) { 
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(FourButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Tensor v = (Tensor)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { buttonRight.setSelected(v.getValue() == 2); buttonUp.setSelected(false); buttonDown.setSelected(false); buttonLeft.setSelected(false); }
					} else { buttonRight.setSelected(true); buttonUp.setSelected(false); buttonDown.setSelected(false); buttonLeft.setSelected(false); }
				} else if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); }
			}

			@Override public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 1) {
					Integer i = (Integer)getProperty(FourButtonWidget.PROP_ACTION);
					int theAction = (i != null) ? i.intValue() : 1; // with a default value for backward compatibility...
					if(theAction == 1) {
						Tensor v = (Tensor)getProperty(InputWidget.PROP_VALUE);
						if(v != null) { buttonRight.setSelected(v.getValue() == 2); buttonUp.setSelected(false); buttonDown.setSelected(false); buttonLeft.setSelected(false); }
						if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
							try {
								((STGraphExec)graph).computeInteractively();
								((STGraphExec)graph).endExec(true);
							} catch (Exception ex) { }
						} else {
							((STGraphExec)graph).computeInteractivelyOnTheFly();
						}
					} else { buttonRight.setSelected(false); buttonUp.setSelected(false); buttonDown.setSelected(false); buttonLeft.setSelected(false); }
				} else if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
				graph.refreshGraph();
			}
		});

		buttonUp.addChangeListener(new ChangeListener() { 
			public void stateChanged(final ChangeEvent e) {
				setProperty(InputWidget.PROP_VALUE, getValue());
				setTitle();
				graph.refreshGraph();
			}
		});
		buttonDown.addChangeListener(new ChangeListener() { 
			public void stateChanged(final ChangeEvent e) {
				setProperty(InputWidget.PROP_VALUE, getValue());
				setTitle();
				graph.refreshGraph();
			}
		});
		buttonLeft.addChangeListener(new ChangeListener() { 
			public void stateChanged(final ChangeEvent e) {
				setProperty(InputWidget.PROP_VALUE, getValue());
				setTitle();
				graph.refreshGraph();
			}
		});
		buttonRight.addChangeListener(new ChangeListener() { 
			public void stateChanged(final ChangeEvent e) {
				setProperty(InputWidget.PROP_VALUE, getValue());
				setTitle();
				graph.refreshGraph();
			}
		});

		KeyAdapter ka = new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				int i = e.getKeyCode(); 
				if(i == KeyEvent.VK_LEFT) {
					buttonLeft.doClick();
					return;
				}
				if(i == KeyEvent.VK_UP) {
					buttonUp.doClick();
					return;
				}
				if(i == KeyEvent.VK_RIGHT) {
					buttonRight.doClick();
					return;
				}
				if(i == KeyEvent.VK_DOWN) {
					buttonDown.doClick();
					return;
				}
			};
		};
		panel.addKeyListener(ka);

		GridConstraints gbcU = new GridConstraints(0, 0);
		gbcU.gridwidth = 2;
		gbcU.fill = GridBagConstraints.BOTH;
		panel.add(buttonUp, gbcU);
		GridConstraints gbcD = new GridConstraints(0, 2);
		gbcD.gridwidth = 2;
		gbcD.fill = GridBagConstraints.BOTH;
		panel.add(buttonDown, gbcD);
		GridConstraints gbcL = new GridConstraints(0, 1);
		gbcL.fill = GridBagConstraints.BOTH;
		panel.add(buttonLeft, gbcL);
		GridConstraints gbcR = new GridConstraints(1, 1);
		gbcR.fill = GridBagConstraints.BOTH;
		panel.add(buttonRight, gbcR);
		setTitle();
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() {
		String w = (String)getProperty(InputWidget.PROP_SOURCE_NA);
		if(w == null) {
			setProperty(STWidget.PROP_TITLE, STGraphC.getMessage("WIDGET.UNBOUND")); //$NON-NLS-1$
			setTitle((String)getProperty(STWidget.PROP_TITLE));
		} else {
			setProperty(STWidget.PROP_TITLE, w);
			setTitle((String)getProperty(STWidget.PROP_TITLE) + " - " + getValue()); //$NON-NLS-1$
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
			setProperty(FourButtonWidget.PROP_ACTION, new Integer(action));
			buttonUp.setSelected(false);
			buttonDown.setSelected(false);
			buttonLeft.setSelected(false);
			buttonRight.setSelected(false);
			setTitle();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_FOURBUTTON_FORMAT")); return false; //$NON-NLS-1$
		}
		return true;
	}


	/** Check whether the renamed node is referenced by the four button, and in that case set the new name accordingly.
	 * @param oldName the old name
	 * @param newName the new name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		String expr = (String)getProperty(InputWidget.PROP_SOURCE_NA);
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


	/** Check whether the removed node is referenced by the four button, and in that case set the name to <code>null</code>.
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
		if((i = ((Integer)am.get(FourButtonWidget.PROP_ACTION))) != null) { dataToSave.put(FourButtonWidget.PROP_ACTION, i.toString()); }
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String name = (String)dataToSave.get(InputWidget.PROP_SOURCE_NA);
		String i = (String)dataToSave.get(FourButtonWidget.PROP_ACTION);
		int action = (i != null) ? Integer.parseInt(i) : 1; // with a default value for backward compatibility...
		setProps(name, action);
		setValue(Tensor.newScalar(Double.parseDouble((String)dataToSave.get(InputWidget.PROP_VALUE))));
	}

}
