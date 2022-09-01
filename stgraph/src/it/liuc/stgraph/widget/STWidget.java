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
package it.liuc.stgraph.widget;

import it.liuc.stgraph.STFrame;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STGraphImpl;
import it.liuc.stgraph.STInternalFrame;
import it.liuc.stgraph.STPopupMenu;
import it.liuc.stgraph.action.EditNodeProperties;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.WidgetListDialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexRenderer;


/** Superclass for widgets. */
@SuppressWarnings("serial")
public abstract class STWidget extends DefaultGraphCell {
	/** The graph. */
	protected transient STGraphImpl graph;
	/** The widget view. */
	protected WidgetView view = null;
	/** The widget dialog. */
	protected WidgetDialog dialog;
	/** The type. */
	private String type;
	/** The Constant WIDGET_INPUT. */
	public static final String WIDGET_INPUT = "InputWidget"; //$NON-NLS-1$
	/** The iconized. */
	public boolean iconized = false;
	/** The Constant PROP_ICONIZED. */
	public static final String PROP_ICONIZED = "iconized"; //$NON-NLS-1$
	/** The stable iconization. */
	public boolean stableIconization = false;
	/** The Constant PROP_TITLE. */
	public static final String PROP_TITLE = "title"; //$NON-NLS-1$
	/** The Constant PROP_SHOWTITLEBAR. */
	public static final String PROP_SHOWTITLEBAR = "showtitlebar"; //$NON-NLS-1$
	/** The show title bar. */
	public boolean showTitleBar = true;
	/** The frame. */
	public transient Container frame;
	/** The panel. */
	public transient JPanel panel;
	/** The data to save. */
	public AttributeMap dataToSave;
	/** The node bound to the input widget. */
	ValueNode node = null;


	/** Class constructor. */
	public STWidget() { this(null); }


	/** Class constructor.
	 * @param _userObject the user object */
	public STWidget(final Object _userObject) {
		super(_userObject);
		graph = STGraph.getSTC().getCurrentGraph();
	}


	/** Get the model to which this widget belongs.
	 * @return graph */
	public final STGraphExec getGraph() { return (STGraphExec)graph; }


	/** Get the view for this widget.
	 * @return view */
	public abstract WidgetView getView();


	/** Get the dialog for this widget.
	 * @return dialog */
	public abstract WidgetDialog getDialog();


	/** Open the configuration dialog for this widget.
	 * <br>This is controlled by the <code>ToolsEditProperties.exec()</code> method. */
	public final void openDialog() { getDialog().open(this); }


	/** Get the node bound to the input widget.
	 * @return node */
	public ValueNode getNode() { return node; }


	/** Refresh the view of this widget. */
	public final void refresh() {
		VertexRenderer vr;
		vr = (VertexRenderer)getView().getRenderer();
		vr.paint(vr.getGraphics());
	}


	/** Remove this widget. */
	public final void remove() {
		STGraph.getSTC().getCurrentDesktop().remove(frame);
		try { ((STNode)(getProperty(InputWidget.PROP_SOURCE_OB))).setInputWidget(null); } catch (Exception ex) { } // delete the reference to the input widget in the input node
		try { STGraph.getSTC().getCurrentGraph().getModel().remove(new Object[] {this}); } catch (Exception ex) { }
	}


	/** Set the position of this widget.
	 * @param pos the pos */
	public final void setPosition(final Point pos) { frame.setLocation(pos.x, pos.y); }


	/** Set the position of this widget.
	 * @param posX the posX
	 * @param posY the posY */
	public final void setPosition(final int posX, final int posY) { frame.setLocation(posX, posY); }


	/** Get the coordinate X of the position of this widget.
	 * @return the coordinate X */
	public final int getPositionX() { return frame.getLocation().x; }


	/** Get the coordinate Y of the position of this widget.
	 * @return the coordinate Y */
	public final int getPositionY() { return frame.getLocation().y; }


	/** Set the size of this widget.
	 * @param width the width
	 * @param height the height */
	public final void setSize(final int width, final int height) { frame.setSize(width, height); }


	/** Get the width of this widget.
	 * @return the width */
	public final int getWidth() { return frame.getSize().width; }


	/** Get the height of this widget.
	 * @return the height */
	public final int getHeight() { return frame.getSize().height; }


	/** Set the iconization state.
	 * @param b the b */
	public final void setIconized(final boolean b) {
		iconized = b;
		try { ((STInternalFrame)frame).setIcon(b); } catch (Exception e) { ; }
		if(WidgetListDialog.isDialogVisible()) { WidgetListDialog.showDialog(); }
	}


	/** Get the iconization state.
	 * @return iconized */
	public final boolean isIconized() { return iconized; }


