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
package it.liuc.stgraph.util;

import it.liuc.stgraph.node.STData;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Arc2D;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.nfunk.jep.type.Tensor;


/** DKnob.java (c) 2000 by Joakim Eriksson
 * <br>Modified and adapted to STGraph by LM
 * <br>DKnob is a component similar to JSlider but with round user interface: a knob. */
@SuppressWarnings("serial")
public class KnobPanel extends JComponent {
	private final static float START = 225;
	private final static float LENGTH = 270;
	private final static float PI = (float)Math.PI;
	private final static float START_ANG = (START / 360) * PI * 2;
	private final static float LENGTH_ANG = (LENGTH / 360) * PI * 2;
	private final static float MULTIP = 180 / PI; 

	private int SHADOWX = 1;
	private int SHADOWY = 1;
	//private float DRAG_SPEED = 0.01F;
	//private float CLICK_SPEED = 0.01F;

	private int size;
	private int middle;

	private ChangeEvent changeEvent = null;
	private EventListenerList listenerList = new EventListenerList();

	private Arc2D hitArc = new Arc2D.Float(Arc2D.PIE);

	private float ang = START_ANG;
	private float val;
	//private int dragpos = -1;
	//private float startVal;
	private Color focusColor;
	private double lastAng;

	private final static Color DEFAULT_FOCUS_COLOR = new Color(0x8080ff);

	private final static Dimension MIN_SIZE = new Dimension(40, 40);
	private final static Dimension PREF_SIZE = new Dimension(80, 80);

	// Set the antialiasing to get the right look!
	private final static RenderingHints AALIAS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	/** The min. */
	private double min;
	/** The max. */
	private double max;
	/** The delta. */
	private double delta;
	/** The dec number. */
	private int decNumber;


	/** Class constructor. */
	public KnobPanel() {
		focusColor = DEFAULT_FOCUS_COLOR;

		setPreferredSize(PREF_SIZE);
		hitArc.setAngleStart(235);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if(!isEnabled()) { return; }
				//dragpos = me.getX() + me.getY();
				//startVal = val;
				int xpos = middle - me.getX(); // fix last angle
				int ypos = middle - me.getY();
				lastAng = Math.atan2(xpos, ypos);
				requestFocus();
			}

			public void mouseClicked(MouseEvent me) {
				if(!isEnabled()) { return; }
				hitArc.setAngleExtent(-(LENGTH + 20));
				if(hitArc.contains(me.getX(), me.getY())) {	   
					hitArc.setAngleExtent(MULTIP * (ang - START_ANG) - 10);
					if(hitArc.contains(me.getX(), me.getY())) {
						decValue();
					} else {
						incValue();
					}
				}
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() { // let the user control the knob with the mouse
			public void mouseDragged(MouseEvent me) {
				if(!isEnabled()) { return; }
				int xpos = middle - me.getX(); // measure relatively the middle of the button
				int ypos = middle - me.getY();
				double ang = Math.atan2(xpos, ypos);
				double diff = lastAng - ang;
				double d = getDelta();
				if(diff >= d / LENGTH_ANG) {
					setValue((float)(getValue() + d));
					lastAng = ang;
				} else if(diff <= -d / LENGTH_ANG) {
					setValue((float)(getValue() - d));
					lastAng = ang;
				}
			}

			public void mouseMoved(MouseEvent me) { ; }
		});

		addKeyListener(new KeyListener() { // let the user control the knob with the keyboard
			public void keyTyped(KeyEvent e) { ; }
			public void keyReleased(KeyEvent e) { ; } 
			public void keyPressed(KeyEvent e) {
				if(!isEnabled()) { return; }
				int k = e.getKeyCode();
				if(k == KeyEvent.VK_RIGHT) {
					incValue();
				} else if(k == KeyEvent.VK_LEFT) {
					decValue();
				}
			}		
		});

