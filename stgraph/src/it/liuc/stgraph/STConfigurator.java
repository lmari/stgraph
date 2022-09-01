/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2021, Luca Mari.
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

import it.liuc.stgraph.action.FileOpenRecent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JMenuItem;


/** User configuration handler.
 * (see also the file <code>stgraph.basic.properties</code>) */
public final class STConfigurator {
	/** The properties. */
	private static Properties properties = new Properties();
	/** The property keys to be written to the user configuration file (there must be an entry in). */
	private static final String[] KEYS = { "LOCALE", "AUTOUPDATE", "LAF", "ICONSET", "TABPOSITION",
		"GRAPH.ANTIALIASED", "GRAPH.DOUBLEBUFFERED",
		"GRAPH.SHOWNODEVALUES", "GRAPH.SHOWEDGELABELS", 
		"GRAPH.GRIDVISIBLE", "GRAPH.GRIDENABLED", "GRAPH.GRIDSIZE",
		"SHOWNODETOOLTIP", "HIGHLIGHTEDGES",
		"EDITOR.FONTSIZE", "MENU.FONTSIZE", "MENU.ICONSIZE", "TOOLBAR.ICONSIZE", "ZOOMWIDGETS"
	};
	/** The number of listed recent files: if changed also actions.spring.xml.properties must be updated. */
	private static int numFiles = 6;


	/** Class constructor. */
	private STConfigurator() { ; }


	/** Get the properties.
	 * @return properties */
	public static Properties getProperties() { return properties; }


	/** Set a property.
	 * @param key the key
	 * @param value the value */
	public static void setProperty(final String key, final String value) { properties.put(key, value); }


	/** Get a property.
	 * @param key the key
	 * @return property */
	public static String getProperty(final String key) {
		if(key == null) { return null; }
		Object result = properties.get(key);
		if(result == null) { return null; }
		return result.toString();
	}


	/** Read the properties from the resource and the user configuration files.
	 * @return properties */
	public static Properties read() {
		InputStream in = null;
		try {
			in = STGraphC.class.getClassLoader().getResource("datafiles/stgraph.basic.properties").openStream(); //$NON-NLS-1$	
			properties.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { in.close(); } catch (Exception e) { ; }
		}
		Properties configProps = new Properties();
		try {
			in = new FileInputStream("config.properties"); //$NON-NLS-1$	
			configProps.load(in);
		} catch (Exception e) {
			;
		} finally {
			try { in.close(); } catch (Exception e) { ; }
		}
		properties.putAll(configProps);
		return properties;
	}


	/** Write the properties to the user configuration files. */
	public static void write() {
		OutputStream out = null;
		Properties p = new Properties();
		String s;
		for(String key : KEYS) {
			if((s = getProperty(key)) != null) { p.setProperty(key, s); }
		}
		String fileName;
		for(int i = 1; i <= numFiles; i++) {
			fileName = getProperty("RECENTFILE" + i); //$NON-NLS-1$
			if(fileName != null) { p.setProperty("RECENTFILE" + i, fileName); } //$NON-NLS-1$
		}
		try {
			out = new FileOutputStream("config.properties"); //$NON-NLS-1$
			p.store(out, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { out.close(); } catch (Exception e) { ; }
		}
	}


	/** Add the named file to the recently opened list,
	 * or resort the list if the file is already there.
	 * @param name the name */
	public static void addFile(final String name) {
		if(name.equals(getProperty("RECENTFILE1"))) { return; } //$NON-NLS-1$
		for(int i = 2; i <= numFiles; i++) {
			if(name.equals(getProperty("RECENTFILE" + i))) { //$NON-NLS-1$
				for(int j = numFiles; j > 1; j--) {
					if(j <= i) { setProperty("RECENTFILE" + j, getProperty("RECENTFILE" + (j - 1))); } //$NON-NLS-1$ //$NON-NLS-2$
				}
				setProperty("RECENTFILE1", name); //$NON-NLS-1$
				return;
			}
		}
		String fileName;
		for(int i = numFiles - 1; i >= 1; i--) {
			fileName = getProperty("RECENTFILE" + i); //$NON-NLS-1$
			if(fileName != null) { setProperty("RECENTFILE" + (i + 1), fileName); } //$NON-NLS-1$
		}
		setProperty("RECENTFILE1", name); //$NON-NLS-1$
	}


	/** Repaint the recently opened list. */
	public static void repaintRecentItems() {
		String fileName;
		JMenuItem jmi;
		for(int i = 1; i <= numFiles; i++) {
			fileName = getProperty("RECENTFILE" + i); //$NON-NLS-1$
			jmi = ((FileOpenRecent)STGraphC.getContext().getBean("actionFileOpenRecent" + i)).getMenuItem(false); //$NON-NLS-1$
			if(fileName != null) {
				jmi.setText(fileName);
				jmi.setVisible(true);
			} else {
				jmi.setVisible(false);
			}
		}
	}

}
