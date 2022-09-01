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

import it.liuc.stgraph.action.AnalysisLoops;
import it.liuc.stgraph.action.EditModelProperties;
import it.liuc.stgraph.action.EditNodeProperties;
import it.liuc.stgraph.action.FileClose;
import it.liuc.stgraph.action.ToolsShowProperties;
import it.liuc.stgraph.action.ToolsZoomIn;
import it.liuc.stgraph.action.ToolsZoomOut;
import it.liuc.stgraph.node.EditPanel4Nodes;

import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STEdge;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.TextNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.GroupListDialog;
import it.liuc.stgraph.util.NodeListDialog;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.util.WidgetListDialog;
import it.liuc.stgraph.widget.STWidget;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;


/** Main controller for the operations performed on graphs. */
@SuppressWarnings("serial")
public class STGraphPanel extends JPanel implements GraphSelectionListener, GraphModelListener, KeyListener {
	/** The selected node. */
	transient private STNode selectedNode;
	/** The selected edge. */
	transient private STEdge selectedEdge;
	/** The array of pasted nodes, to be selected. */
	transient public static ArrayList<DefaultGraphCell> pastedNodes;
	/** The array of pasted STNodes, to be properly initialized. */
	transient private static ArrayList<STNode> pastedSTNodes;

	private boolean isTextNodeHandling = false;


	/** Class constructor. */
	public STGraphPanel() { ; }


	/** Handle the change in the current selection.
	 * @param e the event */
	public final void valueChanged(final GraphSelectionEvent e) {
		STGraphC stc = STGraph.getSTC();
		if(stc.isLoading) { return; }
		STGraphExec graph = stc.getCurrentGraph();
		stc.refreshBars();
		if(selectedNode != null && STGraphC.isHighlightEdges()) { selectedNode.resetEdges(); }

		if(selectedNode != null && graph.getLoopEdgeList().size() > 0) { graph.resetLoops(); }

		selectedNode = null;
		if(selectedEdge != null) { selectedEdge.showLabel(false); }
		selectedEdge = null;
		int num = graph.getSelectionCount();
		Object ob = graph.getSelectionCell();
		STStatusBar sb = STGraphC.getStatusBar();
		if(num == 0) {
			if(sb != null) {
				if(graph.getLastError() == null) { sb.setInfoText(STTools.BLANK, 0); }
				else { sb.setInfoText(graph.getLastError(), 1); }
				sb.setControlVisibile(false);
			}
			if(STTools.infoDataDialog.isVisible()) { STTools.infoDataDialog.showMe(STGraphC.getMessage("SYSTEM.DIALOG.INFOTITLE"), graph.toString(), false); } //$NON-NLS-1$
		} else if(num == 1 && STTools.isNode(ob)) { // display selected node information
			selectedNode = (STNode)ob;
			if(sb != null) {
				if(graph.isRunnable()) {
					if(selectedNode.getEvalErrDescription() != null) {
						String err = selectedNode.getEvalErrDescription();
						int pos = err.indexOf("<br>"); //$NON-NLS-1$
						if(pos != -1) { err = err.substring(0, pos); }
						sb.setInfoText(err, 1);
					} else { sb.setInfoText(selectedNode.getName(), STData.toString(selectedNode.getValue(), STData.FORMAT_SHORT)); }
				}
				sb.setControlVisibile(true);
			}
			if(STTools.infoDataDialog.isVisible()) { STTools.infoDataDialog.showMe(STGraphC.getMessage("NODE.DIALOG.SHOWTITLE"), selectedNode.getInfo(), false); } //$NON-NLS-1$
			if(EditPanel4Nodes.isOpen()) {
				if(EditPanel4Nodes.isDirty()) { // autosaving
					EditPanel4Nodes.node.getDialog().okHandler(EditPanel4Nodes.MODE_APPLY);
				}
				if(EditPanel4Nodes.node.getClass() != selectedNode.getClass()) {
					EditPanel4Nodes.node.getDialog().getButtonCancel().doClick();
				}
				selectedNode.openDialog();
			}
			if(STGraphC.isHighlightEdges()) { selectedNode.highlightEdges(); }
		} else if(num == 1 && STTools.isEdge(ob)) {
			selectedEdge = (STEdge)ob;
			selectedEdge.showLabel(true);
			if(sb != null) {
				if(graph.getLastError() == null) { sb.setInfoText(STTools.BLANK, 0); }
				else { sb.setInfoText(graph.getLastError(), 1); }
				sb.setControlVisibile(false);
			}
		} else {
			if(sb != null) {
				if(graph.getLastError() == null) { sb.setInfoText(STTools.BLANK, 0); }
				else { sb.setInfoText(graph.getLastError(), 1); }
				boolean foundNode = false;
				int i = 0;
				Object[] objs = graph.getSelectionCells();
				while(i < objs.length) {
					if(STTools.isNode(objs[i])) {
						foundNode = true;
						break;
					}
					i++;
				}
				sb.setControlVisibile(foundNode);
			}
		}
		graph.refreshGraph();
	}


