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

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;

import it.liuc.stgraph.fun.CustomFun2;
import it.liuc.stgraph.fun.FunctionMenu;
import it.liuc.stgraph.fun.STFunction;
import it.liuc.stgraph.fun.STParserTools;
import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.userfun.UserFunReader;
import it.liuc.stgraph.util.STTools;

import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.OperatorSet;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.Parser;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.type.Tensor;


/** Customize the behavior of the JEP interpreter. */
@SuppressWarnings("serial")
public class STInterpreter extends JEP implements Serializable {
	/** The functions. */
	private transient STFunction[] functions;
	/** The custom functions. */
	private transient ArrayList<CustomFun2> customFunctions;
	/** The function menus. */
	private transient FunctionMenu[] functionMenus;
	/** The custom function menus. */
	private transient HashMap<String, ArrayList<CustomFun2>> customFunctionMenus;
	/** The system variables. */
	private static transient ArrayList<String> systemVars = null;
	/** The descriptions of the custom functions. */
	private static transient HashMap<String, String> customFunDescr = new HashMap<String, String>();
	/** The private variables. */
	private static transient String[] privateVars = { "$0", "$1", "$a0", "$a1", "$a2", "$a3", "$i", "$numArgs", "$i0", "$i1", "$i2", "$i3", "$v0", "$v1", "$v2", "$v3", "$w0", "$w1", "$w2", "$w3", "$p0", "$p1", "$p2" };
	public static final String THIS = "this"; //$NON-NLS-1$
	public static final String ME = "me"; //$NON-NLS-1$
	/** The shared current number of parameters, allowing re-entrant calls. */ 
	public static Stack<Tensor> cnop;
	/** The shared current number of defined ({...}) subexpressions, allowing re-entrant calls. */ 
	public static Stack<Tensor> nde;
	/** The sequential number of defined ({...}) subexpressions. */
	public static int numDefExpr;
	/** The number of dummy parameters for custom functions. */ 
	public static final int NUM_DUMMY_PARS = 4;
	/** The locale, currently "en" or "it". */
	private static String locale = STGraphC.getSTLocale().toString();


	/** Class constructor.
	 * @param _functions the functions */
	@SuppressWarnings("unchecked")
	public STInterpreter(final List<Object> _functions) {
		super();
		setMessageBundle(STGraphC.getSTLocale()); // to localize JEP messages
		addStandardFunctions();
		addStandardConstants();
		allowAssignment = false;

		OperatorSet operatorSet = getOperatorSet();
		operatorSet.getAdd().setPFMC(new it.liuc.stgraph.fun.OpAddition());
		operatorSet.getSubtract().setPFMC(new it.liuc.stgraph.fun.OpDifference());
		operatorSet.getMultiply().setPFMC(new it.liuc.stgraph.fun.OpProduct());
		operatorSet.getDivide().setPFMC(new it.liuc.stgraph.fun.OpQuotient());
		operatorSet.getUMinus().setPFMC(new it.liuc.stgraph.fun.OpUMinus());
		operatorSet.getSize().setPFMC(new it.liuc.stgraph.fun.Size());
		operatorSet.getPower().setPFMC(new it.liuc.stgraph.fun.OpPower());
		operatorSet.getMod().setPFMC(new it.liuc.stgraph.fun.OpModulus());
		operatorSet.getLT().setPFMC(new it.liuc.stgraph.fun.OpLT());
		operatorSet.getLE().setPFMC(new it.liuc.stgraph.fun.OpLE());
		operatorSet.getGT().setPFMC(new it.liuc.stgraph.fun.OpGT());
		operatorSet.getGE().setPFMC(new it.liuc.stgraph.fun.OpGE());
		operatorSet.getEQ().setPFMC(new it.liuc.stgraph.fun.OpEQ());
		operatorSet.getNE().setPFMC(new it.liuc.stgraph.fun.OpNE());
		operatorSet.getAnd().setPFMC(new it.liuc.stgraph.fun.OpAnd());
		operatorSet.getOr().setPFMC(new it.liuc.stgraph.fun.OpOr());
		operatorSet.getNot().setPFMC(new it.liuc.stgraph.fun.OpNot());
		operatorSet.getList().setPFMC(new it.liuc.stgraph.fun.OpList());
		operatorSet.getList2().setPFMC(new it.liuc.stgraph.fun.OpList2());
		operatorSet.getDefExpr().setPFMC(new it.liuc.stgraph.fun.OpDefExpr());
		operatorSet.getConcatenate().setPFMC(new it.liuc.stgraph.fun.Conc());
		operatorSet.getDecatenate().setPFMC(new it.liuc.stgraph.fun.Dec());

		setCustomFunctions();
		if(customFunctions != null) { _functions.addAll(customFunctions); }
		functions = _functions.toArray(new STFunction[_functions.size()]);
		for(STFunction function : functions) { addFunction(function.getName(), function); }
		
		Arrays.sort(functions, new FunctionComparator());
		
		setPrivateVars();
		if(customFunctions != null) {
			for(CustomFun2 ff : customFunctions) { ff.preParse(); }
		}
		addVariable(THIS, 0.0);
		addVariable(ME, 0.0);
		errorList.removeAllElements();
	}


