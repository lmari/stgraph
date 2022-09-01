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

import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.node.NodeView;
import it.liuc.stgraph.node.STEdge;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.TextNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.widget.STWidget;
import it.liuc.stgraph.widget.WidgetView;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.SwingConstants;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;


/** Factory for the creation of nodes (and subnodes), edges, and widgets. */
public final class STFactory {


	/** Class constructor. */
	private STFactory() { ; }


	/** Create a new node with the specified parameters and the default properties.
	 * @param name the name
	 * @param type the type
	 * @return node */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static STNode nodeCreate(final String name, final String type) {
		STGraphC stc = STGraph.getSTC();
		STGraphExec graph = stc.getCurrentGraph();
		try {
			stc.getLoggerUI().info("creating a new value node..."); //$NON-NLS-1$
			STNode node = (STNode)Class.forName("it.liuc.stgraph.node." + type).newInstance(); //$NON-NLS-1$
			String nodeName = name;
			if(name == null || (node.checkName(name) != null)) { nodeName = STGraphC.getMessage("NODE.DEFAULT_PREFIX") + ++graph.globalNodeCounter; } //$NON-NLS-1$
			node.setName(nodeName);
			node.setNodeType(type);
			if(type.equals(STNode.NODE_VALUE) && ((ValueNode)node).getValueNodeType() == ValueNode.VALUENODE_ALGEBRAIC) { node.setInput(true); } // default setting
			node.add(new DefaultPort()); // create a map that holds the attributes for the vertex
			graph.getModel().insert(new Object[] { node }, NodeView.setInitialProps(node), null, null, null);
			graph.getGraphLayoutCache().setVisible(node, true);
			STTools.moveNodes(1, 0, false); STTools.moveNodes(-1, 0, false); // a dirty trick, just to notify the existence of the node
			graph.getInterpreter().addVariable(nodeName, 0.0); // preset all the variables
			if(type.equals(STNode.NODE_VALUE)) { graph.getInterpreter().addVariable("__" + name, new Vector()); } //$NON-NLS-1$ //TODO [batch exec]
			return node;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/** Create a new node of a given subtype from the user library with the specified parameters and the default properties.
	 * @param name the name
	 * @param type the type
	 * @param subtype the subtype
	 * @param loadModel load the model?
	 * @param showModel show the model?
	 * @return node */
	public static STNode nodeCreate4(final String name, final String type, final String subtype, final boolean loadModel, final boolean showModel) {
		STGraphC stc = STGraph.getSTC();
		STGraphExec graph = stc.getCurrentGraph();
		try {
			stc.getLoggerUI().info("creating a new model node..."); //$NON-NLS-1$
			STNode node = new it.liuc.stgraph.node.ModelNode();
			String nodeName = name;
			if(name == null || (node.checkName(name) != null)) { nodeName = STGraphC.getMessage("NODE.DEFAULT_PREFIX") + ++graph.globalNodeCounter; } //$NON-NLS-1$
			node.setName(nodeName);
			node.setNodeType(type);
			node.setNodeSubtype(subtype);
			node.add(new DefaultPort());
			Color c = new Color(245, 245, 245);
			node.setForeColor(c);
			node.setProperty(GraphConstants.BORDERCOLOR, c);
			node.setBackColor(c);
			node.setProperty(GraphConstants.BACKGROUND, c);

			String icon = subtype + ".png"; //$NON-NLS-1$
			String iconPath = ModelNode.MODELPATH + STTools.SLASH + icon;
			URL iconURL;
			if((new File(iconPath)).exists()) {
				iconURL = (new File(iconPath)).toURI().toURL();
			} else {
				icon = "customDefault.png"; //$NON-NLS-1$
				iconURL = stc.getBaseIcon(icon);
			}
			node.setDefaultIconFile(icon);

			graph.getModel().insert(new Object[] { node }, NodeView.setInitialProps(node, iconURL), null, null, null);
			graph.getGraphLayoutCache().setVisible(node, true);
			STTools.moveNodes(1, 0, false); STTools.moveNodes(-1, 0, false); // a dirty trick, just to notify the existence of the node
			STInterpreter interpreter = graph.getInterpreter();
			interpreter.addVariable(nodeName, 0.0); // preset all the variables
			if(loadModel) {
				((ModelNode)node).setModel(subtype + STConfigurator.getProperty("FILEEXT.STANDARD"), showModel); //$NON-NLS-1$
				STNode[] oNodes = ((ModelNode)node).getSubmodel().getOutputNodeList();
				if(oNodes != null) {
					for(STNode oNode : oNodes) { interpreter.addVariable(nodeName + STTools.DOT + oNode.getName(), 0.0); }
				}
				String desc = ((ModelNode)node).getSubmodel().getModelDescription();
				if(STTools.isEmpty(node.getDescription()) && desc != null) { node.setDescription(desc); }
			}
			return node;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/** Create a new text with the specified parameters and the default properties.
	 * @param bounds the bounds
	 * @param content the content
	 * @return the text node */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static TextNode textCreate(final String content, final Rectangle bounds) {
		STGraphC stc = STGraph.getSTC();
		STGraphExec graph = stc.getCurrentGraph();
		stc.getLoggerUI().info("creating a new text..."); //$NON-NLS-1$
		TextNode node = new TextNode();
		node.setContent(content);
		Map map = new Hashtable();
		GraphConstants.setBounds(map, bounds); // add a bounds attribute to the map
		GraphConstants.setHorizontalAlignment(map, SwingConstants.LEFT);
		GraphConstants.setFont(map, NodeView.getTextFont());
		GraphConstants.setHorizontalAlignment(map, SwingConstants.LEFT);
		GraphConstants.setVerticalAlignment(map, SwingConstants.TOP);
		GraphConstants.setBorderColor(map, Color.white);
		GraphConstants.setBackground(map, Color.white);
		GraphConstants.setOpaque(map, true);
		GraphConstants.setEditable(map, false); // disable the direct editing
		Hashtable attributes = new Hashtable();
		attributes.put(node, map);
		graph.getModel().insert(new Object[] { node }, attributes, null, null, null);
		graph.getGraphLayoutCache().setVisible(node, true);
		return node;
	}


	/** Create a new edge between source and target ports.
	 * @param label the label
	 * @param source the source
	 * @param target the target
	 * @return the edge */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static STEdge edgeCreate(final String label, final Port source, final Port target) {
		STGraphC stc = STGraph.getSTC();
		STGraphExec graph = stc.getCurrentGraph();
		stc.getLoggerUI().info("creating a new edge..."); //$NON-NLS-1$
		ConnectionSet cs = new ConnectionSet();
		STEdge edge = new STEdge();
		cs.connect(edge, source, target);
		Map map = new Hashtable();
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_CLASSIC);
		GraphConstants.setLineStyle(map, GraphConstants.STYLE_SPLINE);
		GraphConstants.setLineColor(map, Color.BLUE);
		GraphConstants.setLineWidth(map, (float)1.0);
		GraphConstants.setLabelAlongEdge(map, true);
		GraphConstants.setEditable(map, true);
		Hashtable attributes = new Hashtable();
		attributes.put(edge, map);
		graph.getModel().insert(new Object[] { edge }, attributes, cs, null, null);
		graph.getGraphLayoutCache().toBack(new Object[] { edge });
		graph.getGraphLayoutCache().setVisible(edge, true);
		edge.setName(label == null ? STTools.BLANK : label);
		return edge;
	}


	/** Create a new edge between source and target nodes.
	 * @param label the label
	 * @param source the source
	 * @param target the target
	 * @return the ST edge */
	public static STEdge edgeCreate(final String label, final String source, final String target) {
		Port pSource = null;
		Port pTarget = null;
		for(STNode node : STGraph.getSTC().getCurrentGraph().getNodes()) {
			if(node.getName().equals(source)) { pSource = (Port)node.getFirstChild(); }
			else if(node.getName().equals(target)) { pTarget = (Port)node.getFirstChild(); }
		}
		return edgeCreate(label, pSource, pTarget);
	}


	/** Create a new widget with the specified parameters and the default properties.
	 * @param type the type
	 * @return widget */
	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
	public static STWidget widgetCreate(final String type) {
		STGraphC stc = STGraph.getSTC();
		STGraphExec graph = stc.getCurrentGraph();
		try {
			stc.getLoggerUI().info("creating a new widget..."); //$NON-NLS-1$
			STWidget widget = (STWidget)Class.forName(("it.liuc.stgraph.widget." + type)).newInstance(); //$NON-NLS-1$
			widget.setType(type);
			Map map = new Hashtable();
			GraphConstants.setBounds(map, new Rectangle(5, 5, 1, 1));
			GraphConstants.setEditable(map, false);
			GraphConstants.setBendable(map, false);
			GraphConstants.setMoveable(map, false);
			GraphConstants.setConnectable(map, false);
			GraphConstants.setDisconnectable(map, false);
			GraphConstants.setSizeable(map, false);
			Hashtable attributes = new Hashtable();
			attributes.put(widget, map);
			graph.getModel().insert(new Object[] { widget }, attributes, null, null, null);
			graph.getGraphLayoutCache().setVisible(widget, true);
			widget.setPanel(WidgetView.getDefaultWidgetBounds());
			return widget;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
