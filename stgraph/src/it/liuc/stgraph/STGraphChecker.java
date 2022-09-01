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

import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.STTools;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.SimpleNode;
import org.nfunk.jep.type.Tensor;


/** Graph correctness checker. */
public final class STGraphChecker {
	private static String c1 = "<code><a href='"; //$NON-NLS-1$
	private static String c3 = "'>"; //$NON-NLS-1$
	private static String c2 = "</a></code>"; //$NON-NLS-1$
	private static String f1 = "<font color='red'>"; //$NON-NLS-1$
	private static String f2 = "</font>"; //$NON-NLS-1$
	private static String br = "<br>"; //$NON-NLS-1$
	private static String funTree;
	private static String[] funs = new String[] {"/", "sqrt", "log"}; 
	private static int numberOfGroups = 0;
	private static int numberOfTeams = 0;


	/** Class constructor. */
	private STGraphChecker() { ; }


	/** Check for consistency specs
	 * (wrapper for the methods checking the single specs).
	 * @param graph the graph
	 * @return result */
	public static String check(final STGraphExec graph) {
		STNode[] nodes = graph.getNodes();
		if(nodes == null || nodes.length == 0) { return STGraphC.getMessage("CHECK.EMPTY"); } //$NON-NLS-1$
		StringBuffer s = new StringBuffer(STGraphChecker.checkTerminals(graph));
		s.append(STGraphChecker.checkConnections(graph));
		s.append(STGraphChecker.checkIssuesInDefs(graph));
		return s.toString();
	}


	/** Check for consistency: all terminal (i.e., without outgoing edges) nodes should be output.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkTerminals(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.TERMINALS.TITLE") + "</h3>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		StringBuffer s2 = new StringBuffer();
		for(STNode node : graph.getNodes()) {
			if(node instanceof ValueNode && ((node.getDefinedNodes() == null || node.getDefinedNodes().size() == 0) && !node.isOutput())) {
				s2.append(c1 + node.getName() + c3 + node.getName() + c2 + STTools.SPACE);
			}
		}
		if(s2.length() > 0) {
			s.append(f1 + STGraphC.getMessage("CHECK.TERMINALS.CAPTION") + br + s2 + f2); //$NON-NLS-1$
		} else {
			s.append(STGraphC.getMessage("CHECK.OK")); //$NON-NLS-1$
		}
		return s;
	}


	/** Check for consistency: all edges should be used in definitions.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkConnections(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.CONNECTIONS.TITLE") + "</h3>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		StringBuffer s2 = new StringBuffer();
		ArrayList<STNode> fromNodes;
		ArrayList<String> res = null;
		for(STNode node : graph.getNodes()) {
			fromNodes = node.getDefiningNodesByEdges();
			if(fromNodes != null && fromNodes.size() > 0) { // if there is at least one incoming edge
				if(node.isAlgebraic() || node.isState()) { res = checkConnections(fromNodes, node.getDefiningNodesByExpressions()); }
				if(res != null && res.size() > 0) {
					String ss;
					for(int j = 0; j < res.size(); j++) {
						ss = res.get(j) + STTools.DASH + node.getName(); 
						s2.append(c1 + ss + c3 + ss + c2 + br);
					}
				}
			}
		}
		if(s2.length() > 0) {
			s.append(f1 + STGraphC.getMessage("CHECK.CONNECTIONS.CAPTION") + br + s2 + f2); //$NON-NLS-1$
		} else {
			s.append(STGraphC.getMessage("CHECK.OK")); //$NON-NLS-1$
		}
		return s;
	}


	/** Helper for check for consistency.
	 * @param connectedNodes the connected nodes
	 * @param nodesInExpr the nodes in expression
	 * @return result */
	private static ArrayList<String> checkConnections(final ArrayList<STNode> connectedNodes, final ArrayList<STNode> nodesInExpr) {
		ArrayList<String> ret = new ArrayList<String>();
		String nodeName;
		String nodeName2;
		boolean found;
		for(STNode node : connectedNodes) {
			if(node.getGraph().isTopGraph()) {
				found = false;
				nodeName = node.getName();
				if(nodesInExpr != null) {
					for(STNode node2 : nodesInExpr) {
						nodeName2 = node2.getName();
						if(nodeName.equals(nodeName2)) { found = true; }
					}
				}
				if(!found) { ret.add(nodeName); }
			}
		}
		return ret;
	}


