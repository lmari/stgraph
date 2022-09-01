package net.ericaro.surfaceplotter;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.ericaro.surfaceplotter.beans.JGridBagScrollPane;
import net.ericaro.surfaceplotter.surface.AbstractSurfaceModel;
import net.ericaro.surfaceplotter.surface.JSurface;
import net.ericaro.surfaceplotter.surface.SurfaceModel;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotColor;
import net.ericaro.surfaceplotter.surface.SurfaceModel.PlotType;
import net.ericaro.surfaceplotter.surface.VerticalConfigurationPanel;


/** Main panel to display a surface plot.
 *
 * @author eric
 */
public class JSurfacePanel extends JPanel {
	private DefaultSurfaceModel sm;
	private JSurfacePanelCaller caller;
	private Locale locale;

	public JSurfacePanel(final JSurfacePanelCaller caller, final Locale locale) {
		this.caller = caller;
		this.locale = locale;
		createDefaultEmptySurfaceModel();
	}

	private SurfaceModel createDefaultEmptySurfaceModel() {
		sm = new DefaultSurfaceModel();
		sm.setPlotFunction2(false);
		sm.setContourLines(10);
		sm.setBoxed(false);
		sm.setDisplayXY(false);
		sm.setExpectDelay(false);
		sm.setAutoScaleZ(true);
		sm.setDisplayZ(false);
		sm.setMesh(false);
		sm.setPlotType(PlotType.SURFACE);
		sm.setFirstFunctionOnly(true);
		sm.setPlotColor(PlotColor.SPECTRUM);
		initComponents();
		setModel(sm);
		return sm;
	}

	public void fillDefaultSurfaceModel() {
		if(sm == null) { createDefaultEmptySurfaceModel(); }
		sm.setCalcDivisions(50);
		sm.setDispDivisions(50);
		sm.setXMin(-3);
		sm.setXMax(3);
		sm.setYMin(-3);
		sm.setYMax(3);
		sm.setMapper(new Mapper() {
			public float f1(float x, float y) {
				float r = x*x+y*y;
				if(r == 0) return 1f;
				return (float)(Math.sin(r)/(r));
			}
			public float f2(float x, float y) {
				return (float)(Math.sin(x*y));
			}
		});
		sm.plot().execute();
	}

	/*
	public JSurfacePanel(SurfaceModel model) {
		super(new BorderLayout());
		initComponents();
		String name = (String)configurationToggler.getValue(Action.NAME);
		getActionMap().put(name, configurationToggler);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), name);
		setModel(model);
	}
	*/

	public DefaultSurfaceModel getModel() { return sm; }

	public void setModel(SurfaceModel model) {
		if(model instanceof AbstractSurfaceModel)
			configurationPanel.setModel((AbstractSurfaceModel) model);
		else {
			scrollpane.setVisible(false);
			configurationPanel.setModel(null);
		}
		surface.setModel(model);
	}

	/**
	 * @return
	 * @see java.awt.Component#getFont()
	 */
	public Font getTitleFont() { return title.getFont(); }

	/**
	 * @return
	 * @see javax.swing.JLabel#getIcon()
	 */
	public Icon getTitleIcon() { return title.getIcon(); }

	/**
	 * @return
	 * @see javax.swing.JLabel#getText()
	 */
	public String getTitleText() { return title.getText(); }

	/**
	 * @return
	 * @see java.awt.Component#isVisible()
	 */
	public boolean isTitleVisible() { return title.isVisible(); }

	/**
	 * @param font
	 * @see javax.swing.JComponent#setFont(java.awt.Font)
	 */
	public void setTitleFont(Font font) { title.setFont(font); }

	/**
	 * @param icon
	 * @see javax.swing.JLabel#setIcon(javax.swing.Icon)
	 */
	public void setTitleIcon(Icon icon) { title.setIcon(icon); }

	/**
	 * @param text
	 * @see javax.swing.JLabel#setText(java.lang.String)
	 */
	public void setTitleText(String text) { title.setText(text); }

	/**
	 * @param aFlag
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void setTitleVisible(boolean aFlag) { title.setVisible(aFlag); }

	/**
	 * @return
	 * @see java.awt.Component#isVisible()
	 */
	public boolean isConfigurationVisible() { return scrollpane.isVisible(); }

	/**
	 * @param aFlag
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void setConfigurationVisible(boolean aFlag) {
		scrollpane.setVisible(aFlag);
		invalidate();
		revalidate();
	}

	public void toggleConfiguration() {
		setConfigurationVisible(!isConfigurationVisible());
		if(!isConfigurationVisible()) surface.requestFocusInWindow();
	}

	public JSurface getSurface() { return surface; }

	@SuppressWarnings("deprecation")
	private void initComponents() {
		ResourceBundle bundle = ResourceBundle.getBundle("net.ericaro.surfaceplotter.JSurfacePanel", locale);
		title = new JLabel();
		surface = new JSurface();
		scrollpane = new JGridBagScrollPane();
		configurationPanel = new VerticalConfigurationPanel(locale);
		//configurationToggler = new AbstractAction(){public void actionPerformed(ActionEvent e){toggleConfiguration();}};

		//======== this ========
		setName("this");
		setLayout(new GridBagLayout());
		((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0, 0};
		((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0, 0};
		((GridBagLayout)getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
		((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

		//---- title ----
		title.setText(bundle.getString("title.text"));
		title.setHorizontalTextPosition(SwingConstants.CENTER);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBackground(Color.white);
		title.setOpaque(true);
		title.setFont(title.getFont().deriveFont(title.getFont().getSize() + 4f));
		title.setName("title");
		add(title, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		//---- surface ----
		surface.setToolTipText(bundle.getString("surface.toolTipText"));
		surface.setInheritsPopupMenu(true);
		surface.setName("surface");
		surface.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//if(e.getClickCount()>=2) toggleConfiguration();
				caller.clickingMouse(e);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				surface.requestFocusInWindow();
				caller.pressingMouse(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getClickCount() == 1 && e.isControlDown()) { toggleConfiguration(); }
				caller.releasingMouse(e);
			}
		});
		add(surface, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		//======== scrollpane ========
		{
			scrollpane.setWidthFixed(true);
			scrollpane.setName("scrollpane");

			//---- configurationPanel ----
			configurationPanel.setNextFocusableComponent(this);
			configurationPanel.setName("configurationPanel");
			scrollpane.setViewportView(configurationPanel);
		}
		add(scrollpane, new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		//---- configurationToggler ----
		//configurationToggler.putValue(Action.NAME, bundle.getString("configurationToggler.Name"));
	}

	private JLabel title;
	private JSurface surface;
	private JGridBagScrollPane scrollpane;
	private VerticalConfigurationPanel configurationPanel;
	//private AbstractAction configurationToggler;

}
