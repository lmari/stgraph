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
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.widget.STWidget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;


/** Generate and control a list pane to let the user interact with nodes, groups, and widgets. */
@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class STListPane extends JPanel implements ComponentListener {
	private final static String HTMLOPEN = "<html><u><font color='blue'>"; //$NON-NLS-1$
	private final static String HTMLCLOSE = "</font></u>"; //$NON-NLS-1$
	private final static String P = "+ "; //$NON-NLS-1$
	private final static String M = "- "; //$NON-NLS-1$
	private static JList nodeList;
	private static JList groupList;
	private static JList widgetList;


	/** Class constructor. */
	public STListPane() {
		super();
		addComponentListener(this);
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		listPane.setBackground(Color.WHITE);

		final JLabel nodeLabel = new JLabel(HTMLOPEN + M + STGraphC.getMessage("NODES.TITLE") + HTMLCLOSE); //$NON-NLS-1$
		nodeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		nodeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		nodeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if(nodeList.isVisible()) {
					nodeList.setVisible(false);
					nodeLabel.setText(HTMLOPEN + P + STGraphC.getMessage("NODES.TITLE") + HTMLCLOSE); //$NON-NLS-1$
				} else {
					nodeList.setVisible(true);
					nodeLabel.setText(HTMLOPEN + M + STGraphC.getMessage("NODES.TITLE") + HTMLCLOSE); //$NON-NLS-1$
				}
			}
		});
		listPane.add(nodeLabel);
		nodeList = new JList();
		nodeList.setCellRenderer(new NodeListRenderer());
		nodeList.setAlignmentX(Component.LEFT_ALIGNMENT);
		nodeList.setCursor(new Cursor(Cursor.HAND_CURSOR));
		nodeList.addMouseListener(new MouseAdapter() { 
			@Override
			public void mouseClicked(final MouseEvent e) {
				STNode n = (STNode)nodeList.getSelectedValue();
				if(n == null) { return; }
				n.select(true);
			}
		});
		listPane.add(nodeList);

		final JLabel groupLabel = new JLabel(HTMLOPEN + M + STGraphC.getMessage("GROUPS.TITLE") + HTMLCLOSE); //$NON-NLS-1$
		groupLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		groupLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		groupLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if(groupList.isVisible()) {
					groupList.setVisible(false);
					groupLabel.setText(HTMLOPEN + P + STGraphC.getMessage("GROUPS.TITLE") + HTMLCLOSE); //$NON-NLS-1$
				} else {
					groupList.setVisible(true);
					groupLabel.setText(HTMLOPEN + M + STGraphC.getMessage("GROUPS.TITLE") + HTMLCLOSE); //$NON-NLS-1$
				}
			}
		});
		listPane.add(groupLabel);
		groupList = new JList();
		groupList.setCellRenderer(new GroupListRenderer());
		groupList.setAlignmentX(Component.LEFT_ALIGNMENT);
		groupList.setCursor(new Cursor(Cursor.HAND_CURSOR));
		groupList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				List<Object> o = groupList.getSelectedValuesList();
				if(o == null) { return; }
				STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
				graph.clearSelection();
				for(Object oo : o) { graph.addSelectionCells(graph.groupMap.get(oo)); }
			}
		});
		listPane.add(groupList);

		final JLabel widgetLabel = new JLabel(HTMLOPEN + M + STGraphC.getMessage("WIDGETS.TITLE") + HTMLCLOSE); //$NON-NLS-1$
		widgetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		widgetLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		widgetLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if(widgetList.isVisible()) {
					widgetList.setVisible(false);
					widgetLabel.setText(HTMLOPEN + P + STGraphC.getMessage("WIDGETS.TITLE") + HTMLCLOSE); //$NON-NLS-1$
				} else {
					widgetList.setVisible(true);
					widgetLabel.setText(HTMLOPEN + M + STGraphC.getMessage("WIDGETS.TITLE") + HTMLCLOSE); //$NON-NLS-1$
				}
			}
		});
		listPane.add(widgetLabel);
		widgetList = new JList();
		widgetList.setCellRenderer(new WidgetListRenderer());
		widgetList.setAlignmentX(Component.LEFT_ALIGNMENT);
		widgetList.setCursor(new Cursor(Cursor.HAND_CURSOR));
		widgetList.addMouseListener(new MouseAdapter() { 
			@Override
			public void mouseClicked(final MouseEvent e) {
				STWidget w = (STWidget)widgetList.getSelectedValue();
				if(w != null && w.iconized) { w.setIconized(false); }
				try { ((STInternalFrame)w.frame).setSelected(true); } catch (PropertyVetoException e1) { ; }
			}
		});
		listPane.add(widgetList);

		scrollPane.setViewportView(listPane);
	}


	public void componentResized(ComponentEvent e) { STGraph.getSTC().componentResized(null); }

	public void componentHidden(ComponentEvent e) { ; }

	public void componentMoved(ComponentEvent e) { ; }

	public void componentShown(ComponentEvent e) { ; }


	/**
	 * Update the lists to be visualized (wrapper).
	 */
	public static void setLists() {
		setNodeList();
		setGroupList();
		setWidgetList();
	}


	/** Update the node list to be visualized. */
	public static void setNodeList() {
		Vector nodes = new Vector();
		if(STGraph.getSTC().getCurrentGraph() != null) {
			STNode[] list = STGraph.getSTC().getCurrentGraph().getNodes();
			if(list != null) {
				Arrays.sort(list, STGraph.getSTC().getNodeNameComparator());
				for(STNode node : list) { nodes.add(node); }
			}
		}
		nodeList.setListData(nodes);
	}


	/**
	 * Update the group list to be visualized.
	 */
	public static void setGroupList() {
		Vector groups = new Vector();
		if(STGraph.getSTC().getCurrentGraph() != null) {
			HashMap map = STGraph.getSTC().getCurrentGraph().groupMap;
			if(map != null && map.size() > 0) {
				Iterator k = map.keySet().iterator();
				while(k.hasNext()) { groups.add(k.next()); }
				Collections.sort(groups);
			}
		}
		groupList.setListData(groups);
	}


	/** Update the widget list to be visualized. */
	public static void setWidgetList() {
		Vector widgets = new Vector();
		if(STGraph.getSTC().getCurrentGraph() != null) {
			for(STWidget widget : STGraph.getSTC().getCurrentGraph().getWidgets()) { widgets.add(widget); }
		}
		widgetList.setListData(widgets);
	}


	/** Render the items of the node list. */
	static class NodeListRenderer extends JTextField implements ListCellRenderer {

		/** The Constructor. */
		public NodeListRenderer() { setOpaque(true); }


		/** Gets the list cell renderer component.
		 * @param list the list
		 * @param value the value
		 * @param index the index
		 * @param isSelected the is selected
		 * @param cellHasFocus the cell has focus
		 * @return the list cell renderer component */
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
			STNode n = (STNode)value;
			String text = n.getName() + STTools.SPACE + STTools.OPENP;
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
			setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10)); //$NON-NLS-1$
			setBorder(null);
			setBackground(Color.WHITE);
			return this;
		}
	}


	/** Render the items of the group list. */
	static class GroupListRenderer extends JTextField implements ListCellRenderer {

		/** The Constructor. */
		public GroupListRenderer() { setOpaque(true); }


		/** Gets the list cell renderer component.
		 * @param list the list
		 * @param value the value
		 * @param index the index
		 * @param isSelected the is selected
		 * @param cellHasFocus the cell has focus
		 * @return the list cell renderer component */
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
			if(!STGraph.getSTC().getCurrentGraph().visibilityGroupMap.get(value).booleanValue()) {
				setText((String)value + " (hidden)"); //$NON-NLS-1$
				setFont(new Font(STGraph.getMyFont(), Font.ITALIC, 10)); //$NON-NLS-1$
				setForeground(Color.RED);
			} else {
				setText((String)value);
				setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10)); //$NON-NLS-1$
				setForeground(Color.BLACK);
			}
			setBorder(null);
			setBackground(Color.WHITE);
			return this;
		}
	}


	/** Render the items of the widget list. */
	static class WidgetListRenderer extends JTextField implements ListCellRenderer {

		/** The Constructor. */
		public WidgetListRenderer() { setOpaque(true); }

		/** Gets the list cell renderer component.
		 * @param index the index
		 * @param list the list
		 * @param value the value
		 * @param cellHasFocus the cell has focus
		 * @param isSelected the is selected
		 * @return the list cell renderer component */
		public final Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
			STWidget w = (STWidget)value;
			String text = w.getLabel() + ": " + w.getTitle(); //$NON-NLS-1$
			if(w.iconized) {
				setText(text + " (icon)"); //$NON-NLS-1$
				setFont(new Font(STGraph.getMyFont(), Font.ITALIC, 10)); //$NON-NLS-1$
				setForeground(Color.RED);
			} else {
				setText(text);
				setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10)); //$NON-NLS-1$
				setForeground(Color.BLACK);
			}
			setBorder(null);
			setBackground(Color.WHITE);
			return this;
		}
	}

}
