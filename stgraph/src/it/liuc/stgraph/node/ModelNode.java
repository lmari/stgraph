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
import it.liuc.stgraph.STFactory;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.util.STTools;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JTabbedPane;

import org.jgraph.graph.Port;


/** Submodel node. */
@SuppressWarnings("serial")
public class ModelNode extends STNode {
	/** The Constant PREFIX. */
	public static final String PREFIX = "MODELNODE"; //$NON-NLS-1$
	/** The pathname of the directory in which custom models are stored. */
	public static final String MODELPATH = STGraphC.getBasicProps().getProperty("MODEL.DIR"); //$NON-NLS-1$

	/** The edit dialog. */
	public static EditPanel4ModelNodes editDialog4ModelNodes;

	/** The desk of the submodel. */
	private transient STDesktop desk;
	/** Submodel name. */
	private String submodelName;
	/** Array of the names of input nodes of the submodel. */
	private String[] subVars;
	/** Array of the expressions saturating the submodel inputs. */
	private String[] superExpressions;
	/** Is the submodel visible? */
	private boolean subVisible = false;


	/** Class constructor. */
	public ModelNode() { this(null); }


	/** Class constructor.
	 * @param userObject the user object */
	public ModelNode(final Object userObject) { super(userObject); }


	/** Get the view for this node.
	 * @return view */
	@Override
	public NodeView getView() {
		if(view == null) { view = new ModelNodeView(this); }
		return view;
	}


	/** Get a new view for this node.
	 * @return view */
	@Override
	public NodeView getNewView() { return view = new ModelNodeView(this); }


	/** Get the dialog for this node.
	 * @return dialog */
	@Override
	public EditPanel4ModelNodes getDialog() {
		if(editDialog4ModelNodes == null) { editDialog4ModelNodes = new EditPanel4ModelNodes(); }
		return editDialog4ModelNodes;
	}


	/** Set the model name.
	 * @param modelName the modelName
	 * @param showModel show the model? */
	public void setModel(final String modelName, final boolean showModel) {
		STGraphC stc = STGraph.getSTC();
		setSubmodelName(modelName);
		STDesktop tempDesktop = stc.getCurrentDesktop();
		STDesktop newDesktop;
		STGraphExec tempGraph = stc.getCurrentGraph();
		STGraphExec topGraph;
		stc.setCurrentDesktop(newDesktop = new STDesktop(stc.getInputStream(getSubmodelFilename(), tempGraph.resource), getSubmodelFilename(), tempGraph.resource, tempGraph, this));
		setDesk(newDesktop);
		setSubVisible(showModel);
		topGraph = (STGraphExec)newDesktop.getGraph().getTopGraph();
		topGraph.setupModel();
		stc.setCurrentDesktop(tempDesktop);
		stc.setCurrentGraph(tempGraph);
		stc.refreshBars();
	}


	/** Return an HTML-formatted string with a description of this node.
	 * @return info */
	public String getInfo() {
		StringBuilder z = new StringBuilder(super.getInfo());
		try {
			z.append(STGraphC.getMessage("NODE.INFO.SUB_NAME") + ": <code>" + submodelName + "</code><br>");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

			z.append(STGraphC.getMessage("NODE.INFO.SUB_SATURATED") + ": <code>"); //$NON-NLS-1$ //$NON-NLS-2$
			if(getSuperExpressions() != null) {
				z.append("["); //$NON-NLS-1$
				for(int i = 0; i < getSuperExpressions().length; i++) {
					z.append(getSubVars()[i] + "=" + getSuperExpressions()[i]); //$NON-NLS-1$
					if(i < getSuperExpressions().length - 1) { z.append(", "); } //$NON-NLS-1$
				}
				z.append("]"); //$NON-NLS-1$
			}
			z.append("</code><br>"); //$NON-NLS-1$

			z.append(STGraphC.getMessage("NODE.INFO.SUB_INPUT") + ": <code>"); //$NON-NLS-1$ //$NON-NLS-2$
			if(getDesk() != null) {
				STNode[] nodes = getDesk().getGraph().getInputNodeList();
				for(int i = 0; i < nodes.length; i++) {
					z.append(nodes[i].getName());
					if(i < nodes.length - 1) { z.append(", "); } //$NON-NLS-1$
				}
			}
			z.append("</code><br>"); //$NON-NLS-1$

			z.append(STGraphC.getMessage("NODE.INFO.SUB_OUTPUT") + ": <code>"); //$NON-NLS-1$ //$NON-NLS-2$
			if(getDesk() != null) {
				STNode[] nodes = getDesk().getGraph().getOutputNodeList();
				for(int i = 0; i < nodes.length; i++) {
					z.append(nodes[i].getName());
					if(i < nodes.length - 1) { z.append(", "); } //$NON-NLS-1$
				}
			}
			z.append("</code><br>"); //$NON-NLS-1$
		} catch (Exception ex) { }
		return z.toString();
	}


