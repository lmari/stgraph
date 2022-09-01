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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

import it.liuc.stgraph.util.TokenAwareEditor;
import it.liuc.stgraph.util.STTools;


/** Expression evaluator. */
@SuppressWarnings("serial")
public class STEvaluator extends JDialog {
	/** The jContentPane. */
	private JPanel jContentPane = null;
	/** The scroll for editor. */
	private JScrollPane scroll = null;
	/** The editor. */
	private TokenAwareEditor editor = null;
	/** The result. */
	private JEditorPane result = null;


	/** Class constructor. */
	public STEvaluator() {
		super();
		setTitle(STGraphC.getMessage("EVALUATOR.DIALOG.TITLE")); //$NON-NLS-1$
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setModal(false);
		open();
	}


	/** Initialize and open the dialog. */
	private void open() {
		setContentPane(getJContentPane());
		editor.setHTMLText(editor.getInterpreter(), STTools.BLANK);
		pack();
		setLocation(STGraph.getSTC().getX() + 50, STGraph.getSTC().getY() + 50);
		setVisible(true);
	}


	/** Initialize jContentPane.
	 * @return panel */
	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			// editor field
			jContentPane.add(getScrollForEditor(), BorderLayout.CENTER);
			// result field
			jContentPane.add(result = new JEditorPane(), BorderLayout.SOUTH);
			////result.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return jContentPane;
	}


	/** Initialize the scroll for editor.
	 * @return scroll */
	private JScrollPane getScrollForEditor() {
		if(scroll == null) {
			scroll = new JScrollPane(getSTTokenAwareEditor());
		}
		return scroll;
	}


	/** Initialize the editor.
	 * @return editor */
	private TokenAwareEditor getSTTokenAwareEditor() {
		if(editor == null) {
			editor = new TokenAwareEditor(STGraph.getSTC().getCurrentGraph().getInterpreter(), result);
			editor.setPreferredSize(new Dimension(200, 60));
			editor.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						String t = editor.getPlainText().trim();
						if(!t.equals(STTools.BLANK)) { exec(t); }
					}
				}
			});
		}
		return editor;
	}


	/** Exec helper method.
	 * @param expr the expr */
	private final void exec(final String expr) {
		STInterpreter interpreter = STGraph.getSTC().getCurrentGraph().getInterpreter();
		interpreter.parseExpression(expr);
		Object r = interpreter.getValueAsObject();
		if(!interpreter.hasError()) { result.setText(r.toString()); }
		else { result.setText(STTools.replaceCRwithBR(interpreter.getErrorInfo())); }
	}

}
