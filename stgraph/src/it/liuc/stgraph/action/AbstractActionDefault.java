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

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STToolBar;
import it.liuc.stgraph.util.STTools;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;


/** Superclass for all actions tied to menubar or toolbar. */
@SuppressWarnings("serial")
public abstract class AbstractActionDefault extends AbstractAction {
	/** The id. */
	protected String id;
	/** The key stroke. */
	private KeyStroke keyStroke;
	/** The menu item. */
	protected JMenuItem menuItem;
	/** The toolbar button. */
	private JButton barButton = null;
	/** The icon1. */
	private ImageIcon icon1;
	/** The icon2. */
	private ImageIcon icon2;
	/** The visibility status for toolbar buttons. */
	private boolean visible;


	/** Class constructor. */
	public AbstractActionDefault() {
		id = STTools.stripPackageName(getClass().getName());
		STGraphC.getActions().put(id, this);
		setVisible(true);
	}


	/** Class constructor.
	 * @param modifiers the modifiers
	 * @param keyChar the key char */
	public AbstractActionDefault(final String keyChar, final int modifiers) {
		this();
		if(keyChar.equals("+")) { //$NON-NLS-1$
			keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, modifiers);
		} else if(keyChar.equals("-")) { //$NON-NLS-1$
			keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, modifiers);
		} else if(keyChar.length() == 1) { // single character
			keyStroke = KeyStroke.getKeyStroke(keyChar.charAt(0), modifiers);
		} else if(keyChar.startsWith("F")) { // function key //$NON-NLS-1$
			keyStroke = KeyStroke.getKeyStroke(111 + Integer.parseInt(keyChar.substring(1)), modifiers);
		} else if(keyChar.equals("DELETE")) { //$NON-NLS-1$
			keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, modifiers);
		}
	}


	/** Class constructor.
	 * @param keyStroke the key stroke */
	public AbstractActionDefault(final KeyStroke keyStroke) {
		this();
		this.keyStroke = keyStroke;
	}


	/** Control the enabling/disabling of the action on the basis of the system state.
	 * This default behavior -- the action is enabled if there is a current editable model --
	 * is possibly overridden by subclasses. */
	public void setEnabledOnState() {
		STGraphC stc = STGraph.getSTC();
		setEnabled(stc.isCurrentDesktop() && stc.getCurrentGraph().isEditable);
	}


	/** Create and return a version of this action suitable for menu insertion.
	 * This default behavior -- the action is associated to an icon menu item --
	 * is possibly overridden by subclasses by simply setting the parameter to false.
	 * @return component */
	public Component prepareForMenu() { return getMenuItem(true); }


	/** Create and return a version of this action suitable for toolbar insertion.
	 * This default behavior -- the action is associated to a toolbar button when the container is not an applet --
	 * is possibly overridden by subclasses.
	 * @return version */
	public Component prepareForToolBar() { 
		if(!STGraphC.isApplet()) { return getToolBarButton(); }
		return null;
	}


	/** Return a button tied to this action.
	 * @return button */
	public final JButton getToolBarButton() {
		if(barButton != null) { return barButton; }
		barButton = new JButton(this);
		String s = id.toUpperCase() + ".png"; //$NON-NLS-1$
		try {
			ImageIcon imageIcon = new ImageIcon(STGraph.getSTC().getSpecificIcon(s));
			int iconSize = imageIcon.getIconWidth(); // as a default
			try {
				iconSize = Integer.parseInt(STConfigurator.getProperty("TOOLBAR.ICONSIZE")); //$NON-NLS-1$
			} catch (Exception e) { ; }
			barButton.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(iconSize, iconSize, java.awt.Image.SCALE_SMOOTH)));
		} catch (Exception e) {
			System.err.println("AbstractActionDefault.getToolBarButton(): error while loading image " + s); //$NON-NLS-1$
		}
		s = id.toUpperCase() + "_TOOLTIP"; //$NON-NLS-1$
		try {
			barButton.setToolTipText(STGraphC.getMessage(s));
		} catch (Exception e) {
			System.err.println("AbstractActionDefault.getToolBarButton(): error while loading message " + s); //$NON-NLS-1$
		}
		barButton.setBorder(null);
		return barButton;
	}


	/** Return a switching button tied to this action.
	 * @return button */
	public final JButton getToolBarSwitchButton() {
		if(barButton != null) { return barButton; }
		barButton = new JButton(this);
		String s = id.toUpperCase() + "2.png"; //$NON-NLS-1$
		try {
			ImageIcon imageIcon = new ImageIcon(STGraph.getSTC().getSpecificIcon(s));
			int iconSize = imageIcon.getIconWidth(); // as a default
			try {
				iconSize = Integer.parseInt(STConfigurator.getProperty("TOOLBAR.ICONSIZE")); //$NON-NLS-1$
			} catch (Exception e) { ; }
			icon2 = new ImageIcon(imageIcon.getImage().getScaledInstance(iconSize, iconSize, java.awt.Image.SCALE_SMOOTH));
		} catch (Exception e) {
			System.err.println("AbstractActionDefault.getToolBarSwitchButton(): error while loading image " + s); //$NON-NLS-1$
		}
		s = id.toUpperCase() + "1.png"; //$NON-NLS-1$
		try {
			ImageIcon imageIcon = new ImageIcon(STGraph.getSTC().getSpecificIcon(s));
			int iconSize = imageIcon.getIconWidth(); // as a default
			try {
				iconSize = Integer.parseInt(STConfigurator.getProperty("TOOLBAR.ICONSIZE")); //$NON-NLS-1$
			} catch (Exception e) { ; }
			barButton.setIcon(icon1 = new ImageIcon(imageIcon.getImage().getScaledInstance(iconSize, iconSize, java.awt.Image.SCALE_SMOOTH)));
		} catch (Exception e) {
			System.err.println("AbstractActionDefault.getToolBarSwitchButton(): error while loading image " + s); //$NON-NLS-1$
		}
		s = id.toUpperCase() + "_TOOLTIP"; //$NON-NLS-1$
		try {
			barButton.setToolTipText(STGraphC.getMessage(s));
		} catch (Exception e) {
			System.err.println("AbstractActionDefault.getToolBarSwitchButton(): error while loading message " + s); //$NON-NLS-1$
		}
		barButton.setBorder(null);
		return barButton;
	}


	/** Return a button tied to this action, or null if it does not exist.
	 * @return button */
	public final JButton getToolBarButton2() { return barButton; }


	/** For switching buttons, set the first (default) icon. */
	public final void setIcon1() { putValue(Action.SMALL_ICON, icon1); }


	/** For switching buttons, set the second (alternate) icon. */
	public final void setIcon2() { putValue(Action.SMALL_ICON, icon2); }


	/** Return the menu item tied to this action.
	 * @param withIcon the with icon
	 * @return item */
	public final JMenuItem getMenuItem(final boolean withIcon) {
		if(menuItem != null) { return menuItem; }
		menuItem = new JMenuItem();
		menuItem.addActionListener(this);
		menuItem.setAction(this);
		String s = id.toUpperCase() + "_MENUTEXT"; //$NON-NLS-1$
		try {
			menuItem.setText(STGraphC.getMessage(s));
		} catch (Exception e) {
			System.err.println("AbstractActionDefault.getMenuItem(): error while loading message " + s); //$NON-NLS-1$
		}
		int itemSize = menuItem.getFont().getSize(); // as a default
		try {
			itemSize = Integer.parseInt(STConfigurator.getProperty("MENU.FONTSIZE")); //$NON-NLS-1$
		} catch (Exception e) { ; }
				menuItem.setFont(new Font(STGraph.getMyFont(), Font.PLAIN, itemSize));
		
		if(keyStroke != null) { menuItem.setAccelerator(keyStroke); }
		if(withIcon) {
			s = id.toUpperCase() + ".png"; //$NON-NLS-1$
			try {
				ImageIcon imageIcon = new ImageIcon(STGraph.getSTC().getMenuIcon(s));
				int iconSize = imageIcon.getIconWidth(); // as a default
				try {
					iconSize = Integer.parseInt(STConfigurator.getProperty("MENU.ICONSIZE")); //$NON-NLS-1$
				} catch (Exception e) { ; }
				menuItem.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(iconSize, iconSize, java.awt.Image.SCALE_SMOOTH)));
			} catch (Exception e) {
				System.err.println("AbstractActionDefault.getMenuItem(): error while loading image " + s); //$NON-NLS-1$
			}
		}
		menuItem.addMenuKeyListener(STMenuKeyListener.getInstance());
		return menuItem;
	}


	/** Action method. */
	public void exec() { return; }


	/** Interceptor, acting as a proxy for help menu and to allow correct key interaction in the status bar.
	 * @param e the e
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */
	public void actionPerformed(final ActionEvent e) {
		if(STGraphC.getContainer() instanceof JFrame) {
			Component c1 = ((JFrame)STGraphC.getContainer()).getFocusOwner();
			if(c1 == null) { return; }
			Component c2 = c1.getParent();
			if(c1 instanceof STGraphExec || c2 instanceof JFrame || c2 instanceof STToolBar) {
				if(STGraphC.isHelp()) {
					String s;
					try {
						s = STGraphC.getMessage(id.toUpperCase() + "_HELP"); //$NON-NLS-1$
					} catch (Exception ex) {
						s = "<h1>" + id + "</h1>" + STGraphC.getMessage("SYSTEM.HELP.NOTFOUND"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					STTools.infoTextDialog.showMe(STGraphC.getMessage("SYSTEM.HELP.TITLE") + ": " + id, s, true); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					AbstractButton ab = (AbstractButton)e.getSource();
					((AbstractActionDefault)ab.getAction()).exec();
				}
			}
		} else {
			AbstractButton ab = (AbstractButton)e.getSource();
			((AbstractActionDefault)ab.getAction()).exec();
		}
		/*
		if(STGraphC.isHelp()) {
			String s;
			try {
				s = STGraphC.getMessage(id.toUpperCase() + "_HELP"); //$NON-NLS-1$
			} catch (Exception ex) {
				s = "<h1>" + id + "</h1>" + STGraphC.getMessage("SYSTEM.HELP.NOTFOUND"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
            STTools.infoTextDialog.showMe(STGraphC.getMessage("SYSTEM.HELP.TITLE") + ": " + id, s, true); //$NON-NLS-1$ //$NON-NLS-2$
		} else if(STGraphC.getStatusBar().isWithFocus()) {
			; // nothing to do: just prevent calling menu items...
		} else {
			AbstractButton ab = (AbstractButton)e.getSource();
			((AbstractActionDefault)ab.getAction()).exec();
		}
		 */
	}


	/** Set the visibility status for the toolbar button possibly controlling this action.
	 * @param visible the visibility state */
	public void setVisible(final boolean visible) { this.visible = visible; }


	/** Get the visibility state for the toolbar button possibly controlling this action.
	 * @return the visible */
	public boolean isVisible() { return visible; }

}
