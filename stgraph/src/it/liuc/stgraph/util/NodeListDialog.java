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
package it.liuc.stgraph.util;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;


/** Generate and control a window which displays the list of nodes of the current graph. */
@SuppressWarnings("rawtypes")
public final class NodeListDialog {
	/** The node dialog. */
	private static JFrame nodeDialog;
	/** The node list. */
	private static JList nodeList;
	/** The filter options. */
	private static JComboBox filterOptions;
	/** The filter text. */
	private static JTextField filterText;


	/** Class constructor. */
	private NodeListDialog() { ; }


	/** Open a window and display the list of nodes in it. */
	@SuppressWarnings("unchecked")
	public static void showDialog() {
		if(nodeDialog == null) {
			nodeDialog = new JFrame();
			nodeDialog.setAlwaysOnTop(true);
			nodeDialog.setResizable(true);
			nodeDialog.setLocation(STGraphC.getContainer().getX() + 100, STGraphC.getContainer().getY() + 100);
			nodeDialog.setSize(new Dimension(400, 300));
			nodeDialog.setTitle(STGraphC.getMessage("NODE.DIALOG.SHOWLISTTITLE")); //$NON-NLS-1$

			nodeDialog.addWindowFocusListener(new WindowAdapter() { //a tentative to maintain the dialog on top, but only of the main STGraph window... 
				public void windowLostFocus(WindowEvent e) {
					Window w = e.getOppositeWindow();
					if(w == null || !w.getName().equals("frame1")) { nodeDialog.setAlwaysOnTop(false); } //$NON-NLS-1$
					else { nodeDialog.setAlwaysOnTop(true); }
				}
			});

			JPanel nodePanel = new JPanel(new BorderLayout());
			nodeDialog.getContentPane().add(nodePanel);
			JLabel filterLabel = new JLabel(STGraphC.getMessage("NODE.DIALOG.FILTERLABEL") + STTools.COLON); //$NON-NLS-1$
			filterOptions = new JComboBox(new String[] {STGraphC.getMessage("NODE.DIALOG.FILTER1"), STGraphC.getMessage("NODE.DIALOG.FILTER2")}); //$NON-NLS-1$ //$NON-NLS-2$
			filterText = new JTextField();
			filterText.setPreferredSize(new Dimension(80, 21));
			JButton filterButton = new JButton(STGraphC.getMessage("NODE.DIALOG.FILTERBUTTON")); //$NON-NLS-1$
			filterButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { 
				setList(filterOptions.getSelectedIndex(), filterText.getText());
			} });

			JPanel filterPanel = new JPanel();
			filterPanel.add(filterLabel);
			filterPanel.add(filterOptions);
			filterPanel.add(filterText);
			filterPanel.add(filterButton);
			nodePanel.add(filterPanel, BorderLayout.NORTH);

			nodeList = new JList();
			nodeList.setCellRenderer(new NodeListRenderer());

			nodeList.addMouseListener(new MouseAdapter() { 
				public void mouseClicked(final MouseEvent e) {
					STNode n = (STNode)nodeList.getSelectedValue();
					if(n == null) { return; }
					n.select(true);
				}
			});

			nodePanel.add(new JScrollPane(nodeList), BorderLayout.CENTER);

			JButton closeButton = new JButton(STGraphC.getMessage("DIALOG.CLOSE")); //$NON-NLS-1$
			closeButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { 
				nodeDialog.setVisible(false);
			} });

			JPanel buttonPanel = new JPanel();
			buttonPanel.add(closeButton);
			nodePanel.add(buttonPanel, BorderLayout.SOUTH);
			nodeDialog.getRootPane().setDefaultButton(filterButton);
		}
		setList(filterOptions.getSelectedIndex(), filterText.getText());
		nodeDialog.setVisible(true);
	}


	@SuppressWarnings("unchecked")
	private static void setList(final int option, final String text) {
		Vector nodes = new Vector();
		if(STGraph.getSTC().getCurrentGraph() != null) {
			STNode[] o = STGraph.getSTC().getCurrentGraph().getNodes();
			if(o != null) {
				Arrays.sort(o, STGraph.getSTC().getNodeNameComparator());
				if(STTools.isEmpty(text)) {
					for(int i = 0; i < o.length; i++) { nodes.add(o[i]); }
				} else if(option == 0) {
					for(int i = 0; i < o.length; i++) {
						if(o[i].getName().indexOf(text) != -1) { nodes.add(o[i]); }
					}
				} else if(option == 1) {
					for(int i = 0; i < o.length; i++) {
						ValueNode n = (ValueNode)o[i];
						if(n.getStateInit().indexOf(text) != -1) {
							nodes.add(o[i]);
						} else if(n.getStateTransition().indexOf(text) != -1) {
							nodes.add(o[i]);
						} else if(n.getExpression().indexOf(text) != -1) {
							nodes.add(o[i]);
						}
					}
				}
			}
		}
		nodeList.setListData(nodes);
	}


	/** Return <code>true</code> if the node dialog is currently visible.
	 * @return is visible */
	public static boolean isDialogVisible() { return (nodeDialog != null) && nodeDialog.isVisible(); }


	/** Render the items of the node list. */
	@SuppressWarnings("serial")
	static class NodeListRenderer extends JTextField implements ListCellRenderer {


		/** The Constructor. */
		public NodeListRenderer() { setOpaque(true); }


		/** Gets the list cell renderer component.
		 * @param index the index
		 * @param list the list
		 * @param value the value
		 * @param cellHasFocus the cell has focus
		 * @param isSelected the is selected
		 * @return the list cell renderer component */
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
			STNode n = (STNode)value;
			String nn = n.getCProperty("Name"); //$NON-NLS-1$
			String text = n.getName();
			if(nn != null && !nn.equals(n.getName())) { text += STTools.SPACE + STTools.OPENV + nn + STTools.CLOSEV; }
			text += STTools.SPACE + STTools.OPENP;
			if(n.isInput()) {
				text += STGraphC.getMessage("NODE.TYPE2.INPUT"); //$NON-NLS-1$
			} else if(n.isAlgebraic()) {
				text += STGraphC.getMessage("NODE.TYPE2.OTHERALGEBRAIC"); //$NON-NLS-1$
			} else if(n.isState()) {
				text += STGraphC.getMessage("NODE.TYPE2.STATE"); //$NON-NLS-1$
			} else if(n.isModel()) {
				text += STGraphC.getMessage("NODE.TYPE2.MODEL"); //$NON-NLS-1$
			}
			text += STTools.CLOSEP;
			setText(text);
			setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10));
			setForeground(isSelected ? Color.WHITE : Color.BLACK);
			setBackground(isSelected ? Color.GRAY : Color.WHITE);
			return this;
		}
	}

}
