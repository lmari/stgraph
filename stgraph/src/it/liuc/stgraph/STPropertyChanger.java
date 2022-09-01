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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STColorChooser;
import it.liuc.stgraph.util.STTools;

import javax.swing.JLabel;


/** Property changer. */
public class STPropertyChanger extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The jContentPane. */
	private JPanel jContentPane = null;
	/** The editor. */
	private JTextField editor = null;
	/** The result. */
	private JLabel result = null;


	/** Class constructor. */
	public STPropertyChanger() {
		super();
		setTitle(STGraphC.getMessage("CHANGER.DIALOG.TITLE")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setModal(false);
		setAlwaysOnTop(true);
		open();
	}


	/** Initialize and open the dialog. */
	private void open() {
		setContentPane(getJContentPane());
		pack();
		setLocation(STGraphC.getContainer().getX() + 50, STGraphC.getContainer().getY() + 50);
		setVisible(true);
	}


	/** Initialize jContentPane.
	 * @return panel */
	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel(new BorderLayout());
			// editor field
			jContentPane.add(getEditor(), BorderLayout.CENTER);
			// result field
			jContentPane.add(result = new JLabel(STTools.SPACE), BorderLayout.SOUTH);
			result.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return jContentPane;
	}


	/** Initialize the editor.
	 * @return editor */
	private JTextField getEditor() {
		if(editor == null) {
			editor = new JTextField("select all do nothing"); //default text //$NON-NLS-1$
			editor.setPreferredSize(new Dimension(200, 40));
			String s = "select {all|current|name=[*]x[*]|forecol=x|backcol=x|cprop=x} do {nothing|forecol=x|backcol=x|cprop=[x]}"; //$NON-NLS-1$
			editor.setToolTipText(s);
			editor.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						String t = editor.getText().trim();
						if(!t.equals(STTools.BLANK)) { exec(t); }
					}
				}
			});
		}
		return editor;
	}


	/** Exec method.
	 * <br>Deal with expressions of the form:
	 * <br><code>select <i>x</i> do <i>y</i></code> (note that spaces are used as token separators!)
	 * <br>where <code><i>x</i></code> is a <b>condition</b> to select zero or more nodes in the current graph,
	 * <br>and <code><i>x</i></code> is an <b>action</b> to execute on the selected nodes.
	 * <br>The recognized conditions are:
	 * <li><code>all</code>: select all nodes
	 * <li><code>current</code>: maintain the current selection
	 * <li><code>name=<i>expr</i></code>: select all nodes whose name matches <code><i>expr</i></code>, which can start or end with the wild char '*' 
	 * <li><code>forecol=<i>expr</i></code>: select all nodes whose foreground color is <code><i>expr</i></code>
	 * <li><code>backcol=<i>expr</i></code>: select all nodes whose background color is <code><i>expr</i></code>
	 * <li><code><i>cprop</i>=<i>expr</i></code>: select all nodes whose custom property of name <i>cprop</i> has value <code><i>expr</i></code>
	 * <br>The recognized action are:
	 * <li><code>nothing</code>: do nothing
	 * <li><code>forecol=<i>expr</i></code>: set foreground color to <code><i>expr</i></code>
	 * <li><code>backcol=<i>expr</i></code>: set background color to <code><i>expr</i></code>
	 * <li><code><i>cprop</i>=<i>[expr]</i></code>: set custom property of name <i>cprop</i> to value <code><i>expr</i></code>, or reset it if <i>expr</i> is not present
	 * @param expr the expr */
	final void exec(final String expr) {
		String[] tokens = expr.split(STTools.SPACE);
		if(tokens.length != 4) { result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR1")); return; }
		if(!tokens[0].equalsIgnoreCase("SELECT")) { result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR2")); return; }
		if(!tokens[2].equalsIgnoreCase("DO")) { result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR3")); return; }
		String condition = tokens[1];
		String action = tokens[3];
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		STNode[] nodes = graph.getAllNodes();

		// *** condition handling ***
		if(condition.equals("current")) {
			for(Object cell : graph.getSelectionCells()) {
				if(!(cell instanceof STNode)) { graph.removeSelectionCell(cell); }
			}
		} else {
			graph.clearSelection();
			if(condition.equals("all")) {
				graph.setSelectionCells(nodes);
			} else if(condition.startsWith("name=")) {
				String name = condition.substring(5);
				if(name.indexOf("*") == -1) {
					graph.setSelectionCell(graph.getNodeByName(name));
				} else {
					if(name.startsWith("*")) {
						name = name.substring(1);
						for(STNode node : nodes) {
							if(node.getName().endsWith(name)) { graph.addSelectionCell(node); }
						}
					} else if(name.endsWith("*")) {
						name = name.substring(0, name.length() - 1);
						for(STNode node : nodes) {
							if(node.getName().startsWith(name)) { graph.addSelectionCell(node); }
						}
					}
				}
			} else if(condition.startsWith("forecol=")) {
				Color c = STColorChooser.myColorMap.get(condition.substring(8));
				if(c == null) { result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR4")); return; }
				for(STNode node : nodes) {
					if(node.getForeColor().equals(c)) { graph.addSelectionCell(node); }
				}
			} else if(condition.startsWith("backcol=")) {
				Color c = STColorChooser.myColorMap.get(condition.substring(8));
				if(c == null) { result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR4")); return; }
				for(STNode node : nodes) {
					if(node.getBackColor().equals(c)) { graph.addSelectionCell(node); }
				}
			} else {
				String[] cProp = condition.split("=");
				if(cProp.length != 2) { result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR5")); return; }
				String value;
				for(STNode node : nodes) {
					value = node.getCProperty(cProp[0]);
					if(value != null && (cProp[1].equals("*") || value.equals(cProp[1]))) { graph.addSelectionCell(node); }
				}
			}
		}

		int numSelected = graph.getSelectionCount();
		if(numSelected == 0) { result.setText(STGraphC.getMessage("DIALOG.OK") + STTools.SPACE + STTools.OPENP + numSelected + STTools.SPACE + STGraphC.getMessage("CHANGER.DIALOG.SELECTED") + STTools.CLOSEP); return; }
		nodes = new STNode[graph.getSelectionCells().length];
		for(int i = 0; i < graph.getSelectionCells().length; i++) { nodes[i] = (STNode)graph.getSelectionCells()[i]; } 

		// *** action handling ***
		if(action.equals("nothing")) {
		} else if(action.startsWith("forecol=")) {
			Color c = STColorChooser.myColorMap.get(action.substring(8));
			if(c == null) { result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR4")); return; }
			for(STNode node : nodes) { node.setForeColor(c); }
		} else if(action.startsWith("backcol=")) {
			Color c = STColorChooser.myColorMap.get(action.substring(8));
			if(c == null) { result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR4")); return; }
			for(STNode node : nodes) { node.setBackColor(c); }
		} else {
			if(action.indexOf("=") == -1) {
				result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR6")); return;
			}
			String[] cProp = action.split("=");
			if(cProp.length == 1) {
				for(STNode node : nodes) { node.setCProperty(cProp[0], STTools.BLANK);
				}
			} else if(cProp.length == 2) {
				for(STNode node : nodes) { node.setCProperty(cProp[0], cProp[1]); }
			} else {
				result.setText(STGraphC.getMessage("CHANGER.DIALOG.ERROR6")); return;
			}
		}
		graph.clearSelection();
		graph.setSelectionCells(nodes);
		result.setText(STGraphC.getMessage("DIALOG.OK") + STTools.SPACE + STTools.OPENP + numSelected + STTools.SPACE + STGraphC.getMessage("CHANGER.DIALOG.SELECTED") + STTools.CLOSEP);
		if(numSelected > 0) { graph.setAsModified(true); }
	}

}
