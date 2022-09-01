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

import it.liuc.stgraph.node.EditPanel4Nodes;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STEdge;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.node.TextNode;
import it.liuc.stgraph.util.NodeNameComparator;
import it.liuc.stgraph.util.STWebTuner;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.util.SerializablePoint;
import it.liuc.stgraph.widget.InputWidget;
import it.liuc.stgraph.widget.STWidget;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lab.spacebrew.Spacebrew;
import lab.spacebrew.SpacebrewClient;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLParserFactory;
import net.n3.nanoxml.XMLWriter;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.plaf.basic.BasicGraphUI;
import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.SimpleNode;
import org.nfunk.jep.type.Tensor;


/** Gather the fields characterizing a model and the methods related to its pre-simulation operations.
 * <br>It delegates to its subclass <code>STGraphExec</code> all the simulation operations. */
@SuppressWarnings("serial")
public class STGraphImpl extends JGraph implements SpacebrewClient {
	/** The Constant STATE_EDITING. */
	public static final int STATE_EDITING = 0;
	/** The Constant STATE_PRE_EVALUATING. */
	public static final int STATE_PRE_EVALUATING = 1;
	/** The Constant STATE_STEPPING. */
	public static final int STATE_STEPPING = 2;
	/** The Constant STATE_TIMING. */
	public static final int STATE_TIMING = 3;
	/** The Constant STATE_RUNNING. */
	public static final int STATE_RUNNING = 4;
	/** The Constant STATE_BATCHRUNNING. */
	public static final int STATE_BATCHRUNNING = 5;
	/** The Constant STATE_ONTHEFLY_INTERACTION. */
	public static final int STATE_PAUSED = 6;

	/** The Constant TIMEFRAME_STANDARD. */
	public static final int TIMEFRAME_STANDARD = 0;
	/** The Constant TIMEFRAME_INSTANTANEOUS. */
	public static final int TIMEFRAME_INSTANTANEOUS = 1;
	/** The Constant TIMEFRAME_WINDOWED. */
	public static final int TIMEFRAME_WINDOWED = 2;
	/** The Constant TIMEFRAME_PLAYMODE. */
	public static final int TIMEFRAME_PLAYMODE = 3;

	/** The Constant EXCEPTIONHANDLING_CONTINUE. */
	public static final int EXCEPTIONHANDLING_CONTINUE = 0;
	/** The Constant EXCEPTIONHANDLING_STOP. */
	public static final int EXCEPTIONHANDLING_STOP = 1;

	/** The Constant EDGEMODE_IN. */
	public static final int EDGEMODE_IN = 0;
	/** The Constant EDGEMODE_OUT. */
	public static final int EDGEMODE_OUT = 1;
	/** The Constant EDGEMODE_INOUT. */
	public static final int EDGEMODE_INOUT = 2;

	/** The Constant RES_TYPE_WORKBOOK: xls workbook resource type. */
	public static final int RES_TYPE_WORKBOOK = 0;
	/** The Constant RES_TYPE_NODEICON: node icon resource type. */
	public static final int RES_TYPE_NODEICON = 1;

	/** The Constant DEFAULT_MODEL_LANGUAGE. */
	public static final String DEFAULT_MODEL_LANGUAGE = "English"; //$NON-NLS-1$
	
	/** The interpreter. */
	private STInterpreter interpreter;
	/** Model state, selected from the fields STATE_*. */
	private int state;
	/** Input stream. */
	private transient InputStream stream;
	/** Full path of the filename. */
	private String fileName;
	/** Zip handler. */
	private ZipOutputStream zipOut;
	/** Title of the model, visualized in the tab name. */
	private String title;
	/** Has the model been loaded as a resource (instead of a standard file)?. */
	public boolean resource;
	/** Path name of the filename. */
	public String contextName;
	/** Top model. */
	private STGraphImpl topGraph;
	/** Submodel node in the super model. */
	private ModelNode superNode;
	/** Super model. */
	private STGraphImpl superGraph;
	/** The model name. */
	private String modelName;
	/** The model description. */
	private String modelDescription;
	/** The time unit description. */
	private String timeUnitDescription;
	/** The time frame. */
	private int timeFrame = TIMEFRAME_STANDARD; // default value
	/** The maxSteps (for TIMEFRAME_WINDOWED and TIMEFRAME_PLAYMODE, where it is forced to 1). */
	private int maxSteps = 10; // default value
	/** The system time, reset when the simulation starts. */
	private long sysTime;
	/** The index origin, either 0 or 1. */
	private int io = 0; // default value
	/** The time0. */
	private double time0 = 0.0; // default value
	/** The time1. */
	private double time1 = 10.0; // default value
	/** The timeD. */
	private double timeD = 1.0; // default value
	/** The numSteps. */
	private int numSteps = computeNumSteps();
	/** The currTime. */
	public double currTime = -1;
	/** The currStep. */
	public int currStep = -1;
	/** The currBatchStep. */
	public int currBatchStep = -1;
	/** The batchSteps. */
	public int batchSteps = 1;
	/** The thethis. */
	public transient Tensor thethis;
	/** The vIndex. */
	public Tensor vIndex;
	/** The vTime. */
	public Tensor vTime;
	/** The steps before pause. */
	public int stepsBeforePause = 1;
	/** The simulation delay. */
	public int simulationDelay = 100;
	/** Is the graph visible. */
	public boolean graphVisible = true;
	/** Are the widgets visible. */
	public boolean widgetsVisible = true;
	/** Does the model contain at least one input node?. */
	boolean isOpen;
	/** Does the model contain at least one subsystem node?. */
	boolean isComposed;
	/** Does the model contain at least one state node?. */
	public boolean isSequential;
	/** Can the model be edited (dynamic property)?. */
	public boolean isEditable;
	/** Can the model be edited (static property)?. */
	public boolean isReadOnly;
	/** Is the model modified in its layout or structure?. */
	public boolean isModified;
	/** Can a simulation on the model be executed?. */
	private boolean runnable;
	/** The starting execution. */
	public boolean startingExecution;
	/** The text of the error found in the last check, or null if the model is correct. */
	private String lastError;
	/** The node whose evaluation generated the last exception, or null if the model is correct. */
	private STNode lastErrorNode;
	/** Should the engine preinitialize all the variables, so to prevent first step errors for critical connections?. */
	public boolean preInitVars;
	/** What should the engine do in case of runtime exceptions? 0: set to 0.0 and continue; 1: stop (default). */
	private int exceptionHandling = EXCEPTIONHANDLING_STOP ;
	/** Should the computation keep interrupts into account?. */
	private boolean withInterrupts;
	/** The is data saved. */
	private boolean dataSaved;
	/** Is the model aimed at being executed as connected to a SpaceBrew server?. */
	private boolean forNet;
	/** The address of the SB server. */
	private String serverAddress = "127.0.0.1"; //just as a default //$NON-NLS-1$
	/** The datatype for data transferred over the Internet via a SB server. */
	public static String NET_DATATYPE = "stgdata"; //$NON-NLS-1$
	/** The connector to a SB server. */
	private Spacebrew serverConnector;
	/** Is the model aimed at being executed by STGraphWeb?. */
	private boolean forWeb;
	/** The languages of the model when executed by STGraphWeb (comma separated string). */
	private String modelLanguages;
	/** The absolute URL to the web model description for each language. */
	private HashMap<String, String> webModelDescription = new HashMap<String, String>();
	/** The time unit description. */
	private HashMap<String, String> webTimeUnit = new HashMap<String, String>();
	/** The explanatory title. */
	private HashMap<String, String> webExplTitle = new HashMap<String, String>();
	/** The explanatory URL. */
	private HashMap<String, String> webExplURL = new HashMap<String, String>();
	/** The group data of the web model for each language: titles and descriptions as absolute URLs. */
	private HashMap<String, String[][]> webModelGroupData = new HashMap<String, String[][]>();
	/** The global node counter. */
	public int globalNodeCounter;
	/** The array of auxiliary nodes. */
	transient ValueNode[] auxiliaryNodeList;
	/** ... */
	transient ValueNode[] auxiliaryNodeGlobalList;
	/** The array of state nodes. */
	transient ValueNode[] stateNodeList;
	/** ... */
	transient ValueNode[] stateNodeGlobalList;
	/** The array of model nodes. */
	transient ModelNode[] modelNodeList;
	/** ... */
	transient public ModelNode[] modelNodeGlobalList;
	/** The array of sequential model nodes, and therefore a subset of <code>systemNodeList</code>. */
	transient STNode[] seqSystemNodeList;
	/** The sorted array of the nodes whose evaluation order must be computed: it includes all the auxiliaries and the valid combinatorial submodels which do not depend, directly or indirectly, on states or sequential submodels. */
	transient STNode[] sortedNodeList1;
	/** The sorted array of the nodes whose evaluation order must be computed: it includes all the auxiliaries and the valid combinatorial submodels which depend, directly or indirectly, on states or sequential submodels. */
	transient STNode[] sortedNodeList2;
	/** The array of input nodes. */
	transient ValueNode[] inputNodeList;
	/** ... */
	transient ValueNode[] inputNodeGlobalList;
	/** The array of output nodes. */
	transient ValueNode[] outputNodeList;
	/** ... */
	transient ValueNode[] outputNodeGlobalList;
	/** The array of edges. */
	transient STEdge[] edgeList;
	/** The array of input widgets. */	
	transient ArrayList<STWidget> inputWidgetList;
	/** The array of the names of input nodes of the submodel. */
	public transient String[] saturatedNodeNames;
	/** The array of the expressions saturating the submodel inputs. */
	public transient String[] superExpressions;
	/** The is loading. */
	boolean isLoading;
	/** The group map. */
	public HashMap<String, DefaultGraphCell[]> groupMap = new HashMap<String, DefaultGraphCell[]>();
	/** The visibility group map. */
	public HashMap<String, Boolean> visibilityGroupMap = new HashMap<String, Boolean>();
	/** The report map. */
	public HashMap<String, String> reportMap = new HashMap<String, String>();
	/** The Constant EULER. */
	public static final int EULER = 0;
	/** The Constant RK2. */
	public static final int RK2 = 1;
	/** The Constant RK23. */
	public static final int RK23 = 2;
	/** The Constant RK4. */
	public static final int RK4 = 3;
	/** The integration method. */
	public int integrationMethod;
	/** The integration phase. */
	protected int integrationPhase;
	/** Show node values?. */
	private boolean showNodeValues = false;
	/** Show edge labels?. */
	private boolean showEdgeLabels = false;
	/** The resource map. */
	private transient Map<String, Object> resourceMap = new HashMap<String, Object>();
	/** The resource last access date map. */
	private transient Map<String, Long> resourceDataMap = new HashMap<String, Long>();
	/** The list of lists of nodes in loop for a given node. */
	private transient ArrayList<ArrayList<STNode>> loopNodeList = new ArrayList<ArrayList<STNode>>();
	/** The list of lists of edges in loop for a given node. */
	private transient ArrayList<ArrayList<STEdge>> loopEdgeList = new ArrayList<ArrayList<STEdge>>();


