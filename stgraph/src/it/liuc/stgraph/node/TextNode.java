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
package it.liuc.stgraph.node;

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.util.STTools;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Locale;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;

import com.hexidec.ekit.Ekit;
import com.hexidec.ekit.EkitCaller;


/**
 * Text node.
 */
@SuppressWarnings("serial")
public class TextNode extends DefaultGraphCell implements EkitCaller {
	/** The Constant NAME_PREFIX. */
	public static final String NAME_PREFIX = "*text"; //$NON-NLS-1$

	/** The text dialog. */
	private transient Ekit textDialog;
	/** The default node bounds. */
	private static transient Rectangle defaultNodeBounds;
	/** The node name (required to deal with texts in groups). */
	private String name = null;
	/** The content. */
	private String content = null;

	static {
		String prefix = "TEXTNODE"; //$NON-NLS-1$
		String[] bounds = STConfigurator.getProperty(prefix + ".BOUNDS").split(","); //$NON-NLS-1$ //$NON-NLS-2$
		defaultNodeBounds = new Rectangle(Integer.parseInt(bounds[0]), Integer.parseInt(bounds[1]), Integer.parseInt(bounds[2]), Integer.parseInt(bounds[3]));
	}


	/**
	 * Class constructor.
	 */
	public TextNode() { this(null); }


	/**
	 * Class constructor.
	 *
	 * @param _userObject the _user object
	 */
	public TextNode(final Object _userObject) { super(_userObject); }


	/**
	 * Set the name of this node.
	 *
	 * @param name the name to be set
	 */
	public final void setName(final String name) { this.name = name; }


	/**
	 * (Generate and) get the name of this node.
	 *
	 * @return name
	 */
	public final String getName() {
		if(name != null) { return name; }
		TextNode[] texts = STGraph.getSTC().getCurrentGraph().getTexts();
		if(texts == null || texts.length == 0) { return name = NAME_PREFIX + "1"; } //$NON-NLS-1$
		int max = 0;
		String t;
		for(TextNode node : texts) {
			if((t = node.name) != null) {
				try { max = Math.max(max, Integer.parseInt(t.substring(NAME_PREFIX.length()))); } catch (Exception e) { ; }
			}
		}
		max++;
		return name = NAME_PREFIX + max;
	}


	/**
	 * Return a reference to the view for this node.
	 *
	 * @param o the o
	 *
	 * @return view
	 */
	public final VertexView getView(final Object o) { return null; }


	/**
	 * Get the bounds of this node.
	 *
	 * @return bounds
	 */
	public final Rectangle2D getBounds() { return GraphConstants.getBounds(getAttributes()); }


	/**
	 * Get the dialog for this node.
	 *
	 * @return dialog
	 */
	public final Ekit getDialog() {
		if(textDialog == null) {
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
			textDialog = new Ekit(this, sDocument, sStyleSheet, sRawDocument, urlStyleSheet, includeToolBar, includeViewSource, includeMenuIcons, modeExclusive, sLang, sCtry, base64, debugOn, spellCheck, multibar, enterBreak);
			textDialog.setAlwaysOnTop(true);
		}
		return textDialog;
	}


	/**
	 * Open the configuration dialog for this node.
	 * <br>This is controlled by the <code>ToolsEditProperties.exec()</code> method.
	 */
	public final void openDialog() {
		/* "smart positioning disabled because not correctly working in the case of non default zoom
        Rectangle2D rect = GraphConstants.getBounds(getAttributes());
        Point point = STGraphC.getContainer().getLocation();
        Point point2 = STGraph.getSTC().getCurrentDesktop().getGraphFrame2().getViewport().getViewPosition();
        int posx = (int)(point.getX() + rect.getX() - point2.getX());
        int posy = (int)(point.getY() + rect.getY() - point2.getY());
		 */
		int posx = STGraphC.getContainer().getX() + 50;
		int posy = STGraphC.getContainer().getY() + 50;
		String text = getContent();
		text = text.equals(STGraphC.getMessage("NODE.DEFAULT_TEXT")) ? null : text; //$NON-NLS-1$
		getDialog().open(posx, posy, text);
	}


	/**
	 * Close the configuration dialog for this node.
	 * <br>This is a callback method, related to the <code>EkitCaller</code> interface.
	 */
	public final void closingDialog() {
		String c = getContent();
		String text = getDialog().getEkitCore().getDocumentText();
		if((c == null && text != null) || (c != null && !c.equals(text))) { STGraph.getSTC().getCurrentGraph().setAsModified(true); }
		setContent(text);
		STGraph.getSTC().getCurrentGraph().refreshGraph();
	}


	/**
	 * Get the default node bounds.
	 *
	 * @return the defaultNodeBounds
	 */
	public static Rectangle getDefaultNodeBounds() { return defaultNodeBounds; }


	/**
	 * Set the content of this node.
	 *
	 * @param content the content to set
	 */
	public final void setContent(final String content) {
		this.content = STTools.adaptImagesToHTML(content);
		setUserObject(this.content);
	}


	/**
	 * Set the content of this node.
	 *
	 * @return the content
	 */
	public final String getContent() { return content; }

}
