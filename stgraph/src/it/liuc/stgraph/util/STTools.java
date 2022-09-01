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

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STDesktop;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.node.STEdge;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.node.TextNode;
import it.liuc.stgraph.widget.InputWidget;
import it.liuc.stgraph.widget.STWidget;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.nfunk.jep.type.Tensor;


/** Utility class. */
public final class STTools {
	/** The information text dialog (injected by Spring and got by the <code>STGraphC.initContainer()</code> method). */
	public static InfoDialog infoTextDialog;
	/** The information data dialog (injected by Spring and got by the <code>STGraphC.initContainer()</code> method). */
	public static InfoDialog infoDataDialog;
	/** The execution tracing dialog (injected by Spring and got by the <code>STGraphC.initContainer()</code> method). */
	public static InfoDialog tracingDialog;
	/** The output vars dialog (injected by Spring and got by the <code>STGraphC.initContainer()</code> method). */
	public static InfoDialog outputVarsDialog;
	/** The samples dialog (injected by Spring and got by the <code>STGraphC.initContainer()</code> method). */
	public static InfoDialog samplesDialog;
	/** The update dialog (injected by Spring and got by the <code>STGraphC.initContainer()</code> method). */
	public static InfoDialog updateDialog;
	/** Blank () constant. */
	public static final String BLANK = ""; //$NON-NLS-1$
	/** Space ( ) constant. */
	public static final String SPACE = " "; //$NON-NLS-1$
	/** Zero (0.0 Double value) constant. */
	public static final Tensor ZERO = Tensor.newScalar(0.0);
	/** Slash (/) constant. */
	public static final String SLASH = "/"; //$NON-NLS-1$
	/** Dot (.) constant. */
	public static final String DOT = "."; //$NON-NLS-1$
	/** Colon (:)constant. */
	public static final String COLON = ":"; //$NON-NLS-1$
	/** Semicolon (;) constant. */
	public static final String SEMICOLON = ";"; //$NON-NLS-1$
	/** Double quote (") constant. */
	public static final String DOUBLEQUOTE = "\""; //$NON-NLS-1$
	/** Comma (,) constant. */
	public static final String COMMA = ","; //$NON-NLS-1$
	/** Underscore (_) constant. */
	public static final String UNDERSCORE = "_"; //$NON-NLS-1$
	/** Asterisk (*) constant. */
	public static final String ASTERISK = "*"; //$NON-NLS-1$
	/** OPENC ({)constant. */
	public static final String OPENC = "{"; //$NON-NLS-1$
	/** CLOSEC (}) constant. */
	public static final String CLOSEC = "}"; //$NON-NLS-1$
	/** OPENV ([)constant. */
	public static final String OPENV = "["; //$NON-NLS-1$
	/** CLOSEV (]) constant. */
	public static final String CLOSEV = "]"; //$NON-NLS-1$
	/** OPENP (()constant. */
	public static final String OPENP = "("; //$NON-NLS-1$
	/** CLOSEP ()) constant. */
	public static final String CLOSEP = ")"; //$NON-NLS-1$
	/** QUESTION (?) constant. */
	public static final String QUESTION = "?"; //$NON-NLS-1$
	/** SHARP (#) constant. */
	public static final String SHARP = "#"; //$NON-NLS-1$
	/** SHARP (@) constant. */
	public static final String AT = "@"; //$NON-NLS-1$
	/** DASH (-) constant. */
	public static final String DASH = "-"; //$NON-NLS-1$
	/** ELLIPSIS (...) constant. */
	public static final String ELLIPSIS = "..."; //$NON-NLS-1$
	/** Newline (\n) constant. */
	public static final String NEWLINE = "\n"; //$NON-NLS-1$
	/** The MESSAGETYPE_ERR constant. */
	public static final int MESSAGETYPE_ERR = 0;
	/** The MESSAGETYPE_WAR constant. */
	public static final int MESSAGETYPE_WAR = 1;
	/** The MESSAGETYPE_INF constant. */
	public static final int MESSAGETYPE_INF = 2;
	/** The ALIGN_HORIZONTAL_TOP constant. */
	public static final int ALIGN_HORIZONTAL_TOP = 0;
	/** The ALIGN_HORIZONTAL_BOTTOM constant. */
	public static final int ALIGN_HORIZONTAL_BOTTOM = 1;
	/** The ALIGN_VERTICAL_LEFT constant. */
	public static final int ALIGN_VERTICAL_LEFT = 2;
	/** The ALIGN_VERTICAL_RIGHT constant. */
	public static final int ALIGN_VERTICAL_RIGHT = 3;


	/** Class constructor. */
	private STTools() { ; }


