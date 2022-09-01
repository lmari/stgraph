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

import it.liuc.stgraph.fun.STParserTools;
import it.liuc.stgraph.node.STData;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.STTools;

import java.net.URL;
import java.util.Locale;
import java.util.Vector;

import org.nfunk.jep.type.Tensor;

import com.hexidec.ekit.Ekit;
import com.hexidec.ekit.EkitCaller;


/** Reporter class.
 * <br>Each report, that is stored in a model map with the report title as the key,
 * is an HTML string, that can include some specially formatted fields:
 * <li><code>$<i>node</i>$</code>: the current value of the node named <code><i>node</i></code>
 * is substituted
 * <li><code>|<i>cond</i>|<i>text</i>|</code>: <code><i>text</i></code> is substituted,
 * provided that the current value of the scalar node named <code><i>cond</i></code> is <code>true</code>
 * (<code><i>text</i></code> can contain one or more <code>$<i>node</i>$</code> references)
 * <li><code>|htable|<i>node1,node2,...</i>|</code>: a table is substituted, with the series
 * of values of scalar output nodes named <code><i>node1,node2,...</i></code> in each row */
public class STReporter implements EkitCaller {
	/** The singleton object of this class. */
	private static transient STReporter reporter = null;
	/** The text dialog. */
	private transient Ekit reportDialog = null;
	/** The graph. */
	private transient STGraphImpl graph;
	/** The report title. */
	private transient String title;

	private static final String CONDITION_DELIMITER = "|"; //$NON-NLS-1$
	private static final String NODE_DELIMITER = "$"; //$NON-NLS-1$


	/** Class constructor.
	 * <br>It is private so to implement a singleton pattern
	 * <br>(the need of maintaining the callback method prevents to instantiate it as a Spring bean). */
	private STReporter() {
		String sDocument = null;
		String sStyleSheet = null;
		String sRawDocument = null;
		URL urlStyleSheet = null;
		boolean includeToolBar = true;
		boolean includeViewSource = false;
		boolean includeMenuIcons = true;
		boolean modeExclusive = true;
		String sLang = null;
		String sCtry = null;
		boolean base64 = false;
		boolean debugOn = false;
		boolean spellCheck = false;
		boolean multibar = true;
		boolean enterBreak = true;
		if(STGraphC.getSTLocale().equals(Locale.ITALIAN)) {
			sLang = "it"; //$NON-NLS-1$
			sCtry = "it"; //$NON-NLS-1$
		}
		reportDialog = new Ekit(this, sDocument, sStyleSheet, sRawDocument, urlStyleSheet, includeToolBar, includeViewSource, includeMenuIcons, modeExclusive, sLang, sCtry, base64, debugOn, spellCheck, multibar, enterBreak);
	}


	/** Get the singleton instance of this class.
	 * @return the object */
	public static STReporter getReporter() {
		if(reporter == null) { reporter = new STReporter(); }
		return reporter;
	}


	/** Open the configuration dialog.
	 * <br>It is assumed that the <code>reportMap</code> for the specified <code>graph</code>
	 * already contains an entry with key <code>title</code>
	 * <br>(note a potential issue: the dialog is not modal, so that more than one of them could be opened
	 * at the same time, and the parameters would be wrong at closing time: to be fixed!
	 * -- e.g. by "registering" in a static map each dialog as it is opened).
	 * @param graph the graph that is going to include this report
	 * @param title the title of this report */
	public final void openDialog(final STGraphImpl graph, final String title) {
		this.graph = graph;
		this.title = title;
		reportDialog.open(10, 10, graph.reportMap.get(title));
	}


	/** Close the configuration dialog for this node.
	 * <br>This is a callback method, related to the <code>EkitCaller</code> interface. */
	public final void closingDialog() {
		String text = reportDialog.getEkitCore().getDocumentText();
		if(!text.equals(graph.reportMap.get(title))) {
			graph.reportMap.put(title, text);
			graph.setAsModified(true);
		}
	}


