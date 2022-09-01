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

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.SwingConstants;

import javax.swing.JLabel;


/** Dialog for running manual garbage collector. */
@SuppressWarnings("serial")
public class STGC extends JDialog {
	/** The content pane. */
	private JPanel jContentPane = null;
	/** The result1. */
	private JLabel result1 = null;
	/** The result2. */
	private JLabel result2 = null;
	/** The button exec. */
	private JButton buttonExec = null;
	/** The free mem. */
	private long freeMem = 0;


	/** Class constructor. */
	public STGC() {
		super();
		setTitle(STGraphC.getMessage("GC.DIALOG.TITLE")); //$NON-NLS-1$
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setModal(true);
		open();
	}


	/** Initialize and open the dialog. */
	private void open() {
		setContentPane(getJContentPane());
		showData(freeMem = Runtime.getRuntime().freeMemory(), Runtime.getRuntime().maxMemory(), 0);
		pack();
		setLocation(STGraph.getSTC().getX() + 50, STGraph.getSTC().getY() + 50);
		setVisible(true);
	}


	/** Initialize jContentPane.
	 * @return panel */
	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel(new BorderLayout());
			// result field 1
			jContentPane.add(result1 = new JLabel(), java.awt.BorderLayout.NORTH);
			result1.setHorizontalAlignment(SwingConstants.CENTER);
			// result field 2
			jContentPane.add(result2 = new JLabel(), java.awt.BorderLayout.CENTER);
			result2.setHorizontalAlignment(SwingConstants.CENTER);
			// exec button
			jContentPane.add(getButtonExec(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}


	/** Show data resulting from the execution of garbage collection.
	 * @param free the free
	 * @param max the max
	 * @param freed the freed */
	final void showData(final long free, final long max, final long freed) {
		final long toMB = 1048576;
		result1.setText(STGraphC.getMessage("GC.DIALOG.FREE") + ": " + (free / toMB) + " / " + (max / toMB)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		result2.setText(STGraphC.getMessage("GC.DIALOG.FREED") + ": " + (freed / toMB)); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/** Initialize buttonExec.
	 * @return button */
	private JButton getButtonExec() {
		if(buttonExec == null) {
			buttonExec = new JButton();
			buttonExec.setText(STGraphC.getMessage("GC.DIALOG.EXEC")); //$NON-NLS-1$
			buttonExec.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					long temp = freeMem;
					Runtime.getRuntime().gc();
					showData(freeMem = Runtime.getRuntime().freeMemory(), Runtime.getRuntime().maxMemory(), freeMem - temp);
				}
			});
		}
		return buttonExec;
	}

}
