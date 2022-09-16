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
package it.liuc.stgraph;

import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STTools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;


/** Main application class.
 * 
 * Some commandline arguments are recognized (see the main() below):
 * <br>-l=<i>zz</i>, where <i>zz</i> is the user interface language (currently "it" or "en");
 * <br>-x=<i>z</i>, where <i>z</i> is the execution level: "e": easy mode; "w": web mode; any other or nothing: default mode;
 * <br>-f=<i>z</i>, where <i>z</i> is the filename of the stg model to be opened;
 * <br>-m: open the specified model as resource;
 * <br>-s: do not show the splash screen;
 * <br>-u: fallback to old user interface;
 * <br>-h: show some help notes on these options.
 *
 * <p>At the bottom of the main() there is the code to handle uncaught exceptions: uncomment to show.
 *
 * <p>To regenerate the application, remember to run STFunctionDocumenter() first...
 *
 * <p>The class it.liuc.stgraph.tools.STTester includes an app to test some aspects of the application.
 *
 * <p>To add a new action:
 * <br>- create a new class in it.liuc.graph.action
 * <br>- edit both actions.base.spring.xml.properties and actions.default.spring.xml.properties
 * <br>- edit menus_xx.properties
 *
 * */
public class STGraph {
	public final static String ICON_PATH = "icons/base/img.png"; //$NON-NLS-1$
	public final static String MOTTO = "Systems Theory for Simulation"; //$NON-NLS-1$
	public final static String AUTHOR = "Luca Mari, 2004-2022"; //$NON-NLS-1$

	/** The stc. */
	private static STGraphC stc;
	/** The is loaded. */
	private boolean loaded = false;
	/** The exec mode. */
	private static int execMode = -1;
	/** The Constant EXECMODE_GUIAPP. */
	public static final int EXECMODE_GUIAPP = 0;
	/** The Constant EXECMODE_GUIENGINE. */
	public static final int EXECMODE_GUIENGINE = 1;
	/** The Constant EXECMODE_NOGUIENGINE. */
	public static final int EXECMODE_NOGUIENGINE = 2;
	/** The Constant EXECMODE_APPLET. */
	public static final int EXECMODE_APPLET = 3; // OUTDATED: removed option...
	/** The exec level. */
	private static int execLevel = -1;
	/** The Constant EXECLEVEL_STANDARD. */
	public static final int EXECLEVEL_STANDARD = 0;
	/** The Constant EXECLEVEL_EASY. */
	public static final int EXECLEVEL_EASY = 1;
	/** The Constant EXECLEVEL_WEB. */
	public static final int EXECLEVEL_WEB = 2;
	/** The fallback to old UI. */
	public static boolean fallback = false;

	public static String getMyFont() { return "Arial"; }