	/** Check for (plausible) consistency: highlight possible issues in definitions.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkIssuesInDefs(final STGraphExec graph) {
		STInterpreter interpreter = graph.getInterpreter();
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.ISSUESINDEFS.TITLE") + "</h3>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		StringBuffer s2 = new StringBuffer();
		for(STNode node : graph.getNodes()) {
			if(node.isAlgebraic()) {
				s2.append(check(interpreter, node, ((ValueNode)node).getExpression()));
			} else if(node.isState()) {
				s2.append(check(interpreter, node, ((ValueNode)node).getStateInit()));
				s2.append(check(interpreter, node, ((ValueNode)node).getStateTransition()));
				if(node.isStateWithOutput()) {
					s2.append(check(interpreter, node, ((ValueNode)node).getExpression()));
				}
			}
		}
		if(s2.length() > 0) {
			s.append(f1 + STGraphC.getMessage("CHECK.ISSUESINDEFS.CAPTION") + br + s2 + f2); //$NON-NLS-1$
		} else {
			s.append(STGraphC.getMessage("CHECK.OK")); //$NON-NLS-1$
		}
		return s;
	}


	/** Helper for check for consistency.
	 * @param interpreter the interpreter
	 * @param node the node
	 * @param expression the expression
	 * @return string */
	private static String check(final STInterpreter interpreter, final STNode node, final String expression) {
		String ss = node.getName(); 
		interpreter.parseExpression(expression);
		if(interpreter.getErrorInfo() != null) {
			return c1 + ss + c3 + ss + c2 + ": expression cannot be parsed" + br; //$NON-NLS-1$
		}
		SimpleNode parsedNode = (SimpleNode)interpreter.getTopNode();
		funTree = checkNode(parsedNode);
		scanNodes(parsedNode, new DefaultMutableTreeNode(getNodeName(parsedNode)));
		if(funTree.length() > 0) { funTree = c1 + ss + c3 + ss + c2 + STTools.COLON + STTools.SPACE + funTree + br; }
		return funTree;
	}


	/** Helper for check for consistency.
	 * @param node the node
	 * @return name */
	private static String checkNode(final SimpleNode node) {
		if(!(node instanceof ASTFunNode)) { return STTools.BLANK; }
		String n = ((ASTFunNode)node).getName();
		String result = STTools.BLANK;
		for(String s : funs) { if(n.equals(s)) { result += s + STTools.SPACE; } }
		return result;
	}


	/** Helper for check for consistency.
	 * @param node the node
	 * @return name */
	private static String getNodeName(final SimpleNode node) {
		if(node instanceof ASTConstant) { return ((ASTConstant)node).getValue().toString(); }
		if(node instanceof ASTFunNode) { return ((ASTFunNode)node).getName(); }
		if(node instanceof ASTVarNode) { return ((ASTVarNode)node).getName(); }
		return STTools.BLANK;
	}


	/** Helper for check for consistency.
	 * @param topParsedNode the top parsed node
	 * @param topTreeNode the top tree node */
	private static void scanNodes(final SimpleNode topParsedNode, final DefaultMutableTreeNode topTreeNode) {
		SimpleNode parsedNode;
		for(int i = 0; i < topParsedNode.jjtGetNumChildren(); i++) {
			parsedNode = (SimpleNode)topParsedNode.jjtGetChild(i);
			funTree += checkNode(parsedNode);
			scanNodes(parsedNode, new DefaultMutableTreeNode(getNodeName(parsedNode)));
		}
	}


