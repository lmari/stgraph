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
package it.liuc.stgraph.util;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STInterpreter;

import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.SimpleNode;


/** Parser tree class. */
@SuppressWarnings("serial")
public class STParserTree extends JTree {
	/** The graph. */
	private STGraphExec graph;
	/** The model. */
	private DefaultTreeModel model;


	/** The Constructor.
	 * @param graph the graph */
	public STParserTree(final STGraphExec graph) {
		super();
		this.graph = graph;
		setBorder(BorderFactory.createLoweredBevelBorder());
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setOpenIcon(new ImageIcon(STGraph.getSTC().getBaseIcon("dot.png"))); //$NON-NLS-1$
		renderer.setLeafIcon(new ImageIcon(STGraph.getSTC().getBaseIcon("rdot.png"))); //$NON-NLS-1$
		setCellRenderer(renderer);
	}


	/** Set the graph.
	 * @param graph the graph */
	public void setGraph(final STGraphExec graph) { this.graph = graph; }


	/** Parse.
	 * @param expression the expression */
	public final void parse(final String expression) {
		STInterpreter interpreter = graph.getInterpreter();
		interpreter.parseExpression(expression);
		if(interpreter.getErrorInfo() != null) {
			model = new DefaultTreeModel(new DefaultMutableTreeNode("err")); //$NON-NLS-1$
		} else {
			SimpleNode parsedNode = (SimpleNode)interpreter.getTopNode();
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(getNodeName(parsedNode));
			model = new DefaultTreeModel(treeNode);
			insertNodes(parsedNode, treeNode);
		}
		setModel(model);
	}


	/** Insert node.
	 * @param topParsedNode the top parsed node
	 * @param topTreeNode the top tree node */
	final void insertNodes(final SimpleNode topParsedNode, final DefaultMutableTreeNode topTreeNode) {
		SimpleNode parsedNode;
		DefaultMutableTreeNode treeNode;
		for(int i = 0; i < topParsedNode.jjtGetNumChildren(); i++) {
			parsedNode = (SimpleNode)topParsedNode.jjtGetChild(i);
			treeNode = new DefaultMutableTreeNode(getNodeName(parsedNode));
			model.insertNodeInto(treeNode, topTreeNode, i);
			insertNodes(parsedNode, treeNode);
		}
	}


	/** Expand nodes.
	 * @param parent the parent */
	@SuppressWarnings("rawtypes")
	private void expandNodes(final TreePath parent) {
		TreeNode node = (TreeNode)parent.getLastPathComponent();
		if(node.getChildCount() >= 0) {
			for(Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode)e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandNodes(path);
			}
		}
		expandPath(parent);
	}


	/** Expand tree. */
	public final void expandTree() {
		TreeNode root = (TreeNode)getModel().getRoot();
		if(root == null) { return; }
		expandNodes(new TreePath(root));
	}


	/** Get node name.
	 * @param node the node
	 * @return name */
	final String getNodeName(final SimpleNode node) {
		if(node instanceof ASTConstant) { return ((ASTConstant)node).getValue().toString(); }
		if(node instanceof ASTFunNode) { return ((ASTFunNode)node).getName(); }
		if(node instanceof ASTVarNode) { return ((ASTVarNode)node).getName(); }
		return STTools.BLANK;
	}

}