	/** Entry point of the application.
	 * @param args the args */
	public static void main(final String[] args) {
		String locale = null;
		String fileName = null;
		String prefix = null;
		boolean asResource = false;
		boolean withSplash = true;
		execMode = EXECMODE_GUIAPP;
		execLevel = EXECLEVEL_STANDARD;
		try {
			for(int i = 0; i < args.length; i++) {
				prefix = args[i].substring(1, 2);
				if(prefix.equals("l")) { locale = args[i].substring(3); } //$NON-NLS-1$
				else if(prefix.equals("x")) { //$NON-NLS-1$
					String x = args[i].substring(3);
					if(x.equals("e")) { //$NON-NLS-1$
						execLevel = EXECLEVEL_EASY;
					} else if(x.equals("w")) { //$NON-NLS-1$
						execLevel = EXECLEVEL_WEB;
					}
				}
				else if(prefix.equals("f")) { fileName = args[i].substring(3); } //$NON-NLS-1$
				else if(prefix.equals("m")) { asResource = true; } //$NON-NLS-1$
				else if(prefix.equals("s")) { withSplash = false; } //$NON-NLS-1$
				else if(prefix.equals("u")) { fallback = true; } //$NON-NLS-1$
				else if(prefix.equals("h")) { //$NON-NLS-1$
					System.out.println("STGraph can be run with the following commandline options:\n" + //$NON-NLS-1$
							"-l=zz, where zz is the user interface language (currently \"it\" or \"en\"\n" + //$NON-NLS-1$
							"-x=z, where z is the execution level: \"e\": easy mode; \"w\": web mode; any other or nothing: default mode\n" + //$NON-NLS-1$
							"-f=z, where z is the filename of the stg model to be opened\n" + //$NON-NLS-1$
							"-m: open the specified model as resource\n" + //$NON-NLS-1$
							"-s: do not show the splash screen\n" + //$NON-NLS-1$
							"-u: fallback to old user interface\n" + //$NON-NLS-1$
							"-h: show these help notes."); //$NON-NLS-1$
					System.exit(0);
				}
			}
		} catch (Exception e) { ; }
		System.out.println("Launching STGraph on JVM " + STTools.getJVMVersion() + " ..."); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("sun.java2d.dpiaware", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		if(withSplash) { STSplash.show(); }
		new STGraph(true, locale, fileName, asResource);
		if(withSplash) { STSplash.hide(); }

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { //added to avoid an exception thrown by Nimbus...
			@Override public void uncaughtException(Thread t, Throwable e) {
				boolean isAboutLAF = false;
				for(StackTraceElement ste: e.getStackTrace()) {
					if(ste.getClassName() == "javax.swing.plaf.nimbus.NimbusLookAndFeel") { // a ridiculous filter!
						isAboutLAF = true;
						break;
					}
				}
				if(!isAboutLAF) {
					for(StackTraceElement ste: e.getStackTrace()) {
						System.err.println(ste.toString());
					}
				}
			}
		});

		new Thread() {
			public void run() { STTools.updateJars(false); }
		}.start();

	}


	/** Initialize the application window, as the basic container, with no visible user interface and default configuration parameters.
	 * @param fileName the file name */
	public STGraph(final String fileName) {
		execMode = EXECMODE_NOGUIENGINE;
		stc = new STGraphC(new JFrame());
		stc.setPreferences(false, null);
		stc.initContainer();
		InputStream stream = stc.getInputStream(fileName, false);
		if(stream != null) {
			stc.loadFile(stream, fileName, false);
			loaded = true;
		} else { loaded = false; }
	}


	/** Initialize the application window, as the basic container, with no visible user interface and default configuration parameters.
	 * @param stream the input stream
	 * @param fileName the file name */
	public STGraph(final InputStream stream, final String fileName) {
		execMode = EXECMODE_NOGUIENGINE;
		stc = new STGraphC(new JFrame());
		stc.setPreferences(false, null);
		stc.initContainer();
		if(stream != null) {
			stc.loadFile(stream, fileName, false);
			loaded = true;
		} else { loaded = false; }
	}


	/** Initialize the application window, as the basic container.
	 * @param visible the visible
	 * @param locale the locale
	 * @param fileName the file name
	 * @param asResource load as resource? */
	public STGraph(final boolean visible, final String locale, final String fileName, final boolean asResource) {
		if(execMode == -1) { execMode = visible ? EXECMODE_GUIENGINE : EXECMODE_NOGUIENGINE; }
		stc = new STGraphC(new JFrame());
		stc.setPreferences(visible, locale);
		stc.initContainer();
		InputStream stream = stc.getInputStream(fileName, asResource);
		if(stream != null) {
			stc.loadFile(stream, fileName, false);
			loaded = true;
		} else { loaded = false; }
	}


	/** Initialize the application window, as the basic container.
	 * @param visible the visible
	 * @param locale the locale
	 * @param stream the input stream
	 * @param fileName the file name */
	public STGraph(final boolean visible, final String locale, final InputStream stream, final String fileName) {
		if(execMode == -1) { execMode = visible ? EXECMODE_GUIENGINE : EXECMODE_NOGUIENGINE; }
		stc = new STGraphC(new JFrame());
		stc.setPreferences(visible, locale);
		stc.initContainer();
		if(stream != null) {
			stc.loadFile(stream, fileName, false);
			loaded = true;
		} else { loaded = false; }
	}


	/** Splash screen handler. */
	static class STSplash {
		/** Splash frame. */
		private static JFrame frame = new JFrame();