	/** Set the desktop object of the submodel associated to this node.
	 * @param desk the desk */
	public final void setDesk(final STDesktop desk) { this.desk = desk; }


	/** Get the desktop object of the submodel associated to this node.
	 * @return desktop */
	public final STDesktop getDesk() { return desk; }


	/** Get the submodel associated to this node.
	 * @return graph */
	public final STGraphExec getSubmodel() {
		if(desk == null) { return null; }
		return desk.getGraph();
	}


	/** Set the submodel name.
	 * @param submodelName name */
	public final void setSubmodelName(final String submodelName) { this.submodelName = submodelName; }


	/** Get the submodel name.
	 * @return the model name */
	public final String getSubmodelName() { return submodelName; }


	/** Get the submodel filename.
	 * @return the submodel filename */
	public String getSubmodelFilename() {
		String name = getSubmodelName();
		if(name == null) { return null; }
		if(getNodeSubtype() == null) { return getGraph().contextName + System.getProperty("file.separator") + name; } //$NON-NLS-1$
		return MODELPATH + STTools.SLASH + name;
	}


	/** Set the array of the names of saturated input nodes of the submodel.
	 * @param subVars the array */
	public final void setSubVars(final String[] subVars) { this.subVars = subVars; }


	/** Get the array of the names of saturated input nodes of the submodel.
	 * @return the subVars */
	public final String[] getSubVars() { return subVars; }


	/** Set the array of the expressions saturating the submodel inputs.
	 * @param superExpressions the array */
	public final void setSuperExpressions(final String[] superExpressions) { this.superExpressions = superExpressions; }


	/** Get the array of the expressions saturating the submodel inputs.
	 * @return the superExpressions */
	public final String[] getSuperExpressions() { return superExpressions; }


	/** Set the visibility status of the submodel(s).
	 * @param subVisible the subVisible to set */
	public final void setSubVisible(final boolean subVisible) {
		this.subVisible = subVisible;
		try {
			String title = getDesk().getGraph().getTitle();
			STDesktop desk = getDesk();
			if(subVisible) {
				STGraphC.getMultiDesktop().addTab(title, desk);
			} else {
				for(int i = 0; i < STGraphC.getMultiDesktop().getTabCount(); i++) {
					if(STGraphC.getMultiDesktop().getTitleAt(i).equals(title)) {
						STGraphC.getMultiDesktop().removeTabAt(i);
						return;
					}
				}
			}
		} catch (Exception e) { ; }
	}


	/** Get the visibility status of the submodel(s).
	 * @return the subVisible */
	public final boolean isSubVisible() { return subVisible; }


