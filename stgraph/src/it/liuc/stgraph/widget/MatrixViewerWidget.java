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

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;
import it.liuc.stgraph.util.STTools;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import net.ericaro.surfaceplotter.JSurfacePanel;
import net.ericaro.surfaceplotter.JSurfacePanelCaller;
import net.ericaro.surfaceplotter.surface.AbstractSurfaceModel;
import net.ericaro.surfaceplotter.surface.Projector;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotColor;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotType;


/** Matrix viewer widget. */
@SuppressWarnings("serial")
public class MatrixViewerWidget extends STWidget implements JSurfacePanelCaller {
	/** The Constant PREFIX. */
	public static final String PREFIX = "MATRIXVIEWER"; //$NON-NLS-1$
	/** The Constant ID. */
	public static final String ID = "MatrixViewerWidget"; //$NON-NLS-1$
	/** The Constant PROP_SOURCE_OB. */
	public static final String PROP_SOURCE_OB = "sourceob";	// reference to node //$NON-NLS-1$
	/** The Constant PROP_SOURCE_NA. */
	public static final String PROP_SOURCE_NA = "sourcena"; // node name //$NON-NLS-1$
	/** The Constant PROP_BOXED. */
	public static final String PROP_BOXED = "boxed"; // is boxed? //$NON-NLS-1$
	/** The Constant PROP_AUTOSCALE. */
	public static final String PROP_AUTOSCALEZ = "autoscalez"; // is autoscale z? //$NON-NLS-1$
	/** The Constant PROP_DISPLAYXY. */
	public static final String PROP_DISPLAYXY = "displayxy"; // is display x-y? //$NON-NLS-1$
	/** The Constant PROP_DISPLAYZ. */
	public static final String PROP_DISPLAYZ = "displayz"; // is display z? //$NON-NLS-1$
	/** The Constant PROP_DISPLAYGRIDS. */
	public static final String PROP_DISPLAYGRIDS = "displaygrids"; // is display grids? //$NON-NLS-1$
	/** The Constant PROP_MESH. */
	public static final String PROP_MESH = "mesh"; // is mesh? //$NON-NLS-1$
	/** The Constant PROP_PLOTCOLOR. */
	public static final String PROP_PLOTCOLOR = "plotcolor"; // plot color //$NON-NLS-1$
	/** The Constant PROP_PLOTTYPE. */
	public static final String PROP_PLOTTYPE = "plottype"; // plot type //$NON-NLS-1$
	/** The Constant PROP_PROJDISTANCE. */
	public static final String PROP_PROJDISTANCE = "projdistance"; // projector distance //$NON-NLS-1$
	/** The Constant PROP_PROJ2DSCALING. */
	public static final String PROP_PROJ2DSCALING = "proj2dscaling"; // projector 2D scaling //$NON-NLS-1$
	/** The Constant PROP_PROJROTATIONANGLE. */
	public static final String PROP_PROJROTATIONANGLE = "projrotationangle"; // projector rotation angle //$NON-NLS-1$
	/** The Constant PROP_PROJELEVATIONANGLE. */
	public static final String PROP_PROJELEVATIONANGLE = "projelevationangle"; // projector elevation angle //$NON-NLS-1$

	/** The paner viewer. */
	private final JSurfacePanel panelViewer = new JSurfacePanel(this, STGraphC.getSTLocale());
	/** The source. */
	private ValueNode source;
	/** The source name. */
	private String sourceName;


	/** Class constructor. */
	public MatrixViewerWidget() { super(); }


	/** Class constructor.
	 * @param _userObject the _user object */
	public MatrixViewerWidget(final Object _userObject) { super(_userObject); }


	/** Get the view for this widget.
	 * @return view */
	public final WidgetView getView() {
		if(view == null) { view = new MatrixViewerWidgetView(this); }
		return view;
	}


	/** Get the dialog for this node.
	 * @return dialog */
	public final WidgetDialog getDialog() {
		if(dialog == null) { dialog = new MatrixViewerWidgetDialog(); }
		return dialog;
	}


	/** Get the label identifying this widget type.
	 * @return label */
	public final String getLabel() { return STGraphC.getMessage("MATRIXVIEWER.LABEL"); } //$NON-NLS-1$


	/** Get the panel viewer.
	 * @return panel */
	public final JSurfacePanel getPanelViewer() { return panelViewer; }


	/** Get the source.
	 * @return source */
	public final ValueNode getSource() { return source; }


