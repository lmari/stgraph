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

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;

import org.nfunk.jep.type.Tensor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class STGraphX {
	private STGraph app;
	private STGraphExec graph;
	private STInterpreter interpreter;
	private JFrame frame;
	private JTextField textField;
	private JTextArea textArea;
	private ArrayList<String> buffer;
	private int bufferPointer;
	private final int BUFFER_SIZE = 10;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					STGraphX window = new STGraphX();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public STGraphX() { initialize(); }

	private void initialize() {
		app = new STGraph(false, null, null, false);
		new STDesktop(null, null, false, null, null);
		graph = STGraph.getSTC().getCurrentGraph();
		interpreter = graph.getInterpreter();
		ValueNode node = (ValueNode)STFactory.nodeCreate(null, STNode.NODE_VALUE);
		node.setValueNodeType(ValueNode.VALUENODE_ALGEBRAIC);
		node.setExpression("0"); //$NON-NLS-1$
		buffer = new ArrayList<String>();
		bufferPointer = 0;

		frame = new JFrame("STGraphX"); //$NON-NLS-1$
		frame.setBounds(100, 100, 500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridBagLayout());

		JLabel label = new JLabel(">"); //$NON-NLS-1$
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.NORTHWEST;
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		frame.getContentPane().add(label, gbc_label);

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_UP) {
					if(bufferPointer > 0) { textField.setText(buffer.get(--bufferPointer)); }
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					if(bufferPointer < buffer.size() - 1) { textField.setText(buffer.get(++bufferPointer)); }
					else if(bufferPointer == buffer.size() - 1) { textField.setText(""); ++bufferPointer; } //$NON-NLS-1$
				}
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					handleInput(textField.getText());
					bufferPointer = buffer.size();
				}
			}
		});
		textField.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		frame.getContentPane().add(textField, gbc_textField);

		JScrollPane scroll = new JScrollPane();
		GridBagConstraints gbc_scroll = new GridBagConstraints();
		gbc_scroll.weightx = 1.0;
		gbc_scroll.weighty = 1.0;
		gbc_scroll.anchor = GridBagConstraints.NORTH;
		gbc_scroll.fill = GridBagConstraints.BOTH;
		gbc_scroll.gridx = 0;
		gbc_scroll.gridy = 1;
		gbc_scroll.gridwidth = 2;
		frame.getContentPane().add(scroll, gbc_scroll);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scroll.setViewportView(textArea);
	}

	private void handleInput(String expr) {
		//STNode node;
		Object val;
		expr = expr.trim();
		if(expr.length() == 0) { return; }
		if(expr.equals("quit")) { System.exit(0); } //$NON-NLS-1$
		if(expr.equals("clear")) { //$NON-NLS-1$
			textArea.setText(STTools.BLANK);
			textField.setText(STTools.BLANK);
			return;
		}
		if(expr.equals("run")) { //$NON-NLS-1$
			graph.setLists();
			graph.setupModel();
			app.exec();
			textArea.append("run: ok\n\n"); //$NON-NLS-1$
			textField.setText(STTools.BLANK);
			return;
		}
		textArea.append(expr + '\n');
		int x = expr.indexOf("="); //$NON-NLS-1$
		if(x != -1 && expr.length() > (x + 1) && expr.charAt(x + 1) != '=') {
			//node = STFactory.nodeCreate(expr.substring(0, x), STNode.NODE_VALUE);
			val = exec(expr.substring(x + 1));
			if(!interpreter.hasError()) {
				//node.setValue(val);
				String name = expr.substring(0, x);
				if(name.equals("time0")) { graph.setTime0(((Tensor)val).getValue()); setTimeData(); } //$NON-NLS-1$
				else if(name.equals("time1")) { graph.setTime1(((Tensor)val).getValue()); setTimeData(); } //$NON-NLS-1$
				else if(name.equals("timeD")) { graph.setTimeD(((Tensor)val).getValue()); setTimeData(); } //$NON-NLS-1$
				interpreter.addVariable(name, val);
			} else {
				//graph.getModel().remove(new Object[]{node});
			}
		} else {
			val = exec(expr);
		}
		if(!interpreter.hasError()) {
			textArea.append("ok: " + val.toString() + "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			textArea.append("err: " + STTools.replaceCRwithSpace(interpreter.getErrorInfo()) + "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		textField.setText(STTools.BLANK);
		buffer.add(expr);
		if(buffer.size() == BUFFER_SIZE) { buffer.remove(0); }
	}

	private Object exec(final String expr) {
		interpreter.parseExpression(expr);
		return interpreter.getValueAsObject();
	}

	private void setTimeData() {
		interpreter.addVariable("numSteps", graph.computeNumSteps()); //$NON-NLS-1$
		graph.setTimeVectors();		
	}

}
