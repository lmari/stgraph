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

import it.liuc.stgraph.STGraphExec;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;


/** Value node view. */
@SuppressWarnings("serial")
public class ValueNodeView extends NodeView {


	/** Class constructor.
	 * @param node the node */
	public ValueNodeView(final STNode node) {
		super(node);
		setDefaultNodeBounds(ValueNode.PREFIX + ".BOUNDS"); //$NON-NLS-1$
		setRenderer(new ValueNodeRenderer());
	}


	/** Renderer class. */
	public class ValueNodeRenderer extends VertexRenderer {
		/** The patt. */
		private transient float[] patt = new float[] {5.0f, 3.0f};


		/** Paint.
		 * @param g the g */
		@Override
		public final void paint(final Graphics g) {
			ValueNode node = (ValueNode)getNode();
			int x = node.getValueNodeType();
			// to properly draw the node name
			node.setProperty(GraphConstants.FONT, node.isNotScalar() ? node.getBoldFont() : node.getPlainFont());
			Graphics2D g2 = (Graphics2D)g;
			Dimension d = getSize();
			int b = borderWidth;
			int bb = b - 1;
			int bw = d.width - b;
			int bh = d.height - b;
			int y = 20;
			boolean tmp = selected;
			Color cb = node.getBackColor();
			Color cf = node.getForeColor();
			if(cb != null && !(cb.getRed() == 245 && cb.getGreen() == 245 && cb.getBlue() == 245)) {
				g.setColor(cb);
				// set the background shape
				if(x == ValueNode.VALUENODE_ALGEBRAIC) {
					if(node.isConstant() && !node.isBoundToWidget()) {
						g.fillPolygon(new int[] {bb, bb + bw / 2, bb + bw, bb + bw / 2}, new int[] {bb + bh / 2, bb, bb + bh / 2, bb + bh}, 4);
					} else {
						g.fillOval(bb, bb, bw, bh);
					}
				} else {
					g.fillRoundRect(bb, bb, bw, bh, y, y);
				}
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
				// set the shape
				if(x == ValueNode.VALUENODE_ALGEBRAIC) {
					if(node.isConstant() && !node.isBoundToWidget()) {
						g.drawPolygon(new int[] {bb, bb + bw / 2, bb + bw, bb + bw / 2}, new int[] {bb + bh / 2, bb, bb + bh / 2, bb + bh}, 4);
						if(node.isOutput()) {
							int w8 = (int)(0.8 * bw);
							g.fillPolygon(new int[] { bb + w8, bw, bb + w8 }, new int[] { (int)(bb + bh / 3.3), (bb + bh) / 2, (int)(bb + bh - bh / 3.3) }, 3);
						}
					} else {
						g.drawOval(bb, bb, bw, bh);
						if(node.isInput()) {
							int w1 = (int) (0.1 * bw);
							g.setColor(Color.WHITE);
							g.fillRect(bb - 2, bb, w1 + 2, bh);
							g.setColor(cf);
							g.fillPolygon(new int[] { bb + w1, bb + 2 * w1, bb + w1 }, new int[] { (int)(bb + bh / 5.5), (bb + bh) / 2, (int)(bb + bh - bh / 5.5) }, 3);
							if(node.isBoundToWidget()) {
								int x1 = bb + w1;
								int x2 = (int)(bb + 1.5 * w1);
								int y1 = (int)(bb + bh / 5.5);
								int y2 = (int)(bb + bh - bh / 5.5);
								g.drawPolygon(new int[] { x1, x2, (int)(bb + 2.5 * w1), x2, x1 }, new int[] { y1, y1, (bb + bh) / 2, y2, y2 }, 5);
							}
						}
						if(node.isOutput()) {
							int w9 = (int)(0.9 * bw);
							g.setColor(Color.WHITE);
							g.fillRect(bb + w9, bb + bh / 9 - 1, (int) (0.25 * bw), (int) (0.8 * bh) + 2);
							g.setColor(cf);
							g.fillPolygon(new int[] { bb + w9, bw, bb + w9 }, new int[] { (int)(bb + bh / 5.5), (bb + bh) / 2, (int)(bb + bh - bh / 5.5) }, 3);
						}
					}
				} else {
					g.drawRoundRect(bb, bb, bw, bh, y, y);
					if(node.isOutput()) {
						int ww8 = (int)(0.8 * bw);
						g.setColor(Color.WHITE);
						g.fillRect(bb + ww8, bb - 2, (int)(0.25 * bw), bh + 4);
						g.setColor(cf);
						g.fillPolygon(new int[] {bb + ww8, bb + ww8, bw}, new int[] {bb, bb + bh, (bb + bh) / 2}, 3);
					}
				}
			}
			STGraphExec graph = node.getGraph();
			if(graph.isShowNodeValues() && graph.isStepped()) {
				g.setColor(node.getFontColor());
				g.drawString(STData.toString(node.getValue(), STData.FORMAT_SHORT, node.getNumberFormat()), (int)(0.4 * bw - 10), (int)(0.5 * bh + 8 + node.getFontSize()));
			} else {
				if(node.isGlobal()) {
					g.setColor(node.getFontColor());
					g.drawString("(G)", (int)(0.4 * bw - 10), (int)(0.5 * bh + 8 + node.getFontSize())); //$NON-NLS-1$
				}
			}
			if(selected) {
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g.setColor(node.getGraph().getHighlightColor());
				if(x == ValueNode.VALUENODE_ALGEBRAIC) {
					g.drawOval(bb, bb, bw, bh);
				} else {
					g.drawRect(bb, bb, bw, bh);
				}
			}
		}
	}

}
