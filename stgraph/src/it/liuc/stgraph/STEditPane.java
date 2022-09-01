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

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/** Generate and control an edit pane to let the user edit some properties of the selected node. */
@SuppressWarnings("serial")
public class STEditPane extends JPanel implements ComponentListener {


	/** Class constructor. */
	public STEditPane() {
		super();
		addComponentListener(this);
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		JPanel editPane = new JPanel();
		editPane.setLayout(new BoxLayout(editPane, BoxLayout.PAGE_AXIS));
		editPane.setBackground(Color.WHITE);

		scrollPane.setViewportView(editPane);
	}


	public void componentResized(ComponentEvent e) { STGraph.getSTC().componentResized(null); }

	public void componentHidden(ComponentEvent e) { ; }

	public void componentMoved(ComponentEvent e) { ; }

	public void componentShown(ComponentEvent e) { ; }

}
