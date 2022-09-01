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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.colorchooser.AbstractColorChooserPanel;


/** Custom color chooser. */
@SuppressWarnings("serial")
public class STColorChooser extends JColorChooser implements ActionListener {
	/** The color panel. */
	private ColorPanel colorPanel;
	/** Is enabled. */
	private boolean enabled;

	/** The constant for constructing a small button panel. */
	public static final int SMALL_BUTTON_PANEL = 0;
	/** The constant for constructing a medium button panel. */
	public static final int MEDIUM_BUTTON_PANEL = 1;
	/** The constant for constructing a large button panel. */
	public static final int LARGE_BUTTON_PANEL = 2;
	/** The constant for constructing a combo panel. */
	public static final int COMBO_PANEL = 3;

	/** The colors for nodes (245,245,245 is conventionally dealt with as transparent). */
	public static final Color[] myColors = new Color[] {
		new Color(245, 245, 245), Color.WHITE, Color.YELLOW, Color.RED, Color.GREEN, Color.CYAN, Color.BLUE, Color.DARK_GRAY, Color.BLACK
	};
	/** The color names for nodes. */
	public static final String[] myColorNames = new String[] {
		"transparent", "white", "yellow", "red", "green", "cyan", "blue", "darkgray", "black"
	};
	/** The color map for nodes. */
	public static final HashMap<String, Color> myColorMap = new HashMap<String, Color>();

	static {
		for(int i = 0; i < myColors.length; i++) { myColorMap.put(myColorNames[i], myColors[i]); }
	}


	/** Class constructor.
	 * @param panelType the panel type */
	public STColorChooser(final int panelType) {
		super();
		setPreviewPanel(new JPanel());
		if(panelType == SMALL_BUTTON_PANEL) {
			AbstractColorChooserPanel[] panels = { colorPanel = new ButtonColorPanel(this, 10, 15) }; 
			setChooserPanels(panels);
		} else if(panelType == MEDIUM_BUTTON_PANEL) {
			AbstractColorChooserPanel[] panels = { colorPanel = new ButtonColorPanel(this, 15, 15) }; 
			setChooserPanels(panels);
		} else if(panelType == LARGE_BUTTON_PANEL) {
			AbstractColorChooserPanel[] panels = { colorPanel = new ButtonColorPanel(this, 20, 15) }; 
			setChooserPanels(panels);
		}  else if(panelType == COMBO_PANEL) {
			AbstractColorChooserPanel[] panels = { colorPanel = new ComboColorPanel(this) }; 
			setChooserPanels(panels);
		}
	}


	/** Class constructor.
	 * @param panelType the panel type
	 * @param col the col */
	public STColorChooser(final int panelType, final Color col) {
		this(panelType);
		setColor(col);
	}


	/** Get the color panel.
	 * @return panel */
	public ColorPanel getColorPanel() { return colorPanel; }


	/** Set the selected color.
	 * @param col the col */
	public final void setColor(final Color col) {
		if(!enabled) { return; }
		colorPanel.setColor(col);
	}


	/** Return the selected color.
	 * @return color */
	public final Color getColor() { 
		if(colorPanel == null) { return null; } // required because of early calls...
		return colorPanel.getColor();
	}


	/** Remove any color selection. */
	public final void removeColorSelection() { colorPanel.removeColorSelection(); }


	/** Set enabled.
	 * @param b the b */
	public final void setEnabled(final boolean b) { colorPanel.setEnabled(enabled = b); }


	/** Get whether it is enabled.
	 * @return result */
	public final boolean isEnabled() { return enabled; }


	/** Action performed.
	 * @param e the e */
	public void actionPerformed(final ActionEvent e) { ; }


	/** Color panel abstract class. */
	abstract class ColorPanel extends AbstractColorChooserPanel implements ActionListener {
		/** The chooser container. */
		STColorChooser chooser;
		/** The color. */
		protected Color color = null;


		/** Class constructor. */
		ColorPanel() { ; }


		/** Update chooser. */
		@Override
		public final void updateChooser() { ; }


		/** Action performed.
		 * @param e the e */
		public abstract void actionPerformed(final ActionEvent e);


		/** Set the selected color.
		 * @param col the col */
		abstract void setColor(final Color col);


		/** Return the selected color.
		 * @return color */
		final Color getColor() { return color; }


		/** Remove any color selection. */
		abstract void removeColorSelection();


		/** Set enabled.
		 * @param b the b */
		@Override
		public abstract void setEnabled(final boolean b);


		/** Get the display name.
		 * @return name */
		@Override
		public final String getDisplayName() { return "Colors"; } //$NON-NLS-1$


		/** Get the small icon.
		 * @return icon */
		@Override
		public final Icon getSmallDisplayIcon() { return null; }


		/** Get the large icon.
		 * @return icon */
		@Override
		public final Icon getLargeDisplayIcon() { return null; }

	}


	/** Button color panel class. */
	class ButtonColorPanel extends ColorPanel {
		/** The colors. */
		private ButtonGroup colors = new ButtonGroup();
		/** The color array. */
		private transient JButton[] colorArray = new JButton[myColors.length];
		/** The button. */
		private JButton button = null;
		/** The button width. */
		private int buttonWidth;
		/** The button height. */
		private int buttonHeight;


		/** Class constructor.
		 * @param chooser the chooser
		 * @param buttonWidth the button width
		 * @param buttonHeight the button height */
		ButtonColorPanel(final STColorChooser chooser, final int buttonWidth, final int buttonHeight) {
			this.chooser = chooser;
			this.buttonWidth = buttonWidth;
			this.buttonHeight = buttonHeight;
		}