	/** Get the selected node.
	 * @return node */
	public final STNode getSelectedNode() { return selectedNode; }


	/** Handle the control keys in a centralized way.
	 * This is required to maintain the keystrokes active when the menubar has not the focus, in particular for applets.
	 * @param e the event */
	public final void keyReleased(final KeyEvent e) {
		// basic actions, as in the menubar
		if(e.getKeyCode() == KeyEvent.VK_F1 && !e.isControlDown()) {
			((ToolsShowProperties)STGraphC.getAction("ToolsShowProperties")).exec(); //$NON-NLS-1$
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_F2 && !e.isControlDown()) {
			if(e.isShiftDown()) {
				((EditModelProperties)STGraphC.getAction("EditModelProperties")).exec(); //$NON-NLS-1$
			} else {
				if(STGraph.getSTC().getCurrentGraph().getSelectionCount() == 1) {
					((EditNodeProperties)STGraphC.getAction("EditNodeProperties")).exec(); //$NON-NLS-1$
				}
			}
			return;
		}
		if(e.getKeyCode() == 107 && e.isControlDown()) { // plus on numkeypad
			((ToolsZoomIn)STGraphC.getAction("ToolsZoomIn")).exec(); //$NON-NLS-1$
			return;
		}
		if(e.getKeyCode() == 109 && e.isControlDown()) { // minus on numkeypad
			((ToolsZoomOut)STGraphC.getAction("ToolsZoomOut")).exec(); //$NON-NLS-1$
			return;
		}


		/*
		if(e.getKeyCode() == KeyEvent.VK_F3 && !e.isControlDown()) {
			((ToolsHandleWidgets)STGraphC.getAction("ToolsHandleWidgets")).exec(); //$NON-NLS-1$
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_F4 && !e.isControlDown()) {
			((ToolsHandleNodes)STGraphC.getAction("ToolsHandleNodes")).exec(); //$NON-NLS-1$
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_F5 && !e.isControlDown()) {
			((ToolsHandleGroups)STGraphC.getAction("ToolsHandleGroups")).exec(); //$NON-NLS-1$
			return;
		}
		 */
		if(e.getKeyCode() == KeyEvent.VK_K && e.isControlDown()) {
			((FileClose)STGraphC.getAction("FileClose")).execWithoutMsgs(); //$NON-NLS-1$
			return;
		}

		
		if(e.getKeyCode() == KeyEvent.VK_M && e.isControlDown()) {
			if(selectedNode != null) {
				//STGraph.getSTC().getCurrentGraph().getLoops(selectedNode);
				new AnalysisLoops().exec();
			}
			return;
		}
		

		/*
		if(e.getKeyCode() == KeyEvent.VK_M && e.isControlDown()) {
			for(STNode n : STGraph.getSTC().getCurrentGraph().getAllNodes()) {
				System.out.println("> " +  n.getGraph().getTitle() + STTools.SPACE + n.getGraph().isForWeb() + STTools.SPACE + n.getName()); //$NON-NLS-1$
				if(n.isState()) { System.out.println("*** " + ((ValueNode)n).getStateInitForWeb()); } //$NON-NLS-1$
			}
			return;
		}
		 */

		/*
		if(e.getKeyCode() == KeyEvent.VK_M && e.isControlDown()) {
			STTools.alignNodes(STTools.ALIGN_HORIZONTAL_TOP);
		}
		 */

		// hidden (experimental) actions
		/*
		if(e.getKeyCode() == KeyEvent.VK_M && !e.isControlDown()) { // print some info on the selected entities
			System.out.println("Selection count: " + STGraph.getSTC().getCurrentGraph().getSelectionCount()); //$NON-NLS-1$
			Object[] o = STGraph.getSTC().getCurrentGraph().getSelectionCells();
			for(int i = 0; i < o.length; i++) {
				System.out.println("> " +  o[i].getClass().getName()); //$NON-NLS-1$
			}
			return;
		}
		 */
		/*
		if(e.getKeyCode() == KeyEvent.VK_2 && e.isControlDown()) { // dump the graph image to a png file
			try {
				STGraphExec graph = STGraph.getSTC().getCurrentGraph();
				OutputStream out = new FileOutputStream("prova.png"); //$NON-NLS-1$
				Color bg = null; // transparent background
				BufferedImage img = graph.getImage(bg, 0);
				ImageIO.write(img, "png", out); //$NON-NLS-1$
				out.flush();
				out.close();
				System.out.println("eseguito"); //$NON-NLS-1$
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_L && !e.isControlDown()) {
	    	STGraphC stc = STGraph.getSTC();
	       	STGraphImpl graph = stc.getCurrentGraph();
	        String gn = stc.isCurrentDesktop() ? graph.getTitle() + STConfigurator.getProperty("FILEEXT.STANDARD") : STTools.BLANK; //$NON-NLS-1$
	        String w = String.valueOf(stc.getWidth());
	        String h = String.valueOf(stc.getHeight());

	        StringBuilder s = new StringBuilder();
			s.append("&lt;applet code=\"it.liuc.stgraph.STGraph.class\"<br>"); //$NON-NLS-1$
			s.append("archive=\"./lib/commons-logging.jar,./lib/jep.jar,./lib/jgraph.jar,./lib/jxl.jar,./lib/nanoxml-2.2.3.jar,./lib/spring-beans.jar,./lib/spring-context.jar,./lib/spring-core.jar,./lib/stgraph.jar\"<br>"); //$NON-NLS-1$
			s.append("width=\"" + w + "\" height=\"" + h + "\"&gt;<br>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			s.append("&lt;param name=\"locale\" value=\"en\"&gt;<br>"); //$NON-NLS-1$
			s.append("&lt;param name=\"filename\" value=\"" + gn + "\"&gt;<br>"); //$NON-NLS-1$ //$NON-NLS-2$
			s.append("&lt;param name=\"run\" value=\"no\"&gt;<br>"); //$NON-NLS-1$
			s.append("&lt;/applet&gt;"); //$NON-NLS-1$
			STTools.infoTextDialog.showMe("", s.toString(), true); //$NON-NLS-1$
			return;
		}
		 */
		if(e.getKeyCode() == KeyEvent.VK_R && !e.isControlDown() && !e.isShiftDown()) {
			String title = "titolo"; //$NON-NLS-1$
			Map<String, String> rm = STGraph.getSTC().getCurrentGraph().reportMap;
			if(rm == null) {
				rm = new HashMap<String, String>();
				rm.put(title, STTools.BLANK);
			}
			STReporter.getReporter().openDialog(STGraph.getSTC().getCurrentGraph(), title);
			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_R && !e.isControlDown() && e.isShiftDown()) {
			String title = "titolo"; //$NON-NLS-1$
			Map<String, String> rm = STGraph.getSTC().getCurrentGraph().reportMap;
			if(rm == null) {
				rm = new HashMap<String, String>();
				rm.put(title, STTools.BLANK);
			}
			String text = STReporter.getReporter().getReport(STGraph.getSTC().getCurrentGraph(), title);
			STTools.infoDataDialog.showMe(title, text, true);
			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_F12 && e.isControlDown()) {
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			System.err.println(graph.getWebModelDescription(graph.getDefaultModelLanguage()));
			String[][] data = graph.getWebModelGroupData(graph.getDefaultModelLanguage());
			if(data != null && data.length > 0) {
				for(int i = 0; i < data.length; i++) {
					System.err.println((i + 1) + " - " + data[i][0] + " - " + data[i][1]); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}


		/*
		if(e.getKeyCode() == KeyEvent.VK_F11 && e.isControlDown()) {
			STTextualModel.export(STGraph.getSTC().getCurrentGraph());
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_F7 && e.isControlDown()) {
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			JGraphFacade facade = new JGraphFacade(graph);
			JGraphLayout layout = new JGraphSelfOrganizingOrganicLayout();
			//layout.setActOnUnconnectedVerticesOnly(false);
			layout.run(facade);
			for(STNode node : graph.getNodes()) {
				Rectangle2D r = facade.getBounds(node);
				node.setPosition(new Point((int)r.getX(), (int)r.getY()));
			}
			graph.getGraphLayoutCache().reload();
			graph.repaint();
			return;
		}
		 */

		// persistence-related actions
		/*
		if(e.getKeyCode() == KeyEvent.VK_Z && !e.isControlDown()) { // single step init
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			DBHandler dbh = new DBHandler(graph, "mySim"); //$NON-NLS-1$
			try {
				dbh.setExternalInput(false);
				dbh.unbindInputs();

				dbh.exec(true, true);
				System.out.println("exec: " + dbh.getCurrStep() + "/" + graph.numSteps); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (Exception ex) { ; }
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_X && !e.isControlDown()) { // single step
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			DBHandler dbh = new DBHandler(graph, "mySim"); //$NON-NLS-1$
			try {
				dbh.exec(false, true);
				System.out.println("exec: " + dbh.getCurrStep() + "/" + graph.numSteps); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (Exception ex) { ; }
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_C && !e.isControlDown()) { // single step with no step advance
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			DBHandler dbh = new DBHandler(graph, "mySim"); //$NON-NLS-1$
			try {
				dbh.exec(false, false);
				System.out.println("exec: " + dbh.getCurrStep() + "/" + graph.numSteps); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (Exception ex) { ; }
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_V && !e.isControlDown()) { // full simulation
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			DBHandler dbh = new DBHandler(graph, "mySim"); //$NON-NLS-1$
			try {
				long startTime = System.nanoTime();
				dbh.exec(true, true);
				System.out.println("exec: " + dbh.getCurrStep() + "/" + graph.numSteps); //$NON-NLS-1$ //$NON-NLS-2$
				for(int i = 0; i < graph.numSteps; i++) {
					dbh.exec(false, true);
					System.out.println("exec: " + dbh.getCurrStep() + "/" + graph.numSteps); //$NON-NLS-1$ //$NON-NLS-2$
				}
				STGraph.getSTC().getLoggerExec().info("runtime (ms): " + (System.nanoTime() - startTime) / 1000000); //$NON-NLS-1$
			} catch (Exception ex) { ; }
			return;
		}
		if(e.getKeyCode() == KeyEvent.VK_B && !e.isControlDown()) { // delete all
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			DBHandler dbh = new DBHandler(graph, "mySim"); //$NON-NLS-1$
			try {
				dbh.deleteAllFromDB();
			} catch (Exception ex) { ; }
			return;
		}
		 */
	}


	/** Handle the control keys for entity movement in a centralized way.
	 * @param e the event */
	public final void keyPressed(final KeyEvent e) {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();

		//TODO to catch keystrokes runtime
		/*
		if(graph.getState() == STGraphImpl.STATE_STEPPING || graph.getState() == STGraphImpl.STATE_TIMING) {
			System.err.println(e.getKeyCode());
		}
		*/

		if(graph.getSelectionCount() == 0) { return; }
		if(graph.isEditable) {
			if(e.getKeyCode() == KeyEvent.VK_LEFT && !e.isAltDown()) { STTools.moveNodes(-1, 0, e.isControlDown()); }
			else if(e.getKeyCode() == KeyEvent.VK_RIGHT && !e.isAltDown()) { STTools.moveNodes(1, 0, e.isControlDown()); }
			else if(e.getKeyCode() == KeyEvent.VK_UP && !e.isAltDown()) { STTools.moveNodes(0, -1, e.isControlDown()); }
			else if(e.getKeyCode() == KeyEvent.VK_DOWN && !e.isAltDown()) { STTools.moveNodes(0, 1, e.isControlDown()); }
			else if(e.getKeyCode() == KeyEvent.VK_UP && e.isAltDown()) { STTools.moveScaleNodes(1.05, 1.05); }
			else if(e.getKeyCode() == KeyEvent.VK_DOWN && e.isAltDown()) { STTools.moveScaleNodes(0.95, 0.95); }
		}
	}


	/** Key typed.
	 * @param e the event */
	public final void keyTyped(final KeyEvent e) { ; }


	/** Handle the model changes, i.e., node / edge insertions, removals, and modifications.
	 * @param e the event */
	public final void graphChanged(final GraphModelEvent e) {
		STGraphC stc = STGraph.getSTC();
		STGraphExec graph = stc.getCurrentGraph();
		if(graph.isLoading) { return; }
		if(isTextNodeHandling) { return; }
		if(e != null) {
			Object[] objects;
			GraphModelEvent.GraphModelChange k = e.getChange();
			if((objects = k.getInserted()) != null) { // node/edge(s) inserted
				pastedNodes = new ArrayList<DefaultGraphCell>();

				pastedSTNodes = new ArrayList<STNode>();
				if(stc.isPasting) {
					for(Object ob : objects) {
						if(STTools.isNode(ob)) { pastedSTNodes.add((STNode) ob); }
					}
				}

				String oldName = null;
				String newName = null;
				for(Object ob : objects) {
					if(STTools.isNode(ob)) {
						STNode node = (STNode)ob;
						STInterpreter interpreter = node.getGraph().getInterpreter();
						node.setGraph(graph); // to deal with nodes which could be pasted to different graphs...
						node.getNewView(); //TODO [node view handling] this behavior, for dealing with pasted nodes, is plausibly not so much efficient
						if(stc.isPasting) {
							oldName = node.getName();
							pastedNodes.add(node);
							if(node.checkName(oldName) == null) {
								node.setName(oldName);
							} else {
								boolean isCorrect = false;
								int i = 2;
								while(!isCorrect) {
									newName = oldName + STTools.UNDERSCORE + i;
									isCorrect = node.checkName(newName) == null;
									i++;
								}
								node.setName(newName);
							}
							if(node.isInput()) { ((ValueNode)node).unbindFromWidget(); }
							graph.globalNodeCounter++;

							node.handleNodeRenaming(oldName, pastedSTNodes); // properly rename variables in equations for pasted nodes 

						} else {
							if((node.checkName(node.getName())) != null) {
								node.setName(newName = STGraphC.getMessage("NODE.DEFAULT_PREFIX") + ++graph.globalNodeCounter); //$NON-NLS-1$
							}
						}
						if(node.isModel()) {
							ModelNode mnode = (ModelNode)node;
							String modelName = mnode.getSubmodelName();
							if(modelName != null) {
								mnode.setModel(mnode.getSubmodelName(), false);
								mnode.addVars(mnode.getSubmodel());
							}
						} else {
							interpreter.addVariable(node.getName(), null);
						}
					} else if(STTools.isEdge(ob)) {
						STEdge edge = (STEdge)ob;
						if(!graph.checkAndRemoveIfWrongEdge(edge)) {
							if(stc.isPasting) { pastedNodes.add(edge); }
						}
						try { edge.setName(edge.getUserObject().toString()); } catch (Exception ex) { ; }
						graph.handleInputOnChanges();
					} else if(STTools.isText(ob)) {
						if(stc.isPasting) { // what follows is just a trick to add clipboard awareness to text nodes...
							TextNode node = (TextNode)ob;
							Rectangle2D bounds = node.getBounds();
							String content = node.getContent();
							stc.getCurrentGraph().getModel().remove(new Object[] { node });
							Rectangle bounds2 = new Rectangle((int)bounds.getX() + 10, (int)bounds.getY() + 10, (int)bounds.getWidth(), (int)bounds.getHeight());
							stc.isPasting = false; // to avoid recursion...
							isTextNodeHandling = true;
							TextNode newNode = STFactory.textCreate(content, bounds2);
							isTextNodeHandling = false;
							stc.isPasting = true;
							pastedNodes.add(newNode);
						}
					}
				}
			} else if((objects = k.getRemoved()) != null) { // node/edge(s) removed
				for(Object ob : objects) {
					if(STTools.isNode(ob)) {
						graph.removeNotConnectedEdges();
						((STNode)ob).handleNodeRemoval();
					} else if(STTools.isWidget(ob)) { 
						((STWidget)ob).handleWidgetRemoval(); 
					} else if(STTools.isEdge(ob)) {
						graph.handleInputOnChanges();
					}
				}
			} else if((objects = k.getChanged()) != null) { // node/edge(s) modified
				for(Object ob : objects) {
					if(STTools.isNode(ob)) {
						STNode node = (STNode)ob;
						if((node.checkName(node.getName())) != null) {
							node.setName(STGraphC.getMessage("NODE.DEFAULT_PREFIX") + ++graph.globalNodeCounter); //$NON-NLS-1$
						}
					} else if(STTools.isEdge(ob)) {
						STEdge edge = (STEdge)ob;
						graph.checkAndRemoveIfWrongEdge(edge);
						try { edge.setName(edge.getUserObject().toString()); } catch (Exception ex) { ; }
					}
				}
			}
		}
		if(WidgetListDialog.isDialogVisible()) { WidgetListDialog.showDialog(); }
		if(NodeListDialog.isDialogVisible()) { NodeListDialog.showDialog(); }
		if(GroupListDialog.isDialogVisible()) { GroupListDialog.showDialog(); }
		STListPane.setLists();
		graph.setAsModified(true);
		graph.setLists();
		graph.setupModel();
		if(graph.getLastError() != null) { graph.setRunnable(false); }
		else {
			graph.checkAllDefinitions(false); // pre-check the definitions
			if(graph.getLastError() != null) { graph.setRunnable(false); }
			else { graph.setRunnable((graph.getCellCount() != 0) && graph.isTopGraph()); }
		}
		STStatusBar sb = STGraphC.getStatusBar();
		if(sb != null) {
			if(graph.getLastError() == null) {
				if(selectedNode == null) { sb.setInfoText(STTools.BLANK, 0); }
			} else { sb.setInfoText(graph.getLastError(), 1); }
		}
		stc.refreshBars();
		graph.refreshGraph();
	}

}
