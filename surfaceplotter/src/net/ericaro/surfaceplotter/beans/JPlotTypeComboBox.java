package net.ericaro.surfaceplotter.beans;

import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotType;

/**
 * @author eric
 *
 */
public class JPlotTypeComboBox extends JEnumComboBox<PlotType> {

	String[] labels;
	
	/**
	 * 
	 */
	public JPlotTypeComboBox() {
		super(PlotType.values(), "plotType");
		labels = new String[PlotType.values().length];
		for (int i = 0; i < PlotType.values().length; i++)
			labels[i] = PlotType.values()[i].getPropertyName() ;
	}

	
	
	
	@Override protected String getEnumLabel(PlotType value) {
		return labels[value.ordinal()];
	}
	
	protected String setEnumLabel(PlotType value, String newValue) {
		labels[value.ordinal()] = newValue;
		return newValue;
	}

	
	public String getWireframeLabel() {
		return getEnumLabel(PlotType.WIREFRAME);
	}

	public void setWireframeLabel(String wireframeLabel) {
		firePropertyChange("wireframeLabel", getWireframeLabel(), setEnumLabel(PlotType.WIREFRAME, wireframeLabel));
	}
	
	public String getSurfaceLabel() {
		return getEnumLabel(PlotType.SURFACE);
	}

	public void setSurfaceLabel(String surfaceLabel) {
		firePropertyChange("surfaceLabel", getSurfaceLabel(), setEnumLabel(PlotType.SURFACE, surfaceLabel));
	}

	public String getDensityLabel() {
		return getEnumLabel(PlotType.DENSITY);
	}

	public void setDensityLabel(String densityLabel) {
		firePropertyChange("densityLabel", getDensityLabel(), setEnumLabel(PlotType.DENSITY, densityLabel));
	}
	
	
	public String getContourLabel() {
		return getEnumLabel(PlotType.CONTOUR);
	}

	public void setContourLabel(String contourLabel) {
		firePropertyChange("contourLabel", getContourLabel(), setEnumLabel(PlotType.CONTOUR, contourLabel));
	}
	
	

}