	/** Get the JVM version on which it is running.
	 * @return the version */
	public static double getJVMVersion() {
		String version = System.getProperty("java.version"); //$NON-NLS-1$
		int pos = 0, count = 0;
		for(; pos < version.length() && count < 2; pos++) {
			if(version.charAt(pos) == '.') { count++; }
		}
		--pos; //EVALUATE double
		double dversion = Double.parseDouble(version.substring(0, pos));
		//System.out.println("dversion:" + dversion);
		return dversion;
	}


	/** Get whether it is running under JWS.
	 * @return result */
	public static boolean isRunningJavaWebStart() {
		return System.getProperty("javawebstart.version", null) != null; //$NON-NLS-1$
	}


	/** Check whether the argument is a null, or an empty string, or the string "null".
	 * @param s the string to check
	 * @return is empty */
	public static boolean isEmpty(final Object s) {
		if(s == null) { return true; }
		String ss = (String)s;
		while(ss.length() > 0 && ss.substring(0, 1).equals("\n")) { //$NON-NLS-1$
			ss = ss.substring(1);
		}
		if(ss.length() == 0 || ss.equals(STTools.BLANK) || ss.equals("null")) { return true; } //$NON-NLS-1$
		return false;
	}


	/** Check whether the argument is a model node.
	 * @param o the o
	 * @return is node */
	public static boolean isNode(final Object o) { return (o instanceof STNode); }


	/** Check whether the argument is a text comment.
	 * @param o the o
	 * @return is text */
	public static boolean isText(final Object o) { return o instanceof TextNode; }


	/** Check whether the argument is a widget.
	 * @param o the o
	 * @return is widget */
	public static boolean isWidget(final Object o) { return (o instanceof STWidget); }


	/** Check whether the argument is an input widget.
	 * @param o the o
	 * @return is input widget */
	public static boolean isInputWidget(final Object o) { return (o instanceof InputWidget); }


	/** Check whether the argument is a model edge.
	 * @param o the o
	 * @return is edge */
	public static boolean isEdge(final Object o) { return (o instanceof STEdge); }


