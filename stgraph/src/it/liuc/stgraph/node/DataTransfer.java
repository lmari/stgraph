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

import java.util.Vector;

import org.nfunk.jep.type.Matrix;


import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.util.STTools;


/** Handle the node related data transfer operations. */
public class DataTransfer {
	/** The node. */
	private STNode node;
	/** The index. */
	private int index = -1;
	/** The last only. */
	private boolean lastOnly = false;
	/** The overloaded value. */
	private String overloadedValue = null;
	/** The Constant WEBMODE_OUT. */
	public static final int WEBMODE_OUT = 0;
	/** The Constant WEBMODE_IN. */
	public static final int WEBMODE_IN = 1;


	/** The class constructor.
	 * @param _node the _node
	 * @param _index the _index
	 * @param _lastOnly the _last only */
	public DataTransfer(final STNode _node, final int _index, final boolean _lastOnly) {
		node = _node;
		index = _index;
		lastOnly = _lastOnly;
	}


	/** The class constructor.
	 * @param _node the _node */
	public DataTransfer(final STNode _node) {
		node = _node;
	}


	/** The class constructor.
	 * @param _node the _node
	 * @param _index the _index */
	public DataTransfer(final STNode _node, final int _index) {
		node = _node;
		index = _index;
	}


	/** The class constructor.
	 * @param _node the _node
	 * @param _lastOnly the _last only */
	public DataTransfer(final STNode _node, final boolean _lastOnly) {
		node = _node;
		lastOnly = _lastOnly;
	}


	/** Generate an HTML-formatted string description of this node, typically for inclusion in a jsp.
	 * To have richer output, the values of some properties are optionally taken from the custom map,
	 * in the property <code>PROP_CUSTOMPROPS</code>.
	 * Furthermore, the property <code>PROP_DOC</code>, if set, is introduced as an online, variable-related, help.
	 * @param mode the mode
	 * @param nameLink the name link
	 * @param target the target
	 * @return result */
	@SuppressWarnings("rawtypes")
	public final String toWeb(final int mode, final String nameLink, final String target) {
		if(mode == WEBMODE_OUT && !node.isOutput()) { return STTools.BLANK; }
		if((mode == WEBMODE_IN) && !node.isInput()) { return STTools.BLANK; }
		final String sTR = "<tr>";
		final String eTR = "</tr>"; 
		final String sTH = "<th align='left'>"; 
		final String eTH = "</th>"; 
		final String sTD = "<td align='right'>"; 
		final String eTD = "</td>"; 
		final String sIN = "<input size='4'"; 

		StringBuilder s = new StringBuilder();
		s.append(sTR + sTH);
		if(nameLink != null) {
			s.append("<a href='" + nameLink + "'");
			if(target != null) { s.append(" target='" + target + "'"); }
			s.append(">");
		}
		s.append(node.getCName());
		if(nameLink != null) { s.append("</a>"); }
		StringBuilder s2 = new StringBuilder();
		String x = node.getCProperty("Unit"); 
		if(!STTools.isEmpty(x)) { s2.append(STGraphC.getMessage("NODE.CUSTOMPROP.UNIT") + ": " + x + "\\n"); }
		x = node.getCProperty("Min"); 
		if(!STTools.isEmpty(x)) { s2.append(STGraphC.getMessage("NODE.CUSTOMPROP.MIN") + ": " + x + "\\n"); }
		x = node.getCProperty("Max"); 
		if(!STTools.isEmpty(x)) { s2.append(STGraphC.getMessage("NODE.CUSTOMPROP.MAX") + ": " + x + "\\n"); }
		x = node.getDescription();
		if(!STTools.isEmpty(x)) { s2.append(STTools.adaptToAlert(x)); }
		if(s2.length() > 0) {
			s.append(" <a href='javascript:window.alert(\"" + s2 + "\");'><sup>[?]</sup></a>");
		}
		s.append(eTH);
		if(mode == WEBMODE_OUT) {
			if(node.isScalar() && lastOnly) {
				s.append(sTD + roundedValue(node.getValue()) + eTD + eTR);
			} else if(node.isScalar() && !lastOnly) {
				Vector v = (Vector)node.getValueHistory();
				for(int i = 0; i < v.size(); i++) { s.append(sTD + roundedValue(v.get(i)) + eTD); }
			} else if(node.isVector() && !node.isVectorOutput()) {
				Vector v = (Vector)node.getValue();
				if(index == -1) {
					for(int i = 0; i < v.size(); i++) { s.append(sTD + roundedValue(v.get(i)) + eTD); }
				} else {
					s.append(sTD + roundedValue(v.get(index)) + eTD);
				}
				s.append(eTR);
			} else if(node.isVector() && node.isVectorOutput()) {
				if(index == -1 && !lastOnly) { // the whole matrix
					Matrix m = (Matrix)node.getValueHistory();
					for(int i = 0; i < m.getRowCount(); i++) {
						s.append(sTR);
						for(int j = 0; j < m.getColumnCount(); j++) { s.append(sTD + roundedValue(m.get(i, j)) + eTD); }
						s.append(eTR);
					}
				} else if(index == -1 && lastOnly) { // the column of the last (current) values
					Vector v = (Vector)node.getValue();
					for(int i = 0; i < v.size(); i++) { s.append(sTD + roundedValue(v.get(i)) + eTD); }
					s.append(eTR);
				} else if(index != -1 && !lastOnly) { // a history row
					Matrix m = (Matrix)node.getValueHistory();
					Vector v = m.getRow(index);
					for(int i = 0; i < v.size(); i++) { s.append(sTD + roundedValue(v.get(i)) + eTD); }
					s.append(eTR);
				} else { // a single last (current) value
					Vector v = (Vector)node.getValue();
					s.append(sTD + roundedValue(v.get(index)) + eTD + eTR);
				}
			}
		} else if(mode == WEBMODE_IN) {
			if(node.isScalar()) {
				s.append(sTD + sIN + completeIN(node.getName()) + eTD + eTR);
			} else if(node.isVector()) {
				if(index == -1) {
					Vector v = (Vector)node.getValue();
					for(int i = 0; i < v.size(); i++) { s.append(sTD + sIN + completeIN(node.getName() + "$" + i) + eTD); } 
					s.append(eTR);
				} else {
					s.append(sTD + sIN + completeIN(node.getName() + "$" + index) + eTD + eTR); 
				}
			}
		}
		return s.toString();
	}


