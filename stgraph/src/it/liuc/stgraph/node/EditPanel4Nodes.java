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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

import it.liuc.stgraph.STConfigurator;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.fun.FunctionMenu;
import it.liuc.stgraph.fun.STFunction;
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STColorChooser;
import it.liuc.stgraph.util.STFileFilter5;
import it.liuc.stgraph.util.STTextArea;
import it.liuc.stgraph.util.STTextField;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.util.TokenAwareEditor;
import it.liuc.stgraph.widget.STWidget;


/** Abstract panel with the commonalities for the configuration of both variables and submodel nodes. */
@SuppressWarnings({"serial", "rawtypes"})
public abstract class EditPanel4Nodes extends JDialog {
	/** The constant MODE_OK. */
	public final static int MODE_OK = 0;
	/** The constant MODE_APPLY. */
	public final static int MODE_APPLY = 1;

	/** The constant DEFAULT_ITEM_FOR_LIBRARY_MODEL_ICON. */
	public final static String DEFAULT_ITEM_FOR_LIBRARY_MODEL_ICON = "<default>"; //$NON-NLS-1$

	/** The node. */
	public static STNode node;
	/** The error message. */
	private String errorMessage;
	/** The error buffer. */
	private String errorBuffer;
	/** The switch to maintain whether the dialog is open. */
	protected static boolean open;
	/** The switch to maintain whether there are some pending edit operations. */
	private static boolean dirty;
	/** The location of panel for value nodes. */
	private static Point locV;
	/** The location of panel for model nodes. */
	private static Point locM;
	/** The size of panel for value nodes. */
	private static Dimension dimV;
	/** The size of panel for model nodes. */
	private static Dimension dimM;
	/** Is the panel for value nodes synthetic or expanded? */
	private static boolean isExpandedV = true;
	/** Is the panel for model nodes synthetic or expanded? */
	private static boolean isExpandedM = true;

	/** The tabbedPane. */
	private JTabbedPane tabbedPanel;
	/** The button panel. */
	protected JPanel buttonPanel;
	/** The contentPanel2. */
	private JPanel contentPanel2;
	/** The contentPanel3. */
	private JPanel contentPanel3;
	/** The contentPanel4. */
	private JPanel contentPanel4;

	//for buttonPanel
	/** The button ok. */
	private JButton buttonOk;
	/** The button ok. */
	private JButton buttonApply;
	/** The button cancel. */
	private JButton buttonCancel;
	/** The button synthetic or expanded view. */
	private JButton buttonSoEview;

	//for contentPanel1 (inherited)
	/** The node name. */
	protected STTextField nodeNameField;
	/** The var and fun panel. */
	protected JPanel varAndFunPanel;
	/** The check show local variables. */
	private JCheckBox checkShowLocalVars = null;
	/** The check show global variables. */
	private JCheckBox checkShowGlobalVars = null;
	/** The check show system variables. */
	private JCheckBox checkShowSystemVars = null;
	/** The scroll var. */
	private JScrollPane scrollVar;
	/** The list var. */
	private JList listVar;
	/** The scroll items. */
	private JScrollPane scrollFun;
	/** The list items. */
	private JList listFun;
	/** The function menus. */
	protected JComboBox functionMenus;
	/** The scroll help. */
	private JScrollPane scrollHelp;
	/** The help text. */
	protected HelpEditorPane helpText;
	/** The help text 2. */
	protected HelpEditorPane helpText2 = new HelpEditorPane();
	/** The current field. */
	public TokenAwareEditor currentField;

	//for contentPanel2
	/** The icon file. */
	protected JComboBox fontFamily;
	/** The fontSize. */
	protected STTextField fontSize;
	/** The numberFormat label. */
	protected JLabel numberFormatLabel;
	/** The numberFormat. */
	protected STTextField numberFormat;
	/** The color font. */
	private JColorChooser colorFont;
	/** The color fore. */
	private JColorChooser colorFore;
	/** The color back. */
	private STColorChooser colorBack;
	/** The scroll doc. */
	private JScrollPane scrollDoc;
	/** The area doc. */
	private STTextArea areaDoc;
	/** The icon file. */
	protected JComboBox iconFile;

	//for contentPanel3
	/** The custom properties table. */
	private STTable1 cPropTable;
	/** The data model. */
	private DefaultTableModel dataModel;
	/** The new cprop button. */
	private JButton newCPropButton;
	/** The del cprop button. */
	private JButton delCPropButton;

	//for contentPanel4
	/** The web properties table. */
	private STTable2 cWebPropTable;
	/** The data model. */
	private DefaultTableModel webDataModel;

	private List<String> predefinedKeys = Arrays.asList(STGraphC.getBasicProps().getProperty("CUSTPROP.BASE.NAMES").split(STTools.COMMA)); //$NON-NLS-1$
	private List<String> predefinedWebKeys = Arrays.asList(STGraphC.getBasicProps().getProperty("CUSTPROP.WEB.NAMES").split(STTools.COMMA)); //$NON-NLS-1$


