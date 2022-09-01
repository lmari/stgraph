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
import it.liuc.stgraph.STGraphImpl;

import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.action.FileOpen;
import it.liuc.stgraph.node.STEdge;
import it.liuc.stgraph.node.STNode;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


/** Generate and control a window which is able to generically display some data. */
@SuppressWarnings("serial")
public class InfoDialog extends JFrame {
	/** The information area. */
	private JTextPane informationArea;
	/** The information text. */
	private StringBuilder informationText;
	/** The hyperlink mode. */
	private int hyperlinkMode = 0;
	/** Hyperlinks are dealt with to datafiles. */
	public final static int MODE_DATAFILES = 0;
	/** Hyperlinks are dealt with to nodes. */
	public final static int MODE_NODES = 1;


	/** Create a dialog. */
	public InfoDialog() {
		super();
		setResizable(true);
		setSize(new Dimension(400, 300));
		setIconImage(STGraphC.getSystemIcon());
		setAlwaysOnTop(true);
		JPanel infoPanel = new JPanel();
		getContentPane().add(infoPanel);
		informationArea = new JTextPane();
		infoPanel.setLayout(new BorderLayout());
		infoPanel.add(new JScrollPane(informationArea), BorderLayout.CENTER);
		informationArea.setContentType("text/html"); //$NON-NLS-1$
		informationArea.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, 12));
		informationArea.setCaretPosition(0);
		informationArea.setEditable(false);

		informationArea.addHyperlinkListener(new HyperlinkListener() { 
			public void hyperlinkUpdate(final HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else if(e.getEventType() == HyperlinkEvent.EventType.EXITED) {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				} else if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if(hyperlinkMode == MODE_DATAFILES) {
						if(e.getDescription().endsWith(STConfigurator.getProperty("FILEEXT.STANDARD"))) { //$NON-NLS-1$
							String fileName = "datafiles/" + e.getDescription(); //$NON-NLS-1$
							FileOpen.opener(fileName, true, false);
						} else {
							setTitle(STTools.BLANK);
							informationArea.setText(STTools.file2HTMLString(e.getDescription()));
							informationArea.setCaretPosition(0);
						}
					} else if(hyperlinkMode == MODE_NODES) {
						String s = e.getDescription();
						if(!s.contains(STTools.DASH)) {
							STNode n = STGraph.getSTC().getCurrentGraph().getNodeByName(s);
							if(n != null) { n.select(true); }
						} else {
							String[] ss = s.split(STTools.DASH);
							STGraphImpl graph = STGraph.getSTC().getCurrentGraph();
							STNode n1 = graph.getNodeByName(ss[0]);
							STNode n2 = graph.getNodeByName(ss[1]);
							if(n1 != null && n2 != null) {
								STEdge edge = graph.getEdge(n1, n2);
								if(edge != null) { edge.select(graph); }
							}
						}
					}
				}
			}
		});

		JButton okButton = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png"))); //$NON-NLS-1$ //$NON-NLS-2$
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		infoPanel.add(buttonPanel, BorderLayout.SOUTH);
		okButton.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { setVisible(false); } });

		setLocation(STGraphC.getContainer().getX() + 50, STGraphC.getContainer().getY() + 50);
	}


	/** Display the specified data in a window, optionally opening it.
	 * @param title the title
	 * @param text the text
	 * @param show the show
	 * @param mode the mode */
	public void showMe(final String title, final String text, final boolean show, final int mode) {
		this.hyperlinkMode = mode;
		showMe(title, text, show);
	}


	/** Display the specified data in a window, optionally opening it.
	 * @param title the title
	 * @param text the text
	 * @param show the show */
	public void showMe(final String title, final String text, final boolean show) {
		setData(title, text);
		if(show) { setVisible(true); }
	}


	/** Set the specified title and text for this dialog.
	 * @param title the title
	 * @param text the text */
	public final void setData(final String title, String text) {
		setTitle(title);
		informationText = new StringBuilder();
		informationArea.setText(text = informationText.append(text).toString());
		informationArea.setCaretPosition(0);
	}


	/** Set the specified text for this dialog.
	 * @param text the text */
	public final void setData(final String text) {
		informationText = new StringBuilder();
		informationArea.setText(informationText.append(text).toString());
	}


	/** Append the specified text for this dialog.
	 * @param text the text
	 * @param withCR with CR? */
	public final void appendData(final String text, final boolean withCR) {
		if(STTools.isEmpty(text)) { return; }
		if(withCR) {
			informationArea.setText(informationText.append("<br>" + text).toString()); //$NON-NLS-1$
		} else {
			informationArea.setText(informationText.append(text).toString());
		}
	}

}
