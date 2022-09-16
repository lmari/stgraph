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

import it.liuc.stgraph.action.AbstractActionDefault;

import it.liuc.stgraph.action.EditRedo;
import it.liuc.stgraph.action.EditUndo;
import it.liuc.stgraph.action.FileClose;
import it.liuc.stgraph.action.FileOpen;
import it.liuc.stgraph.action.FileSave;
import it.liuc.stgraph.action.FileSaveAs;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.CNameComparator;
import it.liuc.stgraph.util.DataDictionaryComparator;
import it.liuc.stgraph.util.FileDrop;
import it.liuc.stgraph.util.GroupListDialog;
import it.liuc.stgraph.util.InfoDialog;
import it.liuc.stgraph.util.NodeNameComparator;
import it.liuc.stgraph.util.NodeListDialog;
import it.liuc.stgraph.util.ParserCheckVisitor;
import it.liuc.stgraph.util.PriorityComparator;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.util.StringNameComparator;
import it.liuc.stgraph.util.WidgetListDialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;

import net.infonode.gui.laf.InfoNodeLookAndFeel;
import net.infonode.gui.laf.InfoNodeLookAndFeelTheme;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.graph.GraphUndoManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/** Main application / applet container. */
@SuppressWarnings("serial")
public class STGraphC extends Container implements ChangeListener, ComponentListener, WindowListener {
	/** The context. */
	private static transient ApplicationContext context;
	/** The container. */
	private static Container container;
	/** The locale. */
	private static Locale locale;
	/** The visible. */
	private boolean visible;
	/** The multi desktop. */
	private static JTabbedPane multiDesktop;
	/** The split pane 1. */
	//private static JSplitPane splitPane1;
	/** The edit pane. */
	//private static STEditPane editPane;
	/** The split pane 2. */
	private static JSplitPane splitPane2;
	/** The list pane. */
	private static STListPane listPane;
	/** The menu bar. */
	private static STMenuBar menuBar;
	/** The tool bar. */
	private static STToolBar toolBar;
	/** The status bar. */
	private static STStatusBar statusBar;
	/** The current desktop. */
	private STDesktop currentDesktop;
	/** The current graph. */
	private STGraphExec currentGraph;
	/** The currently computed graph. */
	private STGraphExec currentlyComputedGraph;
	/** The currently computed node. */
	private STNode currentlyComputedNode;
	/** The current dir. */
	private File currentDir;
	/** The show node tooltips. */
	private static boolean showNodeTooltips = true; // just a default
	/** The highlight edges. */
	private static boolean highlightEdges = false; // just a default
	/** The zoom widgets. */
	private static boolean zoomWidgets = true; // just a default
	/** The in clip. */
	private boolean inClip;
	/** The undo manager. */
	private static GraphUndoManager undoManager;
	/** The logger root. */
	private transient Logger loggerRoot;
	/** The logger ui. */
	private transient Logger loggerUI;
	/** The logger file. */
	private transient Logger loggerFile;
	/** The logger run. */
	private transient Logger loggerRun;
	/** The logger exec. */
	private transient Logger loggerExec;
	/** The system icon. */
	private static transient Image systemIcon;
	/** The basic props. */
	private static Properties basicProps;
	/** Is the user interaction currently set to contextual help?. */
	private static boolean help;
	/** The actions. */
	private static HashMap<String, AbstractActionDefault> actions = new HashMap<String, AbstractActionDefault>();
	/** Is pasting?. */
	public boolean isPasting = false;
	/** Is loading?. */
	public boolean isLoading = false;
	/** Is submodel loading?. */
	public boolean isSubLoading = false;


	/** Return the Spring context for the application.
	 * @return context */
	public static final ApplicationContext getContext() { return context; }

	/** Get the ST locale.
	 * @return the ST locale */
	public static Locale getSTLocale() { return locale; }

	/** Return the main container in which the program is executed.
	 * It is either a JFrame (for application execution) or a JApplet (for applet execution).
	 * @return container */
	public static final Container getContainer() { return container; }

	/** Return the basic properties.
	 * @return basic props */
	public static final Properties getBasicProps() { return basicProps; }

	/** Set the logger root. */
	public final void setLoggerRoot() { loggerRoot = Logger.getLogger("root"); } //$NON-NLS-1$

	/** Get the logger root.
	 * @return the logger root */
	public final Logger getLoggerRoot() { return loggerRoot; }

