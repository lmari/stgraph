/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2021, Luca Mari.
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

import java.util.ArrayList;

import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.widget.InputWidget;
import it.liuc.stgraph.widget.STWidget;


/** Variable node, for both algebraic and state nodes. */
public class ValueNode extends STNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The Constant PREFIX. */
	public static final String PREFIX = "VALUENODE"; //$NON-NLS-1$
	/** The value node type. */
	private int valueNodeType = 0; // default: 0: algebraic; 1: state; 2: state with output
	/** The Constant VALUENODE_VAR. */
	public final static int VALUENODE_ALGEBRAIC = 0;
	/** The Constant VALUENODE_STATE. */
	public final static int VALUENODE_STATE = 1;
	/** The Constant VALUENODE_STATE_OUT. */
	public final static int VALUENODE_STATE_OUT = 2;

	/** The edit dialog. */
	public static EditPanel4ValueNodes editDialog4ValueNodes;

	/** The state init. */
	private String stateInit = STTools.BLANK; 
	/** The formatted state init. */
	private String formattedStateInit = STTools.BLANK; 
	/** The state transition. */
	private String stateTransition = STTools.BLANK;
	/** The formatted state transition. */
	private String formattedStateTransition = STTools.BLANK; 
	/** The expression. */
	private String expression = STTools.BLANK;
	/** The formatted expression. */
	private String formattedExpression = STTools.BLANK; 
	/** The current state. */
	private transient Object currentState;
	/** The next state. */
	private transient Object nextState;


	/** Class constructor. */
	public ValueNode() { this(null); }


	/** Class constructor.
	 * @param userObject the user object */
	public ValueNode(final Object userObject) { super(userObject); }


	/** Get the view for this node.
	 * @return view */
	@Override
	public NodeView getView() {
		if(view == null) { view = new ValueNodeView(this); }
		return view;
	}


	/** Get a new view for this node.
	 * @return view */
	@Override
	public NodeView getNewView() { return view = new ValueNodeView(this); }


	/** Get the dialog for this node.
	 * @return dialog */
	@Override
	public EditPanel4ValueNodes getDialog() {
		if(editDialog4ValueNodes == null) { editDialog4ValueNodes = new EditPanel4ValueNodes(); }
		return editDialog4ValueNodes;
	}


	/** Set the value node type.
	 * @param valueNodeType the valueNodeType */
	public final void setValueNodeType(final int valueNodeType) { this.valueNodeType = valueNodeType; }


	/** Get the value node type.
	 * @return the valueNodeType */
	public final int getValueNodeType() { return valueNodeType; }


	/** Return an HTML-formatted string with a description of this node.
	 * @return info */
	@Override public String getInfo() {
		Object o;
		StringBuffer z = new StringBuffer(super.getInfo());
		int x = getValueNodeType();
		/*
		if(x != ValueNode.VALUENODE_ALGEBRAIC) {
			z.append(STGraphC.getMessage("NODE.INFO.STATE_INIT") + ": <code>" + STData.toHTML(getStateInit()) + "</code><br>");
			z.append(STGraphC.getMessage("NODE.INFO.STATE_TRANS") + ": <code>" + STData.toHTML(getStateTransition()) + "</code><br>");
		}
		if(x != ValueNode.VALUENODE_STATE) {
			z.append(STGraphC.getMessage("NODE.INFO.OUTPUT") + ": <code>" + STData.toHTML(getExpression()) + "</code>");
		}
		z.append("<hr>");
		if(getEvalErrDescription() != null) {
			z.append("<font color='red'>" + getEvalErrDescription() + "</font>");
		} else {
			if(x != ValueNode.VALUENODE_ALGEBRAIC) {
				o = getCurrentState();
				z.append(STGraphC.getMessage("NODE.INFO.STATE_VAL") + " <code>[" + STData.getTypeAsString(o) + "]: " + STData.toString(o, STData.FORMAT_ALTERNATE) + "</code><br>");
				o = getNextState();
				z.append(STGraphC.getMessage("NODE.INFO.NEXTSTATE_VAL") + ": <code>[" + STData.getTypeAsString(o) + "]: " + STData.toString(o, STData.FORMAT_ALTERNATE) + "</code><br>");
			}
			if(x != ValueNode.VALUENODE_STATE) {
				o = getValue();
				z.append(STGraphC.getMessage("NODE.INFO.VALUE") + ": <code>[" + STData.getTypeAsString(o) + "]: " + STData.toString(o, STData.FORMAT_ALTERNATE) + "</code><br>");
			}
		}
		*/
		if(x == ValueNode.VALUENODE_ALGEBRAIC) {
			z.append(STGraphC.getMessage("NODE.INFO.OUTPUT") + ": <code>" + STData.toHTML(getExpression()) + "</code>");
		} else if(x == ValueNode.VALUENODE_STATE) {
			z.append(STGraphC.getMessage("NODE.INFO.STATE_INIT") + ": <code>" + STData.toHTML(getStateInit()) + "</code><br>");
			z.append(STGraphC.getMessage("NODE.INFO.STATE_TRANS") + ": <code>" + STData.toHTML(getStateTransition()) + "</code><br>");
		} else {
			z.append(STGraphC.getMessage("NODE.INFO.STATE_INIT") + ": <code>" + STData.toHTML(getStateInit()) + "</code><br>");
			z.append(STGraphC.getMessage("NODE.INFO.STATE_TRANS") + ": <code>" + STData.toHTML(getStateTransition()) + "</code><br>");
			z.append(STGraphC.getMessage("NODE.INFO.OUTPUT") + ": <code>" + STData.toHTML(getExpression()) + "</code>");
		}
		z.append("<hr>");
		if(getEvalErrDescription() != null) {
			z.append("<font color='red'>" + getEvalErrDescription() + "</font>");
		} else {
			o = getValue();
			z.append(STGraphC.getMessage("NODE.INFO.VALUE") + ": <code>[" + STData.getTypeAsString(o) + "]: " + STData.toString(o, STData.FORMAT_ALTERNATE) + "</code><br>");
			if(x != ValueNode.VALUENODE_ALGEBRAIC) {
				o = getCurrentState();
				z.append(STGraphC.getMessage("NODE.INFO.STATE_VAL") + " <code>[" + STData.getTypeAsString(o) + "]: " + STData.toString(o, STData.FORMAT_ALTERNATE) + "</code><br>");
				o = getNextState();
				z.append(STGraphC.getMessage("NODE.INFO.NEXTSTATE_VAL") + ": <code>[" + STData.getTypeAsString(o) + "]: " + STData.toString(o, STData.FORMAT_ALTERNATE) + "</code><br>");
			}
		}
		
		return z.toString();
	}


	/** Handle the event of node renaming.
	 * @param newName the new name
	 * @param oldName the old name */
	public void handleNodeRenaming(final String oldName, final String newName) {
		String expr = getStateInit();
		if(!STTools.isEmpty(expr)) { setStateInit(renameInExpression(expr, oldName, newName)); }
		expr = getFormattedStateInit();
		if(!STTools.isEmpty(expr)) { setFormattedStateInit(renameInExpression(expr, oldName, newName)); }
		expr = getStateTransition();
		if(!STTools.isEmpty(expr)) { setStateTransition(renameInExpression(expr, oldName, newName)); }
		expr = getFormattedStateTransition();
		if(!STTools.isEmpty(expr)) { setFormattedStateTransition(renameInExpression(expr, oldName, newName)); }
		expr = getExpression();
		if(!STTools.isEmpty(expr)) { setExpression(renameInExpression(expr, oldName, newName)); }
		expr = getFormattedExpression();
		if(!STTools.isEmpty(expr)) { setFormattedExpression(renameInExpression(expr, oldName, newName)); }
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	@Override
	public void prepareForSave() {
		super.prepareForSave();
		dataToSave.put(STNode.PROP_VALUETYPE, Integer.valueOf(getValueNodeType()));
		dataToSave.put(STNode.PROP_ISINPUT, Boolean.valueOf(isInput()));
		dataToSave.put(STNode.PROP_ISGLOBAL, Boolean.valueOf(isGlobal()));
		dataToSave.put(STNode.PROP_ISOUTPUT, Boolean.valueOf(isOutput()));
		dataToSave.put(STNode.PROP_ISVECTOROUTPUT, Boolean.valueOf(isVectorOutput()));
		dataToSave.put(STNode.PROP_INITSTATE_F, getFormattedStateInit());
		dataToSave.put(STNode.PROP_STATETRANSITION_F, getFormattedStateTransition());
		dataToSave.put(STNode.PROP_EXPRESSION_F, getFormattedExpression());
		/* #### comment / remove to avoid unformatted fields to be stored in the stg file */
		dataToSave.put(STNode.PROP_INITSTATE, getStateInit());
		dataToSave.put(STNode.PROP_STATETRANSITION, getStateTransition());
		dataToSave.put(STNode.PROP_EXPRESSION, getExpression());
		/**/
	}


	/** Define the data of this node from the loaded attribute map. */
	@Override
	public void restoreAfterLoad() {
		super.restoreAfterLoad();
		String x = (String)dataToSave.get(STNode.PROP_VALUETYPE);
		if(x != null) { setValueNodeType(Integer.parseInt(x)); }
		setInput(Boolean.parseBoolean((String)dataToSave.get(STNode.PROP_ISINPUT)));
		setGlobal(Boolean.parseBoolean((String)dataToSave.get(STNode.PROP_ISGLOBAL)));
		setOutput(Boolean.parseBoolean((String)dataToSave.get(STNode.PROP_ISOUTPUT)));
		setVectorOutput(Boolean.parseBoolean((String)dataToSave.get(STNode.PROP_ISVECTOROUTPUT)));
		setFormattedStateInit((String)dataToSave.get(STNode.PROP_INITSTATE_F));
		setFormattedStateTransition((String)dataToSave.get(STNode.PROP_STATETRANSITION_F));
		setFormattedExpression((String)dataToSave.get(STNode.PROP_EXPRESSION_F));

		/* #### un comment to avoid unformatted fields to be stored in the stg file
		setStateInit(TokenAwareEditor.cleanText(getFormattedStateInit()));
		setStateTransition(TokenAwareEditor.cleanText(getFormattedStateTransition()));
		setExpression(TokenAwareEditor.cleanText(getFormattedExpression()));
		 */

		/* #### comment / remove to avoid unformatted fields to be stored in the stg file */
		setStateInit((String)dataToSave.get(STNode.PROP_INITSTATE));
		setStateTransition((String)dataToSave.get(STNode.PROP_STATETRANSITION));
		setExpression((String)dataToSave.get(STNode.PROP_EXPRESSION));
		/**/
	}


	/** Define the protocol of the correctness for this node type.
	 * @param interpreter the interpreter
	 * @return error */
	public final String checkDefinition(final STInterpreter interpreter) {
		String def;
		String result;
		int x = getValueNodeType();
		if(x != ValueNode.VALUENODE_ALGEBRAIC) {
			if(STTools.isEmpty(def = getStateInit())) { setValid(false); return getName() + ": " + STGraphC.getMessage("ERR.EMPTY_STATEINIT"); } //$NON-NLS-1$ //$NON-NLS-2$
			if((result = checkExpressionDefinition(interpreter, def, STNode.PROP_INITSTATE)) != null) { setValid(false); return result; }

			if(STTools.isEmpty(def = getStateTransition())) { setValid(false); return getName() + ": " + STGraphC.getMessage("ERR.EMPTY_STATETRANSITION"); } //$NON-NLS-1$ //$NON-NLS-2$
			if((result = checkExpressionDefinition(interpreter, def, STNode.PROP_STATETRANSITION)) != null) { setValid(false); return result; }
		}
		if(x != ValueNode.VALUENODE_STATE) {
			if(STTools.isEmpty(def = getExpression())) { setValid(false); return getName() + ": " + STGraphC.getMessage("ERR.EMPTY_EXPRESSION"); } //$NON-NLS-1$ //$NON-NLS-2$
			if((result = checkExpressionDefinition(interpreter, def, STNode.PROP_EXPRESSION)) != null) { 
				setValid(false);
				return result; }
		}
		setValid(true);
		if(x == ValueNode.VALUENODE_ALGEBRAIC) { setInput(); }
		return null;
	}


	/** Definition checker.
	 * @param var the variable to be checked
	 * @param defType the def type
	 * @return error */
	public final String checkVariable(final String var, final String defType) {
		if(var.startsWith(STTools.UNDERSCORE)) { return null; } //TODO [batch exec] this deals with both recursive user-defined functions (whose name starts with '_') and batch vars
		for(String s : STInterpreter.getPrivateVars()) {
			if(var.equals(s)) { return null; }
		}
		if(var.equals(STInterpreter.THIS)) {
			if(defType.equals(STNode.PROP_STATETRANSITION) || (defType.equals(STNode.PROP_EXPRESSION) && getValueNodeType() != ValueNode.VALUENODE_ALGEBRAIC)) { return null; }
			return STGraphC.getMessage("ERR.THIS_NOT_ALLOWED"); //$NON-NLS-1$
		}
		if(var.equals(STInterpreter.ME)) {
			if(defType.equals(STNode.PROP_EXPRESSION) && isStateWithOutput()) { return null; }
			return STGraphC.getMessage("ERR.ME_NOT_ALLOWED"); //$NON-NLS-1$
		}
		STNode[] nodes = getGraph().getNodes();
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
		String v1 = var.substring(0, x);
		String v2 = var.substring(x + 1);
		for(STNode n : ob) {
			if(n.getName().equals(v2)) {
				ModelNode m = n.getGraph().getSuperNode();
				if(m != null && m.getName().equals(v1)) { return null; }
			}
		}
		return STGraphC.getMessage("ERR.NON_CONNECTED_NODE") + " (" + var + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}


	/** Set the initial state of this node. */
	public void setStateInit() { ; }


	/** Set the initial state of this node.
	 * @param stateInit the state init */
	public final void setStateInit(final String stateInit) { this.stateInit = stateInit; }


	/** Get the initial state of this node.
	 * @return the stateInit */
	public final String getStateInit() { return stateInit != null ? stateInit : STTools.BLANK; }


	/** Get the initial state of this node for web.
	 * @return the stateInit */
	public final String getStateInitForWeb() {
		if(getGraph().isTopGraph()) {
			return stateInit != null ? stateInit : STTools.BLANK;
		}
		STGraphImpl graph = getGraph();
		STNode node = graph.getNodeByName(stateInit);
		String res = STTools.BLANK;
		while(!graph.isTopGraph()) {
			res = ((ValueNode)node).getSaturatingExpression();
			graph = graph.getSuperGraph();
			node = graph.getNodeByName(res);
		}
		return res;
	}


	/** Set the formatted initial state of this node.
	 * @param formattedStateInit the formatted state init */
	public final void setFormattedStateInit(final String formattedStateInit) { this.formattedStateInit = formattedStateInit; }


	/** Get the formatted initial state of this node.
	 * @return formattedStateInit */
	public final String getFormattedStateInit() { return formattedStateInit != null ? formattedStateInit : STTools.BLANK; }


	/** Set the state transition expression for this node. */
	public void setStateTransition() { ; }


	/** Set the state transition expression for this node.
	 * @param stateTransition the state transition */
	public final void setStateTransition(final String stateTransition) { this.stateTransition = stateTransition; }


	/** Get the state transition expression for this node.
	 * @return stateTransition */
	public final String getStateTransition() { return stateTransition != null ? stateTransition : STTools.BLANK; }


	/** Set the formatted state transition expression for this node.
	 * @param formattedStateTransition the formatted state transition */
	public final void setFormattedStateTransition(final String formattedStateTransition) { this.formattedStateTransition = formattedStateTransition; }


	/** Get the formatted state transition expression for this node.
	 * @return formattedStateTransition */
	public final String getFormattedStateTransition() { return formattedStateTransition != null ? formattedStateTransition : STTools.BLANK; }


	/** Set the defining expression for this node. */
	public void setExpression() { ; }


	/** Set the defining expression for this node.
	 * @param expression the expression */
	public final void setExpression(final String expression) {
		this.expression = expression; }


	/** Get the defining expression for this node.
	 * @return expression */
	public final String getExpression() { return expression != null ? expression : STTools.BLANK; }


	/** Set the defining formatted expression for this node.
	 * @param formattedExpression the formatted expression */
	public void setFormattedExpression(final String formattedExpression) { this.formattedExpression = formattedExpression; }


	/** Get the defining formatted expression for this node.
	 * @return expression */
	public String getFormattedExpression() { return formattedExpression != null ? formattedExpression : STTools.BLANK; }


	/** Set the current state of this node.
	 * @param currentState the current state */
	public final void setCurrentState(final Object currentState) { this.currentState = currentState; }


	/** Get the current state of this node.
	 * @return currentState */
	public final Object getCurrentState() { return currentState; }


	/** Set the next state of this node.
	 * @param nextState the next state */
	public final void setNextState(final Object nextState) { this.nextState = nextState; }


	/** Get the next state of this node.
	 * @return nextState */
	public final Object getNextState() { return nextState; }


	/** Get whether this node defines a custom function.
	 * @return result */
	public final boolean isCustomFunction() { return getName().startsWith(STTools.UNDERSCORE) && getExpression().startsWith("function("); } //$NON-NLS-1$


	/** Bind this node to an external value supplier, operating by calling <code>setValue()</code>. */
	public final void bindToExternalControl() {
		if(isInput()) { setInputWidget("DIRECT"); } //$NON-NLS-1$
	}


	/** Unbind this node to a previously bound external value supplier. */
	public final void unbindToExternalControl() {
		if(isInput()) { setInputWidget(null); }
	}


	/** Bind this node to an input widget as value supplier.
	 * @param widget the widget */
	public final void bindToWidget(final InputWidget widget) {
		try {
			STWidget w = (STWidget)widget;
			w.setProperty(InputWidget.PROP_SOURCE_NA, getName());
			w.setProperty(InputWidget.PROP_SOURCE_OB, this);
		} catch (Exception e) { ; }
		setInputWidget(widget);
		if(STTools.isEmpty(getFormattedExpression())) { setFormattedExpression("0"); } // just to be sure that something is there... //$NON-NLS-1$
		Object v = widget.getValue();
		if(v != null) { setExpression(v.toString()); }
	}


	/** Unbind this node from an input widget as value supplier. */
	public final void unbindFromWidget() {
		setInputWidget(null);
		String t = getFormattedExpression();
		//t = t.replaceAll(TokenAwareEditor.RETURN, STTools.BLANK).replaceAll(TokenAwareEditor.BLANK, STTools.BLANK);
		setExpression(t);
	}


	/** Get the value of this node.
	 * @return value */
	@Override
	public final Object getValue() {
		if(isInput()) {
			Object inputObj = null;
			if(isInput() && (inputObj = getInputWidget()) != null && inputObj instanceof InputWidget) { return ((InputWidget)inputObj).getValue(); }
			return super.getValue();
		}
		return super.getValue();
	}


	/** Set the value of this node.
	 * @param _value the _value */
	@Override
	public final void setValue(final Object _value) {
		super.setValue(_value);
		if(isInput()) {
			Object inputObj = null;
			if(_value != null && (inputObj = getInputWidget()) != null && inputObj instanceof InputWidget) { setExpression(_value.toString()); }
		}
	}

}
