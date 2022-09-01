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
package it.liuc.stgraph.util;

import javax.swing.JOptionPane;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.node.STNode;


/** Handler for interrupt conditions.
 * <br>The possible actions are:
 * <li>pause("message")
 * <li>end("message") */
public final class InterruptCondition {
	/** NO_ACTION constant. */
	public static final int NO_ACTION = 0;
	/** CONTINUE_ACTION constant. */
	public static final int CONTINUE_ACTION = 1;
	/** STOP_ACTION constant. */
	public static final int STOP_ACTION = 2;

	private static final String PAUSE = "pause"; //$NON-NLS-1$
	private static final String WARN = "warn"; //$NON-NLS-1$
	private static final String END = "end"; //$NON-NLS-1$


	/** Handle an interrupt condition.
	 * @param node node which generated the interrupt
	 * @param action action to be executed
	 * @return result: one of the defined action constants (nothing to do; continue; stop) */
	public static int handle(STNode node, String action) {
		if(action.startsWith(PAUSE)) {
			String msg = ""; //$NON-NLS-1$
			try { msg = action.substring(PAUSE.length() + 2, action.length() - 2); } catch (Exception e) { ; }
			msg += "\n" + STGraphC.getMessage("MSG.CONTINUE") + "?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			int result = JOptionPane.showConfirmDialog(STGraph.getSTC(), msg, STTools.BLANK, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if(result == JOptionPane.OK_OPTION) { return CONTINUE_ACTION; }
			return STOP_ACTION;
		}
		if(action.startsWith(WARN)) {
			String msg = ""; //$NON-NLS-1$
			try { msg = action.substring(WARN.length() + 2, action.length() - 2); } catch (Exception e) { ; }
			JOptionPane.showMessageDialog(STGraph.getSTC(), msg, STTools.BLANK, JOptionPane.INFORMATION_MESSAGE);
			return CONTINUE_ACTION;
		}
		if(action.startsWith(END)) {
			String msg = ""; //$NON-NLS-1$
			try { msg = action.substring(END.length() + 2, action.length() - 2); } catch (Exception e) { ; }
			JOptionPane.showMessageDialog(STGraph.getSTC(), msg, STTools.BLANK, JOptionPane.ERROR_MESSAGE);
			return STOP_ACTION;
		}
		return NO_ACTION;
	}

}
