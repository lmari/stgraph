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
package it.liuc.stgraph.action;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.util.STTools;


/** Help About action. */
public class HelpAbout extends AbstractActionDefault {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JDialog dialog = null;


	/** Default class constructor. */
	public HelpAbout() { ; }


	/** Control the enabling/disabling of the action on the basis of the system state. */
	@Override
	public final void setEnabledOnState() { setEnabled(true); }


	/** Action method. */
	@Override
	public final void exec() {
		if(dialog == null) {
			dialog = new JDialog();
			dialog.setModal(true);
			dialog.setAlwaysOnTop(true);
			dialog.setResizable(false);

			Box box = Box.createVerticalBox();
			box.setOpaque(true);
			box.setBackground(Color.WHITE);
			JLabel image = new JLabel(new ImageIcon(ClassLoader.getSystemResource(STGraph.ICON_PATH)));
			image.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(image);
			JLabel motto = new JLabel(STGraph.MOTTO);
			motto.setFont(new Font(STGraph.getMyFont(), Font.ITALIC, 16));
			motto.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(motto);
			JLabel author = new JLabel(STGraph.AUTHOR);
			author.setFont(new Font(STGraph.getMyFont(), Font.BOLD, 12));
			author.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(author);
			JLabel sep1 = new JLabel(STTools.SPACE);
			sep1.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(sep1);
			JLabel info1 = new JLabel(STGraphC.getMessage("SYSTEM.ABOUT1")); //$NON-NLS-1$
			info1.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(info1);
			JLabel info2 = new JLabel(STTools.SPACE + STTools.SPACE + STGraphC.getMessage("SYSTEM.ABOUT2") + STTools.SPACE + STTools.SPACE); //$NON-NLS-1$
			info2.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(info2);
			JLabel info3 = new JLabel(STGraphC.getMessage("SYSTEM.ABOUT3")); //$NON-NLS-1$
			info3.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(info3);
			JLabel sep2 = new JLabel(STTools.SPACE);
			sep2.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(sep2);
			JButton okButton = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			okButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { dialog.setVisible(false); } });
			okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(okButton);
			JLabel sep3 = new JLabel(STTools.SPACE);
			sep3.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(sep3);

			dialog.add(box, BorderLayout.CENTER);
			dialog.pack();
		}
		Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		dialog.setLocation(p.x - dialog.getWidth() / 2, p.y - dialog.getHeight() /2);
		dialog.setVisible(true);
	}

}