		/** Show the splash. */
		static void show() {
			frame.setUndecorated(true);
			Container cp = frame.getContentPane();
			((JComponent)cp).setBorder(new SoftBevelBorder(BevelBorder.LOWERED, Color.DARK_GRAY, Color.GRAY));
			cp.setBackground(Color.WHITE);
			Box box = Box.createVerticalBox();
			JLabel image = new JLabel(new ImageIcon(ClassLoader.getSystemResource(ICON_PATH)));
			image.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(image);
			JLabel motto = new JLabel(MOTTO);
			motto.setFont(new Font(STGraph.getMyFont(), Font.ITALIC, 16)); //$NON-NLS-1$
			motto.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(motto);
			JLabel author = new JLabel(AUTHOR);
			author.setFont(new Font(STGraph.getMyFont(), Font.BOLD, 12)); //$NON-NLS-1$
			author.setAlignmentX(Component.CENTER_ALIGNMENT);
			box.add(author);
			frame.add(box, BorderLayout.CENTER);
			frame.pack();
			Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
			frame.setLocation(p.x - frame.getWidth() / 2, p.y - frame.getHeight() /2);
			frame.setVisible(true);
		}


		/** Hide the splash. */
		static void hide() {
			frame.setVisible(false);
			frame = null;
		}

	}


	/** Get the execution mode for the whole program.
	 * @return mode */
	public static final int getExecMode() { return execMode; }


	/** Get the execution level for the whole program.
	 * @return level */
	public static final int getExecLevel() { return execLevel; }


	/** Get the static container for the whole program.
	 * @return container */
	public static final STGraphC getSTC() { return stc; }


	/** Get the current graph.<br>
	 * This is an API method, typically useful for external callers which explicitly create an instance of this class.
	 * "Internal" methods should instead call <code>STGraph.getSTC().getCurrentGraph()</code>.
	 * @return the current graph */
	public final STGraphExec getCurrentGraph() { return stc.getCurrentGraph(); }


	/** Check whether the system was able to load the given file.<br>
	 * This is an API method, typically useful for external callers which explicitly create an instance of this class.
	 * @return is loaded */
	public final boolean isLoaded() { return loaded; }


	/** Open a model.
	 * @param fileName the file name */
	public final void open(final String fileName) {
		String fn = new File(fileName).getAbsolutePath();
		stc.loadFile(stc.getInputStream(fn, false), fn, false);
		loaded = true;
	}


	/** Close the current model. */
	public final void close() { stc.getCurrentDesktop().close(); }


	/** Execute the simulation on the current model. */
	public final void exec() {
		if(stc.getCurrentGraph() != null) { stc.getCurrentGraph().exec(); }
	}


	/** Execute the simulation on the current model in the timed way. */
	public final void timedExec() {
		if(stc.getCurrentGraph() != null) { stc.getCurrentGraph().timedExec(); }
	}


	/** Execute the simulation on the current model in the timed way.
	 * @param timeDelay the time delay */
	public final void timedExec(final int timeDelay) {
		if(stc.getCurrentGraph() != null) {
			stc.getCurrentGraph().simulationDelay = timeDelay;
			stc.getCurrentGraph().timedExec();
		}
	}


	/** Execute a single step of the simulation on the current model.
	 * @return result */
	public final boolean steppedExec() {
		if(stc.getCurrentGraph() != null) { return stc.getCurrentGraph().steppedExec(); }
		return false;
	}


	/** Reset the simulation on the current model. */
	public final void resetExec() {
		if(stc.getCurrentGraph() != null) { stc.getCurrentGraph().resetStepExec(); }
	}


	/** Get the number of steps defined for the simulation on the current model.
	 * @return number of steps */
	public final int getNumSteps() {
		if(stc.getCurrentGraph() != null) { return stc.getCurrentGraph().getNumSteps(); }
		return 0;
	}


	/** Get the current time during the simulation on the current model.
	 * @return current time */
	public final double getCurrentTime() {
		if(stc.getCurrentGraph() != null) { return stc.getCurrentGraph().currTime; }
		return 0.0;
	}


	/** Get the current step index during the simulation on the current model.
	 * @return current step index */
	public final int getCurrentStep() {
		if(stc.getCurrentGraph() != null) { return stc.getCurrentGraph().currStep; }
		return 0;
	}