	/** Set the logger UI. */
	public final void setLoggerUI() { loggerUI = Logger.getLogger("ui"); } //$NON-NLS-1$

	/** Get the logger UI.
	 * @return the logger UI */
	public final Logger getLoggerUI() { return loggerUI; }

	/** Set the logger file. */
	public final void setLoggerFile() { loggerFile = Logger.getLogger("file"); } //$NON-NLS-1$

	/** Get the logger file.
	 * @return the logger file */
	public final Logger getLoggerFile() { return loggerFile; }

	/** Sets the logger run. */
	public final void setLoggerRun() { loggerRun = Logger.getLogger("run"); } //$NON-NLS-1$

	/** Get the logger run.
	 * @return the logger run */
	public final Logger getLoggerRun() { return loggerRun; }

	/** Set the logger exec. */
	public final void setLoggerExec() { loggerExec = Logger.getLogger("exec"); } //$NON-NLS-1$

	/** Get the logger exec.
	 * @return the logger exec */
	public final Logger getLoggerExec() { return loggerExec; }


	/** Check if is applet.
	 * @return true, if is applet */
	public static final boolean isApplet() { return false; } // container instanceof JApplet; }


	/** Check if is network.
	 * @return true, if is network */
	public final boolean isNetwork() {
		if(container instanceof JFrame) { return false; }
		/*
		String theFile1 = ((JApplet)container).getParameter("filename"); //$NON-NLS-1$
		if(theFile1 == null) { return false; }
		URL theFile2 = getClass().getClassLoader().getResource(theFile1);
		if(theFile2 == null) { return false; }
		if(theFile2.getProtocol().equals("http")) { return true; } //$NON-NLS-1$
		*/
		return false;
	}


	/** Check if is visible.
	 * @return true, if is visible */
	public final boolean isVisible() { return visible; }


	/** Get the multi desktop.
	 * @return the multi desktop */
	public static JTabbedPane getMultiDesktop() { return multiDesktop; }


	/** Get the my menu bar.
	 * @return the my menu bar */
	public static STMenuBar getMyMenuBar() { return menuBar; }


	/** Get the tool bar.
	 * @return the tool bar */
	public static STToolBar getToolBar() { return toolBar; }


	/** Get the status bar.
	 * @return the status bar */
	public static STStatusBar getStatusBar() { return statusBar; }


	/** Set the show node tooltips.
	 * @param showNodeTooltips the switch */
	public static boolean setShowNodeTooltips(final boolean showNodeTooltips) { return STGraphC.showNodeTooltips = showNodeTooltips ; }


	/** Get the show node tooltips.
	 * @return the switch */
	public static boolean isShowNodeTooltips() { return showNodeTooltips; }


	/** Set the highlight edges.
	 * @param highlightEdges the switch */
	public static boolean setHighlightEdges(final boolean highlightEdges) { return STGraphC.highlightEdges = highlightEdges ; }


	/** Get the highlight edges.
	 * @return the switch */
	public static boolean isHighlightEdges() { return highlightEdges; }


	/** Set the zoom widgets.
	 * @param zoomwidgets the switch */
	public static boolean setZoomWidgets(final boolean zoomwidgets) { return STGraphC.zoomWidgets = zoomwidgets ; }


	/** Get the zoom widgets.
	 * @return the switch */
	public static boolean isZoomWidgets() { return zoomWidgets; }


