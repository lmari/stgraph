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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.jgraph.JGraph;


/** Frame containing the graph widgets. */
@SuppressWarnings("serial")
public class STFrame extends JFrame implements WindowListener, ComponentListener {
	/** The node. */
	private STWidget node;
	/** The graph. */
	private JGraph graph;


	/** Class constructor.
	 * @param _node the _node
	 * @param _graph the _graph
	 * @param point the point */
	public STFrame(final STWidget _node, final JGraph _graph, final Point point) {
		super();
		setSize(new Dimension(150, 100));
		setLocation(point);
		initMe(_node, _graph);
	}


	/** Class constructor.
	 * @param _node the _node
	 * @param _graph the _graph
	 * @param bounds the bounds */
	public STFrame(final STWidget _node, final JGraph _graph, final Rectangle bounds) {
		super();
		setBounds(bounds);
		initMe(_node, _graph);
	}


	/** Common initialization.
	 * @param _node the _node
	 * @param _graph the _graph */
	final void initMe(final STWidget _node, final JGraph _graph) {
		node = _node;
		graph = _graph;
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		addWindowListener(this);
		addComponentListener(this);
	}


	/** Event handler.
	 * @param e the e */
	public final void windowActivated(final WindowEvent e) { graph.setSelectionCell(node); }


	/** Window deactivated.
	 * @param e the e */
	public final void windowDeactivated(final WindowEvent e) { ; }


	/** Window closed.
	 * @param e the e */
	public final void windowClosed(final WindowEvent e) { ; }


	/** Window closing.
	 * @param e the e */
	public final void windowClosing(final WindowEvent e) { node.remove(); }


	/** Window opened.
	 * @param e the e */
	public final void windowOpened(final WindowEvent e) { ; }


	/** Window deiconified.
	 * @param e the e */
	public final void windowDeiconified(final WindowEvent e) {
		node.iconized = false;
		if(WidgetListDialog.isDialogVisible()) { WidgetListDialog.showDialog(); }
		if(STGraph.getSTC().getCurrentGraph().isTopGraph() && node.stableIconization) { STGraph.getSTC().getCurrentGraph().setAsModified(true); }
	}


	/** Window iconified.
	 * @param e the e */
	public final void windowIconified(final WindowEvent e) {
		node.iconized = true;
		if(WidgetListDialog.isDialogVisible()) { WidgetListDialog.showDialog(); }
		graph.clearSelection();
		if(STGraph.getSTC().getCurrentGraph().isTopGraph() && node.stableIconization) { STGraph.getSTC().getCurrentGraph().setAsModified(true); }
	}


	/** component moved.
	 * @param e the e */
	public final void componentMoved(final ComponentEvent e) { STGraph.getSTC().getCurrentGraph().setAsModified(true); }


	/** Component resized.
	 * @param e the e */
	public final void componentResized(final ComponentEvent e) { STGraph.getSTC().getCurrentGraph().setAsModified(true); }


	/** Component hidden.
	 * @param e the e */
	public final void componentHidden(final ComponentEvent e) { ; }


	/** Component shown.
	 * @param e the e */
	public final void componentShown(final ComponentEvent e) { ; }

}
