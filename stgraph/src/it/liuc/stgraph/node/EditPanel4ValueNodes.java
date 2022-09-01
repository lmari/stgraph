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

import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.util.EditDialog;
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STParserTree;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.util.TokenAwareEditor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/** Panel for the configuration of the variable nodes. */
@SuppressWarnings({"serial", "rawtypes"})
public class EditPanel4ValueNodes extends EditPanel4Nodes {
	/** The constant HTMLOPEN. */
	private final static String HTMLOPEN = "<html><u><font color='blue'>"; //$NON-NLS-1$
	/** The constant HTMLCLOSE. */
	private final static String HTMLCLOSE = "</font></u>"; //$NON-NLS-1$

	/** The contentPanel1. */
	private static JPanel contentPanel1 = null;
	/** The contentPanel1a. */
	private static JPanel contentPanel1a = null;
	/** The check global. */
	private static JCheckBox checkGlobal = null;
	/** The check out. */
	private static JCheckBox checkOut = null;
	/** The check vector output. */
	private static JCheckBox checkVectorOutput = null;
	/** The list of node types. */
	private static JComboBox listNodeTypes = null;
	/** The lab init. */
	private static JLabel labInit = null;
	/** The lab trans. */
	private static JLabel labTrans = null;
	/** The lab trans 2. */
	private static JLabel labTrans2 = null;
	/** The lab expr. */
	private static JLabel labExpr = null; 
	/** The lab expr 2. */
	private static JLabel labExpr2 = null; 
	/** The text init. */
	private static JScrollPane textInit = null;
	/** The text initb. */
	private static TokenAwareEditor textInitb = null;
	/** The text trans. */
	private static JScrollPane textTrans = null;
	/** The text transb. */
	private static TokenAwareEditor textTransb = null;
	/** The text trans2. */
	private static JScrollPane textTrans2 = null;
	/** The text trans2b. */
	private static STParserTree textTrans2b = null;
	/** The text expr. */
	private static JScrollPane textExpr = null;
	/** The text exprb. */
	private static TokenAwareEditor textExprb = null;
	/** The text expr2. */
	private static JScrollPane textExpr2 = null;
	/** The text expr2b. */
	private static STParserTree textExpr2b = null;
	/** The ed. */
	private static TokenAwareEditor ed = null;
	/** The ed. */
	private static TokenAwareEditor currEd = null;

	/** The active tab. */
	protected static int activeTab = 0;


	/** Class constructor. */
	public EditPanel4ValueNodes() { super(); }