	/** Set the current desktop, to properly (re)set the required properties.
	 * @param desk the desk */
	public final void setCurrentDesktop(final STDesktop desk) {
		if(desk != null) {
			currentDesktop = desk;
			if(desk.getGraph() != null) { currentGraph = desk.getGraph(); }
			getLoggerUI().info("calling maximizeDesktop..."); //$NON-NLS-1$
			STTools.maximizeDesktop();
			if(currentGraph != null) {
				currentGraph.clearSelection();
				if(!currentGraph.isTopGraph()) { currentGraph.setAsEditable(false); } // subgraphs should never be editable...
				if(statusBar != null) {
					if(currentGraph.getLastError() == null) { statusBar.setInfoText(STTools.BLANK, 0); }
					else { statusBar.setInfoText(currentGraph.getLastError(), 1); }
					statusBar.setControlVisibile(false);
				}
				boolean t = currentGraph.isEditable;
				currentGraph.setEditable(t); currentGraph.setMoveable(t); currentGraph.setBendable(t);
				currentGraph.setCloneable(t); currentGraph.setSizeable(t); currentGraph.setConnectable(t); currentGraph.setDisconnectable(t);

				if(menuBar != null) { menuBar.getMenu(STMenuBar.WEBMENU_NUM).setVisible(currentGraph.isForWeb()); }
			}
			if(STTools.infoDataDialog.isVisible() && currentGraph != null) { STTools.infoDataDialog.showMe(STGraphC.getMessage("SYSTEM.DIALOG.INFOTITLE"), currentGraph.toString(), false); } //$NON-NLS-1$
			if(STTools.outputVarsDialog.isVisible()) { STTools.outputVarsDialog.showMe(STGraphC.getMessage("SYSTEM.DIALOG.OUTPUTVARS"), STTools.BLANK, false); } //$NON-NLS-1$
		} else {
			currentDesktop = null;
			currentGraph = null;
			if(statusBar != null) { statusBar.setInfoText(STTools.BLANK, 0); }
			if(STTools.infoDataDialog.isVisible()) { STTools.infoDataDialog.showMe(STGraphC.getMessage("NODE.DIALOG.SHOWTITLE"), STTools.BLANK, false); } //$NON-NLS-1$
			if(STTools.outputVarsDialog.isVisible()) { STTools.outputVarsDialog.showMe(STGraphC.getMessage("SYSTEM.DIALOG.OUTPUTVARS"), STTools.BLANK, false); } //$NON-NLS-1$
		}
		refreshBars();
		if(WidgetListDialog.isDialogVisible()) { WidgetListDialog.showDialog(); }
		if(NodeListDialog.isDialogVisible()) { NodeListDialog.showDialog(); }
		if(GroupListDialog.isDialogVisible()) { GroupListDialog.showDialog(); }

    	STListPane.setLists();
	}


	/** Get the current desktop.
	 * @return the current desktop */
	public final STDesktop getCurrentDesktop() { return currentDesktop; }

	/** Check if is current desktop.
	 * @return true, if is current desktop */
	public final boolean isCurrentDesktop() { return multiDesktop.getSelectedIndex() != -1; }

	/** Set the current graph.
	 * @param graph the graph */
	public final void setCurrentGraph(final STGraphExec graph) { currentGraph = graph; }

	/** Get the current graph.
	 * @return the current graph */
	public final STGraphExec getCurrentGraph() { return currentGraph; }

	/** Set the currently computed graph.
	 * @param graph the graph */
	public final void setCurrentlyComputedGraph(final STGraphExec graph) { currentlyComputedGraph = graph; }

	/** Get the currently computed graph.
	 * @return the currently computed graph */
	public final STGraphExec getCurrentlyComputedGraph() { return currentlyComputedGraph; }

	/** Set the currently computed node.
	 * @param node the node */
	public final void setCurrentlyComputedNode(final STNode node) { currentlyComputedNode = node; }

	/** Get the currently computed node.
	 * @return the currently computed node */
	public final STNode getCurrentlyComputedNode() { return currentlyComputedNode; }

	/** Set the current dir.
	 * @param dir the dir */
	public final void setCurrentDir(final File dir) { currentDir = dir; }

	/** Get the current dir.
	 * @return the current dir */
	public final File getCurrentDir() { return currentDir; }

	/** Trigger the event of graph changed for the current graph. */
	public final void signalCurrentGraphAsModified() { if(currentDesktop != null && currentDesktop.getGraphFrame1() != null) { currentDesktop.getGraphFrame1().graphChanged(new GraphModelEvent(this, ((STModel)STGraph.getSTC().getCurrentGraph().getModel()).new GraphModelEdit(null, null, null, null, null))); } }

	/** Set whether something is in the clipboard.
	 * @param ic the ic */
	public final void setInClip(final boolean ic) { inClip = ic; }

	/** Check if is in clip.
	 * @return true, if is in clip */
	public final boolean isInClip() { return inClip; }

	/** Get the undo manager.
	 * @return the undo manager */
	public static GraphUndoManager getUndoManager() { return undoManager; }


	/** Get the priority comparator.
	 * @return the priority comparator */
	public final PriorityComparator getPriorityComparator() { return (PriorityComparator)context.getBean("priorityComparator"); } //$NON-NLS-1$


