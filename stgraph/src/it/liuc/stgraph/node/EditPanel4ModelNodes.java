/*******************************************************************************
 * This file is part of STGraph, Copyright 2004-2021, Luca Mari.
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

import it.liuc.stgraph.STDesktop;
import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.STGraphExec;
import it.liuc.stgraph.STInterpreter;
import it.liuc.stgraph.util.EditDialog;
import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STFileFilter3;
import it.liuc.stgraph.util.STTextField;
import it.liuc.stgraph.util.STTools;
import it.liuc.stgraph.util.TokenAwareEditor;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/** Panel for the configuration of the submodel nodes. */
@SuppressWarnings({"serial", "rawtypes"})
public class EditPanel4ModelNodes extends EditPanel4Nodes {
	/** The model node. */
	private static ModelNode modelNode;

	/** The contentPanel1. */
	private static JPanel contentPanel1 = null;
	/** The scroll file list. */
	private static JScrollPane scrollFileList = null;
	/** The list file list. */
	private static JList listFileList = null;
	/** The text filename. */
	private static STTextField textFilename = null;
	/** The scroll name help. */
	private static JScrollPane scrollNameHelp = null;
	/** The help name text. */
	private static JEditorPane helpNameText = null;
	/** The button open sub system. */
	private static JButton buttonOpenSubSystem = null;
	/** The check subVisible. */
	private static JCheckBox checkSubVisible = null;
	/** The name panel. */
	private static JPanel namePanel;
	/** The input scroll. */
	private static JScrollPane inputScroll = null;
	/** The input panel. */
	private static JPanel inputPanel = null;
	/** The text inputs. */
	private static ArrayList<TokenAwareEditor> textInputs = null;
	/** The number of inputs. */
	private int numInputs;

	/** The active tab. */
	protected static int activeTab = 1;


	/** Class constructor. */
	public EditPanel4ModelNodes() { super(); }


	/** Get the initial panel for choosing the subsystem.
	 * @return panel */
	private JPanel getNamePanel() {
		if(namePanel == null) {
			namePanel = new JPanel();
			namePanel.setLayout(new GridBagLayout());
			// file list
			namePanel.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.FILELIST") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcScrollFileList = new GridConstraints(1, 0);
			gbcScrollFileList.weightx = 1.0; gbcScrollFileList.weighty = 1.0;
			gbcScrollFileList.fill = GridBagConstraints.BOTH;
			namePanel.add(getScrollFileList(), gbcScrollFileList);
			// filename
			namePanel.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.SUBSYSTEMFILE") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcFileName2 = new GridConstraints(1, 1);
			gbcFileName2.fill = GridBagConstraints.HORIZONTAL;
			gbcFileName2.weightx = 1.0;
			namePanel.add(getTextFilename(), gbcFileName2);
			//
			namePanel.add(getButtonOpenSubSystem(), new GridConstraints(2, 1, GridBagConstraints.WEST));
			// sub visible
			GridConstraints gbcSubVisible = new GridConstraints(0, 2, GridBagConstraints.EAST);
			gbcSubVisible.gridwidth = 2;
			namePanel.add(getCheckSubVisible(), gbcSubVisible);
			// helpText
			GridConstraints gbcHelp = new GridConstraints(0, 9);
			gbcHelp.fill = GridBagConstraints.HORIZONTAL;
			gbcHelp.gridwidth = 3;
			namePanel.add(getScrollNameHelp(), gbcHelp);
		}
		return namePanel;
	}


