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

import it.liuc.stgraph.action.ExecuteStop;
import it.liuc.stgraph.action.ToolsToggleEdit;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STEdge;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.util.InterruptCondition;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.widget.InputWidget;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.Timer;

import org.nfunk.jep.Node;
import org.nfunk.jep.type.Matrix;
import org.nfunk.jep.type.Tensor;


/** Handle the simulation operations. */
@SuppressWarnings("serial")
public class STGraphExec extends STGraphImpl {
	/** The timer. */
	private Timer timer = new Timer(simulationDelay, new ActionListener() { public void actionPerformed(final ActionEvent e) { timedExecDef(); } });
	/** The batch timer. */
	private Timer batchTimer = new Timer(1, new ActionListener() { public void actionPerformed(final ActionEvent e) { batchExecDef(); } });
	/** Switch to activate the interrupt that turns timed execution off. */
	private boolean interruptSwitch = false;
	/** The state buffer: used to maintain state values for temporary computation. */
	private transient Object[] stateBuffer;
	/** The state buffer2: used to maintain state values for temporary computation. */
	private transient Object[] stateBuffer2;
	/** The state buffer3: used to maintain state values for temporary computation. */
	private transient Object[] stateBuffer3;
	/** The time buffer: used to maintain time values for temporary computation. */
	private transient Object[] timeBuffer = new Object[3];
	/** The to sort node list. */
	private ArrayList<STNode> toSortNodeList;
	/** The list 0 len. */
	private int list0len;
	/** The list 1 len. */
	private int list1len;
	/** The list 2 len. */
	private int list2len;
	/** The lists len. */
	private int listsLen;
	/** The is stepped. */
	private boolean stepped;
	/** Is the first call? */
	private boolean firstCall;
	/** Handle output? */
	private boolean handleOutput = true;
	/** The number of decimals of the system variable <code>timeD</code>: required to correctly round <code>time</code>. */
	private int decNumber;
	/** Is tracing? */
	private boolean tracing = true;
	/** To trace? */
	private boolean toTrace = true;


	/** Class constructor.
	 * @param stream the input stream
	 * @param fileName the file name
	 * @param asResource load as a resource
	 * @param topGraph the top graph
	 * @param superNode the super node */
	public STGraphExec(final InputStream stream, final String fileName, final boolean asResource, final STGraphImpl topGraph, final ModelNode superNode) {
		super(stream, fileName, asResource, topGraph, superNode);
		STInterpreter interpreter = getInterpreter();
		STGraph.getSTC().setCurrentlyComputedGraph(this);
		interpreter.addVariable("__batch", 0); //$NON-NLS-1$
		interpreter.addVariable("__vBatch", 0); //$NON-NLS-1$
	}


	/** Get the timer.
	 * @return timer */
	public final Timer getTimer() { return timer; }


	/** Assign the time-related interpreter values, by specifying the current step and the total number of steps
	 * (<i>recursive for submodels</i>).
	 * @param currStep the current step */
	public final void initTimeBasis(final int currStep) {
		if(!isTopGraph()) {
			setTimeFrame(getTopGraph().getTimeFrame());
			setNumSteps(getTopGraph().getNumSteps());
			setSysTime(getTopGraph().getSysTime());
			setTime0(getTopGraph().getTime0());
			setTime1(getTopGraph().getTime1());
			setTimeD(getTopGraph().getTimeD());
			integrationMethod = getTopGraph().integrationMethod;
		}
		String t = Double.toString(getTimeD());
		int tt;
		decNumber = ((tt = t.indexOf(".")) != -1) ? t.substring(tt).length() : 0; //$NON-NLS-1$
		STInterpreter interpreter = getInterpreter();
		interpreter.addVariable("numSteps", getNumSteps()); //$NON-NLS-1$
		setSysTime(System.nanoTime());
		/* [such vars may be useful to debug web models...]
		if(isForWeb()) {
			if(currStep != -1) {
				this.currStep = currStep;
				currTime = getTime0() + (currStep - 1) * getTimeD();
				interpreter.removeVariable("index"); //$NON-NLS-1$
				interpreter.removeVariable("time"); //$NON-NLS-1$
				interpreter.removeVariable("vIndex"); //$NON-NLS-1$
				interpreter.removeVariable("vTime"); //$NON-NLS-1$
			}
		} else {
			if(currStep != -1) {
				interpreter.addVariable("index", this.currStep = currStep); //$NON-NLS-1$
				interpreter.addVariable("time", currTime = getTime0() + (currStep - 1) * getTimeD()); //$NON-NLS-1$
			} else {
				interpreter.addVariable("index", 0); //$NON-NLS-1$
				interpreter.addVariable("time", 0); //$NON-NLS-1$
			}
		}
		*/

		if(currStep != -1) {
			interpreter.addVariable("index", this.currStep = currStep); //$NON-NLS-1$
			interpreter.addVariable("time", currTime = getTime0() + (currStep - 1) * getTimeD()); //$NON-NLS-1$
			////if(isConnected()) { connector.send("sysTime", NET_DATATYPE, "" + currTime); } //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			interpreter.addVariable("index", 0); //$NON-NLS-1$
			interpreter.addVariable("time", 0); //$NON-NLS-1$
			////if(isConnected()) { connector.send("sysTime", NET_DATATYPE, "0"); } //$NON-NLS-1$ //$NON-NLS-2$
		}

		interpreter.addVariable("time0", getTime0()); //$NON-NLS-1$
		if(currStep != -1) {
			setTime1(getTime0() + (getNumSteps() - 1) * getTimeD());
		}
		interpreter.addVariable("time1", getTime1()); //$NON-NLS-1$
		interpreter.addVariable("timeD", getTimeD()); //$NON-NLS-1$

		//if(!isForWeb()) { setTimeVectors(); }
		setTimeVectors();

		if(isComposed) {
			STDesktop desk;
			for(ModelNode mnode : modelNodeList) {
				if((desk = mnode.getDesk()) != null) { desk.getGraph().initTimeBasis(currStep); }
			}
		}
	}


	/** Set the time-related (i.e., "vIndex" and "vTime") vectors. */
	public void setTimeVectors() {
		double j = getTime0() - getTimeD();
		int v;
		if(getTimeFrame() == STGraphImpl.TIMEFRAME_WINDOWED || getTimeFrame() == STGraphImpl.TIMEFRAME_PLAYMODE) {
			 v = Math.min(getNumSteps(), getMaxSteps());
		} else {
			v = getNumSteps();
		}
		vIndex = Tensor.newVector(v);
		vTime = Tensor.newVector(v);
		for(int i = 0; i < v; i++) {
			vIndex.setScalar(i, i);
			vTime.setScalar(STData.round(j += getTimeD(), decNumber), i);
		}
		getInterpreter().addVariable("vIndex", vIndex); //$NON-NLS-1$
		getInterpreter().addVariable("vTime", vTime); //$NON-NLS-1$
	}


	/** Reset the properties supporting graph computation (<i>recursive for submodels</i>). */
	final void resetComputationalProperties() {
		STNode[] nodes = getNodes();
		if(nodes == null) { return; }
		for(STNode node : nodes) { node.resetComputationalProperties(); }
		resetStepExec();
		if(isComposed) { // operate on subsystems
			STDesktop desk;
			for(ModelNode mnode : modelNodeList) {
				if((desk = mnode.getDesk()) != null) { desk.getGraph().resetComputationalProperties(); }
			}
		}
	}


