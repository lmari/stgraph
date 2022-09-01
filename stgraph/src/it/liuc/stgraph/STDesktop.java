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

import it.liuc.stgraph.action.FileOpen;
import it.liuc.stgraph.action.ToolsToggleShowGraph;
import it.liuc.stgraph.node.EditPanel4Nodes;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.util.FileDrop;
import it.liuc.stgraph.util.STTools;

import java.awt.Dimension;
import java.io.InputStream;
import java.util.Arrays;

import javax.swing.JDesktopPane;
import javax.swing.JScrollPane;


/** Wrapper class, associating a model to a window pane.
 * It is used to optionally load files, and to maintain the local values required
 * to univocally identify a model and its context of UI and execution. */
@SuppressWarnings("serial")
public class STDesktop extends JDesktopPane {
	/** The graph frame1. */
	private STGraphPanel graphFrame1;
	/** The graph frame2. */
	private JScrollPane graphFrame2;
	/** The graph. */
	private STGraphExec graph;

	public Dimension dim;


	/** Class constructor.
	 * @param stream the input stream
	 * @param fileName the file name
	 * @param asResource load as a resource
	 * @param topGraph the top graph
	 * @param superNode the super node */
	@SuppressWarnings("unchecked")
	public STDesktop(final InputStream stream, final String fileName, final boolean asResource, final STGraphImpl topGraph, final ModelNode superNode) {
		super();
		STGraphC stc = STGraph.getSTC();
		stc.setCurrentDesktop(this);
		stc.setCurrentGraph(graph = new STGraphExec(stream, fileName, asResource, topGraph, superNode));
		graph.isLoading = true;
		boolean visible = stc.isVisible();
		setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		stc.setVisible(visible);
		dim = getSize();

		graphFrame1 = new STGraphPanel();
		add(graphFrame1);
		setLayer(graphFrame1, -1);
		graphFrame1.setBorder(null);
		graphFrame1.setVisible(visible);

		graph.setSize(dim);
		graphFrame2 = new JScrollPane(graph);
		graphFrame1.add(graphFrame2);
		graphFrame2.setBorder(null);
		graphFrame2.setVisible(visible);
		graph.setVisible(visible);

		graph.getModel().addUndoableEditListener(STGraphC.getUndoManager()); // add listeners to graph
		graph.getSelectionModel().addGraphSelectionListener(graphFrame1);
		graph.addKeyListener(graphFrame1);
		graph.getModel().addGraphModelListener(graphFrame1);

		if(graph.isTopGraph()) {
			STGraphC.getMultiDesktop().addTab(graph.getTitle(), this);
		} else {
			graph.getSuperNode().setDesk(this);
			graph.getSuperNode().setSubVisible(graph.getSuperNode().isSubVisible());
		}

		if(fileName != null) {
			if(graph.load()) {
				graph.getInterpreter().handleVarsForWeb();

				graph.setLists();

				graph.isSequential = false; // just a default
				if(graph.stateNodeList != null && graph.stateNodeList.length > 0) { graph.isSequential = true; }

				if(visible) {
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
				}

				if(graph.isComposed) {
					Arrays.sort(graph.modelNodeList, STGraph.getSTC().getNodeNameComparator());
					String fn = null;
					STDesktop desk = null;
					for(STNode node : graph.modelNodeList) {
						try {
							fn = ((ModelNode)node).getSubmodelFilename();
							if(fn != null) {
								InputStream is = stc.getInputStream(graph, fn, asResource);
								if(is != null) {
									((ModelNode)node).setDesk(desk = new STDesktop(is, fn, asResource, graph, (ModelNode)node));
									STGraphExec subGraph = desk.graph;
									if(subGraph.outputNodeList != null && subGraph.outputNodeList.length > 0) {
										String name = node.getName();
										for(STNode outNode : subGraph.outputNodeList) { graph.getInterpreter().addVariable(name + STTools.DOT + outNode.getName(), 0); }
									}
								}
							}
						} catch (Exception e) { e.printStackTrace(); }
					}
				}
				if(graph.isTopGraph()) { graph.setupModel(); }

				ToolsToggleShowGraph ttsg = ((ToolsToggleShowGraph)STGraphC.getAction("ToolsToggleShowGraph")); //$NON-NLS-1$
				if(ttsg != null) { ttsg.showHide(); }
			} else { close(); }
		}
		if(graph != null) { graph.isLoading = false; }
	}


	/** Handle the event of desktop closure. */
	public final void close() {
		if(graph != null && graph.getFileName() != null) {
			System.out.println("Closing file" + STTools.SPACE + graph.getFileName() + "..."); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			System.out.println("Closing model..."); //$NON-NLS-1$
		}
		if(EditPanel4Nodes.isOpen()) { EditPanel4Nodes.node.getDialog().getButtonCancel().doClick(); }

		STGraphC stc = STGraph.getSTC();
		if(graph != null && graph.isComposed) { // operate on subsystems
			try {
				ModelNode node;
				STDesktop desk;
				for(int i = 0; i < graph.modelNodeList.length; i++) {
					node = graph.modelNodeList[i];
					if((desk = node.getDesk()) != null) { desk.close(); }
				}
			} catch (Exception e) { ; }
		}
		graph = null;
		STGraphC.getMultiDesktop().remove(this);
		stc.stateChanged(null); // force a tab change signal
		if(STGraphC.getMultiDesktop().getComponentCount() == 0 && STGraphC.getStatusBar() != null) { STGraphC.getStatusBar().setControlVisibile(false); }
	}


	/** Get the STGraphPanel for this desktop.
	 * @return panel */
	public final STGraphPanel getGraphFrame1() { return graphFrame1; }


	/** Get the JScrollPane for this desktop.
	 * @return scroll pane */
	public final JScrollPane getGraphFrame2() { return graphFrame2; }


	/** Get the STGraphExec for this desktop.
	 * @return graph */
	public final STGraphExec getGraph() { return graph; }

}
