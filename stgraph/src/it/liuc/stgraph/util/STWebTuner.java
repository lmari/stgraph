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
package it.liuc.stgraph.util;

import it.liuc.stgraph.STGraph;
import it.liuc.stgraph.STGraphC;
import it.liuc.stgraph.node.STNode;
import it.liuc.stgraph.node.ValueNode;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.nfunk.jep.type.Matrix;
import org.nfunk.jep.type.Tensor;

import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


/** Class gathering some tools to support the tuning of models based on the STGraphWeb protocol. */
public class STWebTuner extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** User input (vector: one element per team). */
	public final static int USR_IN = 3;
	/** Exogeneous input (scalar: the same value independently of the number of teams). */
	public final static int EXO_IN = 1;
	/** Exogeneous input (scalar: base value multiplied by the number of teams). */
	public final static int EXO_MULT_IN = 2;
	/** Initial state input (vector: one element per team). */
	public final static int ST0_IN = 0;
	/** General parameters. */
	public final static int PAR_IN = 6;

	/** User target (vector: one element per team). */
	public final static int USR_TGT = 6;

	/** User output (vector: one element per team). */
	public final static int USR_OUT = 2;
	/** User optional output (vector: one element per team). */
	public final static int USR_OPT_OUT = 3;
	/** User decision (vector: one element per team). */
	public final static int USR_DEC_OUT = 1;
	/** General parameters (scalar). */
	public final static int PAR_OUT = 4;
	/** General optional parameters (scalar). */
	public final static int PAR_OPT_OUT = 5;

	public final static String HTML = "<html>"; //$NON-NLS-1$

	/** The content pane. */
	private JPanel jContentPane = null;
	/** The initial time. */
	private STTextField fileName;
	/** The number of teams. */
	private STTextField teamNum;
	/** The number of steps. */
	private STTextField stepNum;
	/** The button create file. */
	private JButton buttonCreateFile;
	/** The button link file. */
	private JButton buttonLinkFile;
	/** The button unlink file. */
	private JButton buttonUnlinkFile;
	/** The button ok. */
	private JButton buttonOk;


	/** Class constructor. */
	public STWebTuner() {
		super();
		setTitle(STGraphC.getMessage("WEBTUNER.DIALOG.TITLE")); //$NON-NLS-1$
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setModal(true);
		open();
	}


	/** Initialize and open the dialog. */
	private void open() {
		setContentPane(getJContentPane());
		pack();
		setLocation(STGraph.getSTC().getX() + 50, STGraph.getSTC().getY() + 50);
		setVisible(true);
	}


	/** Initialize jContentPane.
	 * @return panel */
	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel(new GridBagLayout());
			// xls file name
			jContentPane.add(new JLabel(STGraphC.getMessage("WEBTUNER.DIALOG.FILENAME") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			//
			jContentPane.add(getFileName(), new GridConstraints(1, 0, GridBagConstraints.WEST));
			// number of teams
			jContentPane.add(new JLabel(STGraphC.getMessage("WEBTUNER.DIALOG.TEAMNUM") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.EAST));
			//
			jContentPane.add(getTeamNum(), new GridConstraints(1, 1, GridBagConstraints.WEST));
			// number of steps
			jContentPane.add(new JLabel(STGraphC.getMessage("WEBTUNER.DIALOG.STEPNUM") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 2, GridBagConstraints.EAST));
			//
			jContentPane.add(getStepNum(), new GridConstraints(1, 2, GridBagConstraints.WEST));

			JPanel buttonPane = new JPanel(new GridBagLayout());
			buttonPane.add(getButtonCreateFile(), new GridConstraints(0, 0));
			buttonPane.add(getButtonLinkFile(), new GridConstraints(1, 0));
			buttonPane.add(getButtonUnlinkFile(), new GridConstraints(2, 0));
			buttonPane.add(getButtonOk(), new GridConstraints(3, 0));
			GridConstraints gc = new GridConstraints(0, 3, GridBagConstraints.CENTER);
			gc.gridwidth = 2;
			jContentPane.add(buttonPane, gc);
		}
		return jContentPane;
	}


	/** Initialize fileName.
	 * @return text */
	private STTextField getFileName() {
		if(fileName == null) {
			fileName = new STTextField(this, false);
			fileName.setToolTipText(STGraphC.getMessage("WEBTUNER.DIALOG.FILENAME.TT")); //$NON-NLS-1$
			fileName.setPreferredSize(new Dimension(200, 20));
		}
		return fileName;
	}


	/** Initialize teamNum.
	 * @return text */
	private STTextField getTeamNum() {
		if(teamNum == null) {
			teamNum = new STTextField(this, false);
			teamNum.setToolTipText(STGraphC.getMessage("WEBTUNER.DIALOG.TEAMNUM.TT")); //$NON-NLS-1$
			teamNum.setPreferredSize(new Dimension(70, 20));
		}
		return teamNum;
	}


	/** Initialize stepNum.
	 * @return text */
	private STTextField getStepNum() {
		if(stepNum == null) {
			stepNum = new STTextField(this, false);
			stepNum.setToolTipText(STGraphC.getMessage("WEBTUNER.DIALOG.STEPNUM.TT")); //$NON-NLS-1$
			stepNum.setPreferredSize(new Dimension(70, 20));
		}
		return stepNum;
	}


	/** Initialize buttonCreateFile.
	 * @return button */
	private JButton getButtonCreateFile() {
		if(buttonCreateFile == null) {
			buttonCreateFile = new JButton(STGraphC.getMessage("WEBTUNER.DIALOG.CREATEFILE")); //$NON-NLS-1$
			buttonCreateFile.setToolTipText(HTML + STGraphC.getMessage("WEBTUNER.DIALOG.CREATEFILE.TT")); //$NON-NLS-1$
			buttonCreateFile.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					try {
						String s = fileName.getText();
						if(!s.endsWith(".xls")) { s += ".xls"; } //$NON-NLS-1$ //$NON-NLS-2$
						File f = STWebTuner.getFile(s);
						if(f == null) { throw new Exception(); }
						int n1 = Integer.parseInt(teamNum.getText());
						if(n1 < 2) { throw new Exception(); }
						int n2 = Integer.parseInt(stepNum.getText());
						if(n2 < 2) { throw new Exception(); }
						STWebTuner.writeXLS(s, n1, n2);
						STTools.messenger(STTools.MESSAGETYPE_INF, STGraphC.getMessage("WEBTUNER.DIALOG.CREATE_OK")); //$NON-NLS-1$
					} catch (Exception e2) {
						String m = e2.getMessage();
						if(!STTools.isEmpty(m) && m.startsWith(STTools.AT)) {
							STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("WEBTUNER.DIALOG.CREATE_KO2") + STTools.SPACE + m.substring(1)); //$NON-NLS-1$
						} else {
							STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("WEBTUNER.DIALOG.CREATE_KO")); //$NON-NLS-1$
						}
					}

				}
			});
		}
		return buttonCreateFile;
	}


	/** Initialize buttonLinkFile.
	 * @return button */
	private JButton getButtonLinkFile() {
		if(buttonLinkFile == null) {
			buttonLinkFile = new JButton(STGraphC.getMessage("WEBTUNER.DIALOG.LINKFILE")); //$NON-NLS-1$
			buttonLinkFile.setToolTipText(HTML + STGraphC.getMessage("WEBTUNER.DIALOG.LINKFILE.TT")); //$NON-NLS-1$
			buttonLinkFile.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					try {
						String s = fileName.getText();
						if(!s.endsWith(".xls")) { s += ".xls"; } //$NON-NLS-1$ //$NON-NLS-2$
						File f = STWebTuner.getFile(s);
						if(f == null) { throw new Exception(); }
						STWebTuner.saturate(s);
						STTools.messenger(STTools.MESSAGETYPE_INF, STGraphC.getMessage("WEBTUNER.DIALOG.LINK_OK")); //$NON-NLS-1$
					} catch (Exception e2) {
						STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("WEBTUNER.DIALOG.LINK_KO")); //$NON-NLS-1$
					}
				}
			});
		}
		return buttonLinkFile;
	}


	/** Initialize buttonUnlinkFile.
	 * @return button */
	private JButton getButtonUnlinkFile() {
		if(buttonUnlinkFile == null) {
			buttonUnlinkFile = new JButton(STGraphC.getMessage("WEBTUNER.DIALOG.UNLINKFILE")); //$NON-NLS-1$
			buttonUnlinkFile.setToolTipText(HTML + STGraphC.getMessage("WEBTUNER.DIALOG.UNLINKFILE.TT")); //$NON-NLS-1$
			buttonUnlinkFile.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					try {
						int n = Integer.parseInt(teamNum.getText());
						if(n < 2) { throw new Exception(); }
						STWebTuner.desaturate(n);
						STTools.messenger(STTools.MESSAGETYPE_INF, STGraphC.getMessage("WEBTUNER.DIALOG.UNLINK_OK")); //$NON-NLS-1$
					} catch (Exception e2) {
						STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("WEBTUNER.DIALOG.UNLINK_KO")); //$NON-NLS-1$
					}
				}
			});
		}
		return buttonUnlinkFile;
	}


	/** Initialize buttonOk.
	 * @return button */
	private JButton getButtonOk() {
		if(buttonOk == null) {
			buttonOk = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			buttonOk.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					setVisible(false);
					STGraphC.setFocus();
				}
			});
		}
		return buttonOk;
	}


	/** Create an xls file with the appropriate data from the input nodes of the current model.
	 * @param fileName the name of the xls file to create
	 * @param numTeams the number of teams to adopt in the tuning
	 * @param numSteps the number of time steps to adopt in the tuning
	 * @throws Exception */
	public final static void writeXLS(final String fileName, final int numTeams, final int numSteps) throws Exception {
		WritableWorkbook workbook = Workbook.createWorkbook(getFile(fileName));

		int sheetNum = 0;
		WritableSheet sheet = workbook.createSheet("user", sheetNum); //$NON-NLS-1$
		int i = 0;
		for(STNode node : getInputNodes(USR_IN)) {
			double v = Double.parseDouble(node.getCProperty("DefaultValue")); //$NON-NLS-1$
			sheet.addCell(new Label(0, i, node.getName()));
			for(int s = 1; s <= numSteps; s++) { sheet.addCell(new Number(s, i, s)); }
			i++;
			for(int t = 0; t < numTeams; t++) {
				sheet.addCell(new Number(0, i, t));
				for(int s = 1; s <= numSteps; s++) { sheet.addCell(new Number(s, i, v)); }
				i++;
			}
			i++;
		}

		sheet = workbook.createSheet("exo", ++sheetNum); //$NON-NLS-1$
		for(int s = 1; s <= numSteps; s++) { sheet.addCell(new Number(s, 0, s)); }
		i = 1;
		for(STNode node : getInputNodes(EXO_IN)) {
			sheet.addCell(new Label(0, i, node.getName()));
			double v = Double.parseDouble(node.getCProperty("DefaultValue")); //$NON-NLS-1$
			for(int s = 1; s <= numSteps; s++) { sheet.addCell(new Number(s, i, v)); }
			i++;
		}

		sheet = workbook.createSheet("exoMult", ++sheetNum); //$NON-NLS-1$
		for(int s = 1; s <= numSteps; s++) { sheet.addCell(new Number(s, 0, s)); }
		i = 1;
		for(STNode node : getInputNodes(EXO_MULT_IN)) {
			sheet.addCell(new Label(0, i, node.getName()));
			double v = Double.parseDouble(node.getCProperty("DefaultValue")) * numTeams; //$NON-NLS-1$
			for(int s = 1; s <= numSteps; s++) { sheet.addCell(new Number(s, i, v)); }
			i++;
		}

		sheet = workbook.createSheet("init", ++sheetNum); //$NON-NLS-1$
		for(int t = 0; t < numTeams; t++) { sheet.addCell(new Number(t + 1, 0, t)); }
		i = 1;
		for(STNode node : getInputNodes(ST0_IN)) {
			sheet.addCell(new Label(0, i, node.getName()));
			double v = Double.parseDouble(node.getCProperty("DefaultValue")); //$NON-NLS-1$
			for(int t = 0; t < numTeams; t++) { sheet.addCell(new Number(t + 1, i, v)); }
			i++;
		}

		sheet = workbook.createSheet("param", ++sheetNum); //$NON-NLS-1$
		i = 1;
		for(STNode node : getInputNodes(PAR_IN)) {
			sheet.addCell(new Label(0, i, node.getName()));
			String dv = node.getCProperty("DefaultValue"); //$NON-NLS-1$
			if(STTools.isEmpty(dv)) { throw new Exception(STTools.AT + node.getName()); }
			double v = Double.parseDouble(dv);
			sheet.addCell(new Number(1, i, v));
			i++;
		}

		workbook.write();
		workbook.close();
	}


	/** Create an xls file with the appropriate data from the output nodes of the current model.
	 * @param fileName the name of the xls file to create
	 * @throws Exception */
	@SuppressWarnings("rawtypes")
	public final static void writeOutXLS(final String fileName) throws Exception {
		WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));

		List<STNode> nodes = getOutputNodes(USR_TGT);
		if(nodes == null) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("WEBTUNER.OUTXLS.ERR")); //$NON-NLS-1$
			return;
		}
		int sheetNum = 0;
		WritableSheet sheet = workbook.createSheet("target", sheetNum); //$NON-NLS-1$
		int i = 0;
		for(STNode node : nodes) {
			sheet.addCell(new jxl.write.Label(0, i, node.getName()));
			for(int s = 1; s <= STGraph.getSTC().getCurrentGraph().getNumSteps(); s++) { sheet.addCell(new jxl.write.Number(s, i, s)); }
			Matrix value = (Matrix)node.getValueHistory();
			int numTeams = value.getColumns().get(0).size();
			for(int t = 0; t < numTeams; t++) { sheet.addCell(new jxl.write.Number(0, i + t + 1, t)); }
			int c = 1;
			for(Vector col : value.getColumns()) {
				int r = i + 1;
				for(Object val : col) { sheet.addCell(new jxl.write.Number(c, r++, ((Tensor)val).getValue())); }
				c++;
			}
			i += (numTeams + 2);
		}

		nodes = getOutputNodes(USR_OUT);
		if(nodes == null) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("WEBTUNER.OUTXLS.ERR")); //$NON-NLS-1$
			return;
		}
		sheet = workbook.createSheet("user", ++sheetNum); //$NON-NLS-1$
		i = 0;
		for(STNode node : getOutputNodes(USR_OUT)) {
			sheet.addCell(new jxl.write.Label(0, i, node.getName()));
			for(int s = 1; s <= STGraph.getSTC().getCurrentGraph().getNumSteps(); s++) { sheet.addCell(new jxl.write.Number(s, i, s)); }
			Matrix value = (Matrix)node.getValueHistory();
			int numTeams = value.getColumns().get(0).size();
			for(int t = 0; t < numTeams; t++) { sheet.addCell(new jxl.write.Number(0, i + t + 1, t)); }
			int c = 1;
			for(Vector col : value.getColumns()) {
				int r = i + 1;
				for(Object val : col) { sheet.addCell(new jxl.write.Number(c, r++, ((Tensor)val).getValue())); }
				c++;
			}
			i += (numTeams + 2);
		}

		/*
    	sheet = workbook.createSheet("user_opt", ++sheetNum); //$NON-NLS-1$
    	i = 0;
    	for(STNode node : getOutputNodes(USR_OPT_OUT)) {
    		sheet.addCell(new jxl.write.Label(0, i, node.getName()));
    		for(int s = 1; s <= STGraph.getSTC().getCurrentGraph().getNumSteps(); s++) { sheet.addCell(new jxl.write.Number(s, i, s)); }
    		Matrix value = (Matrix)node.getValueHistory();
    		int numTeams = value.getColumns().get(0).size();
    		for(int t = 0; t < numTeams; t++) { sheet.addCell(new jxl.write.Number(0, i + t + 1, t)); }
			int c = 1;
    		for(Vector col : value.getColumns()) {
    			int r = i + 1;
				for(Object val : col) { sheet.addCell(new jxl.write.Number(c, r++, ((Tensor)val).getValue())); }
				c++;
			}
    		i += (numTeams + 2);
    	}
		 */

		nodes = getOutputNodes(USR_DEC_OUT);
		if(nodes == null) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("WEBTUNER.OUTXLS.ERR")); //$NON-NLS-1$
			return;
		}
		sheet = workbook.createSheet("dec", ++sheetNum); //$NON-NLS-1$
		i = 0;
		for(STNode node : getOutputNodes(USR_DEC_OUT)) {
			sheet.addCell(new jxl.write.Label(0, i, node.getName()));
			for(int s = 1; s <= STGraph.getSTC().getCurrentGraph().getNumSteps(); s++) { sheet.addCell(new jxl.write.Number(s, i, s)); }
			Matrix value = (Matrix)node.getValueHistory();
			int numTeams = value.getColumns().get(0).size();
			for(int t = 0; t < numTeams; t++) { sheet.addCell(new jxl.write.Number(0, i + t + 1, t)); }
			int c = 1;
			for(Vector col : value.getColumns()) {
				int r = i + 1;
				for(Object val : col) { sheet.addCell(new jxl.write.Number(c, r++, ((Tensor)val).getValue())); }
				c++;
			}
			i += (numTeams + 2);
		}

		nodes = getOutputNodes(PAR_OUT);
		if(nodes == null) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("WEBTUNER.OUTXLS.ERR")); //$NON-NLS-1$
			return;
		}
		sheet = workbook.createSheet("param", ++sheetNum); //$NON-NLS-1$
		int r = 0;
		for(int s = 1; s <= STGraph.getSTC().getCurrentGraph().getNumSteps(); s++) { sheet.addCell(new jxl.write.Number(s, r, s)); }
		for(STNode node : getOutputNodes(PAR_OUT)) {
			r++;
			sheet.addCell(new jxl.write.Label(0, r, node.getName()));
			Vector value = (Vector)node.getValueHistory();
			int c = 1;
			for(Object val : value) { sheet.addCell(new jxl.write.Number(c++, r, ((Tensor)val).getValue())); }
		}

		nodes = getOutputNodes(PAR_OPT_OUT);
		if(nodes == null) {
			STTools.messenger(STTools.MESSAGETYPE_ERR, STGraphC.getMessage("WEBTUNER.OUTXLS.ERR")); //$NON-NLS-1$
			return;
		}
		sheet = workbook.createSheet("param_opt", ++sheetNum); //$NON-NLS-1$
		i = 0;
		for(STNode node : getOutputNodes(PAR_OPT_OUT)) {
			sheet.addCell(new jxl.write.Label(0, i, node.getName()));
			for(int s = 1; s <= STGraph.getSTC().getCurrentGraph().getNumSteps(); s++) { sheet.addCell(new jxl.write.Number(s, i, s)); }
			Matrix value = (Matrix)node.getValueHistory();
			int numTeams = value.getColumns().get(0).size();
			for(int t = 0; t < numTeams; t++) { sheet.addCell(new jxl.write.Number(0, i + t + 1, t)); }
			int c = 1;
			for(Vector col : value.getColumns()) {
				r = i + 1;
				for(Object val : col) { sheet.addCell(new jxl.write.Number(c, r++, ((Tensor)val).getValue())); }
				c++;
			}
			i += (numTeams + 2);
		}

		workbook.write();
		workbook.close();
	}


	/** Adopt the data from the specified xls file for the tuning.
	 * <br>It operates by properly replacing the expressions in the relevant input nodes.
	 * <br>It assumes that the xls file was created for the current model.
	 * @param fileName the name of the xls file to adopt
	 * @param autoRefresh is the link to the xls file to be autorefreshed?
	 * @throws Exception */
	public final static void saturate(final String fileName) throws Exception {
		int numTeams = getNumTeams(fileName);
		int i = 2;
		for(STNode node : getInputNodes(USR_IN)) {
			String expr = "readFromXLS(\"" + fileName + "\",1," + i + ",2+index," + (i + numTeams - 1) + ",2+index)";
			((ValueNode)node).setExpression(expr);
			((ValueNode)node).setFormattedExpression(expr);
			i += numTeams + 2;
		}

		i = 2;
		for(STNode node : getInputNodes(EXO_IN)) {	
			String expr = "readFromXLS(\"" + fileName + "\",2," + i + ",2+index)";
			((ValueNode)node).setExpression(expr);
			((ValueNode)node).setFormattedExpression(expr);
			i++;
		}

		i = 2;
		for(STNode node : getInputNodes(EXO_MULT_IN)) {
			String expr = "readFromXLS(\"" + fileName + "\",3," + i + ",2+index)";
			((ValueNode)node).setExpression(expr);
			((ValueNode)node).setFormattedExpression(expr);
			i++;
		}

		i = 2;
		for(STNode node : getInputNodes(ST0_IN)) {
			String expr = "readFromXLS(\"" + fileName + "\",4," + i + ",2," + i + "," + (2 + numTeams - 1) + ")";
			((ValueNode)node).setExpression(expr);
			((ValueNode)node).setFormattedExpression(expr);
			i++;
		}

		i = 2;
		for(STNode node : getInputNodes(PAR_IN)) {
			String expr = "readFromXLS(\"" + fileName + "\",5," + i + ",2)";
			((ValueNode)node).setExpression(expr);
			((ValueNode)node).setFormattedExpression(expr);
			i++;
		}

		STGraph.getSTC().getCurrentGraph().refreshGraph();
	}


	/** Clean the expressions in the input nodes of current model by their default values.
	 * <br>It operates by reversing the operation performed by saturate().
	 * @param numTeams the number of teams assumed in the model (typically 2) */
	public final static void desaturate(final int numTeams) {
		for(STNode node : getInputNodes(USR_IN)) {
			String s = node.getCProperty("DefaultValue"); //$NON-NLS-1$
			StringBuilder expr = new StringBuilder("["); //$NON-NLS-1$
			for(int t = 0; t < numTeams; t++) {
				expr.append(s);
				if(t < numTeams - 1) { expr.append(STTools.COMMA); }
			}
			expr.append("]"); //$NON-NLS-1$
			((ValueNode)node).setExpression(expr.toString());
			((ValueNode)node).setFormattedExpression(expr.toString());
		}

		for(STNode node : getInputNodes(EXO_IN)) {
			String expr = node.getCProperty("DefaultValue"); //$NON-NLS-1$
			((ValueNode)node).setExpression(expr);
			((ValueNode)node).setFormattedExpression(expr);
		}

		for(STNode node : getInputNodes(EXO_MULT_IN)) {
			String expr = node.getCProperty("DefaultValue") + "*" + numTeams; //$NON-NLS-1$ //$NON-NLS-2$
			((ValueNode)node).setExpression(expr);
			((ValueNode)node).setFormattedExpression(expr);
		}

		for(STNode node : getInputNodes(ST0_IN)) {
			String s = node.getCProperty("DefaultValue"); //$NON-NLS-1$
			StringBuilder expr = new StringBuilder("["); //$NON-NLS-1$
			for(int t = 0; t < numTeams; t++) {
				expr.append(s);
				if(t < numTeams - 1) { expr.append(STTools.COMMA); }
			}
			expr.append("]"); //$NON-NLS-1$
			((ValueNode)node).setExpression(expr.toString());
			((ValueNode)node).setFormattedExpression(expr.toString());
		}

		for(STNode node : getInputNodes(PAR_IN)) {
			String expr = node.getCProperty("DefaultValue"); //$NON-NLS-1$
			((ValueNode)node).setExpression(expr);
			((ValueNode)node).setFormattedExpression(expr);
		}

		STGraph.getSTC().getCurrentGraph().refreshGraph();
	}


	/** */
	@SuppressWarnings("unchecked")
	private final static List<STNode> getInputNodes(final int type) {
		STNode[] n = STGraph.getSTC().getCurrentGraph().getNodes();
		if(n.length == 0) { return null; }
		List<STNode> result = new ArrayList<STNode>();
		for(STNode node: n) {
			if(node.isInput()) {
				String t = node.getCProperty("InputType"); //$NON-NLS-1$
				if(!STTools.isEmpty(t) && t.equals("" + type)) { result.add(node); } //$NON-NLS-1$
			}
		}
		if(result.size() == 0) { return null; }
		Collections.sort(result, STGraph.getSTC().getNodeNameComparator());
		return result;
	}


	/** */
	@SuppressWarnings("unchecked")
	private final static List<STNode> getOutputNodes(final int type) {
		STNode[] n = STGraph.getSTC().getCurrentGraph().getNodes();
		if(n.length == 0) { return null; }
		List<STNode> result = new ArrayList<STNode>();
		for(STNode node: n) {
			if(node.isOutput()) {
				String t = node.getCProperty("OutputType"); //$NON-NLS-1$
				if(!STTools.isEmpty(t) && t.equals("" + type)) { result.add(node); } //$NON-NLS-1$
			}
		}
		if(result.size() == 0) { return null; }
		Collections.sort(result, STGraph.getSTC().getNodeNameComparator());
		return result;
	}


	/** @throws Exception */
	private final static int getNumTeams(final String fileName) throws Exception {
		Workbook workbook = Workbook.getWorkbook(getFile(fileName));
		Sheet sheet = workbook.getSheet(0);
		double d = 0.0;
		try {
			for(int r = 1; r < 1000; r++) {
				d = ((NumberCell)sheet.getCell(0, r)).getValue();
			}
		} catch (final Exception e) {
			return (int)(d + 1);
		}
		return 0;
	}


	/**
	 * 
	 */
	/*
	private final static int getNumSteps(final String fileName) {
        try {
        	Workbook workbook = Workbook.getWorkbook(getFile(fileName));
			Sheet sheet = workbook.getSheet(0);
			double d = 0.0;
			try {
				for(int r = 1; r < 1000; r++) {
					d = ((NumberCell)sheet.getCell(r, 0)).getValue();
				}
			} catch (final Exception e) {
				return (int)d;
			}
		} catch (Exception e) { e.printStackTrace(); }
		return 0;
    }
	 */


	/** */
	private final static File getFile(final String fileName) {
		if(fileName == null) { return null; }
		String fn = STGraph.getSTC().getCurrentGraph().getFileName();
		int fs = fn.lastIndexOf(System.getProperty("file.separator")); //$NON-NLS-1$
		if(fs != -1) {
			fn = fn.substring(0, fs + 1) + fileName;
		} else {
			fn = fileName;
		}
		return new File(fn);
	}

}