	/** Control the immediate, non-interactive, execution of the simulation.
	 * @return result */
	public final boolean exec() {
		STGraphC stc = STGraph.getSTC();
		// *** pre-computation procedures ***
		if(getState() != STGraphImpl.STATE_BATCHRUNNING) {
			setState(STGraphImpl.STATE_RUNNING); //*//
			stc.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		stepped = false;
		if(currStep == -1) {
			stc.getLoggerRun().info("calling initTimeBasis..."); //$NON-NLS-1$
			initTimeBasis(-1);
			preCompute(true);
			setFirstCall(true);
			if(!compute()) { endExec(false); return false; }
		}
		//*//if(getState() != STGraphImpl.STATE_BATCHRUNNING) { setState(STGraphImpl.STATE_RUNNING); }
		// *** main computation loop ***
		setFirstCall(false);
		for(int i = currStep; i < getNumSteps() - 1; i++) {
			if(!compute()) { endExec(false); return false; }
		}
		// *** post-computation procedures ***
		if(getState() != STGraphImpl.STATE_BATCHRUNNING) {
			endExec(true);
			////if(isDataSaved()) { saveData(); }
			handleInfoDisplay();
		} else {
			currStep = -1;
			currTime = getTime0() - getTimeD();
			refreshGraph();
		}
		return true;
	}


	/** Control the batch execution of the simulation. */
	public final void batchExec() {
		STInterpreter interpreter = getInterpreter();
		for(STNode node : outputNodeList) {
			node.setProperty(STNode.PROP_BATCHOUTPUT, null);
			interpreter.addVariable("__" + node.getName(), STTools.BLANK); //$NON-NLS-1$
		}
		if(STGraphC.getToolBar() != null) { STGraphC.getToolBar().updateProgressControls(0, 0, true); }
		setState(STGraphImpl.STATE_BATCHRUNNING);
		Tensor vBatch = Tensor.newVector(batchSteps);
		for(int i = 0; i < batchSteps; i++) { vBatch.setScalar(i, i); }
		interpreter.setVarValue("__vBatch", vBatch); //$NON-NLS-1$
		if(batchTimer.isRunning()) { batchTimer.stop(); }
		batchTimer.setDelay(1);
		currBatchStep = 0;
		batchTimer.start();
	}


	/** Helper method for the batch execution. */
	@SuppressWarnings("unchecked")
	final void batchExecDef() {
		STInterpreter interpreter = getInterpreter();
		Object o;
		Vector<Object> v;
		Matrix m;
		if(currBatchStep <= batchSteps) {
			interpreter.addVariable("__batch", currBatchStep); //$NON-NLS-1$
			if(!exec()) {
				endExec(false);
				return;
			}
			for(STNode node : outputNodeList) {
				o = node.getValue();
				if(node.isScalar()) {
					v = (Vector<Object>)node.getProperty(STNode.PROP_BATCHOUTPUT);
					if(v == null) { v = new Vector<Object>(); }
					v.add(o);
					node.setProperty(STNode.PROP_BATCHOUTPUT, v);
					interpreter.addVariable("__" + node.getName(), v); //$NON-NLS-1$
				} else if(node.isVector()) {
					m = (Matrix)node.getProperty(STNode.PROP_BATCHOUTPUT);
					if(m == null) { m = new Matrix(); }
					m.setColumn(currBatchStep, ((Tensor)o).getVector());
					node.setProperty(STNode.PROP_BATCHOUTPUT, m);
					interpreter.addVariable("__" + node.getName(), m); //$NON-NLS-1$
				}
			}
			STToolBar tb = STGraphC.getToolBar();
			if(tb != null && STGraph.getSTC().getCurrentGraph() == this) { tb.updateProgressControls(currBatchStep, batchSteps); }
			currBatchStep++;
		} else {
			endExec(true);
		}
	}


	/** Control the single execution of the simulation.
	 * @param withReset with reset
	 * @param currStep the current step
	 * @return result */
	public final boolean singleExec(final boolean withReset, final int currStep) {
		setState(STGraphImpl.STATE_RUNNING); //*//
		stepped = true;
		initTimeBasis(currStep);
		preCompute(withReset);
		setFirstCall(true);
		if(!compute()) { endExec(false); return false; }
		return true;
	}


	/** Control the stepped execution of the simulation.
	 * @return result */
	public final boolean steppedExec() {
		STGraphC stc = STGraph.getSTC();
		stepped = true;
		setState(STGraphImpl.STATE_STEPPING); //*//
		stc.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if(currStep == -1) {
			stc.getLoggerRun().info("calling initTimeBasis..."); //$NON-NLS-1$
			initTimeBasis(-1);
			preCompute(true);
			setFirstCall(true);
			if(!compute()) { endExec(false); return false; }
			if(stepsBeforePause > 1) {
				setFirstCall(false);
				for(int i = 1; i < stepsBeforePause && currStep < getNumSteps() - 1; i++) {
					if(!compute()) { endExec(false); return false; }
				}
				STToolBar tb = STGraphC.getToolBar();
				if(tb != null && stc.getCurrentGraph() == this) { tb.updateProgressControls((currStep), getNumSteps() - 1); }
			}
		} else {
			setFirstCall(false);
			for(int i = 0; i < stepsBeforePause && currStep < getNumSteps() - 1; i++) {
				if(!compute()) { endExec(false); return false; }
			}
			STToolBar tb = STGraphC.getToolBar();
			if(tb != null && stc.getCurrentGraph() == this) { tb.updateProgressControls((currStep), getNumSteps() - 1); }
		}
		stc.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		if(getState() != STGraphImpl.STATE_STEPPING) {
			//*//setState(STGraphImpl.STATE_STEPPING);
			stc.refreshBars();
			if(!isReadOnly) { setAsEditable(false); }
			if(!isReadOnly || STGraphC.isApplet()) { ((ExecuteStop)STGraphC.getAction("ExecuteStop")).setEnabled(true); } //$NON-NLS-1$
			if(timer.isRunning()) { timer.stop(); }
		}
		if(currStep >= getNumSteps() - 1) {
			endExec(true);
			////if(isDataSaved()) { saveData(); }
		}
		refreshGraph();
		handleInfoDisplay();
		setState(STGraphImpl.STATE_PAUSED); /////////////////////////////
		return true;
	}


	/** Init the thread controlling the timed execution of the simulation. */
	public final void timedExec() {
		stepped = true;
		setState(STGraphImpl.STATE_TIMING); //*//
		if(!isReadOnly) { setAsEditable(false); }
		if(!isReadOnly || STGraphC.isApplet()) { ((ExecuteStop)STGraphC.getAction("ExecuteStop")).setEnabled(true); } //$NON-NLS-1$
		if(currStep == -1) {
			STGraph.getSTC().getLoggerRun().info("calling initTimeBasis..."); //$NON-NLS-1$
			initTimeBasis(-1);
			preCompute(true);
			setFirstCall(true);
			if(!compute()) { endExec(false); return; }
			STToolBar tb = STGraphC.getToolBar();
			if(tb != null && STGraph.getSTC().getCurrentGraph() == this) { tb.updateProgressControls(0, 0, true); }
		}
		//*// setState(STGraphImpl.STATE_TIMING);
		if(timer.isRunning()) {
			setState(STGraphImpl.STATE_PAUSED); /////////////////////////////
			timer.stop();
		} else {
			if(!isReadOnly) { setAsEditable(false); }
			timer.setDelay(simulationDelay);
			timer.start();
		}
	}


	/** Control the timed execution of the simulation.
	 * <br>It is a wrapper for the <code>compute</code> method, repeatedly called
	 * by the <code>Timer</code> thread. */
	final void timedExecDef() {
		stepped = true;
		setFirstCall(false);
		for(int i = 0; i < stepsBeforePause && currStep < getNumSteps() - 1; i++) {
			if(!compute()) { endExec(false); return; }
			if(interruptSwitch) {
				STToolBar tb = STGraphC.getToolBar();
				if(tb != null && STGraph.getSTC().getCurrentGraph() == this) { tb.updateProgressControls(currStep, getNumSteps() - 1); }
				timer.stop();
				refreshGraph();
				handleInfoDisplay();
				return;
			}
		}
		STToolBar tb = STGraphC.getToolBar();
		if(tb != null && STGraph.getSTC().getCurrentGraph() == this) { tb.updateProgressControls(currStep, getNumSteps() - 1); }
		if(currStep >= getNumSteps() - 1) {
			endExec(true);
			////if(isDataSaved()) { saveData(); }
		}
		refreshGraph();
		handleInfoDisplay();
	}


	/** Handle the operations required to stop the execution.
	 * @param isCorrect the is correct */
	public final void endExec(final boolean isCorrect) {
		setState(STGraphImpl.STATE_EDITING);
		STGraph.getSTC().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		batchTimer.stop();
		resetStepExec();
		STStatusBar sb = STGraphC.getStatusBar();
		if(!isCorrect && getLastErrorNode() != null && sb != null) {
			String s = getLastErrorNode().getEvalErrDescription();
			if(s != null) {
				int i = s.indexOf("<br>"); //$NON-NLS-1$
				if(i != -1) { s = s.substring(0, i); }
				STGraphC.getStatusBar().setInfoText(s, 2);
			}
		}
		if(Boolean.parseBoolean(STConfigurator.getProperty("SYSTEM.AUTOGC"))) { //$NON-NLS-1$
			Runtime.getRuntime().gc(); // appropriate to enhance execution interactivity, but increases the global execution time
		}
		if(isDataSaved()) { saveData(); }
	}


	/** Reset the variables related to execution. */
	public final void resetStepExec() {
		ToolsToggleEdit tte = (ToolsToggleEdit)STGraphC.getAction("ToolsToggleEdit"); //$NON-NLS-1$
		if(!isReadOnly && tte == null || (tte != null && tte.getState())) { setAsEditable(true); }
		timer.stop();
		currStep = -1;
		currTime = getTime0() - getTimeD();
		clearEdgeValues();
		refreshGraph();
		STGraph.getSTC().refreshBars();
	}


	/** Generate the edge list and the three main, alphabetically sorted, lists of nodes of this model
	 * (<code>auxiliaryNodeList</code>, <code>stateNodeList</code>, and <code>systemNodeList</code>)
	 * such that each node belongs to one and only one of these lists, together with
	 * two complementary, alphabetically sorted, lists (<code>inputNodeList</code> and <code>outputNodeList</code>). */
	@SuppressWarnings("unchecked")
	public final void setLists() {
		edgeList = getEdges();
		ArrayList<STNode> auxiliaries = new ArrayList<STNode>();
		ArrayList<STNode> states = new ArrayList<STNode>();
		ArrayList<STNode> systems = new ArrayList<STNode>();
		ArrayList<STNode> inputs = new ArrayList<STNode>();
		ArrayList<STNode> outputs = new ArrayList<STNode>();
		for(STNode node : getNodes()) {
			if(node.isModel()) {
				systems.add(node);
			} else if(node.isAlgebraic()) {
				auxiliaries.add(node);
				if(node.isInput()) { inputs.add(node); }
				if(node.isOutput()) { outputs.add(node); }
			} else if(node.isState()) {
				states.add(node);
				if(node.isOutput()) { outputs.add(node); }
			}
		}
		modelNodeList = systems.toArray(new ModelNode[systems.size()]);
		if(modelNodeList != null && modelNodeList.length > 0) {
			isComposed = true;
			Arrays.sort(modelNodeList, STGraph.getSTC().getNodeNameComparator());
		} else {
			isComposed = false;
		}
		auxiliaryNodeList = auxiliaries.toArray(new ValueNode[auxiliaries.size()]);
		if(auxiliaryNodeList != null && auxiliaryNodeList.length > 0) { Arrays.sort(auxiliaryNodeList, STGraph.getSTC().getNodeNameComparator()); }
		stateNodeList = states.toArray(new ValueNode[states.size()]);
		isSequential = false; // just a default
		if(stateNodeList != null && stateNodeList.length > 0) {
			isSequential = true;
			Arrays.sort(stateNodeList, STGraph.getSTC().getNodeNameComparator());
		}
		inputNodeList = inputs.toArray(new ValueNode[inputs.size()]);
		if(inputNodeList != null && inputNodeList.length > 0) {
			isOpen = false;
			for(ValueNode node : inputNodeList) {
				if(!node.isConstant() || node.isBoundToWidget()) { isOpen = true; }
			}
			Arrays.sort(inputNodeList, STGraph.getSTC().getNodeNameComparator());
		} else {
			isOpen = false;
		}
		outputNodeList = outputs.toArray(new ValueNode[outputs.size()]);
		if(outputNodeList != null && outputNodeList.length > 0) { Arrays.sort(outputNodeList, STGraph.getSTC().getNodeNameComparator()); }
	}


	/** Set the sequential property for all graphs (<i>recursive for submodels</i>). */
	private void setSequential() {
		if(isComposed) {
			STGraphExec g;
			for(ModelNode mnode : modelNodeList) {
				if((g = mnode.getSubmodel()) != null) {
					g.setSequential();
					if(g.isSequential) { isSequential = true; }
				}
			}

		}
	}


	/** Setup model (wrapper). */
	public final void setupModel() {
		setGlobalLists();
		initGlobals();
	}


	/** Generate the three main lists of nodes of the whole model (thus including submodels)
	 * (<code>auxiliaryNodeGlobalList</code>, <code>stateNodeGlobalList</code>, and <code>systemNodeGlobalList</code>)
	 * such that each node belongs to one and only one of these lists, together with
	 * two complementary, alphabetically sorted, lists (<code>inputNodeGlobalList</code> and <code>outputNodeGlobalList</code>). */
	private void setGlobalLists() {
		ArrayList<STNode> auxiliaries = new ArrayList<STNode>();
		ArrayList<STNode> states = new ArrayList<STNode>();
		ArrayList<STNode> systems = new ArrayList<STNode>();
		ArrayList<STNode> inputs = new ArrayList<STNode>();
		ArrayList<STNode> outputs = new ArrayList<STNode>();
		for(STNode node : getAllNodes()) {
			node.resetTopologicalProperties();
			if(node.isAlgebraic()) {
				auxiliaries.add(node); 
				if(node.isInput()) {
					if(node.getSaturatingExpression() == null) { inputs.add(node); }
				}
				if(node.isOutput()) { outputs.add(node); }
			} else if(node.isState()) {
				states.add(node);
				if(node.isOutput()) { outputs.add(node); }
			} else if(node.isModel()) {
				systems.add(node);
			}
		}
		auxiliaryNodeGlobalList = auxiliaries.toArray(new ValueNode[auxiliaries.size()]);
		stateNodeGlobalList = states.toArray(new ValueNode[states.size()]);
		modelNodeGlobalList = systems.toArray(new ModelNode[systems.size()]);
		inputNodeGlobalList = inputs.toArray(new ValueNode[inputs.size()]);
		outputNodeGlobalList = outputs.toArray(new ValueNode[outputs.size()]);
		list0len = states.size();

		//System.err.println("aux: " + auxiliaryNodeGlobalList.length);
		//System.err.println("state: " + stateNodeGlobalList.length);
		
		setSequential(); // called here to guarantee that the recursive tree begins from the root

		/*
        System.out.print("auxiliaryNodeGlobalList: "); //$NON-NLS-1$
        for(STNode n : auxiliaryNodeGlobalList) { System.out.print(n.getFullName() + " "); } //$NON-NLS-1$
        System.out.println();
        System.out.print("stateNodeGlobalList: "); //$NON-NLS-1$
        for(STNode n : stateNodeGlobalList) { System.out.print(n.getFullName() + " "); } //$NON-NLS-1$
        System.out.println(); System.out.println();
		*/
	}


	/** Handle the node saturation for submodels. */
	private void initSaturation() {
		if(modelNodeGlobalList == null) { return; }
		STDesktop desk = null;
		for(ModelNode mnode : modelNodeGlobalList) {
			desk = mnode.getDesk();
			if(desk != null) {
				STGraphExec subGraph = desk.getGraph(); 

				STNode[] inputNodes = subGraph.getInputNodes(); // reset all saturations
				if(inputNodes != null) {
					for(STNode in : inputNodes) {
						////ValueNode vn = (ValueNode)in;
						////if(!vn.isCustomFunction()) { vn.setExpression(vn.getFormattedExpression()); }
						in.setConnectedFrom(null); in.setConnectedFrom2(null);
					}
				}

				subGraph.saturatedNodeNames = mnode.getSubVars();
				subGraph.superExpressions = mnode.getSuperExpressions();
				if(subGraph.saturatedNodeNames != null && subGraph.superExpressions != null) {
					STNode iNode = null;
					try {
						for(int i = 0; i < subGraph.saturatedNodeNames.length; i++) { // for all saturated input nodes...
							iNode = subGraph.getNodeByName(subGraph.saturatedNodeNames[i]);
							if(iNode != null) {
								iNode.setSaturatingExpression(subGraph.superExpressions[i]);
								iNode.definedByExpr = mnode.getExpressionVarNodes(mnode.getGraph().getInterpreter(), subGraph.superExpressions[i]);
								if(iNode.definedByExpr != null) {
									for(STNode n : iNode.definedByExpr) { STTools.connect(n, iNode); }
								}								
							}
						}
					} catch (Exception e) { System.err.println("STGraph Error in STGraphExec.initSaturation(): "); e.printStackTrace(); } //$NON-NLS-1$
				}
			}
		}
	}


	/** Initialize the global properties of this model. */
	private final void initGlobals() {
		setupProperties(); // pre-compute the properties
		String le = getLastError(); // wrapper, so to continue the check even in the case of errors
		checkAllDefinitions(true); // pre-check the definitions
		inputWidgetList = getInputWidgets();
		if(le != null) { setLastError(STInterpreter.buildErrorDescription(le)); }
		setRunnable((getLastError() == null) && (getCellCount() != 0) && isTopGraph());
	}


	/** Setup the properties required to correctly operate on nodes and widgets.
	 * <br>It sets the <code>connectedFrom</code> and <code>connectedTo</code> properties for each non-submodel node.<br>
	 * It adds the auxiliary nodes and the state nodes with distinct output to <code>toSortNodeList</code>,
	 * and then sort this list according to the execution priority criterion.<br>
	 * From the point of view of the execution, input and state nodes with no distinct output are assumed
	 * to be "top" nodes, that in each step are evaluated before all others.
	 * In particular, it sets the <code>executionPriority</code> property for the "derived" nodes
	 * and creates the <code>sortedNodeList1</code> and <code>sortedNodeList2</code> lists.
	 * As a side-effect, set the <code>lastError</code> property.<p>
	 * This method is called when the graph is firstly loaded (by the <code>STDesktop()</code> constructor) and whenever
	 * it is changed (by <code>STGraphPanel.graphChanged()</code>). Since submodels are assumed to be read-only,
	 * only in the first case the call is recursive for submodels. */
	@SuppressWarnings("unchecked")
	final void setupProperties() {
		// set the connectedFrom and connectedTo properties 
		for(ValueNode node : auxiliaryNodeGlobalList) {
			node.definedByExpr = node.getExpressionVarNodes(node.getGraph().getInterpreter(), node.getExpression());
			if(node.definedByExpr != null) {
				for(STNode n : node.definedByExpr) { STTools.connect(n, node); }
			}
		}
		for(ValueNode node : stateNodeGlobalList) {
			STInterpreter interpreter = node.getGraph().getInterpreter();
			node.definedByInit = node.getExpressionVarNodes(interpreter, node.getStateInit());
			if(node.definedByInit != null) {
				for(STNode n : node.definedByInit) { STTools.connect(n, node); }
			}
			node.definedByTrans = node.getExpressionVarNodes(interpreter, node.getStateTransition());
			if(node.definedByTrans != null) {
				for(STNode n : node.definedByTrans) { STTools.connect(n, node); }
			}
			if(node.isStateWithOutput()) {
				node.definedByExpr = node.getExpressionVarNodes(interpreter, node.getExpression());
				if(node.definedByExpr != null) {
					for(STNode n : node.definedByExpr) { STTools.connect(n, node); }
				}
			}
		}

		initSaturation();

		if(modelNodeGlobalList != null) {
			STDesktop desk = null;
			for(ModelNode mnode : modelNodeGlobalList) {
				desk = mnode.getDesk();
				if(desk != null) {
					ValueNode[] superInputs = ((STGraphExec)desk.getGraph().getSuperGraph()).getInputNodeList();
					ValueNode[] subInputs = desk.getGraph().getInputNodeList();
					if(superInputs != null && superInputs.length > 0 && subInputs != null && subInputs.length > 0) {
						for(ValueNode n1 : superInputs) {
							if(n1.isGlobal()) {
								for(ValueNode n2 : subInputs) {
									STTools.connect2(n1, n2);
								}
							}
						}
					}
				}
			}
		}

		STNode source;
		STNode target;
		for(STEdge edge : getAllEdges()) {
			source = edge.getSourceNode();
			target = edge.getTargetNode();
			if(source != null && target != null) {
				STTools.connect2wrapper(source, target);

				if(!source.isModel()) {
					edge.setDataSource(source);
				} else { // try to discover the value required by the target node
					if(!target.isModel()) {
						STGraphExec g = ((ModelNode)source).getSubmodel();
						STGraphExec g2;
						ArrayList<STNode> found = new ArrayList<STNode>();
						if(target.definedByInit != null) {
							for(STNode n : target.definedByInit) {
								if(n != null) {
									g2 = n.getGraph();
									if(g2 != null && g2.equals(g)) { found.add(n); }
								}
							}
						}
						if(target.definedByTrans != null) {
							for(STNode n : target.definedByTrans) {
								if(n != null) {
									g2 = n.getGraph();
									if(g2 != null && g2.equals(g)) { found.add(n); }
								}
							}
						}
						if(target.definedByExpr != null) {
							for(STNode n : target.definedByExpr) {
								if(n != null) {
									g2 = n.getGraph();
									if(g2 != null && g2.equals(g)) { found.add(n); }
								}
							}
						}                    	
						if(found.size() == 1) {
							edge.setDataSource(found.get(0));
						} else {
							edge.setDataSource(null);
						}
					} else {
						edge.setDataSource(null); //TODO [submodels]: edge labeling still to complete
					}
				}
			}
		}

		toSortNodeList = new ArrayList<STNode>();

		for(ValueNode node : auxiliaryNodeGlobalList) {
			toSortNodeList.add(node);
			node.fromState = false; // just a default
			if(node.getDefiningNodesByExpressions() == null || node.getDefiningNodesByExpressions().size() == 0 || node.isCustomFunction()) { node.setExecutionPriority(0); }
			if(node.getNodeSubtype() != null) { node.setExpression(); } // handling for custom auxiliary (sub)types: here to allow configuring the node independently of its dialog
		}

		//********************
        //for(STNode n : auxiliaryNodeGlobalList) { System.out.print(n.getFullName() + "[" + n.getExecutionPriority() + "] "); } //$NON-NLS-1$
        //System.out.println();
		//********************

		for(ValueNode node : stateNodeGlobalList) {
			if(node.isStateWithOutput()) {
				toSortNodeList.add(node);
			} else {
				node.setExecutionPriority(0);
			}
			if(node.getNodeSubtype() != null) { node.setStateInit(); node.setStateTransition(); node.setExpression(); } // handling for custom state (sub)types: here to allow configuring the node independently of its dialog
		}

		resetLastError();
		resetLastErrorNode();
		int size;
		sortedNodeList1 = null;
		sortedNodeList2 = null;
		list1len = 0;
		list2len = 0;
		listsLen = 0;
		if(toSortNodeList.size() > 0) {
			int maxPriority = 0;
			ArrayList<STNode> toAssign = toSortNodeList;
			int notAssignedCount = -1;
			int assignedCount = -1;
			while(assignedCount != 0 && notAssignedCount != 0) {
				ArrayList<STNode> notAssigned = new ArrayList<STNode>();
				notAssignedCount = 0; assignedCount = 0;
				for(STNode node : toAssign) {
					if(node.getExecutionPriority() == 0) {
						assignedCount++;
					} else {
						maxPriority = 0;
						if(node.getDefiningNodesByExpressions() == null || node.getDefiningNodesByExpressions().size() == 0) { // still possible, in the case of subsystems
							node.setExecutionPriority(0);
							assignedCount++;
						} else {
							for(STNode nodeFrom : node.getDefiningNodesByExpressions()) {
								if(nodeFrom != null) {
									int p = nodeFrom.getExecutionPriority();
									if(p == -1) {
										maxPriority = 9999;
									} else if(p > maxPriority) {
										maxPriority = p;
									}
								}
							}
							if(maxPriority == 9999) {
								notAssigned.add(node);
								notAssignedCount++;
							} else {
								node.setExecutionPriority(++maxPriority);
								assignedCount++;
							}
						}
					}
				}
				toAssign = notAssigned;
			}

			if(notAssignedCount != 0) {
				StringBuilder nodeNames = new StringBuilder();
				for(int i = 0; i < toAssign.size(); i++) { nodeNames.append(toAssign.get(i).getName() + STTools.SPACE); }
				setLastError(STGraphC.getMessage("ERR.WRONG_TOPOLOGY")); //$NON-NLS-1$
				if(nodeNames.length() > 0) {
					setLastError(getLastError() + " - " + STGraphC.getMessage("ERR.WRONG_TOPOLOGY2") + " " + nodeNames.toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					setLastErrorNode(toAssign.get(0));
				}
			}

			Collections.sort(toSortNodeList, STGraph.getSTC().getPriorityComparator());

			boolean assigned = true; // for each node, identify whether it directly or indirectly depends on states or sequential subsystems
			while(assigned) {
				assigned = false;
				for(STNode node : toSortNodeList) {
					if(!node.fromState) { // if not already recognized
						if(node.getExecutionPriority() > 0) {
							for(STNode n : node.getDefiningNodesByExpressions()) {
								if(n != null && ((n.isState() && !n.isStateWithOutput()) || n.isSequentialModel() || n.fromState)) { node.fromState = assigned = true; }
							}
						}
					}
				}
			}
			ArrayList<STNode> nodeList1 = new ArrayList<STNode>();
			ArrayList<STNode> nodeList2 = new ArrayList<STNode>();
			for(STNode node : toSortNodeList) {
				if(node.fromState) { nodeList2.add(node); }
				else { nodeList1.add(node); }
			}
			list1len = size = nodeList1.size();
			sortedNodeList1 = nodeList1.toArray(new STNode[size]);

			list2len = size = nodeList2.size();
			sortedNodeList2 = nodeList2.toArray(new STNode[size]);
			listsLen = list1len + list2len;

			/*
            System.out.print("sortedNodeList1 (handled by the computeInputs() method): "); //$NON-NLS-1$
            for(STNode n : sortedNodeList1) { System.out.print(n.getFullName() + " "); } //$NON-NLS-1$
            System.out.println();
            System.out.print("stateNodeGlobalList (handled by the computeStateValues() method): "); //$NON-NLS-1$
            for(STNode n : stateNodeGlobalList) { System.out.print(n.getFullName() + " "); } //$NON-NLS-1$
            System.out.println();
            System.out.print("sortedNodeList2 (handled by the computeSync() method): "); //$NON-NLS-1$
            for(STNode n : sortedNodeList2) { System.out.print(n.getFullName() + " "); } //$NON-NLS-1$
            System.out.println(); System.out.println();
			*/
		}
		resetStepExec();
	}


	/** Helper method to deal with RK / multi-step integration. */
	private void saveComputationalState() {
		if(list0len > 0) {
			stateBuffer = new Object[list0len];
			stateBuffer2 = new Object[list0len];
			for(int i = 0; i < list0len; i++) {
				stateBuffer[i] = stateNodeGlobalList[i].getNextState();
				stateBuffer2[i] = stateNodeGlobalList[i].getValue();
			}
		}
		if(listsLen > 0) {
			stateBuffer3 = new Object[list1len + list2len];
			for(int i = 0; i < list1len; i++) { stateBuffer3[i] = sortedNodeList1[i].getValue(); }
			for(int i = 0; i < list2len; i++) { stateBuffer3[list1len + i] = sortedNodeList2[i].getValue(); }
		}
	}


	/** Helper method to deal with RK / multi-step integration (<i>recursive for submodels</i>). */
	private void saveTime() {
		timeBuffer[0] = Integer.valueOf(currStep);
		timeBuffer[1] = Tensor.newScalar(currTime);
		timeBuffer[2] = Tensor.newScalar(getTimeD());
		if(isComposed) { // operate on subsystems
			STGraphExec g;
			for(ModelNode mnode : modelNodeList) {
				g = mnode.getSubmodel();
				if(g != null) { g.saveTime(); }
			}
		}
	}


	/** Helper method to deal with RK / multi-step integration. */
	private void restoreComputationalState() {
		Object t;
		for(int i = 0; i < list0len; i++) {
			t = stateBuffer[i];
			if(t != null) { stateNodeGlobalList[i].setNextState(t); }
			t = stateBuffer2[i];
			if(t != null) {
				stateNodeGlobalList[i].setValue(t);
				stateNodeGlobalList[i].getGraph().getInterpreter().addVariable(stateNodeGlobalList[i].getName(), t);
			}
		}
		for(int i = 0; i < list1len; i++) {
			t = stateBuffer3[i];
			if(t != null) {
				sortedNodeList1[i].setValue(t);
				sortedNodeList1[i].getGraph().getInterpreter().addVariable(sortedNodeList1[i].getName(), t);
			}
		}
		for(int i = 0; i < list2len; i++) {
			t = stateBuffer3[list1len + i];
			if(t != null) {
				sortedNodeList2[i].setValue(t);
				sortedNodeList2[i].getGraph().getInterpreter().addVariable(sortedNodeList2[i].getName(), t);
			}
		}
	}


	/** Helper method to deal with RK / multi-step integration (<i>recursive for submodels</i>). */
	private void restoreTime() {
		currStep = ((Integer)timeBuffer[0]).intValue();
		currTime = ((Tensor)timeBuffer[1]).getValue();
		setTimeD(((Tensor)timeBuffer[2]).getValue());
		if(isComposed) { // operate on subsystems
			STGraphExec g;
			for(ModelNode mnode : modelNodeList) {
				g = mnode.getSubmodel();
				if(g != null) { g.restoreTime(); }
			}
		}
	}


	/** Helper method to deal with RK / multi-step integration (<i>recursive for submodels</i>).
	 * @param t the t */
	private void setTempTimeD(final double t) {
		setTimeD(t);
		if(isComposed) { // operate on subsystems
			STGraphExec g;
			for(ModelNode mnode : modelNodeList) {
				g = mnode.getSubmodel();
				if(g != null) { g.setTempTimeD(t); }
			}
		}
	}


	/** Helper method to deal with RK / multi-step integration (<i>recursive for submodels</i>).
	 * @param p the p */
	public void setIntegrationPhase(final int p) {
		integrationPhase = p;
		if(isComposed) { // operate on subsystems
			STGraphExec g;
			for(ModelNode mnode : modelNodeList) {
				g = mnode.getSubmodel();
				if(g != null) { g.setIntegrationPhase(p); }
			}
		}
	}


	/** Set if it is the first call (<i>recursive for submodels</i>).
	 * @param firstCall the firstCall */
	private void setFirstCall(final boolean firstCall) {
		this.firstCall = firstCall;
		if(isComposed) { // operate on subsystems
			STGraphExec g;
			for(ModelNode mnode : modelNodeList) {
				g = mnode.getSubmodel();
				if(g != null) { g.setFirstCall(firstCall); }
			}
		}
	}


	/** Get whether it is the first call.
	 * @return firstCall */	
	public boolean isFirstCall() { return firstCall; }


	/** Check that the model nodes are correctly defined.
	 * <br>As a side-effect, set the <code>lastError</code> property.
	 * @param global check also submodels */
	final void checkAllDefinitions(final boolean global) {
		String t;
		resetLastError();
		resetLastErrorNode();
		if(global) {
			for(STNode node : auxiliaryNodeGlobalList) {
				if((t = node.checkDefinition(node.getGraph().getInterpreter())) != null && getLastError() == null) { setLastError(STInterpreter.buildErrorDescription(t)); setLastErrorNode(node); }
			}
			for(ValueNode node : stateNodeGlobalList) {
				if((t = node.checkDefinition(node.getGraph().getInterpreter())) != null && getLastError() == null) { setLastError(STInterpreter.buildErrorDescription(t)); setLastErrorNode(node); }
			}
			for(ModelNode node : modelNodeGlobalList) {
				if((t = node.checkDefinition(node.getGraph().getInterpreter())) != null && getLastError() == null) { setLastError(STInterpreter.buildErrorDescription(t)); setLastErrorNode(node); }
			}
		} else {
			for(STNode node : getNodes()) {
				if((t = node.checkDefinition(getInterpreter())) != null && getLastError() == null) { setLastError(STInterpreter.buildErrorDescription(t)); setLastErrorNode(node); }
			}
		}
	}


	/** Set the time-related system-defined variables (<i>recursive for submodels</i>). */
	final void setIndexAndTime() {
		/* [such vars may be useful to debug web models...]
		if(isForWeb()) {
			currStep++;
			currTime += getTimeD();
		} else {
			STInterpreter interpreter = getInterpreter();
			interpreter.setVarValue("index", Tensor.newScalar(++currStep)); //$NON-NLS-1$
			interpreter.setVarValue("time", Tensor.newScalar(STData.round(currTime += getTimeD(), decNumber))); //$NON-NLS-1$
			if((getTimeFrame() == STGraphImpl.TIMEFRAME_WINDOWED || getTimeFrame() == STGraphImpl.TIMEFRAME_PLAYMODE) && currStep >= getMaxSteps()) {
				double[] indexList = vIndex.getValues();
				double[] timeList = vTime.getValues();
				int num = indexList.length;
				for(int i = 1; i < num; i++) {
					indexList[i - 1] = indexList[i];
					timeList[i - 1] = timeList[i];
				}
				indexList[num - 1] = currStep;
				timeList[num - 1] = currTime;
			}
		}
		*/
		STInterpreter interpreter = getInterpreter();
		interpreter.setVarValue("index", Tensor.newScalar(++currStep)); //$NON-NLS-1$
		interpreter.setVarValue("time", Tensor.newScalar(STData.round(currTime += getTimeD(), decNumber))); //$NON-NLS-1$
		////if(isConnected()) { connector.send("sysTime", NET_DATATYPE, "" + currTime); } //$NON-NLS-1$ //$NON-NLS-2$
		if((getTimeFrame() == STGraphImpl.TIMEFRAME_WINDOWED || getTimeFrame() == STGraphImpl.TIMEFRAME_PLAYMODE) && currStep >= getMaxSteps()) {
			double[] indexList = vIndex.getValues();
			double[] timeList = vTime.getValues();
			int num = indexList.length;
			for(int i = 1; i < num; i++) {
				indexList[i - 1] = indexList[i];
				timeList[i - 1] = timeList[i];
			}
			indexList[num - 1] = currStep;
			timeList[num - 1] = currTime;
		}

		if(isComposed) { // operate recursively on subsystems
			STGraphExec g;
			for(ModelNode mnode : modelNodeList) {
				g = mnode.getSubmodel();
				if(g != null) { g.setIndexAndTime(); }
			}
		}
	}


	/** Execute the first step of the simulation, for interactive purposes. */
	public final void computeInteractively() {
		resetLastError();
		resetLastErrorNode();
		setState(STGraphImpl.STATE_PRE_EVALUATING);
		stepped = false;
		preCompute(true);
		setFirstCall(true);
		if(!compute()) { endExec(false); }
		setState(STGraphImpl.STATE_EDITING);
	}


	/** Execute interactively one step of the simulation. */
	public final void computeInteractivelyOnTheFly() {
		int x = getState();
		setState(STGraphImpl.STATE_PAUSED);
		compute();
		setState(x);
	}


	/** Initialize the execution environment.
	 * @param withReset with reset */
	final void preCompute(final boolean withReset) {
		STStatusBar sb = STGraphC.getStatusBar();
		if(sb != null) { sb.setInfoText(STTools.BLANK, 0); }
		if(modelNodeGlobalList != null) {
			for(ModelNode node : modelNodeGlobalList) { node.setEvalErrDescription(null); } // model nodes are not evaluated, so that their error description is not reset by the interpreter
		}
		if(withReset) { resetComputationalProperties(); }
		preParseExpressions(); // pre-parse node expressions
		startingExecution = true;
		refreshGraph();
		startingExecution = false;
	}


	/** Execute one step of the simulation, by dealing with the integration method.
	 * <br>The general logic of the computation is the following:
	 * <ul><li><code>computeInputs()</code>
	 * <li><code>computeStateValues()</code>
	 * <li><code>computeSync()</code>
	 * <li><code>computeNextStates()</code></ul>
	 * @return result */
	public final boolean compute() {
		if(getState() != STGraphImpl.STATE_PAUSED) { setIndexAndTime(); } // condition added on Feb 22 to improve interaction via input widgets
		tracing = STTools.tracingDialog.isVisible();
		if(tracing) {
			if(stepped) {
				STTools.tracingDialog.setData("<b>" + STGraphC.getMessage("SYSTEM.INFO.CURRENTSTEP") + ":</b> " + currStep); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if(integrationMethod == RK2) {
					STTools.tracingDialog.appendData(STTools.SPACE + STGraphC.getMessage("SYSTEM.INFO.CURRENTSTEP.RKA") + " 3 " + STGraphC.getMessage("SYSTEM.INFO.CURRENTSTEP.RKB"), false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} else if(integrationMethod == RK23) {
					STTools.tracingDialog.appendData(STTools.SPACE + STGraphC.getMessage("SYSTEM.INFO.CURRENTSTEP.RKA") + " 5 " + STGraphC.getMessage("SYSTEM.INFO.CURRENTSTEP.RKB"), false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			} else {
				STTools.tracingDialog.setData("<b>" + STGraphC.getMessage("SYSTEM.INFO.NOACTIVE") + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		toTrace = tracing && stepped;
			if(integrationMethod == EULER || firstCall) {
			setIntegrationPhase(1);
			if(!computeInputs()) { return false; }
			if(!computeStateValues()) { return false; }
			if(!computeSync()) { return false; }
			if(!computeNextStates()) { return false; }
		} else if(integrationMethod == RK2) {
			saveComputationalState();
			saveTime();
			setIntegrationPhase(1);
			setTempTimeD(0.75 * getTimeD());
			if(!computeInputs()) { return false; }
			if(!computeStateValues()) { return false; }
			if(!computeSync()) { return false; }
			if(!computeNextStates()) { return false; }
			setIntegrationPhase(2);
			restoreTime();
			if(!computeStateValues()) { return false; }
			if(!computeSync()) { return false; }
			if(!computeNextStates()) { return false; }
			restoreComputationalState();
			setIntegrationPhase(3);
			if(!computeInputs()) { return false; }
			if(!computeStateValues()) { return false; }
			if(!computeSync()) { return false; }
			if(!computeNextStates()) { return false; }
		} else if(integrationMethod == RK23) {
			saveComputationalState();
			saveTime();
			setIntegrationPhase(1);
			setTempTimeD(0.5 * getTimeD());
			if(!computeInputs()) { return false; }
			if(!computeStateValues()) { return false; }
			if(!computeSync()) { return false; }
			if(!computeNextStates()) { return false; }
			setIntegrationPhase(2);
			restoreTime();
			if(!computeStateValues()) { return false; }
			if(!computeSync()) { return false; }
			if(!computeNextStates()) { return false; }
			restoreComputationalState();
			setIntegrationPhase(3);
			setTempTimeD(0.75 * getTimeD());
			if(!computeInputs()) { return false; }
			if(!computeStateValues()) { return false; }
			if(!computeSync()) { return false; }
			if(!computeNextStates()) { return false; }
			setIntegrationPhase(4);
			restoreTime();
			if(!computeStateValues()) { return false; }
			if(!computeSync()) { return false; }
			if(!computeNextStates()) { return false; }
			restoreComputationalState();
			setIntegrationPhase(5);
			if(!computeInputs()) { return false; }
			if(!computeStateValues()) { return false; }
			if(!computeSync()) { return false; }
			if(!computeNextStates()) { return false; }
		}

		if(handleOutput && getState() != STGraphImpl.STATE_PAUSED) { // 2nd condition added on Feb 22 to improve interaction via input widgets
			if(!handleOutput()) {
				if(continueWithExceptions()) { ; } else { return errorHandler(null, STGraph.getSTC().getCurrentlyComputedNode(), "ERR.COMP.HISTORY"); } //$NON-NLS-1$
			}
		}
		if(isShowEdgeLabels() && stepped) { setEdgeValues(); }
		
		if(isWithInterrupts() && existInterrupts && (getState() == STGraphImpl.STATE_STEPPING || getState() == STGraphImpl.STATE_TIMING)) {
			refreshGraph(); handleInfoDisplay(); //repaint widgets, so to present updated data
			interruptSwitch = false;
			int t = handleInterruptConditions();
			if(t == InterruptCondition.STOP_ACTION) { return false; }
			if(t == InterruptCondition.CONTINUE_ACTION) {
				if(currStep < getNumSteps() - 1) { interruptSwitch = true; return true; }
				return false;
			}
		}
		
		return true;
	}


	/** There exist interrupts?. */
	private boolean existInterrupts;
	/** The list of the checked nodes for interrupts. */
	private ArrayList<STNode> checkedNodes;
	/** The list of pre-parsed conditions for interrupts. */
	private ArrayList<Node> interruptParsedConditions;
	/** The list of actions to be performed in case of interrupt. */
	private ArrayList<String> interruptActions;

	/** Pre-parse the interrupt conditions for this model.
	 * <br>The checked conditions are:
	 * <li><code>OnBelowMin</code>: the current value is less than the custom property <code>Min</code>
	 * <li><code>OnAboveMax</code>: the current value is greater than the custom property <code>Max</code>
	 * <li><code>OnZero</code>: the current value is equal to zero
	 * <li><code>OnTrue</code>: the current value is greater than zero
	 * <li><code>OnFalse</code>: the current value is equal or less than zero
	 * <br>The actions are dealt with by (and documented in) the <code>it.liuc.stgraph.util.InterruptCondition</code> class. */
	private void preParseInterruptConditions() {
		existInterrupts = false;
		checkedNodes = new ArrayList<STNode>();
		interruptParsedConditions = new ArrayList<Node>();
		interruptActions = new ArrayList<String>();
		String s1;
		String s2;
		for(STNode n : getNodes()) {
			if(!STTools.isEmpty(s1 = n.getCProperty("Min")) && !STTools.isEmpty(s2 = n.getCProperty("OnBelowMin"))) { //$NON-NLS-1$ //$NON-NLS-2$
				checkedNodes.add(n);
				interruptParsedConditions.add(getInterpreter().preParseExpression(n.getName() + "<" + s1).get(0)); //$NON-NLS-1$
				interruptActions.add(s2);
			}
			if(!STTools.isEmpty(s1 = n.getCProperty("Max")) && !STTools.isEmpty(s2 = n.getCProperty("OnAboveMax"))) { //$NON-NLS-1$ //$NON-NLS-2$
				checkedNodes.add(n);
				interruptParsedConditions.add(getInterpreter().preParseExpression(n.getName() + ">" + s1).get(0)); //$NON-NLS-1$
				interruptActions.add(s2);
			}
			/**/
			if(!STTools.isEmpty(s2 = n.getCProperty("OnZero"))) { //$NON-NLS-1$
				checkedNodes.add(n);
				interruptParsedConditions.add(getInterpreter().preParseExpression(n.getName() + "==0.0").get(0)); //$NON-NLS-1$
				interruptActions.add(s2);
			}
			if(!STTools.isEmpty(s2 = n.getCProperty("OnTrue"))) { //$NON-NLS-1$
				checkedNodes.add(n);
				interruptParsedConditions.add(getInterpreter().preParseExpression(n.getName() + ">0.0").get(0)); //$NON-NLS-1$
				interruptActions.add(s2);
			}
			if(!STTools.isEmpty(s2 = n.getCProperty("OnFalse"))) { //$NON-NLS-1$
				checkedNodes.add(n);
				interruptParsedConditions.add(getInterpreter().preParseExpression(n.getName() + "<=0.0").get(0)); //$NON-NLS-1$
				interruptActions.add(s2);
			}
			/**/
		}
		if(checkedNodes.size() > 0) { existInterrupts = true; }
	}


	/** Compute the interrupt conditions for this model.
	 * @return result as in <code>it.liuc.stgraph.util.InterruptCondition.handle()</code> */
	private int handleInterruptConditions() {
		if(STGraph.getExecMode() == STGraph.EXECMODE_NOGUIENGINE) { return InterruptCondition.NO_ACTION; }
		for(int i = 0; i < checkedNodes.size(); i++) {
			if(getInterpreter().evalCondition(interruptParsedConditions.get(i))) {
				return InterruptCondition.handle(checkedNodes.get(i), interruptActions.get(i));
			}
		}
		return InterruptCondition.NO_ACTION;
	}


	/** Pre-parse expressions.
	 * <br>Called when starting the execution of the computeFirst() method,
	 * it stores the pre-parsed tree in the <code>topOfTree*</code> properties of each node,
	 * thus obtaining a significant reduction in execution time. */
	private void preParseExpressions() {
		STInterpreter interpreter;
		for(ValueNode node : auxiliaryNodeGlobalList) {
			interpreter = node.getGraph().getInterpreter();
			if(!node.isBoundToExternalControl()) {
				String s = node.getSaturatingExpression();
				if(s == null) {
					node.topOfTreeValue = interpreter.preParseExpression(node.getExpression());
				} else {
					node.topOfTreeValue = node.getGraph().getSuperGraph().getInterpreter().preParseExpression(node.getSaturatingExpression());
				}
				setNodeValue(node, null);
			} else {
				//node.topOfTreeValue = interpreter.preParseExpression(node.getValue().toString());
				//setNodeValue(node, interpreter.evalExpression(node));
			}
		}
		for(ValueNode node : stateNodeGlobalList) {
			interpreter = node.getGraph().getInterpreter();
			node.topOfTreeState = interpreter.preParseExpression(node.getStateInit());
			node.topOfTreeValue = node.isStateWithOutput() ? interpreter.preParseExpression(node.getExpression()) : node.topOfTreeState;
			setNodeValue(node, null);
		}
		preParseInterruptConditions();
	}


	/** Helper method for tracing.
	 * @param node */
	private void writeStateNodeHeaderToTrace(final STNode node) {
		if(node.isStateWithOutput()) {
			STTools.tracingDialog.appendData("<u>" + node.getFullName() + "</u> [" + STTools.adaptToHTML(((ValueNode)node).getExpression()) + "]", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			if(firstCall) {
				STTools.tracingDialog.appendData("<u>" + node.getFullName() + "</u> [" + STTools.adaptToHTML(((ValueNode)node).getStateInit()) + "]", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				STTools.tracingDialog.appendData("<u>" + node.getFullName() + "</u> [" + STTools.adaptToHTML(((ValueNode)node).getStateTransition()) + "]", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}


	/** Helper method for tracing (see also STFunction.checkStack()).
	 * @param node */
	private void writeNodeHeaderToTrace(final STNode node) {
		STTools.tracingDialog.appendData("<u>" + node.getFullName() + "</u> [" + STTools.adaptToHTML(((ValueNode)node).getExpression()) + "]", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}


	/** Helper method for tracing.
	 * @param node */
	private void writeBoundNodeHeaderToTrace(final STNode node) {
		STTools.tracingDialog.appendData("<u>" + node.getFullName() + "</u> [<i>bound</i>]", true); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/** Helper method for tracing.
	 * @param node
	 * @param out */
	private void writeEvalToTrace(final STNode node, final Object out) {
		STTools.tracingDialog.appendData(" --> " + ((out == null) ? "null" : STData.toString(out, STData.FORMAT_ALTERNATE)), false); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/** Execute a single step of the input component of the simulation.
	 * @return result */
	final boolean computeInputs() {
		if(sortedNodeList1 == null || sortedNodeList1.length == 0) { return true; }
		STGraphExec g; STInterpreter interpreter; Object out = null; Object theMe = null;
		try {
			////if(toTrace) { STTools.tracingDialog.appendData("<b>INPUTS</b><br>"); } //$NON-NLS-1$
			for(STNode node : sortedNodeList1) {
				g = node.getGraph(); STGraph.getSTC().setCurrentlyComputedGraph(g); interpreter = g.getInterpreter(); STGraph.getSTC().setCurrentlyComputedNode(node);
				if(node.isInput()) {
					if(!node.isBoundToExternalControl()) {
						if(node.isBoundToWidget() && node.getSaturatingExpression() == null) { // input widgets in submodels operate only if not saturated in the top graph...
							if(toTrace) { writeBoundNodeHeaderToTrace(node); }
							out = ((InputWidget)node.getInputWidget()).getNextValue(currStep == 0);
							if(toTrace) { writeEvalToTrace(node, out); }
						} else {
							if(toTrace) { writeNodeHeaderToTrace(node); }
							out = interpreter.evalExpression(node); // default evaluation
							if(toTrace) { writeEvalToTrace(node, out); }
							if(out == null) { if(continueWithExceptions()) { out = STTools.ZERO; } else { return errorHandler(null, STGraph.getSTC().getCurrentlyComputedNode(), firstCall ? "ERR.COMP.NULLVALUE.FIRSTCALL" : "ERR.COMP.NULLVALUE"); } } //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				} else {
					if(node.isAlgebraic()) {
						if(toTrace) { writeNodeHeaderToTrace(node); }
						out = interpreter.evalExpression(node);
						if(toTrace) { writeEvalToTrace(node, out); }
						if(out == null) {  if(continueWithExceptions()) { out = STTools.ZERO; } else { return errorHandler(null, STGraph.getSTC().getCurrentlyComputedNode(), firstCall ? "ERR.COMP.NULLVALUE.FIRSTCALL" : "ERR.COMP.NULLVALUE"); } } //$NON-NLS-1$ //$NON-NLS-2$
					} else if(node.isState()) {
						((ValueNode)node).setCurrentState(g.thethis = (Tensor)(firstCall ? interpreter.evalExpression(node, node.topOfTreeState) : ((ValueNode)node).getNextState()));
						interpreter.setVarValue(STInterpreter.THIS, g.thethis);
						if(node.isStateWithOutput()) {
							if(firstCall && integrationPhase == 1) {
			        			node.topOfTreeState = interpreter.preParseExpression(((ValueNode)node).getStateTransition());
							}
							interpreter.setVarValue(STInterpreter.ME, theMe = interpreter.evalExpression(node, node.topOfTreeState));
							((ValueNode)node).setNextState(theMe);
						}
						if(toTrace) { writeStateNodeHeaderToTrace(node); }
						out = interpreter.evalExpression(node);
						if(toTrace) { writeEvalToTrace(node, out); }
						if(out == null) { if(continueWithExceptions()) { out = STTools.ZERO; } else { return errorHandler(null, STGraph.getSTC().getCurrentlyComputedNode(), firstCall ? "ERR.COMP.NULLVALUE.FIRSTCALL" : "ERR.COMP.NULLVALUE"); } } //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				if(!node.isBoundToExternalControl()) { setNodeValue(node, out); }
				if(this != g && node.isOutput()) { // a submodel.output node, whose value must be updated in the supermodel
					g.getSuperGraph().getInterpreter().addVariable(g.getSuperNode().getName() + STTools.DOT + node.getName(), out);
				}
			}
		} catch (Exception ex) { // exception handling
			if(continueWithExceptions()) { return true; } return errorHandler(ex, STGraph.getSTC().getCurrentlyComputedNode(), "ERR.COMP.INPUTS"); //$NON-NLS-1$
		}
		return true;
	}


	/** Execute a single step of the asynchronous component of the simulation.
	 * @return result */
	final boolean computeStateValues() {
		if(stateNodeGlobalList == null || stateNodeGlobalList.length == 0) { return true; }
		STGraphExec g; STInterpreter interpreter; Object out = null;
		try {
			////if(toTrace) { STTools.tracingDialog.appendData("<b>STATES</b><br>"); } //$NON-NLS-1$
			for(ValueNode node : stateNodeGlobalList) {
				g = node.getGraph(); STGraph.getSTC().setCurrentlyComputedGraph(g); interpreter = g.getInterpreter(); STGraph.getSTC().setCurrentlyComputedNode(node);
            	if(!node.isStateWithOutput()) {
    				if(toTrace) { writeStateNodeHeaderToTrace(node); }
    				out = firstCall ? interpreter.evalExpression(node) : node.getNextState();
					if(toTrace) { writeEvalToTrace(node, out); }
        			if(out == null) { if(continueWithExceptions()) { out = STTools.ZERO; } else { return errorHandler(null, STGraph.getSTC().getCurrentlyComputedNode(), firstCall ? "ERR.COMP.NULLVALUE.FIRSTCALL" : "ERR.COMP.NULLVALUE"); } } //$NON-NLS-1$ //$NON-NLS-2$
        			setNodeValue(node, out);
        		}
				if(this != g && node.isOutput() && !node.isStateWithOutput()) { // a submodel.output node, whose value must be updated in the supermodel
						// ************ added "&& !node.isStateWithOutput()" on Nov 21
					g.getSuperGraph().getInterpreter().addVariable(g.getSuperNode().getName() + STTools.DOT + node.getName(), out);
				}
			}
		} catch (Exception ex) { // exception handling
			if(continueWithExceptions()) { return true; } return errorHandler(ex, STGraph.getSTC().getCurrentlyComputedNode(), "ERR.COMP.STATES"); //$NON-NLS-1$
		}
		return true;
	}


	/** Execute a single step of the synchronous component of the simulation.
	 * @return result */
	final boolean computeSync() {
		if(sortedNodeList2 == null || sortedNodeList2.length == 0) { return true; }
		STGraphExec g; STInterpreter interpreter; Object out = null; Object theMe = null;
		try {
			////if(toTrace) { STTools.tracingDialog.appendData("<b>SYNCS</b><br>"); } //$NON-NLS-1$
			for(STNode node : sortedNodeList2) {
				g = node.getGraph(); STGraph.getSTC().setCurrentlyComputedGraph(g); interpreter = g.getInterpreter(); STGraph.getSTC().setCurrentlyComputedNode(node);
				if(node.isAlgebraic()) {
					if(node.isInput()) {
						if(node.isBoundToWidget() && node.getSaturatingExpression() == null) {
							if(toTrace) { writeBoundNodeHeaderToTrace(node); }
							out = ((InputWidget)node.getInputWidget()).getNextValue(currStep == 0);
							if(toTrace) { writeEvalToTrace(node, out); }
						} else {
							if(toTrace) { writeNodeHeaderToTrace(node); }
							out = interpreter.evalExpression(node); // default evaluation
							if(toTrace) { writeEvalToTrace(node, out); }
							if(out == null) { if(continueWithExceptions()) { out = STTools.ZERO; } else { return errorHandler(null, STGraph.getSTC().getCurrentlyComputedNode(), firstCall ? "ERR.COMP.NULLVALUE.FIRSTCALL" : "ERR.COMP.NULLVALUE"); } } //$NON-NLS-1$ //$NON-NLS-2$
						}
					} else {
						if(toTrace) { writeNodeHeaderToTrace(node); }
						out = interpreter.evalExpression(node);
						if(toTrace) { writeEvalToTrace(node, out); }
						if(out == null) {  if(continueWithExceptions()) { out = STTools.ZERO; } else { return errorHandler(null, STGraph.getSTC().getCurrentlyComputedNode(), firstCall ? "ERR.COMP.NULLVALUE.FIRSTCALL" : "ERR.COMP.NULLVALUE"); } } //$NON-NLS-1$ //$NON-NLS-2$
					}
				} else if(node.isState()) {
					((ValueNode)node).setCurrentState(g.thethis = (Tensor)(firstCall ? interpreter.evalExpression(node, node.topOfTreeState) : ((ValueNode)node).getNextState()));
					interpreter.setVarValue(STInterpreter.THIS, g.thethis);
					if(node.isStateWithOutput()) {
						if(firstCall && integrationPhase == 1) { node.topOfTreeState = interpreter.preParseExpression(((ValueNode)node).getStateTransition()); }
						interpreter.setVarValue(STInterpreter.ME, theMe = interpreter.evalExpression(node, node.topOfTreeState));
						((ValueNode)node).setNextState(theMe);
					}
					if(toTrace) { writeStateNodeHeaderToTrace(node); }
					out = interpreter.evalExpression(node);
					if(toTrace) { writeEvalToTrace(node, out); }
					if(out == null) { if(continueWithExceptions()) { out = STTools.ZERO; } else { return errorHandler(null, STGraph.getSTC().getCurrentlyComputedNode(), firstCall ? "ERR.COMP.NULLVALUE.FIRSTCALL" : "ERR.COMP.NULLVALUE"); } } //$NON-NLS-1$ //$NON-NLS-2$
				}
				if(!node.isBoundToExternalControl()) { setNodeValue(node, out); }
				if(this != g && node.isOutput()) { // a submodel.output node, whose value must be updated in the supermodel
					g.getSuperGraph().getInterpreter().addVariable(g.getSuperNode().getName() + STTools.DOT + node.getName(), out);
				}
			}
		} catch (Exception ex) { // exception handling
			if(continueWithExceptions()) { return true; } return errorHandler(ex, STGraph.getSTC().getCurrentlyComputedNode(), "ERR.COMP.ALGEBRAICS"); //$NON-NLS-1$
		}
		return true;
	}


	/** Execute a single step of the asynchronous component of the simulation.
	 * @return result */
	final boolean computeNextStates() {
		if(stateNodeGlobalList == null || stateNodeGlobalList.length == 0) { return true; }
		if(getState() == STGraphImpl.STATE_PAUSED) { return true; } // added on Feb 22 to improve interaction via input widgets
		STGraphExec g; STInterpreter interpreter; Tensor nextState = null;
		try {
			for(ValueNode node : stateNodeGlobalList) { // assign the new value to states
				g = node.getGraph(); STGraph.getSTC().setCurrentlyComputedGraph(g); interpreter = g.getInterpreter(); STGraph.getSTC().setCurrentlyComputedNode(node);
				if(firstCall && integrationPhase == 1) {
        			node.topOfTreeState = interpreter.preParseExpression(node.getStateTransition());
        			if(node.isStateWithOutput()) {
    					interpreter.setVarValue(STInterpreter.THIS, g.thethis = (Tensor)node.getCurrentState());
    					g.thethis = (Tensor)interpreter.evalExpression(node, node.topOfTreeState);
        			} else {
        				g.thethis = (Tensor)node.getValue();
        			}
        		} else {
        			g.thethis = (Tensor)(node.isStateWithOutput() ? node.getCurrentState() : node.getValue()); // a plausible bug (see the two lines below...) fixed on Nov 21
        			//g.thethis = (Tensor)(node.isStateWithOutput() ? node.getNextState() : node.getValue()); ****************
        			//g.thethis = (Tensor)node.getValue(); ***********************
        		}
				node.setCurrentState(g.thethis);
				if(firstCall && integrationPhase == 1 && node.isStateWithOutput()) {
					nextState = g.thethis;
				} else {
					if(node.isStateWithOutput()) {
						nextState = (Tensor)node.getNextState();
					} else {
						interpreter.setVarValue(STInterpreter.THIS, g.thethis);
						nextState = (Tensor)interpreter.evalExpression(node, node.topOfTreeState);	
					}					
				}
				if(nextState == null) { // exception handling
					if(continueWithExceptions()) { nextState = STTools.ZERO; } else { return errorHandler(null, STGraph.getSTC().getCurrentlyComputedNode(), firstCall ? "ERR.COMP.NULLVALUE.FIRSTCALL" : "ERR.COMP.NULLVALUE"); } //$NON-NLS-1$ //$NON-NLS-2$
				}
				node.setNextState(nextState);
			}
		} catch (Exception ex) { // exception handling
			if(continueWithExceptions()) { return true; } return errorHandler(ex, STGraph.getSTC().getCurrentlyComputedNode(), "ERR.COMP.NEXTSTATES"); //$NON-NLS-1$
		}
		return true;
	}


	/** Error handler.
	 * @param ex the exception
	 * @param node the node
	 * @param type the type
	 * @return result */
	private boolean errorHandler(final Exception ex, final STNode node, final String type) {
		if(getState() == STGraphImpl.STATE_PRE_EVALUATING) { return true; }
		String err = STTools.BLANK;
		try { err = node.getName() + ": "; } catch (Exception ex2) { ; } //$NON-NLS-1$
		String err2 = STTools.BLANK;
		if(ex != null) { err2 = ex.toString(); }
		if(getInterpreter().getErrorList().size() > 0) { err2 = STInterpreter.buildErrorDescription((String)getInterpreter().getErrorList().get(0)); }
		STTools.messenger(STTools.MESSAGETYPE_ERR, err + STGraphC.getMessage(type) + STTools.NEWLINE + err2);
		return false;
	}


	/** Set whether at the end of each step the output must be dealt with.
	 * @param handleOutput handle the output? */
	public final void setHandleOutput(final boolean handleOutput) { this.handleOutput = handleOutput; }


	/** Retrieve or create and then upgrade the output vector for handling value history.
	 * <br>It outputs:
	 * <ul><li>vectors of (scalar) tensors;</li>
	 * <li>matrixes of (scalar) tensors.</li></ul>
	 * @return result */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean handleOutput() {
		if(outputNodeList == null) { return true; } // nothing to deal with
		for(STNode node : outputNodeList) {
			STGraph.getSTC().setCurrentlyComputedGraph(node.getGraph()); STGraph.getSTC().setCurrentlyComputedNode(node);
			Object out = node.getValue();
			if(node.isScalar()) {
				Vector<Object> v = null;
				Object o = node.getValueHistory();
				if(o == null) {
					if(getTimeFrame() == STGraphImpl.TIMEFRAME_WINDOWED || getTimeFrame() == STGraphImpl.TIMEFRAME_PLAYMODE) {
						v = new Vector<Object>(Math.min(getNumSteps(), getMaxSteps()));
					} else {
						v = new Vector<Object>(getNumSteps());
					}
				} else if(o instanceof Vector<?>) {
					v = (Vector<Object>)o;
				} else if(o instanceof Matrix) {
					v = ((Matrix)o).getColumn(0);
				}
				if(v == null) { return false; }
				if(getTimeFrame() == STGraphImpl.TIMEFRAME_WINDOWED || getTimeFrame() == STGraphImpl.TIMEFRAME_PLAYMODE) {
					if(currStep < getMaxSteps()) {
						v.add(out);
					} else {
						v.remove(0);
						v.add(out);
					}
				} else {
					v.add(out);
				}
				node.setValueHistory(v);
			} else if(node.isVector()) {
				if(node.isVectorOutput()) {
					Matrix m = null; 
					Object o = node.getValueHistory();
					if(o == null) {
						m = new Matrix();
					} else if(o instanceof Vector<?>) {
						m = new Matrix((Vector)o);
					} else if(o instanceof Matrix) {
						m = (Matrix)o;
					}
					if(m == null) { return false; }
					if(getTimeFrame() == STGraphImpl.TIMEFRAME_WINDOWED || getTimeFrame() == STGraphImpl.TIMEFRAME_PLAYMODE) {
						if(currStep < getMaxSteps()) {
							m.addColumn(((Tensor)out).getVector());
						} else {
							m.removeColumn(0);
							m.addColumn(((Tensor)out).getVector());
						}
					} else {
						m.addColumn(((Tensor)out).getVector());
					}
					node.setValueHistory(m);
				} else {
					node.setValueHistory(out); // vectors do not accumulate in this case
				}
			} else if(node.isMatrix()) {
				node.setValueHistory(out); // matrices do not accumulate
			}
		}
		return true;
	}


	/** Handle the display of information on the current state of the execution. */
	private void handleInfoDisplay() {
		if(STGraph.getSTC().getCurrentGraph() != this) { return; }
		Object cell = getSelectionCell();
		if(cell != null && cell instanceof STNode) {
			STNode node = (STNode)cell;
			STStatusBar sb = STGraphC.getStatusBar();
			if(sb != null) { sb.setInfoText(node.getName(), STData.toString(node.getValue(), STData.FORMAT_SHORT)); }
			if(STTools.infoDataDialog.isVisible()) { STTools.infoDataDialog.showMe(STGraphC.getMessage("NODE.DIALOG.SHOWTITLE"), node.getInfo(), false); } //$NON-NLS-1$
		}
		if(STTools.outputVarsDialog.isVisible()) {
			String x1 = "<tr><td><b>"; //$NON-NLS-1$
			String x2 = "</b></td><td>"; //$NON-NLS-1$
			String x3 = "</td></tr>"; //$NON-NLS-1$
			StringBuilder s = new StringBuilder();
			s.append("<table border='0'>"); //$NON-NLS-1$
			for(ValueNode node : outputNodeList) { s.append(x1 + node.getName() + x2 + STData.toString(node.getValue(), STData.FORMAT_ALTERNATE) + x3); }
			s.append("</table>"); //$NON-NLS-1$
			STTools.outputVarsDialog.showMe(STGraphC.getMessage("SYSTEM.DIALOG.OUTPUTVARS"), s.toString(), false); //$NON-NLS-1$
		}
	}


	/** Set node value.
	 * @param value the value
	 * @param node the node */
	private void setNodeValue(final STNode node, final Object value) {
		node.setValue(value);
		node.getGraph().getInterpreter().addVariable(node.getName(), value);
	}


	/** Set the node value as the label of each graph edge (<i>recursive for submodels</i>). */
	final void setEdgeValues() {
		STNode source;
		for(STEdge edge : edgeList) {
			edge.setLabel(((source = edge.getDataSource()) != null) ? 
					STData.toString(source.getValue(), STData.FORMAT_SHORT) : 
						STTools.BLANK);
		}
		getGraphLayoutCache().reload();
		if(isComposed) { // operate on subsystems
			STDesktop desk;
			for(ModelNode mnode : modelNodeList) {
				if((desk = mnode.getDesk()) != null) { desk.getGraph().setEdgeValues(); }
			}
		}
	}


	/** Clear edge labels. */
	final void clearEdgeValues() {
		if(edgeList != null) {
			for(STEdge edge : edgeList) { edge.showLabel(false); }
			getGraphLayoutCache().reload();
		}
	}


	/** Get the stepped state.
	 * @return the stepped */
	public boolean isStepped() { return stepped; }


	/** Set the stepped state.
	 * @param the stepped state */
	public void setStepped(final boolean stepped) { this.stepped = stepped; }


	/** Get the tracing state.
	 * @return the tracing */
	public boolean isTracing() { return tracing; }

}