		addFocusListener(new FocusListener() { // handle focus so that the knob gets the correct focus highlighting
			public void focusGained(FocusEvent e) { repaint(); }
			public void focusLost(FocusEvent e) { repaint(); }
		});
	}


	//public boolean isManagingFocus() { return true; }


	//public boolean isFocusTraversable() { return true; }


	private void incValue() { setValue(val + (float)getDelta()); }


	private void decValue() { setValue(val - (float)getDelta()); }


	public float getValue() { return val; }


	public void setValue(float val) {
		if(val < 0) { val = 0; }
		if(val > 1) { val = 1; }
		this.val = val;
		ang = START_ANG - LENGTH_ANG * val;
		repaint();
		fireChangeEvent();
	}


	public void addChangeListener(ChangeListener cl) { listenerList.add(ChangeListener.class, cl); }


	public Dimension getMinimumSize() { return MIN_SIZE; }


	protected void fireChangeEvent() {
		Object[] listeners = listenerList.getListenerList(); // guaranteed to return a non-null array
		// process the listeners last to first, notifying those that are interested in this event
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == ChangeListener.class) { // lazily create the event
				if(changeEvent == null) { changeEvent = new ChangeEvent(this); }
				((ChangeListener)listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}


	/** Paint this widget.
	 * @param g the g */
	@Override
	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		size = Math.min(width, height) - 22;
		middle = 10 + size / 2;

		if(g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D)g;
			g2d.setBackground(getParent().getBackground());
			g2d.addRenderingHints(AALIAS);
			hitArc.setFrame(4, 4, size + 12, size + 12); // for the size of the mouse click area
		}

		for(float a2 = START_ANG; a2 >= START_ANG - LENGTH_ANG; a2 = a2 -(float)(LENGTH_ANG / 10.01)) { // paint the markers
			int x = 10 + size / 2 + (int)((6 + size / 2) * Math.cos(a2));
			int y = 10 + size / 2 - (int)((6 + size / 2) * Math.sin(a2));
			g.drawLine(10 + size / 2, 10 + size / 2, x, y);
		}

		g.drawString(getMin() + "", 2, size + 20); // set the position of the min value //$NON-NLS-1$
		g.drawString(getMax() + "", size - 10, size + 20); // set the position of the max value //$NON-NLS-1$

		if(hasFocus()) { // paint focus if in focus
			g.setColor(focusColor);
		} else {
			g.setColor(Color.white);
		}

		g.fillOval(10, 10, size, size);
		g.setColor(Color.gray);
		g.fillOval(14 + SHADOWX, 14 + SHADOWY, size - 8, size - 8);

		g.setColor(Color.black);
		g.drawArc(10, 10, size, size, 315, 270);
		g.fillOval(14, 14, size - 8, size - 8);
		g.setColor(Color.white);

		int x = 10 + size / 2 + (int)(size / 2 * Math.cos(ang));
		int y = 10 + size / 2 - (int)(size / 2 * Math.sin(ang));
		g.drawLine(10 + size / 2, 10 + size / 2, x, y);
		g.setColor(Color.gray);
		int s2 = Math.max(size / 6, 6);
		g.drawOval(10 + s2, 10 + s2, size - s2 * 2, size - s2 * 2);

		int dx = (int)(2 * Math.sin(ang));
		int dy = (int)(2 * Math.cos(ang));
		g.drawLine(10 + dx + size / 2, 10 + dy + size / 2, x, y);
		g.drawLine(10 - dx + size / 2, 10 - dy + size / 2, x, y);
	}


	/** Set the value of this widget.
	 * @param value the value */
	public final void setDecValue(final Tensor value) { setValue((float)((value.getValue() - min) / (max - min))); }


	/** Get the value of this widget.
	 * @return value */
	public final Tensor getDecValue() { return Tensor.newScalar(STData.round(min + getValue() * (max - min), decNumber)); }


	/** Set the delta.
	 * @param delta the delta to set */
	public final void setDelta(final double delta) {
		this.delta = delta;
		String t = Double.toString(delta);
		int tt = t.indexOf(STTools.DOT); // determine the number of decimal digits for rounding
		decNumber = (tt != -1) ? t.substring(tt).length() : 0;
	}


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