	/** Check for consistency to web ("business game") specs
	 * (wrapper for the methods checking the single specs).
	 * <br>The specs are the following.
	 * <h4>General rules for models</h4>
	 * <br>Time steps must range from 0 to the last round with timeD = 1
	 * <br>The system variables 'time', 'index', 'vTime', and 'vIndex' must not be used in expressions
	 * <br>The 'exception handling' level model option must be set to 1 (stop)
	 * <br>The 'interrupts' and 'save simulation data' model options must be switched off
	 * <br>Each player is assumed to be identified by a corresponding vector index (from 0)
	 * <br>At least one (semantic) group for nodes must be defined
	 * <br>The time unit name should be set 
	 * <h4>General rules for nodes</h4>
	 * <br>Only input nodes and output nodes are exposed, as follows:
	 * <li>input nodes must have a custom property 'InputType', whose possible values are described below
	 * <li>output nodes must have a custom property 'OutputType', whose possible values are described below
	 * <br>General / market data are in scalar nodes, under the hypothesis that they are the same for all the teams
	 * <br>Player data (both inputs / decisions and outputs / results) are in vector nodes: each player is associated to an element of the vector
	 * <br>Each initial value of a state node must be set via a non-shared input node, and the dimensions of the initial state and the computed states must be the same 
	 * <br>***???*** both input and output nodes must have the switches 'output' and 'keep series if vector' turned on
	 * <br>The nodes exposed in Data Dictionary (input nodes with InputType = 3 and output nodes with OutputType >= 2 and <= 6) must have a description
	 * <br>Both input and output nodes must have a custom property 'Group', with value either '1', or '2', ... to express that that node belongs to group 1, 2, ... (<i>wrapped by the <code>getGroupDescriptions()</code> method</i>)
	 * <br>Further (optional) custom properties (InputText, OutputText, Phase, PhaseNullValue) must be properly set if used
	 * <h4>Input nodes</h4>
	 * <li>InputType = 0: initial value of a state node: it must be vector (<i>wrapped by the <code>isInitialState()</code> and <code>getInitialStateNodeList()</code> methods</i>)
	 * <li>InputType = 1: exogenous, team number independent, input: it must be scalar (<i>wrapped by the <code>isExogenousInput()</code> and <code>getExogenousInputNodeList()</code> methods</i>)
	 * <li>InputType = 2: exogenous, team number dependent: it must be scalar (i.e., value to be multiplied by the number of teams), input (<i>subset of 'InputType = 1', wrapped by the <code>isNumTeamsDependent()</code> method</i>)
	 * <li>InputType = 3: player decision: it must be vector (<i>wrapped by the <code>isPlayerDecision()</code> and <code>getPlayerDecisionNodeList()</code> methods</i>)
	 * <li>InputType = 6: constant parameter to be displayed to players (<i>wrapped by the <code>isConstant()</code> and <code>getConstantNodeList()</code> methods</i>)
	 * <li>InputType = 7: constant parameter not to be displayed to players (typically a utility node)
	 * <br>Input nodes with InputType <= 6 must have a custom property 'DefaultValue', whose value is equal to the expression value and is included in the interval [Min, Max] if specified
	 * <br>Input nodes with InputType <= 6 must have a custom property 'Decimals'
	 * <br>Input nodes with InputType <= 6 must have a custom property 'Group' (in the case InputType = 0 then Group = 0)
	 * <h4>Output nodes</h4>
	 * <li>OutputType = 0: variable visible to administrators only (not to players)
	 * <li>OutputType = 1: player decision (corresponding to InputType = 3) (<i>wrapped by the <code>isPlayerDecision()</code> and <code>getPlayerDecisionNodeList()</code> methods</i>)
	 * <li>OutputType = 2: player result (<i>wrapped by the <code>isPlayerResult()</code> and <code>getPlayerResultNodeList()</code> methods</i>)
	 * <li>OutputType = 3: player result that should not visualized if zero
	 * <li>OutputType = 4: general / market data (<i>wrapped by the <code>isExogenousOutput()</code> and <code>getExogenousOutputNodeList()</code> methods</i>)
	 * <li>OutputType = 5: general / market data that should not visualized if zero (hence replicated for all teams)
	 * <li>OutputType = 6: target variable for ranking players (<i>wrapped by the <code>isGoalNode()</code> and <code>getGoalNode()</code> methods</i>)
	 * <li>OutputType = 7: message (<i>wrapped by the <code>isMessageNode()</code> and <code>getMessageNodeList()</code> methods</i>)
	 * <br>Output nodes must have a custom property 'Decimals'
	 * <br>Output nodes must have a custom property 'Group'
	 * <h4>Data dictionary</h4>
	 * <br>It includes input nodes with InputType = 3 ("Decisions") and output nodes with OutputType = 2 or 3 ("Results"), and = 4 or 5 ("Market data"), and = 6 ("Target variable")
	 * <br>Nodes are classified by their 'Group' but for target variables
	 * @param graph the graph
	 * @return result */
	public static String checkForWeb(final STGraphExec graph) {
		graph.endExec(true);
		graph.steppedExec(); // guarantee that the model has been executed at least once
		graph.endExec(true);
		STNode[] nodes = graph.getNodes();
		if(nodes == null || nodes.length == 0) { return f1 + STGraphC.getMessage("CHECK.EMPTY") + f2; }
		StringBuffer s = new StringBuffer();
		s.append(STGraphChecker.checkTime(graph));
		s.append(STGraphChecker.checkTime2(graph));
		s.append(STGraphChecker.checkModelSwitches(graph));
		s.append(STGraphChecker.checkGroup(graph));
		s.append(STGraphChecker.checkTargetOutput(graph));
		s.append(STGraphChecker.checkInputs(graph));
		s.append(STGraphChecker.checkOutputs(graph));
		s.append(STGraphChecker.checkStates(graph));
		s.append(STGraphChecker.checkFurtherCProps(graph));
		return s.toString();
	}


