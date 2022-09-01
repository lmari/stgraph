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

import it.liuc.stgraph.action.EditModelProperties;
import it.liuc.stgraph.action.EditNodeProperties;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STTools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;


/** Marquee handler that connects vertices and displays popup menus. */
@SuppressWarnings("serial")
public class STMarqueeHandler extends BasicMarqueeHandler implements Serializable {
	/** The start point. */
	private transient Point2D start;
	/** The current point. */
	private transient Point2D current;
	/** The port. */
	private PortView port;
	/** The first port. */
	private PortView firstPort;
	/** The graph. */
	private STGraphExec graph;
	/** The selecting. */
	private boolean selecting = false;
	/** The port cursor. */
	private static Cursor PORTCURSOR = new Cursor(Cursor.HAND_CURSOR);
	/** The std cursor. */
	private static Cursor STDCURSOR = new Cursor(Cursor.DEFAULT_CURSOR);


	/** Class constructor.
	 * @param graph the graph */
	public STMarqueeHandler(final STGraphExec graph) { this.graph = graph; }


	/** Override to gain control (for popup menu and connect mode).
	 * @param e the event
	 * @return value */
	@Override public final boolean isForceMarqueeEvent(final MouseEvent e) {
		if(e.getClickCount() == 2) { return true; }
		if(SwingUtilities.isRightMouseButton(e)) { return true; }
		port = getSourcePortAt(e.getPoint());
		////if(port != null && graph.isPortsVisible()) { return true; }
		if(port != null && isSelecting()) { return true; }
		return super.isForceMarqueeEvent(e);
	}


	/** Display popup menu or remember start location and first port.
	 * @param e the event */
	@Override public final void mousePressed(final MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)) {
			graph.fromScreen((Point2D)e.getPoint().clone()); // scale from screen to model
			Object cell = graph.getFirstCellForLocation(e.getX(), e.getY()); // find cell in model coordinates
			JPopupMenu menu = new STPopupMenu(cell); // create popup menu for the cell
			menu.show(graph, e.getX(), e.getY()); // display popup menu
			////} else if(port != null && !e.isConsumed() && graph.isPortsVisible()) { // else if in connect mode and remembered port is valid
		} else if(port != null && !e.isConsumed() && isSelecting()) { // else if in connect mode and remembered port is valid
			start = graph.toScreen(this.port.getLocation()); // remember start location
			firstPort = port; // remember first port
			e.consume(); // consume event
		} else { super.mousePressed(e); }
	}


	/** Find port under mouse and repaint connector.
	 * @param e the event */
	@Override public final void mouseDragged(final MouseEvent e) {
		if(start != null && !e.isConsumed()) {
			Graphics g = graph.getGraphics();
			paintConnector(Color.BLACK, graph.getBackground(), g);
			port = getTargetPortAt(e.getPoint());
			if(port != null) {
				current = graph.toScreen(port.getLocation());
			} else {
				current = graph.snap(e.getPoint());
			}
			paintConnector(graph.getBackground(), Color.BLACK, g);
			e.consume();
		}
		//try {
		super.mouseDragged(e);
		//} catch (Exception e2) { ; }
	}


	/** Open an edit dialog, or connect the first port and the current port in the graph, or repaint.
	 * @param e the event */
	@Override public final void mouseReleased(final MouseEvent e) {
		if(e == null) { return; }
		if(e.getClickCount() == 2) {
			if(graph.getSelectionCount() == 0) {
				((EditModelProperties)STGraphC.getAction("EditModelProperties")).exec(); //$NON-NLS-1$
			} else {
				((EditNodeProperties)STGraphC.getAction("EditNodeProperties")).exec(); //$NON-NLS-1$
			}
		} else if(!e.isConsumed() && port != null && firstPort != null && firstPort != port) {
			STFactory.edgeCreate(STTools.BLANK, (Port)firstPort.getCell(), (Port)port.getCell());
			e.consume();
		} else {
			graph.repaint();
		}
		firstPort = port = null;
		start = current = null;
		graph.setCursor(STDCURSOR);
		super.mouseReleased(e);
	}


	/** Display ports if over nodes and show special cursor if over port.
	 * @param e the event */
	@Override public final void mouseMoved(final MouseEvent e) {
		if(e != null && !e.isConsumed()) {
			if(getNodeAt(e.getPoint()) != null) {
				////graph.setPortsVisible(graph.isEditable);
				setSelecting(graph.isEditable);
			} else {
				////graph.setPortsVisible(false);
				setSelecting(false);
			}
			if(getSourcePortAt(e.getPoint()) != null) { // && graph.isPortsVisible()) {
				graph.setCursor(PORTCURSOR);
				e.consume();
			}
			//super.mouseReleased(e);
			super.mouseMoved(e);
		}
	}


	/** Use xor-mode on graphics to paint connector.
	 * @param fg the fg
	 * @param bg the bg
	 * @param g the graphics */
	protected final void paintConnector(final Color fg, final Color bg, final Graphics g) {
		g.setColor(fg);
		g.setXORMode(bg);
		paintPort(this.graph.getGraphics());
		if(firstPort != null && start != null && current != null) {
			g.drawLine((int) start.getX(), (int) start.getY(), (int) current.getX(), (int) current.getY());
		}
	}


	/** Use the preview flag to draw a highlighted port.
	 * @param g the graphics */
	protected final void paintPort(final Graphics g) {
		if(port != null) {
			boolean o = (GraphConstants.getOffset(this.port.getAttributes()) != null);
			Rectangle2D r = (o) ? port.getBounds() : port.getParentView().getBounds();
			r = graph.toScreen((Rectangle2D)r.clone());
			r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6);
			graph.getUI().paintCell(g, port, r, true);
		}
	}


	/** Get the port selected by mouse click.
	 * @param point the point
	 * @return port */
	public final PortView getSourcePortAt(final Point2D point) { return graph.getPortViewAt(point.getX(), point.getY()); }


	/** Find a cell at point and return its first port as a port view.
	 * @param point the point
	 * @return port */
	protected final PortView getTargetPortAt(final Point2D point) {
		Object cell = graph.getFirstCellForLocation(point.getX(), point.getY());
		for(int i = 0; i < graph.getModel().getChildCount(cell); i++) {
			Object tmp = graph.getModel().getChild(cell, i); // get child from model
			tmp = graph.getGraphLayoutCache().getMapping(tmp, false); // get view for child using the graph's view as a cell mapper
			if(tmp instanceof PortView && tmp != firstPort) { return (PortView)tmp; } // if child view is a port view and not equal to first port return as port view
		}
		return getSourcePortAt(point); // no port view found
	}


	/** Get the node selected by mouse click.
	 * @param point the point
	 * @return  node */
	protected final STNode getNodeAt(final Point2D point) {
		Object o = graph.getFirstCellForLocation(point.getX(), point.getY());
		if(o instanceof STNode) { return (STNode)o; }
		return null;
	}


	/** Helper.
	 * @param selecting set selecting */
	private void setSelecting(final boolean selecting) {
		this.selecting = selecting;
		//graph.setPortsVisible(selecting);
	}


	/** Get whether it is selecting
	 * @return result */
	private boolean isSelecting() { return selecting; }

}