	/** Comparator for custom functions. */
	@SuppressWarnings("rawtypes")
	public class FunctionComparator implements Comparator {

		/** Comparison criterion.
		 * @param o1 the o1
		 * @param o2 the o2
		 * @return int */
		public final int compare(final Object o1, final Object o2) {
			String p1 = o1.toString().toLowerCase();
			String p2 = o2.toString().toLowerCase();
			return p1.compareTo(p2);
		}
	}


	/** Read the definition of the custom functions from the corresponding files,
	 * and set the related lists. */
	private final void setCustomFunctions() {
		File[] files = UserFunReader.getFiles();
		if(files == null || files.length == 0) { return; }
		customFunctions = new ArrayList<CustomFun2>();
		customFunctionMenus = new HashMap<String, ArrayList<CustomFun2>>();
		for(File file : files) { setCustomFunctionsHelper(file.getName()); }
		//Collections.sort(customFunctions, new FunctionComparator());
		//for(String m : customFunctionMenus.keySet()) { Collections.sort(customFunctionMenus.get(m), new FunctionComparator()); }
	}


	/** Helper for the <code>setCustomFunctions</code> method. */
	private final void setCustomFunctionsHelper(final String fileName) {
		Properties functionProps = new Properties();
		try {
			InputStream is = UserFunReader.getFile(fileName);
			String s = STTools.stream2String(is);
			s = s.replaceAll("\\&", "\\&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
			s = s.replaceAll("\\&amp;lt;", "\\&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
			is = STTools.string2Stream(s);
			functionProps.loadFromXML(is);
		} catch (Exception e) {
			System.err.println("Error in reading functions from " + fileName + ": " + e.getMessage());  //$NON-NLS-1$//$NON-NLS-2$
			return;
		}
		if(functionProps.size() == 0) { return; }

		String locmenus[];
		String menu;
		String id;
		String def;
		int pos;
		String locdescs[];
		String desc;
		CustomFun2 f;

		for(Map.Entry<Object, Object> entry : functionProps.entrySet()) {
			String s = (String)entry.getKey();
			int i = s.indexOf("|"); //$NON-NLS-1$
			if(i == -1) {
				System.err.println("Error in reading function " + s + " from " + fileName);  //$NON-NLS-1$//$NON-NLS-2$
				return;
			}
			menu = null;
			locmenus = s.substring(0, i).trim().split("__"); //$NON-NLS-1$
			for(String t : locmenus) {
				if(t.startsWith(locale)) { menu = t.substring(locale.length() + 1); }
			}
			if(menu == null) { menu = locmenus[0]; }
			id = s.substring(i + 1).trim();
			def = (String)entry.getValue();
			if((pos = def.indexOf("//")) != -1) { //$NON-NLS-1$
				desc = null;
				locdescs = def.substring(pos + locale.length() + 1).trim().split("__"); //$NON-NLS-1$
				for(String t : locdescs) {
					if(t.trim().startsWith(locale)) { desc = t.trim().substring(locale.length() + 1); }
				}
				if(desc == null) { desc = locdescs[0]; }
				def = def.substring(0, pos).trim();
			} else {
				desc = STTools.BLANK;
			}
			customFunctions.add(f = new CustomFun2(this, id, def));
			customFunDescr.put(id, desc);
			if(!customFunctionMenus.containsKey(menu)) { customFunctionMenus.put(menu, new ArrayList<CustomFun2>()); }
			customFunctionMenus.get(menu).add(f);
		}
	}


	/** Comparator for custom functions. */
	//public class FunctionComparator implements Comparator {

		/** Comparison criterion.
		 * @param o1 the o1
		 * @param o2 the o2
		 * @return int */
		/*
	 	public final int compare(final Object o1, final Object o2) {
			String p1 = ((CustomFun2)o1).getName();
			String p2 = ((CustomFun2)o2).getName();
			return p1.compareTo(p2);
		}
		 */
	//}
	


	/** Set private variables. */
	public void setPrivateVars() {
		for(String s : privateVars) { addVariable(s, 0); }
	}


	/** Reset private variables. */
	public void resetPrivateVars() {
		for(String s : privateVars) { symTab.setVarValue(s, new Tensor()); }
		numDefExpr = 0;
	}


	/** Get the array of the private variables.
	 * @return privateVars */
	public static String[] getPrivateVars() { return privateVars; }


	/** Set the various menus of functions.
	 * @param _functionMenus the function menus */
	public final void setFunctionMenus(final FunctionMenu[] _functionMenus) {
		FunctionMenu firstMenu = new FunctionMenu(STGraphC.getMessage("NODE.DIALOG.ALLFUNCTIONS"), getFunctions()); //$NON-NLS-1$
		functionMenus = new FunctionMenu[_functionMenus.length + 1];
		functionMenus[0] = firstMenu;
		for(int i = 0; i < _functionMenus.length; i++) { functionMenus[i + 1] = _functionMenus[i]; }
	}


	/** Get the function menus.
	 * @return menus */
	public final FunctionMenu[] getFunctionMenus() { return functionMenus; }


	/** Set the optional menu(s) of custom functions.
	 * @param dummy a dummy parameter, due to the Spring constraint of having at least one parameter in injected setters */
	public final void setCustomFunctionMenus(final FunctionMenu[] dummy) {
		// this maintains the custom functions in their own menus...
		/*
		if(customFunctions == null) { return; }
		FunctionMenu[] t = functionMenus;
		functionMenus = new FunctionMenu[t.length + customFunctionMenus.size()];
		for(int i = 0; i < t.length; i++) { functionMenus[i] = t[i]; }
		int i = t.length;
		for(String m : customFunctionMenus.keySet()) {
			functionMenus[i++] = new FunctionMenu(m, customFunctionMenus.get(m).toArray());
		}
		*/
		// ... whereas this put them in the standard menus
		if(customFunctions == null) { return; }
		FunctionMenu targetMenu = null;
		boolean found;
		for(String cfm : customFunctionMenus.keySet()) {
			found = false;
			for(FunctionMenu fm : functionMenus) {
				if(fm.toString().equals(cfm)) {
					targetMenu = fm;
					found = true;
					break;
				}
			}
			if(found) { 
				targetMenu.addItems(customFunctionMenus.get(cfm).toArray());
			} else { System.err.println("Custom function menu " + cfm + " not found"); } //$NON-NLS-1$ //$NON-NLS-2$
		}
	}


	/** Get the message with the specified id, as read from the file.
	 * <br>It operates as a wrapper of the static method STGraphC.getMessage(), just for a better encapsulation of the interpreter.
	 * @param id the id
	 * @return message */
	public static String getMsg(final String id) { return STGraphC.getMessage(id); }


	/** Get the description of the specified function, as read from the file functions*.properties
	 * and the custom function descriptions.
	 * <br>It operates as a wrapper of the static method STGraphC.getMessage(), but with a safer behavior:
	 * it returns a empty string, instead of throwing an exception, if the id is not found.
	 * @param id the id
	 * @return description */
	public static String getFunctionDescription(final String id) {
		try {
			return STGraphC.getMessage(id);
		} catch (Exception e) {
			String t = customFunDescr.get(id);
			if(t != null) { return t; }
			return STTools.BLANK;
		}
	}


	/** Get the description of the specified variable.
	 * @param id the id
	 * @return description */
	public static String getVariableDescription(final String id) {
		String result = STTools.BLANK;
		if(STInterpreter.getSystemVars(true, true).contains(id)) {
			try { result = STGraphC.getMessage("SYSVAR." + id.toUpperCase()); } catch (Exception e) { ; } //$NON-NLS-1$
		} else {
			STNode n;
			if(id.indexOf(STTools.DOT) == -1) {
				n = STGraph.getSTC().getCurrentGraph().getNodeByName(id);
			} else {
				String[] s = id.split("\\."); //$NON-NLS-1$
				n = ((ModelNode)STGraph.getSTC().getCurrentGraph().getNodeByName(s[0])).getSubmodel().getNodeByName(s[1]);
			}
			if(n != null) {
				String s = n.getDescription();
				if(s != null) { result = s; }
			}
		}
		return result;
	}


	/** Get a reference to the parser.
	 * @return parser */
	public final Parser getParser() { return parser; }


	/** Get a reference to the error list.
	 * @return error list */
	@SuppressWarnings("unchecked")
	public final Vector<Object> getErrorList() { return errorList; }


	/** Get the array of the system-defined operator names / signatures.
	 * @return names */
	public static Object[] getOperators() {
		ArrayList<String> al = new ArrayList<String>();
		al.add("x+y"); al.add("x-y"); al.add("x*y"); al.add("x/y"); al.add("x%y"); al.add("x^y"); al.add("-x");
		al.add("x==y"); al.add("x!=y"); al.add("x<y"); al.add("x<=y"); al.add("x>y"); al.add("x>=y"); al.add("x&&y"); al.add("x||y");  al.add("!x");
		al.add("@x"); al.add("x#y"); al.add("x##y"); al.add("[x:y]"); al.add("[x:y:z]"); al.add("{x}");
		return al.toArray();
	}


	/** Get the array of the function names / signatures.
	 * @return names */
	public final STFunction[] getFunctions() { return functions; }


	/** Get the list of system-defined variables.
	 * @param isState the is state
	 * @param withHiddenVars with hidden vars
	 * @return list */
	public static ArrayList<String> getSystemVars(final boolean isState, final boolean withHiddenVars) {
		systemVars = new ArrayList<String>();
		if(isState) {
			systemVars.add(THIS);
			systemVars.add(ME);
		}
		/* [such vars may be useful to debug web models...]
		if(!STGraph.getSTC().getCurrentGraph().isForWeb()) {
			systemVars.add("time"); systemVars.add("index"); systemVars.add("vTime"); systemVars.add("vIndex");
		}
		 */
		systemVars.add("time"); systemVars.add("index"); systemVars.add("vTime"); systemVars.add("vIndex");
		systemVars.add("time0"); systemVars.add("time1"); systemVars.add("timeD"); systemVars.add("numSteps");
		systemVars.add("pi"); systemVars.add("e");
		ArrayList<String> al = new ArrayList<String>(systemVars);
		if(withHiddenVars) {
			for(String s : privateVars) { al.add(s); }
		}
		return al;
	}


	/** Get the list of the model variables.
	 * @return list */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getModelVars() {
		String s;
		ArrayList<String> al = new ArrayList<String>();
		ArrayList<String> al2 = getSystemVars(true, true);
		for(Enumeration<String> e = getSymbolTable().keys(); e.hasMoreElements();) {
			s = e.nextElement();
			if(!s.startsWith("__") && !al2.contains(s)) { al.add(s); } //$NON-NLS-1$
		}
		Collections.sort(al, STGraph.getSTC().getStringNameComparator());
		return al;
	}


	/** Parse the expression, returning the node object which maintains the parsed tree. If there are errors in the expression, they are added to the <code>errorList</code> member.
	 * @param expression_in the input expression string
	 * @return topNode */
	@SuppressWarnings("unchecked")
	public final ArrayList<Node> preParseExpression(final String expression_in) {
		String sep = STTools.SEMICOLON;
		try {
			errorList.removeAllElements();
			ArrayList<Node> result = new ArrayList<Node>();
			if(expression_in.indexOf(sep) == -1) {
				result.add(parser.parseStream(new StringReader(expression_in), this));
			} else {
				for(String expr : expression_in.split(sep)) {
					result.add(parser.parseStream(new StringReader(expr), this));
				}
			}
			return result;
		} catch (final ParseException e) {
			errorList.add(e.getMessage());
			return null;
		} catch (final Throwable e) {
			errorList.add(STInterpreter.getMsg("ERR.FUN.GENERIC")); //$NON-NLS-1$
			return null;
		}
	}


	/** Add a new variable to the parser, or update the value of an existing variable.
	 * @param name name of the variable to be added
	 * @param value value for the variable
	 * @return object of the variable */
	public Object addVariable(String name, double value) {
		Tensor object = Tensor.newScalar(value);
		symTab.makeVarIfNeeded(name, object);
		return object;
	}


	/** Evaluate the expression defining the value of the specified graph node.
	 * @param node the node
	 * @return result */
	public final Object evalExpression(final STNode node) { return evalExpression(node, node.topOfTreeValue); }


	/** Evaluate the expression <code>_topNode</code> of the specified graph node.
	 * @param node the d-graph node
	 * @param _topNode the associated e-graph node
	 * @return result */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final Object evalExpression(final STNode node, final ArrayList<Node> _topNode) {
		resetPrivateVars();
		STGraphImpl graph = node.getGraph();

		STInterpreter.cnop = new Stack();
		STInterpreter.nde = new Stack();

		for(int i = 0; i < _topNode.size() - 1; i++) { // pre-evaluator splitter, allowing subexpressions separated by ';'
			topNode = _topNode.get(i);
			addVariable("$v" + i, getValueAsObject()); //$NON-NLS-1$
		}
		topNode = _topNode.get(_topNode.size() - 1);
		Object result = getValueAsObject(); // basic evaluator

		if(!hasError() && graph.getTopGraph().getLastError() == null) {
			node.setEvalErrDescription(null);
			return result;
		}
		graph.setLastErrorNode(node);
		String s = graph.getTopGraph().getLastError();
		if(s != null && !s.startsWith(STTools.OPENV)) { s = STTools.OPENV + node.getName() + STTools.CLOSEV + STTools.SPACE + s; }
		node.setEvalErrDescription(s);
		graph.getTopGraph().setLastError(s);
		if(!graph.isTopGraph()) {
			graph.getTopGraph().setLastErrorNode(graph.getSuperNode()); //TODO it should be in fact the top node...
			graph.getTopNode().setEvalErrDescription(s);
		}
		return null;
	}


	/** Evaluate the specified condition.
	 * @param topNode the top node
	 * @return result */
	final boolean evalCondition(final Node topNode) {
		STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
		this.topNode = topNode;
		Object result = getValueAsObject();
		if(!hasError() && graph.getLastError() == null) {
			if(result instanceof Tensor && !((Tensor)result).isEmpty()) {
				for(double v : ((Tensor)result).getValues()) {
					if(STParserTools.isTrue(v)) { return true; }
				}
				return false;
			}
		}
		return false;
	}


	/** Build the string describing an error.
	 * @param s the string providing the required information
	 * @return the error */
	public static String buildErrorDescription(final String s) {
		String s2 = s;
		if(s2.indexOf("__") != -1) { //$NON-NLS-1$
			String locerrs[] = s2.split("__"); //$NON-NLS-1$
			for(String t : locerrs) {
				if(t.startsWith(locale)) { s2 = t.substring(locale.length() + 1); }
			}
		}
		final STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
		if(graph.getLastErrorNode() != null && !s2.startsWith(STTools.OPENV)) {
			s2 = STTools.OPENV + graph.getLastErrorNode().getName() + STTools.CLOSEV + STTools.SPACE + s2;
		}
		return s2;
	}


	/** Build the string describing an error.
	 * @param name the name
	 * @param s the string providing the required information
	 * @return the error */
	public static String buildErrorDescription(final String name, final String s) {
		String s2 = s;
		if(s2.indexOf("__") != -1) { //$NON-NLS-1$
			String locerrs[] = s2.split("__"); //$NON-NLS-1$
			for(String t : locerrs) {
				if(t.startsWith(locale)) { s2 = t.substring(locale.length() + 1); }
			}
		}
		s2 = name + STTools.COLON + STTools.SPACE + s2;
		final STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
		if(graph.getLastErrorNode() != null && !s2.startsWith(STTools.OPENV)) {
			s2 = STTools.OPENV + graph.getLastErrorNode().getName() + STTools.CLOSEV + STTools.SPACE + s2;
		}
		return s2;
	}


	/** Handle both the errors and the exceptions caught in evaluating expressions during the simulation.
	 * @param m the m
	 * @param returnType the return type
	 * @return value
	 * @throws ParseException the parse exception */
	public static Object handleException(final String m, final int returnType) throws ParseException {
		final STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
		if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
			graph.setLastError(buildErrorDescription(m));
		} else if(!graph.continueWithExceptions()) { throw new ParseException(m); }
		if(returnType == STData.VALUETYPE_VECTOR) { return Tensor.newVector(); }
		if(returnType == STData.VALUETYPE_MATRIX) { return Tensor.newMatrix(); }
		return new Tensor();
	}


	/** Handle both the errors and the exceptions caught in evaluating expressions during the simulation.
	 * @param m the m
	 * @return value
	 * @throws ParseException the parse exception */
	public static Object handleException(final String m) throws ParseException {
		final STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
		if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
			graph.setLastError(buildErrorDescription(m));
		} else if(!graph.continueWithExceptions()) { throw new ParseException(m); }
		return new Tensor();
	}