	/** Get the first panel.
	 * @return panel */
	protected final JPanel getContentPanel1() {
		if(contentPanel1 == null) {
			contentPanel1 = new JPanel();
			contentPanel1.setLayout(new GridBagLayout());
			// name
			contentPanel1.add(new JLabel(STGraphC.getMessage("NODE.DIALOG.VARNAME") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			//
			GridConstraints gbcName2 = new GridConstraints(1, 0);
			gbcName2.fill = GridBagConstraints.HORIZONTAL;
			gbcName2.weightx = 1.0;
			contentPanel1.add(getNodeNameField(), gbcName2);
			// input panel
			GridConstraints gbcPanel = new GridConstraints(0, 3, GridBagConstraints.NORTHWEST);
			gbcPanel.gridwidth = 2;
			gbcPanel.fill = GridBagConstraints.BOTH;
			gbcPanel.weightx = 1.0; gbcPanel.weighty = 1.0;
			contentPanel1.add(getInputScroll(), gbcPanel);
			// variables and functions
			GridConstraints gbcVar = new GridConstraints(0, 6, GridBagConstraints.NORTHWEST);
			gbcVar.gridwidth = 2;
			gbcVar.fill = GridBagConstraints.BOTH;
			gbcVar.weightx = 1.0; gbcVar.weighty = 1.0;
			contentPanel1.add(getVarAndFunPanel(), gbcVar);
			// helpText
			GridConstraints gbcHelp = new GridConstraints(0, 7);
			gbcHelp.fill = GridBagConstraints.HORIZONTAL;
			gbcHelp.gridwidth = 2;
			contentPanel1.add(getScrollHelp(), gbcHelp);
		}
		return contentPanel1;
	}


	/** Get the scroll panel for handling the file list.
	 * @return pane */
	private JScrollPane getScrollFileList() {
		if(scrollFileList == null) {
			scrollFileList = new JScrollPane();
			scrollFileList.setViewportView(getListFileList());
		}
		return scrollFileList;
	}


	/** Get the listFileList.
	 * @return list */
	private JList getListFileList() {
		if(listFileList == null) {
			listFileList = new JList();
			listFileList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			listFileList.addMouseListener(new MouseAdapter() { 
				public void mouseClicked(final MouseEvent e) {
					Object t = listFileList.getSelectedValue();
					if(t != null) { getTextFilename().setText(t.toString()); }
					if(e.getClickCount() == 2) { getButtonOpenSubSystem().doClick(); }
				}
			});
			listFileList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(final ListSelectionEvent e)  {
					Object t = listFileList.getSelectedValue();
					if(t != null) { getTextFilename().setText(t.toString()); }
				}
			});
		}
		return listFileList;
	}


	/** Get the textFilename.
	 * @return text */
	private STTextField getTextFilename() {
		if(textFilename == null) { textFilename = new STTextField(this, true); }
		return textFilename;
	}