	/** Class constructor. */
	public EditPanel4Nodes() {
		super();
		setAlwaysOnTop(true);

		addWindowFocusListener(new WindowAdapter() { //a tentative to maintain the dialog on top, but only of the main STGraph window... 
			public void windowLostFocus(WindowEvent e) {
				Window w = e.getOppositeWindow();
				if(w == null || !w.getName().equals("frame1")) { setAlwaysOnTop(false); } //$NON-NLS-1$
				else { setAlwaysOnTop(true); }
			}
		});

		if(this instanceof EditPanel4ValueNodes) {
			setLocation(locV = new Point(STGraphC.getContainer().getX() + 50, STGraphC.getContainer().getY() + 50));
			setPreferredSize(dimV = new Dimension(Integer.parseInt(STConfigurator.getProperty("NODE.DIALOG.WIDTH")), Integer.parseInt(STConfigurator.getProperty("NODE.DIALOG.HEIGHT")))); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			setLocation(locM = new Point(STGraphC.getContainer().getX() + 50, STGraphC.getContainer().getY() + 50));
			setPreferredSize(dimM = new Dimension(Integer.parseInt(STConfigurator.getProperty("NODE.DIALOG.WIDTH")), Integer.parseInt(STConfigurator.getProperty("NODE.DIALOG.HEIGHT")))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) { ; }
			public void componentResized(ComponentEvent e) {
				if(EditPanel4Nodes.this instanceof EditPanel4ValueNodes) {
					locV = getLocation();
					dimV = getSize();
				} else {
					locM = getLocation();
					dimM = getSize();
				}
			}
			public void componentMoved(ComponentEvent e) {
				if(EditPanel4Nodes.this instanceof EditPanel4ValueNodes) {
					locV = getLocation();
				} else {
					locM = getLocation();
				}
			}
			public void componentHidden(ComponentEvent e) { ; }
		});
		setLayout(new BorderLayout());
		add(getTabbedPanel(), BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.SOUTH);
	}


	/** Get the tabbed panel.
	 * @return the tabbed panel */
	protected JTabbedPane getTabbedPanel() {
		if(tabbedPanel == null) {
			tabbedPanel = new JTabbedPane();
			tabbedPanel.addTab(STGraphC.getMessage("NODE.DIALOG.TAB1"), getContentPanel1()); // Base //$NON-NLS-1$
			tabbedPanel.addTab(STGraphC.getMessage("NODE.DIALOG.TAB2"), getContentPanel2()); // Further //$NON-NLS-1$
			tabbedPanel.addTab(STGraphC.getMessage("NODE.DIALOG.TAB3"), getContentPanel3()); // Custom //$NON-NLS-1$
			tabbedPanel.addTab(STGraphC.getMessage("NODE.DIALOG.TAB4"), getContentPanel4()); // Web //$NON-NLS-1$
		}
		tabbedPanel.addKeyListener(new KeyAdapter() {
			public void keyReleased(final KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setVisible(open = false);
					return;
				}
			}
		});
		tabbedPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(node instanceof ValueNode) {
					EditPanel4ValueNodes.activeTab = tabbedPanel.getSelectedIndex();
				} else if(node instanceof ModelNode) {
					EditPanel4ModelNodes.activeTab = tabbedPanel.getSelectedIndex();
				}
			}
		});
		return tabbedPanel;
	}


	/** Get the button panel.
	 * @return panel */
	protected JPanel getButtonPanel() {
		if(buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getButtonOk(), null);
			buttonPanel.add(getButtonApply(), null);
			buttonPanel.add(getButtonCancel(), null);
			buttonPanel.add(getButtonSoEview(), null);
		}
		return buttonPanel;
	}


	/** Initialize buttonOk.
	 * @return button */
	public JButton getButtonOk() {
		if(buttonOk == null) {
			buttonOk = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			buttonOk.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) { okHandler(MODE_OK); }
			});
		}
		return buttonOk;
	}


	/** Action for the OK / Apply button.
	 * @param mode the mode, either <code>MODE_OK</code> or <code>MODE_APPLY</code> */
	public void okHandler(final int mode) { ; }


	/** Initialize buttonApply.
	 * @return button */
	public JButton getButtonApply() {
		if(buttonApply == null) {
			buttonApply = new JButton(STGraphC.getMessage("DIALOG.APPLY"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok-apply.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			buttonApply.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) { okHandler(MODE_APPLY); }
			});
		}
		return buttonApply;
	}


	/** Initialize buttonCancel.
	 * @return button */
	public JButton getButtonCancel() {
		if(buttonCancel == null) {
			buttonCancel = new JButton(STGraphC.getMessage("DIALOG.CANCEL"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-cancel.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			buttonCancel.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					node.getGraph().setLastError(errorBuffer); // restore the error state previous to the opening of this dialog
					setVisible(open = false);
					STGraphC.setFocus();
				}
			});
		}
		return buttonCancel;
	}


	/** Initialize buttonSoEview.
	 * @return button */
	protected JButton getButtonSoEview() {
		if(buttonSoEview == null) {
			buttonSoEview = new JButton();
			buttonSoEview.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) { switchSoE(); }
			});
		}
		return buttonSoEview;
	}


	private void switchSoE() {
		int w = getWidth();
		int h = getHeight();
		if(this instanceof EditPanel4ValueNodes) {
			if(isExpandedV) {
				isExpandedV = false;
				buttonSoEview.setText(STGraphC.getMessage("DIALOG.SOEVIEW.EXP")); //$NON-NLS-1$
				varAndFunPanel.setVisible(false);
				setPreferredSize(dimV = new Dimension(w, h - varAndFunPanel.getHeight()));
			} else {
				isExpandedV = true;
				buttonSoEview.setText(STGraphC.getMessage("DIALOG.SOEVIEW.SYN")); //$NON-NLS-1$
				varAndFunPanel.setVisible(true);
				setPreferredSize(dimV = new Dimension(w, h + varAndFunPanel.getHeight()));
			}
		} else {
			if(isExpandedM) {
				isExpandedM = false;
				buttonSoEview.setText(STGraphC.getMessage("DIALOG.SOEVIEW.EXP")); //$NON-NLS-1$
				varAndFunPanel.setVisible(false);
				setPreferredSize(dimM = new Dimension(w, h - varAndFunPanel.getHeight()));
			} else {
				isExpandedM = true;
				buttonSoEview.setText(STGraphC.getMessage("DIALOG.SOEVIEW.SYN")); //$NON-NLS-1$
				varAndFunPanel.setVisible(true);
				setPreferredSize(dimM = new Dimension(w, h + varAndFunPanel.getHeight()));
			}
		}
		pack();
	}


	/** Get whether the dialog is open.
	 * @return the open */
	public static boolean isOpen() { return open; }


	/** Set whether there are some pending edit operations.
	 * @param dirty the dirty to set */
	public void setDirty(boolean isdirty) {
		dirty = isdirty;
		String t = getTitle();
		if(dirty) {
			if(!t.endsWith(STTools.ASTERISK)) { setTitle(t + STTools.ASTERISK); }
		} else {
			if(t.endsWith(STTools.ASTERISK)) { setTitle(t.substring(0, t.length() - 1)); }
		}
	}


	/** Get whether there are some pending edit operations.
	 * @return the dirty */
	public static boolean isDirty() { return dirty; }


	/** Get the first ("base") panel.
	 * @return panel */
	protected abstract JPanel getContentPanel1();


	/** Get the field for node name.
	 * <br>(put in the first panel by subclasses).
	 * @return field */
	public STTextField getNodeNameField() {
		if(nodeNameField == null) {
			nodeNameField = new STTextField(this, true);
			nodeNameField.setToolTipText(STGraphC.getMessage("NODE.DIALOG.VARNAME.TT")); //$NON-NLS-1$
		}
		return nodeNameField;
	}


	/** Get the varAndFunPanel.
	 * <br>(put in the first panel by subclasses).
	 * @return panel */
	protected JPanel getVarAndFunPanel() {
		if(varAndFunPanel == null) {
			varAndFunPanel = new JPanel();
			varAndFunPanel.setLayout(new GridBagLayout());
			// variables: label
			JLabel lab = new JLabel(STGraphC.getMessage("NODE.DIALOG.AVAILABLEVARIABLES") + STTools.COLON); //$NON-NLS-1$
			GridConstraints gbcLab = new GridConstraints(0, 0, GridBagConstraints.NORTHWEST);
			gbcLab.gridwidth = 3;
			varAndFunPanel.add(lab, gbcLab);
			// show local vars?
			varAndFunPanel.add(getCheckShowLocalVars(), new GridConstraints(0, 1, GridBagConstraints.WEST));
			// show global vars?
			varAndFunPanel.add(getCheckShowGlobalVars(), new GridConstraints(1, 1, GridBagConstraints.WEST));
			// show system vars?
			varAndFunPanel.add(getCheckShowSystemVars(), new GridConstraints(2, 1, GridBagConstraints.WEST));
			// variables: list
			GridConstraints gbcVar = new GridConstraints(0, 2, GridBagConstraints.NORTHWEST);
			gbcVar.fill = GridBagConstraints.BOTH;
			gbcVar.weightx = 1.0; gbcVar.weighty = 1.0;
			gbcVar.gridwidth = 3;
			varAndFunPanel.add(getScrollVar(), gbcVar);
			// operators and functions
			varAndFunPanel.add(getFunctionMenus(), new GridConstraints(3, 0, GridBagConstraints.NORTHWEST));
			//
			GridConstraints gbcFun = new GridConstraints(3, 1, GridBagConstraints.NORTHWEST);
			gbcFun.fill = GridBagConstraints.BOTH;
			gbcFun.weightx = 1.0; gbcFun.weighty = 1.0;
			gbcFun.gridheight = 2;
			varAndFunPanel.add(getScrollFun(), gbcFun);
		}
		return varAndFunPanel;
	}


	/** Get the checkShowLocalVars.
	 * @return checkbox */
	protected final JCheckBox getCheckShowLocalVars() {
		if(checkShowLocalVars == null) {
			checkShowLocalVars = new JCheckBox(STGraphC.getMessage("NODE.DIALOG.SHOWLOCAL")); //$NON-NLS-1$
			checkShowLocalVars.setToolTipText(STGraphC.getMessage("NODE.DIALOG.SHOWLOCAL.TT")); //$NON-NLS-1$
			checkShowLocalVars.setSelected(true);
			checkShowLocalVars.addKeyListener(new KeyAdapter() { public void keyReleased(final KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(open = false); return; } } });
			checkShowLocalVars.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { setVarsInList(); } });
		}
		return checkShowLocalVars;
	}


	/** Get the checkShowGlobalVars.
	 * @return checkbox */
	protected final JCheckBox getCheckShowGlobalVars() {
		if(checkShowGlobalVars == null) {
			checkShowGlobalVars = new JCheckBox(STGraphC.getMessage("NODE.DIALOG.SHOWGLOBAL")); //$NON-NLS-1$
			checkShowGlobalVars.setToolTipText(STGraphC.getMessage("NODE.DIALOG.SHOWGLOBAL.TT")); //$NON-NLS-1$
			checkShowGlobalVars.setSelected(true);
			checkShowGlobalVars.addKeyListener(new KeyAdapter() { public void keyReleased(final KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(open = false); return; } } });
			checkShowGlobalVars.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { setVarsInList(); } });
		}
		return checkShowGlobalVars;
	}


	/** Get the checkShowSystemVars.
	 * @return checkbox */
	protected final JCheckBox getCheckShowSystemVars() {
		if(checkShowSystemVars == null) {
			checkShowSystemVars = new JCheckBox(STGraphC.getMessage("NODE.DIALOG.SHOWSYSTEM")); //$NON-NLS-1$
			checkShowSystemVars.setToolTipText(STGraphC.getMessage("NODE.DIALOG.SHOWSYSTEM.TT")); //$NON-NLS-1$
			checkShowSystemVars.setSelected(true);
			checkShowGlobalVars.addKeyListener(new KeyAdapter() { public void keyReleased(final KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(open = false); return; } } });
			checkShowSystemVars.addActionListener(new ActionListener() { public void actionPerformed(final ActionEvent e) { setVarsInList(); } });
		}
		return checkShowSystemVars;
	}


	/** Set the variables to be displayed in the list. */
	@SuppressWarnings("unchecked")
	protected final void setVarsInList() {
		ArrayList<String> al = new ArrayList<String>();
		if(checkShowLocalVars.isSelected()) {
			if(node instanceof ValueNode) {
				ArrayList<STNode> in = node.getDefiningNodesByEdges();
				if(in != null) {
					for(STNode iNode : in) { al.add(iNode.getFullName()); }
				}
			} else {
				ArrayList<STNode> in = node.getDefiningNodesByEdges();
				if(in != null) {
					for(STNode iNode : in) {
						if(!iNode.isModel()) {
							al.add(iNode.getFullName());
						} else {
							if(((ModelNode)iNode).getSubmodel() != null) {
								for(STNode oNode : ((ModelNode)iNode).getSubmodel().getOutputNodeList()) { al.add(oNode.getFullName()); }
							}
						}
					}
				}
			}
		}
		if(checkShowGlobalVars.isSelected()) {
			String s;
			for(STNode iNode : node.getGraph().getNodes()) {
				if(iNode.isGlobal()) {
					s = iNode.getName();
					if(!s.equals(node.getName()) && !al.contains(s)) { al.add(s); }
				}
			}
		}
		if(al != null && al.size() > 0) { Collections.sort(al, STGraph.getSTC().getStringNameComparator()); }
		if(checkShowSystemVars.isSelected()) { al.addAll(STInterpreter.getSystemVars(node.isState(), false)); }
		getListVar().setListData(al.toArray());
	}


	/** Get the scrollVar.
	 * <br>(put in the first panel by subclasses).
	 * @return pane */
	protected JScrollPane getScrollVar() {
		if(scrollVar == null) {
			scrollVar = new JScrollPane();
			scrollVar.setViewportView(getListVar());
		}
		return scrollVar;
	}


	/** Get the listVar.
	 * @return list */
	protected JList getListVar() {
		if(listVar == null) {
			listVar = new JList();
			listVar.setToolTipText(STGraphC.getMessage("NODE.DIALOG.AVAILABLEVARIABLES.TT")); //$NON-NLS-1$
			listVar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listVar.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(final ListSelectionEvent e)  { setHelpText2(listVar); }
			});
			listVar.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					if(!STGraph.getSTC().getCurrentGraph().isEditable) { return; }
					if(listVar.getSelectedValue() != null) { insertText(e, listVar.getSelectedValue().toString(), EditPanel4Nodes.this.currentField); }
				}
			});
		}
		return listVar;
	}


	/** Properly insert the text of the double clicked item into the given field at the current caret position.
	 * @param e the e
	 * @param src the src
	 * @param tgt the tgt */
	public void insertText(final MouseEvent e, final String src, final TokenAwareEditor tgt) {
		STInterpreter interpreter = STGraph.getSTC().getCurrentGraph().getInterpreter();
		if(e.getClickCount() == 2) {
			String txt = tgt.getPlainText(true);
			if(STTools.isEmpty(txt)) {
				tgt.setHTMLText(interpreter, src);
				tgt.setCaretPosition(src.length() + 1);
			} else {
				if(tgt.getSelectedText() == null) {
					int pos = tgt.getCaretPosition() - 1;
					if(pos < 0) { pos = txt.length(); }
					tgt.setHTMLText(interpreter, txt.substring(0, pos) + src + txt.substring(pos));
					tgt.setCaretPosition(pos + src.length() + 1);
				} else {
					int pos1 = tgt.getSelectionStart() - 1;
					int pos2 = tgt.getSelectionEnd() - 1;
					tgt.setHTMLText(interpreter, txt.substring(0, pos1) + src + txt.substring(pos2));
					tgt.setCaretPosition(pos1 + src.length() + 1);
				}
			}
			tgt.requestFocus();
		}
	}


	/** Get the function menus.
	 * <br>(put in the first panel by subclasses).
	 * @return the function menus */
	@SuppressWarnings("unchecked")
	protected JComboBox getFunctionMenus() {
		if(functionMenus == null) {
			functionMenus = new JComboBox();
			functionMenus.setToolTipText(STGraphC.getMessage("NODE.DIALOG.FUNCTIONCATEGORIES.TT")); //$NON-NLS-1$
			FunctionMenu[] m = STGraph.getSTC().getCurrentGraph().getInterpreter().getFunctionMenus();
			functionMenus.addItem(m[0]);
			functionMenus.addItem(STGraphC.getMessage("NODE.DIALOG.OPERATORS")); //$NON-NLS-1$
			for(int i = 1; i < m.length; i++) { functionMenus.addItem(m[i]); }
			functionMenus.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e)  {
					Object t = functionMenus.getSelectedItem();
					if(t instanceof FunctionMenu) {
						getListFun().setModel(new DefaultComboBoxModel(((FunctionMenu)t).getItems()));
						getListFun().setSelectedIndex(0);
						getListFun().ensureIndexIsVisible(0);
					} else {
						getListFun().setModel(new DefaultComboBoxModel(STInterpreter.getOperators()));
						getListFun().setSelectedIndex(0);
						getListFun().ensureIndexIsVisible(0);
					}
				}
			});
		}
		return functionMenus;
	}


	/** Get the scrollFun.
	 * <br>(put in the first panel by subclasses).
	 * @return scrollpane */
	protected JScrollPane getScrollFun() {
		if(scrollFun == null) {
			scrollFun = new JScrollPane();
			scrollFun.setViewportView(getListFun());
		}
		return scrollFun;
	}


	/** Get the listFun.
	 * <br>(put in the first panel by subclasses).
	 * @return list */
	protected JList getListFun() {
		if(listFun == null) {
			listFun = new JList();
			listFun.setToolTipText(STGraphC.getMessage("NODE.DIALOG.FUNCTIONS.TT")); //$NON-NLS-1$
			listFun.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listFun.addMouseListener(new MouseAdapter() { 
				public void mouseClicked(final MouseEvent e) {
					if(!STGraph.getSTC().getCurrentGraph().isEditable) { return; }
					Object t = listFun.getSelectedValue();
					if(t instanceof STFunction) {
						insertText(e, listFun.getSelectedValue().toString() + "()", currentField); //$NON-NLS-1$
					} else {
						insertText(e, listFun.getSelectedValue().toString(), currentField);
					}
				}
			});
			listFun.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(final ListSelectionEvent e)  { setHelpText(listFun); }
			});
		}
		return listFun;
	}


	/** Get the scrollHelp.
	 * <br>(put in the first panel by subclasses).
	 * @return pane */
	protected JScrollPane getScrollHelp() {
		if(scrollHelp == null) {
			scrollHelp = new JScrollPane();
			scrollHelp.setPreferredSize(new Dimension(100, 50));
			scrollHelp.setMinimumSize(new Dimension(100, 50));
			scrollHelp.setViewportView(getHelpText());
		}
		return scrollHelp;
	}


	/** Get the helpText.
	 * <br>(put in the first panel by subclasses).
	 * @return editor */    
	protected JEditorPane getHelpText() {
		if(helpText == null) { helpText = new HelpEditorPane(); }
		return helpText;
	}


	/** Set the text describing the syntax and semantics of the currently selected operator or function.
	 * @param caller the caller */
	private void setHelpText(final JList caller) {
		Object value = caller.getSelectedValue();
		if(value == null) { 
			helpText.setMessage("");
			helpText2.setMessage("");
			return;
		}
		String s1 = value.toString();
		String s2 = STInterpreter.getFunctionDescription(s1);
		helpText.setMessage(s2);
		helpText2.setMessage(s2);
	}


	/** Set the text describing the currently selected input variable.
	 * @param caller the caller */
	private void setHelpText2(final JList caller) {
		Object value = caller.getSelectedValue();
		if(value == null) { 
			helpText.setMessage("");
			helpText2.setMessage("");
			return;
		}
		String s1 = value.toString();
		String s2 = STInterpreter.getVariableDescription(s1);
		helpText.setMessage(s2);
		helpText2.setMessage(s2);
	}


	/** Get the second ("further") panel.
	 * @return the second panel */
	protected JPanel getContentPanel2() {
		if(contentPanel2 == null) {
			contentPanel2 = new JPanel();
			contentPanel2.setLayout(new GridBagLayout());
			// font family
			contentPanel2.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.FONTFAMILY") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			GridConstraints gbcFontFamily = new GridConstraints(1, 0);
			gbcFontFamily.fill = GridBagConstraints.HORIZONTAL;
			contentPanel2.add(getFontFamily(), gbcFontFamily);
			// font size
			contentPanel2.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.FONTSIZE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(2, 0, GridBagConstraints.EAST));
			GridConstraints gbcFontSize = new GridConstraints(3, 0);
			gbcFontSize.anchor = GridBagConstraints.NORTHWEST;
			contentPanel2.add(getFontSizeField(), gbcFontSize);
			// font color
			contentPanel2.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.FONTCOL") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.EAST));
			colorFont = new STColorChooser(STColorChooser.MEDIUM_BUTTON_PANEL) {
				public void actionPerformed(final ActionEvent e) { setDirty(true); }
			};
			contentPanel2.add(colorFont, new GridConstraints(1, 1, GridBagConstraints.WEST));
			// number format
			contentPanel2.add(numberFormatLabel = new JLabel(STGraphC.getMessage("NODE.DIALOG.NUMBERFORMAT") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(2, 1, GridBagConstraints.EAST));
			GridConstraints gbcNumberFormat = new GridConstraints(3, 1);
			gbcNumberFormat.anchor = GridBagConstraints.NORTHWEST;
			contentPanel2.add(getNumberFormatField(), gbcNumberFormat);
			// fore color
			contentPanel2.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.FORECOL") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 2, GridBagConstraints.EAST));
			colorFore = new STColorChooser(STColorChooser.MEDIUM_BUTTON_PANEL) {
				public void actionPerformed(final ActionEvent e) { setDirty(true); }
			};
			contentPanel2.add(colorFore, new GridConstraints(1, 2, GridBagConstraints.WEST));
			// back color
			contentPanel2.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.BACKCOL") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(2, 2, GridBagConstraints.EAST));
			colorBack = new STColorChooser(STColorChooser.MEDIUM_BUTTON_PANEL) {
				public void actionPerformed(final ActionEvent e) { setDirty(true); }
			};
			contentPanel2.add(colorBack, new GridConstraints(3, 2, GridBagConstraints.WEST));
			// documentation
			contentPanel2.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.DOC") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 3, GridBagConstraints.NORTHEAST));
			GridConstraints gbcAreaDoc = new GridConstraints(1, 3);
			gbcAreaDoc.gridwidth = 3;
			gbcAreaDoc.weightx = 1.0; gbcAreaDoc.weighty = 1.0;
			gbcAreaDoc.fill = GridBagConstraints.BOTH;
			contentPanel2.add(getScrollDoc(), gbcAreaDoc);
			// icon file
			contentPanel2.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.ICONFILE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 4, GridBagConstraints.EAST));
			GridConstraints gbcIconFile = new GridConstraints(1, 4);
			gbcIconFile.gridwidth = 3;
			gbcIconFile.fill = GridBagConstraints.HORIZONTAL;
			contentPanel2.add(getIconFile(), gbcIconFile);
		}
		return contentPanel2;
	}


	/** Initialize font family.
	 * @return list */
	@SuppressWarnings("unchecked")
	private JComboBox getFontFamily() {
		if(fontFamily == null) {
			fontFamily = new JComboBox();
			fontFamily.setEditable(false);
			fontFamily.addItem("SansSerif"); //$NON-NLS-1$
			fontFamily.addItem("Serif"); //$NON-NLS-1$
			fontFamily.addItem("Monospaced"); //$NON-NLS-1$
		}
		return fontFamily;
	}


	/** Get the field for font size.
	 * @return field */
	private STTextField getFontSizeField() {
		if(fontSize == null) {
			fontSize = new STTextField(this, true);
			fontSize.setPreferredSize(new Dimension(70, 20));
		}
		return fontSize;
	}


	/** Get the field for number format.
	 * @return field */
	protected STTextField getNumberFormatField() {
		if(numberFormat == null) {
			numberFormat = new STTextField(this, true);
			numberFormat.setPreferredSize(new Dimension(70, 20));
		}
		return numberFormat;
	}


	/** Initialize scrollDoc.
	 * @return pane */
	protected JScrollPane getScrollDoc() {
		if(scrollDoc == null) {
			scrollDoc = new JScrollPane();
			scrollDoc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollDoc.setViewportView(getAreaDoc());
		}
		return scrollDoc;
	}


	/** Initialize areaDoc.
	 * @return textarea */
	protected STTextArea getAreaDoc() {
		if(areaDoc == null) {
			areaDoc = new STTextArea(this, true);
			areaDoc.setToolTipText(STGraphC.getMessage("NODE.DIALOG.DOC.TT")); //$NON-NLS-1$
		}
		return areaDoc;
	}


	/** Initialize iconFile.
	 * @return list */
	@SuppressWarnings("unchecked")
	private JComboBox getIconFile() {
		if(iconFile == null) {
			iconFile = new JComboBox();
			iconFile.setToolTipText(STGraphC.getMessage("NODE.DIALOG.ICONFILE.TT")); //$NON-NLS-1$
			iconFile.setEditable(false);
		}
		iconFile.removeAllItems();
		iconFile.addItem(STTools.SPACE); // corresponding to default icon
		if(node instanceof ModelNode) {
			String s = node.getDefaultIconFile();
			if(s != null) { // i.e., it is from the library
				iconFile.addItem(DEFAULT_ITEM_FOR_LIBRARY_MODEL_ICON);
			}
		}
		if(STGraph.getSTC().getCurrentGraph().getFileName() != null) {
			String curDir = STGraph.getSTC().getCurrentGraph().contextName;
			File[] files = (new File(curDir)).listFiles(new STFileFilter5());
			if(files != null && files.length > 0) {
				Arrays.sort(files);
				for(int i = 0; i < files.length; i++) { iconFile.addItem(files[i].getName()); }
			}
		}
		return iconFile;
	}


	/** Get the third ("custom") panel.
	 * @return the third panel */
	protected JPanel getContentPanel3() {
		if(contentPanel3 == null) {
			contentPanel3 = new JPanel();
			contentPanel3.setLayout(new BorderLayout());
			dataModel = new DefaultTableModel(new Object[] { STGraphC.getMessage("NODE.DIALOG.KEY"), STGraphC.getMessage("NODE.DIALOG.VALUE") }, 0); //$NON-NLS-1$ //$NON-NLS-2$
			cPropTable = new STTable1(dataModel);
			cPropTable.addKeyListener( new KeyAdapter() { public void keyReleased(final KeyEvent e) { setDirty(true); } } );
			JScrollPane scrollpane = new JScrollPane(cPropTable);
			contentPanel3.add(BorderLayout.CENTER, scrollpane);

			newCPropButton = new JButton(STGraphC.getMessage("NODE.DIALOG.NEW")); //$NON-NLS-1$
			newCPropButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					dataModel.addRow(new Object[] { STGraphC.getMessage("NODE.DIALOG.KEY"), STGraphC.getMessage("NODE.DIALOG.VALUE") }); //$NON-NLS-1$ //$NON-NLS-2$
					setDirty(true);
				}
			});

			delCPropButton = new JButton(STGraphC.getMessage("NODE.DIALOG.DEL")); //$NON-NLS-1$
			delCPropButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if(cPropTable.getSelectedRow() != -1 && cPropTable.getSelectedRow() > predefinedKeys.size()) {
						dataModel.removeRow(cPropTable.getSelectedRow());
						setDirty(true);
					}
				}
			});

			JPanel buttPanel = new JPanel();
			buttPanel.add(newCPropButton);
			buttPanel.add(delCPropButton);
			contentPanel3.add(BorderLayout.SOUTH, buttPanel);
		}
		return contentPanel3;
	}


	/** Get the fourth ("web") panel.
	 * @return the forth panel */
	protected JPanel getContentPanel4() {
		if(contentPanel4 == null) {
			contentPanel4 = new JPanel();
			contentPanel4.setLayout(new BorderLayout());
			webDataModel = new DefaultTableModel(new Object[] { STGraphC.getMessage("NODE.DIALOG.KEY"), STGraphC.getMessage("NODE.DIALOG.VALUE") }, 0); //$NON-NLS-1$ //$NON-NLS-2$
			cWebPropTable = new STTable2(webDataModel);
			cWebPropTable.addKeyListener( new KeyAdapter() { public void keyReleased(final KeyEvent e) { setDirty(true); } } );
			JScrollPane scrollpane = new JScrollPane(cWebPropTable);
			contentPanel4.add(BorderLayout.CENTER, scrollpane);

			JPanel buttPanel = new JPanel();
			contentPanel4.add(BorderLayout.SOUTH, buttPanel);
		}
		return contentPanel4;
	}


	/** Custom help handler. */
	public class HelpEditorPane extends JEditorPane {
		/** The helptext size. */
		protected String helptextSize = STConfigurator.getProperty("HELPTEXT.SIZE"); //$NON-NLS-1$


		/** Class constructor. */
		HelpEditorPane() {
			setContentType("text/html"); //$NON-NLS-1$
			setEditable(false);
			setBackground(getContentPanel1().getBackground());

			addHyperlinkListener(new HyperlinkListener() { 
				public void hyperlinkUpdate(final HyperlinkEvent e) {
					if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) { STTools.infoTextDialog.showMe(STTools.BLANK, STTools.file2HTMLString(e.getDescription()), true); }
				}
			});
		}


		/** Set the message to be displayed.
		 * @param message the message */
		public final void setMessage(final String message) {
			setText("<html><font size='" + helptextSize + "'>" + message + "</font></html>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			setCaretPosition(0);
		}


		/** Set the error message to be displayed.
		 * @param message the message */
		public final void setErrorMessage(final String message) {
			errorMessage = message;
			if(STTools.isEmpty(message)) { errorMessage = STTools.BLANK; }
			setText("<html><center><font color='red' size='3'>" + STTools.replaceCRwithBR(errorMessage) + "</font></center></html>"); //$NON-NLS-1$ //$NON-NLS-2$
			setCaretPosition(0);
		}

	}


	/** Set the icon for this node.
	 * @param name the icon name
	 * @return result */
	@SuppressWarnings("unchecked")
	private boolean setIcon(final String name) {
		AttributeMap map = node.getAttributes();
		if(name == null) {
			map.put(GraphConstants.ICON, new ImageIcon());
			map.put(GraphConstants.VERTICAL_TEXT_POSITION, Integer.valueOf(SwingConstants.CENTER));
			map.put(GraphConstants.VERTICAL_ALIGNMENT, Integer.valueOf(SwingConstants.CENTER));
		} else if(name.equals(DEFAULT_ITEM_FOR_LIBRARY_MODEL_ICON)) { // i.e., it is from the library
			String s = node.getDefaultIconFile();
			if(s.equals("customDefault.png")) { // i.e., it is a model without a specific icon //$NON-NLS-1$
				map.put(GraphConstants.ICON, new ImageIcon(getClass().getClassLoader().getResource("icons/base/" + s))); //$NON-NLS-1$
			} else { // i.e., it is a model with a specific icon
				URL fileURL = null;
				try {
					fileURL = (new File(STGraphC.getBasicProps().getProperty("MODEL.DIR").substring(2) + "/" + s)).toURI().toURL(); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (Exception e) { 
					fileURL = getClass().getClassLoader().getResource("icons/base/customDefault.png"); //$NON-NLS-1$
				}
				map.put(GraphConstants.ICON, new ImageIcon(fileURL));
			}
			map.put(GraphConstants.VERTICAL_TEXT_POSITION, Integer.valueOf(SwingConstants.BOTTOM));
			map.put(GraphConstants.VERTICAL_ALIGNMENT, Integer.valueOf(SwingConstants.BOTTOM));
		} else {
			String fileName;
			String curDir = STGraph.getSTC().getCurrentGraph().contextName;
			if(curDir == null) {
				helpText.setErrorMessage(STGraphC.getMessage("ERR_WRONG_CONTEXT")); //$NON-NLS-1$
				tabbedPanel.setSelectedIndex(0);
				return false;
			}
			fileName = curDir + (STGraphC.isApplet() ? "/" : System.getProperty("file.separator")) + name; //$NON-NLS-1$ //$NON-NLS-2$
			if(!(new File(fileName)).exists()) {
				helpText.setErrorMessage(STGraphC.getMessage("ERR_WRONG_ICON_FILE")); //$NON-NLS-1$
				tabbedPanel.setSelectedIndex(0);
				return false;
			}
			//TODO a bug prevents repainting a changed icon (the model must be reloaded for the new icon to appear...)
			map.put(GraphConstants.ICON, new ImageIcon(fileName));
			map.put(GraphConstants.VERTICAL_TEXT_POSITION, Integer.valueOf(SwingConstants.BOTTOM));
			map.put(GraphConstants.VERTICAL_ALIGNMENT, Integer.valueOf(SwingConstants.BOTTOM));

		}
		node.setAttributes(map);
		node.setIconFile(name);
		return true;
	}


	//******************************* dynamic handling *************************************


	/** Open the dialog.
	 * @param node the node */
	public abstract void open(final STNode node);


	/** Initialize the dialog. */
	public final void startInit() {
		setTitle(STGraphC.getMessage("NODE.DIALOG.EDITTITLE") + STTools.SPACE + node.getCName()); //$NON-NLS-1$
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		getNodeNameField().setEnabled(b);
		getCheckShowLocalVars().setEnabled(b);
		getCheckShowGlobalVars().setEnabled(b);
		getCheckShowSystemVars().setEnabled(b);
		getListVar().setEnabled(b);
		getFunctionMenus().setEnabled(b);
		getListFun().setEnabled(b);
		getFontFamily().setEnabled(b);
		getFontSizeField().setEnabled(b);
		getNumberFormatField().setEnabled(b);
		colorFont.setEnabled(b); colorFore.setEnabled(b); colorBack.setEnabled(b);
		getAreaDoc().setEnabled(b);
		getIconFile().setEnabled(b);
		cPropTable.setEnabled(b); newCPropButton.setEnabled(b); delCPropButton.setEnabled(b);
		cWebPropTable.setEnabled(b);
		helpText.setText("&nbsp;"); //$NON-NLS-1$
		errorBuffer = node.getGraph().getLastError();
	}


	/** Complete the dialog initialization.
	 * @param activeTab the active tab */
	public final void completeInit(final int activeTab) {
		tabbedPanel.setSelectedIndex(activeTab);
		getRootPane().setDefaultButton(getButtonOk());
		if(this instanceof EditPanel4ValueNodes) {
			setLocation(locV);
			setPreferredSize(dimV);
			if(isExpandedV) {
				buttonSoEview.setText(STGraphC.getMessage("DIALOG.SOEVIEW.SYN")); //$NON-NLS-1$
				varAndFunPanel.setVisible(true);
			} else {
				buttonSoEview.setText(STGraphC.getMessage("DIALOG.SOEVIEW.EXP")); //$NON-NLS-1$
				varAndFunPanel.setVisible(false);
			}
		} else {
			setLocation(locM);
			setPreferredSize(dimM);
			if(isExpandedM) {
				buttonSoEview.setText(STGraphC.getMessage("DIALOG.SOEVIEW.SYN")); //$NON-NLS-1$
				varAndFunPanel.setVisible(true);
			} else {
				buttonSoEview.setText(STGraphC.getMessage("DIALOG.SOEVIEW.EXP")); //$NON-NLS-1$
				varAndFunPanel.setVisible(false);
			}
		}
		pack();
		setVisible(open = true);
	}


	/** Fill the dialog with the configuration data taken from the node properties. */
	@SuppressWarnings("unchecked")
	public void fill() {
		getNodeNameField().setText(node.getName());
		getFunctionMenus().setSelectedIndex(0);
		getListFun().setModel(new DefaultComboBoxModel(STGraph.getSTC().getCurrentGraph().getInterpreter().getFunctionMenus()[0].getItems()));
		Object p = node.getPlainFont();
		if(p != null) {
			fontFamily.setSelectedItem(String.valueOf(((Font)p).getFamily()));
			fontSize.setText(String.valueOf(((Font)p).getSize()));
		} else {
			fontFamily.setSelectedItem(STConfigurator.getProperty("NODE.FONTNAME"));
			fontSize.setText(STConfigurator.getProperty("NODE.FONTSIZE"));
		}
		p = node.getNumberFormat();
		numberFormat.setText(p != null ? (String)p : STConfigurator.getProperty("NODE.FONTSIZE")); //with default
		p = node.getFontColor();
		colorFont.setColor(p != null ? (Color)p : Color.BLACK); //with default
		p = node.getForeColor();
		colorFore.setColor(p != null ? (Color)p : Color.BLACK); //with default
		p = node.getBackColor();
		colorBack.setColor(p != null ? (Color)p : Color.WHITE); //with default
		p = node.getDescription();
		areaDoc.setText(p != null ? (String)p : STTools.BLANK); //with default
		iconFile.setSelectedIndex(0); // the default case
		p = node.getIconFile();
		if(p != null) { iconFile.setSelectedItem(p); }
		dataModel = new DefaultTableModel(new Object[] { STGraphC.getMessage("NODE.DIALOG.KEY"), STGraphC.getMessage("NODE.DIALOG.VALUE") }, 0);
		webDataModel = new DefaultTableModel(new Object[] { STGraphC.getMessage("NODE.DIALOG.KEY"), STGraphC.getMessage("NODE.DIALOG.VALUE") }, 0);
		String name = node.getCProperty("Name");
		dataModel.addRow(new Object[] { "Name", STTools.isEmpty(name) ? node.getName() : name });


		String[][] d = node.getGraph().getWebModelGroupData(node.getGraph().getDefaultModelLanguage());
		cWebPropTable.groupMap = new HashMap<String, String>();
		if(d == null || d.length == 0) {
			cWebPropTable.group = new JComboBox(new String[] { STTools.BLANK, "0=" + STGraphC.getMessage("WEB.CPROP.NOGROUP") });
		} else {
			cWebPropTable.group = new JComboBox();
			cWebPropTable.group.addItem(STTools.BLANK);
			cWebPropTable.group.addItem("00=" + STGraphC.getMessage("WEB.CPROP.NOGROUP"));
			cWebPropTable.groupMap.put("00", STGraphC.getMessage("WEB.CPROP.NOGROUP"));
			for(int i = 0; i < d.length; i++) {
				if(i < 9) {
					cWebPropTable.group.addItem("0" + (i + 1) + "=" + d[i][0]);
					cWebPropTable.groupMap.put("0" + (i + 1), d[i][0]);
				} else {
					cWebPropTable.group.addItem((i + 1) + "=" + d[i][0]);
					cWebPropTable.groupMap.put(STTools.BLANK + (i + 1), d[i][0]);
				}
			}
		}

		Map<String, String> properties = node.getCProperties();
		if(properties != null) {
			for(String key : predefinedKeys) {
				String v = properties.get(key);
				dataModel.addRow(new Object[] { key, STTools.isEmpty(v) ? STTools.BLANK : v });
			}
			for(String key : properties.keySet()) {
				if(!key.equals("Name") && !predefinedKeys.contains(key) && !predefinedWebKeys.contains(key)) { dataModel.addRow(new Object[] { key, properties.get(key) }); }
			}

			for(String key : predefinedWebKeys) {
				String v = properties.get(key);
				if(key.equals("Group")) {
					webDataModel.addRow(new Object[] { key, STTools.isEmpty(v) ? STTools.BLANK : v + "=" + cWebPropTable.groupMap.get(v) });
				}else if(key.equals("InputType")) {
					webDataModel.addRow(new Object[] { key, STTools.isEmpty(v) ? STTools.BLANK : v + "=" + cWebPropTable.inputMap.get(v) });
				} else if(key.equals("OutputType")) {
					webDataModel.addRow(new Object[] { key, STTools.isEmpty(v) ? STTools.BLANK : v + "=" + cWebPropTable.outputMap.get(v) });
				} else if(key.equals("DefaultPreferred")) {
					webDataModel.addRow(new Object[] { key, STTools.isEmpty(v) ? STTools.BLANK : v + "=" + cWebPropTable.defaultMap.get(v) });
				} else {
					webDataModel.addRow(new Object[] { key, STTools.isEmpty(v) ? STTools.BLANK : v });
				}
			}			
		} else {
			if(node instanceof ValueNode) {
				for(String key : predefinedKeys) {
					dataModel.addRow(new Object[] { key, STTools.BLANK });
				}
			}
		}
		cPropTable.setModel(dataModel);
		cWebPropTable.setModel(webDataModel);

		TableColumnModel tcm = cPropTable.getColumnModel();
		tcm.getColumn(0).setMinWidth(110);
		tcm.getColumn(0).setPreferredWidth(110);
		tcm.getColumn(1).setPreferredWidth(1000);
		tcm = cWebPropTable.getColumnModel();
		tcm.getColumn(0).setMinWidth(110);
		tcm.getColumn(0).setPreferredWidth(110);
		tcm.getColumn(1).setPreferredWidth(1000);

		getNodeNameField().requestFocusInWindow();
	}


	/** Start the dialog finalization.
	 * @param isOutSelected isOutSelected
	 * @return result */
	public final boolean startExit(final boolean isOutSelected) {
		if(!STGraph.getSTC().getCurrentGraph().isEditable) {
			setVisible(open = false);
			return false;
		}
		helpText.setErrorMessage(null);
		String t1 = getNodeNameField().getText().trim();
		String t1b = node.getName();
		if(node.isOutput() && !isOutSelected) {
			for(STWidget widget : STGraph.getSTC().getCurrentGraph().getWidgets()) { widget.handleNodeRemoval(t1b); }
		}
		if(!t1b.equals(t1)) {
			String result = null;
			if((result = node.checkName(t1)) != null) { helpText.setErrorMessage(result); return false; }
			node.redefineVars(t1b, t1);
			node.setName(t1);
			node.handleNodeRenaming(t1b);
		}
		return true;
	}


	/** Start the dialog finalization.
	 * @return result */
	public final boolean startExit() {
		if(!STGraph.getSTC().getCurrentGraph().isEditable) {
			setVisible(open = false);
			return false;
		}
		helpText.setErrorMessage(null);
		String t1 = getNodeNameField().getText().trim();
		String t1b = node.getName();
		if(!t1b.equals(t1)) {
			String result = null;
			if((result = node.checkName(t1)) != null) { helpText.setErrorMessage(result); return false; }
			node.redefineVars(t1b, t1);
			node.setName(t1);
			node.handleNodeRenaming(t1b);
		}
		return true;
	}


	/** Complete the dialog finalization.
	 * @param mode the mode, either <code>MODE_OK</code> or <code>MODE_APPLY</code> */
	public final void completeExit(final int mode) {
		int f = 0;
		String s = fontSize.getText();
		if(s != null) {
			try {
				f = Integer.parseInt(s);
				if(f < 5 || f > 100) { throw new Exception(); }
			} catch (Exception e) {
				f = Integer.parseInt(STConfigurator.getProperty("NODE.FONTSIZE")); //$NON-NLS-1$
			}
		}
		node.setPlainFont(new Font((String)fontFamily.getSelectedItem(), Font.PLAIN, f));
		node.setBoldFont(new Font((String)fontFamily.getSelectedItem(), Font.BOLD, f));
		node.setNumberFormat(numberFormat.getText());
		Color c = colorFont.getColor(); // handle font, border, and background node colors
		if(c != null) { node.setFontColor(c); }
		c = colorFore.getColor();
		if(c != null) { node.setForeColor(c); }
		c = colorBack.getColor();
		if(c != null) { node.setBackColor(c); }

		s = areaDoc.getText(); // handle description
		if(s != null) { node.setDescription(s); }

		int j = iconFile.getSelectedIndex(); // handle node icon
		if(j == 0) { // default icon
			setIcon(null);
		} else {
			setIcon((String)iconFile.getSelectedItem());
		}

		Map<String, String> properties = new HashMap<String, String>(); // handle custom properties
		for(int i = 0; i < dataModel.getRowCount(); i++) { properties.put((String)dataModel.getValueAt(i, 0), (String)dataModel.getValueAt(i, 1)); }
		node.setCProperties(properties);

		properties = new HashMap<String, String>(); // handle custom web properties
		for(int i = 0; i < webDataModel.getRowCount(); i++) { properties.put((String)webDataModel.getValueAt(i, 0), (String)webDataModel.getValueAt(i, 1)); }
		if(properties.containsKey("Group")) { properties.put("Group", properties.get("Group").split("=")[0]); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		if(properties.containsKey("InputType")) { properties.put("InputType", properties.get("InputType").split("=")[0]); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		if(properties.containsKey("OutputType")) { properties.put("OutputType", properties.get("OutputType").split("=")[0]); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		if(properties.containsKey("DefaultPreferred")) { properties.put("DefaultPreferred", properties.get("DefaultPreferred").split("=")[0]); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		node.appendCProperties(properties);

		if(!node.getName().startsWith(STGraphC.getMessage("NODE.DEFAULT_PREFIX")) && node.getCName().startsWith(STGraphC.getMessage("NODE.DEFAULT_PREFIX"))) { node.setCName(node.getName()); } //$NON-NLS-1$ //$NON-NLS-2$

		node.setProperty(GraphConstants.FONT, (node.isVector() || node.isMatrix()) ? node.getBoldFont() : node.getPlainFont());

		if(!STTools.isEmpty(errorMessage)) { return; }
		STGraphExec graph = node.getGraph();
		graph.computeInteractively();
		if(graph.getLastError() != null && graph.getLastErrorNode() != null && node.getName().equals(graph.getLastErrorNode().getName())) {
			helpText.setErrorMessage(graph.getLastError());
			return;
		}

		STGraph.getSTC().signalCurrentGraphAsModified();
		setDirty(false);

		if(mode == MODE_OK) {
			setVisible(open = false);
			STGraphC.setFocus();
			graph.getGraphLayoutCache().toBack(node.getGraph().getEdges()); // prevent the node for going backwards with respect to arrows
			graph.setSelectionCell(node);
		} else {
			setVisible(true);
			requestFocus();
		}
	}


	/** Class for handling (non-web) custom properties interaction. */
	private class STTable1 extends JTable {

		public STTable1(DefaultTableModel dataModel) { super(dataModel); }

		@Override
		public boolean isCellEditable(int row, int column) {
			String key = (String)getValueAt(row, column);
			return column == 1 || (!predefinedKeys.contains(key) && row != 0);
		}

		@Override
		public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
			Component c = super.prepareRenderer(renderer, row, column);
			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				if(row <= predefinedKeys.size()) {
					jc.setToolTipText(STGraphC.getMessage("CPROP." + getValueAt(row, 0).toString().toUpperCase() + ".TT")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			return c;
		}

	}


	/** Class for handling web custom properties interaction. */
	private class STTable2 extends JTable {
		// the custom props to be handled by this table (as defined in
		//      stgraph.basic.properties : CUSTPROP.WEB.NAMES
		// are the following:
		// 0: Order
		// 1: Group (handled below)
		// 2: InputType (handled below)
		// 3: OutputType (handled below)
		// 4: DefaultValue
		// 5: DefaultPreferred (handled below)
		// 6: Phase
		// 7: PhaseNullValue

		JComboBox group = null;
		Map<String, String> groupMap = null;
		JComboBox inputType = null;
		Map<String, String> inputMap = null;
		JComboBox outputType = null;
		Map<String, String> outputMap = null;
		JComboBox defaultPreferred = null;
		Map<String, String> defaultMap = null;


		@SuppressWarnings("unchecked")
		public STTable2(DefaultTableModel dataModel) {
			super(dataModel);
			// the combo and map for groups is dynamic: they are handled in the fill() method
			String[] s = STGraphC.getMessage("WEB.CPROP.INPUTTYPE").split(STTools.SEMICOLON); //$NON-NLS-1$
			inputType = new JComboBox(s);
			inputMap = new HashMap<String, String>();
			for(String t : s) {
				String[] u = t.split("="); //$NON-NLS-1$
				if(u.length == 2) { inputMap.put(u[0], u[1]); }
			}
			s = STGraphC.getMessage("WEB.CPROP.OUTPUTTYPE").split(STTools.SEMICOLON); //$NON-NLS-1$
			outputType = new JComboBox(s);
			outputMap = new HashMap<String, String>();
			for(String t : s) {
				String[] u = t.split("="); //$NON-NLS-1$
				if(u.length == 2) { outputMap.put(u[0], u[1]); }
			}
			s = STGraphC.getMessage("WEB.CPROP.DEFAULTPREFERRED").split(STTools.SEMICOLON); //$NON-NLS-1$
			defaultPreferred = new JComboBox(s);
			defaultMap = new HashMap<String, String>();
			for(String t : s) {
				String[] u = t.split("="); //$NON-NLS-1$
				if(u.length == 2) { defaultMap.put(u[0], u[1]); }
			}
		}

		@Override
		public TableCellEditor getCellEditor(int row, int column) {
			if(row == 1 && column == 1) { return new DefaultCellEditor(group); }
			if(row == 2 && column == 1) { return new DefaultCellEditor(inputType); }
			if(row == 3 && column == 1) { return new DefaultCellEditor(outputType); }
			if(row == 5 && column == 1) { return new DefaultCellEditor(defaultPreferred); }
			return super.getCellEditor(row, column);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			String key = (String)getValueAt(row, column);
			return column == 1 || !predefinedWebKeys.contains(key);
		}

	}

}