	/** Set the current submodel node parameters.
	 * @param subGraph the sub graph
	 * @param superExpressions the super expressions
	 * @param subVars the sub vars
	 * @return result */
	public boolean setProps(final STGraphExec subGraph, final String[] superExpressions, final String[] subVars) {
		if(superExpressions != null) {
			boolean found;
			STNode[] subNodes = subGraph.getNodes();
			STNode inputNode = null;
			for(int i = 0; i < superExpressions.length; i++) { // for each expression x = y (input node of the subsystem = saturating expression of the supersystem)
				found = false;
				for(int j = 0; j < subNodes.length; j++) { // verify that x exists and is an input node
					inputNode = subNodes[j];
					if(inputNode.getName().equals(subVars[i])) {
						if(!inputNode.isInput()) { STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_NONINPUTVAR")); return false; } //$NON-NLS-1$
						found = true;
						break;
					}
				}
				if(!found) { STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_INPUTVARNOTFOUND")); return false; } //$NON-NLS-1$
			}
			setSuperExpressions(subGraph.superExpressions = superExpressions);
			setSubVars(subGraph.saturatedNodeNames = subVars);
		}
		return true;
	}


	/** Handle the event of node renaming.
	 * @param oldName the old name
	 * @param newName the new name */
	public void handleNodeRenaming(final String oldName, final String newName) {
		if(getDesk() != null) {
			STGraphExec g = getSubmodel();
			if(g.getTitle().endsWith(STTools.DOT + oldName)) {
				g.setTitle(); // reset the tab title
				if(subVisible) {
					JTabbedPane md = STGraphC.getMultiDesktop();
					md.setTitleAt(md.indexOfComponent(getDesk()), g.getTitle());
				}
			}
		}
		if(getSuperExpressions() == null) { return; }
		for(int i = 0; i < getSuperExpressions().length; i++) { getSuperExpressions()[i] = renameInExpression(getSuperExpressions()[i], oldName, newName); }
		setSuperExpressions(superExpressions);
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	public void prepareForSave() {
		String s;
		String[] ss;
		super.prepareForSave();
		if((s = getSubmodelName()) != null) { dataToSave.put(STNode.PROP_SUBMODELNAME, s); }
		if((ss = getSuperExpressions()) != null && ss.length > 0) {
			for(int i = 0; i < ss.length; i++) { dataToSave.put(STNode.PROP_SUPEREXPRESSION + i, ss[i]); }
		}
		if((ss = getSubVars()) != null && ss.length > 0) {
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < ss.length; i++) { sb.append(ss[i] + ","); } //$NON-NLS-1$
			s = sb.toString();
			dataToSave.put(STNode.PROP_SUBVARNAMES, s.substring(0, s.length() - 1));
		}
		dataToSave.put(STNode.PROP_SUBMODELVISIBLE, Boolean.valueOf(subVisible));
	}


	/** Define the data of this node from the loaded attribute map. */
	public void restoreAfterLoad() {
		String s;
		super.restoreAfterLoad();
		if((s = (String)dataToSave.get(STNode.PROP_SUBMODELNAME)) != null) { setSubmodelName(s); }

		int i = 0;
		Vector<String> ss = new Vector<String>();
		while((s = (String)dataToSave.get(STNode.PROP_SUPEREXPRESSION + i)) != null) {
			ss.add(s);
			i++;
		}
		if(ss.size() > 0) { setSuperExpressions(ss.toArray(new String[ss.size()])); }

		if((s = (String)dataToSave.get(STNode.PROP_SUBVARNAMES)) != null) { setSubVars(s.split(STTools.COMMA)); }
		setSubVisible(Boolean.parseBoolean((String)dataToSave.get(STNode.PROP_SUBMODELVISIBLE)));
	}


	/** Define the protocol of the correctness for this node type.
	 * @param interpreter the interpreter
	 * @return error */
	public String checkDefinition(final STInterpreter interpreter) { 
		if(getSubmodelName() == null) {
			setValid(false);
			return getName() + ": " + STGraphC.getMessage("ERR.EMPTY_SUBSYSTEM"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if(getDesk() == null) {
			setValid(false);
			return null;
		}
		if(superExpressions != null) {
			String result;
			for(String s : superExpressions) {
				if(!STTools.isEmpty(s)) {
					if((result = checkExpressionDefinition(interpreter, s, STNode.PROP_EXPRESSION)) != null) { setValid(false); return result; }
				}
			}
		}
		setValid(true);
		return null;
	}


	/** Definition checker.
	 * @param var the var
	 * @param defType the def type
	 * @return error */
	public String checkVariable(final String var, final String defType) {
		if(var.startsWith(STTools.UNDERSCORE)) { return null; } //TODO [batch exec] this deals with both recursive user-defined functions (whose name starts with '_') and batch vars
		for(String s : STInterpreter.getPrivateVars()) {
			if(var.equals(s)) { return null; }
		}
		if(var.equals(STInterpreter.THIS)) { return STGraphC.getMessage("ERR.THIS_NOT_ALLOWED"); } //$NON-NLS-1$
		if(var.equals(STInterpreter.ME)) { return STGraphC.getMessage("ERR.ME_NOT_ALLOWED"); } //$NON-NLS-1$
		STNode[] nodes = getGraph().getInputNodeList();
		if(nodes != null) {
			for(STNode node : nodes) {
				if(node.getName().equals(var) && node.isGlobal()) { return null; } // allow a reference to a global var, but in this case prevent this to be an input
			}
		}
		ArrayList<STNode> ob = getDefiningNodesByEdges();
		if(ob == null) { return STGraphC.getMessage("ERR.NON_CONNECTED_NODE") + " (" + var + ")"; } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int x = var.indexOf(STTools.DOT);
		if(x == -1) { // standard variable
			for(STNode n : ob) {
				if(n.getName().equals(var)) { return null; }
			}
			return STGraphC.getMessage("ERR.NON_CONNECTED_NODE") + " (" + var + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		//subsystem output variable (in the system.var format)
		String var2 = var.substring(0, x);
		for(STNode n : ob) {
			if(n.getName().equals(var2)) { return null; }
		}
		return STGraphC.getMessage("ERR.NON_CONNECTED_NODE") + " (" + var + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}


	/** Create a generic wrapper with input, saturating, nodes and output nodes for the input and the output nodes of the submodel. */
	public final void createWrapper() {
		if(getDesk() == null) { return; }

		Rectangle2D bounds = getBounds();
		Rectangle box = ValueNodeView.getDefaultNodeBounds();
		int newPosX = Math.max(0, (int)bounds.getX() - (box.width + 50));
		int newPosY = (int)bounds.getY();
		// inputs
		ValueNode newNode;
		STNode[] nodes = getDesk().getGraph().getInputNodeList();
		String[] superNames = new String[nodes.length];
		String[] subNames = new String[nodes.length];
		for(int i = 0; i < nodes.length; i++) {
			subNames[i] = nodes[i].getName();
			newNode = (ValueNode)STFactory.nodeCreate(nodes[i].getName(), STNode.NODE_VALUE);
			superNames[i] = newNode.getName();
			newNode.setExpression("0"); //$NON-NLS-1$
			newNode.setFormattedExpression("0"); //$NON-NLS-1$
			STFactory.edgeCreate(STTools.BLANK, (Port)newNode.getFirstChild(), (Port)getFirstChild());
			newNode.setPosition(newPosX, newPosY);
			newPosY += box.height + 20;
		}
		setProps(getDesk().getGraph(), superNames, subNames);
		//setSubVisible(isSubVisibile());

		newPosX = (int)bounds.getX() + (int)bounds.getWidth() + 50;
		newPosY = (int)bounds.getY();
		// outputs
		String superName = getName() + STTools.DOT;
		nodes = getDesk().getGraph().getOutputNodeList();
		for(int i = 0; i < nodes.length; i++) {
			newNode = (ValueNode)STFactory.nodeCreate(nodes[i].getName(), STNode.NODE_VALUE);
			STFactory.edgeCreate(STTools.BLANK, (Port)getFirstChild(), (Port)newNode.getFirstChild());
			newNode.setExpression(superName + nodes[i].getName());
			newNode.setFormattedExpression(superName + nodes[i].getName());
			newNode.setOutput(true);
			newNode.setPosition(newPosX, newPosY);
			newPosY += box.height + 20;
		}
		STTools.moveNodes(0, 1, false); // usual crazy trick...
		getGraph().setSelectionCell(this);
	}

}