	/** Set the error message to be dispayed by this dialog.
	 * @param message the message */
	private void setErrorNameMessage(final String message) {
		helpNameText.setText("<html><center><font color='red' size='3'>" + STTools.replaceCRwithBR(message) + "</font></center></html>"); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/** Get the buttonOpenSubSystem.
	 * @return button */
	private JButton getButtonOpenSubSystem() {
		if(buttonOpenSubSystem == null) {
			buttonOpenSubSystem = new JButton(STGraphC.getMessage("NODE.DIALOG.OPENSYSTEM")); // Open system //$NON-NLS-1$
			buttonOpenSubSystem.addActionListener(new ActionListener() { 
				public void actionPerformed(final ActionEvent e) {
					setErrorNameMessage(STTools.BLANK);
					STGraphC stc = STGraph.getSTC();
					String s0 = getTextFilename().getText().trim();
					if(s0.equals(STTools.BLANK) || s0.equals(modelNode.getSubmodelName())) { return; } // the subsystem name is null or is the previous one: nothing to do
					if(stc.getCurrentGraph().getFileName() == null) { setErrorNameMessage(STGraphC.getMessage("ERR_WRONG_SUBSYSTEM_CONTEXT")); return; } //$NON-NLS-1$
					String fileName = stc.getCurrentGraph().contextName + System.getProperty("file.separator") + s0; //$NON-NLS-1$
					if(!(new File(fileName)).canRead()) { setErrorNameMessage(STGraphC.getMessage("ERR_WRONG_SYSTEM_FILE")); return; } //$NON-NLS-1$

					stc.isSubLoading = true;

					STDesktop tempDesktop = stc.getCurrentDesktop();
					STGraphExec tempGraph = stc.getCurrentGraph();

					String name = STTools.stripPathFromFileName(stc.getCurrentGraph().getFileName());
					stc.isLoading = true;
					STDesktop newDesktop = new STDesktop(stc.getInputStream(fileName, tempGraph.resource), fileName, tempGraph.resource, tempGraph, modelNode);
					stc.isLoading = false;
					STGraphExec topGraph = newDesktop.getGraph();
					topGraph.setupModel();
					if(topGraph.modelNodeGlobalList != null) {
						for(ModelNode n : topGraph.modelNodeGlobalList) {
							if(name.equals(STTools.stripPathFromFileName(n.getSubmodelFilename()))) {
								setErrorNameMessage(STGraphC.getMessage("ERR_WRONG_SYSTEM_FILE2")); //$NON-NLS-1$
								stc.setCurrentDesktop(tempDesktop);
								stc.setCurrentGraph(tempGraph);
								stc.isSubLoading = false;
								return;
							}
						}
					}

					modelNode.setSubmodelName(s0);
					if(modelNode.getDesk() == null) { // first assignment
						stc.setCurrentDesktop(newDesktop);
						modelNode.setDesk(newDesktop);
						topGraph = (STGraphExec)newDesktop.getGraph().getTopGraph();
						topGraph.setupModel();
						modelNode.addVars(newDesktop.getGraph());
					} else {
						stc.setCurrentDesktop(modelNode.getDesk());
						modelNode.setSuperExpressions(null);
						if(!fileName.equals(stc.getCurrentGraph().getFileName())) { // re-assignment
							modelNode.removeVars(stc.getCurrentDesktop().getGraph());
							stc.getCurrentDesktop().close();
							stc.setCurrentDesktop(newDesktop);
							modelNode.setDesk(newDesktop);
							modelNode.addVars(newDesktop.getGraph());
						}
					}
					stc.setCurrentDesktop(tempDesktop);
					stc.setCurrentGraph(tempGraph);
					stc.refreshBars();
					fill();
					stc.isSubLoading = false;
				}
			});
		}
		return buttonOpenSubSystem;
	}


	/** Get the scrollNameHelp.
	 * @return pane */
	private JScrollPane getScrollNameHelp() {
		if(scrollNameHelp == null) {
			scrollNameHelp = new JScrollPane();
			scrollNameHelp.setPreferredSize(new Dimension(100, 50));
			scrollNameHelp.setMinimumSize(new Dimension(100, 50));
			scrollNameHelp.setViewportView(getHelpNameText());
		}
		return scrollNameHelp;
	}


	/** Get the helpNameText.
	 * @return editor */
	private JEditorPane getHelpNameText() {
		if(helpNameText == null) {
			helpNameText = new JEditorPane();
			helpNameText.setContentType("text/html"); //$NON-NLS-1$
			helpNameText.setEditable(false);
			helpNameText.setBackground(getNamePanel().getBackground());
		}
		return helpNameText;
	}


	/** Set the text describing the currently selected input variable.
	 * @param caller the caller */
	void setHelpText2(final JList caller) {
		Object value = caller.getSelectedValue();
		if(value == null) { return; }
		String s1 = value.toString();
		STNode n = modelNode.getSubmodel().getNodeByName(s1);
		if(n == null) { return; }
		String s2 = n.getDescription();
		if(s2 == null) { s2 = STTools.BLANK; }
		helpText.setMessage(s2);
	}


	/** Get the subVisible.
	 * @return checkbox */
	private JCheckBox getCheckSubVisible() {
		if(checkSubVisible == null) {
			checkSubVisible = new JCheckBox(STGraphC.getMessage("NODE.DIALOG.SUBVISIBLE") + STTools.QUESTION); //$NON-NLS-1$
			checkSubVisible.addKeyListener(new KeyAdapter() { public void keyReleased(final KeyEvent e) { if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; } } });
		}
		return checkSubVisible;
	}


	/** Get the inputScroll.
	 * @return scroll */
	private JScrollPane getInputScroll() {
		if(inputScroll == null) {
			inputScroll = new JScrollPane();
			inputScroll.setViewportView(inputPanel);
		}
		return inputScroll;
	}


