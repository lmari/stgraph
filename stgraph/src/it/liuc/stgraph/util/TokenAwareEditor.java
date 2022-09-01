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
package it.liuc.stgraph.util;

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.fun.STFunction;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STNode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.nfunk.jep.Parser;
import org.nfunk.jep.ParserConstants;
import org.nfunk.jep.Token;
import org.nfunk.jep.type.Tensor;


/** Handle a token-aware editor.
 * <br>Some functions and formatting options are accessible by right click + contextual popup menu. */
@SuppressWarnings("serial")
public class TokenAwareEditor extends JEditorPane {
	/** The Constant BLANK. */
	public static final String BLANK = "`"; //$NON-NLS-1$
	/** The Constant RETURN. */
	public static final String RETURN = "~"; //$NON-NLS-1$
	/** The Constant PAGE_START. */
	private static final String PAGE_START = "<html><head></head>"; //$NON-NLS-1$
	/** The Constant PAGE_END. */
	private static final String PAGE_END = "</body></html>"; //$NON-NLS-1$
	/** The Constant NUMBER_START. */
	private static final String NUMBER_START = "<font color='red'><b>"; //$NON-NLS-1$
	/** The Constant NUMBER_END. */
	private static final String NUMBER_END = "</b></font>"; //$NON-NLS-1$
	/** The Constant STRING_START. */
	private static final String STRING_START = "<font color='red'><b>"; //$NON-NLS-1$
	/** The Constant STRING_END. */
	private static final String STRING_END = "</b></font>"; //$NON-NLS-1$
	/** The Constant FUNCTION_START. */
	private static final String FUNCTION_START = "<font color='blue'><b>"; //$NON-NLS-1$
	/** The Constant FUNCTION_END. */
	private static final String FUNCTION_END = "</b></font>"; //$NON-NLS-1$
	/** The Constant VARIABLE_START. */
	private static final String VARIABLE_START = "<font color='green'><b>"; //$NON-NLS-1$
	/** The Constant VARIABLE_END. */
	private static final String VARIABLE_END = "</b></font>"; //$NON-NLS-1$
	/** The Constant OPERATOR_START. */
	private static final String OPERATOR_START = "<font color='blue'>"; //$NON-NLS-1$
	/** The Constant OPERATOR_END. */
	private static final String OPERATOR_END = "</font>"; //$NON-NLS-1$
	/** The Constant DELIMITER_START. */
	private static final String DELIMITER_START = "<font color='black'>"; //$NON-NLS-1$
	/** The Constant DELIMITER_END. */
	private static final String DELIMITER_END = "</font>"; //$NON-NLS-1$
	/** The Constant HIGHLIGHT_START. */
	private static final String HIGHLIGHT_START = "<span style=\"background-color:#00FF00\">"; //$NON-NLS-1$
	/** The Constant HIGHLIGHT_END. */
	private static final String HIGHLIGHT_END = "</span>"; //$NON-NLS-1$
	/** The Constant UNRECOGNIZED_START. */
	private static final String UNRECOGNIZED_START = "<font color='gray'>"; //$NON-NLS-1$
	/** The Constant UNRECOGNIZED_END. */
	private static final String UNRECOGNIZED_END = "</font>"; //$NON-NLS-1$
	/** The Constant OK_STATUS_COLOR. */
	private static final Color OK_STATUS_COLOR = Color.WHITE;
	/** The Constant KO_STATUS_COLOR. */
	private static final Color KO_STATUS_COLOR = new Color(220, 220, 220);
	/** The Constant MOD_NOTHING. */
	private static final int MOD_NOTHING = 0;
	/** The Constant MOD_SPACE_AFTER_COMMA. */
	private static final int MOD_INSERT_SPACES = 1;
	/** The Constant MOD_INDENTED. */
	private static final int MOD_INDENTED = 2;
	/** The Constant MOD_REMOVE_FORMATTING. */
	private static final int MOD_REMOVE_FORMATTING = 9;

	/** The interpreter. */
	private STInterpreter interpreter;
	/** The correct tokenization. */
	private boolean correctTokenization = true;
	/** The caret pos. */
	private int caretPos;
	/** The node. */
	private STNode node;
	/** The node expression. */
	private String expressionType;
	/** The current value. */
	private transient Object currentValue;
	/** The token under caret. */
	private String tokenUnderCaret;
	/** The starting position of token under caret. */
	private int tokenUnderCaretPosition;
	/** The current token. */
	private String currentToken;
	/** The current token type. */
	private int currentTokenType;
	public static final int TOKENTYPE_FUN = 0;
	public static final int TOKENTYPE_VAR = 1;
	/** The functions. */
	@SuppressWarnings("rawtypes")
	private Hashtable functions;
	/** The variables. */
	@SuppressWarnings("rawtypes")
	private Hashtable variables;
	/** The first call. */
	private boolean firstCall = true;
	/** The ret pos. */
	private int retPos = -1;
	/** The ls. */
	private static String ls = System.getProperty("line.separator"); //$NON-NLS-1$
	/** The ls2. */
	private static String ls2 = new String(new char[] {10}) + "\\{\\{\\{\\{"; // what a peculiar solution to a peculiar problem... //$NON-NLS-1$
	/** The ls3. */
	private static String ls3 = new String(new char[] {10, 32, 32, 32, 32}); // what a peculiar solution to a peculiar problem...

