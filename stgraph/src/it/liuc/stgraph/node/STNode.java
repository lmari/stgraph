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

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.fun.STFunction;
import it.liuc.stgraph.util.ParserCheckVisitor;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.widget.InputWidget;
import it.liuc.stgraph.widget.STWidget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.nfunk.jep.Node;
import org.nfunk.jep.Parser;
import org.nfunk.jep.type.Tensor;


/** Abstract superclass of the nodes of which a graph is constituted.
 * <br>The direct concrete subclasses are:
 * <ul>
 * <li><code>ValueNode</code>: each value node has a type, distinguishing whether it is
 * 		algebraic (0), state (1), or state with distinct output (2);
 *		value nodes can be input or output nodes;
 *		<br>the node dialog is handled by the class <code>ValueNodeDialog</code>
 *		and the node view by the class <code>ValueNodeView</code>;
 * <li><code>ModelNode</code>: each model node represents a submodel, defined either directly by the user
 *		or as a custom model; in this second case, the node can be either:
 *		<ul>
 *		<li>a <code>CustomModelNode</code>, whose model is taken from the user library
 *				and whose dialog is handled by the class <code>CustomModelNodeDialog</code>, or
 *		<li>a <code>CustomValueNode</code>, whose model is defined by properly subclassing <code>CustomValueNode</code> itself
 *				and <code>NodeDialog</code>.
 * 		</ul>
 * 		Both share the same <code>CustomNodeView</code> class.
 * </ul> */
@SuppressWarnings("serial")
public abstract class STNode extends DefaultGraphCell {
	//*** CONSTANTS ***
	/** The Constant NODE_VALUE: value node nodeType. */
	public static final String NODE_VALUE = "ValueNode"; //$NON-NLS-1$
	/** The Constant NODE_MODEL: submodel node nodeType. */
	public static final String NODE_MODEL = "ModelNode"; //$NON-NLS-1$
	/** The Constant PROP_VALUETYPE: type of a value node. */
	public static final String PROP_VALUETYPE = "valueType"; //$NON-NLS-1$
	/** The Constant PROP_ISINPUT: switch to specify whether this node is input. */
	public static final String PROP_ISINPUT = "isIn"; //$NON-NLS-1$
	/** The Constant PROP_ISGLOBAL: switch to specify whether this node is global. */
	public static final String PROP_ISGLOBAL = "isGlobal"; //$NON-NLS-1$
	/** The Constant PROP_ISOUTPUT: switch to specify whether this node is output. */
	public static final String PROP_ISOUTPUT = "isOut"; //$NON-NLS-1$
	/** The Constant PROP_ISVECTOROUTPUT: switch to specify whether this node is vector output. */
	public static final String PROP_ISVECTOROUTPUT = "isVectorOut"; //$NON-NLS-1$
	/** The Constant PROP_EXPRESSION: expression of this node. */
	public static final String PROP_EXPRESSION = "expression"; //$NON-NLS-1$
	/** The Constant PROP_EXPRESSION_F: formatted expression of this node. */
	public static final String PROP_EXPRESSION_F = "fExpression"; //$NON-NLS-1$
	/** The Constant PROP_BATCHOUTPUT: vector of values, for each batch, of this node. */
	public static final String PROP_BATCHOUTPUT = "batchValueList"; //$NON-NLS-1$
	/** The Constant PROP_INITSTATE: initial state expression. */
	public static final String PROP_INITSTATE = "stateInit"; //$NON-NLS-1$
	/** The Constant PROP_INITSTATE_F: formatted initial state expression. */
	public static final String PROP_INITSTATE_F = "fStateInit"; //$NON-NLS-1$
	/** The Constant PROP_STATETRANSITION: transition state expression. */
	public static final String PROP_STATETRANSITION = "stateTrans"; //$NON-NLS-1$
	/** The Constant PROP_STATETRANSITION_F: formatted transition state expression. */
	public static final String PROP_STATETRANSITION_F = "fStateTrans"; //$NON-NLS-1$
	/** The Constant PROP_SUBMODELNAME: filename of submodel. */
	public static final String PROP_SUBMODELNAME = "systemName"; //$NON-NLS-1$
	/** The Constant PROP_SUPERVARNAMES: array of the names of nodes saturating the submodel inputs. */
	public static final String PROP_SUPERVARNAMES = "superVarNames"; //$NON-NLS-1$
	/** The Constant PROP_SUPEREXPRESSION: expressions saturating the submodel inputs. */
	public static final String PROP_SUPEREXPRESSION = "superExpression"; //$NON-NLS-1$
	/** The Constant PROP_SUBVARNAMES: array of the names of input nodes of the submodel. */
	public static final String PROP_SUBVARNAMES = "subVarNames"; //$NON-NLS-1$
	/** The Constant PROP_SUBMODELVISIBLE: visibility status of submodel. */
	public static final String PROP_SUBMODELVISIBLE = "subvisible"; //$NON-NLS-1$

	//*** GENERAL PROPERTIES ***
	/** The graph. */
	private transient STGraphImpl graph;
	/** The node view. */
	protected transient NodeView view = null;
	/** The node nodeType. */
	private String nodeType;
	/** The input. */
	private boolean input;
	/** The output. */
	private boolean output; 
	/** The vector output. */
	private boolean vectorOutput;
	/** The global. */
	private boolean global;
	/** The node nodeSubtype. */
	private String nodeSubtype;
	/** Is this node expression a constant value?. */
	private boolean constant = false;
	/** Is this (state) node init state non dependent on values of other nodes?. */
	private boolean nondependentInitState = false;
	/** The valid. */
	private boolean valid;

	//*** PROPERTIES RELATED TO THE EXECUTION SETUP ***
	/** The list of nodes connected by outcoming edges. */
	private transient ArrayList<STNode> connectedTo = null;
	/** The list of nodes connected by expression(s). */
	private transient ArrayList<STNode> connectedFrom = null;
	/** The list of nodes connected by incoming edges. */
	private transient ArrayList<STNode> connectedFrom2 = null;

	/** The list of nodes defining the expression of this node. */
	public transient ArrayList<STNode> definedByExpr = null;
	/** The list of nodes defining the init state of this node. */
	public transient ArrayList<STNode> definedByInit = null;
	/** The list of nodes defining the state transition of this node. */
	public transient ArrayList<STNode> definedByTrans = null;

	/** The execution priority. */
	private int executionPriority = -1;
	/** The from state. */
	public boolean fromState;
	/** The node storing the parsed tree for value expression. */
	public transient ArrayList<Node> topOfTreeValue;
	/** The node storing the parsed tree for next state transition. */
	public transient ArrayList<Node> topOfTreeState;
	/** Expression of a supermodel which saturates this node. */
	private String saturatingExpression;
	/** Input widget controlling this node. */
	private transient Object inputWidget;

	//*** PROPERTIES RELATED TO THE LAYOUT ***
	/** The description. */
	private String description;
	/** The icon file. */
	private String iconFile;
	/** The default icon file (for subtypes). */
	private String defaultIconFile;
	/** The plain font. */
	private transient Font plainFont;
	/** The bold font. */
	private transient Font boldFont;
	/** The font color. */
	public transient Color fontColor;
	/** The foreground color. */
	public transient Color foreColor;
	/** The background color. */
	public transient Color backColor;
	/** The number format. */
	private transient String numberFormat;
	/** The custom properties. */
	private transient Map<String, String> cProperties;

	//*** PROPERTIES RELATED TO THE EXECUTION ***
	/** The current value. */
	private transient Object value;
	/** The vector of values, from time0 to current time. */
	private transient Object valueHistory;
	/** Is valueHistory a matrix?. */
	private boolean valueHistoryMatrix;
	/** The number of rows in valueHistory when interpreted as a matrix. */
	private int valueHistoryRows;
	/** The number of columns in valueHistory when interpreted as a matrix. */
	private int valueHistoryColumns;
	/** The data to save. */
	public transient AttributeMap dataToSave;
	/** The eval err description. */
	private String evalErrDescription;

	/** The node name for each non-default language. */
	private HashMap<String, String> webName = new HashMap<String, String>();
	/** The node unit for each non-default language. */
	private HashMap<String, String> webUnit = new HashMap<String, String>();
	/** The node description for each non-default language. */
	private HashMap<String, String> webDescription = new HashMap<String, String>();
	/** The node input texts for each non-default language. */
	private HashMap<String, String> webInputTexts = new HashMap<String, String>();
	/** The node output texts for each non-default language. */
	private HashMap<String, String> webOutputTexts = new HashMap<String, String>();


	/** Class constructor.
	 * @param userObject the user object */
	public STNode(final Object userObject) {
		super(userObject);
		graph = STGraph.getSTC().getCurrentGraph();
	}


