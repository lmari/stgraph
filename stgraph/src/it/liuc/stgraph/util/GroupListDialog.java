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

import it.liuc.stgraph.STListPane;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphImpl;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.jgraph.graph.DefaultGraphCell;


/** Generate and control a window which displays and let the user interact with
 * the list of node groups of the current graph. */
public final class GroupListDialog {
	/** The group dialog. */
	private static JFrame groupDialog;
	/** The group list. */
	@SuppressWarnings("rawtypes")
	private static JList groupList;
	/** The (temporarily) selected indices. */
	private static int[] selectedIndices;


	/** Class constructor. */
	private GroupListDialog() { ; }


	/** Open a window and display the list of groups in it. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void showDialog() {
		if(groupDialog == null) {
			groupDialog = new JFrame();
			groupDialog.setAlwaysOnTop(true);
			groupDialog.setResizable(true);
			groupDialog.setLocation(STGraphC.getContainer().getX() + 100, STGraphC.getContainer().getY() + 100);
			groupDialog.setSize(new Dimension(500, 300));
			groupDialog.setTitle(STGraphC.getMessage("GROUP.DIALOG.SHOWLISTTITLE")); //$NON-NLS-1$

			groupDialog.addWindowFocusListener(new WindowAdapter() { //a tentative to maintain the dialog on top, but only of the main STGraph window... 
				public void windowLostFocus(WindowEvent e) {
					Window w = e.getOppositeWindow();
					if(w == null || !w.getName().equals("frame1")) { groupDialog.setAlwaysOnTop(false); } //$NON-NLS-1$
					else { groupDialog.setAlwaysOnTop(true); }
				}
			});

			JPanel nodePanel = new JPanel();
			groupDialog.getContentPane().add(nodePanel);
			nodePanel.setLayout(new BorderLayout());
			groupList = new JList();
			groupList.setCellRenderer(new GroupListRenderer());

			groupList.addMouseListener(new MouseAdapter() { 
				public void mouseClicked(final MouseEvent e) {
					List<Object> o = groupList.getSelectedValuesList();
					if(o == null) {
						selectedIndices = null;
						return;
					}
					STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
					graph.clearSelection();
					for(Object oo : o) {
						graph.addSelectionCells(graph.groupMap.get(oo));
					}
					selectedIndices = groupList.getSelectedIndices();
				}
			});

			nodePanel.add(new JScrollPane(groupList), BorderLayout.CENTER);

			JButton closeButton = new JButton(STGraphC.getMessage("DIALOG.CLOSE")); //$NON-NLS-1$
			closeButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { 
				groupDialog.setVisible(false);
			} });

			JButton createButton = new JButton(STGraphC.getMessage("GROUP.DIALOG.CREATE")); //$NON-NLS-1$
			createButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) {
				STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
				if(graph == null) { return; }
				Object[] nodes = graph.getSelectionCells();
				if(nodes.length == 0) { return; }
				ArrayList<DefaultGraphCell> nnodes = new ArrayList<DefaultGraphCell>();
				for(Object node : nodes) {
					if(STTools.isNode(node) || STTools.isText(node)) { nnodes.add((DefaultGraphCell)node); }
				}
				if(nnodes.size() == 0) { return; }
				String name = JOptionPane.showInputDialog(STGraph.getSTC(), STGraphC.getMessage("GROUP.DIALOG.GROUPNAMEMSG"), STGraphC.getMessage("GROUP.DIALOG.GROUPNAMETITLE"), JOptionPane.PLAIN_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
				if(STTools.isEmpty(name)) { return; }
				graph.groupMap.put(name, nnodes.toArray(new DefaultGraphCell[0]));
				graph.visibilityGroupMap.put(name, Boolean.valueOf(true)); // default visibility
				graph.setAsModified(true);
				STListPane.setGroupList();
				showDialog();
			} });

			JButton removeButton = new JButton(STGraphC.getMessage("GROUP.DIALOG.REMSEL")); //$NON-NLS-1$
			removeButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { 
				STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
				if(graph == null) { return; }
				List<Object> o = groupList.getSelectedValuesList();
				if(o == null) { return; }
				for(Object oo : o) {
					graph.getGraphLayoutCache().setVisible(graph.groupMap.get(oo), true); // make the group nodes visible before removing the group...
					graph.groupMap.remove(oo);
					graph.visibilityGroupMap.remove(oo);
				}
				graph.setAsModified(true);
				STListPane.setGroupList();
				showDialog();
			} });

			JButton showButton = new JButton(STGraphC.getMessage("GROUP.DIALOG.SHOW")); //$NON-NLS-1$
			showButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { 
				if(STGraph.getSTC().getCurrentGraph() == null) { return; }
				List<Object> o = groupList.getSelectedValuesList();
				if(o == null) { return; }
				STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
				for(Object oo : o) {
					graph.getGraphLayoutCache().setVisible(graph.groupMap.get(oo), true);
					graph.visibilityGroupMap.put(oo.toString(), Boolean.valueOf(true));
				}
				showDialog();
				if(selectedIndices != null) { groupList.setSelectedIndices(selectedIndices); }
			} });

			JButton hideButton = new JButton(STGraphC.getMessage("GROUP.DIALOG.HIDE")); //$NON-NLS-1$
			hideButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { 
				if(STGraph.getSTC().getCurrentGraph() == null) { return; }
				List<Object> o = groupList.getSelectedValuesList();
				if(o == null) { return; }
				STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
				for(Object oo : o) {
					graph.getGraphLayoutCache().setVisible(graph.groupMap.get(oo), false);
					graph.visibilityGroupMap.put(oo.toString(), Boolean.valueOf(false));
				}
				showDialog();
				if(selectedIndices != null) { groupList.setSelectedIndices(selectedIndices); }
			} });

			JPanel buttonPanel = new JPanel();
			buttonPanel.add(closeButton);
			buttonPanel.add(createButton);
			buttonPanel.add(removeButton);
			buttonPanel.add(showButton);
			buttonPanel.add(hideButton);
			nodePanel.add(buttonPanel, BorderLayout.SOUTH);
		}
		Vector keys = new Vector();
		if(STGraph.getSTC().getCurrentGraph() != null) {
			HashMap map = STGraph.getSTC().getCurrentGraph().groupMap;
			if(map != null && map.size() > 0) {
				Iterator k = map.keySet().iterator();
				while(k.hasNext()) { keys.add(k.next()); }
				Collections.sort(keys);
			}
		}
		groupList.setListData(keys);
		groupDialog.setVisible(true);
	}


	/** Return <code>true</code> if the dialog is currently visible.
	 * @return is visible */
	public static boolean isDialogVisible() { return (groupDialog != null) && groupDialog.isVisible(); }


	/** Render the items of the group list. */
	@SuppressWarnings({ "serial", "rawtypes" })
	static class GroupListRenderer extends JTextField implements ListCellRenderer {


		/** The Constructor. */
		public GroupListRenderer() { setOpaque(true); }


		/** Gets the list cell renderer component.
		 * @param index the index
		 * @param list the list
		 * @param value the value
		 * @param cellHasFocus the cell has focus
		 * @param isSelected the is selected
		 * @return the list cell renderer component */
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
			if(!STGraph.getSTC().getCurrentGraph().visibilityGroupMap.get(value).booleanValue()) {
				setText((String)value + " (hidden)"); //$NON-NLS-1$
				setFont(new Font(STGraph.getMyFont(), Font.ITALIC, 10));
				setForeground(Color.RED);
			} else {
				setText((String)value);
				setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10));
				setForeground(isSelected ? Color.WHITE : Color.BLACK);
			}
			setBackground(isSelected ? Color.GRAY : Color.WHITE);
			return this;
		}
	}

}