	/** Check whether a node is selected in the current graph.
	 * @return is selected */
	public static boolean isNodeSelected() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		if(graph == null) { return false; }
		return (graph.getSelectionCount() > 0) && (graph.getSelectionCell() instanceof STNode); 
	}


	/** Return a somehow more appropriate text instead of "vTime" and "vIndex" to visualization in widgets.
	 * @param text the text
	 * @return string */
	public static String setAlternateText(final String text) {
		if(text.equals("vTime")) { return STGraphC.getMessage("TIME.LABEL"); } //$NON-NLS-1$ //$NON-NLS-2$
		else if(text.equals("vIndex")) { return STGraphC.getMessage("INDEX.LABEL"); } //$NON-NLS-1$ //$NON-NLS-2$
		return text;
	}


	/** Handle the movement of selected nodes.
	 * @param dx the dx
	 * @param dy the dy
	 * @param multiplier the multiplier (x5 if true) */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void moveNodes(final int dx, final int dy, final boolean multiplier) {
		final int mul = 5;
		Map map;
		Rectangle2D rect = null;
		List points;
		int ddx = dx * (multiplier ? mul : 1);
		int ddy = dy * (multiplier ? mul : 1);
		Object[] o = STGraph.getSTC().getCurrentGraph().getSelectionCells();
		Map viewMap = new Hashtable();
		for(int i = 0; i < o.length; i++) {
			if(isNode(o[i])) {
				/*
                map = ((DefaultGraphCell)o[i]).getAttributes();
                rect = GraphConstants.getBounds(map);
                rect.setRect(rect.getX() + ddx, rect.getY() + ddy, rect.getWidth(), rect.getHeight());
                GraphConstants.setBounds(map, rect);
                viewMap.put(o[i], map);
				 */
				Point p = ((STNode)o[i]).getPosition();
				((STNode)o[i]).setPosition(p.x + ddx, p.y + ddy);
			} else if(isText(o[i])) {
				map = ((DefaultGraphCell)o[i]).getAttributes();
				rect = GraphConstants.getBounds(map);
				rect.setRect(rect.getX() + ddx, rect.getY() + ddy, rect.getWidth(), rect.getHeight());
				GraphConstants.setBounds(map, rect);
				viewMap.put(o[i], map);
			} else if(isEdge(o[i])) {
				map = ((DefaultGraphCell)o[i]).getAttributes();
				points = GraphConstants.getPoints(map);
				if(points != null) {
					Point2D.Double point;
					for(int j = 0; j < points.size(); j++) {
						point = (Point2D.Double)points.get(j);
						point.x += ddx;
						point.y += ddy;
					}
					GraphConstants.setPoints(map, points);
					viewMap.put(o[i], map);
				}
			}
		}
		STGraph.getSTC().getCurrentGraph().getGraphLayoutCache().edit(viewMap, null, null, null);
	}


	/** Move and scale the selected graph objects, both nodes and edges.
	 * @param shiftFactor the shift factor
	 * @param scaleFactor the scale factor */
	public static void moveScaleNodes(final double shiftFactor, final double scaleFactor) {
		STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
		if(graph == null) { return; }
		Object[] objs = graph.getSelectionCells();
		Point c = STTools.getCenterOfSelectedNodes(objs);
		if(c != null) {
			for(Object obj : objs) {
				if(STTools.isNode(obj) || STTools.isEdge(obj)) {
					STTools.moveScaleNode((DefaultGraphCell)obj, c, shiftFactor, scaleFactor);
				}
			}
		}
	}


	/** Move and scale the specified graph object, a node or an edge.
	 * @param obj the object, a node or an edge
	 * @param center the point toward which shifting
	 * @param shiftFactor the shift factor
	 * @param scaleFactor the scale factor */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void moveScaleNode(final DefaultGraphCell obj, final Point center, final double shiftFactor, final double scaleFactor) {
		if(!isNode(obj) && !isEdge(obj)) { return; }
		Map map;
		Rectangle2D rect = null;
		Map viewMap = new Hashtable();
		map = obj.getAttributes();
		if(isNode(obj)) {
			rect = GraphConstants.getBounds(map);
			rect.setRect((int)(center.x + shiftFactor * (rect.getX() - center.x)), (int)(center.y + shiftFactor * (rect.getY() - center.y)), rect.getWidth() * scaleFactor, rect.getHeight() * scaleFactor);
			//GraphConstants.setBounds(map, rect);
			((STNode)obj).setBounds(rect);
		} else { // it is an edge
			List points = GraphConstants.getPoints(map);
			if(points != null) {
				Point2D.Double point;
				for(int j = 0; j < points.size(); j++) {
					point = (Point2D.Double)points.get(j);
					point.x = center.x + shiftFactor * (point.x - center.x);
					point.y = center.y + shiftFactor * (point.y - center.y);
				}
				GraphConstants.setPoints(map, points);
			}
		}
		viewMap.put(obj, map);
		STGraph.getSTC().getCurrentGraph().getGraphLayoutCache().edit(viewMap, null, null, null);
	}


	/** Align the selected nodes.
	 * @param orientation the orientation, either horizontal (top or bottom) or vertical (left or right) */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void alignNodes(final int orientation) {
		if(orientation != ALIGN_HORIZONTAL_TOP && orientation != ALIGN_HORIZONTAL_BOTTOM &&
				orientation != ALIGN_VERTICAL_LEFT && orientation != ALIGN_VERTICAL_RIGHT) { return; }
		Object[] obj = STGraph.getSTC().getCurrentGraph().getSelectionCells();
		if(obj == null || obj.length == 0) { return; }
		ArrayList<STNode> nodes = new ArrayList<STNode>();
		STNode node;
		double p = (orientation == ALIGN_HORIZONTAL_TOP || orientation == ALIGN_VERTICAL_LEFT) ? 1E10 : -1;
		double pp;
		for(Object o : obj) {
			if(isNode(o)) {
				nodes.add(node = (STNode)o);
				if(orientation == ALIGN_HORIZONTAL_TOP) {
					if((pp = GraphConstants.getBounds(node.getAttributes()).getMinY()) < p) { p = pp; }
				} else if(orientation == ALIGN_HORIZONTAL_BOTTOM) {
					if((pp = GraphConstants.getBounds(node.getAttributes()).getMaxY()) < p) { p = pp; }
				} else if(orientation == ALIGN_VERTICAL_LEFT) {
					if((pp = GraphConstants.getBounds(node.getAttributes()).getMinX()) < p) { p = pp; }
				} else {
					if((pp = GraphConstants.getBounds(node.getAttributes()).getMaxX()) < p) { p = pp; }
				}
			}
		}
		if(nodes.size() <= 1) { return; }
		Map viewMap = new Hashtable();
		Map map;
		Rectangle2D rect = null;
		for(STNode n : nodes) {
			map = n.getAttributes();
			rect = GraphConstants.getBounds(map);
			if(orientation == ALIGN_HORIZONTAL_TOP || orientation == ALIGN_HORIZONTAL_BOTTOM) {
				rect.setRect(rect.getX(), p, rect.getWidth(), rect.getHeight());
			} else {
				rect.setRect(p, rect.getY(), rect.getWidth(), rect.getHeight());
			}
			GraphConstants.setBounds(map, rect);
			viewMap.put(n, map);
		}
		STGraph.getSTC().getCurrentGraph().getGraphLayoutCache().edit(viewMap, null, null, null);
	}


	/** Get the geometric center of the specified array of nodes.
	 * @param nodes the array of nodes
	 * @return center */
	public static Point getCenterOfSelectedNodes(final Object[] nodes) {
		if(nodes.length == 0) { return null; }
		int numNodes = 0;
		Rectangle2D r;
		double xCenter = 0;
		double yCenter = 0;
		for(Object node : nodes) {
			if(STTools.isNode(node)) {
				numNodes++;
				STNode nnode = (STNode)node;
				r = nnode.getBounds();
				xCenter += r.getCenterX();
				yCenter += r.getCenterY();
			}
		}
		if(numNodes > 0) {
			xCenter /= numNodes;
			yCenter /= numNodes;
			return new Point((int)xCenter, (int)yCenter);
		}
		return null;
	}


	/** Get a list of the variables from which the specified node depends, including the ones related to subsystems.
	 * @param node the node
	 * @return list */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList<String> getIncomingVars(final STNode node) {
		STEdge[] edges = STGraph.getSTC().getCurrentGraph().getNodeEdges(node, STGraphImpl.EDGEMODE_IN);
		if(edges == null || edges.length == 0) { return null; }
		ArrayList al = new ArrayList();
		for(STEdge edge : edges) {
			STNode n = edge.getSourceNode();
			String name = n.getFullName();
			if(!n.isModel()) {
				if(!al.contains(name)) { al.add(name); }
			} else {
				STDesktop desk = ((ModelNode)n).getDesk();
				if(desk != null) {
					for(STNode nn : desk.getGraph().getOutputNodeList()) { al.add(nn.getFullName()); }
				}
			}
		}
		Collections.sort(al);
		return al;
	}


	/** Connect the source node to the target node, by modifying the <code>connectedTo</code> property of the former
	 * and the <code>connectedFrom</code> property of the latter.
	 * @param source the source
	 * @param target the target */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void connect(final STNode source, final STNode target) {
		if(source == null || target == null) { return; }
		ArrayList<STNode> ob = source.getDefinedNodes();
		ArrayList<STNode> prop = (ob == null) ? new ArrayList() : ob;
		if(!prop.contains(target)) {
			prop.add(target);
			source.setConnectedTo(prop);
		}
		ob = target.getDefiningNodesByExpressions();
		prop = (ob == null) ? new ArrayList() : ob;
		if(!prop.contains(source)) {
			prop.add(source);
			target.setConnectedFrom(prop);
		}
	}


	/** Connect the source node to the target node, by modifying the <code>connectedFrom2</code> property of the latter.
	 * @param source the source
	 * @param target the target */
	public static void connect2wrapper(final STNode source, final STNode target) {
		if(!target.isModel()) { // *-valuenode connections
			if(!source.isModel()) { // node-node connection
				STTools.connect2(source, target);
			} else { // submodel-node connection
				STDesktop desk = ((ModelNode)source).getDesk();
				if(desk != null) {
					for(STNode n : desk.getGraph().getOutputNodeList()) { STTools.connect2(n, target); }
				}
			}
		} else { // *-submodel connections
			if(!source.isModel()) { // node-submodel connection
				STTools.connect2(source, target);
				STDesktop desk = ((ModelNode)target).getDesk();
				if(desk != null) {
					for(STNode n : desk.getGraph().getInputNodeList()) { STTools.connect2(source, n); }
				}					
			} else { // submodel-submodel connection
				STTools.connect2(source, target);
				STDesktop desk1 = ((ModelNode)source).getDesk();
				STDesktop desk2 = ((ModelNode)target).getDesk();
				if(desk1 != null && desk2 != null) {
					for(STNode n1 : desk1.getGraph().getOutputNodeList()) {
						for(STNode n2 : desk2.getGraph().getInputNodeList()) { STTools.connect2(n1, n2); }
					}
				}
			}
		}
	}


	/** Connect the source node to the target node, by modifying the <code>connectedFrom2</code> property of the latter.
	 * @param source the source
	 * @param target the target */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void connect2(final STNode source, final STNode target) {
		ArrayList<STNode> ob = target.getDefiningNodesByEdges();
		ArrayList<STNode> prop = (ob == null) ? new ArrayList() : ob;
		prop.add(source);
		target.setConnectedFrom2(prop);
	}


	/** Get the reference to the named node if found in the given list.
	 * @param name the name
	 * @param list the list
	 * @return node */
	public static STNode getNamedNodeFromList(final String name, final STNode[] list) {
		STNode node = null;
		for(int i = 0; i < list.length; i++) {
			node = list[i];
			if(node.getName().equals(name)) { return node; }
		}
		return null;
	}


	/** Get  the list of node names in the given list.
	 * @param list the list
	 * @return list */
	public static String[] getNamesFromNodeList(final STNode[] list) {
		String[] names = new String[list.length];
		for(int i = 0; i < list.length; i++) {
			names[i] = list[i].getName();
		}
		Arrays.sort(names);
		return names;
	}


	/** Get the list of node names in the given list, with the second list appended.
	 * @param list the list
	 * @param further the further
	 * @return list */
	public static String[] getNamesFromNodeList(final STNode[] list, final String[] further) {
		String[] names = new String[list.length + further.length];
		for(int i = 0; i < list.length; i++) {
			names[i] = list[i].getName();
		}
		for(int i = 0; i < further.length; i++) {
			names[list.length + i] = further[i];
		}
		Arrays.sort(names);
		return names;
	}


	/** Open an information dialog and display the specified text in it.
	 * @param type the type
	 * @param text the text */
	public static void messenger(final int type, final String text) {
		if(STGraph.getExecLevel() == STGraph.EXECLEVEL_WEB) {
			System.err.println(text);
			return;
		}
		int x = 0;
		String title = STTools.BLANK;
		switch(type) {
		case MESSAGETYPE_ERR:
			title = STGraphC.getMessage("TYPE.MSG.ERR"); x = JOptionPane.ERROR_MESSAGE; //$NON-NLS-1$
			break;
		case MESSAGETYPE_WAR:
			title = STGraphC.getMessage("TYPE.MSG.WAR"); x = JOptionPane.WARNING_MESSAGE; //$NON-NLS-1$
			break;
		case MESSAGETYPE_INF:
			title = STGraphC.getMessage("TYPE.MSG.INF"); x = JOptionPane.INFORMATION_MESSAGE; //$NON-NLS-1$
			break;
		default:
			break;
		}
		JOptionPane.showMessageDialog(STGraph.getSTC(), text, title, x);
	}


	/** Open a confirmation dialog and display the specified text in it.
	 * @param text the text
	 * @return result */
	public static int confirmer(final String text) {
		String title = STGraphC.getMessage("TYPE.MSG.INF"); //$NON-NLS-1$
		JOptionPane.setDefaultLocale(STGraphC.getSTLocale());
		return JOptionPane.showConfirmDialog(STGraph.getSTC(), text, title, JOptionPane.YES_NO_OPTION);
	}


	/** Maximize the current desktop frame. */
	public static void maximizeDesktop() {
		STGraphC stc = STGraph.getSTC();
		STDesktop currentDesktop = stc.getCurrentDesktop();
		try {
			Dimension dim = currentDesktop.getSize();
			if(currentDesktop.dim == null || currentDesktop.dim.width != dim.width || currentDesktop.dim.height != dim.height) {
				currentDesktop.getGraphFrame1().setSize(dim);
				currentDesktop.getGraphFrame2().setPreferredSize(new Dimension(dim.width, dim.height - 5)); // the magic number, 5, guarantees that the bottom scrollbar is fully visualized 
				stc.getCurrentGraph().setSize(dim);
				STGraphC.getStatusBar().setControlVisibile(isNodeSelected());
				//**********************
				/*
                if(currentDesktop.dim != null && currentDesktop.dim.width > 0 && currentDesktop.dim.height > 0) {
                    double w = dim.getWidth() / currentDesktop.dim.getWidth();
                    double h = dim.getHeight() / currentDesktop.dim.getHeight();
                    for(STWidget widget : stc.getCurrentGraph().getWidgets()) {
                    	widget.setPosition((int)(widget.getPositionX() * w), (int)(widget.getPositionY() * h));
                    	widget.setSize((int)(widget.getWidth() * w), (int)(widget.getHeight() * h));
                    }
                }
				 */
				//**********************
				currentDesktop.dim = dim;
			}
		} catch (Exception e) { ; }
	}


	/** Scan the contents of a text file replace the <code>\n</code> characters with the corresponding HTML tag, somehow clean it, then
	 * returning the generated string.
	 * @param fileName the file name
	 * @return string */
	public static String file2HTMLString(final String fileName) {
		try {
			BufferedReader file = new BufferedReader(new InputStreamReader(STGraph.getSTC().getClass().getClassLoader().getResourceAsStream("datafiles/" + fileName))); //$NON-NLS-1$
			StringBuilder sb = new StringBuilder();
			String s;
			int p;
			while(file.ready()) {
				s = file.readLine();
				if(s.startsWith(SHARP)) { s = s.substring(1); }
				else {
					p = s.indexOf(" = "); //$NON-NLS-1$
					if(p >= 0) { s = s.substring(p + 3).replace('\\', ' '); }
				}
				sb.append(s + "<br>"); //$NON-NLS-1$
			}
			file.close();
			return sb.toString();
		} catch (Exception ex) { ex.printStackTrace(); }
		return STTools.BLANK;
	}


	/** Convert the specified stream to a string.
	 * @param is the stream to be read
	 * @return string
	 * @throws IOException if the stream cannot be read */
	public static String stream2String(InputStream is) throws IOException {
		if(is == null) {
			return STTools.BLANK;
		}
		StringBuilder sb = new StringBuilder();
		String l;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while((l = br.readLine()) != null) {
				sb.append(l).append(STTools.NEWLINE);
			}
		} finally {
			is.close();
		}
		return sb.toString();	
	}


	/** Convert the specified string to a stream.
	 * @param s the string to be read
	 * @return stream
	 * @throws Exception if the stream cannot be encoded */
	public static InputStream string2Stream(String s) throws Exception {
		return new ByteArrayInputStream(s.getBytes());
	}


	/** Encode for XML, to properly handle special chars (note that NanoXML does not require this handling).
	 * @param text the text
	 * @return text */
	public static String encodeForXML(final String text) {
		try { return new String(text.getBytes("UTF-8")); } catch (Exception e) { return text; } //$NON-NLS-1$
	}


	/** Replace all the instances of the first argument in the given string with the second argument.
	 * @param s the s
	 * @param from the from
	 * @param to the to
	 * @return string */
	public static String replaceAll(final String s, final String from, final String to) {
		String s2 = s;
		int p = 0;
		while((p = s2.indexOf(from)) != -1) { s2 = s2.substring(0, p) + to + s2.substring(p + from.length()); }
		return s2;
	}


	/** Replace all the CR characters in the given string with the BR HTML tag.
	 * @param s the s
	 * @return string */
	public static String replaceCRwithBR(final String s) {
		String s2 = s;
		return replaceAll(s2, STTools.NEWLINE, "<br>"); //$NON-NLS-1$
	}


	/** Replace all the CR characters in the given string with a space.
	 * @param s the s
	 * @return string */
	public static String replaceCRwithSpace(final String s) {
		String s2 = s;
		return replaceAll(s2, STTools.NEWLINE, STTools.SPACE);
	}


	/** Adapt the given string to its visualization in a JS window.alert() box.
	 * @param s the s
	 * @return string */
	public static String adaptToAlert(final String s) {
		String s2 = s;
		String result = replaceAll(s2, STTools.NEWLINE, "\\n"); //$NON-NLS-1$
		return replaceAll(replaceAll(result, "'", "&acute;"), "\"", "&acute;&acute;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}


	/** Adapt the given string to its visualization in an HTML page.
	 * @param s the s
	 * @return string */
	public static String adaptToHTML(final String s) {
		String s2 = s;
		String result = replaceAll(s2, "<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
		return replaceAll(result, STTools.NEWLINE, "<br>"); //$NON-NLS-1$
	}


	/** Replace the HTML meta-characters for delimiters in the given string with angular brackets.
	 * @param s the s
	 * @return string */
	public static String replaceHTMLDelimiters(final String s) {
		String s2 = s;
		String result = replaceAll(s2, "&lt;", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		result = replaceAll(result, "&gt;", ">"); //$NON-NLS-1$ //$NON-NLS-2$
		result = replaceAll(result, "&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		return result;
	}


	/** Adapt the given image tag to its visualization in an HTML page.
	 * @param s the s
	 * @return string */
	public static String adaptImagesToHTML(final String s) {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		if(graph == null) { return s; }
		String context = graph.contextName;
		if(STTools.isEmpty(context)) { return s; }
		context = "file:" + context + System.getProperty("file.separator");
		String s2 = s;
		int start = 0;
		int end = 0;
		while((start = (s2.indexOf("<img", start))) != -1) {
			end = s2.indexOf(">", start + 5);
			if(end == -1) { return s; } // wrongly formatted string
			end++;
			String imgTag = s2.substring(start, end);
			int start1 = imgTag.indexOf("src=\"");
			if(start1 == -1) { return s; } // wrongly formatted string
			start1 += 5;
			int end1 = imgTag.indexOf("\"", start1);
			if(end1 == -1) { return s; } // wrongly formatted string
			String srcArg = imgTag.substring(start1, end1);
			if(srcArg.startsWith("http:")) {
				; // nothing to do
			} else { // in the case of local images, they must be stored in the same dir as the model
				int p = srcArg.lastIndexOf(System.getProperty("file.separator"));
				if(p != -1) { srcArg = srcArg.substring(p + 1); }
				s2 = s2.substring(0, start + start1) + context + srcArg + s2.substring(start + end1);
			}
			start = end;
		}
		return s2;
	}


	/** Strip the path from the file name.
	 * @param name the name
	 * @return string */
	public static String stripPathFromFileName(final String name) {
		String name2 = name;
		int pos = -1;
		if((pos = name2.lastIndexOf(STTools.SLASH)) != -1) { name2 = name2.substring(pos + 1); }
		return name2;
	}


	/** Strip the package name from the full class name.
	 * @param name the name
	 * @return string */
	public static String stripPackageName(final String name) {
		String name2 = name;
		int pos = -1;
		if((pos = name2.lastIndexOf(STTools.DOT)) != -1) { name2 = name2.substring(pos + 1); } // remove the package name
		return name2;
	}


	/** Map obj.
	 * @param in the in
	 * @param out the out
	 * @param prop the prop */
	@SuppressWarnings("unchecked")
	public static void mapObj(final AttributeMap in, final AttributeMap out, final String prop) {
		Object b;
		if((b = in.get(prop)) != null) { out.put(prop, b); }
	}


	/** Map obj array.
	 * @param in the in
	 * @param out the out
	 * @param prop the prop */
	@SuppressWarnings("unchecked")
	public static void mapObjArray(final AttributeMap in, final AttributeMap out, final String prop) {
		Object[] s;
		if((s = (Object[])in.get(prop)) != null) {
			StringBuilder ss = new StringBuilder();
			for(int i = 0; i < s.length - 1; i++) { ss.append(s[i] + COMMA); }
			ss.append(s[s.length - 1]);
			out.put(prop, ss.toString());
		}
	}


	/** Map int array.
	 * @param in the in
	 * @param out the out
	 * @param prop the prop */
	@SuppressWarnings("unchecked")
	public static void mapIntArray(final AttributeMap in, final AttributeMap out, final String prop) {
		int[] s;
		if((s = (int[])in.get(prop)) != null) {
			StringBuilder ss = new StringBuilder();
			for(int i = 0; i < s.length - 1; i++) { ss.append(s[i] + COMMA); }
			ss.append(s[s.length - 1]);
			out.put(prop, ss.toString());
		}
	}


	/** Map boolean array.
	 * @param in the in
	 * @param out the out
	 * @param prop the prop */
	@SuppressWarnings("unchecked")
	public static void mapBooleanArray(final AttributeMap in, final AttributeMap out, final String prop) {
		boolean[] s;
		if((s = (boolean[])in.get(prop)) != null) {
			StringBuilder ss = new StringBuilder();
			for(int i = 0; i < s.length - 1; i++) { ss.append(s[i] + COMMA); }
			ss.append(s[s.length - 1]);
			out.put(prop, ss.toString());
		}
	}


	/** Put string array.
	 * @param in the in
	 * @param num the num
	 * @param def the def
	 * @return the array */
	public static String[] putStringArray(final Object in, final int num, final String def) {
		String[] ret = null;
		if(in != null) { ret = ((String)in).split(COMMA); }
		if(ret == null || ret.length != num) {
			ret = new String[num];
			for(int i = 0; i < num; i++) { ret[i] = def; }
		}
		return ret;
	}


	/** Put int array.
	 * @param in the in
	 * @param num the num
	 * @param def the def
	 * @return the array */
	public static int[] putIntArray(final Object in, final int num, final int def) {
		int[] ret = null;
		if(in != null) {
			String[] s = ((String)in).split(COMMA);
			if(s.length == num) {
				ret = new int[num];
				for(int i = 0; i < num; i++) { ret[i] = Integer.parseInt(s[i]); }
			}
		}
		if(ret == null || ret.length != num) {
			ret = new int[num];
			for(int i = 0; i < num; i++) { ret[i] = def; }
		}
		return ret;
	}


	/** Copy the image (as screenshot) of the component to the system clipboard.
	 * @param comp the comp */
	public static void copyImageToClipboard(final JComponent comp) {
		ImageTransferable it = new ImageTransferable(comp);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(it, null);
	}


	/** List classes inside the specified package.
	 * It requires the package to be in the file system (not in a jar).
	 * @param packageName String name of a package
	 * @return Class[] classes inside the root of the given package
	 * @throws ClassNotFoundException if the package is invalid */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Class[] getClassesInPackage(final String packageName) throws ClassNotFoundException {
		ArrayList classes = new ArrayList();
		File directory = null;
		try {
			String path = packageName.replace('.', '/');
			URL resource = STGraph.class.getClassLoader().getResource(path);
			if(resource == null) { throw new ClassNotFoundException("No resource for " + path); } //$NON-NLS-1$
			directory = new File(resource.getFile());
		} catch (NullPointerException e) {
			throw new ClassNotFoundException(packageName + " (" + directory + ") does not appear to be a valid package"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if(directory.exists()) { // get the list of the files contained in the package
			String[] files = directory.list();
			for(int i = 0; i < files.length; i++) {
				if(files[i].endsWith(".class")) { //$NON-NLS-1$
					classes.add(Class.forName(packageName + '.' + files[i].substring(0, files[i].length() - 6))); // remove the .class extension
				}
			}
		} else {
			throw new ClassNotFoundException(packageName + " does not appear to be a valid package"); //$NON-NLS-1$
		}
		Class[] classesA = new Class[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}


	/** List classes inside the specified package of the specified jar.
	 * @param packageName String name of a package
	 * @param jarName String name of the jar
	 * @return Class[] classes inside the root of the given package
	 * @throws Exception if the package is invalid */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Class[] getClassesInPackage(final String packageName, final String jarName) throws Exception {
		FileInputStream fis;
		try { fis = new FileInputStream(jarName); } catch (Exception e) { return null; }
		ArrayList classes = new ArrayList();
		String path = packageName.replace('.', '/');
		try {
			JarInputStream jarFile = new JarInputStream(fis);
			JarEntry jarEntry;
			String name;
			while(true) {
				jarEntry = jarFile.getNextJarEntry();
				if(jarEntry == null) { break; }
				name = jarEntry.getName();
				if((name.startsWith(path)) && (name.endsWith(".class"))) { //$NON-NLS-1$
					classes.add(Class.forName(name.replace('/', '.').substring(0, name.length() - 6))); // remove the .class extension
				}
			}
			jarFile.close();
		} catch (Exception e) { ; }
		Class[] classesA = new Class[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}


	/** Update application jars.
	 * @param isExplicitCheck is a check explicitly performed by the user? */
	public static void updateJars(final boolean isExplicitCheck) {
		if(!isExplicitCheck && STConfigurator.getProperty("AUTOUPDATE").equals("false")) { return; } //$NON-NLS-1$ //$NON-NLS-2$
		String localPath = STGraphC.getBasicProps().getProperty("JAR.LOCALDIR"); //$NON-NLS-1$
		String remotePath = STGraphC.getBasicProps().getProperty("JAR.REMOTEDIR"); //$NON-NLS-1$
		List<String> jars = Arrays.asList(STGraphC.getBasicProps().getProperty("JAR.NAMES").split(STTools.COMMA)); //$NON-NLS-1$
		boolean somethingToDo = false;
		try {
			STGraph.getSTC().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			StringBuilder s = new StringBuilder(STGraphC.getMessage("HELP.UPDATE.TOUPDATE") + STTools.NEWLINE); //$NON-NLS-1$
			ArrayList<URL> remoteUrls = new ArrayList<URL>();
			ArrayList<String> localFiles = new ArrayList<String>();
			for(String jar : jars) {
				File localFile = new File(localPath + jar);
				long localLastModified = localFile.lastModified();
				URL remoteUrl = new URL(remotePath + jar);
				URLConnection connection = remoteUrl.openConnection();
				long remoteLastModified = connection.getLastModified();
				if(remoteLastModified == 0) { // unreachable file
					if(isExplicitCheck) { STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("HELP.UPDATE.UNREACHABLE")); } //$NON-NLS-1$
					STGraph.getSTC().setCursor(Cursor.getDefaultCursor());
					return;
				} else if(localLastModified == 0) { // new file
					s.append(localFile.getName() + STTools.SPACE + STTools.OPENP + new Date(remoteLastModified) + STTools.CLOSEP + STTools.NEWLINE);
					remoteUrls.add(remoteUrl);
					localFiles.add(localPath + jar);
					somethingToDo = true;
				} else {
					if((remoteLastModified - localLastModified) / 86400000 > 1) { // existing file to update (at least one day newer, to prevent micro-updates)
						s.append(localFile.getName() + STTools.SPACE + STTools.OPENP + new Date(localLastModified) + " --> " + new Date(remoteLastModified) + STTools.CLOSEP + STTools.NEWLINE); //$NON-NLS-1$
						remoteUrls.add(remoteUrl);
						localFiles.add(localPath + jar);
						somethingToDo = true;
					} else { // already updated file
						;
					}
				}
			}
			STGraph.getSTC().setCursor(Cursor.getDefaultCursor());
			if(!somethingToDo) {
				if(isExplicitCheck) { STTools.messenger(STTools.MESSAGETYPE_INF, STGraphC.getMessage("HELP.UPDATE.NOOP")); } //$NON-NLS-1$
			} else {
				int resp = STTools.confirmer(s.toString() + STGraphC.getMessage("HELP.UPDATE.QUESTION")); //$NON-NLS-1$
				if(resp == 0) { // perform update
					STGraph.getSTC().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					if(remoteUrls.size() > 0) {
						for(int i = 0; i < remoteUrls.size(); i++) { download(remoteUrls.get(i), localFiles.get(i)); }
					}
					STTools.messenger(STTools.MESSAGETYPE_INF, STGraphC.getMessage("HELP.UPDATE.CORRECT")); //$NON-NLS-1$
				}
			}
		} catch (MalformedURLException e) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("HELP.UPDATE.ERR1")); //$NON-NLS-1$
		} catch (IOException e) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("HELP.UPDATE.ERR2")); //$NON-NLS-1$
		} finally {
			STGraph.getSTC().setCursor(Cursor.getDefaultCursor());
		}
	}


	/** Helper method for jar updating to download the requested files.
	 * @param remoteUrl the server URL 
	 * @param localFile the file to be downloaded and saved locally
	 * @throws IOException exception related to server reachability or data transfer */
	private static void download(final URL remoteUrl, final String localFile) throws IOException {
		ReadableByteChannel rbc = Channels.newChannel(remoteUrl.openStream());
		FileOutputStream fos = new FileOutputStream(localFile);
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		fos.close();
	}

}