	/** Set panel.
	 * @param bounds the bounds */
	@Override
	public final void setPanel(final Rectangle bounds) {
		super.setPanel(bounds);
		panel.setLayout(new BorderLayout());

		/*
        frame.addMouseListener(new MouseAdapter() {
			@Override
            public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override
            public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
        });

        panelViewer.addMouseListener(new MouseAdapter() {
			@Override
            public void mousePressed(final MouseEvent e) { if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); } }
			@Override
            public void mouseReleased(final MouseEvent e) {
				if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
				panel.requestFocus(); // required to maintain the possibility of handling keyboard events
			}
        });
		 */

		panelViewer.setTitleVisible(false);
		panelViewer.setConfigurationVisible(false);

		panel.add(panelViewer, BorderLayout.CENTER);
		setTitle();
	}


	/** Set the title to be displayed in the frame of this widget. */
	public final void setTitle() { setTitle(!STTools.isEmpty(sourceName) ? sourceName : STTools.BLANK); }


	/** Set the properties of this widget.
	 * @param name the name
	 * @return result */
	final boolean setProps(final String name) {
		try {
			STNode[] nodes = STGraph.getSTC().getCurrentGraph().getNodes();
			STNode node = null;
			boolean found = false;
			for(int i = 0; i < nodes.length; i++) {
				node = nodes[i];
				if(node.getName().equals(name)) {
					if(!node.isOutput()) { throw new Exception(); }
					found = true;
					break;
				}
			}
			if(!found) { throw new Exception(); }
			source = (ValueNode)node;
			sourceName = name;
			setTitle();
			refresh();
		} catch (Exception ex) {
			STTools.messenger(STTools.MESSAGETYPE_WAR, STGraphC.getMessage("ERR_WRONG_MATRIXVIEWER_FORMAT")); return false; //$NON-NLS-1$
		}
		return true;
	}


	/** Check whether the renamed node is referenced by the toggle indicator, and in that case set the new name accordingly.
	 * @param oldName the old name
	 * @param newName the new name */
	public final void handleNodeRenaming(final String oldName, final String newName) {
		if(!STTools.isEmpty(sourceName) && sourceName.equals(oldName)) { sourceName = newName; }
		setTitle();
	}


	/** Handle the event of this widget removal. */
	public void handleWidgetRemoval() { ; }


	/** Check whether the removed node is referenced by the toggle indicator, and in that case set the name to <code>null</code>.
	 * @param name the name */
	public final void handleNodeRemoval(final String name) {
		if(!STTools.isEmpty(sourceName) && sourceName.equals(name)) {
			source = null;
			sourceName = null;
		}
		setTitle();
	}


	/** Transfer the data of this node to the attribute map used for saving. */
	@SuppressWarnings("unchecked")
	public final void prepareForSave() {
		super.prepareForSave();
		if(!STTools.isEmpty(sourceName)) { dataToSave.put(MatrixViewerWidget.PROP_SOURCE_NA, sourceName); }
		AbstractSurfaceModel asm = panelViewer.getModel();
		dataToSave.put(MatrixViewerWidget.PROP_BOXED, Boolean.toString(asm.isBoxed()));
		dataToSave.put(MatrixViewerWidget.PROP_AUTOSCALEZ, Boolean.toString(asm.isAutoScaleZ()));
		dataToSave.put(MatrixViewerWidget.PROP_DISPLAYXY, Boolean.toString(asm.isDisplayXY()));
		dataToSave.put(MatrixViewerWidget.PROP_DISPLAYZ, Boolean.toString(asm.isDisplayZ()));
		dataToSave.put(MatrixViewerWidget.PROP_DISPLAYGRIDS, Boolean.toString(asm.isDisplayGrids()));
		dataToSave.put(MatrixViewerWidget.PROP_MESH, Boolean.toString(asm.isMesh()));
		dataToSave.put(MatrixViewerWidget.PROP_PLOTCOLOR, asm.getPlotColor());
		dataToSave.put(MatrixViewerWidget.PROP_PLOTTYPE, asm.getPlotType());
		Projector p = asm.getProjector();
		dataToSave.put(MatrixViewerWidget.PROP_PROJDISTANCE, Float.toString(p.getDistance()));
		dataToSave.put(MatrixViewerWidget.PROP_PROJ2DSCALING, Float.toString(p.get2DScaling()));
		dataToSave.put(MatrixViewerWidget.PROP_PROJROTATIONANGLE, Float.toString(p.getRotationAngle()));
		dataToSave.put(MatrixViewerWidget.PROP_PROJELEVATIONANGLE, Float.toString(p.getElevationAngle()));
	}