	/** Get the node name comparator.
	 * @return the node name comparator */
	public final NodeNameComparator getNodeNameComparator() { return (NodeNameComparator)context.getBean("nodeNameComparator"); } //$NON-NLS-1$


	/** Get the string name comparator.
	 * @return the string name comparator */
	public final StringNameComparator getStringNameComparator() { return (StringNameComparator)context.getBean("stringNameComparator"); } //$NON-NLS-1$


	/** Get the descriptive name comparator.
	 * @return the name comparator */
	public final CNameComparator getCNameComparator() { return (CNameComparator)context.getBean("cNameComparator"); } //$NON-NLS-1$


	/** Get the data dictionary comparator.
	 * @return the data dictionary comparator */
	public final DataDictionaryComparator getDataDictionaryComparator() { return (DataDictionaryComparator)context.getBean("DataDictionaryComparator"); } //$NON-NLS-1$


	/** Get the expression parser.
	 * @return the expression parser */
	public final ParserCheckVisitor getExpressionParser() { return (ParserCheckVisitor)context.getBean("parserCheckVisitor"); } //$NON-NLS-1$


	/** Get the specified base icon.
	 * @param id the id
	 * @return the URL */
	public final URL getBaseIcon(final String id) { return getClass().getClassLoader().getResource("icons/base/" + id); } //$NON-NLS-1$


	/** Get the specified icon for a menu item.
	 * @param id the id
	 * @return the URL */
	public final URL getMenuIcon(final String id) { return getClass().getClassLoader().getResource("icons/menuset/" + id); } //$NON-NLS-1$


	/** Get the specified default icon.
	 * @param id the id
	 * @return the URL */
	public final URL getDefaultIcon(final String id) { return getClass().getClassLoader().getResource("icons/set0/" + id); } //$NON-NLS-1$


	/** Set the specified specific icon.
	 * @param id the id
	 * @return the URL */
	public final URL getSpecificIcon(final String id) {
		URL u = getClass().getClassLoader().getResource("icons/" + basicProps.getProperty("ICONSET") + STTools.SLASH + id); //$NON-NLS-1$ //$NON-NLS-2$
		if(u != null) { return u; }
		return getDefaultIcon(id);
	}


	/** Get the system icon.
	 * @return the system icon */
	public static final Image getSystemIcon() { return systemIcon; }


	/** Get the message.
	 * @param id the id
	 * @return the message */
	public static String getMessage(final String id) { return context.getMessage(id, new Object[0], locale); }


	/** Get the message.
	 * @param id the id
	 * @param locale the locale
	 * @return the message */
	public static String getMessage(final String id, final String locale) { return context.getMessage(id, new Object[0], getLocale(locale)); }


	/** Get available languages for UI.
	 * @return the languages */
	public static String[] getUILanguages() { return STConfigurator.getProperty("LANGUAGES.UI").split(","); } //$NON-NLS-1$ //$NON-NLS-2$


	/** Get the locale corresponding to the specified string.
	 * @param locale the string containing the locale, e.g., "en" or "it"
	 * @return locale, or the default locale is the string is invalid */
	public static Locale getLocale(final String locale) {
		if(STTools.isEmpty(locale)) { return getDefaultLocale(); }
		for(String l : getUILanguages()) {
			if(locale.equals(l)) { return new Locale(l); }
		}
		return getDefaultLocale();
	}


	/** Get the default locale.
	 * @return the default locale */
	public static Locale getDefaultLocale() { return new Locale(STConfigurator.getProperty("LANGUAGES.UI.DEFAULT")); } // Locale.ENGLISH; } //$NON-NLS-1$


	/** Get the actions.
	 * @return the actions */
	public static HashMap<String, AbstractActionDefault> getActions() { return actions; }


	/** Get the action.
	 * @param action the action
	 * @return the action */
	public static AbstractActionDefault getAction(final String action) { return actions.get(action); }


	/** Class constructor. */
	public STGraphC() { ; }


