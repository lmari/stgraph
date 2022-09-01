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
import it.liuc.stgraph.widget.STWidget;

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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;


/** Generate and control a window which displays the list of widgets of the current graph. */
public final class WidgetListDialog {
	/** The widget dialog. */
	private static JFrame widgetDialog;
	/** The widget list. */
	@SuppressWarnings("rawtypes")
	private static JList widgetList;


	/** Class constructor. */
	private WidgetListDialog() { ; }


	/** Open a window and display the list of widgets in it. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void showDialog() {
		if(widgetDialog == null) {
			widgetDialog = new JFrame();
			widgetDialog.setAlwaysOnTop(true);
			widgetDialog.setResizable(true);
			widgetDialog.setLocation(STGraphC.getContainer().getX() + 100, STGraphC.getContainer().getY() + 100);
			widgetDialog.setSize(new Dimension(500, 300));
			widgetDialog.setTitle(STGraphC.getMessage("WIDGET.DIALOG.SHOWLISTTITLE")); //$NON-NLS-1$

			widgetDialog.addWindowFocusListener(new WindowAdapter() { //a tentative to maintain the dialog on top, but only of the main STGraph window... 
				public void windowLostFocus(WindowEvent e) {
					Window w = e.getOppositeWindow();
					if(w == null || !w.getName().equals("frame1")) { widgetDialog.setAlwaysOnTop(false); } //$NON-NLS-1$
					else { widgetDialog.setAlwaysOnTop(true); }
				}
			});

			JPanel widgetPanel = new JPanel();
			widgetDialog.getContentPane().add(widgetPanel);
			widgetPanel.setLayout(new BorderLayout());
			widgetList = new JList();
			widgetList.setCellRenderer(new WidgetListRenderer());

			widgetList.addMouseListener(new MouseAdapter() { 
				public void mouseClicked(final MouseEvent e) {
					STWidget w = (STWidget)widgetList.getSelectedValue();
					if(w == null) { return; }
					if(w.iconized) { w.setIconized(false); }
				}
			});
			widgetPanel.add(new JScrollPane(widgetList), BorderLayout.CENTER);
			// close
			JButton closeButton = new JButton(STGraphC.getMessage("DIALOG.CLOSE")); //$NON-NLS-1$
			closeButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { 
				widgetDialog.setVisible(false);
			} });
			// minimize all
			JButton minAllButton = new JButton(STGraphC.getMessage("WIDGET.DIALOG.MINALL")); //$NON-NLS-1$
			minAllButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) {
				if(STGraph.getSTC().getCurrentGraph() == null) { return; }
				for(STWidget w : STGraph.getSTC().getCurrentGraph().getWidgets()) { w.setIconized(true); }
			} });
			// restore all
			JButton maxAllButton = new JButton(STGraphC.getMessage("WIDGET.DIALOG.MAXALL")); //$NON-NLS-1$
			maxAllButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) {
				if(STGraph.getSTC().getCurrentGraph() == null) { return; }
				for(STWidget w : STGraph.getSTC().getCurrentGraph().getWidgets()) { w.setIconized(false); }
			} });
			// reposition selected
			JButton posSelButton = new JButton(STGraphC.getMessage("WIDGET.DIALOG.POSSEL")); //$NON-NLS-1$
			posSelButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) {
				if(STGraph.getSTC().getCurrentGraph() == null) { return; }
				STWidget w = (STWidget)widgetList.getSelectedValue();
				if(w == null) { return; }
				w.frame.setLocation(STGraphC.getContainer().getX() + 50, STGraphC.getContainer().getY() + 100);
				showDialog();
				STGraph.getSTC().getCurrentGraph().refreshGraph();
			} });
			// delete selected
			JButton delSelButton = new JButton(STGraphC.getMessage("WIDGET.DIALOG.DELSEL")); //$NON-NLS-1$
			delSelButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { 
				if(STGraph.getSTC().getCurrentGraph() == null) { return; }
				STWidget w = (STWidget)widgetList.getSelectedValue();
				if(w == null) { return; }
				w.remove();
				showDialog();
				STGraph.getSTC().getCurrentGraph().refreshGraph();
			} });

			JPanel buttonPanel = new JPanel();
			buttonPanel.add(closeButton);
			buttonPanel.add(minAllButton);
			buttonPanel.add(maxAllButton);
			buttonPanel.add(posSelButton);
			buttonPanel.add(delSelButton);
			widgetPanel.add(buttonPanel, BorderLayout.SOUTH);
		}
		Vector vwidgets = new Vector();
		if(STGraph.getSTC().getCurrentGraph() != null) {
			for(STWidget widget : STGraph.getSTC().getCurrentGraph().getWidgets()) { vwidgets.add(widget); }
		}
		widgetList.setListData(vwidgets);
		widgetDialog.setVisible(true);
	}


	/** Return <code>true</code> if the widget dialog is currently visible.
	 * @return is visible */
	public static boolean isDialogVisible() { return (widgetDialog != null) && widgetDialog.isVisible(); }


	/** Render the items of the widget list. */
	@SuppressWarnings({ "serial", "rawtypes" })
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
				setFont(new Font(STGraph.getMyFont(), Font.ITALIC, 10));
				setForeground(Color.RED);
			} else {
				setText(text);
				setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10));
				setForeground(isSelected ? Color.WHITE : Color.BLACK);
			}
			setBackground(isSelected ? Color.GRAY : Color.WHITE);
			return this;
		}
	}

}