	/** Get the first panel.
	 * @return the first panel */
	@Override protected JPanel getContentPanel1() {
		if(contentPanel1 == null) {
			contentPanel1 = new JPanel(new GridBagLayout());
			//
			JPanel panel1 = new JPanel();
			panel1.setLayout(new GridBagLayout());
			panel1.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.VARNAME") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			GridConstraints gbcName2 = new GridConstraints(1, 0);
			gbcName2.fill = GridBagConstraints.HORIZONTAL;
			gbcName2.weightx = 1.0;
			panel1.add(getNodeNameField(), gbcName2);
			panel1.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.TYPE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(2, 0, GridBagConstraints.EAST));
			panel1.add(getListNodeTypes(), new GridConstraints(3, 0, GridBagConstraints.WEST));
			GridConstraints gbcPanel1 = new GridConstraints(0, 0, GridBagConstraints.WEST);
			gbcPanel1.fill = GridBagConstraints.HORIZONTAL;
			gbcPanel1.weightx = 1.0;
			gbcPanel1.gridwidth = 3;
			contentPanel1.add(panel1, gbcPanel1);
			//
			JPanel panel2 = new JPanel();
			panel2.add(getCheckOut());
			panel2.add(getCheckVectorOutput());
			panel2.add(getCheckGlobal());
			GridConstraints gbcPanel2 = new GridConstraints(0, 1, GridBagConstraints.EAST);
			gbcPanel2.weightx = 1.0;
			gbcPanel2.gridwidth = 3;
			contentPanel1.add(panel2, gbcPanel2);
			// initial state
			contentPanel1.add(labInit = new JLabel(STGraphC.getMessage("NODE.DIALOG.INITSTATE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 3, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcInit2 = new GridConstraints(1, 3);
			gbcInit2.fill = GridBagConstraints.HORIZONTAL;
			gbcInit2.gridwidth = 2;
			gbcInit2.weightx = 1.0;
			contentPanel1.add(getTextInit(), gbcInit2);
			// state transition
			labTrans = new JLabel(HTMLOPEN + STGraphC.getMessage("NODE.DIALOG.TRANSSTATE") + STTools.COLON + HTMLCLOSE); // State transition //$NON-NLS-1$
			labTrans.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) { switcher(getTextTrans(), getTextTrans2()); }
			});
			contentPanel1.add(labTrans, new GridConstraints(0, 4, GridBagConstraints.NORTHEAST));
			//
			labTrans2 = new JLabel(">>"); //$NON-NLS-1$
			labTrans2.setCursor(new Cursor(Cursor.HAND_CURSOR));
			labTrans2.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) { getTabbedPanel().setComponentAt(0, getContentPanel1a(nodeNameField.getText(), STGraphC.getMessage("NODE.DIALOG.TRANSSTATE"), textTransb)); getTabbedPanel().repaint(); } //$NON-NLS-1$
			});
			contentPanel1.add(labTrans2, new GridConstraints(0, 5, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcTrans2 = new GridConstraints(1, 4);
			gbcTrans2.fill = GridBagConstraints.BOTH;
			gbcTrans2.gridwidth = 2;
			gbcTrans2.gridheight = 2;
			gbcTrans2.weightx = 1.0; gbcTrans2.weighty = 1.0;
			contentPanel1.add(getTextTrans2(), gbcTrans2);
			getTextTrans2().setVisible(false);
			contentPanel1.add(getTextTrans(), gbcTrans2);
			// output function
			labExpr = new JLabel(HTMLOPEN + STGraphC.getMessage("NODE.DIALOG.OUTEXPR") + STTools.COLON + HTMLCLOSE); // Output expression //$NON-NLS-1$
			labExpr.setCursor(new Cursor(Cursor.HAND_CURSOR));
			labExpr.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) { switcher(getTextExpr(), getTextExpr2()); }
			});
			contentPanel1.add(labExpr, new GridConstraints(0, 6, GridBagConstraints.NORTHEAST));
			//
			labExpr2 = new JLabel(">>"); //$NON-NLS-1$
			labExpr2.setCursor(new Cursor(Cursor.HAND_CURSOR));
			labExpr2.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) { getTabbedPanel().setComponentAt(0, getContentPanel1a(nodeNameField.getText(), STGraphC.getMessage("NODE.DIALOG.OUTEXPR"), textExprb)); getTabbedPanel().repaint(); } //$NON-NLS-1$
			});
			contentPanel1.add(labExpr2, new GridConstraints(0, 7, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcExpr2 = new GridConstraints(1, 6);
			gbcExpr2.fill = GridBagConstraints.BOTH;
			gbcExpr2.gridwidth = 2;
			gbcExpr2.gridheight = 2;
			gbcExpr2.weightx = 1.0; gbcExpr2.weighty = 1.0;
			contentPanel1.add(getTextExpr2(), gbcExpr2);
			getTextExpr2().setVisible(false);
			contentPanel1.add(getTextExpr(), gbcExpr2);
			// variables and functions
			GridConstraints gbcVar = new GridConstraints(0, 8, GridBagConstraints.NORTHWEST);
			gbcVar.gridwidth = 3;
			gbcVar.fill = GridBagConstraints.BOTH;
			gbcVar.weightx = 1.0; gbcVar.weighty = 1.0;
			contentPanel1.add(getVarAndFunPanel(), gbcVar);
			// helpText
			GridConstraints gbcHelp = new GridConstraints(0, 9);
			gbcHelp.fill = GridBagConstraints.BOTH;
			gbcHelp.gridwidth = 3;
			contentPanel1.add(getScrollHelp(), gbcHelp);
		}
		return contentPanel1;
	}


	/** Get the modified version of the first panel.
	 * @param name the name
	 * @param type the type
	 * @param expr the expr
	 * @return the first panel */
	private JPanel getContentPanel1a(final String name, final String type, final TokenAwareEditor expr) {
		contentPanel1a = new JPanel(new GridBagLayout());

		ed = new TokenAwareEditor(null, helpText2);
		ed.addFocusListener(new MyFocusAdapter(ed));
		ed.addKeyListener(new MyKeyAdapter(ed));
		ed.addMouseListener(new MyMouseAdapter(ed));

		JLabel lab = new JLabel("<< " + name + STTools.SPACE + type + " <<"); //$NON-NLS-1$ //$NON-NLS-2$
		lab.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lab.addMouseListener(new MouseAdapter() {
			public void mouseClicked(final MouseEvent e) {
				expr.setHTMLText(ed.getInterpreter(), ed.getPlainText(true));
				getTabbedPanel().setComponentAt(0, contentPanel1);
				getTabbedPanel().repaint();
			}
		});
		GridConstraints gbcLab = new GridConstraints(0, 0, GridBagConstraints.NORTHWEST);
		gbcLab.weightx = 1.0;
		contentPanel1a.add(lab, gbcLab);

		GridConstraints gbcEd = new GridConstraints(0, 1, GridBagConstraints.WEST);
		gbcEd.fill = GridBagConstraints.BOTH;
		gbcEd.weightx = 1.0;
		gbcEd.weighty = 1.0;
		JScrollPane scrollEd = new JScrollPane();
		scrollEd.setViewportView(ed);
		contentPanel1a.add(scrollEd, gbcEd);
		
		helpText2.setText(STTools.BLANK);
		JScrollPane scrollHelp = new JScrollPane();
		scrollHelp.setPreferredSize(new Dimension(100, 50));
		scrollHelp.setMinimumSize(new Dimension(100, 50));
		scrollHelp.setViewportView(helpText2);
		GridConstraints gbcHelp = new GridConstraints(0, 2);
		gbcHelp.fill = GridBagConstraints.HORIZONTAL;
		contentPanel1a.add(scrollHelp, gbcHelp);

		currEd = expr;
		lab.setText("<< " + name + STTools.SPACE + type + " <<"); //$NON-NLS-1$ //$NON-NLS-2$
		STInterpreter interpreter = node.getGraph().getInterpreter();
		ed.setInterpreter(interpreter);
		if(type.equals(STGraphC.getMessage("NODE.DIALOG.TRANSSTATE"))) { //$NON-NLS-1$
			ed.setParams(node, STNode.PROP_STATETRANSITION);
			ed.setToolTipText(STGraphC.getMessage("NODE.DIALOG.TRANSSTATE.TT")); //$NON-NLS-1$
		} else if(type.equals(STGraphC.getMessage("NODE.DIALOG.OUTEXPR"))) { //$NON-NLS-1$
			ed.setParams(node, STNode.PROP_EXPRESSION);
			ed.setToolTipText(STGraphC.getMessage("NODE.DIALOG.OUTEXPR.TT")); //$NON-NLS-1$
		}
		ed.setHTMLText(expr.getInterpreter(), expr.getPlainText(true));
		return contentPanel1a;
	}


	/** Switch the visualized object between the text field and the tree.
	 * @param aComp the aComp
	 * @param bComp the bComp */
	private void switcher(final JScrollPane aComp, final JScrollPane bComp) {
		if(node.isBoundToWidget()) { return; }
		if(aComp.isVisible()) {
			STParserTree b2Comp = (STParserTree)bComp.getViewport().getView(); 
			aComp.setVisible(false);
			b2Comp.parse(((TokenAwareEditor)aComp.getViewport().getView()).getPlainText());
			b2Comp.expandTree();
			bComp.setVisible(true);
			bComp.repaint();
		} else {
			aComp.setVisible(true);
			bComp.setVisible(false);
			aComp.repaint();
		}
		pack();
	}


	/** Get the checkbox for setting whether is node is global.
	 * @return checkbox */
	protected final JCheckBox getCheckGlobal() {
		if(checkGlobal == null) {
			checkGlobal = new JCheckBox(STGraphC.getMessage("NODE.DIALOG.ISGLOBAL") + STTools.QUESTION); //$NON-NLS-1$
			checkGlobal.setToolTipText(STGraphC.getMessage("NODE.DIALOG.ISGLOBAL.TT")); //$NON-NLS-1$
			checkGlobal.addKeyListener(new KeyAdapter() { public void keyReleased(final KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; } } });
			checkGlobal.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { setDirty(true); } });
		}
		return checkGlobal;
	}


	/** Get the checkbox for setting whether is node is output.
	 * @return checkbox */
	protected final JCheckBox getCheckOut() {
		if(checkOut == null) {
			checkOut = new JCheckBox(STGraphC.getMessage("NODE.DIALOG.ISOUTPUT") + STTools.QUESTION); //$NON-NLS-1$
			checkOut.setToolTipText(STGraphC.getMessage("NODE.DIALOG.ISOUTPUT.TT")); //$NON-NLS-1$
			checkOut.addKeyListener(new KeyAdapter() { public void keyReleased(final KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; } } });
			checkOut.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) {
				setDirty(true);
				getCheckVectorOutput().setEnabled(checkOut.isSelected());
			} });
		}
		return checkOut;
	}


	/** Get the checkbox for setting whether is node is vector output.
	 * @return checkbox */
	private JCheckBox getCheckVectorOutput() {
		if(checkVectorOutput == null) {
			checkVectorOutput = new JCheckBox(STGraphC.getMessage("NODE.DIALOG.ISVECTOROUTPUT") + STTools.QUESTION); //$NON-NLS-1$
			checkVectorOutput.setToolTipText(STGraphC.getMessage("NODE.DIALOG.ISVECTOROUTPUT.TT")); //$NON-NLS-1$
			checkVectorOutput.addKeyListener(new KeyAdapter() { public void keyReleased(final KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; } } });
			checkVectorOutput.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { setDirty(true); } });
		}
		return checkVectorOutput;
	}


	/** Initialize listNodeType.
	 * @return alignment */
	@SuppressWarnings("unchecked")
	private JComboBox getListNodeTypes() {
		if(listNodeTypes == null) {
			listNodeTypes = new JComboBox(STGraphC.getMessage("NODE.DIALOG.NODETYPES").split(STTools.COMMA)); //$NON-NLS-1$
			listNodeTypes.setToolTipText(STGraphC.getMessage("NODE.DIALOG.NODETYPES.TT")); //$NON-NLS-1$
			listNodeTypes.setEditable(false);
			listNodeTypes.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					int selItem = listNodeTypes.getSelectedIndex();
					setDirty(true);
					setFieldVisibility(selItem);
					setVarsInList();
					setVisible(true); // prevent losing focus after selection
					requestFocus();
				}
			});
		}
		return listNodeTypes;
	}


	/** Control the visibility of the fields.
	 * @param x the x */
	private void setFieldVisibility(final int x) {
		getListNodeTypes().setSelectedIndex(x);
		if(x == 0) {
			labInit.setVisible(false); getTextInit().setVisible(false);
			labTrans.setVisible(false); labTrans2.setVisible(false); getTextTrans().setVisible(false); getTextTrans2().setVisible(false);
			labExpr.setVisible(true); labExpr2.setVisible(true); getTextExpr().setVisible(true); getTextExpr2().setVisible(false);
		} else if(x == 1) {
			labInit.setVisible(true); getTextInit().setVisible(true);
			labTrans.setVisible(true); labTrans2.setVisible(true); getTextTrans().setVisible(true); getTextTrans2().setVisible(false);
			labExpr.setVisible(false); labExpr2.setVisible(false); getTextExpr().setVisible(false); getTextExpr2().setVisible(false);
		} else {
			labInit.setVisible(true); getTextInit().setVisible(true);
			labTrans.setVisible(true); labTrans2.setVisible(true); getTextTrans().setVisible(true); getTextTrans2().setVisible(false);
			labExpr.setVisible(true); labExpr2.setVisible(true); getTextExpr().setVisible(true); getTextExpr2().setVisible(false);
		}
		pack();
		ValueNode n = (ValueNode)node;
		if(n.getValueNodeType() != x) {
			n.setValueNodeType(x);
			node.getGraph().setupModel();
		}
	}


	/** Get the textInit.
	 * @return pane */
	private JScrollPane getTextInit() {
		if(textInit == null) {
			textInit = new JScrollPane();
			textInit.setName("myTextInit");
			textInit.setMinimumSize(new Dimension(100, 30));
			textInit.setPreferredSize(new Dimension(100, 50));
			textInit.setMaximumSize(new Dimension(100, 90));
			textInit.setViewportView(getTextInitb());
		}
		return textInit;
	}


	/** Get the textInitb.
	 * @return editor */
	private TokenAwareEditor getTextInitb() {
		if(textInitb == null) {
			textInitb = new TokenAwareEditor(null, getHelpText());
			textInitb.setToolTipText(STGraphC.getMessage("NODE.DIALOG.INITSTATE.TT")); //$NON-NLS-1$
			textInitb.addFocusListener(new MyFocusAdapter(textInitb));
			textInitb.addKeyListener(new MyKeyAdapter(textInitb));
			textInitb.addMouseListener(new MyMouseAdapter(textInitb));
		}
		return textInitb;
	}


	/** Get the textTrans.
	 * @return pane */
	private JScrollPane getTextTrans() {
		if(textTrans == null) {
			textTrans = new JScrollPane();
			textTrans.setPreferredSize(new Dimension(100, 100));
			textTrans.setViewportView(getTextTransb());
		}
		return textTrans;
	}


	/** Get the textTransb.
	 * @return editor */
	private TokenAwareEditor getTextTransb() {
		if(textTransb == null) {
			textTransb = new TokenAwareEditor(null, getHelpText());
			textTransb.setToolTipText(STGraphC.getMessage("NODE.DIALOG.TRANSSTATE.TT")); //$NON-NLS-1$
			textTransb.addFocusListener(new MyFocusAdapter(textTransb));
			textTransb.addKeyListener(new MyKeyAdapter(textTransb));
			textTransb.addMouseListener(new MyMouseAdapter(textTransb));
		}
		return textTransb;
	}


	/** Get the textTrans2.
	 * @return pane */
	private JScrollPane getTextTrans2() {
		if(textTrans2 == null) {
			textTrans2 = new JScrollPane();
			textTrans2.setPreferredSize(new Dimension(100, 100));
			textTrans2.setViewportView(getTextTrans2b());
		}
		return textTrans2;
	}


	/** Get the textTrans2b.
	 * @return tree */
	private STParserTree getTextTrans2b() {
		if(textTrans2b == null) { textTrans2b = new STParserTree(null); }
		return textTrans2b;
	}


	/** Get the textExpr.
	 * @return pane */
	private JScrollPane getTextExpr() {
		if(textExpr == null) {
			textExpr = new JScrollPane();
			textExpr.setPreferredSize(new Dimension(100, 100));
			textExpr.setViewportView(getTextExprb());
		}
		return textExpr;
	}


	/** Get the textExprb.
	 * @return editor */
	private TokenAwareEditor getTextExprb() {
		if(textExprb == null) {
			textExprb = new TokenAwareEditor(null, getHelpText());
			textExprb.setToolTipText(STGraphC.getMessage("NODE.DIALOG.OUTEXPR.TT")); //$NON-NLS-1$
			textExprb.addFocusListener(new MyFocusAdapter(textExprb));
			textExprb.addKeyListener(new MyKeyAdapter(textExprb));
			textExprb.addMouseListener(new MyMouseAdapter(textExprb));
		}
		return textExprb;
	}


	/** Get the textExpr2.
	 * @return pane */
	private JScrollPane getTextExpr2() {
		if(textExpr2 == null) {
			textExpr2 = new JScrollPane();
			textExpr2.setPreferredSize(new Dimension(100, 100));
			textExpr2.setViewportView(getTextExpr2b());
		}
		return textExpr2;
	}


	/** Get the textExpr2b.
	 * @return tree */
	private STParserTree getTextExpr2b() {
		if(textExpr2b == null) { textExpr2b = new STParserTree(null); }
		return textExpr2b;
	}


	/** Mouse adapter for custom editors. */
	private class MyMouseAdapter extends MouseAdapter {
		TokenAwareEditor o;
		MyMouseAdapter(final TokenAwareEditor o) { this.o = o; }

		public void mouseClicked(final MouseEvent e) { showHelpOnToken(o); }
	}


	/** Focus adapter for custom editors. */
	private class MyFocusAdapter extends FocusAdapter {
		TokenAwareEditor o;
		MyFocusAdapter(TokenAwareEditor o) { this.o = o; }

		public void focusGained(final FocusEvent e) {
			currentField = o;
			EditDialog.setPreviousContent(o.getText());
		}

	}


	/** Key adapter for custom editors. */
	private class MyKeyAdapter extends KeyAdapter {
		TokenAwareEditor o;
		MyKeyAdapter(final TokenAwareEditor o) { this.o = o; }

		public void keyReleased(final KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_TAB) {
				if(!e.isShiftDown()) { o.transferFocus(); }
				else { o.transferFocusBackward(); }
			}
			else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { 
				if(o.isAutoCompleteVisible()) {
					o.setAutoCompleteVisible(false);
				} else {
					setVisible(false);
				}
				return;
			}
			else if(e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
				if(o.isAutoCompleteVisible()) {
					o.setAutoCompleteVisible(false);
				} else {
					getButtonOk().doClick();
				}
				return;
			}
			else if(e.getKeyCode() == 17) { ; }
			else if(e.getKeyCode() == KeyEvent.VK_E && e.isControlDown()) {
				STInterpreter interpreter = node.getGraph().getInterpreter();
				if(node.isState()) { interpreter.addVariable(STInterpreter.THIS, textInitb.onTheFlyEvaluate(interpreter, false)); } // by hypothesis, 'this' is initialized to the node initial value
				if(node.isStateWithOutput()) { interpreter.addVariable(STInterpreter.ME, textTransb.onTheFlyEvaluate(interpreter, false)); } // by hypothesis, 'me' is initialized to the node initial state transition value
				helpText.setText((String)o.onTheFlyEvaluate(interpreter, true));
				helpText2.setText((String)o.onTheFlyEvaluate(interpreter, true));
			} else if(e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
				STInterpreter interpreter = node.getGraph().getInterpreter();
				if(node.isState()) { interpreter.addVariable(STInterpreter.THIS, textInitb.onTheFlyEvaluate(interpreter, false)); }
				if(node.isStateWithOutput()) { interpreter.addVariable(STInterpreter.ME, textTransb.onTheFlyEvaluate(interpreter, false)); }
				if(o.getCurrentTokenType() == TokenAwareEditor.TOKENTYPE_VAR && o.getCurrentToken() != null) {
					helpText.setText((String)o.onTheFlyEvaluate(interpreter, o.getCurrentToken(), true));
					helpText2.setText((String)o.onTheFlyEvaluate(interpreter, o.getCurrentToken(), true));
				} else {
					helpText.setText(STTools.BLANK);
					helpText2.setText(STTools.BLANK);
				}
			} else {
				showHelpOnToken(o);
				if(!(o.getText().equals(EditDialog.getPreviousContent()))) { setDirty(true); }
			}
		}	
	}


	private void showHelpOnToken(final TokenAwareEditor o) {
		String token = o.getCurrentToken();
		if(STTools.isEmpty(token)) {
			helpText.setText(STTools.BLANK);
			helpText2.setText(STTools.BLANK);
			return;
		}
		int type = o.getCurrentTokenType();
		if(type == TokenAwareEditor.TOKENTYPE_FUN) {
			String s = STInterpreter.getFunctionDescription(token);
			helpText.setMessage(!STTools.isEmpty(s) ? s : STTools.BLANK);
			helpText2.setMessage(!STTools.isEmpty(s) ? s : STTools.BLANK);
			return;
		}
		if(type == TokenAwareEditor.TOKENTYPE_VAR) {
			STNode n = node.getGraph().getNodeByName(token);
			if(n != null) {
				helpText.setMessage(n.getCName() + "<br>" + (STTools.isEmpty(n.getDescription()) ? STTools.BLANK : n.getDescription())); //$NON-NLS-1$
				helpText2.setMessage(n.getCName() + "<br>" + (STTools.isEmpty(n.getDescription()) ? STTools.BLANK : n.getDescription())); //$NON-NLS-1$
			} else if(STInterpreter.getSystemVars(true, true).contains(token)) {
				try {
					helpText.setMessage(STGraphC.getMessage("SYSVAR." + token.toUpperCase())); //$NON-NLS-1$ 
					helpText2.setMessage(STGraphC.getMessage("SYSVAR." + token.toUpperCase())); //$NON-NLS-1$ 
				} catch (Exception e) {
					helpText.setText(STTools.BLANK);
					helpText2.setText(STTools.BLANK);
				}
			} else {
				helpText.setText(STTools.BLANK);
				helpText2.setText(STTools.BLANK);
			}
			return;
		}
		helpText.setText(STTools.BLANK);
		helpText2.setText(STTools.BLANK);
	}


	//******************************* dynamic handling *************************************


	/** Open the dialog, that had been created in a static way.
	 * @param node the node */
	public final void open(final STNode thenode) {
		if(getTabbedPanel().getComponentAt(0) == contentPanel1a) {
			getTabbedPanel().setComponentAt(0, contentPanel1);
			getTabbedPanel().repaint();
		}
		node = thenode;
		startInit();
		numberFormatLabel.setVisible(true);
		getNumberFormatField().setVisible(true);
		STInterpreter interpreter = node.getGraph().getInterpreter();
		textInitb.setInterpreter(interpreter);
		textInitb.setParams(node, STNode.PROP_INITSTATE);
		textInitb.setHTMLText(interpreter, STTools.BLANK);
		textTransb.setInterpreter(interpreter);
		textTransb.setParams(node, STNode.PROP_STATETRANSITION);
		textTransb.setHTMLText(interpreter, STTools.BLANK);
		textTrans2b.setGraph(node.getGraph());
		textExprb.setInterpreter(interpreter);
		textExprb.setParams(node, STNode.PROP_EXPRESSION);
		textExprb.setHTMLText(interpreter, STTools.BLANK);
		textExpr2b.setGraph(node.getGraph());
		boolean b = node.getGraph().isEditable;
		getListNodeTypes().setEnabled(b); 
		getCheckGlobal().setEnabled(b); getCheckOut().setEnabled(b); getCheckVectorOutput().setEnabled(b);
		getTextInitb().setEnabled(b); getTextTransb().setEnabled(b); getTextExprb().setEnabled(b);
		fill();
		completeInit(activeTab);
	}


	/** Fill the dialog with the configuration data taken from the node properties. */
	public final void fill() {
		super.fill();

		if(!node.getGraph().getTopGraph().isForWeb() && (getTabbedPanel().getTitleAt(getTabbedPanel().getTabCount() - 1)).equals(STGraphC.getMessage("NODE.DIALOG.TAB4"))) { //$NON-NLS-1$
			getTabbedPanel().removeTabAt(getTabbedPanel().getTabCount() - 1);
		} else if(node.getGraph().getTopGraph().isForWeb() && !(getTabbedPanel().getTitleAt(getTabbedPanel().getTabCount() - 1)).equals(STGraphC.getMessage("NODE.DIALOG.TAB4"))) { //$NON-NLS-1$
			getTabbedPanel().addTab(STGraphC.getMessage("NODE.DIALOG.TAB4"), getContentPanel4()); //$NON-NLS-1$
		}

		String s = null;
		ValueNode n = (ValueNode)node;
		int x = n.getValueNodeType();
		setFieldVisibility(x);
		getCheckGlobal().setSelected(node.isGlobal());
		getCheckGlobal().setEnabled(node.getGraph().isEditable);
		getCheckOut().setSelected(node.isOutput());
		getCheckVectorOutput().setSelected(node.isVectorOutput());
		getCheckVectorOutput().setEnabled(node.getGraph().isEditable && getCheckOut().isEnabled() && getCheckOut().isSelected());

		setVarsInList();

		STInterpreter interpreter = node.getGraph().getInterpreter();
		if(x != ValueNode.VALUENODE_ALGEBRAIC) {
			s = n.getFormattedStateInit(); ////if(STTools.isEmpty(s)) { s = n.getStateInit(); }
			if(s != null) { textInitb.setHTMLText(interpreter, s.trim()); }
			s = n.getFormattedStateTransition(); ////if(STTools.isEmpty(s)) { s = n.getStateTransition(); }
			if(s != null) { textTransb.setHTMLText(interpreter, s.trim()); }
		}
		if(x != ValueNode.VALUENODE_STATE) {
			s = n.getFormattedExpression(); ////if(STTools.isEmpty(s)) { s = n.getExpression(); }
			if(s != null) { textExprb.setHTMLText(interpreter, s.trim()); }
		}

		if(node.isBoundToWidget()) { // it cannot be but an algebraic node
			textExprb.setEnabled(false);
			textExprb.setText(STGraphC.getMessage("NODE.DIALOG.SETBYWIDGET")); //$NON-NLS-1$
		}

		currentField = (x == ValueNode.VALUENODE_ALGEBRAIC) ? getTextExprb() : getTextInitb();
	}


	/** Action for the OK button.
	 * @param mode the mode, either <code>MODE_OK</code> or <code>MODE_APPLY</code> */
	@Override
	public final void okHandler(final int mode) {
		if(!startExit(getCheckOut().isSelected())) {
			STGraphC.setFocus();
			return;
		}
		if(getTabbedPanel().getComponentAt(0) == contentPanel1a) { currEd.setHTMLText(ed.getInterpreter(), ed.getPlainText(true)); }
		String result = null;
		ValueNode n = (ValueNode)node;
		int x = listNodeTypes.getSelectedIndex();
		if(n.getValueNodeType() != x) { n.setValueNodeType(x); }

		n.setGlobal(getCheckGlobal().isSelected());
		n.setOutput(getCheckOut().isSelected());
		n.setVectorOutput(getCheckVectorOutput().isSelected());

		boolean isInitStateValid = true;
		if(x != 0) {
			isInitStateValid = false;
			String t = getTextInitb().getQuotedText(false);
			if(!STTools.isEmpty(t)) {
				if((result = n.checkExpressionDefinition(node.getGraph().getInterpreter(), t, STNode.PROP_INITSTATE)) != null) {
					helpText.setErrorMessage(result);
				} else {
					t = n.cleanExpression(t);
					n.setStateInit(t);
					n.setFormattedStateInit(getTextInitb().getQuotedText(true));
					isInitStateValid = true;
				}
			} else {
				n.setStateInit(STTools.BLANK);
				n.setFormattedStateInit(STTools.BLANK);
			}
		}

		boolean isTransValid = true;
		if(x != 0) {
			isTransValid = false;
			String t2 = getTextTransb().getQuotedText(false);
			if(!STTools.isEmpty(t2)) {
				if((result = n.checkExpressionDefinition(node.getGraph().getInterpreter(), t2, STNode.PROP_STATETRANSITION)) != null) {
					helpText.setErrorMessage(result);
				} else {
					t2 = n.cleanExpression(t2);
					n.setStateTransition(t2);
					n.setFormattedStateTransition(getTextTransb().getQuotedText(true));
					isTransValid = true;
				}
			} else {
				n.setStateTransition(STTools.BLANK);
				n.setFormattedStateTransition(STTools.BLANK);
			}
		}

		boolean isExprValid = true;
		if(x != 1 && !node.isBoundToWidget()) {
			isExprValid = false;
			String t3 = getTextExprb().getQuotedText(false);
			if(!STTools.isEmpty(t3)) {
				if((result = n.checkExpressionDefinition(node.getGraph().getInterpreter(), t3, STNode.PROP_EXPRESSION)) != null) {
					helpText.setErrorMessage(result);
				} else {
					t3 = n.cleanExpression(t3);
					n.setExpression(t3);
					n.setFormattedExpression(getTextExprb().getQuotedText(true));
					isExprValid = true;

					ArrayList<STNode> definingNodes = node.getExpressionVarNodes(node.getGraph().getInterpreter(), t3);
					if(definingNodes != null) {
						for(STNode nn : definingNodes) {
							if(nn != null && nn.isGlobal()) { n.setInput(false); }
						}
					}
				}
			} else {
				n.setExpression(STTools.BLANK);
				n.setFormattedExpression(STTools.BLANK);
			}
		}
		node.setInput();
		node.setValid(isInitStateValid && isTransValid && isExprValid);
		completeExit(mode);
	}

}
