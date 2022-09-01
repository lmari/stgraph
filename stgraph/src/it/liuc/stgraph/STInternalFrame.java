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
package it.liuc.stgraph;

import it.liuc.stgraph.util.WidgetListDialog;
import it.liuc.stgraph.widget.STWidget;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;


/** Frame containing a widget. */
@SuppressWarnings("serial")
public class STInternalFrame extends JInternalFrame implements InternalFrameListener, ComponentListener {
	/** The node. */
	private STWidget widget;
	/** The graph. */
	private STGraphImpl graph;
	/** Is the creation movement? */
	private boolean isCreation;
	/** The title bar. */
	private JComponent titleBar;
	/** The title bar size. */
	public static final int TITLEBAR_SIZE = 15;

	/** Class constructor.
	 * @param desktop the desktop
	 * @param node the node
	 * @param graph the graph
	 * @param point the point */
	public STInternalFrame(final STWidget node, final JDesktopPane desktop, final STGraphImpl graph, final Point point) {
		super(null, true, true, false, true);
		setSize(new Dimension(150, 100));
		setLocation(point.x + 100, point.y);
		initMe(node, desktop, graph);
	}


	/** Class constructor.
	 * @param node the node
	 * @param desktop the desktop
	 * @param graph the graph
	 * @param bounds the bounds */
	public STInternalFrame(final STWidget node, final JDesktopPane desktop, final STGraphImpl graph, final Rectangle bounds) {
		super(null, true, true, false, true);
		setBounds(bounds);
		initMe(node, desktop, graph);
	}


	/** Common initialization.
	 * @param node the node
	 * @param desktop the desktop
	 * @param graph the graph */
	final void initMe(final STWidget node, final JDesktopPane desktop, final STGraphImpl graph) {
		this.widget = node;
		this.graph = graph;
		titleBar = ((BasicInternalFrameUI)this.getUI()).getNorthPane();
		Dimension d = new Dimension(titleBar.getPreferredSize().width , TITLEBAR_SIZE);
		titleBar.setMinimumSize(d);
		titleBar.setSize(d);
		titleBar.setPreferredSize(d);
		titleBar.setMaximumSize(d);
		setFrameIcon(null);
		desktop.add(this);
		setVisible(true);
		setFocusable(true);
		addInternalFrameListener(this);
		addComponentListener(this);
		isCreation = true;
	}


	/** Get the title bar.
	 * @return the title bar */
	public final JComponent getTitleBar() { return titleBar; }


	/** Internal frame activated.
	 * @param e the e */
	public final void internalFrameActivated(final InternalFrameEvent e) { graph.setSelectionCell(widget); }


	/** Internal frame deactivated.
	 * @param e the e */
	public final void internalFrameDeactivated(final InternalFrameEvent e) { ; }


	/** Internal frame closed.
	 * @param e the e */
	public final void internalFrameClosed(final InternalFrameEvent e) { ; }


	/** Internal frame closing.
	 * @param e the e */
	public final void internalFrameClosing(final InternalFrameEvent e) { widget.remove(); }


	/** Internal frame opened.
	 * @param e the e */
	public final void internalFrameOpened(final InternalFrameEvent e) { ; }


	/** Internal frame deiconified.
	 * @param e the e */
	public final void internalFrameDeiconified(final InternalFrameEvent e) {
		widget.iconized = false;
		setVisible(true);
		if(WidgetListDialog.isDialogVisible()) { WidgetListDialog.showDialog(); }
		STListPane.setWidgetList();
		if(graph.isTopGraph()) { graph.setAsModified(true); }
	}


	/** Internal frame iconified.
	 * @param e the e */
	public final void internalFrameIconified(final InternalFrameEvent e) {
		widget.iconized = true;
		setVisible(false);
		graph.clearSelection();
		if(WidgetListDialog.isDialogVisible()) { WidgetListDialog.showDialog(); }
		STListPane.setWidgetList();
		if(graph.isTopGraph()) { graph.setAsModified(true); }
	}


	/** Component moved.
	 * @param e the e */
	public final void componentMoved(final ComponentEvent e) {
		if(!isCreation) { graph.setAsModified(true); }
		isCreation = false;
		if(graph.isGridEnabled()) { alignToGrid(); }
	}


	/** Component resized.
	 * @param e the e */
	public final void componentResized(final ComponentEvent e) {
		if(!isCreation) { graph.setAsModified(true); }
		if(graph.isGridEnabled()) { alignToGrid(); }
	}


	/** Align this component to the grid. */
	private void alignToGrid() {
		double gs = graph.getGridSize();
		int igs = (int)gs;
		Point l = getLocation();
		Point nl = new Point(l);
		if((l.x / gs) != (int)(l.x / gs)) {
			int t = l.x % igs;
			nl.x = l.x + ((t <= gs / 2) ? -t : igs - t); 
		}
		if((l.y / gs) != (int)(l.y / gs)) {
			int t = l.y % igs;
			nl.y = l.y + ((t <= gs / 2) ? -t : igs - t); 
		}
		if(!nl.equals(l)) { setLocation(nl); }
		Dimension s = getSize();
		Dimension ns = new Dimension(s);
		if((s.width / gs) != (int)(s.width / gs)) {
			int t = s.width % igs;
			ns.width = s.width + ((t <= gs / 2) ? -t : igs - t); 
		}
		if((s.height / gs) != (int)(s.height / gs)) {
			int t = s.height % igs;
			ns.height = s.height + ((t <= gs / 2) ? -t : igs - t); 
		}
		if(!ns.equals(s)) { setSize(ns); }
	}


	/** Component hidden.
	 * @param e the e */
	public final void componentHidden(final ComponentEvent e) { ; }


	/** Component shown.
	 * @param e the e */
	public final void componentShown(final ComponentEvent e) { ; }

}