	/** Handle both the errors and the exceptions caught in evaluating expressions during the simulation.
	 * @param m the m
	 * @param returnValue the return value
	 * @return value
	 * @throws ParseException the parse exception */
	public static Object handleException(final String m, final Object returnValue) throws ParseException {
		String m2 = buildErrorDescription(m);
		STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
		if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
			graph.setLastError(m2);
		} else if(!graph.continueWithExceptions()) {
			throw new ParseException(m2);
		}
		return returnValue;
	}


	/** Handle both the errors and the exceptions caught in evaluating expressions during the simulation.
	 * The error / warning string can be internally localized (typically for the use in custom functions)
	 * according to the syntax: <code>enmsg1__itmsg2</code>.
	 * @param name the function name
	 * @param m the m
	 * @return value
	 * @throws ParseException the parse exception */
	public static Object handleLocalizableException(final String name, final String m) throws ParseException {
		final STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
		if(graph.getState() == STGraphImpl.STATE_EDITING || graph.getState() == STGraphImpl.STATE_PRE_EVALUATING) {
			graph.setLastError(buildErrorDescription(name, m));
		} else if(!graph.continueWithExceptions()) { throw new ParseException(m); }
		return new Tensor();
	}


	/** Activate or deactivate some system variables for web models. */
	public final void handleVarsForWeb() {
		/* [such vars may be useful to debug web models...]
        if(STGraph.getSTC().getCurrentGraph().isForWeb()) {
        	removeVariable("index"); //$NON-NLS-1$
        	removeVariable("time"); //$NON-NLS-1$
        	removeVariable("vIndex"); //$NON-NLS-1$
        	removeVariable("vTime"); //$NON-NLS-1$
        } else {
        	addVariable("index", 0); //$NON-NLS-1$
        	addVariable("time", 0); //$NON-NLS-1$
        	addVariable("vIndex", 0); //$NON-NLS-1$
        	addVariable("vTime", 0); //$NON-NLS-1$
        }
		 */
	}


	/** Get the defining nodes for the given top node.
	 * @param node the node
	 * @return vector listing nodes, i.e, non-system variables, in the parsed tree  */
	public final ArrayList<Node> getDefiningParserNodes(final Node node) {
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<String> temp = new ArrayList<String>();
		if(node instanceof ASTVarNode) {
			String s = ((ASTVarNode)node).getName();
			if(!getSystemVars(false, true).contains(s) && !temp.contains(s)) { result.add(node); }
		}
		insertNodes(node, result, temp);
		return result;
	}


	/** Insert node (helper for the recursive call of the <code>serializeExpression</code> method).
	 * @param tNode the top node
	 * @param result the resulting list
	 * @param temp the temporary list */
	final void insertNodes(final Node tNode, final ArrayList<Node> result, final ArrayList<String> temp) {
		Node node = null;
		for(int i = 0; i < tNode.jjtGetNumChildren(); i++) {
			node = tNode.jjtGetChild(i);
			if(node instanceof ASTVarNode) {
				String s = ((ASTVarNode)node).getName();
				if(!getSystemVars(false, true).contains(s) && !temp.contains(s)) { result.add(node); }
			}
			insertNodes(node, result, temp);
		}
	}


	/** Get the value object for the given node.
	 * @param node the node
	 * @return result */
	public final Object getValueObj(final Node node) { return ((ASTVarNode)node).getVar().getValue(); }


	/** Get the graph nodes corresponding to the given parser nodes.
	 * @param nodes the nodes
	 * @param node the node
	 * @return list of nodes, i.e, non-system variables, in the parsed tree */
	public final ArrayList<STNode> getDefiningGraphNodes(final ArrayList<Node> nodes, final STNode node) {
		String name;
		ArrayList<STNode> result = new ArrayList<STNode>();
		for(Node n : nodes) {
			name = ((ASTVarNode)n).getName();
			if(!name.equals(THIS) && !name.equals(ME)) {
				result.add(node.getGraph().getNodeByName(name));
			} else {
				result.add(node);
			}
		}
		return result;
	}


	/** Get the defining nodes in the given expression.
	 * @param expr the expression
	 * @return list of nodes, i.e, non-system variables, in the parsed tree */
	public final ArrayList<String> getDefiningGraphNodeNames(final String expr) {
		STGraphC stc = STGraph.getSTC();
		parseExpression(expr);
		if(getErrorInfo() != null) { return null; }
		stc.getExpressionParser().setVars(new ArrayList<String>()); // reset the list of variables
		try { getTopNode().jjtAccept(stc.getExpressionParser(), null); // and fill it by traversing the expression tree
		} catch (Exception ex) { return null; }
		ArrayList<String> temp = stc.getExpressionParser().getVars();
		ArrayList<String> result = new ArrayList<String>();
		for(String t : temp) {
			if(!getSystemVars(false, true).contains(t) && !result.contains(t)) { result.add(t); }
		}
		return result;
	}


	/** Get the symbol table.
	 * @return table */
	public SymbolTable getSymTab() { return symTab; }

}
