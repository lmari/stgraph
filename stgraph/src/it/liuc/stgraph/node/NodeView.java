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

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.util.STColorChooser;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;


/** Node view. */
@SuppressWarnings({"serial", "rawtypes"})
public class NodeView extends VertexView {
	/** The node. */
	private STNode node;
	/** The default node bounds. */
	private static Rectangle defaultNodeBounds;
	/** The node bounds. */
	private Rectangle nodeBounds = null;
	/** The renderer. */
	private VertexRenderer myRenderer;
	/** The text font. */
	private static Font textFont = new Font(STConfigurator.getProperty("TEXT.FONTNAME"), Font.PLAIN, Integer.parseInt(STConfigurator.getProperty("TEXT.FONTSIZE"))); //$NON-NLS-1$ //$NON-NLS-2$
	/** The default line width. */
	private static float defaultLineWidth = Float.parseFloat(STConfigurator.getProperty("NODE.BORDERWIDTH")); //$NON-NLS-1$
	/** The map. */
	protected static Hashtable map = new Hashtable();
	/** The attributes. */
	protected static Hashtable attributes = new Hashtable();


	/** Class constructor.
	 * @param node the node */
	public NodeView(final STNode node) {
		super(node);
		this.node = node;
	}


	/** Get the node.
	 * @return the node */
	public final STNode getNode() { return node; }


	/** Set renderer.
	 * @param myRenderer the renderer */
	public final void setRenderer(final VertexRenderer myRenderer) { this.myRenderer = myRenderer; }


	/** Get renderer.
	 * @return the renderer */
	public final VertexRenderer getRenderer() { return myRenderer; }


	/** Set default node bounds.
	 * @param defaultNodeBounds the default node bounds */
	public static void setDefaultNodeBounds(final Rectangle defaultNodeBounds) { NodeView.defaultNodeBounds = defaultNodeBounds; }


	/** Set default node bounds.
	 * @param id the id */
	public static void setDefaultNodeBounds(final String id) {
		String[] bounds = STConfigurator.getProperty(id).split(","); //$NON-NLS-1$
		setDefaultNodeBounds(new Rectangle(Integer.parseInt(bounds[0]), Integer.parseInt(bounds[1]), Integer.parseInt(bounds[2]), Integer.parseInt(bounds[3])));
	}


	/** Get default node bounds.
	 * @return the default node bounds */
	public static Rectangle getDefaultNodeBounds() { return defaultNodeBounds; }


	/** Set node bounds.
	 * @param nodeBounds the node bounds */
	public final void setNodeBounds(final Rectangle nodeBounds) { node.setProperty("bounds", this.nodeBounds = nodeBounds); } //$NON-NLS-1$


	/** Get node bounds.
	 * @return the node bounds */
	public final Rectangle getNodeBounds() {
		if(nodeBounds == null) { return defaultNodeBounds; }
		return nodeBounds;
	}


	/** Get the font for texts.
	 * @return font */
	public static Font getTextFont() { return textFont; } 


	/** Get the line width for arrows.
	 * @return width */
	public static float getDefaultLineWidth() { return defaultLineWidth; }


	/** Set the initial properties for this node.
	 * @param node the node
	 * @return the property hashtable */
	@SuppressWarnings("unchecked")
	public static Hashtable setInitialProps(final STNode node) {
		map.clear();
		Font f = new Font(STConfigurator.getProperty("NODE.FONTNAME"), Font.PLAIN, Integer.parseInt(STConfigurator.getProperty("NODE.FONTSIZE"))); //$NON-NLS-1$ //$NON-NLS-2$
		GraphConstants.setFont(map, f); // add a font attribute to the map
		node.setPlainFont(f);
		node.setBoldFont(new Font(STConfigurator.getProperty("NODE.FONTNAME"), Font.BOLD, Integer.parseInt(STConfigurator.getProperty("NODE.FONTSIZE")))); //$NON-NLS-1$ //$NON-NLS-2$
		Color c = STColorChooser.myColorMap.get(STConfigurator.getProperty("NODE.FONTCOL")); //$NON-NLS-1$
		GraphConstants.setForeground(map, c); // add a font color attribute to the map
		node.setFontColor(c);
		GraphConstants.setBounds(map, node.getView().getNodeBounds()); // add a bounds attribute to the map
		c = STColorChooser.myColorMap.get(STConfigurator.getProperty("NODE.FORECOL")); //$NON-NLS-1$
		GraphConstants.setBorderColor(map, c); // add a border color attribute to the map
		node.setForeColor(c);
		GraphConstants.setLineWidth(map, getDefaultLineWidth());
		c = STColorChooser.myColorMap.get(STConfigurator.getProperty("NODE.BACKCOL")); //$NON-NLS-1$
		GraphConstants.setBackground(map, c); // add a colored background
		node.setBackColor(c);
		node.setNumberFormat(STConfigurator.getProperty("NODE.FORMAT")); //$NON-NLS-1$
		GraphConstants.setOpaque(map, true); // make vertex opaque
		GraphConstants.setEditable(map, false); // disable the direct editing, and thus the renaming by the F2 editor
		attributes.clear();
		attributes.put(node, map); // associate the vertex with its attributes
		return attributes;
	}


	/** Set the initial properties for this node.
	 * @param node the node
	 * @param icon the icon file
	 * @return the property hashtable */
	@SuppressWarnings("unchecked")
	public static Hashtable setInitialProps(final STNode node, final URL icon) {
		map.clear();
		GraphConstants.setFont(map, node.getPlainFont()); // add a font attribute to the map
		GraphConstants.setBounds(map, node.getView().getNodeBounds()); // add a bounds attribute to the map
		GraphConstants.setIcon(map, new ImageIcon(icon));
		GraphConstants.setVerticalTextPosition(map, SwingConstants.BOTTOM);
		GraphConstants.setVerticalAlignment(map, SwingConstants.BOTTOM);
		GraphConstants.setOpaque(map, true); // make vertex opaque
		GraphConstants.setEditable(map, false); // disable the direct editing, and thus the renaming by the F2 editor
		attributes.clear();
		attributes.put(node, map); // associate the vertex with its attributes
		return attributes;
	}

}
