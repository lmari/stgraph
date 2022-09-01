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

import it.liuc.stgraph.STGraphC;

import java.util.ArrayList;

import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTStart;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.ParserVisitor;
import org.nfunk.jep.SimpleNode;


/**
 * ParserCheckVisitor.
 */
public class ParserCheckVisitor implements ParserVisitor {
	/** The vars. */
	private ArrayList<String> vars;
	/** The msg. */
	private String msg;


	/**
	 * Get the message.
	 *
	 * @return msg
	 */
	public final String getMsg() { return msg; }


	/**
	 * Set the message.
	 *
	 * @param _msg the msg to set
	 */
	public final void setMsg(final String _msg) { msg = _msg; }


	/**
	 * Get the vars.
	 *
	 * @return the vars
	 */
	public final ArrayList<String> getVars() { return vars; }


	/**
	 * Set the vars.
	 *
	 * @param _vars the vars to set
	 */
	public final void setVars(final ArrayList<String> _vars) { vars = _vars; }


	/**
	 * Visit.
	 * 
	 * @param data the data
	 * @param node the node
	 * 
	 * @return data
	 */
	public final Object visit(final SimpleNode node, final Object data) { return data; }


	/**
	 * Visit.
	 * 
	 * @param data the data
	 * @param node the node
	 * 
	 * @return data
	 * 
	 * @throws ParseException the parse exception
	 */
	public final Object visit(final ASTStart node, final Object data) throws ParseException {
		Object data2 = data;
		try { data2 = node.childrenAccept(this, data2); } catch (Exception ex) { msg = STGraphC.getMessage("ERR.PARSE_START"); throw(new ParseException()); } //$NON-NLS-1$
		return data2;
	}


	/**
	 * Visit.
	 * 
	 * @param data the data
	 * @param node the node
	 * 
	 * @return data
	 * 
	 * @throws ParseException the parse exception
	 */
	public final Object visit(final ASTFunNode node, final Object data) throws ParseException {
		Object data2 = data;
		try { data2 = node.childrenAccept(this, data2); } catch (Exception ex) { msg = STGraphC.getMessage("ERR.PARSE_FUN"); throw(new ParseException()); } //$NON-NLS-1$
		return data2;
	}


	/**
	 * Visit.
	 *
	 * @param data the data
	 * @param node the node
	 *
	 * @return data
	 *
	 * @throws ParseException the parse exception
	 */
	public final Object visit(final ASTVarNode node, final Object data) throws ParseException {
		Object data2 = data;
		try { vars.add(node.getName()); data2 = node.childrenAccept(this, data2); } catch (Exception ex) { msg = STGraphC.getMessage("ERR.PARSE_VAR"); throw(new ParseException()); } //$NON-NLS-1$
		return data2;
	}


	/**
	 * Visit.
	 *
	 * @param data the data
	 * @param node the node
	 *
	 * @return data
	 *
	 * @throws ParseException the parse exception
	 */
	public final Object visit(final org.nfunk.jep.ASTConstant node, final Object data) throws ParseException {
		Object data2 = data;
		try { data2 = node.childrenAccept(this, data2); } catch (Exception ex) { msg = STGraphC.getMessage("ERR.PARSE_CONSTANT"); throw(new ParseException()); } //$NON-NLS-1$
		return data2;
	}

}