	/** Class constructor.
	 * @param _container the _container */
	public STGraphC(final Container _container) {
		super();
		container = _container;
		try { // read before Spring configuration, so to filter out its logging
			java.util.logging.LogManager.getLogManager().readConfiguration(getClass().getClassLoader().getResource("datafiles/stgraph.logging.properties").openStream());
		} catch (Exception e) { e.printStackTrace(); }
		Logger.getLogger("org.springframework"); // linked and therefore made it controllable by the configuration file
		setLoggerRoot(); setLoggerUI(); setLoggerFile(); setLoggerRun(); setLoggerExec();

		String[] f = new String[4];
		f[0] = "datafiles/stgraph.spring.xml.properties";
		f[1] = "datafiles/interpreter.spring.xml.properties";
		f[2] = "datafiles/actions.base.spring.xml.properties";
		if(STGraph.getExecLevel() == STGraph.EXECLEVEL_EASY) {
			f[3] = "datafiles/actions.easy.spring.xml.properties";
		} else if(STGraph.getExecLevel() == STGraph.EXECLEVEL_WEB) {
			f[3] = "datafiles/actions.web.spring.xml.properties";
		} else {
			f[3] = "datafiles/actions.default.spring.xml.properties";
		}
		context = new ClassPathXmlApplicationContext(f);
	}