	/** Get the text of the specified report.
	 * @param graph the graph that includes the report
	 * @param title the title of the report */
	@SuppressWarnings("rawtypes")
	public final String getReport(final STGraphImpl graph, final String title) {
		String text = graph.reportMap.get(title);
		int i1;
		int i2;
		int i3;
		String leftText;
		String nodeName;
		String[] nodeNames;
		String nodeValue;
		STNode node;
		STNode[] nodes;
		int numNodes;
		String conditionedText;
		StringBuffer conditionedBuffer;
		while((i1 = text.indexOf(CONDITION_DELIMITER)) != -1) { // look for tables or conditions for handling them
			i2 = text.indexOf(CONDITION_DELIMITER, i1 + 1);
			i3 = text.indexOf(CONDITION_DELIMITER, i2 + 1);
			if(i2 != -1 && i3 != -1) { // the closing tags are present...
				leftText = text.substring(i1 + 1, i2);
				if(leftText.equals("htable")) { // htable //$NON-NLS-1$
					conditionedBuffer = new StringBuffer("<table border='1'><tr><th></th>"); //$NON-NLS-1$
					int numSteps = graph.currStep + 1;
					double t = graph.getTime0();
					for(int j = 0; j < numSteps; j++) {
						conditionedBuffer.append("<th>" + t + "</th>"); //$NON-NLS-1$ //$NON-NLS-2$
						t += graph.getTimeD();
					}
					conditionedBuffer.append("</tr>"); //$NON-NLS-1$
					nodeNames = text.substring(i2 + 1, i3).split(","); //$NON-NLS-1$
					numNodes = nodeNames.length;
					nodes = new STNode[numNodes];
					for(int i = 0; i < numNodes; i++) {
						conditionedBuffer.append("<tr><th>" + nodeNames[i] + "</th>"); //$NON-NLS-1$ //$NON-NLS-2$
						nodes[i] = graph.getNodeByName(nodeNames[i]);
						if(nodes[i] == null) {
							conditionedBuffer.append("<td colspan='" + numSteps + "'>" + STGraphC.getMessage("REPORTER.UNDEFINED") + "</td>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						} else if(!nodes[i].isScalar()) {
							conditionedBuffer.append("<td colspan='" + numSteps + "'>" + STGraphC.getMessage("REPORTER.NONSCALAR") + "</td>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						} else {
							Vector v = (Vector)nodes[i].getValueHistory();
							if(v == null) {
								conditionedBuffer.append("<td colspan='" + numSteps + "'>" + STGraphC.getMessage("REPORTER.NONOUTPUT") + "</td>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							} else {
								for(Object o : v) {
									conditionedBuffer.append("<td>" + STData.toString(o, STData.FORMAT_ALTERNATE) + "</td>"); //$NON-NLS-1$ //$NON-NLS-2$
								}
							}
						}
						conditionedBuffer.append("</tr>"); //$NON-NLS-1$
					}
					conditionedBuffer.append("</table>"); //$NON-NLS-1$
					text = text.substring(0, i1) + conditionedBuffer.toString() + text.substring(i3 + 1);

				} else { // conditional text
					nodeName = leftText;
					node = graph.getNodeByName(nodeName);
					if(node == null) {
						conditionedText = STTools.OPENV + nodeName + STTools.COLON + STTools.SPACE + STGraphC.getMessage("REPORTER.UNDEFINED") + STTools.CLOSEV; //$NON-NLS-1$
					} else {
						if(!node.isScalar()) {
							conditionedText = STTools.OPENV + nodeName + STTools.COLON + STTools.SPACE + STGraphC.getMessage("REPORTER.NONSCALAR") + STTools.CLOSEV; //$NON-NLS-1$
						} else {
							if(STParserTools.isTrue(((Tensor)node.getValue()).getValue())) {
								conditionedText = text.substring(i2 + 1, i3);
							} else {
								conditionedText = STTools.BLANK;
							}
						}
					}
					text = text.substring(0, i1) + conditionedText + text.substring(i3 + 1);
				}
			} else {
				text = text.substring(0, i1) + STTools.SPACE + STGraphC.getMessage("REPORTER.NONCLOSEDCONDITIONTAG"); //$NON-NLS-1$
			}
		}

		while((i1 = text.indexOf(NODE_DELIMITER)) != -1) { // look for node names for substituting them with their current values
			i2 = text.indexOf(NODE_DELIMITER, i1 + 1);
			if(i2 != -1) { // the closing tag is present...
				nodeName = text.substring(i1 + 1, i2);
				node = graph.getNodeByName(nodeName);
				if(node == null) {
					nodeValue = STTools.OPENV + nodeName + STTools.COLON + STTools.SPACE + STGraphC.getMessage("REPORTER.UNDEFINED") + STTools.CLOSEV; //$NON-NLS-1$
				} else {
					nodeValue = STData.toString(node.getValue(), STData.FORMAT_ALTERNATE);
				}
				text = text.substring(0, i1) + nodeValue + text.substring(i2 + 1);
			} else {
				text = text.substring(0, i1) + STTools.SPACE + STGraphC.getMessage("REPORTER.NONCLOSEDNODETAG"); //$NON-NLS-1$
			}
		}
		return text;
	}

}