	/** Check for web consistency: time steps must range from 0 to the last round with timeD = 1.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkTime(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.WEBTIME.TITLE") + "</h3>");
		StringBuffer s2 = new StringBuffer();
		if(graph.getTime0() != 0.0) {
			s2.append(f1 + c1 + "time0 = " + graph.getTime0() + c2 + f2 + br);
		}
		if(graph.getTimeD() != 1.0) {
			s2.append(f1 + c1 + "timeD = " + graph.getTimeD() + c2 + f2 + br);
		}
		if(s2.length() > 0) {
			s.append(s2);
		} else {
			s.append(STGraphC.getMessage("CHECK.OK"));
		}
		return s;
	}


	/** Check for web consistency: the system variables 'time', 'index', 'vTime', and 'vIndex' must not be used in expressions.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkTime2(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.WEBTIME2.TITLE") + "</h3>");
		STNode[] nodes = graph.getAllNodes();
		if(nodes == null || nodes.length == 0) {
			s.append(f1 + STGraphC.getMessage("CHECK.EMPTY") + f2);
			return s;
		}
		StringBuffer s2 = new StringBuffer();
		for(STNode node : nodes) {
			if(node.isVariable()) {
				ValueNode vnode = (ValueNode)node;
				STInterpreter interpreter = node.getGraph().getInterpreter();
				boolean found = false;
				if(node.isState()) {
					ArrayList<String> vars = node.getExpressionVars(interpreter, vnode.getStateInit(), false);
					if(vars != null && vars.size() > 0) {
						for(String var : vars) {
							if(var.equals("time") || var.equals("vTime") || var.equals("index") || var.equals("vIndex")) { found = true; }
						}
					}
					vars = node.getExpressionVars(interpreter, vnode.getStateTransition(), false);
					if(vars != null && vars.size() > 0) {
						for(String var : vars) {
							if(var.equals("time") || var.equals("vTime") || var.equals("index") || var.equals("vIndex")) { found = true; }
						}
					}
				}
				if(node.isAlgebraic() || node.isStateWithOutput()) {
					ArrayList<String> vars = node.getExpressionVars(interpreter, vnode.getExpression(), false);
					if(vars != null && vars.size() > 0) {
						for(String var : vars) {
							if(var.equals("time") || var.equals("vTime") || var.equals("index") || var.equals("vIndex")) { found = true; }
						}
					}
				}
				if(found) {
					String n = node.getName();
					s2.append(f1 + c1 + n + c3 + n + c2 + STTools.SPACE);
				}
			}
		}		
		if(s2.length() > 0) {
			s.append(STGraphC.getMessage("CHECK.WEBTIME2.TEXT1") + STTools.SPACE + s2);
		} else {
			s.append(STGraphC.getMessage("CHECK.OK"));
		}
		return s;
	}


	/** Check for web consistency: the 'exception handling' level model option must be set to 1 (stop)
	 * and the 'interrupts' and 'save simulation data' model options must be switched off.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkModelSwitches(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.WEBSWITCHES.TITLE") + "</h3>");
		StringBuffer s2 = new StringBuffer();
		if(graph.getExceptionHandling() != STGraphImpl.EXCEPTIONHANDLING_STOP) {
			s2.append(f1 + STGraphC.getMessage("CHECK.WEBSWITCHES.TEXT1") + f2 + br);
		}
		if(graph.isWithInterrupts()) {
			s2.append(f1 + STGraphC.getMessage("CHECK.WEBSWITCHES.TEXT2") + f2 + br);
		}
		if(graph.isDataSaved()) {
			s2.append(f1 + STGraphC.getMessage("CHECK.WEBSWITCHES.TEXT3") + f2 + br);
		}
		if(s2.length() > 0) {
			s.append(s2);
		} else {
			s.append(STGraphC.getMessage("CHECK.OK"));
		}
		return s;
	}


	/** Check for web consistency: the 'Group' information must be properly defined.
	 * <br>It generates the side-effect of setting the variable 'numberOfGroups',
	 * then used in the checkInputs() and checkOutputs() methods.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkGroup(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.WEBGROUP.TITLE") + "</h3>");
		numberOfGroups = 0;
		StringBuffer s2 = new StringBuffer();
		String[][] x = graph.getWebModelGroupData(graph.getDefaultModelLanguage());
		if(x == null || x.length < 1) {
			s2.append(f1 + STGraphC.getMessage("CHECK.WEBGROUP.TEXT") + f2 + br);
		} else {
			numberOfGroups = s.length();
		}
		if(s2.length() > 0) {
			s.append(s2);
		} else {
			s.append(STGraphC.getMessage("CHECK.OK"));
		}
		return s;
	}


	/** Check for web consistency: at least one target output node must exist.
	 * <br>It generates the side-effect of setting the variable 'numberOfTeams',
	 * then used in the checkInputs() method.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkTargetOutput(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.WEBTARGET.TITLE") + "</h3>");
		STNode[] nodes = graph.getOutputNodes();
		if(nodes == null || nodes.length == 0) {
			s.append(f1 + STGraphC.getMessage("CHECK.WEBTARGET.NONODES") + f2);
			return s;
		}
		boolean found = false;
		StringBuffer s2 = new StringBuffer();
		for(STNode node : nodes) {
			String outputType = node.getCProperty("OutputType");
			if(!STTools.isEmpty(outputType) && outputType.equals("6")) {
				found = true;
				if(!node.isVector()) {
					s2.append(getMsg(node, "CHECK.WEBTARGET.NOVECTOR"));
				} else {
					if((numberOfTeams = ((Tensor)node.getValue()).getSize()) <= 0) {
						s2.append(getMsg(node, "CHECK.WEBTARGET.NOVECTOR2"));
					}

				}
			}
		}
		if(!found) {
			s2.append(f1 + STGraphC.getMessage("CHECK.WEBTARGET.NOTARGET") + f2);
		}
		if(s2.length() > 0) {
			s.append(s2);
		} else {
			s.append(STGraphC.getMessage("CHECK.OK"));
		}
		return s;
	}


	/** Check for web consistency: custom properties must be properly set for input nodes.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkInputs(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.WEBINPUTS.TITLE") + "</h3>");
		STNode[] nodes = graph.getInputNodes();
		if(nodes == null || nodes.length == 0) {
			s.append(f1 + STGraphC.getMessage("CHECK.WEBINPUTS.NONODES") + f2);
			return s;
		}
		StringBuffer s2 = new StringBuffer();
		for(STNode node : nodes) {
			String inputType = node.getCProperty("InputType");
			if(STTools.isEmpty(inputType)) {
				s2.append(getMsg(node, "CHECK.WEBINPUTS.NOINPUTTYPE"));
			} else {
				if(inputType.equals("0") || inputType.equals("3")) {
					if(!node.isVector()) {
						s2.append(getMsg(node, "CHECK.WEBINPUTS.NOVECTOR"));
					}
					if(inputType.equals("0")) {
						ArrayList<STNode> vars = node.getDefinedNodes();
						if(vars == null || vars.size() != 1) {
							s2.append(getMsg(node, "CHECK.WEBINPUTS.NOSINGLEDEFNODE"));
						} else if(!vars.get(0).isState()) {
							s2.append(getMsg(node, "CHECK.WEBINPUTS.INITNONSTATENODE"));
						}
					}
				} else if(inputType.equals("1") || inputType.equals("2")) {
					if(!node.isScalar()) {
						s2.append(getMsg(node, "CHECK.WEBINPUTS.NOSCALAR"));
					}
				} else if(inputType.equals("6")) {
					// nothing to do...
				} else if(inputType.equals("7")) {
					// nothing to do...
				} else {
					s2.append(getMsg(node, "CHECK.WEBINPUTS.WRONGINPUTTYPE"));
				}
				if(!inputType.equals("7")) {
					String dv = node.getCProperty("DefaultValue");
					if(STTools.isEmpty(dv)) {
						s2.append(getMsg(node, "CHECK.WEBINPUTS.NODEFAULTVALUE"));
					} else {
						try {
							boolean correct = true;
							double d = Double.valueOf(dv).doubleValue();
							Tensor t = (Tensor)node.getValue();
							if(t.isScalar()) {
								if(!inputType.equals("2")) {
									if(t.getValue() != d) {
										s2.append(getMsg(node, "CHECK.WEBINPUTS.DOUBTFULDEFAULTVALUE"));
										correct = false;
									}
								} else {
									if(t.getValue() != d * numberOfTeams) {
										s2.append(getMsg(node, "CHECK.WEBINPUTS.DOUBTFULDEFAULTVALUE"));
										correct = false;
									}
								}
							} else if(t.isVector()) {
								if(t.getScalar(0).getValue() != d) {
									s2.append(getMsg(node, "CHECK.WEBINPUTS.DOUBTFULDEFAULTVALUE"));
									correct = false;
								}
							} else {
								s2.append(getMsg(node, "CHECK.WEBINPUTS.DOUBTFULDEFAULTVALUE2"));
							}
							if(correct) {
								String min = node.getCProperty("Min");
								if(!STTools.isEmpty(min)) {
									try {
										double m = Double.valueOf(min).doubleValue();
										if(d < m) {
											s2.append(getMsg(node, "CHECK.WEBINPUTS.BELOWMINDEFAULTVALUE"));
										}
									} catch (Exception e) {
										s2.append(getMsg(node, "CHECK.WEBINPUTS.WRONGMINVALUE"));
									}
								}
								String max = node.getCProperty("Max");
								if(!STTools.isEmpty(max)) {
									try {
										double m = Double.valueOf(max).doubleValue();
										if(d > m) {
											s2.append(getMsg(node, "CHECK.WEBINPUTS.ABOVEMAXDEFAULTVALUE"));
										}
									} catch (Exception e) {
										s2.append(getMsg(node, "CHECK.WEBINPUTS.WRONGMAXVALUE"));
									}
								}
							}
						} catch (Exception e) {
							s2.append(getMsg(node, "CHECK.WEBINPUTS.WRONGDEFAULTVALUE"));
						}
					}
				}
				if(STTools.isEmpty(node.getCProperty("Decimals")) && !inputType.equals("7")) {
					s2.append(getMsg(node, "CHECK.WEBINPUTS.NODECIMALS"));
				}
				if(inputType.equals("3") && STTools.isEmpty(node.getDescription())) {
					s2.append(getMsg(node, "CHECK.WEBINPUTS.NODESCRIPTION"));
				}
				String group = node.getCProperty("Group");
				if(!inputType.equals("0") && !inputType.equals("7")) {
					if(STTools.isEmpty(group)) {
						s2.append(getMsg(node, "CHECK.WEBINPUTS.NOGROUP"));
					} else {
						try {
							int g = Integer.parseInt(group);
							if(g < 0 || g > numberOfGroups) { throw new Exception(); }
							if(g < 10 && !group.startsWith("0")) {
								s2.append(getMsg(node, "CHECK.WEBINPUTS.WRONGGROUP"));
							}
						} catch (Exception e) {
							s2.append(getMsg(node, "CHECK.WEBINPUTS.WRONGGROUP"));
						}
					}
				}
			}
		}
		if(s2.length() > 0) {
			s.append(s2);
		} else {
			s.append(STGraphC.getMessage("CHECK.OK"));
		}
		return s;
	}


	/** Check for web consistency: custom properties must be properly set for output nodes.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkOutputs(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.WEBOUTPUTS.TITLE") + "</h3>");
		STNode[] nodes = graph.getOutputNodes();
		if(nodes == null || nodes.length == 0) {
			s.append(f1 + STGraphC.getMessage("CHECK.WEBOUTPUTS.NONODES") + f2);
			return s;
		}
		StringBuffer s2 = new StringBuffer();
		for(STNode node : nodes) {
			String outputType = node.getCProperty("OutputType");
			if(STTools.isEmpty(outputType)) {
				s2.append(getMsg(node, "CHECK.WEBOUTPUTS.NOOUTPUTTYPE"));
			} else {
				if(!(outputType.equals("0") || outputType.equals("1") || outputType.equals("2") || outputType.equals("3") || outputType.equals("4") || outputType.equals("5") || outputType.equals("6") || outputType.equals("7"))) {
					s2.append(getMsg(node, "CHECK.WEBOUTPUTS.WRONGOUTPUTTYPE"));
				}
				if(STTools.isEmpty(node.getCProperty("Decimals")) && !outputType.equals("7")) {
					s2.append(getMsg(node, "CHECK.WEBOUTPUTS.NODECIMALS"));
				}
				String group = node.getCProperty("Group");
				if(STTools.isEmpty(group)) {
					s2.append(getMsg(node, "CHECK.WEBOUTPUTS.NOGROUP"));
				} else {
					try {
						int g = Integer.parseInt(group);
						if(g < 0 || g > numberOfGroups) { throw new Exception(); }
						if(g < 10 && !group.startsWith("0")) {
							s2.append(getMsg(node, "CHECK.WEBOUTPUTS.WRONGGROUP"));
						}
					} catch (Exception e) {
						s2.append(getMsg(node, "CHECK.WEBOUTPUTS.WRONGGROUP"));
					}
				}
				if((outputType.equals("2") || outputType.equals("3") || outputType.equals("4") || outputType.equals("5") || outputType.equals("6")) && STTools.isEmpty(node.getDescription())) {
					s2.append(getMsg(node, "CHECK.WEBOUTPUTS.NODESCRIPTION"));
				}
				if(outputType.equals("7") && STTools.isEmpty(node.getCProperty("OutputText"))) {
					s2.append(getMsg(node, "CHECK.WEBOUTPUTS.NOOUTPUTTEXT"));
				}
			}
		}
		if(s2.length() > 0) {
			s.append(s2);
		} else {
			s.append(STGraphC.getMessage("CHECK.OK"));
		}
		return s;
	}


	/** Check for web consistency: state nodes must be properly initialized and have proper dimension.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkStates(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.WEBSTATES.TITLE") + "</h3>");
		STNode[] nodes = graph.getStateNodes();
		if(nodes == null || nodes.length == 0) {
			s.append(f1 + STGraphC.getMessage("CHECK.WEBSTATES.NONODES") + f2);
			return s;
		}
		int size = -2;
		graph.endExec(true);
		graph.steppedExec();
		StringBuffer s2 = new StringBuffer();
		for(STNode node : nodes) {
			if(!node.isVector()) {
				s2.append(getMsg(node, "CHECK.WEBSTATES.NONVECTORINIT"));
			} else {
				if(size == -2) { size = ((Tensor)node.getValue()).getSize(); }
				if(((Tensor)node.getValue()).getSize() != size) {
					s2.append(getMsg(node, "CHECK.WEBSTATES.WRONGDIMINIT"));
				}
			}
		}
		graph.steppedExec();
		for(STNode node : nodes) {
			if(!node.isVector()) {
				s2.append(getMsg(node, "CHECK.WEBSTATES.NONVECTORSTATE"));
			} else {
				if(size == -2) { size = ((Tensor)node.getValue()).getSize(); }
				if(((Tensor)node.getValue()).getSize() != size) {
					s2.append(getMsg(node, "CHECK.WEBSTATES.WRONGDIMSTATE"));
				}
			}
		}
		graph.endExec(true);
		for(STNode node : nodes) {
			ValueNode vnode = (ValueNode)node;
			STInterpreter interpreter = node.getGraph().getInterpreter();
			ArrayList<STNode> vars = node.getExpressionVarNodes(interpreter, vnode.getStateInit());
			if(vars == null || vars.size() != 1) {
				s2.append(getMsg(node, "CHECK.WEBSTATES.NOSINGLEDEFNODE"));
			} else {
				ArrayList<STNode> vars2 = vars.get(0).getDefinedNodes();
				if(vars2 == null || vars2.size() != 1) {
					s2.append(getMsg(node, "CHECK.WEBSTATES.NOSINGLEDEFNODE2"));
				}
				String inputType = vars.get(0).getCProperty("InputType");
				if(STTools.isEmpty(inputType) || !inputType.equals("0")) {
					s2.append(getMsg(node, "CHECK.WEBSTATES.NOINITNODE"));
				}
			}
		}
		if(s2.length() > 0) {
			s.append(s2);
		} else {
			s.append(STGraphC.getMessage("CHECK.OK"));
		}
		return s;
	}


	/** Check for web consistency: further (optional) custom properties (InputText, OutputText, Phase, PhaseNullValue) must be properly set.
	 * @param graph the graph
	 * @return result */
	public static StringBuffer checkFurtherCProps(final STGraphExec graph) {
		StringBuffer s = new StringBuffer("<h3>" + STGraphC.getMessage("CHECK.WEBFURTHERCPROPS.TITLE") + "</h3>");
		STNode[] nodes = graph.getNodes();
		StringBuffer s2 = new StringBuffer();
		for(STNode node : nodes) {
			if(!STTools.isEmpty(node.getCProperty("Phase")) && (node.getPhaseCProperty() == null || STTools.isEmpty(node.getCProperty("PhaseNullValue")))) {
				s2.append(getMsg(node, "CHECK.WEBFURTHERCPROPS.WRONGPHASEINFO"));
			}
			if(!STTools.isEmpty(node.getCProperty("InputText")) && node.getInputTextCProperty() == null) {
				s2.append(getMsg(node, "CHECK.WEBFURTHERCPROPS.WRONGINPUTTEXT"));
			}
			if(!STTools.isEmpty(node.getCProperty("OutputText")) && node.getOutputTextCProperty() == null) {
				s2.append(getMsg(node, "CHECK.WEBFURTHERCPROPS.WRONGOUTPUTTEXT"));
			}
		}
		if(s2.length() > 0) {
			s.append(s2);
		} else {
			s.append(STGraphC.getMessage("CHECK.OK"));
		}
		return s;
	}


	/** Build and get the string with the name of the node that
	 * has generated an error and the corresponding message.
	 * @param node the node
	 * @param msg the message
	 * @return string */
	private static String getMsg(final STNode node, final String msg) {
		String n = node.getName();
		return f1 + c1 + n + c3 + n + c2 + STTools.SPACE + STGraphC.getMessage(msg) + f2 + br;
	}

}
