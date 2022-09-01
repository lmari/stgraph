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

import it.liuc.stgraph.action.AbstractActionDefault;
import it.liuc.stgraph.util.STTools;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;


/** Handle the toolbar. */
@SuppressWarnings("serial")
public class STToolBar extends JToolBar {
	/** The actions. */
	private transient AbstractActionDefault[] actions;
	/** The progress bar. */
	private JProgressBar progressBar;

	private int playCounter = 0;
	private String[] playMarker = new String[] {"   ", ".  ", ".. ", "..."}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$


	/** Class constructor.
	 * @param _actions the _actions */
	public STToolBar(final AbstractActionDefault[] _actions) {
		super();
		setFloatable(false);
		setRollover(true);
		actions = _actions;
		Component c;
		for(AbstractActionDefault action : actions) {
			c = action.prepareForToolBar();
			if(c != null) { add(c); }
		}
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		add(progressBar);
		updateItems();

		for(Component component : getComponents()) {
			if(component instanceof JButton) {
				((JButton)component).putClientProperty("JComponent.sizeVariant", "mini"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

	}


	/** Update the progress controls.
	 * @param val the val
	 * @param max the max */
	public final void updateProgressControls(final int val, final int max) {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		if(graph.getTimeFrame() != STGraphImpl.TIMEFRAME_PLAYMODE) {
			int xval = val >= 0 ? val : 0;
			progressBar.setValue(xval);
			progressBar.setMaximum(max);
			progressBar.setString(xval + STTools.SLASH + max + STTools.SPACE + STGraph.getSTC().getCurrentGraph().getTimeUnitDescription());
		} else {
			progressBar.setValue(0);
			progressBar.setMaximum(1);
			progressBar.setString("playmode" + playMarker[playCounter++]); //$NON-NLS-1$
			if(playCounter == 4) { playCounter = 0; }
		}
	}


	/** Update the progress controls and set their visibility state.
	 * @param val the val
	 * @param max the max
	 * @param visible the visibility */
	public final void updateProgressControls(final int val, final int max, final boolean visible) {
		updateProgressControls(val, max);
		progressBar.setVisible(visible);
	}


	/** Handle the activation and deactivation of the toolbar items in a centralized way. */
	public final void updateItems() {
		JButton b;
		AbstractActionDefault a;
		for(Component component : getComponents()) {
			if(component instanceof JButton) {
				b = (JButton)component;
				a = (AbstractActionDefault)b.getAction();
				a.setEnabledOnState();
				b.setVisible(a.isVisible());
			}
		}
		STGraphC stc = STGraph.getSTC();
		if(!stc.isCurrentDesktop()) {
			progressBar.setVisible(false);
		} else {
			STGraphImpl graph = stc.getCurrentGraph();
			if(graph.getState() == STGraphImpl.STATE_RUNNING || graph.getState() == STGraphImpl.STATE_BATCHRUNNING) { return; }
			if(graph.getState() == STGraphImpl.STATE_STEPPING || graph.getState() == STGraphImpl.STATE_TIMING) {
				int val = graph.currStep;
				int max = graph.computeNumSteps() - 1;
				updateProgressControls(val, max);
				progressBar.setVisible(true);
			} else {
				progressBar.setVisible(false);
			}
		}
	}

}