	/** Generate an HTML-formatted string description of this node, typically for inclusion in a jsp.
	 * @param mode the mode
	 * @return result */
	public final String toWeb(final int mode) { return toWeb(mode, null, null); }


	/** Generate an HTML-formatted string description of this node, typically for inclusion in a jsp.
	 * @param mode the mode
	 * @param nameLink the name link
	 * @return result */
	public final String toWeb(final int mode, final String nameLink) { return toWeb(mode, nameLink, null); }


	/** Helper method for the <code>toWeb()</code> method.
	 * @param v the v
	 * @return result */
	private String roundedValue(final Object v) {
		String d = node.getCProperty("Decimals");
		if(STTools.isEmpty(d)) { return v.toString(); }
		if(d.equals("0")) { return "" + ((Number)v).intValue(); }
		int scale;
		try {
			scale = Integer.parseInt(d);
		} catch (Exception e) {
			return v.toString();
		}
		return "" + STData.round(((Number)v).doubleValue(), scale);
	}


	/** Force a value to write in the input field.
	 * @param _overloadedValue the overloadedValue to set */
	public final void setOverloadedValue(final String _overloadedValue) { overloadedValue = _overloadedValue; }


	/** Helper method for the <code>toWeb()</code> method.
	 * @param name the name
	 * @return result */
	private String completeIN(final String name) {
		StringBuilder s = new StringBuilder(" name='" + name + "' id='" + name + "'");
		s.append(" value='");
		String value = overloadedValue;
		if(!STTools.isEmpty(value)) {
			s.append(value);
		} else {
			value = node.getCProperty("DefaultValue");
			s.append(!STTools.isEmpty(value) ? value : "0");
		}
		s.append("'");
		s.append(" onblur=\"var n = " + name + ".value; if(n.length == 0) { return; }");
		s.append(" n = parseFloat(n); if(isNaN(n)) { window.alert('" + STGraphC.getMessage("EXC.WEB.NON_NUMERIC") + "'); return; }");
		try {
			double min = Double.parseDouble(node.getCProperty("Min"));
			s.append(" if(n < " + min + ") { window.alert('" + STGraphC.getMessage("EXC.WEB.LESS_THAN_MIN") + ": " + min + "'); " + name + ".focus(); return; } ");
		} catch (Exception e) { ; }
		try {
			double max = Double.parseDouble(node.getCProperty("Max"));
			s.append(" if(n > " + max + ") { window.alert('" + STGraphC.getMessage("EXC.WEB.GREATER_THAN_MAX") + ": " + max + "'); " + name + ".focus(); return; }");
		} catch (Exception e) { ; }
		s.append("\">");
		return s.toString();
	}

}
