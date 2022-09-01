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

import it.liuc.stgraph.action.AnalysisDefinedNodes;
import it.liuc.stgraph.action.AnalysisDefiningNodes;
import it.liuc.stgraph.action.AnalysisDirectlyDefinedNodes;
import it.liuc.stgraph.action.AnalysisDirectlyDefiningNodes;
import it.liuc.stgraph.action.AnalysisIncomingArrows;
import it.liuc.stgraph.action.AnalysisLoops;
import it.liuc.stgraph.action.AnalysisOutgoingArrows;
import it.liuc.stgraph.action.EditCopy;
import it.liuc.stgraph.action.EditCopyGraph;
import it.liuc.stgraph.action.EditCopyWidget;
import it.liuc.stgraph.action.EditCut;
import it.liuc.stgraph.action.EditNodeProperties;
import it.liuc.stgraph.action.EditPaste;
import it.liuc.stgraph.action.EditRemove;
import it.liuc.stgraph.action.EditModelProperties;
import it.liuc.stgraph.action.ToolsShowProperties;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ModelNode;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.widget.STWidget;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;


/** Popup menu. */
@SuppressWarnings("serial")
public class STPopupMenu extends JPopupMenu {


	/** Class constructor.
	 * @param cell the cell */
	public STPopupMenu(final Object cell) {
		super();
		STGraphC stc = STGraph.getSTC();
		final STGraphExec graph = stc.getCurrentGraph();
		String s1;
		String s2;
		if(cell != null) { s1 = "POPMENU_PROPERTIES"; s2 = "POPMENU_EDIT"; }
		else { s1 = "POPMENU_SYS_PROPERTIES"; s2 = "POPMENU_SYS_EDIT"; }
		if(cell == null) {
			add(new AbstractAction(STGraphC.getMessage(s1)) { public void actionPerformed(final ActionEvent e) { ((ToolsShowProperties)STGraphC.getAction("ToolsShowProperties")).exec(); } });
			if(graph.isEditable) { add(new AbstractAction(STGraphC.getMessage(s2)) { public void actionPerformed(final ActionEvent e) { ((EditModelProperties)STGraphC.getAction("EditModelProperties")).exec(); } }); }
			if(graph.isEditable && stc.isInClip()) { addSeparator(); }
		} else if(STTools.isNode(cell)) {
			add(new AbstractAction(STGraphC.getMessage(s1)) { public void actionPerformed(final ActionEvent e) { ((ToolsShowProperties)STGraphC.getAction("ToolsShowProperties")).exec(); } });
			if(graph.isEditable) { add(new AbstractAction(STGraphC.getMessage(s2)) { public void actionPerformed(final ActionEvent e) { ((EditNodeProperties)STGraphC.getAction("EditNodeProperties")).exec(); } }); }
			if(graph.isEditable && (!graph.isSelectionEmpty() || stc.isInClip())) { addSeparator(); }
			if(graph.isEditable && !graph.isSelectionEmpty()) {
				add(new AbstractAction(STGraphC.getMessage("POPMENU_REMOVE")) { public void actionPerformed(final ActionEvent e) { ((EditRemove)STGraphC.getAction("EditRemove")).exec(); } });
				add(new AbstractAction(STGraphC.getMessage("POPMENU_CUT")) { public void actionPerformed(final ActionEvent e) { ((EditCut)STGraphC.getAction("EditCut")).exec(); } });
				add(new AbstractAction(STGraphC.getMessage("POPMENU_COPY")) { public void actionPerformed(final ActionEvent e) { ((EditCopy)STGraphC.getAction("EditCopy")).exec(); } });
			}
			if(graph.isEditable) { addSeparator(); }
			add(new AbstractAction(STGraphC.getMessage("POPMENU_SELECTDEFININGNODES")) { public void actionPerformed(final ActionEvent e) { ((AnalysisDefiningNodes)STGraphC.getAction("AnalysisDefiningNodes")).exec(); } });
			add(new AbstractAction(STGraphC.getMessage("POPMENU_SELECTDIRECTLYDEFININGNODES")) { public void actionPerformed(final ActionEvent e) { ((AnalysisDirectlyDefiningNodes)STGraphC.getAction("AnalysisDirectlyDefiningNodes")).exec(); } });
			add(new AbstractAction(STGraphC.getMessage("POPMENU_SELECTDEFINEDNODES")) { public void actionPerformed(final ActionEvent e) { ((AnalysisDefinedNodes)STGraphC.getAction("AnalysisDefinedNodes")).exec(); } });
			add(new AbstractAction(STGraphC.getMessage("POPMENU_SELECTDIRECTLYDEFINEDNODES")) { public void actionPerformed(final ActionEvent e) { ((AnalysisDirectlyDefinedNodes)STGraphC.getAction("AnalysisDirectlyDefinedNodes")).exec(); } });
			add(new AbstractAction(STGraphC.getMessage("POPMENU_ANALYSISINCOMINGARROWS")) { public void actionPerformed(final ActionEvent e) { ((AnalysisIncomingArrows)STGraphC.getAction("AnalysisIncomingArrows")).exec(); } });
			add(new AbstractAction(STGraphC.getMessage("POPMENU_ANALYSISOUTGOINGARROWS")) { public void actionPerformed(final ActionEvent e) { ((AnalysisOutgoingArrows)STGraphC.getAction("AnalysisOutgoingArrows")).exec(); } });
			add(new AbstractAction(STGraphC.getMessage("POPMENU_HIGHLIGHTLOOPS")) { public void actionPerformed(final ActionEvent e) { ((AnalysisLoops)STGraphC.getAction("AnalysisLoops")).exec(); } });
		} else if(STTools.isWidget(cell)) {
			add(new AbstractAction(STGraphC.getMessage(s2)) { public void actionPerformed(final ActionEvent e) { ((EditNodeProperties)STGraphC.getAction("EditNodeProperties")).exec(); } });
			add(new AbstractAction(STGraphC.getMessage("POPMENU_REMOVE")) { public void actionPerformed(final ActionEvent e) { ((EditRemove)STGraphC.getAction("EditRemove")).exec(); } });
			addSeparator();
			add(new AbstractAction(STGraphC.getMessage("POPMENU_WIDGETCOPY")) { public void actionPerformed(final ActionEvent e) { ((EditCopyWidget)STGraphC.getAction("EditCopyWidget")).exec(); } });
			addSeparator();
			add(new AbstractAction(((STWidget)cell).isShowTitleBar() ? STGraphC.getMessage("POPMENU_HIDETITLE") : STGraphC.getMessage("POPMENU_SHOWTITLE")) { public void actionPerformed(final ActionEvent e) { ((STWidget)cell).switchShowTitleBar(); } });
		}
		if(graph.isEditable && stc.isInClip()) { add(new AbstractAction(STGraphC.getMessage("POPMENU_PASTE")) { public void actionPerformed(final ActionEvent e) { ((EditPaste)STGraphC.getAction("EditPaste")).exec(); } }); }
		if(cell == null) {
			addSeparator();
			add(new AbstractAction(STGraphC.getMessage("POPMENU_GRAPHCOPY")) { public void actionPerformed(final ActionEvent e) { ((EditCopyGraph)STGraphC.getAction("EditCopyGraph")).exec(); } });
		}
		if(cell != null && STTools.isNode(cell) && ((STNode)cell).isModel()) {
			addSeparator();
			add(new AbstractAction(STGraphC.getMessage("POPMENU_SUBSYSTEM_WRAPPER")) { public void actionPerformed(final ActionEvent e) { ((ModelNode)cell).createWrapper(); } });
		}
	}


	/** Position and show this popup menu.
	 * @param graph the graph
	 * @param mif the mif */
	public final void show(final STGraphExec graph, final Container mif) {
		Point p = mif.getMousePosition();
		if(p != null) { super.show(graph, mif.getX() + p.x + 5, mif.getY() + p.y + 5); }
	}

}
