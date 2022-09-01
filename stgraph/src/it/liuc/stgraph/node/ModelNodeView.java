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
package it.liuc.stgraph.node;

import it.liuc.stgraph.STDesktop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;


/** Model node view. */
@SuppressWarnings("serial")
public class ModelNodeView extends NodeView {


	/** Class constructor.
	 * @param node the node */
	public ModelNodeView(final STNode node) {
		super(node);
		setDefaultNodeBounds(ModelNode.PREFIX + ".BOUNDS"); //$NON-NLS-1$
		setRenderer(new SystemNodeRenderer());
	}


	/** Renderer class. */
	public class SystemNodeRenderer extends VertexRenderer {
		/** The patt. */
		private transient float[] patt = new float[] {5.0f, 3.0f};


		/** Paint.
		 * @param g the g */
		@Override
		public final void paint(final Graphics g) {
			ModelNode node = (ModelNode)getNode();
			Graphics2D g2 = (Graphics2D)g;
			Dimension d = getSize();
			int b = borderWidth;
			int bb = b - 1;
			int bw = d.width - b;
			int bh = d.height - b;
			boolean tmp = selected;
			Color cb = node.getBackColor();
			Color cf = node.getForeColor();
			STDesktop desk = null;
			if((desk = node.getDesk()) == null || desk.getGraph() == null) {
				if(!(cb.getRed() == 245 && cb.getGreen() == 245 && cb.getBlue() == 245)) {
					g.setColor(cb);
					g.fillRoundRect(bb, bb, bw, bh, 5, 5);
				}
				try {
					setBorder(null);
					setOpaque(false);
					selected = false;
					super.paint(g);
				} finally { selected = tmp; }
				g2.setStroke(node.isValid() ? new BasicStroke(b) : new BasicStroke(b, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, patt, 0.0f));
				if(cf != null && !(cf.getRed() == 245 && cf.getGreen() == 245 && cf.getBlue() == 245)) {
					g.setColor(cf);
					g.drawRoundRect(bb, bb, bw, bh, 5, 5);
					g.drawRoundRect(bb + 5, bb + 5, bw - 10, bh - 10, 5, 5);
				}
			} else if(desk.getGraph().isSequential) {
				if(!(cb.getRed() == 245 && cb.getGreen() == 245 && cb.getBlue() == 245)) {
					g.setColor(cb);
					g.fillRoundRect(bb, bb, bw, bh, 5, 5);
				}
				try {
					setBorder(null);
					setOpaque(false);
					selected = false;
					super.paint(g);
				} finally { selected = tmp; }
				g2.setStroke(node.isValid() ? new BasicStroke(b) : new BasicStroke(b, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, patt, 0.0f));
				if(cf != null && !(cf.getRed() == 245 && cf.getGreen() == 245 && cf.getBlue() == 245)) {
					g.setColor(cf);
					g.drawRoundRect(bb, bb, bw, bh, 5, 5);
					g.drawRoundRect(bb + 5, bb + 5, bw - 10, bh - 10, 5, 5);
				}
			} else { // i.e., the subsystem is a combinatorial one
				if(!(cb.getRed() == 245 && cb.getGreen() == 245 && cb.getBlue() == 245)) {
					g.setColor(cb);
					g.fillOval(bb, bb, bw, bh);
				}
				try {
					setBorder(null);
					setOpaque(false);
					selected = false;
					super.paint(g);
				} finally { selected = tmp; }
				g2.setStroke(node.isValid() ? new BasicStroke(b) : new BasicStroke(b, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, patt, 0.0f));
				if(cf != null && !(cf.getRed() == 245 && cf.getGreen() == 245 && cf.getBlue() == 245)) {
					g.setColor(cf);
					g.drawOval(bb, bb, bw, bh);
					g.drawOval(bb + 5, bb + 5, bw - 10, bh - 10);
				}
			}
			if(selected) {
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g.setColor(node.getGraph().getHighlightColor());
				g.drawRect(bb, bb, bw, bh);
			}
		}
	}

}
