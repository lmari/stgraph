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

import it.liuc.bg.bgicons.IconManager;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STTextArea;
import it.liuc.stgraph.util.STTextField;
import it.liuc.stgraph.util.STTools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/** Dialog for web configuration of the current model. */
@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class STWebDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The content pane. */
	private JPanel jContentPane;
	/** The list locale. */
	private JComboBox listLocale;
	/** The edit button. */
	private JButton editButton;
	/** The model description. */
	private STTextField modelDesc;
	/** The time unit. */
	private STTextField timeUnit;
	/** The title of the explanatory page. */
	private STTextField explTitle;
	/** The URL of the explanatory page. */
	private STTextField explURL;	
	/** The group panel. */
	private JPanel groupPanel;
	/** The group table. */
	private STTable groupTable;
	/** The group data model. */
	private DefaultTableModel groupDataModel;
	/** The new group button. */
	private JButton newGroupButton;
	/** The del group button. */
	private JButton delGroupButton;
	/** The button ok. */
	private JButton buttonOk;
	/** The button cancel. */
	private JButton buttonCancel;
	/** The button panel. */
	private JPanel buttonPanel;
	/** The edit dialog. */
	private HashMap<String, TextDialog> editDialogs;
	/** The edit description dialog. */
	private DescDialog editDescDialog;
	/** The current language. */
	private String currentLang;
	/** The loading. */
	private boolean loading;

	private static int TABLE_NAME = 0;
	private static int TABLE_ORDER = 1;
	private static int TABLE_CNAME = 2;
	private static int TABLE_UNIT = 3;
	private static int TABLE_EDITDESC = 4;
	private static int TABLE_DESC = 5;
	private static int TABLE_INTEXT = 6;
	private static int TABLE_OUTTEXT = 7;

	/** Class constructor. */
	public STWebDialog() {
		super();
		if(STGraphC.getContainer() instanceof JFrame) { setAlwaysOnTop(true); }
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setModal(true);
		setIconImage(STGraphC.getSystemIcon());
	}


	/** Initialize and open the dialog. */
	public void open() {
		setTitle(STGraphC.getMessage("WEBMODEL.DIALOG.EDITTITLE")); //$NON-NLS-1$
		setContentPane(getJContentPane());
		fill();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		listLocale.setEnabled(b);
		modelDesc.setEnabled(b);
		timeUnit.setEnabled(b);
		explTitle.setEnabled(b);
		explURL.setEnabled(b);
		groupTable.setEnabled(b); newGroupButton.setEnabled(b); delGroupButton.setEnabled(b);
		jContentPane.getRootPane().setDefaultButton(getButtonOk());
		pack();
		setLocation(STGraphC.getContainer().getX() + 100, STGraphC.getContainer().getY() + 100);
		setVisible(true);
	}


	/** Fill the fields in the dialog. */
	private void fill() {
		loading = true;
		getListLocale().setSelectedIndex(0);
		currentLang = (String)listLocale.getSelectedItem();
		fillHelper(currentLang);
		loading = false;
		TableColumnModel tcm = groupTable.getColumnModel();
		tcm.getColumn(0).setMinWidth(25);
		tcm.getColumn(0).setPreferredWidth(25);
		tcm.getColumn(1).setPreferredWidth(200);
		tcm.getColumn(2).setPreferredWidth(300);
		tcm.getColumn(3).setPreferredWidth(300);
		tcm.getColumn(4).setPreferredWidth(300);
		tcm.getColumn(5).setPreferredWidth(300);
	}


	/** Initialize jContentPane.
	 * @return panel */
	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel(new GridBagLayout());
			// model locale
			jContentPane.add(new JLabel(STGraphC.getMessage("WEBMODEL.EDIT.LOCALE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			//
			jContentPane.add(getListLocale(), new GridConstraints(1, 0, GridBagConstraints.WEST));
			// edit localized data
			jContentPane.add(getEditButton(), new GridConstraints(2, 0, GridBagConstraints.CENTER));
			// model description
			jContentPane.add(new JLabel(STGraphC.getMessage("WEBMODEL.EDIT.DESC") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.EAST));
			//
			GridConstraints gbcModelDesc = new GridConstraints(1, 1);
			gbcModelDesc.gridwidth = 2;
			gbcModelDesc.fill = GridBagConstraints.HORIZONTAL;
			gbcModelDesc.weightx = 1.0;
			jContentPane.add(getModelDesc(), gbcModelDesc);
			// time unit
			jContentPane.add(new JLabel(STGraphC.getMessage("WEBMODEL.EDIT.TIMEUNIT") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 2, GridBagConstraints.EAST));
			//
			GridConstraints gbcTimeUnit = new GridConstraints(1, 2);
			gbcTimeUnit.gridwidth = 2;
			gbcTimeUnit.fill = GridBagConstraints.HORIZONTAL;
			gbcTimeUnit.weightx = 1.0;
			jContentPane.add(getTimeUnit(), gbcTimeUnit);
			
			// explanation title
			jContentPane.add(new JLabel(STGraphC.getMessage("WEBMODEL.EDIT.EXPLTITLE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 3, GridBagConstraints.EAST));
			//
			GridConstraints gbcExplTitle = new GridConstraints(1, 3);
			gbcExplTitle.gridwidth = 2;
			gbcExplTitle.fill = GridBagConstraints.HORIZONTAL;
			gbcExplTitle.weightx = 1.0;
			jContentPane.add(getExplTitle(), gbcExplTitle);

			// explanation URL
			jContentPane.add(new JLabel(STGraphC.getMessage("WEBMODEL.EDIT.EXPLURL") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 4, GridBagConstraints.EAST));
			//
			GridConstraints gbcExplURL = new GridConstraints(1, 4);
			gbcExplURL.gridwidth = 2;
			gbcExplURL.fill = GridBagConstraints.HORIZONTAL;
			gbcExplURL.weightx = 1.0;
			jContentPane.add(getExplURL(), gbcExplURL);

			// group panel
			jContentPane.add(new JLabel(STGraphC.getMessage("WEBMODEL.EDIT.GROUPS") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 5, GridBagConstraints.EAST));
			//
			GridConstraints gbcGroup = new GridConstraints(0, 6);
			gbcGroup.gridwidth = 3;
			gbcGroup.fill = GridBagConstraints.BOTH;
			gbcGroup.weighty = 1.0;
			jContentPane.add(getGroupPanel(), gbcGroup);
			// buttons
			GridConstraints gbcButt = new GridConstraints(0, 7);
			gbcButt.gridwidth = 3;
			gbcButt.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getButtonPanel(), gbcButt);
		}
		return jContentPane;
	}


	/** Initialize listLocale.
	 * @return combobox */
	private JComboBox getListLocale() {
		if(listLocale == null) {
			listLocale = new JComboBox();
			listLocale.setToolTipText(STGraphC.getMessage("WEBMODEL.EDIT.LOCALE.TT")); //$NON-NLS-1$
			listLocale.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!loading) {
						saveHelper(currentLang);
						fillHelper(currentLang = (String)listLocale.getSelectedItem());
					}
				}
			});
		}
		listLocale.setModel(new DefaultComboBoxModel(STGraph.getSTC().getCurrentGraph().getModelLanguages()));
		return listLocale;
	}


	/** Initialize editButton.
	 * @return button */
	private JButton getEditButton() {
		if(editButton == null) {
			editButton = new JButton(STGraphC.getMessage("WEBMODEL.EDIT.EDIT")); //$NON-NLS-1$
			editButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editDialogs = new HashMap<String, TextDialog>();
					for(String lang : STGraph.getSTC().getCurrentGraph().getModelLanguages()) {
						editDialogs.put(lang, new TextDialog(lang));
					}
					editDialogs.get(currentLang).open();
				}
			});
		}
		return editButton;
	}


	/** Initialize modelDesc.
	 * @return text */
	private STTextField getModelDesc() {
		if(modelDesc == null) {
			modelDesc = new STTextField(this, false);
			modelDesc.setToolTipText(STGraphC.getMessage("WEBMODEL.EDIT.DESC.TT")); //$NON-NLS-1$
			modelDesc.setPreferredSize(new Dimension(300, 20));
		}
		return modelDesc;
	}


	/** Initialize timeUnit.
	 * @return text */
	private STTextField getTimeUnit() {
		if(timeUnit == null) {
			timeUnit = new STTextField(this, false);
			timeUnit.setToolTipText(STGraphC.getMessage("WEBMODEL.EDIT.TIMEUNIT.TT")); //$NON-NLS-1$
			timeUnit.setPreferredSize(new Dimension(300, 20));
		}
		return timeUnit;
	}


	/** Initialize explTitle.
	 * @return text */
	private STTextField getExplTitle() {
		if(explTitle == null) {
			explTitle = new STTextField(this, false);
			explTitle.setToolTipText(STGraphC.getMessage("WEBMODEL.EDIT.EXPLTITLE.TT")); //$NON-NLS-1$
			explTitle.setPreferredSize(new Dimension(300, 20));
		}
		return explTitle;
	}


	/** Initialize explURL.
	 * @return text */
	private STTextField getExplURL() {
		if(explURL == null) {
			explURL = new STTextField(this, false);
			explURL.setToolTipText(STGraphC.getMessage("WEBMODEL.EDIT.EXPLURL.TT")); //$NON-NLS-1$
			explURL.setPreferredSize(new Dimension(300, 20));
		}
		return explURL;
	}


	/** Initialize groupPanel.
	 * @return panel */
	private JPanel getGroupPanel() {
		if(groupPanel == null) {
			groupPanel = new JPanel();
			groupPanel.setLayout(new BorderLayout());
			groupPanel.setMinimumSize(new Dimension(600, 150));
			groupPanel.setPreferredSize(new Dimension(600, 150));
			groupDataModel = new DefaultTableModel(new Object[] { STGraphC.getMessage("WEBMODEL.EDIT.GROUP.ID"), // See the structure in the documentation of adaptGroupInfo() //$NON-NLS-1$
					STGraphC.getMessage("WEBMODEL.EDIT.GROUP.TYPE"), //$NON-NLS-1$
					STGraphC.getMessage("WEBMODEL.EDIT.GROUP.TITLE"), //$NON-NLS-1$
					STGraphC.getMessage("WEBMODEL.EDIT.GROUP.DESC"), //$NON-NLS-1$
					STGraphC.getMessage("WEBMODEL.EDIT.GROUP.IMG"), //$NON-NLS-1$
					STGraphC.getMessage("WEBMODEL.EDIT.GROUP.HELP")}, 0); //$NON-NLS-1$
			groupTable = new STTable(groupDataModel);
			JScrollPane scrollpane = new JScrollPane(groupTable);
			groupPanel.add(BorderLayout.CENTER, scrollpane);

			newGroupButton = new JButton(STGraphC.getMessage("NODE.DIALOG.NEW")); //$NON-NLS-1$
			newGroupButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Integer newGroupId = Integer.valueOf(groupDataModel.getRowCount() + 1);
					groupDataModel.addRow(new Object[] { newGroupId,
							STGraphC.getMessage("WEBMODEL.GROUPTYPE.STANDARD"), //$NON-NLS-1$
							STTools.BLANK,
							STTools.BLANK,
							STTools.BLANK,
							STTools.BLANK });
					saveHelper(currentLang); // and then ensure that all languages have always the same number of groups and data are consistent
					STGraphExec graph = STGraph.getSTC().getCurrentGraph();
					int dataLen = graph.getWebModelGroupData(currentLang).length - 1;
					String[][] data;
					for(String lang : graph.getModelLanguages()) {
						if(!lang.equals(currentLang)) {
							data = graph.getWebModelGroupData(lang);
							String[][] newData = new String[dataLen + 1][5];
							for(int i = 0; i < dataLen; i++) {
								newData[i][0] = data[i][0];
								newData[i][1] = data[i][1];
								newData[i][2] = data[i][2];
								newData[i][3] = data[i][3];
								newData[i][4] = data[i][4];
							}
							newData[dataLen][0] = "0"; // the default group type //$NON-NLS-1$
							newData[dataLen][1] = STTools.BLANK;
							newData[dataLen][2] = STTools.BLANK;
							newData[dataLen][3] = STTools.BLANK;
							newData[dataLen][4] = STTools.BLANK;
							graph.setWebModelGroupData(newData, lang);
						}
					}
				}
			});

			delGroupButton = new JButton(STGraphC.getMessage("NODE.DIALOG.DEL")); //$NON-NLS-1$
			delGroupButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(groupDataModel.getRowCount() > 0) {
						groupDataModel.removeRow(groupDataModel.getRowCount() - 1);
						saveHelper(currentLang); // ensure that all languages have always the same number of groups
						STGraphExec graph = STGraph.getSTC().getCurrentGraph();
						String[][] data;
						for(String lang : graph.getModelLanguages()) {
							if(!lang.equals(currentLang)) {
								data = graph.getWebModelGroupData(lang);
								String[][] newData = new String[data.length - 1][5];
								for(int i = 0; i < data.length - 1; i++) {
									newData[i][0] = data[i][0];
									newData[i][1] = data[i][1];
									newData[i][2] = data[i][2];
									newData[i][3] = data[i][3];
									newData[i][4] = data[i][4];
								}
								graph.setWebModelGroupData(newData, lang);
							}
						}
					}
				}
			});

			JPanel buttPanel = new JPanel();
			buttPanel.add(newGroupButton);
			buttPanel.add(delGroupButton);
			groupPanel.add(BorderLayout.SOUTH, buttPanel);
		}
		return groupPanel;
	}


	/** Initialize buttonOk.
	 * @return button */
	private JButton getButtonOk() {
		if(buttonOk == null) {
			buttonOk = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			buttonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					STGraphExec graph = STGraph.getSTC().getCurrentGraph();
					if(!graph.isEditable) {
						setVisible(false);
						return;
					}
					saveHelper(currentLang);
					STGraph.getSTC().getCurrentDesktop().getGraphFrame1().graphChanged(null);
					graph.setAsModified(true);
					if(editDialogs != null) {
						for(String lang : graph.getModelLanguages()) { editDialogs.get(lang).setVisible(false); }
					}
					setVisible(false);
					STGraph.getSTC().refreshBars();
					STGraphC.setFocus();
				}
			});
		}
		return buttonOk;
	}


	/** Initialize buttonCancel.
	 * @return button */
	private JButton getButtonCancel() {
		if(buttonCancel == null) {
			buttonCancel = new JButton(STGraphC.getMessage("DIALOG.CANCEL"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-cancel.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			buttonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(editDialogs != null) {
						for(String lang : STGraph.getSTC().getCurrentGraph().getModelLanguages()) { editDialogs.get(lang).setVisible(false); }
					}
					setVisible(false);
					STGraphC.setFocus();
				}
			});
		}
		return buttonCancel;
	}


	/** Initialize buttonPanel.
	 * @return panel */
	private JPanel getButtonPanel() {
		if(buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getButtonOk(), null);
			buttonPanel.add(getButtonCancel(), null);
		}
		return buttonPanel;
	}


	/** Add group information for a new language, typically specified in the general configuration dialog for model.
	 * @param lang the language */
	public static void addGroupInfoForNewLanguage(final String lang) {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		String[][] groupData = graph.getWebModelGroupData(graph.getDefaultModelLanguage()); // use data of default language as blueprint
		int m = groupData == null ? 0 : groupData.length;
		String[][] groupNewData = new String[m][5];
		for(int i = 0; i < m; i++) { // for each group...
			groupNewData[i][0] = groupData[i][0];
			groupNewData[i][1] = STTools.BLANK;
			groupNewData[i][2] = STTools.BLANK;
			groupNewData[i][3] = STTools.BLANK;
			groupNewData[i][4] = STTools.BLANK;
		}
		graph.setWebModelGroupData(groupNewData, lang);
	}


	/** Helper for the fill method.
	 * @param lang the language */
	private void fillHelper(String lang) {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		modelDesc.setText(graph.getWebModelDescription(lang, true));
		timeUnit.setText(graph.getWebTimeUnit(lang, true));
		explTitle.setText(graph.getWebExplTitle(lang, true));
		explURL.setText(graph.getWebExplURL(lang, true));
		int size = groupDataModel.getRowCount();
		for(int i = 0; i < size; i++) { groupDataModel.removeRow(0); } // reset it
		String[][] groupData = graph.getWebModelGroupData(lang, true);
		if(groupData != null && groupData.length > 0) {
			for(int i = 0; i < groupData.length; i++) {
				groupDataModel.addRow(new Object[] { Integer.valueOf(i + 1),
						graph.getWebModelGroupTypes()[Integer.valueOf(groupData[i][0]).intValue()],
						groupData[i][1],
						groupData[i][2],
						groupData[i][3],
						groupData[i][4] });
			}
		}
	}


	/** Helper for the save (ok button and change language in combo) method.
	 * @param lang the language */
	private void saveHelper(String lang) {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		graph.setWebModelDescription(modelDesc.getText(), lang);
		graph.setWebTimeUnit(timeUnit.getText(), lang);
		graph.setWebExplTitle(explTitle.getText(), lang);
		graph.setWebExplURL(explURL.getText(), lang);
		int size = groupDataModel.getRowCount();
		if(size == 0) {
			graph.setWebModelGroupData(null, lang);
		} else {
			String[][] groupData = new String[size][5];
			for(int i = 0; i < size; i++) {
				groupData[i][0] = "" + graph.getWebModelGroupType((String)groupDataModel.getValueAt(i, 1)); //$NON-NLS-1$
				groupData[i][1] = (String)groupDataModel.getValueAt(i, 2); 
				groupData[i][2] = (String)groupDataModel.getValueAt(i, 3); 
				groupData[i][3] = (String)groupDataModel.getValueAt(i, 4); 
				groupData[i][4] = (String)groupDataModel.getValueAt(i, 5); 
			}
			graph.setWebModelGroupData(groupData, lang);
		}
	}


	/** Class for handling group interaction. */
	private class STTable extends JTable {

		public STTable(DefaultTableModel dataModel) { super(dataModel); }

		@Override
		public TableCellEditor getCellEditor(int row, int column) {
			if(column == 1) { return new DefaultCellEditor(new STComboBox(row, STGraph.getSTC().getCurrentGraph().getWebModelGroupTypes())); }
			Object[] imgNames = IconManager.getGroupImageNames().toArray();
			String[] names = new String[imgNames.length + 1];
			names[0] = STTools.BLANK;
			for(int i = 0; i < imgNames.length; i++) { names[i + 1] = (String)imgNames[i]; }
			if(column == 4) { return new DefaultCellEditor(new JComboBox(names)); }
			return super.getCellEditor(row, column);
		}

		@Override
		public boolean isCellEditable(int row, int column) { return column != 0; }

	}


	private class STComboBox extends JComboBox {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public STComboBox(final int row, final String[] data) {
			super(data);
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					STGraphExec graph = STGraph.getSTC().getCurrentGraph();
					String[][] data;
					for(String lang : graph.getModelLanguages()) {
						if(!lang.equals(currentLang)) {
							data = graph.getWebModelGroupData(lang);
							data[row][0] = "" + getSelectedIndex(); //$NON-NLS-1$
							graph.setWebModelGroupData(data, lang);
						}
					}
				}
			});
		}
	}


	/** Dialog for editing texts of the current model. */
	private class TextDialog extends JDialog {
		/** The language. */
		private String lang;
		/** The edit content pane. */
		private JPanel jEditContentPane;
		/** The edit scroll pane. */
		private JScrollPane editScrollPane;
		/** The edit table. */
		private STEditTable editTable;
		/** The edit data model. */
		private DefaultTableModel editDataModel;
		/** The edit button ok. */
		private JButton editButtonOk;
		/** The edit button cancel. */
		private JButton editButtonCancel;
		/** The edit button panel. */
		private JPanel editButtonPanel;

		/** Class constructor. */
		public TextDialog(String lang) {
			super();
			this.lang = lang;
			if(STGraphC.getContainer() instanceof JFrame) { setAlwaysOnTop(true); }
			setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
			setModal(true);
			setIconImage(STGraphC.getSystemIcon());
		}


		/** Initialize and open the dialog. */
		public void open() {
			setTitle(STGraphC.getMessage("WEBMODEL.EDITTEXTDIALOG.EDITTITLE") + STTools.COLON + STTools.SPACE + lang); //$NON-NLS-1$
			setContentPane(getJEditContentPane());
			fill();
			jEditContentPane.getRootPane().setDefaultButton(getEditButtonOk());
			pack();
			setLocation(STGraphC.getContainer().getX() + 150, STGraphC.getContainer().getY() + 150);
			setVisible(true);
		}


		/** Fill the fields in the dialog. */
		private void fill() {
			editFillHelper();
			TableColumnModel tcm = editTable.getColumnModel();
			tcm.getColumn(TABLE_NAME).setMinWidth(50); //name
			tcm.getColumn(TABLE_NAME).setPreferredWidth(50);
			tcm.getColumn(TABLE_ORDER).setPreferredWidth(50); //order
			tcm.getColumn(TABLE_ORDER).setMinWidth(50);
			tcm.getColumn(TABLE_ORDER).setMaxWidth(50);
			tcm.getColumn(TABLE_CNAME).setPreferredWidth(200); //cname
			tcm.getColumn(TABLE_UNIT).setPreferredWidth(150); //cname
			tcm.getColumn(TABLE_EDITDESC).setPreferredWidth(20); //... (button for edit description)
			tcm.getColumn(TABLE_EDITDESC).setMinWidth(20);
			tcm.getColumn(TABLE_EDITDESC).setMaxWidth(20);
			tcm.getColumn(TABLE_DESC).setPreferredWidth(500); //description
			tcm.getColumn(TABLE_INTEXT).setPreferredWidth(100); //input text //@@@//
			tcm.getColumn(TABLE_OUTTEXT).setPreferredWidth(100); //output text //@@@//
		}


		/** Initialize jEditContentPane.
		 * @return panel */
		private JPanel getJEditContentPane() {
			if(jEditContentPane == null) {
				jEditContentPane = new JPanel(new GridBagLayout());
				// table
				GridConstraints gbcTable = new GridConstraints(0, 0);
				gbcTable.fill = GridBagConstraints.BOTH;
				gbcTable.weightx = 1.0;
				gbcTable.weighty = 1.0;
				jEditContentPane.add(getEditScrollPane(), gbcTable);
				// buttons
				GridConstraints gbcButt = new GridConstraints(0, 1);
				gbcButt.fill = GridBagConstraints.HORIZONTAL;
				jEditContentPane.add(getEditButtonPanel(), gbcButt);
			}
			return jEditContentPane;
		}


		/** Initialize editScrollPane.
		 * @return pane */
		private JScrollPane getEditScrollPane() {
			if(editScrollPane == null) {
				editDataModel = new DefaultTableModel(new Object[] {
						STTools.BLANK,
						STGraphC.getMessage("WEBMODEL.EDITTEXT.ORDER"), //$NON-NLS-1$
						STGraphC.getMessage("WEBMODEL.EDITTEXT.NAME"), //$NON-NLS-1$
						STGraphC.getMessage("WEBMODEL.EDITTEXT.UNIT"), //$NON-NLS-1$
						STTools.ELLIPSIS,
						STGraphC.getMessage("WEBMODEL.EDITTEXT.DESCRIPTION"), //$NON-NLS-1$
						STGraphC.getMessage("WEBMODEL.EDITTEXT.INPUTTEXTS"), //$NON-NLS-1$
						STGraphC.getMessage("WEBMODEL.EDITTEXT.OUTPUTTEXTS") //$NON-NLS-1$
				}, 0);
				editTable = new STEditTable(editDataModel);
				editTable.setAutoCreateRowSorter(true);
				editScrollPane = new JScrollPane(editTable);
				editScrollPane.setMinimumSize(new Dimension(800, 300));
				editScrollPane.setPreferredSize(new Dimension(800, 300));
			}
			return editScrollPane;
		}


		/** Initialize editButtonOk.
		 * @return button */
		private JButton getEditButtonOk() {
			if(editButtonOk == null) {
				editButtonOk = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png"))); //$NON-NLS-1$ //$NON-NLS-2$
				editButtonOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						editSaveHelper();
						STGraph.getSTC().getCurrentDesktop().getGraphFrame1().graphChanged(null);
						STGraph.getSTC().getCurrentGraph().setAsModified(true);
						setVisible(false);
					}
				});
			}
			return editButtonOk;
		}


		/** Initialize editButtonCancel.
		 * @return button */
		private JButton getEditButtonCancel() {
			if(editButtonCancel == null) {
				editButtonCancel = new JButton(STGraphC.getMessage("DIALOG.CANCEL"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-cancel.png"))); //$NON-NLS-1$ //$NON-NLS-2$
				editButtonCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						//STGraphC.setFocus();
					}
				});
			}
			return editButtonCancel;
		}


		/** Initialize editButtonPanel.
		 * @return panel */
		private JPanel getEditButtonPanel() {
			if(editButtonPanel == null) {
				editButtonPanel = new JPanel();
				editButtonPanel.add(getEditButtonOk(), null);
				editButtonPanel.add(getEditButtonCancel(), null);
			}
			return editButtonPanel;
		}


		/** Helper for the fill method.
		 * @param lang the language */
		private void editFillHelper() {
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			int size = editDataModel.getRowCount();
			for(int i = 0; i < size; i++) { editDataModel.removeRow(0); }
			ArrayList<STNode> nodes = graph.getSortedNodesForWeb();
			if(nodes.size() == 0) { return; }
			for(STNode node : nodes) {
				editDataModel.addRow(new Object[] {
						node.getName(),
						node.getCProperty("Order"), //$NON-NLS-1$
						node.getCName(lang, true),
						node.getUnit(lang, true),
						STTools.ELLIPSIS,
						node.getDescription(lang, true),
						node.getInputTexts(lang, true),
						node.getOutputTexts(lang, true)
				});
			}
		}


		/** Helper for the save (ok button) method.
		 * @param lang the language */
		private void editSaveHelper() {
			int size = editDataModel.getRowCount();
			if(size == 0) { return; }
			STGraphExec graph = STGraph.getSTC().getCurrentGraph();
			for(int i = 0; i < size; i++) {
				STNode node = graph.getNodeByName((String)editDataModel.getValueAt(i, TABLE_NAME));
				node.setCProperty("Order", (String)editDataModel.getValueAt(i, TABLE_ORDER)); //$NON-NLS-1$
				node.setCName((String)editDataModel.getValueAt(i, TABLE_CNAME), lang);
				node.setUnit((String)editDataModel.getValueAt(i, TABLE_UNIT), lang);
				node.setDescription((String)editDataModel.getValueAt(i, TABLE_DESC), lang);
				node.setInputTexts((String)editDataModel.getValueAt(i, TABLE_INTEXT), lang);
				node.setOutputTexts((String)editDataModel.getValueAt(i, TABLE_OUTTEXT), lang);
			}
		}

	}


	/** Class for handling text interaction. */
	private class STEditTable extends JTable {

		public STEditTable(DefaultTableModel dataModel) {
			super(dataModel);
			TableColumn tc = getColumn(STTools.ELLIPSIS);
			tc.setCellRenderer(new STButtonRenderer());
			tc.setCellEditor(new STButtonEditor(dataModel, new JCheckBox()));
		}

		@Override
		public boolean isCellEditable(int row, int column) { return column != TABLE_NAME; }

	}


	private class STButtonRenderer extends JButton implements TableCellRenderer {

		public STButtonRenderer() { setOpaque(true); }

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if(isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background")); //$NON-NLS-1$
			}
			setText((value == null) ? STTools.BLANK : value.toString());
			return this;
		}
	}


	class STButtonEditor extends DefaultCellEditor {
		protected JButton button;
		private String label;
		private boolean isPushed;
		private int row;
		private DefaultTableModel dataModel;

		public STButtonEditor(DefaultTableModel dataModel, JCheckBox checkBox) {
			super(checkBox);
			this.dataModel = dataModel;
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { fireEditingStopped(); }
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if(isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			button.setText(label = (value == null) ? STTools.BLANK : value.toString());
			isPushed = true;
			this.row = row;
			return button;
		}

		@Override
		public Object getCellEditorValue() {
			if(isPushed) {
				if(editDescDialog == null) { editDescDialog = new DescDialog(dataModel); }
				editDescDialog.open(row, (String)dataModel.getValueAt(row, 0), (String)dataModel.getValueAt(row, TABLE_DESC));
			}
			isPushed = false;
			return new String(label);
		}

		@Override
		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		@Override
		protected void fireEditingStopped() { super.fireEditingStopped(); }

	}


	/** Dialog for editing node descriptions of the current model. */
	private class DescDialog extends JDialog {
		private DefaultTableModel dataModel;
		/** The edit desc content pane. */
		private JPanel jEditDescContentPane;
		/** The scroll doc. */
		private JScrollPane scrollDoc;
		/** The area doc. */
		private STTextArea areaDoc;
		/** The edit desc button ok. */
		private JButton editDescButtonOk;
		/** The edit desc button apply. */
		private JButton editDescButtonApply;
		/** The edit desc button cancel. */
		private JButton editDescButtonCancel;
		/** The edit desc button panel. */
		private JPanel editDescButtonPanel;
		/** The table row of the currently edited node. */
		private int tableRow;
		/** The name of the currently edited node. */
		private String nodeName;


		/** Class constructor. */
		public DescDialog(DefaultTableModel dataModel) {
			super();
			this.dataModel = dataModel;
			if(STGraphC.getContainer() instanceof JFrame) { setAlwaysOnTop(true); }
			setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
			setModal(true);
			setIconImage(STGraphC.getSystemIcon());
		}


		/** Initialize, open, and fill the dialog. */
		public void open(int row, String name, String desc) {
			setTitle(STGraphC.getMessage("WEBMODEL.EDITDESCDIALOG.EDITTITLE")); //$NON-NLS-1$
			setContentPane(getJEditDescContentPane());
			tableRow = row;
			nodeName = name;
			areaDoc.setText(desc);
			jEditDescContentPane.getRootPane().setDefaultButton(getEditDescButtonOk());
			pack();
			if(!isShowing()) { setLocation(STGraphC.getContainer().getX() + 200, STGraphC.getContainer().getY() + 200); }
			setVisible(true);
		}


		/** Initialize jEditDescContentPane.
		 * @return panel */
		private JPanel getJEditDescContentPane() {
			if(jEditDescContentPane == null) {
				jEditDescContentPane = new JPanel(new GridBagLayout());
				// description
				GridConstraints gbcAreaDoc = new GridConstraints(0, 0);
				gbcAreaDoc.weightx = 1.0; gbcAreaDoc.weighty = 1.0;
				gbcAreaDoc.fill = GridBagConstraints.BOTH;
				jEditDescContentPane.add(getScrollDoc(), gbcAreaDoc);
				// buttons
				GridConstraints gbcButt = new GridConstraints(0, 1);
				gbcButt.fill = GridBagConstraints.HORIZONTAL;
				jEditDescContentPane.add(getEditDescButtonPanel(), gbcButt);
			}
			return jEditDescContentPane;
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
				areaDoc.setPreferredSize(new Dimension(500, 400));
				areaDoc.setToolTipText(STGraphC.getMessage("NODE.DIALOG.DOC.TT")); //$NON-NLS-1$
			}
			return areaDoc;
		}


		/** Initialize editDescButtonOk.
		 * @return button */
		private JButton getEditDescButtonOk() {
			if(editDescButtonOk == null) {
				editDescButtonOk = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png"))); //$NON-NLS-1$ //$NON-NLS-2$
				editDescButtonOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dataModel.setValueAt(areaDoc.getText(), tableRow, TABLE_DESC);
						STNode node = STGraph.getSTC().getCurrentGraph().getNodeByName(nodeName);
						node.setDescription(areaDoc.getText(), currentLang);
						STGraph.getSTC().getCurrentDesktop().getGraphFrame1().graphChanged(null);
						STGraph.getSTC().getCurrentGraph().setAsModified(true);
						editDescDialog.setVisible(false);
					}
				});
			}
			return editDescButtonOk;
		}


		/** Initialize editDescButtonApply.
		 * @return button */
		private JButton getEditDescButtonApply() {
			if(editDescButtonApply == null) {
				editDescButtonApply = new JButton(STGraphC.getMessage("DIALOG.APPLY"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok-apply.png"))); //$NON-NLS-1$ //$NON-NLS-2$
				editDescButtonApply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dataModel.setValueAt(areaDoc.getText(), tableRow, TABLE_DESC);
						STNode node = STGraph.getSTC().getCurrentGraph().getNodeByName(nodeName);
						node.setDescription(areaDoc.getText(), currentLang);
						STGraph.getSTC().getCurrentDesktop().getGraphFrame1().graphChanged(null);
						STGraph.getSTC().getCurrentGraph().setAsModified(true);
					}
				});
			}
			return editDescButtonApply;
		}


		/** Initialize editDescButtonCancel.
		 * @return button */
		private JButton getEditDescButtonCancel() {
			if(editDescButtonCancel == null) {
				editDescButtonCancel = new JButton(STGraphC.getMessage("DIALOG.CANCEL"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-cancel.png"))); //$NON-NLS-1$ //$NON-NLS-2$
				editDescButtonCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						editDescDialog.setVisible(false);
						STGraphC.setFocus();
					}
				});
			}
			return editDescButtonCancel;
		}


		/** Initialize editDescButtonPanel.
		 * @return panel */
		private JPanel getEditDescButtonPanel() {
			if(editDescButtonPanel == null) {
				editDescButtonPanel = new JPanel();
				editDescButtonPanel.add(getEditDescButtonOk(), null);
				editDescButtonPanel.add(getEditDescButtonApply(), null);
				editDescButtonPanel.add(getEditDescButtonCancel(), null);
			}
			return editDescButtonPanel;
		}

	}

}