	/** Get an array containing the names of the nodes of the current model.
	 * @return names of nodes */
	public final String[] getNodeNames() {
		if(stc.getCurrentGraph() == null) { return null; }
		String[] result = new String[stc.getCurrentGraph().getNodeCount()];
		int i = 0;
		for(STNode node : stc.getCurrentGraph().getNodes()) { result[i++] = node.getName(); }
		return result;
	}


	/** Get an array containing the nodes of the current model and its sub-models.
	 * @return nodes */
	public final STNode[] getNodes() {
		if(stc.getCurrentGraph() == null) { return null; }
		ArrayList<STNode> n = new ArrayList<STNode>();
		n = getNodesHelper(stc.getCurrentGraph(), n);
		return n.toArray(new STNode[n.size()]);
	}


	/** Helper for the <code>getNodes()</code> method.
	 * @param g the graph
	 * @param n the node list
	 * @return the node list */
	private ArrayList<STNode> getNodesHelper(final STGraphImpl g, final ArrayList<STNode> n) {
		Object[] o = g.getEntities();
		if(o == null) { return n; }
		for(Object ob : o) {
			if(STTools.isNode(ob)) { n.add((STNode)ob); }
		}
		if(g.isComposed) {
			STDesktop desk;
			for(ModelNode submodel : g.modelNodeList) {
				if((desk = submodel.getDesk()) != null) { getNodesHelper(desk.getGraph(), n); }
			}
		}
		return n;
	}


	/** Get an array containing the input nodes of the current model and its sub-models.
	 * @return input nodes */
	public final STNode[] getInputNodes() {
		if(stc.getCurrentGraph() == null) { return null; }
		STNode[] nodes = getNodes();
		if(nodes == null) { return null; }
		ArrayList<STNode> n = new ArrayList<STNode>();
		for(STNode node : nodes) {
			if(node.isInput()) { n.add(node); }
		}
		return n.toArray(new STNode[n.size()]);
	}


	/** Get an array containing the state nodes of the current model and its sub-models.
	 * @return state nodes */
	public final STNode[] getStateNodes() {
		if(stc.getCurrentGraph() == null) { return null; }
		STNode[] nodes = getNodes();
		if(nodes == null) { return null; }
		ArrayList<STNode> n = new ArrayList<STNode>();
		for(STNode node : nodes) {
			if(node.isState()) { n.add(node); }
		}
		return n.toArray(new STNode[n.size()]);
	}


	/** Get an array containing the output nodes of the current model and its sub-models.
	 * @return output nodes */
	public final STNode[] getOutputNodes() {
		if(stc.getCurrentGraph() == null) { return null; }
		STNode[] nodes = getNodes();
		if(nodes == null) { return null; }
		ArrayList<STNode> n = new ArrayList<STNode>();
		for(STNode node : nodes) {
			if(node.isOutput()) { n.add(node); }
		}
		return n.toArray(new STNode[n.size()]);
	}


	/** Get a map containing the names (keys) and the references (values) of the nodes
	 * of the current model and its sub-models.
	 * @return map */
	public final HashMap<String, STNode> getNodeRefs() {
		if(stc.getCurrentGraph() == null) { return null; }
		HashMap<String, STNode> result = new HashMap<String, STNode>();
		for(STNode node : getNodes()) { result.put(node.getFullName(), node); }
		return result;
	}


	/** Get the reference to the specified node, in the current model or one of its sub-models.
	 * @param name the name
	 * @return reference of specified node */
	public final STNode getNodeRef(final String name) {
		if(stc.getCurrentGraph() == null) { return null; }
		STGraphImpl g = stc.getCurrentGraph();
		String n = name;
		int p;
		while((p = n.indexOf(STTools.DOT)) != -1) {
			if(!g.isComposed) { return null; }
			String subN = n.substring(0, p);
			STDesktop desk;
			boolean found = false;
			for(ModelNode submodel : g.modelNodeList) {
				if(submodel.getName().equals(subN)) {
					if((desk = submodel.getDesk()) != null) {
						g = desk.getGraph();
						found = true;
					} else { return null; }
				}
			}
			if(!found) { return null; }
			n = n.substring(p + 1);
		}
		Object[] o = g.getEntities();
		if(o == null) { return null; }
		for(Object ob : o) {
			if(STTools.isNode(ob) && ((STNode)ob).getName().equals(n)) { return (STNode)ob; }
		}
		return null;
	}

}