	/** Construct the model.
	 * @param stream the input stream
	 * @param fileName the file name
	 * @param asResource load as a resource
	 * @param _superNode the _super node
	 * @param _topGraph the _top graph */
	public STGraphImpl(final InputStream stream, final String fileName, final boolean asResource, final STGraphImpl _topGraph, final ModelNode _superNode) {
		super(new STModel());
		STGraphC stc = STGraph.getSTC();
		this.stream = stream;
		this.fileName = fileName;
		resource = asResource;

		stc.getLoggerFile().info("constructing STGraphImpl... (filename: " + fileName + " isTopGraph? " + (_topGraph == null) + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		setAsEditable(!STGraphC.isApplet() && !resource);
		isReadOnly = STGraphC.isApplet() || resource;

		topGraph = this; // just as default
		superGraph = this; // just as default
		if(_topGraph != null) {
			topGraph = _topGraph.isTopGraph() ? _topGraph : _topGraph.topGraph;
			superNode = _superNode;
			superGraph = _superNode.getGraph();
			setAsEditable(false);
			isReadOnly = true;
			setRunnable(false);
		}

		DefaultCellViewFactory factory = new DefaultCellViewFactory() {
			protected EdgeView createEdgeView(final Object o) {
				return new EdgeView(o) {
					public CellHandle getHandle(final GraphContext context) {
						return new EdgeView.EdgeHandle(this, context) {
							public boolean isAddPointEvent(final MouseEvent event) { return event.isShiftDown(); } // points are added using shift-click
							public boolean isRemovePointEvent(final MouseEvent event) { return event.isShiftDown(); } // points are removed using shift-click
						};
					}
				};
			}

			protected VertexView createVertexView(final Object o) {
				if(STTools.isNode(o)) { return ((STNode)o).getView(); }
				if(STTools.isWidget(o)) { return ((STWidget)o).getView(); }
				return super.createVertexView(o);
			}
		};

		setGraphLayoutCache(new GraphLayoutCache(getModel(), factory, true));
		getGraphLayoutCache().setHidesExistingConnections(true);

		setTitle();

		interpreter = (STInterpreter)STGraphC.getContext().getBean("stinterpreter"); // create a new interpreter instance //$NON-NLS-1$
		((STGraphExec)this).initTimeBasis(-1);
		BasicGraphUI.MAXHANDLES = 1000; // set (by increasing the default: 20) the max number of selected cells with shown handles 
		setHandleSize(2);
		setHandleColor(Color.RED);
		setMarqueeHandler(new STMarqueeHandler((STGraphExec)this)); // use a custom marquee handler
		setMarqueeColor(Color.GREEN);
		getGraphLayoutCache().setSelectsAllInsertedCells(true); // tell the graph to select new cells upon insertion
		setPortsVisible(false); // make ports invisible by default
		setInvokesStopCellEditing(true); // accept edits if click on background
		setBackground(Color.WHITE);

		Properties props = STConfigurator.getProperties();
		setAntiAliased((Boolean.valueOf(props.getProperty("GRAPH.ANTIALIASED"))).booleanValue()); //$NON-NLS-1$
		//setDoubleBuffered((Boolean.valueOf(props.getProperty("GRAPH.DOUBLEBUFFERED"))).booleanValue()); //$NON-NLS-1$
		setDoubleBuffered(false); //TODO [double buffering] ... that does not work
		setShowNodeValues((Boolean.valueOf(props.getProperty("GRAPH.SHOWNODEVALUES"))).booleanValue()); //$NON-NLS-1$
		setShowEdgeLabels((Boolean.valueOf(props.getProperty("GRAPH.SHOWEDGELABELS"))).booleanValue()); //$NON-NLS-1$
		setGridVisible((Boolean.valueOf(props.getProperty("GRAPH.GRIDVISIBLE"))).booleanValue()); //$NON-NLS-1$
		setGridEnabled((Boolean.valueOf(props.getProperty("GRAPH.GRIDENABLED"))).booleanValue()); //$NON-NLS-1$
		setGridSize((Double.valueOf(props.getProperty("GRAPH.GRIDSIZE"))).doubleValue()); //$NON-NLS-1$

		setTolerance(2); // set the tolerance to 2 pixel
		ToolTipManager.sharedInstance().registerComponent(this); // activate the tooltip handling
	}


	/** Get the interpreter.
	 * @return interpreter */
	public final STInterpreter getInterpreter() { return interpreter; }


	/** Set the tab title for this model. */
	public final void setTitle() { //TODO when the top model is renamed the titles of the submodels are not refreshed
		String fs = STGraphC.isApplet() || resource ? "/" : System.getProperty("file.separator"); //$NON-NLS-1$ //$NON-NLS-2$
		title = (fileName == null) ? STGraphC.getMessage("SYSTEM.NEWGRAPH") : fileName.substring(1 + fileName.lastIndexOf(fs)); //$NON-NLS-1$
		if(title.endsWith(STConfigurator.getProperty("FILEEXT.STANDARD")) || title.endsWith(STConfigurator.getProperty("FILEEXT.COMPRESSED"))) { title = title.substring(0, title.length() - 4); } //$NON-NLS-1$ //$NON-NLS-2$
		if(!isTopGraph()) { title = superGraph.title + STTools.DOT + superNode.getName(); }
		contextName = STTools.isEmpty(fileName) ? null : (fileName.lastIndexOf(fs) == -1 ? STTools.BLANK : fileName.substring(0, fileName.lastIndexOf(fs)));
	}


	/** Set the tab title for this model.
	 * @param title the title */
	public final void setTitle(final String title) { this.title = title; }


	/** Get the tab title for this model.
	 * @return title */
	public final String getTitle() { return title; }


	/** Set the current state of this model.
	 * @param state the state */
	public final void setState(final int state) { this.state = state; }


	/** Get the current state of this model.
	 * @return state */
	public final int getState() { return state; }


	/** Get the top graph, or this graph if top.
	 * @return graph */
	public STGraphImpl getTopGraph() { return topGraph; }


	/** Get whether this is the top graph.
	 * @return result */
	public boolean isTopGraph() { return topGraph == this; }


	/** Get the super graph, or this graph if top.
	 * @return graph */
	public STGraphImpl getSuperGraph() { return superGraph; }


	/** Get the submodel node in the super model.
	 * @return node */
	public ModelNode getSuperNode() { return superNode; }


	/** Get the submodel node in the top model, or null if top.
	 * @return node */
	public ModelNode getTopNode() {
		if(isTopGraph()) { return null; }
		STGraphImpl g = this;
		ModelNode result = null;
		while(!g.isTopGraph()) {
			result = g.superNode;
			g = g.superGraph;
		}
		return result;
	}


	/** Set this model as editable.
	 * @param editable the editable */
	public final void setAsEditable(final boolean editable) {
		STGraphC stc = STGraph.getSTC();
		isEditable = editable;
		setEditable(editable); setMoveable(editable); setBendable(editable);
		setCloneable(editable); setSizeable(editable); setConnectable(editable);
		setDisconnectable(editable);
		stc.refreshBars();
		if(STGraphC.getStatusBar() != null) { STGraphC.getStatusBar().setEditable(editable); }
		if(stc.isVisible()) { STGraphC.setFocus(); }
	}


	/** Return the node documentation as tooltip, also basing
	 * on the custom properties application protocol.
	 * @param e the e
	 * @return tooltip */
	@Override
	public final String getToolTipText(final MouseEvent e) {
		if(e == null || !STGraphC.isShowNodeTooltips()) { return null; }
		Object c = getFirstCellForLocation(e.getX(), e.getY()); // fetch cell under mouse pointer
		if(c == null || !(c instanceof STNode)) { return null; }
		STNode n = (STNode)c;
		StringBuilder s = new StringBuilder();
		s.append("<p bgcolor='yellow'>");
		s.append("<b>" + n.getCName() + "</b>");
		String s2 = n.getCProperty("Unit");
		if(!STTools.isEmpty(s2)) { s.append(" [" + s2 + "]"); }
		s2 = n.getCProperty("DefaultValue");
		if(!STTools.isEmpty(s2)) { s.append("<br>" + STGraphC.getMessage("NODE.CUSTOMPROP.DEFAULT") + ": " + s2); }
		s2 = n.getCProperty("Min");
		if(!STTools.isEmpty(s2)) { s.append("<br>" + STGraphC.getMessage("NODE.CUSTOMPROP.MIN") + ": " + s2); }
		s2 = n.getCProperty("Max");
		if(!STTools.isEmpty(s2)) { s.append("<br>" + STGraphC.getMessage("NODE.CUSTOMPROP.MAX") + ": " + s2); }
		s.append("</p");
		s2 = n.getDescription(); if(!STTools.isEmpty(s2)) { s.append("<hr>" + s2); }
		s2 = STData.toString(n.getValue(), STData.FORMAT_ALTERNATE); s.append("<hr>" + s2);
		return "<html>" + STTools.replaceCRwithBR(s.toString()) + "</html>";
	}


	/** Load a new model from the previously specified XML file.
	 * @return success */
	@SuppressWarnings("unchecked")
	public final boolean load() {
		STGraphC stc = STGraph.getSTC();

		if(EditPanel4Nodes.isOpen() && !stc.isSubLoading) {
			if(EditPanel4Nodes.isDirty()) { // autosaving
				EditPanel4Nodes.node.getDialog().okHandler(EditPanel4Nodes.MODE_APPLY);
			}
			EditPanel4Nodes.node.getDialog().getButtonCancel().doClick();
		}

		InputStream is = null;
		if(topGraph.fileName == null) { // this is the peculiar case of submodels inserted from library in yet not saved supermodels...
			is = stc.getInputStream(fileName, resource);
		} else if(!topGraph.fileName.endsWith(STConfigurator.getProperty("FILEEXT.COMPRESSED"))) {
			is = stream;
		} else {
			is = stc.getInputStream(this, fileName, resource);
		}

		STXMLElement node = null;
		try {
			IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
			parser.setReader(new StdXMLReader(is));
			XMLElement root = (XMLElement)parser.parse();
			Enumeration<Object> theEnum = root.enumerateChildren();

			node = new STXMLElement((XMLElement)theEnum.nextElement()); // <head>
			modelName = node.getStringAttribute("systemName");
			modelDescription = node.getStringAttribute("description");
			timeUnitDescription = node.getStringAttribute("timeUnitDescription");
			if(isTopGraph()) {
				timeFrame = node.getIntAttribute("timeFrame");
				time0 = node.getDoubleAttribute("time0");
				time1 = node.getDoubleAttribute("time1");
				timeD = node.getDoubleAttribute("timeD");
				numSteps = computeNumSteps();
				maxSteps = Math.max(1, node.getIntAttribute("maxSteps"));
				batchSteps = node.getIntAttribute("batchSteps");
				STGraphC.getContainer().setSize(node.getIntAttribute("width"), node.getIntAttribute("height"));
				setScale(node.getDoubleAttribute("scale"));
				simulationDelay = node.getIntAttribute("simulationDelay");
				stepsBeforePause = node.getIntAttribute("stepsBeforePause");
				integrationMethod = node.getIntAttribute("integrationMethod");
				exceptionHandling = node.getIntAttribute("exceptionHandling");
				withInterrupts = Boolean.valueOf((node.getStringAttribute("withInterrupts"))).booleanValue();
				dataSaved = Boolean.valueOf((node.getStringAttribute("isDataSaved"))).booleanValue();
				forNet = Boolean.valueOf((node.getStringAttribute("forNet"))).booleanValue();
				if(forNet) { serverAddress = node.getStringAttribute("serverAddress"); }
				forWeb = Boolean.valueOf((node.getStringAttribute("forWeb"))).booleanValue();
				if(forWeb) { modelLanguages = node.getStringAttribute("webModelLanguages"); }
				if(forWeb) {
					webModelDescription.put(getDefaultModelLanguage(), node.getStringAttribute("webModelDescription"));
					webTimeUnit.put(getDefaultModelLanguage(), node.getStringAttribute("webTimeUnit"));
					webExplTitle.put(getDefaultModelLanguage(), node.getStringAttribute("webExplTitle"));
					webExplURL.put(getDefaultModelLanguage(), node.getStringAttribute("webExplURL"));
					webModelGroupData.put(getDefaultModelLanguage(), genGroupDataFromString(node.getStringAttribute("webModelGroups")));
					for(String lang : getNonDefaultModelLanguages()) {
						webModelDescription.put(lang, node.getStringAttribute("webModelDescription_" + lang));
						webTimeUnit.put(lang, node.getStringAttribute("webTimeUnit_" + lang));
						webExplTitle.put(lang, node.getStringAttribute("webExplTitle_" + lang));
						webExplURL.put(lang, node.getStringAttribute("webExplURL_" + lang));
						webModelGroupData.put(lang, genGroupDataFromString(node.getStringAttribute("webModelGroups_" + lang)));
					}
				}
			}
			io = node.getIntAttribute("indexOrigin");
			String s = node.getStringAttribute("isGraphVisible");
			if(s == null) { graphVisible = true; }
			else { graphVisible = Boolean.valueOf((s)).booleanValue(); }
			widgetsVisible = Boolean.valueOf((node.getStringAttribute("areWidgetsVisible"))).booleanValue();

			STXMLElement nodes = new STXMLElement((XMLElement)theEnum.nextElement()); // <nodes>
			if(nodes.countChildren() > 0) {
				String name;
				String type;
				String subtype;
				String content;
				STNode newNode;
				for(XMLElement n : (Vector<XMLElement>)nodes.getChildren()) {
					node = new STXMLElement(n);
					type = node.getStringAttribute("type");
					if((subtype = node.getStringAttribute("subtype")) == null || subtype.equals(STTools.BLANK)) {
						newNode = STFactory.nodeCreate(name = node.getStringAttribute("name"), type);
						newNode.setBounds(new Rectangle(node.getIntAttribute("pos-x"), node.getIntAttribute("pos-y"), node.getIntAttribute("width"), node.getIntAttribute("height")));
					} else {
						newNode = STFactory.nodeCreate4(name = node.getStringAttribute("name"), type, subtype, false, false);
						newNode.setBounds(new Rectangle(node.getIntAttribute("pos-x"), node.getIntAttribute("pos-y"), node.getIntAttribute("width"), node.getIntAttribute("height")));
					}
					newNode.select(false); STTools.moveNodes(0, 0, false); // a trick to properly (?) initialize the nodes (it seems to solve the bug: when scale != 1 dragged nodes change dimensions)
					if(name.startsWith(STGraphC.getMessage("NODE.DEFAULT_PREFIX"))) {
						try {
							int t = Integer.parseInt(name.substring(4));
							globalNodeCounter = Math.max(globalNodeCounter, t);
						} catch (Exception ex) { ; }
					}
					if(node.countChildren() > 0) {
						newNode.dataToSave = new AttributeMap();
						Vector<XMLElement> attrList = node.getChildren();
						for(int j = 0; j < attrList.size(); j++) {
							XMLElement attr = attrList.get(j);
							if((content = attr.getContent()) != null) { newNode.dataToSave.put(attr.getName(), content); }
						}
						newNode.restoreAfterLoad();
					}
				}
			}

			STXMLElement texts = new STXMLElement((XMLElement)theEnum.nextElement()); // <texts>
			if(texts.countChildren() > 0) {
				for(XMLElement n : (Vector<XMLElement>)texts.getChildren()) {
					node = new STXMLElement(n);
					String content = node.getStringAttribute("content");
					TextNode text = STFactory.textCreate(content, new Rectangle(node.getIntAttribute("pos-x"), node.getIntAttribute("pos-y"), node.getIntAttribute("width"), node.getIntAttribute("height")));
					text.setName(node.getStringAttribute("name"));
				}
			}

			STXMLElement edges = new STXMLElement((XMLElement)theEnum.nextElement()); // <edges>
			if(edges.countChildren() > 0) {
				for(XMLElement n : (Vector<XMLElement>)edges.getChildren()) {
					node = new STXMLElement(n);
					STEdge newEdge = STFactory.edgeCreate(node.getStringAttribute("label"), node.getStringAttribute("source"), node.getStringAttribute("target"));

					String t1 = node.getStringAttribute("labpos-x");
					String t2 = node.getStringAttribute("labpos-y");
					if(t1 != null && t2 != null) { newEdge.setLabelPosition(new SerializablePoint(Double.valueOf(t1).doubleValue(), Double.valueOf(t2).doubleValue())); }

					int numPoints = node.getIntAttribute("numpoints");
					if(numPoints != 0) {
						ArrayList<SerializablePoint> points = new ArrayList<SerializablePoint>();
						for(int j = 0; j < numPoints; j++) {
							points.add(new SerializablePoint(node.getDoubleAttribute("p" + j + "x"), node.getDoubleAttribute("p" + j + "y")));
						}
						GraphConstants.setPoints(newEdge.getAttributes(), points);
					}
				}
			}

			STXMLElement widgets = new STXMLElement((XMLElement)theEnum.nextElement()); // <widgets>
			if(widgets.countChildren() > 0) {
				String content;
				for(XMLElement n : (Vector<XMLElement>)widgets.getChildren()) {
					node = new STXMLElement(n);
					STWidget newWidget = STFactory.widgetCreate(node.getStringAttribute("type"));
					newWidget.getView().setWidgetBounds(new Rectangle(node.getIntAttribute("pos-x"), node.getIntAttribute("pos-y"), node.getIntAttribute("width"), node.getIntAttribute("height")));
					if(node.countChildren() > 0) {
						newWidget.dataToSave = new AttributeMap();
						Vector<XMLElement> attrList = node.getChildren();
						for(int j = 0; j < attrList.size(); j++) {
							XMLElement attr = attrList.get(j);
							if((content = attr.getContent()) != null) { newWidget.dataToSave.put(attr.getName(), content); }
						}
						newWidget.restoreAfterLoad();
					}
				}
			}

			try { // for backward compatibility of models without groups
				STXMLElement groups = new STXMLElement((XMLElement)theEnum.nextElement()); // <groups>
				if(groups.countChildren() > 0) {
					for(XMLElement n : (Vector<XMLElement>)groups.getChildren()) {
						node = new STXMLElement(n);
						String groupName = node.getStringAttribute("name");
						Vector<XMLElement> compList = node.getChildren();
						DefaultGraphCell[] components = new DefaultGraphCell[compList.size()];
						for(int j = 0; j < compList.size(); j++) {
							STXMLElement comp = new STXMLElement(compList.get(j));
							String compName = comp.getStringAttribute("name");
							if(compName.startsWith(TextNode.NAME_PREFIX)) {
								components[j] = getTextByName(compName);
							} else {
								components[j] = getNodeByName(compName);
							}
						}
						groupMap.put(groupName, components);
						visibilityGroupMap.put(groupName, Boolean.valueOf(true)); // initial visibility
					}
				}
			} catch (Exception e) { ; }

			try { // for backward compatibility of models without reports
				STXMLElement reports = new STXMLElement((XMLElement)theEnum.nextElement()); // <reports>
				if(reports.countChildren() > 0) {
					for(XMLElement n : (Vector<XMLElement>)reports.getChildren()) {
						node = new STXMLElement(n);
						String reportTitle = node.getStringAttribute("title");
						String reportText = node.getStringAttribute("text");
						reportMap.put(reportTitle, reportText);
					}
				}
			} catch (Exception e) { ; }

			if(!fileName.endsWith(STConfigurator.getProperty("FILEEXT.COMPRESSED"))) { stream = null; }
			stc.setVisible(stc.isVisible());
		} catch (Exception e) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("ERR.WRONG_FILEFORMAT") + STTools.NEWLINE + e.getMessage() + (node != null ? STTools.NEWLINE + node.toString() : STTools.BLANK));
			fileName = null;
			stream = null;
			return false;
		}
		return true;
	}


	/** Save the current model to the previously specified XML file. */
	@SuppressWarnings("unchecked")
	public final void save() {
		try {
			XMLElement root = new XMLElement();
			root.setName("stgraph");
			root.setAttribute("class", "STGraph.decoder");
			root.setAttribute("version", STConfigurator.getProperty("SYSTEM.NAME"));

			XMLElement head = new XMLElement(); head.setName("head");
			head.setAttribute("systemName", !STTools.isEmpty(modelName) ? modelName : STTools.BLANK);
			head.setAttribute("description", !STTools.isEmpty(modelDescription) ? modelDescription : STTools.BLANK);
			head.setAttribute("timeUnitDescription", !STTools.isEmpty(timeUnitDescription) ? timeUnitDescription : STTools.BLANK);
			head.setAttribute("timeFrame", Integer.valueOf(timeFrame).toString());
			head.setAttribute("time0", Double.valueOf(time0).toString());
			head.setAttribute("time1", Double.valueOf(time1).toString());
			head.setAttribute("timeD", Double.valueOf(timeD).toString());
			head.setAttribute("maxSteps", Integer.valueOf(maxSteps).toString());
			head.setAttribute("batchSteps", Integer.valueOf(batchSteps).toString());
			head.setAttribute("width", Integer.valueOf(STGraphC.getContainer().getSize().width).toString());
			head.setAttribute("height", Integer.valueOf(STGraphC.getContainer().getSize().height).toString());
			head.setAttribute("scale", Double.valueOf(getScale()).toString());
			head.setAttribute("simulationDelay", Integer.valueOf(simulationDelay).toString());
			head.setAttribute("stepsBeforePause", Integer.valueOf(stepsBeforePause).toString());
			head.setAttribute("integrationMethod", Integer.valueOf(integrationMethod).toString());
			head.setAttribute("exceptionHandling", Integer.valueOf(exceptionHandling).toString());
			head.setAttribute("withInterrupts", Boolean.valueOf(withInterrupts).toString());
			head.setAttribute("isDataSaved", Boolean.valueOf(dataSaved).toString());
			head.setAttribute("forNet", Boolean.valueOf(forNet).toString());
			head.setAttribute("serverAddress", forNet ? serverAddress : STTools.BLANK);
			head.setAttribute("forWeb", Boolean.valueOf(forWeb).toString());
			head.setAttribute("webModelLanguages", forWeb ? modelLanguages : STTools.BLANK);
			head.setAttribute("webModelDescription", forWeb && !STTools.isEmpty(webModelDescription.get(getDefaultModelLanguage())) ? webModelDescription.get(getDefaultModelLanguage()) : STTools.BLANK);
			head.setAttribute("webTimeUnit", forWeb && !STTools.isEmpty(webTimeUnit.get(getDefaultModelLanguage())) ? webTimeUnit.get(getDefaultModelLanguage()) : STTools.BLANK);
			head.setAttribute("webExplTitle", forWeb && !STTools.isEmpty(webExplTitle.get(getDefaultModelLanguage())) ? webExplTitle.get(getDefaultModelLanguage()) : STTools.BLANK);
			head.setAttribute("webExplURL", forWeb && !STTools.isEmpty(webExplURL.get(getDefaultModelLanguage())) ? webExplURL.get(getDefaultModelLanguage()) : STTools.BLANK);
			head.setAttribute("webModelGroups", forWeb ? genStringFromGroupData(getDefaultModelLanguage()) : STTools.BLANK);
			if(!forWeb) {
				head.setAttribute("webModelLanguages", STTools.BLANK);
			} else {
				if(modelLanguages == null) {
					head.setAttribute("webModelLanguages", getDefaultModelLanguage());
				} else {
					head.setAttribute("webModelLanguages", modelLanguages);
					for(String lang : getNonDefaultModelLanguages()) {
						head.setAttribute("webModelDescription_" + lang, !STTools.isEmpty(webModelDescription.get(lang)) ? webModelDescription.get(lang) : STTools.BLANK);
						head.setAttribute("webTimeUnit_" + lang, !STTools.isEmpty(webTimeUnit.get(lang)) ? webTimeUnit.get(lang) : STTools.BLANK);
						head.setAttribute("webExplTitle_" + lang, !STTools.isEmpty(webExplTitle.get(lang)) ? webExplTitle.get(lang) : STTools.BLANK);
						head.setAttribute("webExplURL_" + lang, !STTools.isEmpty(webExplURL.get(lang)) ? webExplURL.get(lang) : STTools.BLANK);
						head.setAttribute("webModelGroups_" + lang, genStringFromGroupData(lang));
					}
				}
			}
			head.setAttribute("indexOrigin", Integer.valueOf(io).toString());
			head.setAttribute("isGraphVisible", Boolean.valueOf(graphVisible).toString());
			head.setAttribute("areWidgetsVisible", Boolean.valueOf(widgetsVisible).toString());
			root.addChild(head);

			STNode[] nodeList = getNodes();
			if(nodeList != null) {
				Arrays.sort(nodeList, STGraph.getSTC().getNodeNameComparator());

				String s;
				if(fileName.endsWith(STConfigurator.getProperty("FILEEXT.COMPRESSED"))) { // preprocessing required for local submodels to be stored in a zip file
					for(STNode oNode : nodeList) {
						if(oNode.isModel() && ((ModelNode)oNode).getSubmodel() != null && !(oNode instanceof ModelNode)) { // only if NOT from library...
							s = ((ModelNode)oNode).getSubmodelName();
							String fs = "/"; //STGraphC.isApplet() || resource ? "/" : System.getProperty("file.separator");
							s = "local" + fs + s;
							s = s.substring(0, s.length() - 4);
							oNode.setNodeSubtype(s);
							((ModelNode)oNode).setSubmodelName("local" + fs + ((ModelNode)oNode).getSubmodelName());
						}
					}
				}

				XMLElement nodes = new XMLElement(); // <nodes>
				nodes.setName("nodes");
				root.addChild(nodes);

				for(STNode oNode : nodeList) {
					XMLElement node = new XMLElement(); node.setName("node");
					node.setAttribute("name", oNode.getName());
					node.setAttribute("type", oNode.getNodeType());
					if((s = oNode.getNodeSubtype()) != null) { node.setAttribute("subtype", s); }
					Rectangle2D bounds = oNode.getBounds();
					node.setAttribute("pos-x", Integer.valueOf((int)bounds.getX()).toString());
					node.setAttribute("pos-y", Integer.valueOf((int)bounds.getY()).toString());
					node.setAttribute("width", Integer.valueOf((int)bounds.getWidth()).toString());
					node.setAttribute("height", Integer.valueOf((int)bounds.getHeight()).toString());
					oNode.prepareForSave();
					AttributeMap map = oNode.dataToSave;
					for(Enumeration<Object> keys = map.keys(); keys.hasMoreElements();) {
						String name = keys.nextElement().toString();
						XMLElement attribute = new XMLElement(); attribute.setName(name);
						attribute.setContent(map.get(name).toString());
						node.addChild(attribute);
					}
					nodes.addChild(node);
				}
			}

			XMLElement texts = new XMLElement(); // <texts>
			texts.setName("texts");
			root.addChild(texts);
			for(TextNode oNode : getTexts()) {
				XMLElement node = new XMLElement(); node.setName("text");
				node.setAttribute("name", oNode.getName());
				Rectangle2D bounds = GraphConstants.getBounds(oNode.getAttributes());
				node.setAttribute("pos-x", Integer.toString((int)bounds.getX()));
				node.setAttribute("pos-y", Integer.toString((int)bounds.getY()));
				node.setAttribute("width", Integer.toString((int)bounds.getWidth()));
				node.setAttribute("height", Integer.toString((int)bounds.getHeight()));
				node.setAttribute("content", oNode.getContent());
				texts.addChild(node);
			}

			XMLElement edges = new XMLElement(); // <edges>
			edges.setName("edges");
			root.addChild(edges);
			for(STEdge oEdge : getEdges()) {
				XMLElement node = new XMLElement(); node.setName("edge");
				node.setAttribute("source", oEdge.getSourceNodeName());
				node.setAttribute("target", oEdge.getTargetNodeName());
				node.setAttribute("label", oEdge.getName());

				Point2D labPos = oEdge.getLabelPosition();
				if(labPos != null) {
					node.setAttribute("labpos-x", Double.valueOf(labPos.getX()).toString());
					node.setAttribute("labpos-y", Double.valueOf(labPos.getY()).toString());
				}

				List<Object> points = GraphConstants.getPoints(oEdge.getAttributes());
				if(points != null) {
					Iterator<Object> pointIterator = points.iterator();
					Point2D.Double p = null;
					int numPoints = 0;
					while(pointIterator.hasNext()) {
						p = (Point2D.Double)pointIterator.next();
						node.setAttribute("p" + numPoints + "x", Double.valueOf(p.getX()).toString());
						node.setAttribute("p" + numPoints + "y", Double.valueOf(p.getY()).toString());
						numPoints++;
					}
					node.setAttribute("numpoints", Integer.toString(numPoints));
				}
				edges.addChild(node);
			}

			XMLElement widgets = new XMLElement(); // <widgets>
			widgets.setName("widgets");
			root.addChild(widgets);
			for(STWidget oWidget : getWidgets()) {
				XMLElement node = new XMLElement(); node.setName("widget");
				node.setAttribute("type", oWidget.getType());
				node.setAttribute("pos-x", Integer.toString(oWidget.frame.getX()));
				node.setAttribute("pos-y", Integer.toString(oWidget.frame.getY()));
				node.setAttribute("width", Integer.toString(oWidget.frame.getWidth()));
				node.setAttribute("height", Integer.toString(oWidget.frame.getHeight()));
				oWidget.prepareForSave();
				AttributeMap map = oWidget.dataToSave;
				for(Enumeration<Object> keys = map.keys(); keys.hasMoreElements();) {
					String name = keys.nextElement().toString();
					XMLElement attribute = new XMLElement(); attribute.setName(name);
					attribute.setContent(map.get(name).toString());
					node.addChild(attribute);
				}
				widgets.addChild(node);
			}

			XMLElement groups = new XMLElement();
			groups.setName("groups");
			root.addChild(groups);
			if(groupMap != null && groupMap.size() > 0) { // <groups>
				Iterator<String> k = groupMap.keySet().iterator();
				while(k.hasNext()) {
					String name = k.next();
					if(name != null) {
						DefaultGraphCell[] components = groupMap.get(name);
						if(components != null && components.length > 0) {
							XMLElement node = new XMLElement(); node.setName("group");
							node.setAttribute("name", name);
							for(int i = 0; i < components.length; i++) {
								if(components[i] != null) {
									XMLElement attribute = new XMLElement(); attribute.setName("comp");
									if(components[i] instanceof STNode) {
										attribute.setAttribute("name", ((STNode)components[i]).getName());
									} else {
										attribute.setAttribute("name", ((TextNode)components[i]).getName());
									}
									node.addChild(attribute);
								}
							}
							groups.addChild(node);
						}
					}
				}
			}

			XMLElement reports = new XMLElement();
			reports.setName("reports");
			root.addChild(reports);
			if(reportMap != null && reportMap.size() > 0) { // <reports>
				Iterator<String> k = reportMap.keySet().iterator();
				while(k.hasNext()) {
					String title = k.next();
					if(title != null) {
						XMLElement node = new XMLElement(); node.setName("report");
						node.setAttribute("title", title);
						node.setAttribute("text", reportMap.get(title));
						reports.addChild(node);						
					}
				}
			}

			if((isTopGraph() && fileName.endsWith(STConfigurator.getProperty("FILEEXT.COMPRESSED"))) || (!isTopGraph() && getTopGraph().fileName.endsWith(STConfigurator.getProperty("FILEEXT.COMPRESSED")))) {
				ZipOutputStream zo;
				boolean toSave;
				if(isTopGraph()) {
					zipOut = new ZipOutputStream(new FileOutputStream(fileName));
					zo = zipOut;
					zo.putNextEntry(new ZipEntry("base")); // 'base' is the dummy name of the supermodel in the zip file
					toSave = true;
				} else {
					zo = getTopGraph().zipOut;
					try {
						String fn = fileName;
						if(!fn.startsWith(STGraphC.getBasicProps().getProperty("MODEL.DIR"))) { // a submodel stored in the current dir instead of the library
							String fs = "/"; //STGraphC.isApplet() || resource ? "/" : System.getProperty("file.separator");
							String fs2 = STGraphC.isApplet() || resource ? "/" : System.getProperty("file.separator");
							fn = STGraphC.getBasicProps().getProperty("MODEL.DIR") + fs + "local" + fs + fn.substring(1 + fn.lastIndexOf(fs2));
						}
						zo.putNextEntry(new ZipEntry(fn));
						toSave = true;
					} catch (Exception e) {
						toSave = false;
					}
				}
				if(toSave) {
					zo.write("<?xml version=\"1.0\"?>".getBytes());
					StringWriter sw = new StringWriter();
					new XMLWriter(sw).write(root, Boolean.parseBoolean(STConfigurator.getProperty("STGFILE.PRETTYPRINT")));
					zo.write(sw.toString().getBytes());
					zo.closeEntry();
					STGraphImpl g;
					for(ModelNode mnode : modelNodeList) {
						if((g = mnode.getSubmodel()) != null) { g.save(); }
					}
				}
				if(isTopGraph()) { zo.close(); }
			} else {
				new XMLWriter(new FileWriter(fileName, false)).write(root, Boolean.parseBoolean(STConfigurator.getProperty("STGFILE.PRETTYPRINT")));
			}

			setAsModified(false);
		} catch (Exception e) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("ERR.FILE_NOT_SAVED") + STTools.NEWLINE + e.getMessage());
		}
	}


	/** Save the values of output nodes to an xls file.
	 * <br>If the model is not for web, only the values of the last iteration are written;
	 * otherwise, the xls file and its contents are structured so to ease their analysis
	 * for web-oriented applications.
	 * <br>This method operates only in desktop app mode. */
	@SuppressWarnings("rawtypes")
	public final void saveData() {
		if(STGraph.getExecMode() != STGraph.EXECMODE_GUIAPP) { return; }
		String fn = fileName;
		if(fn == null) { return; }
		int l = fn.length();
		if(fn.substring(l - 4, l).toLowerCase().equals(STConfigurator.getProperty("FILEEXT.STANDARD"))) { fn = fn.substring(0, l - 4); } //$NON-NLS-1$

		String ext = STConfigurator.getProperty("FILEEXT.DATAFILE"); //$NON-NLS-1$
		int n = 1;
		while(new File(fn + STTools.UNDERSCORE + n + ext).exists()) { n++; }

		fn += STTools.UNDERSCORE + n + ext;

		if(!forWeb) {
			try {
				WritableWorkbook workbook = Workbook.createWorkbook(new File(fn));
				WritableSheet sheet = workbook.createSheet("data", 0); //$NON-NLS-1$
				int i = 0;
				Object value;
				Tensor val;
				for(STNode node : outputNodeList) {
					value = node.getValue();
					if(value instanceof Tensor) {
						sheet.addCell(new jxl.write.Label(i, 0, node.getName()));
						val = (Tensor)value;
						if(val.getSize() != -1) {
							if(node.isScalar()) {
								Vector vect = (Vector)((ValueNode)node).getValueHistory(); //... the full history
								for(int j = 0; j < vect.size(); j++) {
									sheet.addCell(new Number(i, j + 1, ((Tensor)vect.get(j)).getValue()));
								}
								i++;
							} else if(node.isVector()) {
								int row = 1;
								for(double v : val.getValues()) {
									sheet.addCell(new Number(i, row++, v));
								}
								i++;
							} else if(node.isMatrix()) {
								for(int j = 0; j < val.getColumnCount(); j++) {
									Tensor c = val.getColumn(j);
									int row = 1;
									for(double v : c.getValues()) {
										sheet.addCell(new Number(i, row++, v));
									}
									i++;
								}
							}
						}
					}
				}
				workbook.write();
				workbook.close();
			} catch (Exception e) { e.printStackTrace(); }
		} else {
			try {
				STWebTuner.writeOutXLS(fn);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}


	/** Handle the operations related to the status of modification of a graph.
	 * @param b the b */
	public final void setAsModified(final boolean b) {
		if(isLoading || isReadOnly) { return; }
		if(STGraphC.isApplet()) { return; }
		JTabbedPane md = STGraphC.getMultiDesktop();
		isModified = b;
		int i = md.getSelectedIndex();
		String t = md.getTitleAt(i);
		String c = t.substring(0, 1);
		if(b && !c.equals("*")) { md.setTitleAt(i, "*" + t); } //$NON-NLS-1$ //$NON-NLS-2$
		else if(!b && c.equals("*")) { md.setTitleAt(i, t.substring(1)); } //$NON-NLS-1$
		md.repaint();
	}


	/** Refresh the graph. */
	public final void refreshGraph() {
		//getGraphLayoutCache().reload();
		repaint();
		// what follows is really peculiar: it should not be required (the repainting should be inherited),
		// but it seems to be the only way to refresh widgets with LaF such as motif and metal...
		/*
		Object[] o = getEntities();
		if(o != null) {
			for(Object ob : o) {
				if(STTools.isWidget(ob)) { ((STWidget)ob).refresh(); }
				//if(STTools.isWidget(ob)) {
				//	if(!(getState() == 6 && ob instanceof ChartWidget)) ((STWidget)ob).refresh();
				//}
			}
		}
		*/
		if(inputWidgetList != null) {
			//ValueNode node;
			for(STWidget widget : inputWidgetList) {
				//node = widget.getNode();
				((InputWidget)widget).setInteractable(true);
			}
		}
	}


	/** Get the array of graph cells.
	 * @return cells */
	final Object[] getCells() { return getDescendants(getRoots()); }


	/** Get the number of graph cells.
	 * @return cell count */
	final int getCellCount() { Object[] o = getCells(); return (o == null) ? 0 : o.length; }


	/** Get the array of graph entities (nodes and edges = graph roots).
	 * @return entities */
	public final Object[] getEntities() { return getRoots(); }


	/** Get the number of graph entities.
	 * @return entity count */
	final int getEntityCount() { Object[] o = getEntities(); return (o == null) ? 0 : o.length; }


	/** Construct and get the array of the nodes of the current model and its sub-models.
	 * @return nodes */
	public final STNode[] getAllNodes() {
		ArrayList<STNode> n = new ArrayList<STNode>();
		n = getAllNodesHelper(this, n);
		return n.toArray(new STNode[n.size()]);
	}


	/** Helper for the <code>getAllNodes()</code> method.
	 * @param g the graph
	 * @param n the node list
	 * @return the node list */
	private ArrayList<STNode> getAllNodesHelper(final STGraphImpl g, final ArrayList<STNode> n) {
		Object[] o = g.getEntities();
		if(o == null) { return n; }
		STNode node;
		ArrayList<STNode> subNodes = new ArrayList<STNode>();
		for(Object ob : o) {
			if(STTools.isNode(ob)) {
				n.add(node = (STNode)ob);
				if(node.isModel()) { subNodes.add(node); }
			}
		}
		STDesktop desk;
		for(STNode subNode : subNodes) {
			if((desk = ((ModelNode)subNode).getDesk()) != null) { getAllNodesHelper(desk.getGraph(), n); }
		}
		return n;
	}


	/** Construct and get the array of the nodes of the current model.
	 * @return nodes */
	public final STNode[] getNodes() {
		Object[] o = getEntities();
		if(o == null) { return null; }
		ArrayList<STNode> result = new ArrayList<STNode>();
		for(Object ob : o) {
			if(STTools.isNode(ob)) { result.add((STNode)ob); }
		}
		return result.toArray(new STNode[result.size()]);
	}


	/** Construct and get the array of the nodes of the given type of the current model.
	 * @param type the type
	 * @return nodes */
	final STNode[] getNodes(final String type) {
		Object[] o = getEntities();
		if(o == null) { return null; }
		ArrayList<STNode> result = new ArrayList<STNode>();
		for(Object ob : o) {
			if(STTools.isNode(ob) && ((STNode)ob).getNodeType().equals(type)) { result.add((STNode)ob); }
		}
		return result.toArray(new STNode[result.size()]);
	}


	/** Construct and get the array of the state nodes of the current model.
	 * @return nodes */
	final ValueNode[] getStateNodes() {
		Object[] o = getEntities();
		if(o == null) { return null; }
		ArrayList<ValueNode> result = new ArrayList<ValueNode>();
		for(Object ob : o) {
			if(STTools.isNode(ob) && ((STNode)ob).isState()) { result.add((ValueNode)ob); }
		}
		return result.toArray(new ValueNode[result.size()]);
	}


	/** Construct and get the array of the submodel nodes of the current model.
	 * @return nodes */
	final ModelNode[] getModelNodes() {
		Object[] o = getEntities();
		if(o == null) { return null; }
		ArrayList<ModelNode> result = new ArrayList<ModelNode>();
		for(Object ob : o) {
			if(STTools.isNode(ob) && ((STNode)ob).isModel()) { result.add((ModelNode)ob); }
		}
		return result.toArray(new ModelNode[result.size()]);
	}


	/** Construct and get the array of the input nodes of the current model.
	 * @return nodes */
	final STNode[] getInputNodes() {
		Object[] o = getEntities();
		if(o == null) { return null; }
		ArrayList<STNode> result = new ArrayList<STNode>();
		for(Object ob : o) {
			if(STTools.isNode(ob) && ((STNode)ob).isInput()) { result.add((STNode)ob); }
		}
		return result.toArray(new STNode[result.size()]);
	}


	/** Construct and get the array of the output nodes of the current model.
	 * @return nodes */
	final STNode[] getOutputNodes() {
		Object[] o = getEntities();
		if(o == null) { return null; }
		ArrayList<STNode> result = new ArrayList<STNode>();
		for(Object ob : o) {
			if(STTools.isNode(ob) && ((STNode)ob).isOutput()) { result.add((STNode)ob); }
		}
		return result.toArray(new STNode[result.size()]);
	}


	/** Get the array of the nodes of the current model whose definition contains the given expression.
	 * @param expr expression to search
	 * @return nodes */
	final STNode[] getNodesWithExprInDef(final String expr) {
		Object[] o = getEntities();
		if(o == null) { return null; }
		ArrayList<STNode> result = new ArrayList<STNode>();
		STNode node;
		boolean found;
		for(Object ob : o) {
			if(STTools.isNode(ob)) {
				node = (STNode)ob;
				found = false;
				if(node.isAlgebraic()) {
					found = checkDef(interpreter, node, expr, ((ValueNode)node).getExpression());
				} else if(node.isState()) {
					found = checkDef(interpreter, node, expr, ((ValueNode)node).getStateInit());
					found = checkDef(interpreter, node, expr, ((ValueNode)node).getStateTransition());
					if(node.isStateWithOutput()) {
						found = checkDef(interpreter, node, expr, ((ValueNode)node).getExpression());
					}
				}
				if(found) { result.add(node); };
			}
		}
		return result.toArray(new STNode[result.size()]);
	}

	/** Helper for getNodesWithExprInDef.
	 * @param interpreter the interpreter
	 * @param node the node
	 * @param expr the expression to seach
	 * @param def the definition where to search 
	 * @return found */
	private static boolean checkDef(final STInterpreter interpreter, final STNode node, final String expr, final String def) {
		interpreter.parseExpression(def);
		if(interpreter.getErrorInfo() != null) { return false; } // expression cannot be parsed
		SimpleNode parsedNode = (SimpleNode)interpreter.getTopNode();
		boolean found = checkNode(parsedNode, expr);
		if(!found) {
			found = found || scanNodes(parsedNode, new DefaultMutableTreeNode(getNodeName(parsedNode)), expr);
		}
		return found;
	}


	/** Helper for check for getNodesWithExprInDef.
	 * @param topParsedNode the top parsed node
	 * @param topTreeNode the top tree node
	 * @param expr the expression to seach */
	@SuppressWarnings("unused")
	private static boolean scanNodes(final SimpleNode topParsedNode, final DefaultMutableTreeNode topTreeNode, final String expr) {
		SimpleNode parsedNode;
		for(int i = 0; i < topParsedNode.jjtGetNumChildren(); i++) {
			parsedNode = (SimpleNode)topParsedNode.jjtGetChild(i);
			if(checkNode(parsedNode, expr)) { return true; }
			return scanNodes(parsedNode, new DefaultMutableTreeNode(getNodeName(parsedNode)), expr);
		}
		return false;
	}


	/** Helper for getNodesWithExprInDef.
	 * @param node the node
	 * @param expr the expression to seach
	 * @return found */
	private static boolean checkNode(final SimpleNode node, final String expr) {
		if(!(node instanceof ASTFunNode)) { return false; }
		String n = ((ASTFunNode)node).getName();
		return n.equals(expr);
	}


	/** Helper for check for getNodesWithExprInDef.
	 * @param node the node
	 * @return name */
	private static String getNodeName(final SimpleNode node) {
		if(node instanceof ASTConstant) { return ((ASTConstant)node).getValue().toString(); }
		if(node instanceof ASTFunNode) { return ((ASTFunNode)node).getName(); }
		if(node instanceof ASTVarNode) { return ((ASTVarNode)node).getName(); }
		return STTools.BLANK;
	}


	/** Get the named node, or <code>null</code> if not found.
	 * <br>It operates also with nodes whose name has the form <code>sub.name</name>.
	 * @param name the name
	 * @return node */
	public final STNode getNodeByName(final String name) {
		int p;
		if((p = name.indexOf(STTools.DOT)) == -1) {
			for(Object ob : getEntities()) {
				if(STTools.isNode(ob) && ((STNode)ob).getName().equals(name)) { return (STNode)ob; }
			}
			return null;
		}
		String n1 = name.substring(0, p);
		String n2 = name.substring(p + 1);
		for(Object ob : getEntities()) {
			if(STTools.isNode(ob) && ((STNode)ob).getName().equals(n1) && ((STNode)ob).isModel()) {
				for(Object ob2 : ((ModelNode)ob).getSubmodel().getEntities()) {
					if(STTools.isNode(ob2) && ((STNode)ob2).getName().equals(n2)) { return (STNode)ob2; }
				}
			}
		}
		return null;
	}


	/** Get the named text node, or <code>null</code> if not found.
	 * @param name the name
	 * @return node */
	public final TextNode getTextByName(final String name) {
		for(Object ob : getEntities()) {
			if(STTools.isText(ob) && ((TextNode)ob).getName().equals(name)) { return (TextNode)ob; }
		}
		return null;
	}


	/** Get the array of graph nodes connected from or to the specified node, i.e., (directly or not) defining it or defined by it.
	 * @param node the node
	 * @param edgeMode the edge mode (EDGEMODE_IN, EDGEMODE_OUT, EDGEMODE_INOUT)  
	 * @param directOnly consider only directly connected nodes?
	 * @param withSelection extend the selection to the identified nodes?
	 * @return nodes */
	@SuppressWarnings("unchecked")
	public final ArrayList<STNode> getConnectedNodes(final STNode node, final int edgeMode, final boolean directOnly, final boolean withSelection) {
		if(directOnly) {
			STEdge[] edges = getNodeEdges(node, edgeMode);
			if(edges == null) { return null; }
			ArrayList<STNode> result = new ArrayList<STNode>();
			STNode n = null;
			if(edgeMode == EDGEMODE_IN) {
				for(STEdge edge : edges) {
					n = edge.getSourceNode();
					if(!result.contains(n)) { result.add(n); if(withSelection) { addSelectionCell(n); } }
				}
				return result;
			}
			if(edgeMode == EDGEMODE_OUT) {
				for(STEdge edge : edges) {
					n = edge.getTargetNode();
					if(!result.contains(n)) { result.add(n); if(withSelection) { addSelectionCell(n); } }
				}
				return result;
			}
			for(STEdge edge : edges) {
				n = edge.getSourceNode();
				if(!result.contains(n)) { result.add(n); if(withSelection) { addSelectionCell(n); } }
				n = edge.getTargetNode();
				if(!result.contains(n)) { result.add(n); if(withSelection) { addSelectionCell(n); } }
			}
			return result;
		}
		ArrayList<STNode> result = getConnectedNodesHelper(new ArrayList<STNode>(), node, edgeMode, withSelection);
		if(result == null) { return null; }
		Collections.sort(result, STGraph.getSTC().getPriorityComparator());
		return result;
	}


	/** Helper for the <code>getConnectedNodes()</code> method.
	 * @param list the list
	 * @param node the node
	 * @param edgeMode the edge mode (EDGEMODE_IN, EDGEMODE_OUT, EDGEMODE_INOUT)  
	 * @param withSelection extend the selection to the identified nodes?
	 * @return list */
	private ArrayList<STNode> getConnectedNodesHelper(ArrayList<STNode> list, final STNode node, final int edgeMode, final boolean withSelection) {
		STEdge[] edges = getNodeEdges(node, edgeMode);
		if(edges == null) { return list; }
		STNode n = null;
		if(edgeMode == EDGEMODE_IN) {
			for(STEdge edge : edges) {
				n = edge.getSourceNode();
				if(!list.contains(n)) {
					list.add(n); if(withSelection) { addSelectionCell(n); }
					list = getConnectedNodesHelper(list, n, edgeMode, withSelection);
				}
			}
			return list;
		}
		if(edgeMode == EDGEMODE_OUT) {
			for(STEdge edge : edges) {
				n = edge.getTargetNode();
				if(!list.contains(n)) {
					list.add(n); if(withSelection) { addSelectionCell(n); }
					list = getConnectedNodesHelper(list, n, edgeMode, withSelection);
				}
			}
			return list;
		}
		for(STEdge edge : edges) { // for EDGEMODE_INOUT mode
			n = edge.getSourceNode();
				if(!list.contains(n)) {
					list.add(n); if(withSelection) { addSelectionCell(n); }
					list = getConnectedNodesHelper(list, n, edgeMode, withSelection);
				}
				n = edge.getTargetNode();
				if(!list.contains(n)) {
					list.add(n); if(withSelection) { addSelectionCell(n); }
					list = getConnectedNodesHelper(list, n, edgeMode, withSelection);
				}
		}
		return list;
	}


	/** Get the list of lists of nodes in loop for a given node.
	 * @return the list */
	public ArrayList<ArrayList<STNode>> getLoopNodeList() { return loopNodeList; } 


	/** Get the list of lists of edges in loop for a given node.
	 * @return the list */
	public ArrayList<ArrayList<STEdge>> getLoopEdgeList() { return loopEdgeList; } 


	/** Get the lists of graph nodes and edges, if any, connected in loop from the specified node.
	 * @param node the node */
    public final void getLoops(final STNode node) {
    	loopNodeList = new ArrayList<ArrayList<STNode>>();
    	loopEdgeList = new ArrayList<ArrayList<STEdge>>();
    	STEdge[] edges = getNodeEdges(node, EDGEMODE_OUT);
        if(edges != null) {
            for(STEdge edge : edges) {
                ArrayList<STNode> nodePath = new ArrayList<STNode>();
                ArrayList<STEdge> edgePath = new ArrayList<STEdge>();
                nodePath.add(node);
                edgePath.add(edge);
                getLoopsHelper(nodePath, edgePath, edge.getTargetNode());
            }
        }
        /*
    	if(loopNodeList.size() == 0) {
    		System.out.println("No loops");
    	} else {
    		for(ArrayList<STNode> nodeLoop : loopNodeList) {
        		System.out.println("loop: " + nodeLoop);
    		}
    	}
		*/
    }

    private void getLoopsHelper(ArrayList<STNode> nodePath, ArrayList<STEdge> edgePath, STNode node) {
    	if(node.equals(nodePath.get(0))) { // found a loop: add it to the list
    		nodePath.add(node);
    		loopNodeList.add(nodePath);
    		loopEdgeList.add(edgePath);
    		return;
    	}
    	for(int i = 1; i < nodePath.size(); i++) {
        	if(node.equals(nodePath.get(i))) { return; }// this is a loop not starting from the given node: discard it
    	}
    	STEdge[] edges = getNodeEdges(node, EDGEMODE_OUT);
        if(edges == null) { return; } // terminal node, hence not a loop: discard it
        for(STEdge edge : edges) { // non-terminal node: keep on searching
        	ArrayList<STNode> newNodePath = new ArrayList<STNode>();
        	ArrayList<STEdge> newEdgePath = new ArrayList<STEdge>();
        	newNodePath.addAll(nodePath);
        	newNodePath.add(node);
        	newEdgePath.addAll(edgePath);
        	newEdgePath.add(edge);
        	getLoopsHelper(newNodePath, newEdgePath, edge.getTargetNode());
        }
    }


    public void highlightLoops() {
		ArrayList<ArrayList<STEdge>> loops = getLoopEdgeList();
		if(loops.size() > 0) {
			for(ArrayList<STEdge> loop : loops) {
				for(STEdge edge : loop) { edge.setColor(Color.CYAN); }
			}
		}
    }


    public void resetLoops() {
		ArrayList<ArrayList<STEdge>> loops = getLoopEdgeList();
		if(loops.size() > 0) {
			for(ArrayList<STEdge> loop : loops) {
				for(STEdge edge : loop) { edge.resetColor(); }
			}
		}
    }



	/** Get the number of graph nodes.
	 * @return node count */
	public final int getNodeCount() { STNode[] nodes = getNodes(); return (nodes == null) ? 0 : nodes.length; }


	/** Get the array of graph texts.
	 * @return texts */
	public final TextNode[] getTexts() {
		Object[] o = getEntities();
		if(o == null) { return null; }
		ArrayList<TextNode> result = new ArrayList<TextNode>();
		for(Object ob : o) {
			if(STTools.isText(ob)) { result.add((TextNode)ob); }
		}
		return result.toArray(new TextNode[result.size()]);
	}


	/** Get the number of graph texts.
	 * @return text count */
	public final int getTextCount() { TextNode[] o = getTexts(); return (o == null) ? 0 : o.length; }


	/** Get an array containing the edges of the current model and its sub-models.
	 * @return edges */
	public final STEdge[] getAllEdges() {
		ArrayList<STEdge> e = new ArrayList<STEdge>();
		e = getAllEdgesHelper(this, e);
		return e.toArray(new STEdge[e.size()]);
	}


	/** Helper for the <code>getAllNodes()</code> method.
	 * @param g the graph
	 * @param e the edge list
	 * @return the edge list */
	private ArrayList<STEdge> getAllEdgesHelper(final STGraphImpl g, final ArrayList<STEdge> e) {
		Object[] o = g.getEntities();
		if(o == null) { return e; }
		ArrayList<STNode> subNodes = new ArrayList<STNode>();
		for(Object ob : o) {
			if(STTools.isEdge(ob)) {
				e.add((STEdge)ob);
			} else if(STTools.isNode(ob) && ((STNode)ob).isModel()) {
				subNodes.add(((STNode)ob));
			}
		}
		STDesktop desk;
		for(STNode subNode : subNodes) {
			if((desk = ((ModelNode)subNode).getDesk()) != null) { getAllEdgesHelper(desk.getGraph(), e); }
		}
		return e;
	}


	/** Get the array of graph edges.
	 * @return edges */
	public final STEdge[] getEdges() {
		Object[] o = getEntities();
		if(o == null) { return null; }
		ArrayList<STEdge> result = new ArrayList<STEdge>();
		for(Object ob : o) {
			if(STTools.isEdge(ob)) { result.add((STEdge)ob); }
		}
		return result.toArray(new STEdge[result.size()]);
	}


	/** Get the number of graph edges.
	 * @return edge count */
	final int getEdgeCount() { STEdge[] o = getEdges(); return (o == null) ? 0 : o.length; }


	/** Get the list of graph widgets.
	 * @return widget list */
	public final ArrayList<STWidget> getWidgets() {
		ArrayList<STWidget> result = new ArrayList<STWidget>();
		Object[] o = getEntities();
		if(o == null) { return result; }
		for(Object ob : o) {
			if(STTools.isWidget(ob)) { result.add((STWidget)ob); }
		}
		return result;
	}


	/** Get the list of graph input widgets.
	 * @return widget list */
	public final ArrayList<STWidget> getInputWidgets() {
		ArrayList<STWidget> result = new ArrayList<STWidget>();
		Object[] o = getEntities();
		if(o == null) { return null; }
		for(Object ob : o) {
			if(STTools.isInputWidget(ob)) { result.add((STWidget)ob); }
		}
		return result;
	}


	/** Get the number of graph widgets.
	 * @return widget count */
	final int getWidgetCount() { return getWidgets().size(); }


	/** Get the array of the graph edges entering or leaving the node.
	 * @param node the node
	 * @param edgeMode the edge mode (EDGEMODE_IN, EDGEMODE_OUT, EDGEMODE_INOUT)  
	 * @return edges */
	public final STEdge[] getNodeEdges(final STNode node, final int edgeMode) {
		if(edgeList == null || edgeList.length == 0) { return null; }
		ArrayList<STEdge> result = new ArrayList<STEdge>();
		if(edgeMode == EDGEMODE_IN) {
			for(STEdge edge : edgeList) {
				if(edge.getTargetNode() == node) { result.add(edge); }
			}
		} else if(edgeMode == EDGEMODE_OUT) {
			for(STEdge edge : edgeList) {
				if(edge.getSourceNode() == node) { result.add(edge); }
			}
		} else {
			for(STEdge edge : edgeList) {
				if(edge.getSourceNode() == node || edge.getTargetNode() == node) { result.add(edge); }
			}
		}
		return result.toArray(new STEdge[result.size()]);
	}


	/** Get the array of input nodes.
	 * @return list */
	public final ValueNode[] getInputNodeList() { return inputNodeList; }


	/** Get the array of input nodes that are not bound by input widgets.
	 * @return list */
	public final ValueNode[] getUnboundInputNodeList() {
		int size = inputNodeList.length;
		if(size == 0) { return inputNodeList; }
		ArrayList<ValueNode> result = new ArrayList<ValueNode>();
		for(int i = 0; i < size; i++) {
			if(!inputNodeList[i].isBoundToWidget()) { result.add(inputNodeList[i]); }
		}
		return result.toArray(new ValueNode[result.size()]);
	}


	/** Get the array of output nodes.
	 * @return list */
	public final ValueNode[] getOutputNodeList() { return outputNodeList; }


	/** Get the array of state nodes.
	 * @return list */
	public final STNode[] getStateNodeList() { return stateNodeList; }


	/** Remove all edges that are not connected. */
	final void removeNotConnectedEdges() {
		Object[] o = getEntities();
		if(o == null) { return; }
		int top = o.length - 1;
		Object ob = null;
		STEdge edge;
		for(int i = top; i >= 0; i--) {	// backward indexing to keep into account the automatic renumber due to removals...
			ob = o[i];
			if(STTools.isEdge(ob)) {
				edge = (STEdge)ob;
				if(edge.getSourceNode() == null || edge.getTargetNode() == null) {
					try { getModel().remove(new Object[]{ob}); } catch (Exception ex) { ; }
				}
			}
		}
		try { clearSelection(); } catch (Exception ex) { ; }
	}


	/** Remove the specified edge if it is wrong for some reason.
	 * @param edge the edge
	 * @return <code>true</code> if an edge has been actually removed */
	final boolean checkAndRemoveIfWrongEdge(final STEdge edge) {
		STModel model = (STModel)getModel();
		STNode source = edge.getSourceNode();
		STNode target = edge.getTargetNode();
		if(source == null || target == null) { // remove if it is not connected
			try { model.remove(new Object[]{edge}); } catch (Exception ex) { ; }
			try { clearSelection(); } catch (Exception ex) { ; }
			return true;
		}
		if(source == target) { // remove if it is a loop
			try { model.remove(new Object[]{edge}); } catch (Exception ex) { ; }
			try { clearSelection(); } catch (Exception ex) { ; }
			return true;
		}
		// remove if another edge with the same source and target exists
		Object[] o = getEntities();
		Object ob = null;
		STEdge xedge;
		for(int i = 0; i < o.length; i++) {
			ob = o[i];
			if(STTools.isEdge(ob)) {
				xedge = (STEdge)ob;
				if(xedge != edge && xedge.getSourceNode() == source && xedge.getTargetNode() == target) {
					try { model.remove(new Object[]{edge}); } catch (Exception ex) { ; }
					try { clearSelection(); } catch (Exception ex) { ; }
					STTools.messenger(STTools.MESSAGETYPE_INF, STGraphC.getMessage("MSG.DUPLICATED_EDGE_REMOVED")); //$NON-NLS-1$
					return true;
				}
			}
		}
		return false;
	}


	/** Get the edge between the specified nodes, if any, if null otherwise.
	 * @param node1 one node
	 * @param node2 the other node
	 * @return the edge */
	public final STEdge getEdge(final STNode n1, final STNode n2) {
		if(n1 == null || n2 == null) { return null; }
		for(Object o : getEntities()) {
			if(STTools.isEdge(o)) {
				STEdge edge = (STEdge)o;
				if(edge.getSourceNode() == n1 && edge.getTargetNode() == n2) { return edge; }
				//if(edge.getSourceNode() == n2 && edge.getTargetNode() == n1) { return edge; } //only oriented specs are allowed... 
			}
		}
		return null;
	}


	/** Check whether there exists an edge of specified source and target nodes.
	 * @param source the source node
	 * @param target the target node
	 * @return <code>true</code> if an edge exists */
	public final boolean checkEdge(final STNode source, final STNode target) {
		if(source == null || target == null) { return false; }
		for(Object o : getEntities()) {
			if(STTools.isEdge(o)) {
				STEdge edge = (STEdge)o;
				if(edge.getSourceNode() == source && edge.getTargetNode() == target) { return true; }
			}
		}
		return false;
	}


	/** Remove the edge of specified source and target nodes.
	 * @param source the source node
	 * @param target the target node */
	public final void removeEdge(final STNode source, final STNode target) {
		STModel model = (STModel)getModel();
		if(source == null || target == null) { return; }
		for(Object o : getEntities()) {
			if(STTools.isEdge(o)) {
				STEdge edge = (STEdge)o;
				if(edge.getSourceNode() == source && edge.getTargetNode() == target) {
					try { model.remove(new Object[]{edge}); } catch (Exception ex) { ; }
				}
			}

		}
	}


	/** Handle the input attribute of auxiliary nodes on edge insertion or removal. */
	final void handleInputOnChanges() {
		STEdge[] edges = getEdges(); if(edges == null) { return; }
		STNode[] nodes = getNodes(); if(nodes == null) { return; }
		STNode source;
		STNode target;
		for(STNode node : nodes) { node.resetTopologicalProperties(); }
		for(STEdge edge : edges) { // set the CONNECTEDFROM properties
			source = edge.getSourceNode();
			if(source == null) { return; } // (temporarily) invalid graph
			target = edge.getTargetNode();
			if(target == null) { return; } // (temporarily) invalid graph
			STTools.connect2wrapper(source, target);
		}
		for(STNode node : nodes) {
			if(node.isAlgebraic()) {
				node.setInput(!node.isDefinedByNodes() && !node.hasIncomingArrows());
			} else {
				node.setInput(false);
			}
		}
	}


	/** Reset the whole model. */
	public final void resetModel() {
		Object[] o = getEntities();
		if(o == null) { return; }
		for(Object ob : o) {
			if(STTools.isWidget(ob)) { STGraph.getSTC().getCurrentDesktop().remove(((STWidget)ob).frame); }
		}
		getModel().remove(getCells());
	}


	/** Set the lastError.
	 * @param lastError the lastError to set */
	public final void setLastError(final String lastError) { this.lastError = lastError; }


	/** Reset the lastError. */
	public final void resetLastError() { this.lastError = null; }


	/** Get the lastError.
	 * @return lastError */
	public final String getLastError() { return this.lastError; }


	/** Set the lastErrorNode.
	 * @param lastErrorNode the lastErrorNode to set */
	public final void setLastErrorNode(final STNode lastErrorNode) { this.lastErrorNode = lastErrorNode; }


	/** Reset the lastErrorNode. */
	public final void resetLastErrorNode() { this.lastErrorNode = null; }


	/** Get the lastErrorNode.
	 * @return lastErrorNode */
	public final STNode getLastErrorNode() { return this.lastErrorNode; }


	/** Return an HTML-formatted string with a summary description of the model.
	 * @return string */
	@Override
	public final String toString() {
		STGraphExec graph = (STGraphExec)this;
		graph.initTimeBasis(-1);
		StringBuffer s = new StringBuffer();
		s.append("<b>" + STGraphC.getMessage("SYSTEM.INFO.SYSTEM") + ": " + (!STTools.isEmpty(modelName) ? modelName : title) + "</b><br>");
		s.append("<font size='-2'>(" + fileName + ")</font><br>");
		if(modelDescription != null) { s.append(modelDescription + "<br>"); }

		if(graph.getNodeCount() == 0) {
			s.append("<b>" + STGraphC.getMessage("SYSTEM.INFO.MODEL") + " ");
			s.append(STGraphC.getMessage("SYSTEM.INFO.EMPTY") + "</b>");
			return s.toString();
		}

		s.append("<b>" + STGraphC.getMessage("SYSTEM.INFO.MODEL") + ":</b><ul>");
		s.append("<li>" + (isSequential ? STGraphC.getMessage("SYSTEM.INFO.SEQ") : STGraphC.getMessage("SYSTEM.INFO.COMB")) + "</li>");
		s.append("<li>" + (isOpen ? STGraphC.getMessage("SYSTEM.INFO.OPEN") : STGraphC.getMessage("SYSTEM.INFO.AUTO")) + "</li>");
		s.append("</ul>");

		s.append("<b>" + STGraphC.getMessage("SYSTEM.INFO.SIMTIME") + "</b>");
		if(!STTools.isEmpty(timeUnitDescription)) { s.append(STTools.SPACE + STTools.OPENV + timeUnitDescription + STTools.CLOSEV); }
		s.append(":<ul>");
		s.append("<li><code>" + STGraphC.getMessage("SYSTEM.INFO.TIME0") + ": " + time0 + "</code></li>");
		s.append("<li><code>" + STGraphC.getMessage("SYSTEM.INFO.TIMED") + ": " + timeD + "</code></li>");
		s.append("<li><code>" + STGraphC.getMessage("SYSTEM.INFO.TIME1") + ": " + time1 + "</code></li>");
		s.append("</ul>");

		return s.toString();
	}


	/** Return an HTML-formatted string with the computational description of the model.
	 * @return string */
	public final String getComputationalDescription() {
		STGraphExec graph = (STGraphExec)this;
		StringBuffer s = new StringBuffer();
		s.append("<style type=\"text/css\">" +
				".code { background-color: #EEEEEE; font-family: 'courier',sans-serif; font-size: 10px; }" +
				".desc { font-size: 11px; }" +
				"</style>");
		s.append(toString());
		if(graph.getNodeCount() == 0) { return s.toString(); }
		if(isSequential) {
			s.append("<h2>" + STGraphC.getMessage("SYSTEM.INFO.STATEVAR") + "</h2>");
			for(ValueNode vnode : stateNodeList) {
				s.append("<p>" + getFullNodeDescription(vnode) + "<br/>");
				s.append(STGraphC.getMessage("SYSTEM.INFO.X0") + " = <code class='code'>" + STData.toHTML(vnode.getStateInit()) + "</code><br/>");
				s.append(STGraphC.getMessage("SYSTEM.INFO.PHI") + " = <code class='code'>" + STData.toHTML(vnode.getStateTransition()) + "</code><br/>");
				if(vnode.isStateWithOutput()) { s.append(STGraphC.getMessage("SYSTEM.INFO.ETA") + " = <code class='code'>" + STData.toHTML(vnode.getExpression()) + "</code><br/>"); }
				//if(vnode.isGlobal()) { s.append(" <i>" + STGraphC.getMessage("SYSTEM.INFO.ISGLOBAL") + "</i>"); }
				//if(vnode.isOutput()) { s.append(" <i>" + STGraphC.getMessage("SYSTEM.INFO.ISOUTPUT") + "</i>"); }
				s.append("</p>");
			}
		}
		if(isOpen) {
			s.append("<h2>" + STGraphC.getMessage("SYSTEM.INFO.INPUTVAR") + "</h2>");
			for(ValueNode vnode : inputNodeList) {
				if(!vnode.isConstant() || vnode.isBoundToWidget()) {
					s.append("<p>" + getFullNodeDescription(vnode) + "<br/>");
					if(vnode.isBoundToWidget()) {
						s.append("<i>" + STGraphC.getMessage("SYSTEM.INFO.BOUNDVAR") + "</i>");
					} else {
						s.append(STGraphC.getMessage("SYSTEM.INFO.ETA") + " = <code class='code'>" + STData.toHTML(vnode.getExpression()) + "</code><br/>");
					}
					//if(vnode.isGlobal()) { s.append(" <i>" + STGraphC.getMessage("SYSTEM.INFO.ISGLOBAL") + "</i>"); }
					//if(vnode.isOutput()) { s.append(" <i>" + STGraphC.getMessage("SYSTEM.INFO.ISOUTPUT") + "</i>"); }
					s.append("</p>");
				}
			}
		}
		if(auxiliaryNodeList != null && auxiliaryNodeList.length > 0) {
			ArrayList<ValueNode> otherNodeList = new ArrayList<ValueNode>();
			for(ValueNode vnode : auxiliaryNodeList) {
				if(vnode.isConstant() || !vnode.isInput()) { otherNodeList.add(vnode); }
			}
			if(otherNodeList.size() > 0) {
				s.append("<h2>" + STGraphC.getMessage("SYSTEM.INFO.OTHERVAR") + "</h2>");
				for(ValueNode vnode : otherNodeList) {
					s.append("<p>" + getFullNodeDescription(vnode) + "<br/>");
					s.append(STGraphC.getMessage("SYSTEM.INFO.ETA") + " = <code class='code'>" + STData.toHTML(vnode.getExpression()) + "</code><br/>");
					//if(vnode.isGlobal()) { s.append(" <i>" + STGraphC.getMessage("SYSTEM.INFO.ISGLOBAL") + "</i>"); }
					//if(vnode.isOutput()) { s.append(" <i>" + STGraphC.getMessage("SYSTEM.INFO.ISOUTPUT") + "</i>"); }
					s.append("</p>");
				}
			}
		}
		if(isComposed) {
			s.append("<h2>" + STGraphC.getMessage("SYSTEM.INFO.MODELVAR") + "</h2>");
			for(ModelNode mnode : modelNodeList) { s.append("<p>" + getFullNodeDescription(mnode) + "<p/>"); }
		}
		return s.toString();
	}


	/** Return an HTML-formatted string with the full description of the specified node.
	 * @param node the node to be described
	 * @return string */
	private final StringBuffer getFullNodeDescription(final STNode node) {
		StringBuffer s = new StringBuffer();
		s.append("<b>" + node.getName() + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$
		String t = node.getCProperty("Unit"); //$NON-NLS-1$
		if(!STTools.isEmpty(t)) { s.append(STTools.SPACE + STTools.OPENV + t + STTools.CLOSEV); }
		if(!STTools.isEmpty(node.getDescription())) { s.append("<br/><span class='desc'>" + node.getDescription() + "</span>"); } //$NON-NLS-1$ //$NON-NLS-2$
		String t1 = node.getCProperty("Min"); //$NON-NLS-1$
		String t2 = node.getCProperty("Max"); //$NON-NLS-1$
		if(!STTools.isEmpty(t1) || !STTools.isEmpty(t2)) {
			s.append("<br/>"); //$NON-NLS-1$
			if(!STTools.isEmpty(t1)) {
				s.append("Min" + STTools.COLON + STTools.SPACE + t1); //$NON-NLS-1$
				if(!STTools.isEmpty(t2)) { s.append(STTools.SPACE + STTools.SEMICOLON); }
			}
			if(!STTools.isEmpty(t2)) { s.append("Max" + STTools.COLON + STTools.SPACE + t2); } //$NON-NLS-1$
		}
		return s;
	}


	/** Get an HTML-formatted string with the web-oriented description (i.e., data dictionary) of the model.
	 * <br>See the constraints specified in STGraphChecker.checkForWeb().
	 * <br>See also the recoding for proper sorting done in DataDictionaryComparator.compare().
	 * @param lang the language
	 * @return the description */
	public final String getWebDescription(final String lang) {
		if(webModelGroupData == null || webModelGroupData.get(lang) == null || webModelGroupData.get(lang).length == 0) { return STGraphC.getMessage("DATADICT.NOGROUPDATA"); }
		int numGroups = webModelGroupData.get(lang).length;
		if(getModelLanguages().length > 1) {
			for(String lan : getModelLanguages()) {
				if(webModelGroupData.get(lan) == null || webModelGroupData.get(lan).length == 0 || webModelGroupData.get(lan).length != numGroups) { return STGraphC.getMessage("DATADICT.WRONGGROUPNUM"); }
			}
		}
		ArrayList<STNode> nodes = getSortedNodesForWeb();
		if(nodes.size() == 0) { return STGraphC.getMessage("DATADICT.NONODES"); }
		StringBuffer s = new StringBuffer();
		s.append("<style type=\"text/css\">" +
				".code { background-color: #EEEEEE; font-family: 'courier',sans-serif; font-size: 10px; }" +
				".desc { font-size: 11px; }" +
				"</style><body>");
		s.append("<h1>" + STGraphC.getMessage("DATADICT.TITLE", lang) + "</h1>");

		String prevGroup = STTools.BLANK;
		String prevIType = STTools.BLANK;
		String prevOType = STTools.BLANK;
		for(STNode node : nodes) {
			String group = node.getCProperty("Group");
			String inputType = node.getCProperty("InputType");
			String outputType = node.getCProperty("OutputType");
			if(node.isOutput() && outputType != null && outputType.equals("6")) {
				s.append("<h2>" + STGraphC.getMessage("DATADICT.TARGETVAR", lang) + "</h2>");
			} else {
				if(!group.equals(prevGroup)) {
					int groupNum = Integer.parseInt(group);
					if(groupNum > numGroups) { return STGraphC.getMessage("DATADICT.WRONGGROUPDATA1") + STTools.SPACE + node.getName() + STTools.SPACE + STGraphC.getMessage("DATADICT.WRONGGROUPDATA2"); }
					s.append("<h2>" + group + STTools.DOT + STTools.SPACE + webModelGroupData.get(lang)[groupNum - 1][0] + "</h2>");
					prevGroup = group;
					prevIType = STTools.BLANK;
					prevOType = STTools.BLANK;
				}
				if(node.isInput() && inputType != null) {
					if(inputType.equals("3") && !prevIType.equals("3")) {
						s.append("<h3>" + STGraphC.getMessage("DATADICT.DECISIONS", lang) + "</h3>");
						prevIType = inputType;
					} else if(inputType.equals("6") && !prevIType.equals("6")) {
						s.append("<h3>" + STGraphC.getMessage("DATADICT.PARAMETERS", lang) + "</h3>");
						prevIType = inputType;
					}
				}
				if(node.isOutput() && outputType != null) {
					if((outputType.equals("2") || outputType.equals("3")) && (!prevOType.equals("2") && !prevOType.equals("3"))) {
						s.append("<h3>" + STGraphC.getMessage("DATADICT.RESULTS", lang) + "</h3>");
						prevOType = outputType;
					} else if((outputType.equals("4") || outputType.equals("5")) && (!prevOType.equals("4") && !prevOType.equals("5"))) {
						s.append("<h3>" + STGraphC.getMessage("DATADICT.MARKETDATA", lang) + "</h3>");
						prevOType = outputType;
					} 
				}
			}
			s.append(getFullNodeWebDescription(node, lang));
		}
		return s.toString();
	}


	/** Get an HTML-formatted string with the web-oriented overview of the model.
	 * <br>The result is a reduced version of the description generated by getWebDescription().
	 * @param lang the language
	 * @return the overview */
	public final String getWebOverview(final String lang) {
		if(webModelGroupData == null || webModelGroupData.get(lang) == null || webModelGroupData.get(lang).length == 0) { return STGraphC.getMessage("DATADICT.NOGROUPDATA"); }
		int numGroups = webModelGroupData.get(lang).length;
		if(getModelLanguages().length > 1) {
			for(String lan : getModelLanguages()) {
				if(webModelGroupData.get(lan) == null || webModelGroupData.get(lan).length == 0 || webModelGroupData.get(lan).length != numGroups) { return STGraphC.getMessage("DATADICT.WRONGGROUPNUM"); }
			}
		}
		ArrayList<STNode> nodes = getSortedNodesForWeb();
		if(nodes.size() == 0) { return STGraphC.getMessage("DATADICT.NONODES"); }
		StringBuffer s = new StringBuffer();
		s.append("<style type=\"text/css\">" +
				".group { text-align:center; font-weight:bold; }" +
				".head { background-color: #EEEEEE; font-style:italic; }" +
				".numcell { text-align:right; }" +
				"</style><body>");
		s.append("<h1>" + STGraphC.getMessage("DATADICT.SHORT.TITLE", lang) + "</h1><table border='1'>");

		String prevGroup = STTools.BLANK;
		String prevIType = STTools.BLANK;
		String prevOType = STTools.BLANK;
		for(STNode node : nodes) {
			String group = node.getCProperty("Group");
			String inputType = node.getCProperty("InputType");
			String outputType = node.getCProperty("OutputType");
			if(node.isOutput() && outputType != null && outputType.equals("6")) {
				////s.append("<tr><td colspan='4' class='group'>" + STGraphC.getMessage("DATADICT.TARGETVAR", lang) + "</td></tr>");
			} else {
				if(!group.equals(prevGroup)) {
					int groupNum = Integer.parseInt(group);
					if(groupNum > numGroups) { return STGraphC.getMessage("DATADICT.WRONGGROUPDATA1") + STTools.SPACE + node.getName() + STTools.SPACE + STGraphC.getMessage("DATADICT.WRONGGROUPDATA2"); }
					s.append("<tr><td colspan='4' class='group'>" + group + STTools.DOT + STTools.SPACE + webModelGroupData.get(lang)[groupNum - 1][0] + "</td></tr>");
					prevGroup = group;
					prevIType = STTools.BLANK;
					prevOType = STTools.BLANK;
				}
				if(node.isInput() && inputType != null) {
					if(inputType.equals("3") && !prevIType.equals("3")) {
						s.append("<tr><td class='head'>" + STGraphC.getMessage("DATADICT.DECISIONS", lang) + "</td><td class='head'>Val</td><td class='head'>Min</td><td class='head'>Max</td></tr>");
						prevIType = inputType;
					} else if(inputType.equals("6") && !prevIType.equals("6")) {
						s.append("<tr><td class='head'>" + STGraphC.getMessage("DATADICT.PARAMETERS", lang) + "</td><td class='head'>Val</td><td class='head'>Min</td><td class='head'>Max</td></tr>");
						prevIType = inputType;
					}
				}
				if(node.isOutput() && outputType != null) {
					if((outputType.equals("2") || outputType.equals("3")) && (!prevOType.equals("2") && !prevOType.equals("3"))) {
						////s.append("<tr><td><i>" + STGraphC.getMessage("DATADICT.RESULTS", lang) + "</i></td><td>Val</td><td>Min</td><td>Max</td></tr>");
						prevOType = outputType;
					} else if((outputType.equals("4") || outputType.equals("5")) && (!prevOType.equals("4") && !prevOType.equals("5"))) {
						s.append("<tr><td class='head'>" + STGraphC.getMessage("DATADICT.MARKETDATA", lang) + "</td><td class='head'>Val</td><td class='head'>Min</td><td class='head'>Max</td></tr>");
						prevOType = outputType;
					}
				}
			}
			////s.append(getShortNodeWebDescription(node, lang));
			if((node.isInput() && inputType != null) ||
					(node.isOutput() && outputType != null && outputType.equals("4"))) {
				s.append(getShortNodeWebDescription(node, lang));
			}
		}
		s.append("</table>");
		return s.toString();
	}


	/** Get the properly sorted list of the nodes to be included in the web-oriented (i.e., data dictionary) description of the model.
	 * @return list */
	@SuppressWarnings("unchecked")
	public ArrayList<STNode> getSortedNodesForWeb() {
		ArrayList<STNode> nodes = new ArrayList<STNode>();
		STNode[] n = getNodes();
		if(n.length == 0) { return nodes; }
		for(STNode node: n) {
			if(node.isInput()) {
				String t = node.getCProperty("InputType");
				if(!STTools.isEmpty(t) && (t.equals("3") || t.equals("6")) && !STTools.isEmpty(node.getCProperty("Group"))) { nodes.add(node); }
			}
			if(node.isOutput()) {
				String t = node.getCProperty("OutputType");
				if(!STTools.isEmpty(t) && (t.equals("2") || t.equals("3") || t.equals("4") || t.equals("5") || t.equals("6")) && !STTools.isEmpty(node.getCProperty("Group"))) { nodes.add(node); }
			}
		}
		if(nodes.size() == 0) { return nodes; }
		Collections.sort(nodes, STGraph.getSTC().getDataDictionaryComparator());
		return nodes;
	}


	/** Get an HTML-formatted string with the full description of the specified node.
	 * @param node the node to be described
	 * @param lang the language
	 * @return string */
	private final StringBuffer getFullNodeWebDescription(final STNode node, final String lang) {
		String t;
		String t2;
		StringBuffer s = new StringBuffer();
		//s.append("<h4>" + node.getCProperty("Order") + STTools.SPACE + node.getCName(lang) + "</h4>");
		s.append("<h4>" + node.getCName(lang) + "</h4>");
		HashMap<Integer, String> m;
		if(node.isInput() && (m = node.getInputTextCProperty()) != null) {
			s.append(STGraphC.getMessage("DATADICT.POSSIBLEVALUES", lang) + STTools.COLON + STTools.SPACE);
			for(String u : m.values()) {
				s.append(STTools.DOUBLEQUOTE + u + STTools.DOUBLEQUOTE + STTools.SPACE);
			}
			s.append("<br>");
		} else if(node.isOutput() && (m = node.getOutputTextCProperty()) != null) {
			s.append(STGraphC.getMessage("DATADICT.POSSIBLEVALUES", lang) + STTools.COLON + STTools.SPACE);
			for(String u : m.values()) {
				s.append(STTools.DOUBLEQUOTE + u + STTools.DOUBLEQUOTE + STTools.SPACE);
			}
			s.append("<br>");
		} else {
			if(!STTools.isEmpty(t = node.getCProperty("Unit"))) { s.append(STGraphC.getMessage("DATADICT.UNIT", lang) + STTools.COLON + STTools.SPACE + "<i>" + t + "</i><br>"); }
			if(!STTools.isEmpty(t = node.getCProperty("Min"))) { s.append(STGraphC.getMessage("DATADICT.MIN", lang) + STTools.COLON + STTools.SPACE + t + "<br>"); }
			if(!STTools.isEmpty(t = node.getCProperty("Max"))) { s.append(STGraphC.getMessage("DATADICT.MAX", lang) + STTools.COLON + STTools.SPACE + t + "<br>"); }
			if(!STTools.isEmpty(t = node.getCProperty("DefaultValue"))) {
				if(!STTools.isEmpty(t2 = node.getCProperty("InputType")) && t2.equals("6")) {
					s.append(STGraphC.getMessage("DATADICT.VALUE", lang));
				} else {
					s.append(STGraphC.getMessage("DATADICT.DEFAULTVALUE", lang));
				}
				s.append(STTools.COLON + STTools.SPACE + t + "<br>");
			}
		}
		if(!STTools.isEmpty(t = node.getDescription(lang))) { s.append("<span class='desc'>" + STTools.adaptToHTML(t) + "</span>"); }
		return s;
	}


	/** Get an HTML-tabular formatted string with the short description of the specified node.
	 * @param node the node to be described
	 * @param lang the language
	 * @return string */
	private final StringBuffer getShortNodeWebDescription(final STNode node, final String lang) {
		String t;
		StringBuffer s = new StringBuffer();
		s.append("<tr><td>" + node.getCName(lang));
		HashMap<Integer, String> m;
		if(node.isInput() && (m = node.getInputTextCProperty()) != null) {
			s.append("</td><td colspan='3'>" + STTools.OPENC);
			for(String u : m.values()) {
				s.append(STTools.DOUBLEQUOTE + u + STTools.DOUBLEQUOTE + STTools.SPACE);
			}
			s.append(STTools.CLOSEC + "</td></tr>");
		} else if(node.isOutput() && (m = node.getOutputTextCProperty()) != null) {
			s.append("</td><td colspan='3'>" + STTools.OPENC);
			for(String u : m.values()) {
				s.append(STTools.DOUBLEQUOTE + u + STTools.DOUBLEQUOTE + STTools.SPACE);
			}
			s.append(STTools.CLOSEC + "</td></tr>");
		} else {
			if(!STTools.isEmpty(t = node.getCProperty("Unit"))) { s.append(STTools.SPACE + STTools.OPENV + t + STTools.CLOSEV); }
			s.append("</td>");
			s.append("<td class='numcell'>" + (!STTools.isEmpty(t = node.getCProperty("DefaultValue")) ? t : STTools.SPACE) + "</td>");
			s.append("<td class='numcell'>" + (!STTools.isEmpty(t = node.getCProperty("Min")) ? t : STTools.SPACE) + "</td>");
			s.append("<td class='numcell'>" + (!STTools.isEmpty(t = node.getCProperty("Max")) ? t : STTools.SPACE) + "</td></tr>");
		}
		return s;
	}


	/** Return a text-only string with a short description of the graph nodes and their values and next states.
	 * @param pattern string to be matched by node names to be displayed
	 * @return string */
	@SuppressWarnings("unchecked")
	public final String getComputationalDescription2(final String pattern) {
		STGraphExec graph = (STGraphExec)this;
		StringBuffer s = new StringBuffer();
		if(graph.getNodeCount() == 0) { return s.toString(); }
		STNode[] nodes = getNodes();
		Arrays.sort(nodes, new NodeNameComparator());
		ValueNode n;
		for(STNode node : nodes) {
			if(!node.isModel() && node.getName().indexOf(pattern) != -1) {
				n = (ValueNode)node;
				if(n.isAlgebraic()) {
					s.append("[aux] " + n.getName() + " = " + n.getValue() + STTools.NEWLINE);
				} else {
					s.append("[sta] " + n.getName() + " = " + n.getValue() + " / " + n.getNextState() + STTools.NEWLINE);
				}
			}
		}
		return s.toString();
	}


	/** Return a text-only string with a short description of the graph nodes and their values and next states.
	 * @return string */
	public final String getComputationalDescription2() { return getComputationalDescription2(STTools.BLANK); }


	/** Get the integration phase.
	 * @return the integrationPhase */
	public final int getIntegrationPhase() { return integrationPhase; }


	/** Get the input stream for this graph.
	 * @return stream */
	public final InputStream getInputStream() { return stream; }


	/** Set the filename.
	 * @param fileName the fileName to set */
	public final void setFileName(final String fileName) { this.fileName = fileName; }


	/** Get the filename.
	 * @return the fileName */
	public String getFileName() { return fileName; }


	/** Set the model name.
	 * @param modelName the systemName to set */
	public final void setModelName(final String modelName) { this.modelName = modelName; }


	/** Get the model name.
	 * @return the model name */
	public String getModelName() { return modelName; }


	/** Set the model description.
	 * @param description the model description to set */
	public final void setModelDescription(final String description) { this.modelDescription = description; }


	/** Get the model description.
	 * @return the model description */
	public String getModelDescription() { return modelDescription; }


	/** Get the exception handling level (what should the engine do in case of runtime exceptions?).
	 * <br>0: set the var to 0.0 and continue; 1: stop (default).
	 * @param exceptionHandling the exception handling level */
	public final void setExceptionHandling(final int exceptionHandling) { this.exceptionHandling = exceptionHandling; }


	/** Get the exception handling level (what should the engine do in case of runtime exceptions?).
	 * <br>0: set the var to 0.0 and continue; 1: stop (default).
	 * @return exception handling level */
	public int getExceptionHandling() { return exceptionHandling; }


	/** Get whether the simulation should be continued despite an exception.
	 * @return continue? */
	public boolean continueWithExceptions() { return exceptionHandling == EXCEPTIONHANDLING_CONTINUE; }


	/** Set whether the computation should keep interrupts into account.
	 * @param withInterrupts the with interrupts */
	public final void setWithInterrupts(final boolean withInterrupts) { this.withInterrupts = withInterrupts; }


	/** Get whether the computation should keep interrupts into account.
	 * @return with interrupts */
	public boolean isWithInterrupts() { return withInterrupts; }


	/** Set whether the values of the output nodes should be saved at the end of each simulation.
	 * @param dataSaved the data saved */
	public final void setDataSaved(final boolean dataSaved) { this.dataSaved = dataSaved; }


	/** Get whether the values of the output nodes should be saved at the end of each simulation.
	 * @return data saved */
	public boolean isDataSaved() { return dataSaved; }


	/** Set whether the model is aimed at being executed by STGraphWeb.
	 * @param forWeb the for web */
	public final void setForWeb(final boolean forWeb) { this.forWeb = forWeb; }


	/** Get whether the model is aimed at being executed by STGraphWeb.
	 * @return for web */
	public final boolean isForWeb() { return forWeb; }


	/** Get the default language of model when executed by STGraphWeb.
	 * @return default language */
	public final String getDefaultModelLanguage() { 
		if(STTools.isEmpty(modelLanguages)) { return DEFAULT_MODEL_LANGUAGE; } //just as a default
		return modelLanguages.split(STTools.COMMA)[0].trim();
	}


	/** Set all languages of model when executed by STGraphWeb.
	 * @param all languages */
	public final void setModelLanguagesAsString(final String languages) { this.modelLanguages = languages; }


	/** Get all languages of model when executed by STGraphWeb.
	 * @return all languages */
	public final String getModelLanguagesAsString() {
		if(STTools.isEmpty(modelLanguages)) { return STGraphC.getBasicProps().getProperty("MODEL.LANGUAGES.DEFAULT"); } //just as a default //$NON-NLS-1$
		return modelLanguages;
	}


	/** Get all languages of model when executed by STGraphWeb.
	 * @return all languages */
	public final String[] getModelLanguages() {
		if(STTools.isEmpty(modelLanguages)) { return new String[]{STGraphC.getBasicProps().getProperty("MODEL.LANGUAGES.DEFAULT")}; } //just as a default //$NON-NLS-1$
		return modelLanguages.split(STTools.COMMA);
	}


	/** Get all non default languages of model when executed by STGraphWeb.
	 * @return all languages */
	public final String[] getNonDefaultModelLanguages() {
		if(STTools.isEmpty(modelLanguages)) { return new String[]{}; }
		String[] x = modelLanguages.split(STTools.COMMA);
		if(x.length == 1) { return new String[]{}; }
		String[] y = new String[x.length - 1];
		for(int i = 1; i < x.length; i++) { y[i - 1] = x[i]; }
		return y;
	}


	/** Set timeFrame.
	 * @param timeFrame the timeFrame to set */
	public final void setTimeFrame(int timeFrame) { this.timeFrame = timeFrame; }


	/** Get timeFrame.
	 * @return the timeFrame */
	public final int getTimeFrame() { return timeFrame; }


	/** Set maxSteps.
	 * @param maxSteps the maxSteps to set */
	public final void setMaxSteps(int maxSteps) { this.maxSteps = maxSteps; }


	/** Get maxSteps.
	 * @return the maxSteps */
	public final int getMaxSteps() { return maxSteps; }


	/** Set system time.
	 * @param sysTime the sysTime to set */
	public final void setSysTime(long sysTime) { this.sysTime = sysTime; }


	/** Get system time.
	 * @return the sysTime */
	public final long getSysTime() { return sysTime; }


	/** Set index origin.
	 * @param io the index origin to set */
	public final void setIO(int io) { this.io = io; }


	/** Get index origin.
	 * @return the io */
	public final int getIO() { return io; }


	/** Set time0.
	 * @param time0 the time0 to set */
	public final void setTime0(double time0) { this.time0 = time0; }


	/** Get time0.
	 * @return the time0 */
	public final double getTime0() { return time0; }


	/** Set time1.
	 * @param time1 the time1 to set */
	public final void setTime1(double time1) { this.time1 = time1; }


	/** Get time1.
	 * @return the time1 */
	public final double getTime1() { return time1; }


	/** Set timeD.
	 * @param timeD the timeD to set */
	public final void setTimeD(double timeD) { this.timeD = timeD; }


	/** Get timeD.
	 * @return the timeD */
	public final double getTimeD() { return timeD; }


	/** Set numSteps.
	 * @return the numSteps */
	public final int computeNumSteps() { return numSteps = (int)STData.round(1 + (time1 - time0) / timeD, 0); }


	/** Set timeD.
	 * @param numSteps the numSteps to set */
	public final void setNumSteps(int numSteps) { this.numSteps = numSteps; }


	/** Get numSteps.
	 * @return the numSteps */
	public final int getNumSteps() { return numSteps; }


	/** Set runnable.
	 * @param runnable the runnable to set */
	public final void setRunnable(boolean runnable) {
		this.runnable = runnable; }


	/** Get runnable.
	 * @return the runnable */
	public final boolean isRunnable() { return runnable; }


	/** Get whether the node values should be shown.
	 * @return the showNodeValues */
	public boolean isShowNodeValues() { return showNodeValues; }


	/** Set whether the node values should be shown.
	 * @param showNodeValues the showNodeValues to set */
	public void setShowNodeValues(boolean showNodeValues) { this.showNodeValues = showNodeValues; }


	/** Get whether the edge labels should be shown.
	 * @return the showEdgeLabels */
	public boolean isShowEdgeLabels() { return showEdgeLabels; }


	/** Set whether the edge labels should be shown.
	 * @param showEdgeLabels the showEdgeLabels to set */
	public void setShowEdgeLabels(boolean showEdgeLabels) { this.showEdgeLabels = showEdgeLabels; }


	/** Set the web model description, as the absolute URL of a web page containing the description.
	 * @param webModelDescription the webModelDescription to set
	 * @param lang the language */
	public void setWebModelDescription(String webModelDescription, final String lang) { this.webModelDescription.put(lang, webModelDescription); }


	/** Get the web model description for the default language, as the absolute URL of a web page containing the description.
	 * @return the webModelDescription */
	public String getWebModelDescription() { return webModelDescription.get(getDefaultModelLanguage()); }


	/** Get the web model description as the absolute URL of a web page containing the description for the specified language
	 * or, if null or for a non-existing language, as for the standard language.
	 * @param lang the language
	 * @return the webModelDescription */
	public String getWebModelDescription(final String lang) {
		if(lang == null) { return getWebModelDescription(); }
		String n = webModelDescription.get(lang);
		if(n != null) { return n; }
		return getWebModelDescription();
	}


	/** Get the web model description as the absolute URL of a web page containing the description for the specified language.
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return the webModelDescription */
	public String getWebModelDescription(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getWebModelDescription(lang); }
		if(lang == null || lang.equals(getDefaultModelLanguage())) { return getWebModelDescription(); }
		String n = webModelDescription.get(lang);
		if(n == null) { return STTools.BLANK; }
		return n;
	}


	/** Set the web time unit.
	 * @param webTimeUnit the webTimeUnit to set
	 * @param lang the language */
	public void setWebTimeUnit(String webTimeUnit, final String lang) { this.webTimeUnit.put(lang, webTimeUnit); }


	/** Get the web time unit.
	 * @return the webTimeUnit */
	public String getWebTimeUnit() { return webTimeUnit.get(getDefaultModelLanguage()); }


	/** Get the web time unit for the specified language
	 * or, if null or for a non-existing language, as for the standard language.
	 * @param lang the language
	 * @return the webTimeUnit */
	public String getWebTimeUnit(final String lang) {
		if(lang == null) { return getWebTimeUnit(); }
		String n = webTimeUnit.get(lang);
		if(n != null) { return n; }
		return getWebTimeUnit();
	}


	/** Get the web time unit for the specified language.
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return the webTimeUnit */
	public String getWebTimeUnit(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getWebTimeUnit(lang); }
		if(lang == null || lang.equals(getDefaultModelLanguage())) { return getWebTimeUnit(); }
		String n = webTimeUnit.get(lang);
		if(n == null) { return STTools.BLANK; }
		return n;
	}


	/** Set the web explanatory title.
	 * @param webExplTitle the webExplTitle to set
	 * @param lang the language */
	public void setWebExplTitle(String webExplTitle, final String lang) { this.webExplTitle.put(lang, webExplTitle); }


	/** Get the web explanatory title.
	 * @return the webExplTitle */
	public String getWebExplTitle() { return webExplTitle.get(getDefaultModelLanguage()); }


	/** Get the web explanatory title for the specified language
	 * or, if null or for a non-existing language, as for the standard language.
	 * @param lang the language
	 * @return the webExplTitle */
	public String getWebExplTitle(final String lang) {
		if(lang == null) { return getWebExplTitle(); }
		String n = webExplTitle.get(lang);
		if(n != null) { return n; }
		return getWebExplTitle();
	}


	/** Get the web explanatory title for the specified language.
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return the webExplTitle */
	public String getWebExplTitle(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getWebExplTitle(lang); }
		if(lang == null || lang.equals(getDefaultModelLanguage())) { return getWebExplTitle(); }
		String n = webExplTitle.get(lang);
		if(n == null) { return STTools.BLANK; }
		return n;
	}


	/** Set the web explanatory URL.
	 * @param webExplURL the webExplURL to set
	 * @param lang the language */
	public void setWebExplURL(String webExplURL, final String lang) { this.webExplURL.put(lang, webExplURL); }


	/** Get the web explanatory URL.
	 * @return the webExplURL */
	public String getWebExplURL() { return webExplURL.get(getDefaultModelLanguage()); }


	/** Get the web explanatory URL for the specified language
	 * or, if null or for a non-existing language, as for the standard language.
	 * @param lang the language
	 * @return the webExplTitle */
	public String getWebExplURL(final String lang) {
		if(lang == null) { return getWebExplURL(); }
		String n = webExplURL.get(lang);
		if(n != null) { return n; }
		return getWebExplURL();
	}


	/** Get the web explanatory URL for the specified language.
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return the webExplURL */
	public String getWebExplURL(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getWebExplURL(lang); }
		if(lang == null || lang.equals(getDefaultModelLanguage())) { return getWebExplURL(); }
		String n = webExplURL.get(lang);
		if(n == null) { return STTools.BLANK; }
		return n;
	}


	/** Set the time unit description.
	 * @param description the description to set */
	public final void setTimeUnitDescription(final String description) { this.timeUnitDescription = description; }


	/** Get the time unit description.
	 * @return the description */
	public String getTimeUnitDescription() { return STTools.isEmpty(timeUnitDescription) ? STTools.BLANK : timeUnitDescription; }


	/** Set the group data of the web model.
	 * @param group data the group data to set
	 * @param lang the language */
	public void setWebModelGroupData(String[][] webModelGroupData, final String lang) { this.webModelGroupData.put(lang, webModelGroupData); }


	/** Get the group data of the web model for the default language.
	 * <br>The array can be null, if no group data are available,
	 * or it is guaranteed that its 0-th column contains group titles
	 * and its 1-st column group descriptions.
	 * <br>The 0-th row contains the data for the group whose id is "1", "2", and so on.
	 * @return the group data */
	public String[][] getWebModelGroupData() { return webModelGroupData.get(getDefaultModelLanguage()); }


	/** Get the group data of the web model for the specified language
	 * or, if null or for a non-existing language, as for the standard language.
	 * <br>(see the specs of getWebModelGroupData())
	 * @param lang the language
	 * @return the group data */
	public String[][] getWebModelGroupData(final String lang) {
		if(lang == null) { return getWebModelGroupData(); }
		String[][] n = webModelGroupData.get(lang);
		if(n != null) { return n; }
		return getWebModelGroupData();
	}


	/** Get the group data of the web model for the specified language
	 * or, if null or for a non-existing language, as for the standard language.
	 * <br>(see the specs of getWebModelGroupData())
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return the group data */
	public String[][] getWebModelGroupData(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getWebModelGroupData(lang); }
		if(lang == null || lang.equals(getDefaultModelLanguage())) { return getWebModelGroupData(); }
		String[][] n = webModelGroupData.get(lang);
		if(n == null) { return null; }
		return n;
	}


	/** The Constant WEBMODEL_GROUPTYPE_STANDARD. */
	public static final int WEBMODEL_GROUPTYPE_STANDARD = 0;
	/** The Constant WEBMODEL_GROUPTYPE_ASSESSMENT. */
	public static final int WEBMODEL_GROUPTYPE_ASSESSMENT = 1;


	/** Get the list of localized strings of group types for web models.
	 * @return the list */
	public String[] getWebModelGroupTypes() {
		return new String[]{STGraphC.getMessage("WEBMODEL.GROUPTYPE.STANDARD"), //$NON-NLS-1$
				STGraphC.getMessage("WEBMODEL.GROUPTYPE.ASSESSMENT")}; //$NON-NLS-1$
	}
	
	
	public int getWebModelGroupType(final String type) {
		if(type.equals(STGraphC.getMessage("WEBMODEL.GROUPTYPE.STANDARD"))) { return WEBMODEL_GROUPTYPE_STANDARD; } //$NON-NLS-1$
		if(type.equals(STGraphC.getMessage("WEBMODEL.GROUPTYPE.ASSESSMENT"))) { return WEBMODEL_GROUPTYPE_ASSESSMENT; } //$NON-NLS-1$
		return 0;
	}


	/** Generate the array dealing with group data of the web model.
	 * <br>The source string is assumed to have the following format:
	 * <br><code>type1__title1__desc1__image1__help1|type2__title2__desc2__image2__help2|...</code>.
	 * <br>Also adapt the data if required. 
	 * @param s the string from which the array is to be generated
	 * @return the group data */
	private String[][] genGroupDataFromString(final String s) {
		if(STTools.isEmpty(s)) { return null; }
		String[] data = s.split("\\|"); //$NON-NLS-1$
		int m = data.length;
		if(m == 0) { return null; } // wrong formatting: it should not happen...
		String[][] wmgd = new String[data.length][5];
		for(int i = 0; i < m; i++) {
			String[] groupData = data[i].split("__"); //$NON-NLS-1$
			int n = groupData.length;
			if(n == 2) { // first release; it was: title, description
				wmgd[i][0] = "0"; // default type //$NON-NLS-1$
				wmgd[i][1] = groupData[0].trim(); // title
				wmgd[i][2] = groupData[1].trim(); // description
				wmgd[i][3] = STTools.BLANK; // icon
				wmgd[i][4] = STTools.BLANK; // help
			} else if(n == 3) { // third release; it was: title, description, icon
				wmgd[i][0] = "0"; // default type //$NON-NLS-1$
				wmgd[i][1] = groupData[0]; // title
				wmgd[i][2] = groupData[1]; // description
				wmgd[i][3] = groupData[2]; // icon
				wmgd[i][4] = STTools.BLANK; // help
			} else if(n == 4) { // fourth release; it was: title, description, icon, type
				wmgd[i][0] = groupData[3]; // default type
				wmgd[i][1] = groupData[0]; // title
				wmgd[i][2] = groupData[1]; // description
				wmgd[i][3] = groupData[2]; // icon
				wmgd[i][4] = STTools.BLANK; // help
			} else {
				for(int j = 0; j < groupData.length; j++) { wmgd[i][j] = groupData[j].trim(); }
			}
		}
		return wmgd;
	}


	/** Generate a string with the data from the two maps dealing with group data of the web model.
	 * <br>The target string is written with the following format:
	 * <br><code>type1__title1__desc1__image1__help1|type2__title2__desc2__image2__help2|...</code>.
	 * @param lang the language
	 * @return the string */
	private String genStringFromGroupData(final String lang) {
		if(webModelGroupData == null) { return STTools.BLANK; } 
		int size = webModelGroupData.size();
		if(size == 0) { return STTools.BLANK; }
		String[][] wmgd = webModelGroupData.get(lang);
		if(wmgd == null || wmgd.length == 0) { return STTools.BLANK; }
		size = wmgd.length;
		StringBuffer s = new StringBuffer();
		String x0;
		String x1;
		String x2;
		String x3;
		String x4;
		for(int i = 0; i < size; i++) {
			x0 = wmgd[i][0].trim(); if(STTools.isEmpty(x0)) { x0 = STTools.SPACE; }
			x1 = wmgd[i][1].trim(); if(STTools.isEmpty(x1)) { x1 = STTools.SPACE; }
			x2 = wmgd[i][2].trim(); if(STTools.isEmpty(x2)) { x2 = STTools.SPACE; }
			x3 = wmgd[i][3].trim(); if(STTools.isEmpty(x3)) { x3 = STTools.SPACE; }
			x4 = wmgd[i][4].trim(); if(STTools.isEmpty(x4)) { x4 = STTools.SPACE; }
			s.append(x0 + "__" + x1 + "__" + x2 + "__" + x3 + "__" + x4); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			if(i < size - 1) { s.append("|"); } //$NON-NLS-1$
		}
		return s.toString();
	}


	/** Handle the named resource, by adding to the map of graph resources and
	 * initializing / opening if required, and then returning it.
	 * @param name the name
	 * @param type the type
	 * @param autoRefresh is the resource to be autorefreshed?
	 * @return the resource */
	public final Object handleResource(final String name, final int type, final boolean autoRefresh) {
		//TODO: add images in texts and charts (and submodels?)
		//TODO: freed resources must be removed from the map
		Object o = resourceMap.get(name);
		if(o == null) {
			if(type == RES_TYPE_WORKBOOK) {
				try {
					File file = new File(name);
					WorkbookSettings wbs = new WorkbookSettings();
					wbs.setGCDisabled(true);
					resourceMap.put(name, o = Workbook.getWorkbook(file, wbs));
					resourceDataMap.put(name, Long.valueOf(file.lastModified()));
				} catch (Exception e) { return null; }
			} else if(type == RES_TYPE_NODEICON) {
				resourceMap.put(name, o = new ImageIcon(name));
			}
		} else {
			if(autoRefresh) {
				if(type == RES_TYPE_WORKBOOK) {
					try {
						File file = new File(name);
						Long lm = Long.valueOf(file.lastModified());
						if(!resourceDataMap.get(name).equals(lm)) {
							WorkbookSettings wbs = new WorkbookSettings();
							wbs.setGCDisabled(true);
							resourceMap.put(name, o = Workbook.getWorkbook(file, wbs));
							resourceDataMap.put(name, lm);
						}
					} catch (Exception e) { return null; }
				}
			}
		}
		return o;
	}

	/** Set whether the model is aimed at being executed as connected to a SpaceBrew server.
	 * @param forNet the forNet */
	public void setForNet(final boolean forNet) { this.forNet = forNet; }


	/** Get whether the model is aimed at being executed as connected to a SpaceBrew server.
	 * @return the isForNet */
	public boolean isForNet() { return forNet; }


	/** Set the address of the SpaceBrew server to connect to.
	 * @param serverAddress the serverAddress */
	public void setServerAddress(final String serverAddress) { this.serverAddress = serverAddress; }


	/** Get the address of the specified SpaceBrew server.
	 * @return the serverAddress */
	public String getServerAddress() { return serverAddress; }


	/** Set the connector to the specified SpaceBrew server.
	 * @param serverConnector the serverConnector */
	public void setConnector(final Spacebrew serverConnector) { this.serverConnector = serverConnector; }


	/** Get the connector to the specified SpaceBrew server.
	 * @return the serverConnector */
	public Spacebrew getConnector() { return serverConnector; }


	/** Get whether the model is connected to the specified SpaceBrew server.
	 * @return the isConnectedToNet */
	public boolean isConnectedToNet() { return serverConnector != null && serverConnector.connected(); }


	/** Connect to the specified SpaceBrew server. */
	public void connectToNet() {
		serverConnector = new Spacebrew(this, false);
		////connector.addPublish("sysTime", NET_DATATYPE, STTools.BLANK); //$NON-NLS-1$
		STNode[] nodes = getNodesWithExprInDef("writeToNet"); // expose publishers, i.e., nodes containing the writeToNet() function //$NON-NLS-1$
		if(nodes != null && nodes.length > 0) {
			for(STNode node : nodes) { serverConnector.addPublish(node.getName(), NET_DATATYPE, STTools.BLANK); }
		}
		nodes = getNodesWithExprInDef("readFromNet"); // expose subscribers, i.e., nodes containing the readFromNet() function //$NON-NLS-1$
		if(nodes != null && nodes.length > 0) {
			for(STNode node : nodes) { serverConnector.addSubscribe(node.getName(), NET_DATATYPE); }
		}
		serverConnector.connect(getServerAddress(), getTitle(), STTools.BLANK);
		System.out.println("Connecting to SB server..."); //$NON-NLS-1$
	}


	/** Disconnect from the specified SpaceBrew server. */
	public void disconnectFromNet() {
		try {
			serverConnector.close();
		} catch (Exception e) { ; }
		serverConnector = null;
		System.out.println("Disconnecting from SB server..."); //$NON-NLS-1$
	}


	/** Handle the event of a message to be received by a subscriber,
	 * operating as a proxy for the readFromNet() function in the specified node.
	 * @param name the name of the subscriber node
	 * @param type the value type, currently unused and assumed to be equal to NET_DATATYPE
	 * @param value the received value 
	 */
	public void onCustomMessage(String name, String type, String value) {
		STNode node = getNodeByName(name);
		if(node != null) {
			try {
				node.setValueFromNet(new Tensor(value));
			} catch (Exception e) {
				node.setValueFromNet(Tensor.newScalar(0.0));
			}
		}
	}


	/** Wrapper class. */
	private class STXMLElement {
		/** The element. */
		private XMLElement e;


		/** Class constructor.
		 * @param x the element */
		STXMLElement(final XMLElement x) { e = x; }


		/** Get the attribute as a string.
		 * @param x the attribute name
		 * @return the value */
		String getStringAttribute(final String x) { return e.getAttribute(x, null); }


		/** Get the attribute as a double.
		 * @param x the attribute name
		 * @return the value */
		double getDoubleAttribute(final String x) {
			String s = e.getAttribute(x, null);
			return s != null ? Double.parseDouble(s) : 0.0;
		}


		/** Get the attribute as an int.
		 * @param x the attribute name
		 * @return the value */
		int getIntAttribute(final String x) {
			String s = e.getAttribute(x, null);
			return s != null ? Integer.parseInt(s) : 0;
		}


		/** Get the number of children.
		 * @return the number */
		int countChildren() { return e.getChildrenCount(); }


		/** Get the children.
		 * @return the children */
		@SuppressWarnings("rawtypes")
		Vector getChildren() { return e.getChildren(); }


		/** Get the string content of this XML node.
		 * @return the string */
		public String toString() { return e.getName() + STTools.SPACE + e.getAttributes().toString(); }

	}

}