	/** Set the model to which this node belongs.
	 * @param graph the graph */
	public final void setGraph(final STGraphImpl graph) { this.graph = graph; }


	/** Get the model to which this node belongs.
	 * @return graph */
	public final STGraphExec getGraph() { return (STGraphExec)graph; }


	/** Get the view for this node.
	 * @return view */
	public abstract NodeView getView();


	/** Get a new view for this node.
	 * @return view */
	public abstract NodeView getNewView();


	/** Get the dialog for this node.
	 * @return dialog */
	public abstract EditPanel4Nodes getDialog();


	/** Open the configuration dialog for this node.
	 * <br>This is controlled by the <code>ToolsEditProperties.exec()</code> method. */
	public void openDialog() { getDialog().open(this); }


	/** Set the named property for this node to the specified value.
	 * @param name the name
	 * @param _value the value */
	@SuppressWarnings("unchecked")
	public final void setProperty(final String name, final Object _value) {
		AttributeMap map = getAttributes();
		if(_value != null) { map.put(name, _value); }
		else { map.remove(name); }
		setAttributes(map);
	}


	/** Get the named property for this node.
	 * @param name the name
	 * @return property */
	public final Object getProperty(final String name) { return getAttributes().get(name); }


	/** Set the plain font for this node.
	 * <br>TODO Why is this required? (plausibly a JGraph bug?) (see also VertexRenderer)
	 * @param font the font */
	@SuppressWarnings("unchecked")
	public final void setPlainFont(final Font font) {
		//GraphConstants.setFont(getAttributes(), this.plainFont = font);
		if(font != null && view != null) {
			AttributeMap map = view.getAllAttributes();
			map.put("font", this.plainFont = font); //$NON-NLS-1$
			view.changeAttributes(graph.getGraphLayoutCache(), map);
		}
	}


	/** Get the plain font for this node.
	 * @return font */
	public final Font getPlainFont() { return (plainFont != null) ? plainFont : GraphConstants.getFont(getAttributes()); }


	/** Set the bold font for this node.
	 * <br>TODO Why is this required? (plausibly a JGraph bug?) (see also VertexRenderer)
	 * @param font the font */
	@SuppressWarnings("unchecked")
	public final void setBoldFont(final Font font) {
		//GraphConstants.setFont(getAttributes(), this.boldFont = font);
		if(font != null && view != null) {
			AttributeMap map = view.getAllAttributes();
			map.put("font", this.boldFont = font); //$NON-NLS-1$
			view.changeAttributes(graph.getGraphLayoutCache(), map);
		}
	}


	/** Get the bold font for this node.
	 * @return font */
	public final Font getBoldFont() { return (boldFont != null) ? boldFont : GraphConstants.getFont(getAttributes()); }


	/** Get the font size for this node.
	 * @return size */
	//public final int getFontSize() { return (Integer) ((plainFont != null) ? plainFont.getSize() : ((Font)GraphConstants.getFont(getAttributes()).getSize()); }
	public final int getFontSize() { return (plainFont != null) ? plainFont.getSize() : GraphConstants.getFont(getAttributes()).getSize(); }


	/** Set the font color for this node.
	 * <br>TODO Why is this required? (plausibly a JGraph bug?) (see also VertexRenderer)
	 * @param color the color */
	@SuppressWarnings("unchecked")
	public final void setFontColor(final Color color) { 
		//GraphConstants.setForeground(getAttributes(), fontColor = color);
		if(color != null && view != null) {
			fontColor = color;
			AttributeMap map = view.getAllAttributes();
			map.put("foregroundColor", color); //$NON-NLS-1$
			view.changeAttributes(graph.getGraphLayoutCache(), map);
		}
	}


	/** Get the font color for this node.
	 * @return color */
	public final Color getFontColor() { return (fontColor != null) ? fontColor : GraphConstants.getForeground(getAttributes()); }


	/** Set the foreground color for this node.
	 * @param color the color */
	public final void setForeColor(final Color color) { GraphConstants.setBorderColor(getAttributes(), foreColor = color); }


	/** Get the foreground color for this node.
	 * @return color */
	public final Color getForeColor() { return (foreColor != null) ? foreColor : GraphConstants.getBorderColor(getAttributes()); }


	/** Set the background color for this node.
	 * @param color the color */
	public final void setBackColor(final Color color) { GraphConstants.setBackground(getAttributes(), backColor = color); }


	/** Get the background color for this node.
	 * @return color */
	public final Color getBackColor() { return (backColor != null) ? backColor : GraphConstants.getBackground(getAttributes()); }


	/** Set the number format for this node.
	 * @param format the format */
	public final void setNumberFormat(final String format) { this.numberFormat = format; }


	/** Get the number format for this node.
	 * @return format */
	public final String getNumberFormat() { return numberFormat; }


	/** Set the custom properties.
	 * @param cProperties the cProperties */
	public final void setCProperties(final Map<String, String> cProperties) { this.cProperties = cProperties; }


	/** Get the custom properties.
	 * @return cProperties */
	public final Map<String, String> getCProperties() { return cProperties; }


	/** Append to the custom properties.
	 * @param cProperties the cProperties to be appended */
	public final void appendCProperties(final Map<String, String> cProperties) {
		if(this.cProperties == null) { this.cProperties = new HashMap<String, String>(); }
		this.cProperties.putAll(cProperties);
	}


	/** Set the named property for this node in the custom map.
	 * @param name the name
	 * @param value the value */
	public final void setCProperty(final String name, final String value) {
		if(cProperties == null) { cProperties = new HashMap<String, String>(); }
		cProperties.put(name, value);
	}


	/** Get the named property for this node in the custom map.
	 * The related application protocol is based, among the others, on the following keys:
	 * <li><code>Name</code> = descriptive name of the node, displayed alternatively to the variable name
	 * <li><code>Min</code> = minimum allowed value
	 * <li><code>Max</code> = maximum allowed value
	 * <li><code>OnBelowMin</code> = action (documented in the <code>ConsistencyCondition</code> class triggered if the current value is less than the custom property <code>Min</code>
	 * <li><code>OnAboveMax</code> = action (documented in the <code>ConsistencyCondition</code> class triggered if the current value is greater than the custom property <code>Max</code>
	 * <li><code>Unit</code> = Evaluation / measurement unit
	 * <li><code>DefaultValue</code> = default value
	 * <li><code>Decimals</code> = number of decimals digits to be displayed
	 * @param name the name
	 * @return property */
	public final String getCProperty(final String name) {
		if(cProperties != null) { return cProperties.get(name); }
		return null;
	}


	/** Get the array of the keys of the properties for this node in the custom map.
	 * @return keys */
	public final String[] getCPropertyKeys() {
		if(cProperties == null) { return null; }
		return cProperties.keySet().toArray(new String[cProperties.size()]);
	}


	/** Set the name of this node.
	 * @param name the name */
	public final void setName(final String name) { setUserObject(name); }


	/** Get the name of this node.
	 * @return name */
	public final String getName() { return (String)getUserObject(); }


	/** Get the full name of this node, with the OO syntax <code>super.sub.node</code> in the case of nested nodes.
	 * @return name */
	public final String getFullName() {
		String result = (String)getUserObject();
		ModelNode n = getGraph().getSuperNode();
		while(n != null) {
			result = n.getName() + STTools.DOT + result;
			n = n.getGraph().getSuperNode();
		}
		return result;
	}


	/** Get the name of this node, with the OO syntax <code>sub.node</code> in the case of nested nodes.
	 * @return name */
	public final String getSuperName() {
		String result = (String)getUserObject();
		ModelNode n = getGraph().getSuperNode();
		if(n != null) { result = n.getName() + STTools.DOT + result; }
		return result;
	}


	/** Set the name of this node, as specified in the custom map.
	 * @param n the name */
	public final void setCName(final String n) { setCProperty("Name", n); } //$NON-NLS-1$


	/** Set the name of this node in the specified language, as specified in the custom map.
	 * @param n the name
	 * @param lang the language */
	public final void setCName(final String n, final String lang) {
		if(!graph.isForWeb() || lang.equals(graph.getDefaultModelLanguage())) {
			setCProperty("Name", n); //$NON-NLS-1$
		} else {
			webName.put(lang, n);
		}
	}


	/** Get the name of this node, as specified in the custom map or, if null, as standard name.
	 * @return name */
	public final String getCName() {
		String n = getCProperty("Name"); //$NON-NLS-1$
		if(n != null) { return n; }
		return (String)getUserObject();
	}