	/** Get the inputTexts.
	 * @return editor */
	private ArrayList<TokenAwareEditor> getInputTexts() {
		textInputs = new ArrayList<TokenAwareEditor>(numInputs);
		STInterpreter interpreter = modelNode.getGraph().getInterpreter();
		TokenAwareEditor tae;
		for(int i = 0; i < numInputs; i++) {
			textInputs.add(tae = new TokenAwareEditor(interpreter, getHelpText()));
			tae.addFocusListener(new MyFocusAdapter(tae));
			tae.addKeyListener(new MyKeyAdapter(tae));
			tae.addMouseListener(new MyMouseAdapter(tae));
			tae.setParams(modelNode, STNode.PROP_EXPRESSION);
		}
		return textInputs;
	}


	/** Local mouse adapter. */
	private class MyMouseAdapter extends MouseAdapter {
		TokenAwareEditor o;
		MyMouseAdapter(final TokenAwareEditor o) { this.o = o; }

		public void mouseClicked(final MouseEvent e) {
			if(o.getCurrentToken() != null) { 
				String s = STInterpreter.getFunctionDescription(o.getCurrentToken());
				if(s != null) { helpText.setMessage(s); }
			} else {
				helpText.setText(STTools.BLANK);
			}
		}
	}


	/** Local focus adapter. */
	private class MyFocusAdapter extends FocusAdapter {
		TokenAwareEditor o;
		MyFocusAdapter(final TokenAwareEditor o) { this.o = o; }

		public void focusGained(final FocusEvent e) {
			currentField = o;
			EditDialog.setPreviousContent(o.getText());
		}
	}


	/** Local key adapter. */
	private class MyKeyAdapter extends KeyAdapter {
		TokenAwareEditor o;
		MyKeyAdapter(final TokenAwareEditor o) { this.o = o; }

