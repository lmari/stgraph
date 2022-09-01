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
package it.liuc.stgraph;

import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STColorChooser;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.widget.STWidget;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;


/** Handle the interactive status bar. */
@SuppressWarnings("serial")
public class STStatusBar extends JPanel implements ComponentListener {
	/** The control area subpanel. */
	private JPanel controlArea;
	/** The node name. */
	private JTextField nodeName;
	/** The check out. */
	private JCheckBox checkOut;
	/** The label for the fontcolor controller. */
	private JLabel fontLabel;
	/** The label for the forecolor controller. */
	private JLabel foreLabel;
	/** The label for the backcolor controller. */
	private JLabel backLabel;
	/** The fontcolor controller. */
	private STColorChooser fontColor;
	/** The forecolor controller. */
	private STColorChooser foreColor;
	/** The backcolor controller. */
	private STColorChooser backColor;
	/** The info area. */
	private JTextField infoArea;
	/** The info BG color. */
	private static Color infoBGColor;
	/** The err BG color. */
	private static Color errBGColor = Color.YELLOW;
	/** The exc BG color. */
	private static Color excBGColor = Color.RED;

	private STNode node = null;


	/** Class constructor. */
	public STStatusBar() {
		super();
		addComponentListener(this);
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		setBorder(BorderFactory.createLoweredBevelBorder());
		setPreferredSize(new Dimension(800, 32));

		controlArea = new JPanel();
		controlArea.setLayout(new FlowLayout(FlowLayout.LEFT));
		controlArea.setBorder(null);
		nodeName = new JTextField();
		nodeName.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10));
		nodeName.setPreferredSize(new Dimension(120, 24));
		nodeName.setMinimumSize(new Dimension(120, 24));
		nodeName.setMaximumSize(new Dimension(120, 24));
		nodeName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(node.checkName(nodeName.getText()) != null) {
					nodeName.setBackground(Color.LIGHT_GRAY);
					return;
				}
				String t1 = nodeName.getText();
				String t1b = node.getName();
				nodeName.setBackground(Color.WHITE);
				node.redefineVars(t1b, t1);
				node.setName(t1);
				node.handleNodeRenaming(t1b);
				STGraph.getSTC().signalCurrentGraphAsModified();
				nodeName.requestFocus();
			}
		});
		controlArea.add(nodeName);
		checkOut = new JCheckBox(STGraphC.getMessage("NODE.DIALOG.ISOUTPUT")); //$NON-NLS-1$
		checkOut.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10));
		checkOut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				STGraphExec graph = STGraph.getSTC().getCurrentGraph();
				if(!graph.isEditable) { return; }
				boolean o = checkOut.isSelected();
				for(Object obj : graph.getSelectionCells()) {
					if(STTools.isNode(obj)) {
						((STNode)obj).setOutput(o);
						if(!o) {
							String t1b = node.getName();
							for(STWidget widget : STGraph.getSTC().getCurrentGraph().getWidgets()) { widget.handleNodeRemoval(t1b); }
						}
					}
				}
				STGraph.getSTC().signalCurrentGraphAsModified();
			}
		});
		controlArea.add(checkOut);

		fontLabel = new JLabel(STGraphC.getMessage("NODE.DIALOG.FONTCOL2")); //$NON-NLS-1$
		fontLabel.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10));
		controlArea.add(fontLabel);
		controlArea.add(Box.createHorizontalGlue());
		fontColor = new FontColorChooser(STColorChooser.COMBO_PANEL);
		controlArea.add(fontColor);
		fontColor.setEnabled(true);

		foreLabel = new JLabel(STGraphC.getMessage("NODE.DIALOG.FORECOL2")); //$NON-NLS-1$
		foreLabel.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10));
		controlArea.add(foreLabel);
		controlArea.add(Box.createHorizontalGlue());
		foreColor = new ForeColorChooser(STColorChooser.COMBO_PANEL);
		controlArea.add(foreColor);
		foreColor.setEnabled(true);

		backLabel = new JLabel(STGraphC.getMessage("NODE.DIALOG.BACKCOL2")); //$NON-NLS-1$
		backLabel.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10));
		controlArea.add(backLabel);
		controlArea.add(Box.createHorizontalGlue());
		backColor = new BackColorChooser(STColorChooser.COMBO_PANEL);
		controlArea.add(backColor);
		backColor.setEnabled(true);

		add(controlArea);

		infoArea = new JTextField();
		infoArea.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10));
		infoArea.setHorizontalAlignment(SwingConstants.RIGHT);
		infoArea.setBorder(null);
		infoArea.setVisible(true);
		infoArea.setEditable(false);
		infoArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
		infoArea.addMouseListener(new MouseAdapter() {
			public void mouseClicked(final MouseEvent e) { highlightNode(); }
		});
		dimInfoArea();
		infoBGColor = infoArea.getBackground();
		add(infoArea);

		layout.putConstraint(SpringLayout.WEST, controlArea, 2, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, infoArea, -5, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, infoArea, 5, SpringLayout.NORTH, this);

		setControlVisibile(false);
	}



	/** Get whether this bar has currently the focus.
	 * @return has this bar currently the focus? */
	public boolean isWithFocus() { return nodeName.isFocusOwner(); }


	/** Set the focus on this bar, specifically the node name field. */
	public void setFocus() { nodeName.requestFocusInWindow(); }


	/** Chooser for font color. */
	private class FontColorChooser extends STColorChooser implements ActionListener {


		/** Class constructor.
		 * @param panelType the panel type */
		public FontColorChooser(int panelType) { super(panelType); }


		/** Action handler. */
		public final void actionPerformed(final ActionEvent e) {
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			if(!graph.isEditable) { return; }
			Color c = getColor();
			boolean changed = false;
			for(Object obj : graph.getSelectionCells()) {
				if(STTools.isNode(obj)) {
					if(!((STNode)obj).getFontColor().equals(c)) {
						((STNode)obj).setFontColor(c);
						changed = true;	
					}
				}
			}
			if(changed) { STGraph.getSTC().signalCurrentGraphAsModified(); }
		}

	}


	/** Chooser for border color. */
	private class ForeColorChooser extends STColorChooser implements ActionListener {


		/** Class constructor.
		 * @param panelType the panel type */
		public ForeColorChooser(int panelType) { super(panelType); }


		/** Action handler. */
		public final void actionPerformed(final ActionEvent e) {
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			if(!graph.isEditable) { return; }
			Color c = getColor();
			boolean changed = false;
			for(Object obj : graph.getSelectionCells()) {
				if(STTools.isNode(obj)) {
					if(!((STNode)obj).getForeColor().equals(c)) {
						((STNode)obj).setForeColor(c);
						changed = true;	
					}
				}
			}
			if(changed) { STGraph.getSTC().signalCurrentGraphAsModified(); }
		}

	}


	/** Chooser for background color. */
	private class BackColorChooser extends STColorChooser implements ActionListener {


		/** Class constructor.
		 * @param panelType the panel type */
		public BackColorChooser(int panelType) { super(panelType); }


		/** Action handler. */
		public final void actionPerformed(final ActionEvent e) {
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			if(!graph.isEditable) { return; }
			Color c = getColor();
			boolean changed = false;
			for(Object obj : graph.getSelectionCells()) {
				if(STTools.isNode(obj)) {
					if(!((STNode)obj).getBackColor().equals(c)) {
						((STNode)obj).setBackColor(c);
						changed = true;	
					}
				}
			}
			if(changed) { STGraph.getSTC().signalCurrentGraphAsModified(); }
		}

	}


	/** Set the visibility state of the control area.
	 * @param visible is visible */
	public final void setControlVisibile(final boolean visible) {
		controlArea.setVisible(visible);
		if(visible) {
			STGraphC stc = STGraph.getSTC();
			if(stc == null) { return; }
			STGraphExec graph = stc.getCurrentGraph();
			if(graph == null) { return; }
			if(graph.isEditable) {
				node = null;
				ArrayList<Boolean> outputs = new ArrayList<Boolean>();
				ArrayList<Color> fontColors = new ArrayList<Color>();
				ArrayList<Color> foreColors = new ArrayList<Color>();
				ArrayList<Color> backColors = new ArrayList<Color>();
				for(Object obj : graph.getSelectionCells()) {
					if(STTools.isNode(obj)) {
						node = (STNode)obj;
						if(node.isVariable()) { outputs.add(Boolean.valueOf(node.isOutput())); }
						fontColors.add(node.getFontColor());
						foreColors.add(node.getForeColor());
						backColors.add(node.getBackColor());
					}
				}
				boolean isOutputSame = true;
				boolean isFontSame = true;
				boolean isForeSame = true;
				boolean isBackSame = true;
				Boolean o = outputs.size() > 0 ? outputs.get(0) : Boolean.valueOf(false);
				Color xC = fontColors.get(0);
				Color fC = foreColors.get(0);
				Color bC = backColors.get(0);
				for(int i = 1; i < foreColors.size(); i++) {
					if(!fontColors.get(i).equals(xC)) { isFontSame = false; }
					if(!foreColors.get(i).equals(fC)) { isForeSame = false; }
					if(!backColors.get(i).equals(bC)) { isBackSame = false; }
				}
				for(int i = 1; i < outputs.size(); i++) {
					if(!outputs.get(i).equals(o)) { isOutputSame = false; }
				}
				if(fontColors.size() == 1) {
					nodeName.setEnabled(true);
					nodeName.setText(node.getName());
				} else {
					nodeName.setEnabled(false);
					nodeName.setText("..."); //$NON-NLS-1$
				}
				if(outputs.size() == 0) {
					checkOut.setEnabled(false);
					checkOut.setSelected(false);
				} else {
					checkOut.setEnabled(true);
					if(isOutputSame) { checkOut.setSelected(o.booleanValue()); } else { checkOut.setSelected(false); }
				}
				if(isFontSame) { fontColor.setColor(xC); } else { fontColor.removeColorSelection(); }
				if(isForeSame) { foreColor.setColor(fC); } else { foreColor.removeColorSelection(); }
				if(isBackSame) { backColor.setColor(bC); } else { backColor.removeColorSelection(); }
			}
			boolean b = getWidth() > 650;
			fontLabel.setVisible(b);
			foreLabel.setVisible(b);
			backLabel.setVisible(b);
			fontColor.setVisible(b);
			foreColor.setVisible(b);
			backColor.setVisible(b);
			controlArea.setVisible(true);
		}
	}


	/** Set the editability status of the widgets of this bar.
	 * @param editable is editable */
	public final void setEditable(final boolean editable) { ; }


	/** Set the information text to be displayed and the related properties.
	 * @param text the text
	 * @param type 0: info; 1: error; 2: runtime exception */
	public final void setInfoText(final String text, final int type) {
		infoArea.setText(STTools.SPACE + STTools.SPACE + STTools.SPACE + text);
		infoArea.setToolTipText(text);
		switch(type) {
		case 0: // info
			infoArea.setBackground(infoBGColor); break;
		case 1: // error
			infoArea.setBackground(errBGColor); break;
		case 2: // runtime exception
			infoArea.setBackground(excBGColor); break;
		default:
			;
		}
	}


	/** Set the information text to be displayed as the pair node name - node value.
	 * @param name the node name
	 * @param value the node value */
	public final void setInfoText(final String name, final String value) {
		String s = name + STTools.COLON + STTools.SPACE + value;
		infoArea.setText(s);
		infoArea.setToolTipText(s);
		infoArea.setBackground(infoBGColor);
	}


	/** Highlight the node whose name is displayed in the status bar,
	 * either the one generating the first error, or the selected one. */
	private void highlightNode() {
		node = STGraph.getSTC().getCurrentGraph().getLastErrorNode();
		if(node != null) {
			node.select(true);
			return;
		}
		int n = 0;
		for(Object o : STGraph.getSTC().getCurrentGraph().getSelectionCells()) {
			if(STTools.isNode(o)) {
				node = (STNode)o;
				n++;
			}
		}
		if(n == 1) { node.select(true); }
	}


	private void dimInfoArea() {
		Dimension d = new Dimension(getWidth() - controlArea.getWidth() - 10, 20);
		infoArea.setMinimumSize(d);
		infoArea.setPreferredSize(d);
		infoArea.setMaximumSize(d);
		infoArea.revalidate();
		controlArea.revalidate();
	}


	public void componentResized(ComponentEvent e) { dimInfoArea(); }


	public void componentHidden(ComponentEvent e) { ; }


	public void componentMoved(ComponentEvent e) { ; }


	public void componentShown(ComponentEvent e) { ; }

	/** Handle the activation and deactivation of the statusbar items in a centralized way. */
	public final void updateItems() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		if(graph == null) { return; }
		boolean b = graph.isEditable;
		nodeName.setEnabled(b);
		checkOut.setEnabled(b);
		fontColor.setEnabled(b);
		foreColor.setEnabled(b);
		backColor.setEnabled(b);
	}

}