	/** Get the name of this node in the specified language, as specified in the custom map
	 * or, if null or for a non-existing language, as for default language.
	 * @param lang the language
	 * @return name */
	public final String getCName(final String lang) {
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getCName(); }
		String n = webName.get(lang);
		if(n != null) { return n; }
		return getCName();
	}


	/** Get the name of this node in the specified language, as specified in the custom map.
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return name */
	public final String getCName(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getCName(lang); }
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getCName(); }
		String n = webName.get(lang);
		if(n == null) { return STTools.BLANK; }
		return n;
	}


	/** Set the unit of this node, as specified in the custom map.
	 * @param u the unit */
	public final void setUnit(final String u) { setCProperty("Unit", u); } //$NON-NLS-1$


	/** Set the unit of this node in the specified language, as specified in the custom map.
	 * @param u the unit
	 * @param lang the language */
	public final void setUnit(final String u, final String lang) {
		if(!graph.isForWeb() || lang.equals(graph.getDefaultModelLanguage())) {
			setCProperty("Unit", u); //$NON-NLS-1$
		} else {
			webUnit.put(lang, u);
		}
	}


	/** Get the unit of this node, as specified in the custom map.
	 * @return unit */
	public final String getUnit() { return getCProperty("Unit"); } //$NON-NLS-1$


	/** Get the unit of this node in the specified language, as specified in the custom map,
	 * or, if null or for a non-existing language, as for the default language.
	 * @param lang the language
	 * @return unit */
	public final String getUnit(final String lang) {
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getUnit(); }
		String n = webUnit.get(lang);
		if(n != null) { return n; }
		return getUnit();
	}


	/** Get the unit of this node in the specified language, as specified in the custom map.
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return unit */
	public final String getUnit(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getUnit(lang); }
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getUnit(); }
		String n = webUnit.get(lang);
		if(n == null) { return STTools.BLANK; }
		return n;
	}


	/** Set the description of this node.
	 * @param description the description */
	public final void setDescription(final String description) { this.description = description; }


	/** Set the description of this node in the specified language.
	 * @param _value the value
	 * @param lang the language */
	public final void setDescription(final String description, final String lang) {
		if(!graph.isForWeb() || lang.equals(graph.getDefaultModelLanguage())) {
			this.description = description;
		} else {
			webDescription.put(lang, description);
		}
	}


	/** Get the description of this node.
	 * @return description */
	public final String getDescription() { return description; }


	/** Get the description of this node in the specified language,
	 * or, if null or for a non-existing language, as for the default language.
	 * @param lang the language
	 * @return description */
	public final String getDescription(final String lang) {
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getDescription(); }
		String n = webDescription.get(lang);
		if(n != null) { return n; }
		return getDescription();
	}


	/** Get the description of this node in the specified language.
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return description */
	public final String getDescription(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getDescription(lang); }
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getDescription(); }
		String n = webDescription.get(lang);
		if(n == null) { return STTools.BLANK; }
		return n;
	}


	/** Set the input texts of this node, as specified in the custom map.
	 * @param t the texts */
	public final void setInputTexts(final String t) { setCProperty("InputText", t); } //$NON-NLS-1$


	/** Set the input texts of this node in the specified language, as specified in the custom map.
	 * @param t the texts
	 * @param lang the language */
	public final void setInputTexts(final String t, final String lang) {
		if(!graph.isForWeb() || lang.equals(graph.getDefaultModelLanguage())) {
			setCProperty("InputText", t); //$NON-NLS-1$
		} else {
			webInputTexts.put(lang, t);
		}
	}


	/** Get the input texts of this node, as specified in the custom map.
	 * @return input texts */
	public final String getInputTexts() { return getCProperty("InputText"); } //$NON-NLS-1$


	/** Get the input texts of this node in the specified language, as specified in the custom map,
	 * or, if null or for a non-existing language, as for the default language.
	 * @param lang the language
	 * @return input texts */
	public final String getInputTexts(final String lang) {
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getInputTexts(); }
		String n = webInputTexts.get(lang);
		if(n != null) { return n; }
		return getInputTexts();
	}


	/** Get the input texts of this node in the specified language, as specified in the custom map.
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return input texts */
	public final String getInputTexts(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getInputTexts(lang); }
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getInputTexts(); }
		String n = webInputTexts.get(lang);
		if(n == null) { return STTools.BLANK; }
		return n;
	}


	/** Get the information related to input text of this node (as specified in the custom property "InputText").
	 * <br>It is assumed that the custom property has a String value of the type val1#text1__val2#text2 ecc.
	 * where the values are distinct integer values. It is also assumed that no texts are repeated in the map,
	 * so that also texts can be used as search keys.
	 * @return map of input number, input text */
	public final HashMap<Integer, String> getInputTextCProperty() {
		String s = getInputTexts();
		if(s == null) { return null; }
		HashMap<Integer, String> result = new LinkedHashMap<Integer, String>();
		String[] s2 = s.split(STTools.UNDERSCORE + STTools.UNDERSCORE);
		for(int i = 0; i < s2.length; i++) {
			String[] s3 = s2[i].split(STTools.SHARP);
			try {
				result.put(Integer.valueOf(s3[0]), s3[1]);
			} catch (Exception e) {
				return null;
			}
		}
		return result;
	}


	/** Get the information related to input text of this node (as specified in the custom property "InputText").
	 * <br>It is assumed that the custom property has a String value of the type val1#text1__val2#text2 ecc.
	 * where the values are distinct integer values. It is also assumed that no texts are repeated in the map,
	 * so that also texts can be used as search keys.
	 * @param lang the language
	 * @return map of input number, input text */
	public final HashMap<Integer, String> getInputTextCProperty(final String lang) {
		String s = getInputTexts(lang);
		if(s == null) { return null; }
		HashMap<Integer, String> result = new LinkedHashMap<Integer, String>();
		String[] s2 = s.split(STTools.UNDERSCORE + STTools.UNDERSCORE);
		for(int i = 0; i < s2.length; i++) {
			String[] s3 = s2[i].split(STTools.SHARP);
			try {
				result.put(Integer.valueOf(s3[0]), s3[1]);
			} catch (Exception e) {
				return null;
			}
		}
		return result;
	}


	/** Set the output texts of this node, as specified in the custom map.
	 * @param t the texts */
	public final void setOutputTexts(final String t) { setCProperty("OutputText", t); } //$NON-NLS-1$


	/** Set the output texts of this node in the specified language, as specified in the custom map.
	 * @param t the texts
	 * @param lang the language */
	public final void setOutputTexts(final String t, final String lang) {
		if(!graph.isForWeb() || lang.equals(graph.getDefaultModelLanguage())) {
			setCProperty("OutputText", t); //$NON-NLS-1$
		} else {
			webOutputTexts.put(lang, t);
		}
	}


	/** Get the output texts of this node, as specified in the custom map.
	 * @return output texts */
	public final String getOutputTexts() { return getCProperty("OutputText"); } //$NON-NLS-1$


	/** Get the output texts of this node in the specified language, as specified in the custom map,
	 * or, if null or for a non-existing language, as for the default language.
	 * @param lang the language
	 * @return output texts */
	public final String getOutputTexts(final String lang) {
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getOutputTexts(); }
		String n = webOutputTexts.get(lang);
		if(n != null) { return n; }
		return getOutputTexts();
	}


	/** Get the output texts of this node in the specified language, as specified in the custom map.
	 * @param lang the language
	 * @param allowBlank is blank allowed
	 * @return output texts */
	public final String getOutputTexts(final String lang, final boolean allowBlank) {
		if(!allowBlank) { return getOutputTexts(lang); }
		if(!graph.isForWeb() || lang == null || lang.equals(graph.getDefaultModelLanguage())) { return getOutputTexts(); }
		String n = webOutputTexts.get(lang);
		if(n == null) { return STTools.BLANK; }
		return n;
	}


	/** Get the information related to output text of this node (as specified in the custom property "OutputText").
	 * <br>It is assumed that the custom property has a String value of the type val1#text1__val2#text2 ecc.
	 * where the values are distinct integer values.
	 * @return map of output number, output text */
	public final HashMap<Integer, String> getOutputTextCProperty() {
		return _getOutputTextCProperty(getOutputTexts());
	}


	/** Get the information related to output text of this node (as specified in the custom property "OutputText").
	 * <br>It is assumed that the custom property has a String value of the type val1#text1__val2#text2 ecc.
	 * where the values are distinct integer values.
	 * @param lang the language
	 * @return map of output number, output text */
	public final HashMap<Integer, String> getOutputTextCProperty(final String lang) {
		return _getOutputTextCProperty(getOutputTexts(lang));
	}


	private final HashMap<Integer, String> _getOutputTextCProperty(final String s) {
		if(s == null) { return null; }
		HashMap<Integer, String> result = new LinkedHashMap<Integer, String>();
		String[] s2 = s.split(STTools.UNDERSCORE + STTools.UNDERSCORE);
		for(int i = 0; i < s2.length; i++) {
			String[] s3 = s2[i].split(STTools.SHARP);
			try {
				result.put(Integer.valueOf(s3[0]), s3[1]);
			} catch (Exception e) {
				return null;
			}
		}
		return result;
	}


	/** Get the text for the specified argument in the output text of this node (as specified in the custom property "OutputText").
	 * <br>See <code>getOutputTextCProperty()</code>.
	 * <br>The text may contain <code>{node}</code>, automatically substituted with the current value of <code>node</node>.
	 * @param n the id
	 * @return the output text corresponding to the specified argument */
	public final String getOutputText(final int n) {
		return _getOutputText(getOutputTextCProperty(), n);
	}


	/** Get the text for the specified argument in the output text of this node (as specified in the custom property "OutputText").
	 * <br>See <code>getOutputTextCProperty()</code>.
	 * <br>The text may contain <code>{node}</code>, automatically substituted with the current value of <code>node</node>.
	 * @param n the id
	 * @param lang the language
	 * @return the output text corresponding to the specified argument */
	public final String getOutputText(final int n, final String lang) {
		return _getOutputText(getOutputTextCProperty(lang), n);
	}


	private final String _getOutputText(HashMap<Integer, String> m, final int n) {
		if(m == null) { return STTools.BLANK; }
		String s = m.get(Integer.valueOf(n));
		if(s == null) { return STTools.BLANK; }
		int i;
		int j;
		STNode node;
		String res;
		while((i = s.indexOf(STTools.OPENC)) != -1) {
			j = s.indexOf(STTools.CLOSEC, i);
			if(j != -1) {
				node = graph.getNodeByName(s.substring(i + 1, j));
				if(node != null && node.isVariable()) {
					res = ((Tensor)node.getValue()).toString();
				} else {
					res = STTools.BLANK;
				}
				s = s.substring(0, i) + res + s.substring(j + 1);
			}
		}
		return s;
	}


	/** Get the information related to phase of this node (as specified in the custom property "Phase").
	 * <br>It is assumed that the custom property has a String value of the type 1-3,5 ecc.
	 * @return list of phases in which the node is considered "active" (e.g., visible) */
	public final Vector<Tensor> getPhaseCProperty() {
		String s = getCProperty("Phase"); //$NON-NLS-1$
		if(s == null) { return null; }
		Vector<Tensor> result = new Vector<Tensor>();
		String[] s2 = s.split(STTools.COMMA);
		for(int i = 0; i < s2.length; i++) {
			String[] s3 = s2[i].split(STTools.DASH);
			try {
				if(s3.length == 1) {
					result.add(Tensor.newScalar(Integer.parseInt(s3[0])));
				} else {
					for(int j = Integer.parseInt(s3[0]); j <= Integer.parseInt(s3[1]); j++) {
						result.add(Tensor.newScalar(j));
					}
				}
			} catch (Exception e) {
				return null;
			}
		}
		return result;
	}


	/** Get the information related to phase of this node (as specified in the custom property "Phase").
	 * <br>It is assumed that the custom property has a String value of the type 1-3,5 ecc.
	 * @return comma separated string with list of phases in which the node is considered "active" (e.g., visible) */
	public final String getPhaseCPropertyAsString() {
		Vector<Tensor> v = getPhaseCProperty();
		if(v == null) { return null; }
		StringBuilder s = new StringBuilder(""); //$NON-NLS-1$
		for(Tensor t : v) { s.append(t.getValue()).append(STTools.COMMA); }
		return s.substring(0, s.length() - 1);
	}


	/** Set the icon file of this node.
	 * @param file the file */
	public final void setIconFile(final String file) { iconFile = file; }


	/** Get the icon file of this node.
	 * @return file */
	public final String getIconFile() { return iconFile; }


	/** Set the default icon file of this node.
	 * @param file the file */
	public final void setDefaultIconFile(final String file) { defaultIconFile = file; }


	/** Get the default icon file of this node.
	 * @return file */
	public final String getDefaultIconFile() { return defaultIconFile; }


	/** Set the bounds of this node.
	 * @param bounds the bounds */
	@SuppressWarnings("unchecked")
	public final void setBounds(final Rectangle2D bounds) {
		GraphConstants.setBounds(getAttributes(), bounds);
		if(view != null) {
			Rectangle2D r = new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
			r.setRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
			AttributeMap map = view.getAllAttributes();
			map.put("bounds", r); //$NON-NLS-1$
			view.changeAttributes(graph.getGraphLayoutCache(), map);
			return;
		}
	}


	/** Get the bounds of this node.
	 * @return bounds */
	public final Rectangle2D getBounds() { return GraphConstants.getBounds(getAttributes()); }


	/** Set the position of this node.
	 * @param pos the pos */
	@SuppressWarnings("unchecked")
	public final void setPosition(final Point pos) {
		Rectangle2D r = GraphConstants.getBounds(getAttributes());
		r.setRect(pos.getX(), pos.getY(), r.getWidth(), r.getHeight());
		GraphConstants.setBounds(getAttributes(), r);
		if(view != null) {
			AttributeMap map = view.getAllAttributes();
			map.put("bounds", r); //$NON-NLS-1$
			view.changeAttributes(graph.getGraphLayoutCache(), map);
		}
	}


	/** Set the position of this node.
	 * @param posX the pos x
	 * @param posY the pos y */
	@SuppressWarnings("unchecked")
	public final void setPosition(final int posX, final int posY) {
		Rectangle2D r = GraphConstants.getBounds(getAttributes());
		r.setRect(posX, posY, r.getWidth(), r.getHeight());
		GraphConstants.setBounds(getAttributes(), r);
		if(view != null) {
			AttributeMap map = view.getAllAttributes();
			map.put("bounds", r); //$NON-NLS-1$
			view.changeAttributes(graph.getGraphLayoutCache(), map);
		}
	}


	/** Get the position of this node.
	 * @return position */
	public final Point getPosition() { 
		Rectangle2D r = GraphConstants.getBounds(getAttributes());
		return new Point((int)r.getX(), (int)r.getY());
	}


	/** Get whether the node is an algebraic node.
	 * @return result */
	public final boolean isAlgebraic() { return getNodeType().equals(STNode.NODE_VALUE) && ((ValueNode)this).getValueNodeType() == ValueNode.VALUENODE_ALGEBRAIC; }


	/** Get whether the node is a state or a state with out node.
	 * @return result */
	public final boolean isState() { return getNodeType().equals(STNode.NODE_VALUE) && ((ValueNode)this).getValueNodeType() != ValueNode.VALUENODE_ALGEBRAIC; }


	/** Get whether the node is a state with out node.
	 * @return result */
	public final boolean isStateWithOutput() { return getNodeType().equals(STNode.NODE_VALUE) && ((ValueNode)this).getValueNodeType() == ValueNode.VALUENODE_STATE_OUT; }


	/** Specify whether the node is a variable node.
	 * @return result */
	public final boolean isVariable() { return getNodeType().equals(STNode.NODE_VALUE); }


	/** Specify whether the node is a model node, containing a submodel.
	 * @return result */
	public final boolean isModel() { return getNodeType().equals(STNode.NODE_MODEL); }


	/** Specify whether the node is a sequential model node.
	 * @return result */
	public final boolean isSequentialModel() { return isModel() && ((ModelNode)this).getDesk() != null && ((ModelNode)this).getDesk().getGraph().isSequential; }


	/** Specify whether the node has a scalar value.
	 * @return result */
	public final boolean isScalar() { return value != null && value instanceof Tensor && ((Tensor)value).isScalar(); }


	/** Specify whether the node has a vector value.
	 * @return result */
	public final boolean isVector() { return value != null && value instanceof Tensor && ((Tensor)value).isVector(); }


	/** Specify whether the node has a matrix value.
	 * @return result */
	public final boolean isMatrix() { return value != null && value instanceof Tensor && ((Tensor)value).isMatrix(); }


	/** Specify whether the node has a non-scalar value.
	 * @return result */
	public final boolean isNotScalar() { return value != null && value instanceof Tensor && ((Tensor)value).isNotScalar(); }


	/** Get if this node is bound to an external control, from which it takes its value.
	 * @return result */
	public final boolean isBoundToExternalControl() {
		Object o = getInputWidget();
		return o != null && o instanceof String && ((String)o).equals("DIRECT"); //$NON-NLS-1$
	}


	/** Get if this node is bound to a widget, from which it takes its value.
	 * @return result */
	public final boolean isBoundToWidget() {
		Object o = getInputWidget();
		return o != null && o instanceof InputWidget;
	}


	private Timer timer = new Timer(50, new ActionListener() { public void actionPerformed(final ActionEvent e) { timeIt(); } });
	private int counter = 100;

	private void timeIt() {
		double s = graph.getScale();
		Rectangle2D r = graph.getCellBounds(this);
		int cX = (int)((r.getX() + r.getWidth() / 2) * s);
		int cY = (int)((r.getY() + r.getHeight() / 2) * s);
		Graphics g = graph.getGraphics(); 
		g.setColor(Color.RED);
		g.drawOval(cX - counter / 2, cY - counter / 2, counter, counter);
		counter -= 10;
		if(counter <= 10) {
			timer.stop();
			counter = 100;
		}
	}


	/** Select this node, by properly scrolling the graph if required. 
	 * @param highlight with highlight? */
	public final void select(final boolean highlight) {
		try {
			graph.setSelectionCell(this);
			graph.scrollCellToVisible(this);
			if(highlight) { timer.start(); }
		} catch (Exception e) { ; }
	}


	/** Reset the properties related to graph topology for this node. */
	public final void resetTopologicalProperties() {
		connectedTo = null;
		connectedFrom = null;
		connectedFrom2 = null;
		executionPriority = -1;
	}


	/** Reset the properties supporting graph computation for this node. */
	public final void resetComputationalProperties() {
		valueHistory = null; // remember: do not set the value property to null instead, because it could be pre-set in the case of STGraph.EXECMODE_GUIENGINE or STGraph.EXECMODE_NOGUIENGINE
	}


	/** Check whether the node name is correct.
	 * <br>Correct names are syntactically admissible, not duplicated
	 * and not coinciding with a reserved symbol.
	 * @param _name the old name
	 * @return result */
	public final String checkName(final String _name) {
		String error = STGraphC.getMessage("ERR.WRONG_NODE_NAME") + STTools.NEWLINE; //$NON-NLS-1$
		String name = _name.trim();
		if(name.length() == 0) { return error + STGraphC.getMessage("ERR.BLANK_NODE_NAME"); } //$NON-NLS-1$
		if(name.charAt(0) >= '0' && name.charAt(0) <= '9') { return error + STGraphC.getMessage("ERR.FORBIDDEN_NODE_NAME"); } //$NON-NLS-1$

		char[] fc = new char[] { ' ', '.', '?', '!', '%', '&', '+', '-', '*', '/', '#', ',', '(', ')', '[', ']', '{', '}', '\'', '"' };
		for(char c : fc) {
			if(name.indexOf(c) != -1) { return error + STGraphC.getMessage("ERR.FORBIDDEN_NODE_NAME"); } //$NON-NLS-1$
		}

		for(char c : name.toCharArray()) {
			if(c > 127) { return error + STGraphC.getMessage("ERR.FORBIDDEN_NODE_NAME"); } //$NON-NLS-1$
		}

		if(STInterpreter.getSystemVars(true, true).contains(name)) { return error + STGraphC.getMessage("ERR.RESERVED_NAME"); } //$NON-NLS-1$
		STInterpreter interpreter = graph.getInterpreter();
		STFunction[] functions = interpreter.getFunctions();
		for(int i = 0; i < functions.length; i++) {
			if(functions[i].getName().equals(name)) { return error + STGraphC.getMessage("ERR.RESERVED_NAME"); } //$NON-NLS-1$
		}
		for(STNode node : graph.getNodes()) {
			if(node.getName().equals(name) && node != this) { return error + STGraphC.getMessage("ERR.DUPLICATED_NODE_NAME"); } //$NON-NLS-1$
		}
		return null;
	}


	/** Redefine variables in case of the change of a node name.
	 * <br>If the new name is correct, remove the old name from the list
	 * of interpreter variables and add the new name to the same list.
	 * @param oldName the old name
	 * @param newName the new name */
	public final void redefineVars(final String oldName, final String newName) {
		STInterpreter interpreter = graph.getInterpreter();
		interpreter.removeVariable(oldName);
		interpreter.addVariable(newName, STTools.BLANK);
		interpreter.addVariable("__" + newName, STTools.BLANK); //$NON-NLS-1$ //TODO [batch exec]
		if(isModel() && ((ModelNode)this).getDesk() != null) {
			for(STNode n : ((ModelNode)this).getSubmodel().getOutputNodeList()) {
				interpreter.removeVariable(oldName + STTools.DOT + n.getName());
				interpreter.addVariable(newName + STTools.DOT + n.getName(), null);
			}
		}
	}


	/** Add the output variables of the specified submodel
	 * to the list of interpreter variables.
	 * @param submodel the submodel */
	public final void addVars(final STGraphImpl submodel) {
		STInterpreter interpreter = graph.getInterpreter();
		String s = getName();
		if(submodel != null) {
			for(STNode n : submodel.getOutputNodeList()) {
				interpreter.addVariable(s + STTools.DOT + n.getName(), null);
			}
		}
	}


	/** Remove the output variables of the specified submodel
	 * from the list of interpreter variables.
	 * @param submodel the submodel */
	public final void removeVars(final STGraphImpl submodel) {
		STInterpreter interpreter = graph.getInterpreter();
		String s = getName();
		if(submodel != null) {
			for(STNode n : submodel.getOutputNodeList()) {
				interpreter.removeVariable(s + STTools.DOT + n.getName());
			}
		}
	}


	/** Check node definition.
	 * @param interpreter the interpreter
	 * @return error */
	public abstract String checkDefinition(STInterpreter interpreter);


	/** Get the list of variables in the expression.
	 * @param interpreter the interpreter
	 * @param def expression to be checked
	 * @param userDefinedOnly user-defined only
	 * @return the list */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final ArrayList<String> getExpressionVars(final STInterpreter interpreter, final String def, final boolean userDefinedOnly) {
		ParserCheckVisitor pcv = STGraph.getSTC().getExpressionParser();
		interpreter.parseExpression(def);
		if(interpreter.getErrorInfo() != null) { return null; }
		pcv.setVars(new ArrayList()); // reset the list of variables
		try { interpreter.getTopNode().jjtAccept(pcv, null); // and fill it by traversing the expression tree
		} catch (Exception ex) { return null; }
		ArrayList<String> vars = pcv.getVars();
		if(vars == null) { return null; }
		ArrayList<String> ret = new ArrayList<String>();
		for(String var : vars) {
			if(STInterpreter.getSystemVars(true, true).contains(var)) {
				if(!userDefinedOnly) { ret.add(var); }
			} else {
				ret.add(var);
			}
		}
		return ret;
	}


	/** Get the list of variables in the expression.
	 * @param interpreter the interpreter
	 * @param def expression to be checked
	 * @return the list */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final ArrayList<STNode> getExpressionVarNodes(final STInterpreter interpreter, final String def) {
		ParserCheckVisitor pcv = STGraph.getSTC().getExpressionParser();
		interpreter.parseExpression(def);
		if(interpreter.getErrorInfo() != null) { return null; }
		pcv.setVars(new ArrayList()); // reset the list of variables
		try { interpreter.getTopNode().jjtAccept(pcv, null); // and fill it by traversing the expression tree
		} catch (Exception ex) { return null; }
		ArrayList<String> vars = pcv.getVars();
		if(vars == null) { return null; }
		ArrayList<STNode> ret = new ArrayList<STNode>();
		int p;
		String s1;
		String s2;
		STNode n;
		for(String var : vars) {
			if(!STInterpreter.getSystemVars(true, true).contains(var)) {
				if((p = var.indexOf(STTools.DOT)) == -1) {
					ret.add(graph.getNodeByName(var));	
				} else {
					s1 = var.substring(0, p);
					s2 = var.substring(p + 1);
					n = graph.getNodeByName(s1);
					if(n != null) { ret.add(((ModelNode)n).getSubmodel().getNodeByName(s2)); }
				}
			}
		}
		return ret;
	}


	/** Check whether the specified expression defining the node behavior is correct.
	 * @param interpreter the interpreter
	 * @param def expression to be checked
	 * @param defType nodeType of the expression
	 * @return result <code>null</code> if the expression is correct, a string
	 * with a message error otherwise */
	public final String checkExpressionDefinition(final STInterpreter interpreter, final String def, final String defType) {
		STGraphC stc = STGraph.getSTC();
		String error = getName() + ": "; //$NON-NLS-1$
		interpreter.parseExpression(def);

		String errInfo = interpreter.getErrorInfo();
		if(errInfo != null) {
			int nPos = errInfo.indexOf(STTools.NEWLINE);
			if(nPos != -1) { errInfo = errInfo.substring(0, nPos); }
			return error + errInfo;
		}

		stc.getExpressionParser().setVars(new ArrayList<String>()); // reset the list of variables
		stc.getExpressionParser().setMsg(null); // reset the error message

		try { interpreter.getTopNode().jjtAccept(stc.getExpressionParser(), null); // and fill it by traversing the expression tree
		} catch (Exception ex) { 
			return error + stc.getExpressionParser().getMsg();
		}

		ArrayList<String> vars = stc.getExpressionParser().getVars();

		String msg = null;
		for(String var : vars) {
			if(var.equals(STTools.BLANK)) { // variable not found in the symbol table
				return error + STGraphC.getMessage("ERR.VAR_NOT_FOUND") + " (" + var + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if(STInterpreter.getSystemVars(false, true).contains(var)) { continue; } // ok: 'time', 'index', ...
			if((msg = checkVariable(var, defType)) != null) { return error + msg; }
		}
		constant = (vars.size() == 0) && defType.equals(STNode.PROP_EXPRESSION);
		if(defType.equals(STNode.PROP_INITSTATE)) {
			nondependentInitState = true;
			if(vars.size() > 0) {
				for(String var : vars) {
					if(!STInterpreter.getSystemVars(false, true).contains(var)) {
						nondependentInitState = false;
					}
				}
			}
		}
		return null;
	}


	/** Wrapper for the checkDefinition method, also showing a message error when required.
	 * @param def the def
	 * @param defType the def nodeType
	 * @return error */
	public final boolean checkDefinitionWithAdvice(final String def, final String defType) {
		String result = checkExpressionDefinition(graph.getInterpreter(), def, defType);
		if(result == null) { return true; }
		STTools.messenger(STTools.MESSAGETYPE_ERR, result);
		return false;
	}


	/** Stub for the checkVariable method.
	 * @param var the var
	 * @param defType the def nodeType
	 * @return error */
	public abstract String checkVariable(String var, String defType);


	/** Clean the expression.
	 * @param expr the expr
	 * @return string */
	public final String cleanExpression(final String expr) {
		String s;
		StringBuffer sb = new StringBuffer(STTools.BLANK);
		Parser parser = new Parser(new StringReader(expr));
		while(!(s = parser.getNextToken().image).equals(STTools.BLANK)) { sb.append(s); }
		return sb.toString();
	}


	/** Handle the variable renaming within an expression.
	 * @param expr the expr
	 * @param oldName the old name
	 * @param newName the new name
	 * @return string */
	public final String renameInExpression(final String expr, final String oldName, final String newName) {
		String s;
		String ss;
		String t = STTools.UNDERSCORE + STTools.UNDERSCORE; 
		int p;
		StringBuffer sb = new StringBuffer(STTools.BLANK);
		Parser parser = new Parser(new StringReader(expr));
		while(!(s = parser.getNextToken().image).equals(STTools.BLANK)) {
			if((p = s.indexOf(STTools.DOT)) == -1) {
				if(s.equals(oldName)) { sb.append(newName); }
				else if(s.equals(t + oldName)) { sb.append(t + newName); }
				else { sb.append(s); }
			} else {
				ss = s.substring(0, p);
				if(ss.equals(oldName)) { sb.append(newName + STTools.DOT + s.substring(p + 1)); }
				else { sb.append(s); }
			}
		}
		return sb.toString();
	}


	/** Handle the event of this node removal. */
	public final void handleNodeRemoval() {
		if(isModel() && ((ModelNode)this).getSubmodelName() != null) {
			STNode[] nodes = ((ModelNode)this).getSubmodel().getOutputNodeList();
			if(nodes != null) {
				for(STNode node : nodes) { graph.getInterpreter().removeVariable(getName() + STTools.DOT + node.getName()); }
			}
			STGraphC.getMultiDesktop().remove(((ModelNode)this).getDesk());
		}
		graph.getInterpreter().removeVariable(getName());
		for(STWidget widget : graph.getWidgets()) { widget.handleNodeRemoval(getName()); }
		graph.refreshGraph();
	}


	/** Handle the event of this node renaming.
	 * @param oldName the old name */
	public final void handleNodeRenaming(final String oldName) {
		String newName = getName();
		STNode[] nodes = graph.getNodes();
		if(nodes != null) {
			for(STNode node : nodes) { 
				if(node.isAlgebraic() || node.isState()) {
					((ValueNode)node).handleNodeRenaming(oldName, newName);
				} else if(node.isModel()) {
					((ModelNode)node).handleNodeRenaming(oldName, newName);
				}
			}
		}
		for(STWidget widget : graph.getWidgets()) { widget.handleNodeRenaming(oldName, newName); }
	}


	/** Handle the event of this node renaming, limited to pasted nodes.
	 * @param oldName the old name
	 * @param pastedNodes the pasted nodes */
	public final void handleNodeRenaming(final String oldName, final ArrayList<STNode> pastedNodes) {
		String newName = getName();
		for(STNode node : pastedNodes) { 
			if(node.isAlgebraic() || node.isState()) {
				((ValueNode)node).handleNodeRenaming(oldName, newName);
			} else if(node.isModel()) {
				((ModelNode)node).handleNodeRenaming(oldName, newName);
			}
		}
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void prepareForSave() {
		dataToSave = new AttributeMap();
		if(description != null) { dataToSave.put("documentation", description); }
		if(plainFont != null) { dataToSave.put("font", plainFont.getFamily() + "," + plainFont.getSize()); }
		if(fontColor != null) { dataToSave.put("fontcol", fontColor.getRed() + "," + fontColor.getGreen() + "," + fontColor.getBlue()); }
		if(foreColor != null) { dataToSave.put("forecol", foreColor.getRed() + "," + foreColor.getGreen() + "," + foreColor.getBlue()); }
		if(iconFile != null) { dataToSave.put("iconfile", iconFile); }
		if(backColor != null) { dataToSave.put("backcol", backColor.getRed() + "," + backColor.getGreen() + "," + backColor.getBlue()); }
		if(numberFormat != null) { dataToSave.put("format", numberFormat); }
		if(cProperties != null && !cProperties.isEmpty()) {
			StringBuilder b = new StringBuilder();
			Iterator it = cProperties.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				Object o = entry.getValue();
				if(!STTools.isEmpty(o)) { b.append(entry.getKey() + "=" + o + ";"); } //$NON-NLS-1$ //$NON-NLS-2$
			}
			dataToSave.put("customprops", b.substring(0, b.length() - 1));
		}
		if(graph.isForWeb()) {
			for(String lang : graph.getNonDefaultModelLanguages()) {
				if(webName != null && !STTools.isEmpty(webName.get(lang))) { dataToSave.put("name_" + lang, webName.get(lang)); }
				if(webUnit != null && !STTools.isEmpty(webUnit.get(lang))) { dataToSave.put("unit_" + lang, webUnit.get(lang)); }
				if(webDescription != null && !STTools.isEmpty(webDescription.get(lang))) { dataToSave.put("documentation_" + lang, webDescription.get(lang)); }
				if(webInputTexts != null && !STTools.isEmpty(webInputTexts.get(lang))) { dataToSave.put("inputTexts_" + lang, webInputTexts.get(lang)); }
				if(webOutputTexts != null && !STTools.isEmpty(webOutputTexts.get(lang))) { dataToSave.put("outputTexts_" + lang, webOutputTexts.get(lang)); }
			}
		}
	}


	/** Define the data of this node from the loaded attribute map. */
	@SuppressWarnings("unchecked")
	public void restoreAfterLoad() {
		String s;
		AttributeMap am = getAttributes();
		if((s = (String)dataToSave.get("font")) != null) {
			String[] c = s.split(",");
			am.put(GraphConstants.FONT, plainFont = new Font(c[0], Font.PLAIN, Integer.parseInt(c[1])));
			am.put(GraphConstants.FONT, boldFont = new Font(c[0], Font.BOLD, Integer.parseInt(c[1])));
		}
		if((s = (String)dataToSave.get("fontcol-r")) != null) { // for backward compatibility...
			am.put(GraphConstants.FOREGROUND, fontColor = new Color(Integer.parseInt(s), Integer.parseInt((String)dataToSave.get("fontcol-g")), Integer.parseInt((String)dataToSave.get("fontcol-b"))));
		} else if((s = (String)dataToSave.get("fontcol")) != null) {
			String[] c = s.split(",");
			am.put(GraphConstants.FOREGROUND, fontColor = new Color(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2])));
		}
		if((s = (String)dataToSave.get("forecol-r")) != null) { // for backward compatibility...
			am.put(GraphConstants.BORDERCOLOR, foreColor = new Color(Integer.parseInt(s), Integer.parseInt((String)dataToSave.get("forecol-g")), Integer.parseInt((String)dataToSave.get("forecol-b"))));
		} else if((s = (String)dataToSave.get("forecol")) != null) {
			String[] c = s.split(",");
			am.put(GraphConstants.BORDERCOLOR, foreColor = new Color(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2])));
		}
		if((s = (String)dataToSave.get("backcol-r")) != null) { // for backward compatibility...
			am.put(GraphConstants.BACKGROUND, backColor = new Color(Integer.parseInt(s), Integer.parseInt((String)dataToSave.get("backcol-g")), Integer.parseInt((String)dataToSave.get("backcol-b"))));
		} else if((s = (String)dataToSave.get("backcol")) != null) {
			String[] c = s.split(",");
			am.put(GraphConstants.BACKGROUND, backColor = new Color(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2])));
		}
		if((s = (String)dataToSave.get("format")) != null) { numberFormat = s; }
		description = (String)dataToSave.get("documentation");
		s = iconFile = (String)dataToSave.get("iconfile");
		if(s == null) { // no icon
			am.put(GraphConstants.ICON, new ImageIcon());
			am.put(GraphConstants.VERTICAL_TEXT_POSITION, Integer.valueOf(SwingConstants.CENTER));
			am.put(GraphConstants.VERTICAL_ALIGNMENT, Integer.valueOf(SwingConstants.CENTER));
		} else {
			if(s.equals(EditPanel4Nodes.DEFAULT_ITEM_FOR_LIBRARY_MODEL_ICON)) { // i.e., it is from the library
				String ss = getDefaultIconFile();
				if(ss.equals("customDefault.png")) { // i.e., it is a model without a specific icon //$NON-NLS-1$
					am.put(GraphConstants.ICON, new ImageIcon(getClass().getClassLoader().getResource("icons/base/" + ss))); //$NON-NLS-1$
				} else { // i.e., it is a model with a specific icon
					URL fileURL = null;
					try {
						fileURL = (new File(STGraphC.getBasicProps().getProperty("MODEL.DIR").substring(2) + "/" + ss)).toURI().toURL(); //$NON-NLS-1$ //$NON-NLS-2$
					} catch (Exception e) { 
						fileURL = getClass().getClassLoader().getResource("icons/base/customDefault.png"); //$NON-NLS-1$
					}
					am.put(GraphConstants.ICON, new ImageIcon(fileURL));
				}
			} else {
				String fileName = STGraph.getSTC().getCurrentGraph().contextName + (STGraphC.isApplet() ? "/" : System.getProperty("file.separator")) + s; //$NON-NLS-1$ //$NON-NLS-2$
				if((new File(fileName)).exists()) {
					am.put(GraphConstants.ICON, new ImageIcon(fileName));
				}
			}
			am.put(GraphConstants.VERTICAL_TEXT_POSITION, Integer.valueOf(SwingConstants.BOTTOM));
			am.put(GraphConstants.VERTICAL_ALIGNMENT, Integer.valueOf(SwingConstants.BOTTOM));
		}
		if((s = (String)dataToSave.get("customprops")) != null) {
			cProperties = new HashMap<String, String>();
			String[] v = s.split(";"); //$NON-NLS-1$
			for(int i = 0; i < v.length; i++) {
				int j = v[i].indexOf("=");
				if(j != -1) { cProperties.put(v[i].substring(0, j), v[i].substring(j + 1)); }
			}
		}
		if(graph.isForWeb()) {
			for(String lang : graph.getNonDefaultModelLanguages()) {
				webName.put(lang, (String)dataToSave.get("name_" + lang));
				webUnit.put(lang, (String)dataToSave.get("unit_" + lang));
				webDescription.put(lang, (String)dataToSave.get("documentation_" + lang));
				webInputTexts.put(lang, (String)dataToSave.get("inputTexts_" + lang));
				webOutputTexts.put(lang, (String)dataToSave.get("outputTexts_" + lang));
			}
		}
		setAttributes(am);
		setPlainFont(plainFont); //TODO  Why are these required? (plausibly a JGraph bug?) (see also VertexRenderer)
		setBoldFont(boldFont);
		setFontColor(fontColor);
	}


	/** Return an HTML-formatted string with a description of this node.
	 * @return string */
	public String getInfo() {
		StringBuffer z = new StringBuffer();
		try {
			String t = STTools.BLANK;
			if(this instanceof ValueNode) {
				int t2 = ((ValueNode)this).getValueNodeType();
				if(t2 == ValueNode.VALUENODE_ALGEBRAIC) {
					t = STGraphC.getMessage("NODE.TYPE.ALGEBRAIC");
				} else if(t2 == ValueNode.VALUENODE_STATE) {
					t = STGraphC.getMessage("NODE.TYPE.STATE");
				} else if(t2 == ValueNode.VALUENODE_STATE_OUT) {
					t = STGraphC.getMessage("NODE.TYPE.OUTSTATE");
				}
			} else if(this instanceof ModelNode) {
				t = STGraphC.getMessage("NODE.TYPE.MODEL");
			}
			z.append("<h2>" + t + ": " + getFullName() + "</h2>");
			/*
    		if(this instanceof ValueNode) {
        		z.append("Priority: " +  executionPriority + "<br>");
        		z.append("Connected by expression(s) from: ");
        		if(connectedFrom == null) {
        			z.append("-");
        		} else {
        			for(STNode n : connectedFrom) { z.append(n.getFullName() + " "); }
        		}
        		z.append("<br>");
        		z.append("Connected by arrows from: ");
        		if(connectedFrom2 == null) {
        			z.append("-");
        		} else {
        			ArrayList<String> s = new ArrayList<String>();
        			String ss;
        			int i;
        			boolean found;
        			for(STNode n : connectedFrom2) {
        				ss = n.getFullName();
        				if((i = ss.indexOf(STTools.DOT)) == -1) {
            				z.append(ss + " ");
        				} else {
        					ss = ss.substring(0, i);
        					found = false;
        					for(String s2 : s) {
        						if(s2.equals(ss)) { found = true; }
        					}
        					if(!found) {
        						s.add(ss);
        						z.append(ss + ".* ");
        					}
        				}
        			}
        		}
        		z.append("<br>");
        		z.append("Connected to: ");
        		if(connectedTo == null) {
        			z.append("-");
        		} else {
        			for(STNode n : connectedTo) { z.append(n.getFullName() + " "); }
        		}
    			z.append("<br>");
    		}
			 */
			if(!STTools.isEmpty(description)) { z.append("<i>" + description + "</i>"); }
			z.append("<hr>");
		} catch (Exception ex) { ; }
		return z.toString();
	}


	/** Set the nodeType of this node.
	 * @param type the type */
	public final void setNodeType(final String type) { this.nodeType = type; }


	/** Get the nodeType of this node.
	 * @return nodeType */
	public final String getNodeType() { return nodeType; }


	/** Set the nodeSubtype of this node.
	 * @param subtype the subtype */
	public final void setNodeSubtype(final String subtype) { this.nodeSubtype = subtype; }


	/** Get the nodeSubtype of this node.
	 * @return nodeSubtype */
	public final String getNodeSubtype() { return nodeSubtype; }


	/** Set the value of this node, i.e., operate as connector between parser objects
	 * and graph nodes.
	 * It is overloaded by input nodes to handle the case of nodes bound to input widgets.
	 * @param newValue the value to set */
	public void setValue(final Object newValue) {
		value = newValue; // transfer the value;
		if(isBoundToExternalControl()) { graph.getInterpreter().addVariable(getName(), value); }
	}


	/** Set the value.
	 * @param newValue the value to set
	 * @param i the index */
	public void setValue(final Object newValue, final int i) {
		if(value instanceof Tensor && ((Tensor)value).isVector()) {
			((Tensor)value).setScalar(newValue, i);
			if(isBoundToExternalControl()) { graph.getInterpreter().addVariable(getName(), value); }
		}
	}


	/** Get the value of this node.
	 * It is overloaded by input nodes to handle the case of nodes bound to input widgets.
	 * @return value */
	public Object getValue() { return value; }


	/** Set the value history of this node.
	 * @param valueHistory the value history */
	public final void setValueHistory(final Object valueHistory) { this.valueHistory = valueHistory; }


	/** Get the value history of this node.
	 * @return valueHistory */
	public final Object getValueHistory() { return valueHistory; }


	/** Set the value history rows of this node.
	 * @param rows the number of rows */
	public final void setValueHistoryRows(final int rows) { this.valueHistoryRows = rows; }


	/** Get the value history rows of this node.
	 * @return the number of rows */
	public final int getValueHistoryRows() { return valueHistoryRows; }


	/** Set the value history columns of this node.
	 * @param columns the number of columns */
	public final void setValueHistoryColumns(final int columns) { this.valueHistoryColumns = columns; }


	/** Get the value history columns of this node.
	 * @return the number of columns */
	public final int getValueHistoryColumns() { return valueHistoryColumns; }


	/** Set whether the value history of this node is a matrix.
	 * @param isMatrix is matrix? */
	public final void setValueHistoryMatrix(final boolean isMatrix) { this.valueHistoryMatrix = isMatrix; }


	/** Get whether the value history of this node is a matrix.
	 * @return is matrix? */
	public final boolean isValueHistoryMatrix() { return valueHistoryMatrix; }


	/** Get whether this node is defined by one or more nodes.
	 * @return result */
	public final boolean isDefinedByNodes() { return connectedFrom != null && connectedFrom.size() > 0; }


	/** Get whether this node is defined by one or more nodes of the same model.
	 * @return result */
	public final boolean isDefinedByLocalNodes() {
		if(connectedFrom == null || connectedFrom.size() == 0) { return false; }
		if(getGraph().isTopGraph()) { return true; }
		/*
		boolean belongsToSuper = false;
		for(STNode n : connectedFrom) {
			if(n.getGraph() != getGraph()) { belongsToSuper = true; } 
		}
		return !belongsToSuper;
		 */
		boolean belongsToSuper = true;
		for(STNode n : connectedFrom) {
			if(n.getGraph() == getGraph()) { belongsToSuper = false; } 
		}
		return !belongsToSuper;
	}


	/** Set the value of the field connectedTo, supposed to store the list of directly defined nodes,
	 * i.e., the list of nodes connected by outgoing edges.
	 * <br>This method does not actually connect the specified nodes, and therefore
	 * should be used with care.
	 * @param list the list of nodes */
	public final void setConnectedTo(final ArrayList<STNode> nodes) { connectedTo = nodes; }


	/** Get the list of directly defined nodes, i.e., the list of nodes connected by outgoing edges.
	 * @return list */
	public final ArrayList<STNode> getDefinedNodes() { return connectedTo; }


	/** Set the value of the field connectedFrom, supposed to store the list of directly defining nodes,
	 * as specified in the defining expression(s).
	 * <br>This method does not actually connect the specified nodes, and therefore
	 * should be used with care.
	 * @param list the list of nodes */
	public final void setConnectedFrom(final ArrayList<STNode> nodes) { connectedFrom = nodes; }


	/** Get the list of directly defining nodes as specified in the defining expression(s).
	 * @return list */
	public final ArrayList<STNode> getDefiningNodesByExpressions() { return connectedFrom; }


	/** Set the value of the field connectedFrom2, supposed to store the list of directly defining nodes,
	 * by incoming edges.
	 * <br>This method does not actually connect the specified nodes, and therefore
	 * should be used with care.
	 * @param list the list of nodes */
	public final void setConnectedFrom2(final ArrayList<STNode> nodes) { connectedFrom2 = nodes; }


	/** Get the list of directly defining nodes by incoming edges.
	 * @return list */
	public final ArrayList<STNode> getDefiningNodesByEdges() { return connectedFrom2; }


	/** Get whether this node has one or more incoming arrows.
	 * @return result */
	public final boolean hasIncomingArrows() { return connectedFrom2 != null && connectedFrom2.size() > 0; }


	/** Get whether this node has one or more incoming local arrows.
	 * @return result */
	public final boolean hasIncomingLocalArrows() {
		if(connectedFrom2 == null || connectedFrom2.size() == 0) { return false; }
		if(getGraph().isTopGraph()) { return true; }
		boolean belongsToSuper = true;
		for(STNode n : connectedFrom2) {
			if(n.getGraph() == getGraph()) { belongsToSuper = false; } 
		}
		return !belongsToSuper;
	}


	/** Get the list of the incoming edges of this node.
	 * @return edges */
	public final ArrayList<STEdge> getInEdges() {
		STEdge[] edges = getGraph().getEdges();
		if(edges == null || edges.length == 0) { return null; }
		ArrayList<STEdge> result = new ArrayList<STEdge>();
		for(STEdge edge : edges) {
			if(edge.getTargetNode() == this) { result.add(edge); }
		}
		return result;
	}


	/** Get the list of the outgoing edges of this node.
	 * @return edges */
	public final ArrayList<STEdge> getOutEdges() {
		STEdge[] edges = getGraph().getEdges();
		if(edges == null || edges.length == 0) { return null; }
		ArrayList<STEdge> result = new ArrayList<STEdge>();
		for(STEdge edge : edges) {
			if(edge.getSourceNode() == this) { result.add(edge); }
		}
		return result;
	}

	private static final Color HICOLOR1 = Color.RED;
	private static final Color HICOLOR2 = new Color(75, 160, 75);

	/** Highlight the edges about this node. */
	public final void highlightEdges() {
		STEdge edge;
		ArrayList<STNode> nodes = getDefiningNodesByEdges();
		if(nodes != null) {
			for(STNode node : nodes) {
				edge = STEdge.getEdge(node, this);
				if(edge != null && graph.getGraphLayoutCache().isVisible(node)) { edge.setColor(HICOLOR1); }
			}
		}
		nodes = getDefinedNodes();
		if(nodes != null) {
			for(STNode node : nodes) {
				edge = STEdge.getEdge(this, node);
				if(edge != null && graph.getGraphLayoutCache().isVisible(node)) { edge.setColor(HICOLOR2); }
			}
		}
	}


	/** Remove the highlight from the edges about this node. */
	public final void resetEdges() {
		STEdge edge;
		ArrayList<STNode> nodes = getDefiningNodesByEdges();
		if(nodes != null) {
			for(STNode node : nodes) {
				edge = STEdge.getEdge(node, this);
				if(edge != null && graph.getGraphLayoutCache().isVisible(node)) { edge.resetColor(); }
			}
		}
		nodes = getDefinedNodes();
		if(nodes != null) {
			for(STNode node : nodes) {
				edge = STEdge.getEdge(this, node);
				if(edge != null && graph.getGraphLayoutCache().isVisible(node)) { edge.resetColor(); }
			}
		}
	}


	/** Check and set whether this node is input. */
	public final void setInput() { setInput(isAlgebraic() && !isDefinedByLocalNodes() && !hasIncomingLocalArrows()); }


	/** Set whether this node is input.
	 * @param input the input */
	public final void setInput(final boolean input) { 
		this.input = input; }


	/** Check whether this node is input.
	 * @return is input */
	public final boolean isInput() { return input; }


	/** Set whether this node is set as output.
	 * @param output the output */
	public final void setOutput(final boolean output) { this.output = output; }


	/** Check whether this node is set as output.
	 * @return is output */
	public final boolean isOutput() { return output; }


	/** Set whether this node is set as vector output.
	 * @param vectorOutput the vectorOutput */
	public final void setVectorOutput(final boolean vectorOutput) { this.vectorOutput = vectorOutput; }


	/** Check whether this node is set as vector output.
	 * @return is vector output */
	public final boolean isVectorOutput() { return vectorOutput; }


	/** Set whether this node is set as global.
	 * @param global the global */
	public final void setGlobal(final boolean global) { this.global = global; }


	/** Check whether this node is set as global.
	 * @return is global */
	public final boolean isGlobal() { return global; }


	/** Get whether this node is constant.
	 * @return constant */
	public final boolean isConstant() { return constant; }


	/** Get whether this (state) node init state non dependent on values of other nodes.
	 * @return nondependentInitState */
	public final boolean isNondependentInitState() { return nondependentInitState; }


	/** Get whether this node is valid.
	 * @return valid */
	public final boolean isValid() { return valid; }


	/** Set whether this node is valid.
	 * @param valid the valid to set */
	public final void setValid(final boolean valid) { this.valid = valid; }


	/** Set the executionPriority of this node.
	 * @param executionPriority the executionPriority to set */
	public final void setExecutionPriority(final int executionPriority) { this.executionPriority = executionPriority; }


	/** Get the executionPriority of this node.
	 * @return the executionPriority */
	public final int getExecutionPriority() { return executionPriority; }


	/** Set the expression which saturates this node.
	 * @param saturatingExpression the saturatingExpression to set */
	public final void setSaturatingExpression(final String saturatingExpression) { this.saturatingExpression = saturatingExpression; }


	/** Get the expression which saturates this node.
	 * @return the saturatingExpression */
	public final String getSaturatingExpression() { return saturatingExpression; }


	/** Set the input widget controlling this node.
	 * @param inputWidget the inputWidget to set */
	public final void setInputWidget(final Object inputWidget) { this.inputWidget = inputWidget; }


	/** Get the input widget controlling this node.
	 * @return the inputWidget */
	public final Object getInputWidget() { return inputWidget; }


	/** Set the error description as obtained by evaluating this node.
	 * @param evalErrDescription the evalErrDescription to set */
	public final void setEvalErrDescription(final String evalErrDescription) { this.evalErrDescription = evalErrDescription; }


	/** Get the error description as obtained by evaluating this node.
	 * @return the evalErrDescription */
	public String getEvalErrDescription() { return evalErrDescription; }


	/** The value read from net in the case this node is a subscriber. */
	private Tensor valueFromNet = Tensor.newScalar(0.0);


	/** Set the value read from net in the case this node is a subscriber.
	 * @param valueFromNet the value to set */
	public void setValueFromNet(Tensor valueFromNet) { this.valueFromNet = valueFromNet; }


	/** Get the value read from net in the case this node is a subscriber.
	 * @return value */
	public Tensor getValueFromNet() { return valueFromNet; }

}
