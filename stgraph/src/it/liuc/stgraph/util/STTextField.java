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

import it.liuc.stgraph.node.EditPanel4Nodes;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JTextField;


/** Define a custom text field. */
@SuppressWarnings("serial")
public class STTextField extends JTextField {


	/** Class constructor.
	 * @param dialog the dialog
	 * @param forNodes switch to define whether this is used in a dialog for node definition */
	public STTextField(final JDialog dialog, final boolean forNodes) {
		super();
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLoweredBevelBorder());

		addKeyListener(new KeyAdapter() {
			public void keyReleased(final KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dialog.setVisible(false);
					return;
				}
				if(forNodes && !(getText().equals(EditDialog.getPreviousContent()))) {
					if(dialog instanceof EditPanel4Nodes) {
						((EditPanel4Nodes)dialog).setDirty(true);
					} else {
						EditDialog.setDirty(true);
					}
				}
			}
		});

		addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent arg0) {
				if(forNodes) { EditDialog.setPreviousContent(getText()); }
			}
		});
	}

}