	/** Is the given expression to be traced? (currently switched by ctrl-Q). */
	private static boolean traced = false;
	/** Is the given expression under evaluation?. */
	private static boolean underEvaluation = false;
	/** The trace text (currenly appended to the help text). */
	private static String traceText = null;
	/** The popup menu. */
	private JPopupMenu popupMenu;
	/** The popup autocomplete. */
	private JPopupMenu popupAutoComplete;
	/** Is autocomplete visible?. */
	private boolean autoCompleteVisible;
	/** The editor pane. */
	JEditorPane helpPane;
	/** The helptext size. */
	protected String helptextSize = STConfigurator.getProperty("HELPTEXT.SIZE"); //$NON-NLS-1$

	private int selStart;
	private int selEnd;


	/** Class constructor. */
	public TokenAwareEditor() { ; }


	/** Class constructor.
	 * @param interpreter the interpreter */
	public TokenAwareEditor(final STInterpreter interpreter, final JEditorPane helpPane) {
		super("text/html", STTools.BLANK); //$NON-NLS-1$
		setInterpreter(interpreter);
		this.helpPane = helpPane;
		setBorder(BorderFactory.createLoweredBevelBorder());
		setBackground(OK_STATUS_COLOR);
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(final KeyEvent e) {
				if(TokenAwareEditor.this.getSelectedText() != null) {
					TokenAwareEditor.this.selStart = TokenAwareEditor.this.getSelectionStart();
					TokenAwareEditor.this.selEnd = TokenAwareEditor.this.getSelectionEnd();
				} else {
					TokenAwareEditor.this.selStart = -1;
					TokenAwareEditor.this.selEnd = -1;
				}
				if(e.getKeyCode() == KeyEvent.VK_ENTER) { e.consume(); }
			}

			public void keyReleased(final KeyEvent e) {
				retPos = -1;
				int k = e.getKeyCode();
				if(k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT) {
					caretPos = fixedGetCaretPosition();
					if(e.isControlDown() || e.isShiftDown()) { return; }
					if(TokenAwareEditor.this.selStart != TokenAwareEditor.this.selEnd) {
						if(k == KeyEvent.VK_LEFT) { setCaretPosition(caretPos = TokenAwareEditor.this.selStart); }
						else { setCaretPosition(caretPos = TokenAwareEditor.this.selEnd); }
					}
				}
				else if(k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN) { caretPos = fixedGetCaretPosition(); if(e.isControlDown() || e.isShiftDown()) { return; } }
				else if(k == KeyEvent.VK_HOME) { setCaretPosition(caretPos = 1); }
				else if(k == KeyEvent.VK_BACK_SPACE) { if(e.isControlDown() || e.isShiftDown()) { return; } }
				else if(k == KeyEvent.VK_V && e.isControlDown()) {
					int l = 0;
					try { l = ((String)getToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor)).length(); } catch (Exception ex) { ; }
					try { setCaretPosition(caretPos += l); } catch (Exception ex) { ; } }
				else if(k == KeyEvent.VK_SHIFT || k == KeyEvent.VK_CONTROL || (k == KeyEvent.VK_C && e.isControlDown())) { caretPos = fixedGetCaretPosition(); return; }
				else if(k == KeyEvent.VK_ENTER && e.isShiftDown()) {
					retPos = fixedGetCaretPosition();
					try { setCaretPosition(++caretPos); } catch (Exception ex) { ; } // catch the error -- that should not be there... -- if it is the last character
				}
				else if(k == KeyEvent.VK_Z && e.isControlDown()) { setHTMLText(TokenAwareEditor.this.interpreter, getPlainText(false), getPairedBrackets(), MOD_NOTHING); return; }
				else if(k == KeyEvent.VK_R && e.isControlDown()) { pmRemoveFormatting(); return; }
				else if(k == KeyEvent.VK_S && e.isControlDown()) { pmAddSpaces(); return; }
				else if(k == KeyEvent.VK_T && e.isControlDown()) { pmIndent(); return; }
				else if(k == KeyEvent.VK_PLUS && e.isControlDown()) { pmIncreaseFont(); return; }
				else if(k == KeyEvent.VK_MINUS && e.isControlDown()) { pmDecreaseFont(); return; }
				else if(k == KeyEvent.VK_E && e.isControlDown()) { pmEvaluate(); return; }
				else if(k == KeyEvent.VK_F && e.isControlDown()) { pmEvaluateVar(); return; }
				else if(k == KeyEvent.VK_Q && e.isControlDown()) { pmSwitchTrace(); return; }

				else if(k == KeyEvent.VK_SPACE && e.isControlDown()) { getPopupAutocomplete(); return; }

				else if(k == KeyEvent.VK_A && e.isControlDown()) { return; } // allow the default (select all) action
				setHTMLText(TokenAwareEditor.this.interpreter, getPlainText(true), getPairedBrackets(), MOD_NOTHING);
				if(caretPos < 1) { setCaretPosition(caretPos = 1); }
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(final MouseEvent e) {
				if(node == null || node.getGraph() == null) { return; }
				if(!node.getGraph().isEditable) { return; }
				if(SwingUtilities.isRightMouseButton(e)) {
					getPopupMenu(e);
					return;
				}
				if(e.getClickCount() != 2) { setHTMLText(TokenAwareEditor.this.interpreter, getQuotedText(true), getPairedBrackets(), MOD_NOTHING); }
			}
		});

	}


	/** Set the interpreter.
	 * @param interpreter the interpreter */
	public final void setInterpreter(final STInterpreter interpreter) {
		this.interpreter = interpreter;
		if(interpreter != null) { functions = interpreter.getFunctionTable(); }
	}

	/** Get the interpreter.
	 * @return the interpreter */
	public STInterpreter getInterpreter() { return interpreter; }


	/** Set parameters allowing contextual evaluation.
	 * @param node the node
	 * @param expressionType the expression type */
	public final void setParams(final STNode node, final String expressionType) {
		this.node = node;
		this.expressionType = expressionType;
	}


	/** Get the current font size.
	 * @return the font size */
	private int getFontSize() {
		String s = STConfigurator.getProperty("EDITOR.FONTSIZE"); //$NON-NLS-1$
		return (s == null) ? 12 : Integer.parseInt(s); // default value
	}


	/** Operate in dependence on the result of the tokenization.
	 * <br>(possibly overridden by subclasses). */
	protected final void operateOnTokenizationResult() {
		if(correctTokenization) {
			setBackground(OK_STATUS_COLOR);
		} else {
			setBackground(KO_STATUS_COLOR);
		}
	}


	/** Return the result of the tokenization.
	 * @return is correct */
	public final boolean isCorrectTokenization() { return correctTokenization; }


	/** Type maintaining information on brackets. */
	private class BracketInfo {
		/** The typ. */
		private int typ;
		/** The pos. */
		private int pos;
		/** The seq. */
		private int seq;


		/** Class constructor.
		 * @param typ the typ
		 * @param pos the pos
		 * @param seq the seq */
		BracketInfo(final int typ, final int pos, final int seq) {
			this.typ = typ;
			this.pos = pos;
			this.seq = seq;
		}

	}


	/** Type maintaining information on brackets. */
	private class BracketInfo2 {
		/** The position of the macthing left bracket. */
		private int left;
		/** The position of the macthing right bracket. */
		private int right;
		/** The bracket type. */
		private String type;


		/** Class constructor.
		 * @param left the left
		 * @param right the right
		 * @param type the type */
		BracketInfo2(final int left, final int right, final String type) {
			this.left = left;
			this.right = right;
			this.type = type;
		}

	}


	/** Get the position of the found bracket.
	 * @return bracket info */
	final BracketInfo2 getPairedBrackets() {
		int pos = getCaretPosition() - 1;
		if(pos == 0) { return new BracketInfo2(0, 0, STTools.BLANK); }
		String c = null;
		try { c = getText(pos, 1); } catch (Exception e) { return new BracketInfo2(0, 0, STTools.BLANK); }
		if(c.equals(STTools.OPENP) || c.equals(STTools.CLOSEP) || c.equals(STTools.OPENV) || c.equals(STTools.CLOSEV)) { 
			Point p = getPairedBracketsHelper(c, pos);
			if(c.equals(STTools.OPENP) || c.equals(STTools.CLOSEP)) { return new BracketInfo2(p.x, p.y, STTools.OPENP); }
			return new BracketInfo2(p.x, p.y, STTools.OPENV);
		}
		return new BracketInfo2(0, 0, STTools.BLANK);
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Point getPairedBracketsHelper(final String c, final int p) {
		try {
			int pos = p;
			char lb = 0;
			char rb = 0;
			if(c.equals(STTools.OPENP) || c.equals(STTools.CLOSEP)) { 
				lb = '(';
				rb = ')';
			}
			if(c.equals(STTools.OPENV) || c.equals(STTools.CLOSEV)) {
				lb = '[';
				rb = ']';
			}
			Vector stack = new Vector();
			String text = getPlainText(true);
			int numL = 0;
			int numR = 0;
			for(int i = 0; i < text.length(); i++) { // generate a stack of '(' and ')' or '[' and ']'
				if(text.charAt(i) == lb) {
					stack.add(new BracketInfo(lb, i, ++numL));
				} else if(text.charAt(i) == rb) {
					stack.add(new BracketInfo(rb, i, ++numR));
				}
			}
			if(stack.size() < 2) { return new Point(0, 0); } // nothing to match!
			pos--; // consider as the current character the one at the left of the cursor
			int nestingLevel = 0;
			if(c.equals(STTools.OPENP) || c.equals(STTools.OPENV)) {
				int ii = 0;
				while(((BracketInfo)stack.get(ii)).pos < pos) {
					ii++; // discard the brackets at the left of the selected one and the selected one itself
				}
				for(int i = ii + 1; i < stack.size(); i++) {
					BracketInfo bi = (BracketInfo)stack.get(i);
					if(bi.typ == rb && nestingLevel == 0) { return new Point(((BracketInfo)stack.get(ii)).seq, bi.seq); }
					if(bi.typ == lb) { nestingLevel++; }
					if(bi.typ == rb) { nestingLevel--; }
				}
				return new Point(0, 0); // no matching ) found to pair the current (
			}
			// if ')' or ']' is the current character
			int ii = stack.size() - 1;
			while(((BracketInfo)stack.get(ii)).pos >= pos) {
				ii--; // discard the brackets at the right of the selected one and the selected one itself
				if(ii < 0) { return new Point(0, 0); } // nothing to match!
			}
			for(int i = ii; i >= 0; i--) {
				BracketInfo bi = (BracketInfo)stack.get(i);
				if(bi.typ == lb && nestingLevel == 0) { return new Point(bi.seq, ((BracketInfo)stack.get(ii + 1)).seq); }
				if(bi.typ == lb) { nestingLevel++; }
				if(bi.typ == rb) { nestingLevel--; }
			}
		} catch (Exception e) { ; }
		return new Point(0, 0); // no matching ')' or ']' found to pair the current '(' or '['
	}


	/** Get the computed value.
	 * <br>This method returns a new value at each keystroke, and therefore it can be used in callback, typically in a keyReleased() method.
	 * @return string */
	public final String getCurrentValue() {
		if(currentValue == null) { return STTools.BLANK; }
		if(currentValue instanceof Tensor) { return ((Tensor)currentValue).getValueAsString(STData.FORMAT_ALTERNATE); }
		if(currentValue instanceof String) { return (String)currentValue; }
		return STTools.BLANK;
	}


	/** Get the computed value.
	 * This method returns a new value at each keystroke, and therefore it can be used in callback, typically in a keyReleased() method.
	 * @return object */
	public final Object getCurrentValueAsObject() { return currentValue; }


	/** Get the current token.
	 * @return currentToken */
	public final String getCurrentToken() { return currentToken; }


	/** Get the current token type.
	 * @return currentTokenType */
	public final int getCurrentTokenType() { return currentTokenType; }


	/** Get the plain text in the field, cleaned from any HTML tag and code.
	 * @return text */
	public final String getPlainText() { return getPlainText(false); }


	/** Get the plain text in the field, cleaned from any HTML tag and code but with quotes.
	 * @param maintainFormat the maintain format
	 * @return text */
	public final String getQuotedText(final boolean maintainFormat) { return getPlainText(maintainFormat).replaceAll("&quot;", "\""); } //$NON-NLS-1$ //$NON-NLS-2$


	/** Get the plain text of the specified string, cleaned from any HTML tag and code.
	 * @param text the string to be cleaned
	 * @return text */
	public final static String cleanText(final String text) {
		String result = text;
		result = result.replaceAll("<[^>]*>", STTools.BLANK).replaceAll(" ", STTools.BLANK).replaceAll("&nbsp;", STTools.BLANK).replaceAll("&#160;", STTools.BLANK).replaceAll(ls, STTools.BLANK);
		result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
		result = result.replaceAll("&quot;", "\"").replaceAll(BLANK, "").replaceAll(RETURN, "");    	
		return result;
	}


	/** Get the plain text in the field, cleaned from any HTML tag and code, but possibly maintaining spaces and newlines.
	 * @param maintainFormat should the format be maintained?
	 * @return text */
	public final String getPlainText(final boolean maintainFormat) {
		caretPos = getCaretPosition(); //TODO [token editor] this method has the (wrong) side-effect of moving the caret
		if(firstCall) { // a ridicolous trick, to avoid a "peculiar" behavior...
			caretPos++;
			firstCall = false;
		}
		String result = getText();
		if(maintainFormat) {
			int p = result.indexOf("<body>");
			if(p != -1) { result = result.substring(p + 6); } // remove the HTML header
			p = result.indexOf("</body>");
			if(p != -1) { result = result.substring(0, p); } // remove the HTML footer
			result = result.replaceAll(ls3, STTools.BLANK).replaceAll(ls2, STTools.BLANK).replaceAll(ls, STTools.BLANK); // remove all line breaks
			result = result.replaceAll("<br>", RETURN); // deal with the "manual" line breaks
			result = result.replaceAll("<[^>]*>", STTools.BLANK); // remove all remaining HTML tags
			result = result.replaceAll("&nbsp;", BLANK).replaceAll("&#160;", BLANK).replaceAll(" ", BLANK); // deal with the "manual" spaces
			result = result.substring(0, result.length() - 2); // odd requirement, to avoid the previous trim and allow manually inserted trailing spaces and line breaks (the latter still do not work correctly)...
		} else {
			result = result.replaceAll("<[^>]*>", STTools.BLANK).replaceAll(" ", STTools.BLANK).replaceAll("&nbsp;", STTools.BLANK).replaceAll("&#160;", STTools.BLANK).replaceAll(ls, STTools.BLANK);
		}
		result = result.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
		return result;
	}


	/** Set the HTML-formatted text in the field.
	 * @param text the text */
	public final void setHTMLText(final String text) { super.setText(text); }


	/** Get the HTML-formatted text in the field.
	 * @return text */
	public final String getHTMLText() { return super.getText(); }


	/** Tokenize the text in the field and accordingly assign some HTML formatting codes to the recognized tokens.
	 * @param interpreter the interpreter
	 * @param text the text */
	public final void setHTMLText(final STInterpreter interpreter, final String text) { setHTMLText(interpreter, text, new BracketInfo2(0, 0, STTools.BLANK), MOD_NOTHING); }


	/** Type maintaining information on tokens. */
	private class TokenInfo {
		/** The ima. */
		private String ima;
		/** The typ. */
		private int typ;


		/** Class constructor.
		 * @param ima the ima
		 * @param typ the typ */
		TokenInfo(final String ima, final int typ) {
			this.ima = ima;
			this.typ = typ;
		}

	}


	/** Set the HTML-formatted text, by firstly tokenizing it.
	 * @param interpreter the interpreter
	 * @param text the text
	 * @param bracketPos the bracket pos */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void setHTMLText(final STInterpreter interpreter, final String text, final BracketInfo2 bracketPos, final int modifier) {
		correctTokenization = true;
		if(STTools.isEmpty(text) || text.equals(BLANK) || text.equals(BLANK + BLANK)) {
			super.setText(PAGE_START + "<body>" + PAGE_END);
			operateOnTokenizationResult();
			return;
		}
		String text2 = text.replaceAll("&quot;", "\"");
		variables = interpreter.getSymbolTable();
		currentValue = null;
		if(retPos != -1) { text2 = text2.substring(0, retPos - 1) + RETURN + text2.substring(retPos - 1); }
		StringBuilder nextText = new StringBuilder(PAGE_START + "<body style='font-size:" + getFontSize() + "pt; font-family: Arial;'>");
		String s = null;
		Parser parser = new Parser(new StringReader(text2));
		Token token = null;
		int type;
		int numLeft = 0;
		int numRight = 0;
		correctTokenization = true;
		Vector tokens = new Vector();
		while(true) {
			type = -99;
			try {
				token = parser.getNextToken();
				if((s = token.image).equals(STTools.BLANK)) { break; }
				type = token.kind;
				tokens.add(new TokenInfo(s, type));
			} catch (Exception e) { break; }
		}

		/* monitor
        for(int i = 0; i < tokens.size(); i++) {
        	System.out.println("caretPos: " + caretPos);
        	System.out.print("[" + i + "] " + ((TokenInfo)tokens.get(i)).ima + " ");
        }
    	System.out.println(STTools.BLANK);
		*/

		int l1 = 0;
		int l2 = 0;
        for(int i = 0; i < tokens.size(); i++) {
        	l2 = l1 + ((TokenInfo)tokens.get(i)).ima.length();
        	if(caretPos >= l1 && caretPos <= l2 + 1) {
        		tokenUnderCaret = ((TokenInfo)tokens.get(i)).ima;
        		tokenUnderCaretPosition = l1;
        		break;
        	}
        	l1 = l2;
        }

		String s1 = (modifier == MOD_INSERT_SPACES) ? "&nbsp;" : "";
		String x2 = "&nbsp;&nbsp;";
		String s2 = "";

		currentToken = null;
		currentTokenType = -1;
		boolean tokenFound;
		int currLen = 1;
		int prevLen = 1;
		for(int i = 0; i < tokens.size(); i++) {
			s = ((TokenInfo)tokens.get(i)).ima;
			currLen += s.length();
			tokenFound = currLen >= caretPos && prevLen < caretPos;
			prevLen = currLen;
			type = ((TokenInfo)tokens.get(i)).typ;

			// here the modifier operates:
			// MOD_NOTHING, MOD_SPACE_AFTER_COMMA, MOD_INDENTED, MOD_REMOVE_FORMATTING
			if(type == ParserConstants.NEWLINE) {
				if(modifier == MOD_NOTHING) { nextText.append("<br>"); }
			} else if(type == ParserConstants.BLANK) {
				if(modifier == MOD_NOTHING) { nextText.append("&nbsp;"); }
			} else if(type == ParserConstants.INTEGER_LITERAL || type == ParserConstants.FLOATING_POINT_LITERAL) {
				nextText.append(NUMBER_START + s + NUMBER_END); // numbers
			} else if(type == ParserConstants.STRING_LITERAL) {
				nextText.append(STRING_START + s.replaceAll("<", "&lt;") + STRING_END); // constant strings
			} else if(type == ParserConstants.IDENTIFIER1) { // strings
				if(functions.containsKey(s)) {
					nextText.append(FUNCTION_START + s + FUNCTION_END); // functions
					if(tokenFound) {
						currentToken = s;
						currentTokenType = TOKENTYPE_FUN;
					}
				} else if(variables.containsKey(s) && (STInterpreter.getSystemVars(false, true).contains(s) || node == null || (node != null && node.checkVariable(s, expressionType) == null))) {
					nextText.append(VARIABLE_START + s + VARIABLE_END); // variables
					if(tokenFound) {
						currentToken = s;
						currentTokenType = TOKENTYPE_VAR;
					}
				} else { // (still) unrecognized identifiers
					nextText.append(UNRECOGNIZED_START + s + UNRECOGNIZED_END);
					correctTokenization = false;
				}
			}
			// logical and prefix operators
			else if(type == ParserConstants.GT) {
				nextText.append(s1 + OPERATOR_START + "&gt;" + OPERATOR_END + s1);
			} else if(type == ParserConstants.LT) {
				nextText.append(s1 + OPERATOR_START + "&lt;" + OPERATOR_END + s1);
			} else if(type == ParserConstants.LE) {
				nextText.append(s1 + OPERATOR_START + "&lt;=" + OPERATOR_END + s1);
			} else if(type == ParserConstants.GE) {
				nextText.append(s1 + OPERATOR_START + "&gt;=" + OPERATOR_END + s1);
			} else if(type == ParserConstants.EQ || type == ParserConstants.NE) {
				nextText.append(s1 + OPERATOR_START + s + OPERATOR_END + s1);
			} else if(type == ParserConstants.AND || type == ParserConstants.OR) {
				nextText.append(s1 + OPERATOR_START + s + OPERATOR_END + s1);
			}
			// infix operators
			else if(type == ParserConstants.ASSIGN) {
				nextText.append(s1 + OPERATOR_START + s + OPERATOR_END + s1);
			} else if((type >= ParserConstants.PLUS && type <= ParserConstants.CROSS)) {
				nextText.append(s1 + OPERATOR_START + s + OPERATOR_END + s1);
			} else if((type == ParserConstants.AT)) {
				nextText.append(s1 + OPERATOR_START + s + OPERATOR_END + s1);
			} else if(type == ParserConstants.SHARP || type == ParserConstants.SSHARP) {
				nextText.append(s1 + DELIMITER_START + s + DELIMITER_END + s1);
			} else if(type == ParserConstants.COMMA || type == ParserConstants.SEMI || type == ParserConstants.COLON || type == ParserConstants.LCURLY || type == ParserConstants.RCURLY) {
				nextText.append(DELIMITER_START + s + DELIMITER_END + s1);
			} else if(type == ParserConstants.LSQ) {
				if(bracketPos.type.equals(STTools.OPENV) && ++numLeft == bracketPos.left) {
					nextText.append(HIGHLIGHT_START + s + HIGHLIGHT_END);
				} else {
					nextText.append(DELIMITER_START + s + DELIMITER_END);
				}
			} else if(type == ParserConstants.RSQ) {
				if(bracketPos.type.equals(STTools.OPENV) && ++numRight == bracketPos.right) {
					nextText.append(HIGHLIGHT_START + s + HIGHLIGHT_END);
				} else {
					nextText.append(DELIMITER_START + s + DELIMITER_END);
				}
			} else if(type == ParserConstants.LRND) {
				if(bracketPos.type.equals(STTools.OPENP) && ++numLeft == bracketPos.left) {
					nextText.append(HIGHLIGHT_START + s + HIGHLIGHT_END);
				} else {
					nextText.append(DELIMITER_START + s + DELIMITER_END);
				}
				if(modifier == MOD_INDENTED) { nextText.append("<br>" + (s2 += x2)); }
			} else if(type == ParserConstants.RRND) {
				if(modifier == MOD_INDENTED) { nextText.append("<br>" + (s2 = s2.substring(0, s2.length() - 12))); }
				if(bracketPos.type.equals(STTools.OPENP) && ++numRight == bracketPos.right) {
					nextText.append(HIGHLIGHT_START + s + HIGHLIGHT_END);
				} else {
					nextText.append(DELIMITER_START + s + DELIMITER_END);
				}
			} else { // unrecognized token
				if(s.equals("&") && (i <= tokens.size() - 3) && ((((TokenInfo)tokens.get(i + 1)).ima + ((TokenInfo)tokens.get(i + 2)).ima).equals("nbsp;"))) {
					i += 2;
					nextText.append("&nbsp;");
				} else if(s.equals("&") && (i <= tokens.size() - 3) && ((((TokenInfo)tokens.get(i + 1)).ima + ((TokenInfo)tokens.get(i + 2)).ima).equals("quot;"))) {
					i += 2;
					//nextText.append("&quot;");
					nextText.append("\"");
				} else if(s.equals("&") && (i <= tokens.size() - 4) && ((((TokenInfo)tokens.get(i + 1)).ima + ((TokenInfo)tokens.get(i + 2)).ima + ((TokenInfo)tokens.get(i + 3)).ima).equals("#160;"))) {
					i += 3;
					nextText.append("&nbsp;");
				} else if(s.equals("&") && (i <= tokens.size() - 3) && (((TokenInfo)tokens.get(i + 2)).ima).equals(";")) {
					i += 2;
				} else if(s.equals("&") && (i <= tokens.size() - 4) && (((TokenInfo)tokens.get(i + 1)).ima).equals("#") && (((TokenInfo)tokens.get(i + 3)).ima).equals(";")) {
					i += 3;
				} else {
					nextText.append(UNRECOGNIZED_START + s + UNRECOGNIZED_END);
					correctTokenization = false;
				}
			}
		}
		nextText.append(PAGE_END);
		super.setText(nextText.toString());
		operateOnTokenizationResult();

		try { setCaretPosition(caretPos); } catch (Exception e) { ; }
	}


	/** On-the-fly evaluate the value of the current expression.
	 * @param interpreter the interpreter
	 * @param asString the as string
	 * @return value */
	public final Object onTheFlyEvaluate(final STInterpreter interpreter, final boolean asString) {
		return onTheFlyEvaluate(interpreter, getText(), asString);
	}


	/** * On-the-fly evaluate the value of the current expression.
	 * @param interpreter the interpreter
	 * @param text the text to be evaluated
	 * @param asString the as string
	 * @return value */
	public final Object onTheFlyEvaluate(final STInterpreter interpreter, final String text, final boolean asString) {
		if(correctTokenization && node != null) {
			String expr = text.replaceAll("<[^>]*>", STTools.BLANK).replaceAll(" ", STTools.BLANK).replaceAll("&nbsp;", STTools.BLANK).replaceAll("&#160;", STTools.BLANK).replaceAll("&quot;", "\"").replaceAll(ls, STTools.BLANK); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			expr = expr.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			correctTokenization = node.checkExpressionDefinition(interpreter, expr, expressionType) == null;
			if(correctTokenization) {
				underEvaluation = true;
				traceText = STTools.BLANK;
				node.getGraph().setIntegrationPhase(1);
				STGraph.getSTC().setCurrentlyComputedNode(node);
				Object r = interpreter.evalExpression(node, interpreter.preParseExpression(expr));
				currentValue = !interpreter.hasError() ? r : null;
				interpreter.addVariable(node.getName(), currentValue);
				underEvaluation = false;
				if(traced) { return asString ? traceText + "-->" + getCurrentValue() : getCurrentValueAsObject(); } //$NON-NLS-1$
				return asString ? getCurrentValue() : getCurrentValueAsObject();
			}
		}
		return asString ? STTools.BLANK : null;
	}


	/** Get whether the given expression should be traced.
	 * @return value */
	public final static boolean isTraced() { return traced; }


	/** Get whether the given expression is under evaluation.
	 * @return value */
	public final static boolean isUnderEvaluation() { return underEvaluation; }


	/** Append the text to be traced.
	 * @param traceText the traceText to set */
	public static void appendTraceText(String traceText) { TokenAwareEditor.traceText += traceText; }


	/** Get the fixed (i.e., >0) caret position.
	 * @return fixed caret position */
	private int fixedGetCaretPosition() {
		int t = getCaretPosition();
		if(t >= 1) { return t; }
		setCaretPosition(1);
		return 1;
	}


	/** Get and show the popup menu.
	 * @param e the triggering mouse event */
	private void getPopupMenu(final MouseEvent e) {
		AbstractAction cut = new AbstractAction(STGraphC.getMessage("TAE_POPMENU_CUT")) { public void actionPerformed(final ActionEvent e) { cut(); } }; //$NON-NLS-1$
		AbstractAction cop = new AbstractAction(STGraphC.getMessage("TAE_POPMENU_COPY")) { public void actionPerformed(final ActionEvent e) { copy(); } }; //$NON-NLS-1$
		AbstractAction pas = new AbstractAction(STGraphC.getMessage("TAE_POPMENU_PASTE")) { public void actionPerformed(final ActionEvent e) { paste(); } }; //$NON-NLS-1$
		if(popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(cut);
			popupMenu.add(cop);
			popupMenu.add(pas);
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_SELECTALL")) { public void actionPerformed(final ActionEvent e) { selectAll(); } }); //$NON-NLS-1$
			popupMenu.addSeparator();
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_AUTOCOMPLETE")) { public void actionPerformed(final ActionEvent e) { getPopupAutocomplete(); } }); //$NON-NLS-1$
			popupMenu.addSeparator();
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_ADDSPACES")) { public void actionPerformed(final ActionEvent e) { pmAddSpaces(); } }); //$NON-NLS-1$
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_INDENT")) { public void actionPerformed(final ActionEvent e) { pmIndent(); } }); //$NON-NLS-1$
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_REMOVEFORMAT")) { public void actionPerformed(final ActionEvent e) { pmRemoveFormatting(); } }); //$NON-NLS-1$
			popupMenu.addSeparator();
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_INCREASEFONT")) { public void actionPerformed(final ActionEvent e) { pmIncreaseFont(); } }); //$NON-NLS-1$
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_DECREASEFONT")) { public void actionPerformed(final ActionEvent e) { pmDecreaseFont(); } }); //$NON-NLS-1$
			popupMenu.addSeparator();
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_EVALUATE")) { public void actionPerformed(final ActionEvent e) { pmEvaluate(); } }); //$NON-NLS-1$
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_EVALUATEVAR")) { public void actionPerformed(final ActionEvent e) { pmEvaluateVar(); } }); //$NON-NLS-1$
			popupMenu.add(new AbstractAction(STGraphC.getMessage("TAE_POPMENU_SWITCHTRACING")) { public void actionPerformed(final ActionEvent e) { pmSwitchTrace(); } }); //$NON-NLS-1$
		}
		popupMenu.show(this, e.getX(), e.getY()); // display popup menu
	}


	private void pmAddSpaces() {
		setHTMLText(TokenAwareEditor.this.interpreter, getPlainText(true), getPairedBrackets(), MOD_INSERT_SPACES);
		setCaretPosition(caretPos = 1);
	}


	private void pmIndent() {
		setHTMLText(TokenAwareEditor.this.interpreter, getPlainText(true), getPairedBrackets(), MOD_INDENTED);
		setCaretPosition(caretPos = 1);
	}


	private void pmRemoveFormatting() {
		setHTMLText(TokenAwareEditor.this.interpreter, getPlainText(true), getPairedBrackets(), MOD_REMOVE_FORMATTING);
		setCaretPosition(caretPos = 1);
	}


	private void pmIncreaseFont() {
		int fontSize = getFontSize() + 1;
		STConfigurator.setProperty("EDITOR.FONTSIZE", String.valueOf(fontSize)); //$NON-NLS-1$
		STConfigurator.write();
		setHTMLText(TokenAwareEditor.this.interpreter, getPlainText(true), getPairedBrackets(), MOD_NOTHING);
		adaptContainer(fontSize);		
	}


	private void pmDecreaseFont() {
		int fontSize = getFontSize();
		if(fontSize > 7) {
			fontSize--;
			STConfigurator.setProperty("EDITOR.FONTSIZE", String.valueOf(fontSize)); //$NON-NLS-1$
			STConfigurator.write();
			setHTMLText(TokenAwareEditor.this.interpreter, getPlainText(true), getPairedBrackets(), MOD_NOTHING);
		}
		adaptContainer(fontSize);
	}
	
	
	private void adaptContainer(int fontSize) {
		if(getParent().getParent().getName() == "myTextInit") { // only in the case of the initial state editor...
			JScrollPane container = (JScrollPane)getParent().getParent();
			int w = (int)container.getSize().getWidth();
			container.setSize(new Dimension(w, (int)(fontSize * 1.75)));
		}
	}


	public void pmEvaluate() { helpPane.setText((String)onTheFlyEvaluate(interpreter, true)); }


	public void pmEvaluateVar() {
		if(currentTokenType == TOKENTYPE_VAR) {
			helpPane.setText((String)onTheFlyEvaluate(interpreter, currentToken, true));
		} else {
			helpPane.setText(STTools.BLANK);
		}
	}


	private void pmSwitchTrace() { traced = !traced; }


	/** Get and show the popup autocomplete. */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void getPopupAutocomplete() {
		if(popupAutoComplete == null) {
			popupAutoComplete = new JPopupMenu();

			popupAutoComplete.addPopupMenuListener(new PopupMenuListener() {
				@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { autoCompleteVisible = true; }
				@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { ; }
				@Override public void popupMenuCanceled(PopupMenuEvent e) { ; }
			});

		}

		if(tokenUnderCaret == null || tokenUnderCaret.length() == 0) { return; } // appropriate only for non-null strings

		final ArrayList<String> allVars = new ArrayList<String>();
		Enumeration<String> e = interpreter.getSymTab().keys(); 
		while(e.hasMoreElements()) { allVars.add(e.nextElement()); }

		final ArrayList<String> allFuns = new ArrayList<String>();
		for(STFunction f : interpreter.getFunctions()) { allFuns.add(f.getName()); }

		ArrayList<String> list = new ArrayList<String>();
		for(String s : allVars) {
			if(s.startsWith(tokenUnderCaret)) { list.add(s); }
		}
		for(String s : allFuns) {
			if(s.startsWith(tokenUnderCaret)) { list.add(s); }
		}
		if(list.size() == 0) { return; }

		try {
			JMenuItem item;
			popupAutoComplete.removeAll();
			for(final String s : list) {
				popupAutoComplete.add(item = new JMenuItem(s));
				item.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 10)); //$NON-NLS-1$

				item.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent e) {
						int l = tokenUnderCaret.length();
						String t = getPlainText(true);
						t = t.substring(0, tokenUnderCaretPosition) + s + t.substring(tokenUnderCaretPosition + l);
						setHTMLText(TokenAwareEditor.this.interpreter, t, getPairedBrackets(), MOD_NOTHING);
						setCaretPosition(getCaretPosition() + s.length() - l);
					}
				});
				item.addChangeListener(new ChangeListener() {
					@Override public void stateChanged(ChangeEvent e) {
						String h = STInterpreter.getVariableDescription(((JMenuItem)e.getSource()).getText());
						if(h.length() == 0) { h = STInterpreter.getFunctionDescription(((JMenuItem)e.getSource()).getText()); }
						setMessage(h);
					}
				});
			}
			Rectangle r = this.modelToView(caretPos);
			popupAutoComplete.show(this, r.x, r.y); // display popup autocomplete
		} catch (Exception ex) { ; }
	}

	public void setAutoCompleteVisible(boolean autoCompleteVisible) { this.autoCompleteVisible = autoCompleteVisible ; }

	public boolean isAutoCompleteVisible() { return autoCompleteVisible; }


	/** Set the message to be displayed.
	 * @param message the message */
	public final void setMessage(final String message) {
		helpPane.setText("<html><font size='" + helptextSize + "'>" + message + "</font></html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		helpPane.setCaretPosition(0);
	}

}