	/** Define the data of this node from the loaded attribute map. */
	public final void restoreAfterLoad() {
		super.restoreAfterLoad();
		String name = (String)dataToSave.get(MatrixViewerWidget.PROP_SOURCE_NA);
		if(!STTools.isEmpty(name)) { setProps(name); }
		AbstractSurfaceModel asm = panelViewer.getModel();
		asm.setBoxed(Boolean.valueOf((String)dataToSave.get(MatrixViewerWidget.PROP_BOXED)).booleanValue());
		asm.setAutoScaleZ(Boolean.valueOf((String)dataToSave.get(MatrixViewerWidget.PROP_AUTOSCALEZ)).booleanValue());
		asm.setDisplayXY(Boolean.valueOf((String)dataToSave.get(MatrixViewerWidget.PROP_DISPLAYXY)).booleanValue());
		asm.setDisplayZ(Boolean.valueOf((String)dataToSave.get(MatrixViewerWidget.PROP_DISPLAYZ)).booleanValue());
		asm.setDisplayGrids(Boolean.valueOf((String)dataToSave.get(MatrixViewerWidget.PROP_DISPLAYGRIDS)).booleanValue());
		asm.setMesh(Boolean.valueOf((String)dataToSave.get(MatrixViewerWidget.PROP_MESH)).booleanValue());
		String s = (String)dataToSave.get(MatrixViewerWidget.PROP_PLOTCOLOR);
		if(!STTools.isEmpty(s)) {
			if(s.equals("OPAQUE")) { asm.setPlotColor(PlotColor.OPAQUE); } //$NON-NLS-1$
			else if(s.equals("SPECTRUM")) { asm.setPlotColor(PlotColor.SPECTRUM); } //$NON-NLS-1$
			else if(s.equals("GRAYSCALE")) { asm.setPlotColor(PlotColor.GRAYSCALE); } //$NON-NLS-1$
			else if(s.equals("DUALSHADE")) { asm.setPlotColor(PlotColor.DUALSHADE); } //$NON-NLS-1$
			else { asm.setPlotColor(PlotColor.FOG); }
		} else { asm.setPlotColor(PlotColor.FOG); }
		s = (String)dataToSave.get(MatrixViewerWidget.PROP_PLOTTYPE);
		if(!STTools.isEmpty(s)) {
			if(s.equals("WIREFRAME")) { asm.setPlotType(PlotType.WIREFRAME); } //$NON-NLS-1$
			else if(s.equals("SURFACE")) { asm.setPlotType(PlotType.SURFACE); } //$NON-NLS-1$
			else if(s.equals("CONTOUR")) { asm.setPlotType(PlotType.CONTOUR); } //$NON-NLS-1$
			else { asm.setPlotType(PlotType.DENSITY); }
		} else { asm.setPlotType(PlotType.DENSITY); }
		Projector p = asm.getProjector();
		s = (String)dataToSave.get(MatrixViewerWidget.PROP_PROJDISTANCE);
		if(!STTools.isEmpty(s)) { p.setDistance(Float.valueOf(s).floatValue()); }
		s = (String)dataToSave.get(MatrixViewerWidget.PROP_PROJ2DSCALING);
		if(!STTools.isEmpty(s)) { p.set2DScaling(Float.valueOf(s).floatValue()); }
		s = (String)dataToSave.get(MatrixViewerWidget.PROP_PROJROTATIONANGLE);
		if(!STTools.isEmpty(s)) { p.setRotationAngle(Float.valueOf(s).floatValue()); }
		s = (String)dataToSave.get(MatrixViewerWidget.PROP_PROJELEVATIONANGLE);
		if(!STTools.isEmpty(s)) { p.setElevationAngle(Float.valueOf(s).floatValue()); }
	}


	/** Get the source name.
	 * @return the sourceName */
	public final String getSourceName() { return sourceName; }


	public void clickingMouse(MouseEvent e) { ; }


	public void pressingMouse(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)) { defaultRightClickAction(frame); }
	}


	public void releasingMouse(MouseEvent e) {
		/*
		if(e.getClickCount() == 1 && e.isControlDown()) {
	        panelViewer.toggleConfiguration();
		}
		 */
		if(e.getClickCount() == 2) { defaultDoubleClickAction(); }
		panel.requestFocus(); // required to maintain the possibility of handling keyboard events
	}

}