		public void keyReleased(final KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_TAB) {
				if(!e.isShiftDown()) { o.transferFocus(); }
				else { o.transferFocusBackward(); }
			}
			else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; }
			else if(e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) { getButtonOk().doClick(); return; }
			else if(e.getKeyCode() == 17) { ; }
			else if(e.getKeyCode() == KeyEvent.VK_E && e.isControlDown()) {
				helpText.setText((String)o.onTheFlyEvaluate(modelNode.getGraph().getInterpreter(), true));
			} else {
				if(o.getCurrentToken() != null) {
					String s = STInterpreter.getFunctionDescription(o.getCurrentToken());
					if(s != null) { helpText.setMessage(s); }
				} else {
					helpText.setText(STTools.BLANK);
				}
				if(!(o.getText().equals(EditDialog.getPreviousContent()))) { setDirty(true); }
			}
		}
	}


	/** Local mouse adapter. */
	private class MyMouseAdapter2 extends MouseAdapter {
		String s;
		MyMouseAdapter2(final String s) { this.s = s; }

		public void mouseEntered(final MouseEvent e) { helpText.setMessage(s); }

		public void mouseExited(final MouseEvent e) { helpText.setText("&nbsp;"); } //$NON-NLS-1$
	}


	//******************************* dynamic handling *************************************


	/** Open the dialog, that had been created in a static way.
	 * @param node the node */
	public final void open(final STNode thenode) {
		node = modelNode = (ModelNode)thenode;
		startInit();
		numberFormatLabel.setVisible(false);
		getNumberFormatField().setVisible(false);
		getHelpNameText().setText("&nbsp;"); //$NON-NLS-1$
		boolean b = modelNode.getGraph().isEditable;
		getNodeNameField().setEnabled(b); getTextFilename().setEnabled(b);
		getListFileList().setEnabled(b);
		getButtonOpenSubSystem().setEnabled(b);
		getCheckSubVisible().setEnabled(true); // leave it always enabled, so to allow displaying the submodel...
		fill();
		boolean isFromLibrary = node.getNodeSubtype() != null;
		completeInit((!isFromLibrary && getTextFilename().getText().length() > 0) ? activeTab : 0);
	}


	/** Fill the dialog with the configuration data taken from the node properties. */
	@SuppressWarnings("unchecked")
	public final void fill() {
		super.fill();

		if((getTabbedPanel().getTitleAt(getTabbedPanel().getTabCount() - 1)).equals(STGraphC.getMessage("NODE.DIALOG.TAB4"))) { //$NON-NLS-1$
			getTabbedPanel().removeTabAt(getTabbedPanel().getTabCount() - 1);
		}

		boolean isFromLibrary = node.getNodeSubtype() != null;
		if(isFromLibrary) {
			String t = getTabbedPanel().getTitleAt(0);
			if(t != null && t.equals(STGraphC.getMessage("NODE.DIALOG.TABSYS"))) { //$NON-NLS-1$
				getTabbedPanel().removeTabAt(0);
			}
		} else {
			getTabbedPanel().add(getNamePanel(), 0);
			getTabbedPanel().setTitleAt(0, STGraphC.getMessage("NODE.DIALOG.TABSYS")); //$NON-NLS-1$

			String curDir = modelNode.getGraph().contextName;
			if(curDir == null) {
				setErrorNameMessage(STGraphC.getMessage("ERR_WRONG_SUBSYSTEM_CONTEXT")); //$NON-NLS-1$
				return;
			}
			DefaultListModel listNamesModel = new DefaultListModel();
			listNamesModel.clear();

			//*** 2024 fix: to open the edit window of submodels of sample models
			File[] files = (new File(curDir)).listFiles(new STFileFilter3());
			if(files == null) {
				files = new File(getClass().getClassLoader().getResource(curDir).getPath()).listFiles();
			}
		
			if(files.length > 0) {
				Arrays.sort(files);
				String fileName;
				String graphName = modelNode.getGraph().getFileName();
				graphName = graphName.substring(1 + graphName.lastIndexOf(STGraphC.isApplet() ? "/" : System.getProperty("file.separator"))); //$NON-NLS-1$ //$NON-NLS-2$
				for(int i = 0; i < files.length; i++) {
					fileName = files[i].getName();
					if(!fileName.equals(graphName)) { listNamesModel.addElement(fileName); }
				}
			}
			getListFileList().setModel(listNamesModel);

			getCheckSubVisible().setSelected(modelNode.isSubVisible());

			String modelName = modelNode.getSubmodelName();
			if(STTools.isEmpty(modelName)) {
				getTextFilename().setText(STTools.BLANK);
				return;
			}

			getTextFilename().setText(modelName);
		}

		inputPanel = new JPanel(); // recreate it to force its reorganization...
		inputPanel.setLayout(new GridBagLayout());
		STGraphExec subGraph = modelNode.getSubmodel();
		if(subGraph != null) {
			ArrayList<ValueNode> inputNodes = new ArrayList<ValueNode>();
			for(ValueNode iNode : subGraph.getInputNodeList()) {
				if(!iNode.getName().startsWith(STTools.UNDERSCORE)) { inputNodes.add(iNode); }
			}
			numInputs = inputNodes.size();
			if(numInputs == 0) {
				getInputScroll().setViewportView(inputPanel); // force to cleanup
			} else {
				textInputs = getInputTexts();
				GridConstraints[] gbc = new GridConstraints[numInputs];
				GridConstraints[] gbc2 = new GridConstraints[numInputs];
				boolean b = modelNode.getGraph().isEditable;
				String name;
				String cname;
				String desc;
				String text;
				for(int i = 0; i < numInputs; i++) {
					JLabel labInput = new JLabel();
					name = inputNodes.get(i).getName();
					cname = inputNodes.get(i).getCName();
					desc = inputNodes.get(i).getDescription();
					if((STTools.isEmpty(cname) || cname.equals(name)) && STTools.isEmpty(desc)) {
						labInput.setText(name + STTools.COLON);
					} else {
						labInput.setText("<html><u><font color='blue'>" + name + STTools.COLON + "</font></u>");  //$NON-NLS-1$//$NON-NLS-2$
						labInput.setCursor(new Cursor(Cursor.HAND_CURSOR));
						if(STTools.isEmpty(cname)) {
							text = desc;
						} else if(STTools.isEmpty(desc)) {
							text = "<b>" + cname + "</b>"; //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							text = "<b>" + cname + "</b><br>" + desc; //$NON-NLS-1$ //$NON-NLS-2$
						}
						labInput.addMouseListener(new MyMouseAdapter2(text));
					}
					gbc[i] = new GridConstraints(0, i);
					gbc[i].anchor = GridBagConstraints.NORTHEAST;
					inputPanel.add(labInput, gbc[i]);
					gbc2[i] = new GridConstraints(1, i);
					gbc2[i].fill = GridBagConstraints.HORIZONTAL;
					gbc2[i].weightx = 1.0;
					inputPanel.add(textInputs.get(i), gbc2[i]);
					textInputs.get(i).setEnabled(b);
				}

				String[] a1 = modelNode.getSuperExpressions();
				if(a1 != null && a1.length > 0) {
					STInterpreter interpreter = modelNode.getGraph().getInterpreter();
					String[] a2 = modelNode.getSubVars();
					for(int i = 0; i < a1.length; i++) {
						name = a2[i];
						for(int j = 0; j < numInputs; j++) {
							if(inputNodes.get(j).getName().equals(name)) {
								textInputs.get(j).setHTMLText(interpreter, a1[i].trim());
								//helpText.setText((String)textInputs.get(j).onTheFlyEvaluate(interpreter, true));
								break;
							}
						}
					}
				}
				currentField = textInputs.get(0);
				getInputScroll().setViewportView(inputPanel);
			}
		}
		setVarsInList();
	}


	/** Action for the OK button.
	 * @param mode the mode, either <code>MODE_OK</code> or <code>MODE_APPLY</code> */
	@Override
	public final void okHandler(final int mode) {
		helpText.setErrorMessage(null);

		modelNode.setSubVisible(checkSubVisible.isSelected());

		if(!modelNode.getGraph().isEditable) {
			setVisible(open = false);
			STGraphC.setFocus();
			return;
		}
		String t1 = getNodeNameField().getText().trim();
		String t1b = modelNode.getName();
		String result = null;
		if(!t1b.equals(t1)) {
			if((result = modelNode.checkName(t1)) != null) { helpText.setErrorMessage(result); return; }
			modelNode.redefineVars(t1b, t1);
			modelNode.setName(t1);
			modelNode.handleNodeRenaming(t1b);
		} else {
			modelNode.addVars(modelNode.getSubmodel());
		}

		modelNode.setValid(true);

		modelNode.setSuperExpressions(null);
		modelNode.setSubVars(null);
		STGraphExec subGraph = modelNode.getSubmodel();
		if(subGraph != null) {
			ArrayList<ValueNode> inputNodes = new ArrayList<ValueNode>();
			for(ValueNode iNode : subGraph.getInputNodeList()) {
				if(!iNode.getName().startsWith(STTools.UNDERSCORE)) { inputNodes.add(iNode); }
			}
			numInputs = inputNodes.size();

			if(numInputs > 0) {
				ArrayList<String> superAl = new ArrayList<String>();
				ArrayList<String> subAl = new ArrayList<String>();
				String t;
				for(int i = 0; i < numInputs; i++) {
					inputNodes.get(i).setSaturatingExpression(null);
					t = textInputs.get(i).getPlainText();
					if(!STTools.isEmpty(t)) {
						if((result = inputNodes.get(i).checkExpressionDefinition(modelNode.getGraph().getInterpreter(), t, STNode.PROP_EXPRESSION)) != null) {
							helpText.setErrorMessage(result);
							modelNode.setValid(false);
						} else {
							superAl.add(modelNode.cleanExpression(t));
							subAl.add(inputNodes.get(i).getName());
						}
					}
				}
				if(!modelNode.setProps(subGraph, superAl.toArray(new String[superAl.size()]), subAl.toArray(new String[subAl.size()]))) { return; }
			}            		
		}		
		completeExit(mode);
	}

}