		/** Build chooser. */
		@Override
		protected final void buildChooser() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					buildChooserHelper();
				}
			});
		}


		private final void buildChooserHelper() {
			JButton b = null;
			setLayout(new GridLayout(1, 0));
			for(int i = 0; i < myColors.length; i++) {
				colorArray[i] = b = createColor(myColors[i]);
				colors.add(b);
				add(b);
			}
		}


		/** Create color button.
		 * @param col the color
		 * @return button */
		private final JButton createColor(final Color col) {
			Icon icon = new ImageIcon() {
				public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
					g.setColor(col);
					g.fillRect(0, 0, buttonWidth, buttonHeight);
					if(col.getRed() == 245 && col.getGreen() == 245 && col.getBlue() == 245) {
						g.setColor(Color.BLACK);
						g.drawLine(0, 0, buttonWidth, buttonHeight);
						g.drawLine(0, buttonHeight, buttonWidth, 0);
					}
				}
			};
			JButton butt = new JButton(icon);
			butt.setDisabledIcon(icon);
			butt.addActionListener(this);
			butt.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
			butt.setBackground(col);
			butt.setForeground(col);
			butt.setHorizontalAlignment(SwingConstants.HORIZONTAL);
			butt.setBorder(BorderFactory.createLineBorder(Color.WHITE));
			return butt;
		}


		/** Action performed.
		 * @param e the e */
		@Override
		public final void actionPerformed(final ActionEvent e) {
			if(chooser.enabled) { activateColor((JButton)e.getSource()); }
			chooser.actionPerformed(e); // push the event to the container, so it can be overridden
		}


		/** Set the selected color.
		 * @param col the col */
		@SuppressWarnings("rawtypes")
		@Override
		public final void setColor(final Color col) {
			Enumeration buttons = colors.getElements(); 
			JButton butt;
			while(buttons.hasMoreElements()) {
				butt = (JButton)buttons.nextElement();
				if(butt.getBackground().equals(col)) {
					activateColor(butt);
					break;
				}
			}
		}


		/** Activate color.
		 * @param butt the butt */
		private final void activateColor(final JButton butt) {
			if(button != null) { button.setBorder(BorderFactory.createLineBorder(Color.WHITE)); }
			button = butt;
			button.setBorder(BorderFactory.createLineBorder(Color.RED));
			color = button.getBackground();
		}


		/** Remove any color selection. */
		@SuppressWarnings("rawtypes")
		@Override final void removeColorSelection() {
			Enumeration buttons = colors.getElements();
			while(buttons.hasMoreElements()) { ((JButton)buttons.nextElement()).setBorder(BorderFactory.createLineBorder(Color.WHITE)); }
		}


		/** Set enabled.
		 * @param b the b */
		@SuppressWarnings("rawtypes")
		@Override public final void setEnabled(final boolean b) {
			enabled = b;
			Enumeration buttons = colors.getElements();
			while(buttons.hasMoreElements()) { ((JButton)buttons.nextElement()).setEnabled(b); }
		}

	}


	/** Combo color panel class. */
	class ComboColorPanel extends ColorPanel {
		/** The color combo. */
		@SuppressWarnings("rawtypes")
		private JComboBox colors = new JComboBox();
		/** The color array. */
		private transient Object[] colorArray = new Object[myColors.length];
		/** The button width. */
		private int buttonWidth = 15;
		/** The button height. */
		private int buttonHeight = 15;


		/** Class constructor.
		 * @param chooser the chooser */
		ComboColorPanel(final STColorChooser chooser) { this.chooser = chooser; }


		/** Build chooser. */
		@Override
		protected final void buildChooser() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					buildChooserHelper();
				}
			});
		}


		@SuppressWarnings("unchecked")
		private final void buildChooserHelper() {
			try { // to fix a bug of Nimbus LaF...
				setLayout(new GridLayout(1, 0));
				Object o;
				for(int i = 0; i < myColors.length; i++) {
					colorArray[i] = o = createColor(myColors[i]);
					colors.addItem(o);
				}
				colors.addActionListener(this);
				colors.setPreferredSize(new Dimension(40, 20));
				add(colors);
			} catch (Exception e) { ; }
		}


		/** Create color button.
		 * @param col the color
		 * @return icon */
		private final Icon createColor(final Color col) {
			Icon icon = new ImageIcon() {
				public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
					g.setColor(col);
					g.fillRect(0, 0, buttonWidth, buttonHeight);
					if(col.getRed() == 245 && col.getGreen() == 245 && col.getBlue() == 245) {
						g.setColor(Color.BLACK);
						g.drawLine(0, 0, buttonWidth, buttonHeight);
						g.drawLine(0, buttonHeight, buttonWidth, 0);
					}
				}
			};
			return icon;
		}


		/** Action performed.
		 * @param e the e */
		public final void actionPerformed(final ActionEvent e) {
			if(chooser.enabled) { color = myColors[colors.getSelectedIndex()]; }
			chooser.actionPerformed(e); // push the event to the container, so it can be overridden
		}


		/** Set the selected color.
		 * @param col the col */
		@Override
		public final void setColor(final Color col) {
			for(int i = 0; i < myColors.length; i++) {
				if(myColors[i].equals(col)) {
					colors.setSelectedIndex(i);
					color = col;
					break;
				}
			}
		}


		/** Remove any color selection. */
		@Override
		final void removeColorSelection() { ; }


		/** Set enabled.
		 * @param b the b */
		@Override
		public final void setEnabled(final boolean b) { colors.setEnabled(enabled = b); }

	}

}