	/** Set the show title bar state.
	 * @param b the b */
	public final void setShowTitleBar(final boolean b) {
		showTitleBar = b;
		JComponent c = ((STInternalFrame)frame).getTitleBar();
		Dimension d = new Dimension(c.getPreferredSize().width, b ? STInternalFrame.TITLEBAR_SIZE : 0);
		c.setMinimumSize(d);
		c.setSize(d);
		c.setPreferredSize(d);
		c.setMaximumSize(d);
		((STInternalFrame)frame).setBorder(b ? BorderFactory.createEtchedBorder() : BorderFactory.createLineBorder(Color.WHITE, 1));
		((STInternalFrame)frame).internalFrameDeiconified(null);
		refresh();
	}


	/** Switch the show title bar state. */
	public final void switchShowTitleBar() { setShowTitleBar(!showTitleBar); }


	/** Get the show title bar state.
	 * @return the show title bar state */
	public final boolean isShowTitleBar() { return showTitleBar; }


	/** Set the named property for this widget to the specified value.
	 * @param value the value
	 * @param name the name */
	@SuppressWarnings("unchecked")
	public final void setProperty(final String name, final Object value) {
		try {
			AttributeMap map = getAttributes();
			if(value != null) { map.put(name, value); }
			else { map.remove(name); }
			setAttributes(map);
		} catch (Exception e) { }
	}


	/** Get the named property for this widget.
	 * @param name the name
	 * @return property */
	public final Object getProperty(final String name) {
		AttributeMap map = getAttributes();
		return map.get(name);
	}


	/** Get the type of this widget.
	 * @return type */
	public final String getType() { return type; }


	/** Set the type of this node.
	 * @param t the type */
	public final void setType(final String t) { type = t; }


	/** Widget loader. */
	public final void loader() { setPanel((Rectangle)getAttributes().get("framebounds")); } //$NON-NLS-1$


	/** Set panel: common settings, to be specialized to specific widgets.
	 * @param bounds the bounds */
	public void setPanel(final Rectangle bounds) {
		frame = new STInternalFrame(this, STGraph.getSTC().getCurrentDesktop(), STGraph.getSTC().getCurrentGraph(), bounds);
		((STInternalFrame)frame).getContentPane().add(panel = new JPanel());
		setIconized(iconized);
		setTitle((String)getProperty(STWidget.PROP_TITLE));
		setShowTitleBar(showTitleBar);

		panel.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_F2) { defaultDoubleClickAction(); }
			}
		});

	}


	/** Define the default action for the double click event on the widget. */
	public void defaultDoubleClickAction() {
		graph.setSelectionCell(this);
		if(graph.getState() == STGraphImpl.STATE_EDITING) {
			((EditNodeProperties)STGraphC.getAction("EditNodeProperties")).exec(); //$NON-NLS-1$
		}
	}


	/** Define the default action for the right click event on the widget.
	 * @param mif the mif */
	public void defaultRightClickAction(final Container mif) {
		graph.setSelectionCell(this);
		if(graph.getState() == STGraphImpl.STATE_EDITING) {
			new STPopupMenu(STGraph.getSTC().getCurrentGraph().getSelectionCell()).show(STGraph.getSTC().getCurrentGraph(), mif);
		}
	}


	/** Get the label identifying the widget type.
	 * @return label */
	public abstract String getLabel();


	/** Get the text describing this widget contents.
	 * @return title */
	public String getTitle() { return (String)getProperty(STWidget.PROP_TITLE); }


	/** Wrapper: set the text describing this widget contents.
	 * @param title the title */
	public void setTitle(final String title) {
		try {
			if(frame instanceof STInternalFrame) { ((STInternalFrame)frame).setTitle(title); }
			else { ((STFrame)frame).setTitle(title); }
			setProperty(STWidget.PROP_TITLE, title);
		} catch (Exception e) { ; }
	}


	/** General handler for the event of this widget removal. */
	public abstract void handleWidgetRemoval();


	/** General handler for the event of node removal.
	 * @param name the name */
	public abstract void handleNodeRemoval(final String name);


	/** General handler for the event of node rename.
	 * @param oldName the old name
	 * @param newName the new name */
	public abstract void handleNodeRenaming(final String oldName, final String newName);


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	public void prepareForSave() {
		String t;
		dataToSave = new AttributeMap();
		AttributeMap am = getAttributes();
		if((t = (String)am.get(STWidget.PROP_TITLE)) != null) { dataToSave.put(STWidget.PROP_TITLE, t); }
		dataToSave.put(STWidget.PROP_ICONIZED, Boolean.valueOf(iconized));
		dataToSave.put(STWidget.PROP_SHOWTITLEBAR, Boolean.valueOf(showTitleBar));
	}


	/** Define the data of this node from the loaded attribute map. */
	public void restoreAfterLoad() {
		setIconized(Boolean.parseBoolean((String)dataToSave.get(STWidget.PROP_ICONIZED)));
		setShowTitleBar(Boolean.parseBoolean((String)dataToSave.get(STWidget.PROP_SHOWTITLEBAR)));
	}

}
