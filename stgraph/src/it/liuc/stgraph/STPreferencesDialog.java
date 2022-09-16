/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2022, Luca Mari.
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

import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STTools;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;
import java.util.Properties;


/** Handle the dialog to set various preferences. */
@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class STPreferencesDialog extends JDialog {
	/** The jContentPane. */
	private JPanel jContentPane = null;
	/** The jTabbedPane. */
	private JTabbedPane jTabbedPane = null;
	/** The jContentPane1. */
	private JPanel jContentPane1 = null;
	/** The list locale. */
	private JComboBox listLocale = null;
	/** The menu font size. */
	private JTextField textMenuFontSize = null;    
	/** The menu icon size. */
	private JTextField textMenuIconSize = null;    
	/** The toolbar icon size. */
	private JTextField textToolbarIconSize = null;
	/** The check autoupdate. */
	private JCheckBox checkUIFallback = null;
	/** The check autoupdate. */
	private JCheckBox checkAutoupdate = null;
	/** The list icon set. */
	//private JComboBox listIconSet = null;
	/** The list LaF. */
	//private JComboBox listLaF = null;
	/** The list tab pos. */
	//private JComboBox listTabPos = null;
	/** The jContentPane2. */
	private JPanel jContentPane2 = null;
	/** The check anti aliased. */
	private JCheckBox checkAntiAliased = null;
	/** The check double buffered. */
	//private JCheckBox checkDoubleBuffered = null;
	/** The check show node values. */
	private JCheckBox checkShowNodeValues = null;
	/** The check show edge labels. */
	private JCheckBox checkShowEdgeLabels = null;
	/** The check grid visible. */
	private JCheckBox checkGridVisible = null;
	/** The check grid enabled. */
	private JCheckBox checkGridEnabled = null;
	/** The text grid size. */
	private JTextField textGridSize = null;    
	/** The check show node tooltip. */
	private JCheckBox checkShowNodeTooltip = null;
	/** The highlight edges. */
	private JCheckBox checkHighlightEdges = null;
	/** The zoom widgets. */
	private JCheckBox checkZoomWidgets = null;
	/** The button panel. */
	private JPanel buttonPanel = null;
	/** The button ok. */
	private JButton buttonOk = null;
	/** The button cancel. */
	private JButton buttonCancel = null;


	/** Class constructor. */
	public STPreferencesDialog() {
		super();
		if(STGraphC.getContainer() instanceof JFrame) { setAlwaysOnTop(true); }
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setModal(true);
	}


	/** Initialize and open the dialog. */
	public void open() {
		setTitle(STGraphC.getMessage("PREFS.DIALOGTITLE"));
		setContentPane(getJContentPane());
		fill();
		STGraphExec g = STGraph.getSTC().getCurrentGraph();
		boolean b = (g == null) || g.isEditable;
		getListLocale().setEnabled(b);
		getTextMenuFontSize().setEnabled(b); getTextMenuIconSize().setEnabled(b); getTextToolbarIconSize().setEnabled(b);
		getCheckUIFallback().setEnabled(b);
		getCheckAutoupdate().setEnabled(b);
		//getListIconSet().setEnabled(b);
		//getListLaF().setEnabled(b);
		//getListTabPos().setEnabled(b);
		getCheckAntiAliased().setEnabled(b);
		//getCheckDoubleBuffered().setEnabled(b);
		getCheckShowNodeValues().setEnabled(b); getCheckShowEdgeLabels().setEnabled(b);
		getCheckGridVisible().setEnabled(b); getCheckGridEnabled().setEnabled(b); getTextGridSize().setEnabled(b);
		getCheckShowNodeTooltip().setEnabled(b); getCheckHighlightEdges().setEnabled(b); getCheckZoomWidgets().setEnabled(b);
		getJContentPane().getRootPane().setDefaultButton(getButtonOk());
		pack();
		setLocation(STGraph.getSTC().getX() + 50, STGraph.getSTC().getY() + 50);
		setVisible(true);
	}


	/** Fill the dialog. */
	private void fill() {
		Properties p = STConfigurator.getProperties();
		getListLocale().setSelectedItem(p.getProperty("LOCALE"));
		getTextMenuFontSize().setText(p.getProperty("MENU.FONTSIZE"));
		getTextMenuIconSize().setText(p.getProperty("MENU.ICONSIZE"));
		getTextToolbarIconSize().setText(p.getProperty("TOOLBAR.ICONSIZE"));
		getCheckUIFallback().setSelected(Boolean.valueOf(p.getProperty("UIFALLBACK")).booleanValue());
		getCheckAutoupdate().setSelected(Boolean.valueOf(p.getProperty("AUTOUPDATE")).booleanValue());
		//getListIconSet().setSelectedIndex(Integer.parseInt(p.getProperty("ICONSET").substring(3)));
		//getListLaF().setSelectedItem(p.getProperty("LAF"));
		//getListTabPos().setSelectedItem(p.getProperty("TABPOSITION"));
		getCheckAntiAliased().setSelected(Boolean.valueOf(p.getProperty("GRAPH.ANTIALIASED")).booleanValue());
		//getCheckDoubleBuffered().setSelected(Boolean.valueOf(p.getProperty("GRAPH.DOUBLEBUFFERED")).booleanValue());
		getCheckShowNodeValues().setSelected(Boolean.valueOf(p.getProperty("GRAPH.SHOWNODEVALUES")).booleanValue());
		getCheckShowEdgeLabels().setSelected(Boolean.valueOf(p.getProperty("GRAPH.SHOWEDGELABELS")).booleanValue());
		getCheckGridVisible().setSelected(Boolean.valueOf(p.getProperty("GRAPH.GRIDVISIBLE")).booleanValue());
		getCheckGridEnabled().setSelected(Boolean.valueOf(p.getProperty("GRAPH.GRIDENABLED")).booleanValue());
		getTextGridSize().setText(p.getProperty("GRAPH.GRIDSIZE"));
		getCheckShowNodeTooltip().setSelected(Boolean.valueOf(p.getProperty("SHOWNODETOOLTIP")).booleanValue());
		getCheckHighlightEdges().setSelected(Boolean.valueOf(p.getProperty("HIGHLIGHTEDGES")).booleanValue());
		getCheckZoomWidgets().setSelected(Boolean.valueOf(p.getProperty("ZOOMWIDGETS")).booleanValue());
	}


	/** Initialize jContentPane.
	 * @return panel */
	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}


	/** Initialize the tabbed pane.
	 * @return pane */
	protected JTabbedPane getJTabbedPane() {
		if(jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab(STGraphC.getMessage("PREFS.TAB1"), getJContentPane1());
			jTabbedPane.addTab(STGraphC.getMessage("PREFS.TAB2"), getJContentPane2());
		}
		jTabbedPane.addKeyListener(new KeyAdapter() {
			public void keyReleased(final KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; }
			}
		});
		return jTabbedPane;
	}


	/** Initialize the first panel.
	 * @return panel */
	protected JPanel getJContentPane1() {
		if(jContentPane1 == null) {
			jContentPane1 = new JPanel(new GridBagLayout());

			JPanel p = new JPanel(new GridBagLayout());
			p.add(new JLabel(STGraphC.getMessage("PREFS.LOCALE") + STTools.COLON),
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			p.add(getListLocale(), new GridConstraints(1, 0, GridBagConstraints.WEST));

			p.add(new JLabel(STGraphC.getMessage("PREFS.MENUFONTSIZE") + STTools.COLON),
					new GridConstraints(0, 1, GridBagConstraints.EAST));
			p.add(getTextMenuFontSize(), new GridConstraints(1, 1, GridBagConstraints.WEST));

			p.add(new JLabel(STGraphC.getMessage("PREFS.MENUICONSIZE") + STTools.COLON),
					new GridConstraints(0, 2, GridBagConstraints.EAST));
			p.add(getTextMenuIconSize(), new GridConstraints(1, 2, GridBagConstraints.WEST));

			p.add(new JLabel(STGraphC.getMessage("PREFS.TOOLBARICONSIZE") + STTools.COLON),
					new GridConstraints(0, 3, GridBagConstraints.EAST));
			p.add(getTextToolbarIconSize(), new GridConstraints(1, 3, GridBagConstraints.WEST));

			jContentPane1.add(p, new GridConstraints(0, 0, GridBagConstraints.WEST));

			jContentPane1.add(getCheckUIFallback(), new GridConstraints(0, 1, GridBagConstraints.CENTER));

			jContentPane1.add(getCheckAutoupdate(), new GridConstraints(0, 2, GridBagConstraints.CENTER));


			jContentPane1.add(new JLabel(STTools.SPACE), new GridConstraints(0, 3, GridBagConstraints.CENTER));
			jContentPane1.add(new JLabel(STTools.SPACE), new GridConstraints(0, 4, GridBagConstraints.CENTER));

			/*
            JLabel labIconSet = new JLabel(STGraphC.getMessage("PREFS.ICONSET") + STTools.COLON);
            GridConstraints gbcIconSet = new GridConstraints(0, 1);
            gbcIconSet.anchor = GridBagConstraints.EAST;
            jContentPane1.add(labIconSet, gbcIconSet);

            GridConstraints gbcIconSet2 = new GridConstraints(1, 1);
            gbcIconSet2.anchor = GridBagConstraints.WEST;
            jContentPane1.add(getListIconSet(), gbcIconSet2);
			 */

			/*
            JLabel labLaF = new JLabel(STGraphC.getMessage("PREFS.LAF") + STTools.COLON);
            GridConstraints gbcLaF = new GridConstraints(0, 3);
            gbcLaF.anchor = GridBagConstraints.EAST;
            jContentPane1.add(labLaF, gbcLaF);

            GridConstraints gbcLaF2 = new GridConstraints(1, 3);
            gbcLaF2.anchor = GridBagConstraints.WEST;
            jContentPane1.add(getListLaF(), gbcLaF2);
            */

			/*
            JLabel labTabPos = new JLabel(STGraphC.getMessage("PREFS.TABPOS") + STTools.COLON);
            GridConstraints gbcTabPos = new GridConstraints(0, 3);
            gbcTabPos.anchor = GridBagConstraints.EAST;
            jContentPane1.add(labTabPos, gbcTabPos);

            GridConstraints gbcTabPos2 = new GridConstraints(1, 3);
            gbcTabPos2.anchor = GridBagConstraints.WEST;
            jContentPane1.add(getListTabPos(), gbcTabPos2);
			 */
			JLabel labAdvice = new JLabel(STGraphC.getMessage("PREFS.ADVICE"));
			GridConstraints gbcAdvice = new GridConstraints(0, 10, GridBagConstraints.CENTER);
			gbcAdvice.gridwidth = 2;
			jContentPane1.add(labAdvice, gbcAdvice);
		}
		return jContentPane1;
	}


	/** Initialize listLocale.
	 * @return combobox */
	JComboBox getListLocale() {
		if(listLocale == null) {
			String[] locales = STGraphC.getUILanguages();
			listLocale = new JComboBox(locales);
		}
		return listLocale;
	}


	/** Initialize textMenuFontSize.
	 * @return text */
	JTextField getTextMenuFontSize() {
		if(textMenuFontSize == null) {
			textMenuFontSize = new JTextField();
			textMenuFontSize.setPreferredSize(new Dimension(50, 21));
		}
		return textMenuFontSize;
	}


	/** Initialize textMenuIconSize.
	 * @return text */
	JTextField getTextMenuIconSize() {
		if(textMenuIconSize == null) {
			textMenuIconSize = new JTextField();
			textMenuIconSize.setPreferredSize(new Dimension(50, 21));
		}
		return textMenuIconSize;
	}


	/** Initialize textToolbarIconSize.
	 * @return text */
	JTextField getTextToolbarIconSize() {
		if(textToolbarIconSize == null) {
			textToolbarIconSize = new JTextField();
			textToolbarIconSize.setPreferredSize(new Dimension(50, 21));
		}
		return textToolbarIconSize;
	}


	/** Initialize checkUIFallback.
	 * @return checkbox */
	JCheckBox getCheckUIFallback() {
		if(checkUIFallback == null) {
			checkUIFallback = new JCheckBox(STGraphC.getMessage("PREFS.UIFALLBACK"));
		}
		return checkUIFallback;
	}

	
	/** Initialize checkAutoupdate.
	 * @return checkbox */
	JCheckBox getCheckAutoupdate() {
		if(checkAutoupdate == null) {
			checkAutoupdate = new JCheckBox(STGraphC.getMessage("PREFS.AUTOUPDATE"));
		}
		return checkAutoupdate;
	}


	/** Initialize listIconSet.
	 * @return combobox */
	/*
    JComboBox getListIconSet() {
    	if(listIconSet == null) {
    		Properties p = new Properties();
            try {
            	p.load(STGraphC.class.getClassLoader().getResource("datafiles/iconsets.properties").openStream());
            } catch (Exception e) { e.printStackTrace(); }
            Vector v = new Vector();
            int i = 0;
            String s = p.getProperty("set0");
            v.add(s);
            while(s != null) {
            	i++;
            	v.add(s = p.getProperty("set" + i));
            }
    		listIconSet = new JComboBox(v.toArray());
    	}
    	return listIconSet;
    }
	 */


	/** Initialize listLaF.
	 * @return combobox */
	/*
    JComboBox getListLaF() {
    	if(listLaF == null) {
			LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
			String[] lafNames = new String[lafs.length];
			for(int i = 0; i < lafs.length; i++) { lafNames[i] = lafs[i].getClassName(); }
			listLaF = new JComboBox(lafNames);

			listLaF.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					try {
						UIManager.setLookAndFeel((String)listLaF.getSelectedItem());
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					SwingUtilities.updateComponentTreeUI(STGraph.getSTC());
					STTools.maximizeDesktop();	
				}
			});

    	}
    	return listLaF;
    }
    */


	/** Initialize listTabPos.
	 * @return combobox */
	/*
    JComboBox getListTabPos() {
    	if(listTabPos == null) {
        	Properties p = Configurator.getProperties();
    		String[] tabPos = { p.getProperty("PREFS.TABPOS.TOP"), p.getProperty("PREFS.TABPOS.BOTTOM") };
    		listTabPos = new JComboBox(tabPos);
    	}
    	return listTabPos;
    }
	 */


	/** Initialize the second panel.
	 * @return panel */
	protected JPanel getJContentPane2() {
		if(jContentPane2 == null) {
			jContentPane2 = new JPanel(new GridBagLayout());

			jContentPane2.add(getCheckAntiAliased(), new GridConstraints(0, 0, GridBagConstraints.WEST));
			/*
            GridConstraints gbcCheckDoubleBuffered = new GridConstraints(0, 1);
            gbcCheckDoubleBuffered.anchor = GridBagConstraints.WEST;
            jContentPane2.add(getCheckDoubleBuffered(), gbcCheckDoubleBuffered);
			 */
			jContentPane2.add(getCheckShowNodeValues(), new GridConstraints(0, 2, GridBagConstraints.WEST));
			jContentPane2.add(getCheckShowEdgeLabels(), new GridConstraints(0, 3, GridBagConstraints.WEST));
			jContentPane2.add(getCheckGridVisible(), new GridConstraints(0, 4, GridBagConstraints.WEST));
			jContentPane2.add(getCheckGridEnabled(), new GridConstraints(0, 5, GridBagConstraints.WEST));

			JPanel p = new JPanel(new GridBagLayout());
			p.add(new JLabel(STGraphC.getMessage("PREFS.GRIDSIZE") + STTools.COLON),
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			p.add(getTextGridSize(), new GridConstraints(1, 0, GridBagConstraints.WEST));
			jContentPane2.add(p, new GridConstraints(0, 6, GridBagConstraints.WEST));
			jContentPane2.add(getCheckShowNodeTooltip(), new GridConstraints(0, 7, GridBagConstraints.WEST));
			jContentPane2.add(getCheckHighlightEdges(), new GridConstraints(0, 8, GridBagConstraints.WEST));
			jContentPane2.add(getCheckZoomWidgets(), new GridConstraints(0, 9, GridBagConstraints.WEST));
		}
		return jContentPane2;
	}


	/** Initialize checkAntiAliased.
	 * @return checkbox */
	JCheckBox getCheckAntiAliased() {
		if(checkAntiAliased == null) {
			checkAntiAliased = new JCheckBox(STGraphC.getMessage("PREFS.ANTIALIASED"));
			checkAntiAliased.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					for(int i = 0; i < STGraphC.getMultiDesktop().getComponentCount(); i++) {
						((STDesktop)STGraphC.getMultiDesktop().getComponentAt(i)).getGraph().setAntiAliased(checkAntiAliased.isSelected());
					}
				}
			});
		}
		return checkAntiAliased;
	}


	/** Initialize checkDoubleBuffered.
	 * @return checkbox */
	/*
    JCheckBox getCheckDoubleBuffered() {
        if(checkDoubleBuffered == null) {
            checkDoubleBuffered = new JCheckBox();
            checkDoubleBuffered.setText(STGraphC.getMessage("PREFS.DOUBLEBUFFERED"));
            checkDoubleBuffered.addChangeListener(new ChangeListener() {
            	public void stateChanged(ChangeEvent e) {
            		for(int i = 0; i < STGraphC.getMultiDesktop().getComponentCount(); i++) {
            			((STDesktop)STGraphC.getMultiDesktop().getComponentAt(i)).getGraph().setDoubleBuffered(checkDoubleBuffered.isSelected());
            		}
            	}
            });
        }
        return checkDoubleBuffered;
    }
	 */


	/** Initialize checkShowNodeValues.
	 * @return checkbox */
	JCheckBox getCheckShowNodeValues() {
		if(checkShowNodeValues == null) {
			checkShowNodeValues = new JCheckBox(STGraphC.getMessage("PREFS.SHOWNODEVALUES"));
			checkShowNodeValues.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					for(int i = 0; i < STGraphC.getMultiDesktop().getComponentCount(); i++) {
						((STDesktop)STGraphC.getMultiDesktop().getComponentAt(i)).getGraph().setShowNodeValues(checkShowNodeValues.isSelected());
					}
				}
			});
		}
		return checkShowNodeValues;
	}


	/** Initialize checkShowEdgeLabels.
	 * @return checkbox */
	JCheckBox getCheckShowEdgeLabels() {
		if(checkShowEdgeLabels == null) {
			checkShowEdgeLabels = new JCheckBox(STGraphC.getMessage("PREFS.SHOWEDGELABELS"));
			checkShowEdgeLabels.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					for(int i = 0; i < STGraphC.getMultiDesktop().getComponentCount(); i++) {
						((STDesktop)STGraphC.getMultiDesktop().getComponentAt(i)).getGraph().setShowEdgeLabels(checkShowEdgeLabels.isSelected());
					}
				}
			});
		}
		return checkShowEdgeLabels;
	}


	/** Initialize checkGridVisible.
	 * @return checkbox */
	JCheckBox getCheckGridVisible() {
		if(checkGridVisible == null) {
			checkGridVisible = new JCheckBox(STGraphC.getMessage("PREFS.GRIDVISIBLE"));
			checkGridVisible.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					for(int i = 0; i < STGraphC.getMultiDesktop().getComponentCount(); i++) {
						((STDesktop)STGraphC.getMultiDesktop().getComponentAt(i)).getGraph().setGridVisible(checkGridVisible.isSelected());
					}
				}
			});
		}
		return checkGridVisible;
	}


	/** Initialize checkGridEnabled.
	 * @return checkbox */
	JCheckBox getCheckGridEnabled() {
		if(checkGridEnabled == null) {
			checkGridEnabled = new JCheckBox(STGraphC.getMessage("PREFS.GRIDENABLED"));
			checkGridEnabled.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					for(int i = 0; i < STGraphC.getMultiDesktop().getComponentCount(); i++) {
						((STDesktop)STGraphC.getMultiDesktop().getComponentAt(i)).getGraph().setGridEnabled(checkGridEnabled.isSelected());
					}
				}
			});
		}
		return checkGridEnabled;
	}


	/** Initialize textGridSize.
	 * @return text */
	JTextField getTextGridSize() {
		if(textGridSize == null) {
			textGridSize = new JTextField();
			textGridSize.setPreferredSize(new Dimension(50, 21));
			textGridSize.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					int size = 10;
					try { size = Integer.parseInt(getTextGridSize().getText()); } catch (Exception ex) { ; }
					for(int i = 0; i < STGraphC.getMultiDesktop().getComponentCount(); i++) {
						((STDesktop)STGraphC.getMultiDesktop().getComponentAt(i)).getGraph().setGridSize(size);
					}
				}
				public void removeUpdate(DocumentEvent e) {
					int size = 10;
					try { size = Integer.parseInt(getTextGridSize().getText()); } catch (Exception ex) { ; }
					for(int i = 0; i < STGraphC.getMultiDesktop().getComponentCount(); i++) {
						((STDesktop)STGraphC.getMultiDesktop().getComponentAt(i)).getGraph().setGridSize(size);
					}
				}
				public void changedUpdate(DocumentEvent e) { ; }
			});
		}
		return textGridSize;
	}


	/** Initialize checkShowNodeTooltip.
	 * @return checkbox */
	JCheckBox getCheckShowNodeTooltip() {
		if(checkShowNodeTooltip == null) {
			checkShowNodeTooltip = new JCheckBox(STGraphC.getMessage("PREFS.SHOWNODETOOLTIP"));
			checkShowNodeTooltip.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					STGraphC.setShowNodeTooltips(checkShowNodeTooltip.isSelected());
				}
			});
		}
		return checkShowNodeTooltip;
	}


	/** Initialize checkHighlightEdges.
	 * @return checkbox */
	JCheckBox getCheckHighlightEdges() {
		if(checkHighlightEdges == null) {
			checkHighlightEdges = new JCheckBox(STGraphC.getMessage("PREFS.HIGHLIGHTEDGES"));
			checkHighlightEdges.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					STDesktop desktop = STGraph.getSTC().getCurrentDesktop();
					if(desktop != null) {
						STGraphC.setHighlightEdges(checkHighlightEdges.isSelected());
						STNode node = desktop.getGraphFrame1().getSelectedNode();
						if(node != null) { node.resetEdges(); }
					}
				}
			});
		}
		return checkHighlightEdges;
	}

	
	/** Initialize checkZoomWidgets.
	 * @return checkbox */
	JCheckBox getCheckZoomWidgets() {
		if(checkZoomWidgets == null) {
			checkZoomWidgets = new JCheckBox(STGraphC.getMessage("PREFS.ZOOMWIDGETS"));
			checkZoomWidgets.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					STGraphC.setZoomWidgets(checkZoomWidgets.isSelected());
				}
			});
		}
		return checkZoomWidgets;
	}


	/** Initialize buttonPanel.
	 * @return panel */
	protected JPanel getButtonPanel() {
		if(buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getButtonOk(), null);
			buttonPanel.add(getButtonCancel(), null);
		}
		return buttonPanel;
	}


	/** Initialize buttonOk.
	 * @return button */
	protected JButton getButtonOk() {
		if(buttonOk == null) {
			buttonOk = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png")));
			buttonOk.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					STConfigurator.setProperty("LOCALE", (String)getListLocale().getSelectedItem());

					String s = getTextMenuFontSize().getText();
					try { Integer.parseInt(s); } catch (Exception ex) { s = "10"; }
					STConfigurator.setProperty("MENU.FONTSIZE", s);

					s = getTextMenuIconSize().getText();
					try { Integer.parseInt(s); } catch (Exception ex) { s = "10"; }
					STConfigurator.setProperty("MENU.ICONSIZE", s);

					s = getTextToolbarIconSize().getText();
					try { Integer.parseInt(s); } catch (Exception ex) { s = "10"; }
					STConfigurator.setProperty("TOOLBAR.ICONSIZE", s);

					STConfigurator.setProperty("UIFALLBACK", getCheckUIFallback().isSelected() ? "true" : "false");
					STConfigurator.setProperty("AUTOUPDATE", getCheckAutoupdate().isSelected() ? "true" : "false");
					//Configurator.setProperty("ICONSET", "set" + getListIconSet().getSelectedIndex());
					//STConfigurator.setProperty("LAF", (String)getListLaF().getSelectedItem());
					//Configurator.setProperty("TABPOSITION", (String)getListTabPos().getSelectedItem());
					STConfigurator.setProperty("GRAPH.ANTIALIASED", getCheckAntiAliased().isSelected() ? "true" : "false");
					//Configurator.setProperty("GRAPH.DOUBLEBUFFERED", getCheckDoubleBuffered().isSelected() ? "true" : "false");
					STConfigurator.setProperty("GRAPH.SHOWNODEVALUES", getCheckShowNodeValues().isSelected() ? "true" : "false");
					STConfigurator.setProperty("GRAPH.SHOWEDGELABELS", getCheckShowEdgeLabels().isSelected() ? "true" : "false");
					STConfigurator.setProperty("GRAPH.GRIDVISIBLE", getCheckGridVisible().isSelected() ? "true" : "false");
					STConfigurator.setProperty("GRAPH.GRIDENABLED", getCheckGridEnabled().isSelected() ? "true" : "false");

					s = getTextGridSize().getText();
					try { Integer.parseInt(s); } catch (Exception ex) { s = "10"; }
					STConfigurator.setProperty("GRAPH.GRIDSIZE", s);

					STConfigurator.setProperty("SHOWNODETOOLTIP", getCheckShowNodeTooltip().isSelected() ? "true" : "false");
					STConfigurator.setProperty("HIGHLIGHTEDGES", getCheckHighlightEdges().isSelected() ? "true" : "false");
					STConfigurator.setProperty("ZOOMWIDGETS", getCheckZoomWidgets().isSelected() ? "true" : "false");
					STConfigurator.write();
					setVisible(false);
				}
			});
		}
		return buttonOk;
	}


	/** Initialize buttonCancel.
	 * @return button */    
	protected JButton getButtonCancel() {
		if(buttonCancel == null) {
			buttonCancel = new JButton(STGraphC.getMessage("DIALOG.CANCEL"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-cancel.png"))); //$NON-NLS-2$
			buttonCancel.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) { setVisible(false); }
			});
		}
		return buttonCancel;
	}

}