	/** Sets the preferences.
	 * @param visible the visible
	 * @param locale the locale */
	public final void setPreferences(final boolean visible, final String locale) {
    	System.out.println("Setting preferences..."); //$NON-NLS-1$
		this.visible = visible;
		basicProps = STConfigurator.read();
		//System.out.println("--> " + basicProps.getProperty("SYSTEM.NAME"));

		STGraphC.locale = getDefaultLocale(); // default language
		if(!STTools.isEmpty(locale)) {
			for(String l : getUILanguages()) {
				if(locale.equals(l)) { basicProps.setProperty("LOCALE", l); }
			}
		}
		String l = basicProps.getProperty("LOCALE"); //$NON-NLS-1$
		for(String ll : getUILanguages()) {
			if(l.equals(ll)) { STGraphC.locale = new Locale(ll); }
		}

		try {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			Toolkit.getDefaultToolkit().setDynamicLayout(true);
			System.setProperty("sun.awt.noerasebackground", "true");

			/**/
			String s = basicProps.getProperty("UIFALLBACK");
			STGraph.fallback = (s != null && s.equals("true"));
			
			boolean found = false;
			if(!STGraph.fallback) {
			    for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			        if(info.getName().equals("Nimbus")) {
			            UIManager.setLookAndFeel(info.getClassName());
			            found = true;
			            break;
			        }
			    }
			}
			if(!found) { // if Nimbus is not available or a fallback is forced, let us set the good old LaF
				UIManager.setLookAndFeel(new InfoNodeLookAndFeel(new InfoNodeLookAndFeelTheme()));
			}
			/**/

			//UIManager.setLookAndFeel(new InfoNodeLookAndFeel(new InfoNodeLookAndFeelTheme()));

			s = basicProps.getProperty("SHOWNODETOOLTIP");
			STGraphC.setShowNodeTooltips(s == null || s.equals("true"));

			s = basicProps.getProperty("HIGHLIGHTEDGES");
			STGraphC.setHighlightEdges(s == null || s.equals("true"));
		} catch (Exception ex) { /*ex.printStackTrace();*/ }
	}


	/** Main initializator. */
	public final void initContainer() {
    	System.out.println("Initializing container window..."); //$NON-NLS-1$
		JFrame stf = null; if(container instanceof JFrame) { stf = (JFrame)container; }
		//JApplet sta = null; if(container instanceof JApplet) { sta = (JApplet)container; }

		if(stf != null && visible) { // application call
			stf.setTitle(basicProps.getProperty("SYSTEM.NAME")); //$NON-NLS-1$
			stf.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			URL jgraphUrl = getBaseIcon(basicProps.getProperty("SYSTEM.ICON")); //$NON-NLS-1$
			if(jgraphUrl != null) {
				ImageIcon jgraphIcon = new ImageIcon(jgraphUrl);
				stf.setIconImage(systemIcon = jgraphIcon.getImage());
			}
		}

		multiDesktop = new JTabbedPane(basicProps.getProperty("TABPOSITION").equals(basicProps.getProperty("PREFS.TABPOS.TOP")) ? SwingConstants.TOP : SwingConstants.BOTTOM); //$NON-NLS-1$ //$NON-NLS-2$
		multiDesktop.addChangeListener(this); // enable the event of tab switching
		
		////
		new FileDrop(this, new FileDrop.Listener() {
			public void filesDropped(java.io.File[] files) { // handle file drop
				for(int i = 0; i < files.length; i++) {
					String fileName = files[i].getAbsolutePath();
					if(fileName.endsWith(STConfigurator.getProperty("FILEEXT.STANDARD")) || fileName.endsWith(STConfigurator.getProperty("FILEEXT.COMPRESSED"))) { //$NON-NLS-1$ //$NON-NLS-2$
						FileOpen.opener(fileName, false, true);
					}
				}
			}
		});
		////

		if(stf != null && visible) {
			String[] prop = basicProps.getProperty("WINDOW.BOUNDS").split(","); //$NON-NLS-1$ //$NON-NLS-2$
			stf.setBounds(Integer.parseInt(prop[0]), Integer.parseInt(prop[1]), Integer.parseInt(prop[2]), Integer.parseInt(prop[3]));
			stf.setJMenuBar(menuBar = (STMenuBar)context.getBean("menubar")); //$NON-NLS-1$
		}

		if(stf != null) {
			stf.setContentPane(this);
		} /*else if(sta != null) {
			sta.setContentPane(this);
		}*/
		setLayout(new BorderLayout());

		if(visible) {
			add(toolBar = (STToolBar)context.getBean("toolbar"), BorderLayout.PAGE_START); //$NON-NLS-1$
			add(statusBar = new STStatusBar(), BorderLayout.PAGE_END);
		}

		/**/
		splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, multiDesktop, listPane = new STListPane());
		listPane.addComponentListener(new ComponentAdapter() {
        	public void componentResized(ComponentEvent e) { STGraphC.container.repaint(); }
		});
		splitPane2.setOneTouchExpandable(true);
		splitPane2.setDividerLocation(10000);
		splitPane2.setResizeWeight(1.0);
		listPane.setMinimumSize(new Dimension(0, 0));

		/*
		splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, splitPane2, editPane = new STEditPane());
		editPane.addComponentListener(new ComponentAdapter() {
        	public void componentResized(ComponentEvent e) { STGraphC.container.repaint(); }
		});
		splitPane1.setOneTouchExpandable(true);
		splitPane1.setDividerLocation(10000);
		splitPane1.setResizeWeight(1.0);
		editPane.setMinimumSize(new Dimension(0, 0));
		
		add(splitPane1, BorderLayout.CENTER);
		*/

		add(splitPane2, BorderLayout.CENTER);
		
		/**/
		//add(multiDesktop, BorderLayout.CENTER);

		container.setVisible(visible);
		undoManager = new GraphUndoManager() { // create a GraphUndoManager which also updates the toolbar
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void undoableEditHappened(final UndoableEditEvent e) {
				super.undoableEditHappened(e);
				if(visible) {
					((EditUndo)STGraphC.getAction("EditUndo")).setEnabled(getUndoManager().canUndo(getCurrentGraph().getGraphLayoutCache())); //$NON-NLS-1$
					((EditRedo)STGraphC.getAction("EditRedo")).setEnabled(getUndoManager().canRedo(getCurrentGraph().getGraphLayoutCache())); //$NON-NLS-1$
				}
			}
		};
		if(stf != null) {
			stf.addComponentListener(this);
			stf.addWindowListener(this);
		}
		/*
		if(sta != null) {
			sta.addComponentListener(this);
		}
		*/

	    STTools.infoTextDialog = (InfoDialog)context.getBean("InfoDialog"); //$NON-NLS-1$
	    STTools.infoDataDialog = (InfoDialog)context.getBean("InfoDialog"); //$NON-NLS-1$
	    STTools.tracingDialog = (InfoDialog)context.getBean("InfoDialog"); //$NON-NLS-1$
	    STTools.samplesDialog = (InfoDialog)context.getBean("InfoDialog"); //$NON-NLS-1$
	    STTools.outputVarsDialog = (InfoDialog)context.getBean("InfoDialog"); //$NON-NLS-1$
	    STTools.updateDialog = (InfoDialog)context.getBean("InfoDialog"); //$NON-NLS-1$

		STConfigurator.repaintRecentItems();
	}


	/** Get the edit pane.
	 * @return the edit pane */
	//public static STListPane getEditPane() { return listPane; }


	/** Get the input stream for the specified file.
	 * <br>This is the main method for getting an input stream.
	 * @param fileName the file name
	 * @param asResource open as resource
	 * @return stream */
	public final InputStream getInputStream(final String fileName, final boolean asResource) {
		if(STTools.isEmpty(fileName)) { return null; }
		try {
			if(asResource || isApplet()) { return getClass().getClassLoader().getResource(fileName).openStream(); }
			return new FileInputStream(fileName);
		} catch (Exception e) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("ERR.FILE_NOT_FOUND") + " (" + fileName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return null;
		}
	}


	/** Get the input stream for the specified top graph and the specified submodel file.
	 * @param graph the graph
	 * @param fileName the file name
	 * @param asResource open as resource
	 * @return stream */
	public final InputStream getInputStream(final STGraphImpl graph, final String fileName, final boolean asResource) {
		String topFN = graph.getTopGraph().getFileName();
		if(!topFN.endsWith(STConfigurator.getProperty("FILEEXT.COMPRESSED"))) { return getInputStream(fileName, asResource); } //$NON-NLS-1$
		try {
			InputStream s = graph.getTopGraph().getInputStream();
			if(s instanceof FileInputStream) {
				((FileInputStream)s).getChannel().position(0);
			} else {
				s.reset();
			}
			ZipInputStream stream = new ZipInputStream(s);
			////String fn = graph.isTopGraph() ? "base" : fileName; //$NON-NLS-1$ (it did not work: changed (for symmetry) as in STGraphImpl.save())

			String fs = "/"; //STGraphC.isApplet() || resource ? "/" : System.getProperty("file.separator"); //$NON-NLS-1$
			String fs2 = STGraphC.isApplet() || asResource ? "/" : System.getProperty("file.separator");  //$NON-NLS-1$//$NON-NLS-2$
			String fn = graph.isTopGraph() ? "base" : STGraphC.getBasicProps().getProperty("MODEL.DIR") + fs + "local" + fs + fileName.substring(1 + fileName.lastIndexOf(fs2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			while(!stream.getNextEntry().getName().equals(fn)) { ; }
			return stream;
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("ERR.FILE_NOT_FOUND") + " (" + fileName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return null;
		}
	}


	/** Load the specified file.
	 * @param stream the input stream
	 * @param fileName the file name
	 * @param asResource load as a resource */
	public final void loadFile(final InputStream stream, final String fileName, final boolean asResource) {
    	System.out.println("Loading file " +  fileName + "..."); //$NON-NLS-1$ //$NON-NLS-2$
		STDesktop desktop = new STDesktop(stream, fileName, asResource, null, null);
		if(desktop.getGraph() != null) {
			STGraphC.getMultiDesktop().setSelectedComponent(desktop);
			stateChanged(null); // signal the active window must be refreshed
			STGraphExec graph = getCurrentGraph();
			if(graph != null) {
				graph.clearSelection();
				graph.setAsModified(false);
				STTools.maximizeDesktop();
				graph.refreshGraph();
				if(!graph.isRunnable()) { STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("ERR.FILE_WITH_ERRORS")); } //$NON-NLS-1$
			}
			refreshBars();
			STStatusBar sb = STGraphC.getStatusBar();
			if(sb != null) { sb.setInfoText(STTools.BLANK, 0); }
			/*
			if(STGraph.getExecMode() == STGraph.EXECMODE_APPLET) {
				String autoRun = ((JApplet)container).getParameter("run"); //$NON-NLS-1$
				if(autoRun != null && autoRun.equalsIgnoreCase("yes")) { graph.exec(); } //$NON-NLS-1$
			}
			*/
		}
		if(visible) { container.repaint(); }
    	System.out.println("File loaded..."); //$NON-NLS-1$
	}


	/** Handle the tab switching, i.e., the activation of a model.
	 * @param e the e */
	public final void stateChanged(final ChangeEvent e) {
		if(multiDesktop.getSelectedIndex() != -1) { setCurrentDesktop((STDesktop)multiDesktop.getSelectedComponent()); }
		else { setCurrentDesktop(null); }
	}


	/** Utility method, for refreshing the menubar and the toolbar. */
	public final void refreshBars() {
		if(menuBar != null) { menuBar.updateItems(); }
		if(toolBar != null) { toolBar.updateItems(); }
		if(statusBar != null) { statusBar.updateItems(); }
	}


	/** Set focus to the current graph. */
	public static void setFocus() {
		if(getMultiDesktop().getSelectedIndex() != -1 && STGraph.getSTC().isVisible()) { STGraph.getSTC().getCurrentGraph().requestFocus(); }
	}


	/** Handle the event of window resizing.
	 * @param e the e */
	public final void componentResized(final ComponentEvent e) {
		if(getCurrentGraph() != null && !getCurrentGraph().isLoading) {
			getLoggerUI().info("calling maximizeDesktop..."); //$NON-NLS-1$
			STTools.maximizeDesktop();
		}
	}


	/** Component moved.
	 * @param e the e */
	public final void componentMoved(final ComponentEvent e) { ; }


	/** Component hidden.
	 * @param e the e */
	public final void componentHidden(final ComponentEvent e) { ; }


	/** Component shown.
	 * @param e the e */
	public final void componentShown(final ComponentEvent e) { ; }


	/** Handle the event of window closure.
	 * @param e the e */
	public final void windowClosing(final WindowEvent e) {
    	System.out.println("Closing STGraph..."); //$NON-NLS-1$
		int execMode = STGraph.getExecMode();
		boolean somethingModified = false;
		for(int i = 0; i < multiDesktop.getComponentCount(); i++) {
			if(((STDesktop)multiDesktop.getComponentAt(i)).getGraph().isModified) { somethingModified = true; break; }
		}
		if(!somethingModified) {
			if(execMode == STGraph.EXECMODE_GUIAPP || execMode == STGraph.EXECMODE_GUIENGINE) {
				((Window)getContainer()).dispose();
			}
			if(execMode == STGraph.EXECMODE_GUIAPP) {
		    	System.out.println("STGraph closed."); //$NON-NLS-1$
				System.exit(0);
			} else {
				return;
			}
		}
		int state = JOptionPane.showConfirmDialog(container, getMessage("MSG.SAVE2"), getMessage("MSG.TITLE.CLOSE"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		if(state == JOptionPane.CANCEL_OPTION) { return; }
		if(state == JOptionPane.NO_OPTION) {
			if(execMode == STGraph.EXECMODE_GUIAPP || execMode == STGraph.EXECMODE_GUIENGINE) {
				((Window)getContainer()).dispose();
			}
			if(execMode == STGraph.EXECMODE_GUIAPP) {
		    	System.out.println("STGraph closed."); //$NON-NLS-1$
				System.exit(0);
			} else {
				return;
			}
		}
		multiDesktop.setSelectedIndex(0);
		int num = multiDesktop.getComponentCount();
		for(int i = 0; i < num;  i++) {
			try {
				currentDesktop = (STDesktop)multiDesktop.getComponentAt(0);
				currentGraph = currentDesktop.getGraph();
				if(currentGraph.isModified) {
					if(currentGraph.getFileName() != null) { ((FileSave)STGraphC.getAction("FileSave")).exec(); } //$NON-NLS-1$
					else { ((FileSaveAs)STGraphC.getAction("FileSaveAs")).exec(); } //$NON-NLS-1$
				}
				((FileClose)STGraphC.getAction("FileClose")).execWithoutMsgs(); //$NON-NLS-1$
			} catch (Exception ex) { ; }
		}
		if(execMode == STGraph.EXECMODE_GUIAPP || execMode == STGraph.EXECMODE_GUIENGINE) {
			((Window)getContainer()).dispose();
		}
		if(execMode == STGraph.EXECMODE_GUIAPP) {
	    	System.out.println("Closing STGraph..."); //$NON-NLS-1$
			System.exit(0);
		} else {
			return;
		}
	}


	/** Window activated.
	 * @param arg0 the arg0 */
	public final void windowActivated(final WindowEvent arg0) { ; }


	/** Window closed.
	 * @param arg0 the arg0 */
	public final void windowClosed(final WindowEvent arg0) { ; }


	/** Window deactivated.
	 * @param arg0 the arg0 */
	public final void windowDeactivated(final WindowEvent arg0) { ; }


	/** Window deiconified.
	 * @param arg0 the arg0 */
	public final void windowDeiconified(final WindowEvent arg0) { ; }


	/** Window iconified.
	 * @param arg0 the arg0 */
	public final void windowIconified(final WindowEvent arg0) { ; }


	/** Window opened.
	 * @param arg0 the arg0 */
	public final void windowOpened(final WindowEvent arg0) { ; }


	/** Set whether is help.
	 * @param _help the help to set */
	public static void setHelp(final boolean _help) { STGraphC.help = _help; }


	/** Get whether is help.
	 * @return the help */
	public static boolean isHelp() { return help; }

}
