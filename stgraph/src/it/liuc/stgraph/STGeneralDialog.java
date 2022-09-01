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

import it.liuc.stgraph.util.GridConstraints;
import it.liuc.stgraph.util.STTextArea;
import it.liuc.stgraph.util.STTextField;
import it.liuc.stgraph.util.STTools;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;


/** Dialog for general configuration of the current model. */
@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class STGeneralDialog extends JDialog {
	/** The content pane. */
	private JPanel jContentPane;
	/** The model name. */
	private STTextField modelName;
	/** The scroll pane for model description. */
	private JScrollPane scrollModelDescription;
	/** The model description. */
	private STTextArea modelDescription;
	/** The time unit description. */
	private STTextField timeUnitDescription;
	/** The group of time frame. */
	private JPanel groupTimeFrame;
	/** The standard time frame. */
	private JRadioButton isStd;
	/** The instantaneous time frame. */
	private JRadioButton isIns;
	/** The windowed time frame. */
	private JRadioButton isWin;
	/** The playmode time frame. */
	private JRadioButton isPlM;
	/** The initial time label. */
	private JLabel labInitialTime;
	/** The initial time. */
	private STTextField initialTime;
	/** The final time label. */
	private JLabel labFinalTime;
	/** The final time. */
	private STTextField finalTime;
	/** The delta time label. */
	private JLabel labDeltaTime;
	/** The delta time. */
	private STTextField deltaTime;
	/** The window size label. */
	private JLabel labWinSize;
	/** The window size. */
	private STTextField windowSize;
	/** The delay label. */
	private JLabel labDelay;
	/** The delay. */
	private STTextField delay;
	/** The steps label. */
	private JLabel labSteps;
	/** The steps. */
	private STTextField steps;
	/** The batch steps label. */
	private JLabel labBatchSteps;
	/** The batch steps. */
	private STTextField batchSteps;
	/** The integration method. */
	private JComboBox intMethod;
	/** The exception handling label. */
	@SuppressWarnings("unused")
	private JLabel labExceptionHandling;
	/** The exception handling. */
	private JComboBox exceptionHandling;
	/** The with interrupts. */
	private JCheckBox withInterrupts;
	/** The data save. */
	private JCheckBox dataSave;
	/** The group of index origin. */
	private JPanel groupIO;
	/** The isIO=0. */
	private JRadioButton isIO0;
	/** The isIO=1. */
	private JRadioButton isIO1;
	/** The for net. */
	private JCheckBox forNet;
	/** The server address panel. */
	private JPanel serverAddressPanel;
	/** The server address. */
	private STTextField serverAddress;
	/** The for web. */
	private JCheckBox forWeb;
	/** The languages for web panel. */
	private JPanel modelLanguagesPanel;
	/** The model languages for web. */
	private STTextField modelLanguages;
	/** The button ok. */
	private JButton buttonOk;
	/** The button cancel. */
	private JButton buttonCancel;
	/** The button panel. */
	private JPanel buttonPanel;
	/** The field size. */
	private static Dimension fieldSize = new Dimension(70, 20);
	/** The big field size. */
	private static Dimension bigFieldSize = new Dimension(300, 20);


	/** Class constructor. */
	public STGeneralDialog() {
		super();
		if(STGraphC.getContainer() instanceof JFrame) { setAlwaysOnTop(true); }
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setModal(true);
		setIconImage(STGraphC.getSystemIcon());
	}


	/** Initialize and open the dialog. */
	public final void open() {
		setTitle(STGraphC.getMessage("SYSTEM.DIALOG.EDITTITLE")); //$NON-NLS-1$
		setContentPane(getJContentPane());
		fill();
		boolean b = STGraph.getSTC().getCurrentGraph().isEditable;
		modelName.setEnabled(b);
		modelDescription.setEnabled(b);
		timeUnitDescription.setEnabled(b);
		isStd.setEnabled(b);
		isIns.setEnabled(b);
		isWin.setEnabled(b);
		isPlM.setEnabled(b);
		initialTime.setEnabled(b);
		finalTime.setEnabled(b);
		deltaTime.setEnabled(b);
		delay.setEnabled(b);
		steps.setEnabled(b);
		batchSteps.setEnabled(b);
		windowSize.setEnabled(b);
		intMethod.setEnabled(b);
		exceptionHandling.setEnabled(b);
		withInterrupts.setEnabled(b);
		dataSave.setEnabled(b);
		isIO0.setEnabled(b);
		isIO1.setEnabled(b);
		forNet.setEnabled(b);
		serverAddress.setEnabled(b);
		forWeb.setEnabled(b);
		modelLanguages.setEnabled(b);
		setFieldVisibility(STGraph.getSTC().getCurrentGraph().getTimeFrame());
		getJContentPane().getRootPane().setDefaultButton(getButtonOk());
		pack();
		setLocation(STGraphC.getContainer().getX() + 100, STGraphC.getContainer().getY() + 100);
		setVisible(true);
	}


	/** Fill the fields in the dialog. */
	private void fill() {
		STGraphExec graph = STGraph.getSTC().getCurrentGraph();
		modelName.setText(!STTools.isEmpty(graph.getModelName()) ? graph.getModelName() : STTools.BLANK);
		modelDescription.setText(!STTools.isEmpty(graph.getModelDescription()) ? graph.getModelDescription() : STTools.BLANK);
		timeUnitDescription.setText(!STTools.isEmpty(graph.getTimeUnitDescription()) ? graph.getTimeUnitDescription() : STTools.BLANK);
		initialTime.setText(STTools.BLANK + graph.getTime0());
		finalTime.setText(STTools.BLANK + graph.getTime1());
		windowSize.setText(STTools.BLANK + graph.getMaxSteps());
		deltaTime.setText(STTools.BLANK + graph.getTimeD());
		delay.setText(STTools.BLANK + graph.simulationDelay);
		steps.setText(STTools.BLANK + graph.stepsBeforePause);
		batchSteps.setText(STTools.BLANK + graph.batchSteps);
		intMethod.setSelectedIndex(graph.integrationMethod);
		exceptionHandling.setSelectedIndex(graph.getExceptionHandling());
		withInterrupts.setSelected(graph.isWithInterrupts());
		dataSave.setSelected(graph.isDataSaved());
		forNet.setSelected(graph.isForNet());
		serverAddressPanel.setVisible(graph.isForNet());
		serverAddress.setText(graph.getServerAddress());
		forWeb.setSelected(graph.isForWeb());
		modelLanguagesPanel.setVisible(graph.isForWeb());
		modelLanguages.setText(graph.getModelLanguagesAsString());
		if(graph.getIO() == 0) {
			isIO0.setSelected(true);
		} else {
			isIO1.setSelected(true);
		}
	}


	/** Initialize jContentPane.
	 * @return panel */
	private JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new JPanel(new GridBagLayout());
			// model name
			jContentPane.add(new JLabel(STGraphC.getMessage("MODEL.EDIT.NAME") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			//
			GridConstraints gbcModelName = new GridConstraints(1, 0);
			gbcModelName.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getModelName(), gbcModelName);
			// model description
			jContentPane.add(new JLabel(STGraphC.getMessage("MODEL.EDIT.DESCRIPTION") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 1, GridBagConstraints.NORTHEAST));
			//
			GridConstraints gbcModelDesc = new GridConstraints(1, 1);
			gbcModelDesc.weightx = 1.0;
			gbcModelDesc.weighty = 1.0;
			gbcModelDesc.fill = GridBagConstraints.BOTH;
			jContentPane.add(getModelDescription(), gbcModelDesc);
			// time unit description
			jContentPane.add(new JLabel(STGraphC.getMessage("MODEL.EDIT.TIMEUNIT") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 2, GridBagConstraints.EAST));
			//
			GridConstraints gbcTimeUnitDesc = new GridConstraints(1, 2);
			gbcTimeUnitDesc.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getTimeUnitDescription(), gbcTimeUnitDesc);
			// time frame
			JLabel labTimeFrame = new JLabel(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME") + STTools.COLON); //$NON-NLS-1$
			labTimeFrame.setToolTipText(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME.TT")); //$NON-NLS-1$
			jContentPane.add(labTimeFrame, new GridConstraints(0, 3, GridBagConstraints.EAST));
			//
			GridConstraints gbcTimeFrame = new GridConstraints(1, 3);
			gbcTimeFrame.anchor = GridBagConstraints.WEST;
			gbcTimeFrame.gridwidth = 2;
			gbcTimeFrame.weightx = 1.0;
			jContentPane.add(getGroupTimeFrame(), gbcTimeFrame);
			// initial time
			jContentPane.add(labInitialTime = new JLabel(STGraphC.getMessage("MODEL.EDIT.INIT") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 4, GridBagConstraints.EAST));
			//
			jContentPane.add(getInitialTime(), new GridConstraints(1, 4, GridBagConstraints.WEST));
			// final time
			jContentPane.add(labFinalTime = new JLabel(STGraphC.getMessage("MODEL.EDIT.FINAL") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 5, GridBagConstraints.EAST));
			//
			jContentPane.add(getFinalTime(), new GridConstraints(1, 5, GridBagConstraints.WEST));
			// delta time
			jContentPane.add(labDeltaTime = new JLabel(STGraphC.getMessage("MODEL.EDIT.DELTA") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 6, GridBagConstraints.EAST));
			//
			GridConstraints gbcDeltaTime = new GridConstraints(1, 6, GridBagConstraints.WEST);
			gbcDeltaTime.weightx = 1.0;
			jContentPane.add(getDeltaTime(), gbcDeltaTime);
			// window size
			labWinSize = new JLabel(STGraphC.getMessage("MODEL.EDIT.WINSIZE") + STTools.COLON); //$NON-NLS-1$
			labWinSize.setToolTipText(STGraphC.getMessage("MODEL.EDIT.WINSIZE.TT")); //$NON-NLS-1$
			jContentPane.add(labWinSize, new GridConstraints(0, 7, GridBagConstraints.EAST));
			//
			GridConstraints gbcWinSize = new GridConstraints(1, 7, GridBagConstraints.WEST);
			gbcWinSize.weightx = 1.0;
			jContentPane.add(getWindowSize(), gbcWinSize);
			// simulation delay
			jContentPane.add(labDelay = new JLabel(STGraphC.getMessage("MODEL.EDIT.DELAY") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 8, GridBagConstraints.EAST));
			//			
			jContentPane.add(getDelay(), new GridConstraints(1, 8, GridBagConstraints.WEST));
			// number of steps before pause
			jContentPane.add(labSteps = new JLabel(STGraphC.getMessage("MODEL.EDIT.STEPS") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 9, GridBagConstraints.EAST));
			//
			jContentPane.add(getSteps(), new GridConstraints(1, 9, GridBagConstraints.WEST));
			// number of batchSteps (only in standard exec level)
			if(STGraph.getExecLevel() == STGraph.EXECLEVEL_STANDARD) {
				jContentPane.add(labBatchSteps = new JLabel(STGraphC.getMessage("MODEL.EDIT.BATCHSTEPS") + STTools.COLON), //$NON-NLS-1$
						new GridConstraints(0, 10, GridBagConstraints.EAST));
				//
				jContentPane.add(getBatchSteps(), new GridConstraints(1, 10, GridBagConstraints.WEST));
			} else {
				labBatchSteps = new JLabel(); // just to define everything, as a side effect...
				getBatchSteps();
			}
			// integration method
			jContentPane.add(new JLabel(STGraphC.getMessage("MODEL.EDIT.INTMETHOD") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 11, GridBagConstraints.EAST));
			//
			jContentPane.add(getIntMethod(), new GridConstraints(1, 11, GridBagConstraints.WEST));
			// exception handling
			jContentPane.add(labExceptionHandling = new JLabel(STGraphC.getMessage("MODEL.EDIT.EXCEPTIONHANDLING") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 13, GridBagConstraints.EAST));
			//
			jContentPane.add(getExceptionHandling(), new GridConstraints(1, 13, GridBagConstraints.WEST));
			// with interrupts
			jContentPane.add(getWithInterrupts(), new GridConstraints(1, 14, GridBagConstraints.WEST));
			// data saving
			jContentPane.add(getDataSave(), new GridConstraints(1, 16, GridBagConstraints.WEST));
			// index origin
			jContentPane.add(new JLabel(STGraphC.getMessage("MODEL.EDIT.IO") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 17, GridBagConstraints.EAST));
			//
			GridConstraints gbcIO2 = new GridConstraints(1, 17, GridBagConstraints.WEST);
			gbcIO2.gridwidth = 2;
			gbcIO2.weightx = 1.0;
			jContentPane.add(getGroupIO(), gbcIO2);
			
			// for net
			jContentPane.add(getForNet(), new GridConstraints(1, 18, GridBagConstraints.WEST));
			GridConstraints gbcSA = new GridConstraints(0, 19, GridBagConstraints.WEST);
			gbcSA.gridwidth = 2;
			gbcSA.weightx = 1.0;
			jContentPane.add(getServerAddressPanel(), gbcSA);

			// for web
			if(STGraph.getExecLevel() != STGraph.EXECLEVEL_EASY) {
				jContentPane.add(getForWeb(), new GridConstraints(1, 20, GridBagConstraints.WEST));
				GridConstraints gbcLFW = new GridConstraints(0, 21, GridBagConstraints.WEST);
				gbcLFW.gridwidth = 2;
				gbcLFW.weightx = 1.0;
				jContentPane.add(getModelLanguagesPanel(), gbcLFW);
			} else {
				getForWeb(); // just to define everything, as a side effect...
				getModelLanguagesPanel();
			}
			// buttons
			GridConstraints gbcButt = new GridConstraints(0, 22);
			gbcButt.gridwidth = 2;
			gbcButt.fill = GridBagConstraints.HORIZONTAL;
			jContentPane.add(getButtonPanel(), gbcButt);
		}
		return jContentPane;
	}


	/** Initialize modelName.
	 * @return text */
	private STTextField getModelName() {
		if(modelName == null) {
			modelName = new STTextField(this, false);
			modelName.setToolTipText(STGraphC.getMessage("MODEL.EDIT.NAME.TT")); //$NON-NLS-1$
		}
		return modelName;
	}


	/** Initialize modelDescription.
	 * @return textarea */
	private JScrollPane getModelDescription() {
		if(scrollModelDescription == null) {
			modelDescription = new STTextArea(this, false);
			modelDescription.setToolTipText(STGraphC.getMessage("MODEL.EDIT.DESCRIPTION.TT")); //$NON-NLS-1$
			scrollModelDescription = new JScrollPane();
			scrollModelDescription.setPreferredSize(new Dimension(100, 60));
			scrollModelDescription.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollModelDescription.setViewportView(modelDescription);
		}
		return scrollModelDescription;
	}


	/** Initialize timeUnitDescription.
	 * @return text */
	private STTextField getTimeUnitDescription() {
		if(timeUnitDescription == null) {
			timeUnitDescription = new STTextField(this, false);
			timeUnitDescription.setToolTipText(STGraphC.getMessage("MODEL.EDIT.TIMEUNIT.TT")); //$NON-NLS-1$
		}
		return timeUnitDescription;
	}


	/** Initialize groupTimeFrame.
	 * @return radio group */
	private JPanel getGroupTimeFrame() {
		if(groupTimeFrame == null) {
			isStd = new JRadioButton(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME.STD")); //$NON-NLS-1$
			isStd.setToolTipText(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME.STD.TT")); //$NON-NLS-1$
			isStd.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; }
				}
			});
			isStd.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) { setFieldVisibility(STGraphImpl.TIMEFRAME_STANDARD); }
			});
			isIns = new JRadioButton(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME.INS")); //$NON-NLS-1$
			isIns.setToolTipText(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME.INS.TT")); //$NON-NLS-1$
			isIns.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; }
				}
			});
			isIns.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) { setFieldVisibility(STGraphImpl.TIMEFRAME_INSTANTANEOUS); }
			});
			isWin = new JRadioButton(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME.WIN")); //$NON-NLS-1$
			isWin.setToolTipText(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME.WIN.TT")); //$NON-NLS-1$
			isWin.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; }
				}
			});
			isWin.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) { setFieldVisibility(STGraphImpl.TIMEFRAME_WINDOWED); }
			});
			isPlM = new JRadioButton(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME.PLM")); //$NON-NLS-1$
			isPlM.setToolTipText(STGraphC.getMessage("MODEL.EDIT.TIMEFRAME.PLM.TT")); //$NON-NLS-1$
			isPlM.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE) { setVisible(false); return; }
				}
			});
			isPlM.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) { setFieldVisibility(STGraphImpl.TIMEFRAME_PLAYMODE); }
			});
			ButtonGroup radios = new ButtonGroup();
			radios.add(isStd);
			radios.add(isIns);
			radios.add(isWin);
			radios.add(isPlM);
			groupTimeFrame = new JPanel();
			groupTimeFrame.add(isStd);
			groupTimeFrame.add(isIns);
			groupTimeFrame.add(isWin);
			groupTimeFrame.add(isPlM);
		}
		return groupTimeFrame;
	}


	/** Control the visibility of the fields.
	 * @param x the x */
	private void setFieldVisibility(final int x) {
		if(x == STGraphImpl.TIMEFRAME_INSTANTANEOUS || x == STGraphImpl.TIMEFRAME_PLAYMODE) {
			boolean b = false;
			getInitialTime().setVisible(b); labInitialTime.setVisible(b);
			getFinalTime().setVisible(b); labFinalTime.setVisible(b);
			getSteps().setVisible(b); labSteps.setVisible(b);
			getBatchSteps().setVisible(b); labBatchSteps.setVisible(b);
			getWindowSize().setVisible(b); labWinSize.setVisible(b);
			if(x == STGraphImpl.TIMEFRAME_INSTANTANEOUS) {
				isIns.setSelected(true);
				getDeltaTime().setVisible(false); labDeltaTime.setVisible(false);
				getDelay().setVisible(false); labDelay.setVisible(false);				
			} else {
				isPlM.setSelected(true);
				getDeltaTime().setVisible(true); labDeltaTime.setVisible(true);
				getDelay().setVisible(true); labDelay.setVisible(true);
			}
		} else if(x == STGraphImpl.TIMEFRAME_STANDARD || x == STGraphImpl.TIMEFRAME_WINDOWED) {
			boolean b = true;
			getInitialTime().setVisible(b); labInitialTime.setVisible(b);
			getFinalTime().setVisible(b); labFinalTime.setVisible(b);
			getDeltaTime().setVisible(b); labDeltaTime.setVisible(b);
			getDelay().setVisible(b); labDelay.setVisible(b);
			getSteps().setVisible(b); labSteps.setVisible(b);
			getBatchSteps().setVisible(b); labBatchSteps.setVisible(b);			
			if(x == STGraphImpl.TIMEFRAME_STANDARD) {
				isStd.setSelected(true);
				getWindowSize().setVisible(false); labWinSize.setVisible(false);
			} else {
				isWin.setSelected(true);
				getWindowSize().setVisible(true); labWinSize.setVisible(true);
			}
			// try to avoid some common mistakes
			if(x == STGraphImpl.TIMEFRAME_STANDARD || x == STGraphImpl.TIMEFRAME_WINDOWED) {
				try {
					if(Double.parseDouble(initialTime.getText()) == 0.0 && Double.parseDouble(finalTime.getText()) == 0.0) {
						finalTime.setText("10.0"); //$NON-NLS-1$
					}
				} catch (Exception ex) { ; }
				try {
					if(Double.parseDouble(finalTime.getText()) == Double.MAX_VALUE) {
						finalTime.setText("10.0"); //$NON-NLS-1$
					}
				} catch (Exception ex) { ; }
			}
		}
		pack();
	}


	/** Initialize initialTime.
	 * @return text */
	private STTextField getInitialTime() {
		if(initialTime == null) {
			initialTime = new STTextField(this, false);
			initialTime.setToolTipText(STGraphC.getMessage("MODEL.EDIT.INIT.TT")); //$NON-NLS-1$
			initialTime.setPreferredSize(fieldSize);
			initialTime.setMinimumSize(fieldSize);
		}
		return initialTime;
	}


	/** Initialize finalTime.
	 * @return text */
	private STTextField getFinalTime() {
		if(finalTime == null) {
			finalTime = new STTextField(this, false);
			finalTime.setToolTipText(STGraphC.getMessage("MODEL.EDIT.FINAL.TT")); //$NON-NLS-1$
			finalTime.setPreferredSize(fieldSize);
			finalTime.setMinimumSize(fieldSize);
		}
		return finalTime;
	}


	/** Initialize deltaTime.
	 * @return text */
	private STTextField getDeltaTime() {
		if (deltaTime == null) {
			deltaTime = new STTextField(this, false);
			deltaTime.setToolTipText(STGraphC.getMessage("MODEL.EDIT.DELTA.TT")); //$NON-NLS-1$
			deltaTime.setPreferredSize(fieldSize);
			deltaTime.setMinimumSize(fieldSize);
		}
		return deltaTime;
	}


	/** Initialize delay.
	 * @return text */
	private STTextField getDelay() {
		if(delay == null) {
			delay = new STTextField(this, false);
			delay.setToolTipText(STGraphC.getMessage("MODEL.EDIT.DELAY.TT")); //$NON-NLS-1$
			delay.setPreferredSize(fieldSize);
			delay.setMinimumSize(fieldSize);
		}
		return delay;
	}


	/** Initialize steps.
	 * @return text */
	private STTextField getSteps() {
		if(steps == null) {
			steps = new STTextField(this, false);
			steps.setToolTipText(STGraphC.getMessage("MODEL.EDIT.STEPS.TT")); //$NON-NLS-1$
			steps.setPreferredSize(fieldSize);
			steps.setMinimumSize(fieldSize);
		}
		return steps;
	}


	/** Initialize batchSteps.
	 * @return text */
	private STTextField getBatchSteps() {
		if(batchSteps == null) {
			batchSteps = new STTextField(this, false);
			batchSteps.setToolTipText(STGraphC.getMessage("MODEL.EDIT.BATCHSTEPS.TT")); //$NON-NLS-1$
			batchSteps.setPreferredSize(fieldSize);
			batchSteps.setMinimumSize(fieldSize);
		}
		return batchSteps;
	}


	/** Initialize windowSize.
	 * @return text */
	private STTextField getWindowSize() {
		if(windowSize == null) {
			windowSize = new STTextField(this, false);
			windowSize.setToolTipText(STGraphC.getMessage("MODEL.EDIT.WINSIZE.TT")); //$NON-NLS-1$
			windowSize.setPreferredSize(fieldSize);
			windowSize.setMinimumSize(fieldSize);
		}
		return windowSize;
	}


	/** Initialize intMethod.
	 * @return combobox */
	private JComboBox getIntMethod() {
		if(intMethod == null) {
			String[] methods = {
					STGraphC.getMessage("MODEL.EDIT.INTMETHOD.EULER"), //$NON-NLS-1$
					STGraphC.getMessage("MODEL.EDIT.INTMETHOD.RK2"), //$NON-NLS-1$
					STGraphC.getMessage("MODEL.EDIT.INTMETHOD.RK23") }; //$NON-NLS-1$
			intMethod = new JComboBox(methods);
			intMethod.setToolTipText(STGraphC.getMessage("MODEL.EDIT.INTMETHOD.TT")); //$NON-NLS-1$

		}
		return intMethod;
	}


	/** Initialize exceptionHandling.
	 * @return combobox */
	private JComboBox getExceptionHandling() {
		if(exceptionHandling == null) {
			String[] methods = {
					STGraphC.getMessage("MODEL.EDIT.EXCEPTIONHANDLING.GO"), //$NON-NLS-1$
					STGraphC.getMessage("MODEL.EDIT.EXCEPTIONHANDLING.STOP") }; //$NON-NLS-1$
			exceptionHandling = new JComboBox(methods);
			exceptionHandling.setToolTipText(STGraphC.getMessage("MODEL.EDIT.EXCEPTIONHANDLING.TT")); //$NON-NLS-1$
		}
		return exceptionHandling;
	}


	/** Initialize withInterrupts.
	 * @return checkbox */
	private JCheckBox getWithInterrupts() {
		if(withInterrupts == null) {
			withInterrupts = new JCheckBox();
			withInterrupts.setToolTipText(STGraphC.getMessage("MODEL.EDIT.INTERRUPTS.TT")); //$NON-NLS-1$
			withInterrupts.setText(STGraphC.getMessage("MODEL.EDIT.INTERRUPTS")); // Handle interrupts? //$NON-NLS-1$
			withInterrupts.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						setVisible(false);
						return;
					}
				}
			});
		}
		return withInterrupts;
	}


	/** Initialize dataSave.
	 * @return checkbox */
	private JCheckBox getDataSave() {
		if(dataSave == null) {
			dataSave = new JCheckBox();
			dataSave.setToolTipText(STGraphC.getMessage("MODEL.EDIT.SAVE.TT")); //$NON-NLS-1$
			dataSave.setText(STGraphC.getMessage("MODEL.EDIT.SAVE")); // Save simulation data? //$NON-NLS-1$
			dataSave.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						setVisible(false);
						return;
					}
				}
			});
		}
		return dataSave;
	}


	/** Initialize groupIO.
	 * @return radio group */
	private JPanel getGroupIO() {
		if(groupIO == null) {
			isIO0 = new JRadioButton("0"); //$NON-NLS-1$
			isIO1 = new JRadioButton("1"); //$NON-NLS-1$
			isIO0.setToolTipText(STGraphC.getMessage("MODEL.EDIT.IO.TT")); //$NON-NLS-1$
			isIO1.setToolTipText(STGraphC.getMessage("MODEL.EDIT.IO.TT")); //$NON-NLS-1$
			ButtonGroup radios = new ButtonGroup();
			radios.add(isIO0);
			radios.add(isIO1);
			groupIO = new JPanel();
			groupIO.add(isIO0);
			groupIO.add(isIO1);

		}
		return groupIO;
	}


	/** Initialize forNet.
	 * @return checkbox */
	private JCheckBox getForNet() {
		if(forNet == null) {
			forNet = new JCheckBox();
			forNet.setToolTipText(STGraphC.getMessage("MODEL.EDIT.FORNET.TT")); //$NON-NLS-1$
			forNet.setText(STGraphC.getMessage("MODEL.EDIT.FORNET")); // For net? //$NON-NLS-1$
			forNet.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					boolean s = forNet.isSelected();
					serverAddressPanel.setVisible(s);
				}
			});
			forNet.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						setVisible(false);
						return;
					}
				}
			});
		}
		return forNet;
	}


	/** Initialize serverAddress.
	 * @return text */
	private STTextField getServerAddress() {
		if(serverAddress == null) {
			serverAddress = new STTextField(this, false);
			serverAddress.setToolTipText(STGraphC.getMessage("MODEL.EDIT.SERVERADDRESS.TT")); //$NON-NLS-1$
			serverAddress.setPreferredSize(bigFieldSize);
			serverAddress.setMinimumSize(bigFieldSize);
		}
		return serverAddress;
	}


	/** Initialize serverAddressPanel.
	 * @return ServerAddressPanel */
	private JPanel getServerAddressPanel() {
		if(serverAddressPanel == null) {
			serverAddressPanel = new JPanel(new GridBagLayout());
			serverAddressPanel.add(new JLabel(STGraphC.getMessage("MODEL.EDIT.SERVERADDRESS") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			GridConstraints gbc = new GridConstraints(1, 0);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			serverAddressPanel.add(getServerAddress(), gbc);
		}
		return serverAddressPanel;
	}


	/** Initialize forWeb.
	 * @return checkbox */
	private JCheckBox getForWeb() {
		if(forWeb == null) {
			forWeb = new JCheckBox();
			forWeb.setToolTipText(STGraphC.getMessage("MODEL.EDIT.FORWEB.TT")); //$NON-NLS-1$
			forWeb.setText(STGraphC.getMessage("MODEL.EDIT.FORWEB")); // For web? //$NON-NLS-1$
			forWeb.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					boolean s = forWeb.isSelected();
					modelLanguagesPanel.setVisible(s);
				}
			});
			forWeb.addKeyListener(new KeyAdapter() {
				public void keyReleased(final KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						setVisible(false);
						return;
					}
				}
			});
		}
		return forWeb;
	}


	/** Initialize modelLanguages.
	 * @return text */
	private STTextField getModelLanguages() {
		if(modelLanguages == null) {
			modelLanguages = new STTextField(this, false);
			modelLanguages.setToolTipText(STGraphC.getMessage("MODEL.EDIT.LANGUAGESFORWEB.TT")); //$NON-NLS-1$
			modelLanguages.setPreferredSize(bigFieldSize);
			modelLanguages.setMinimumSize(bigFieldSize);
		}
		return modelLanguages;
	}


	/** Initialize modelLanguagesPanel.
	 * @return modelLanguagesPanel */
	private JPanel getModelLanguagesPanel() {
		if(modelLanguagesPanel == null) {
			modelLanguagesPanel = new JPanel(new GridBagLayout());
			modelLanguagesPanel.add(new JLabel(STGraphC.getMessage("MODEL.EDIT.LANGUAGESFORWEB") + STTools.COLON), //$NON-NLS-1$
					new GridConstraints(0, 0, GridBagConstraints.EAST));
			GridConstraints gbc = new GridConstraints(1, 0);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			modelLanguagesPanel.add(getModelLanguages(), gbc);
		}
		return modelLanguagesPanel;
	}


	/** Initialize buttonOk.
	 * @return button */
	private JButton getButtonOk() {
		if(buttonOk == null) {
			buttonOk = new JButton(STGraphC.getMessage("DIALOG.OK"), new ImageIcon(STGraph.getSTC().getBaseIcon("dialog-ok.png"))); //$NON-NLS-1$ //$NON-NLS-2$
			buttonOk.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					STGraphExec graph = STGraph.getSTC().getCurrentGraph();
					if(!graph.isEditable) {
						setVisible(false);
						return;
					}
					graph.setModelName(modelName.getText());
					graph.setModelDescription(modelDescription.getText());
					graph.setTimeUnitDescription(timeUnitDescription.getText());
					if(isIns.isSelected()) {
						graph.setTimeFrame(STGraphImpl.TIMEFRAME_INSTANTANEOUS);
						graph.setTime0(0.0);
						graph.setTime1(0.0);
						graph.simulationDelay = 1;
						graph.stepsBeforePause = 1;
						graph.batchSteps = 0;
					} else if(isPlM.isSelected()) {
						graph.setTimeFrame(STGraphImpl.TIMEFRAME_PLAYMODE);
						graph.setTime0(0.0);
						graph.setTime1(Double.MAX_VALUE); //hopefully it could be enough...
						try {
							int i = Integer.parseInt(delay.getText());
							if(i < 1) { i = 100; }
							graph.simulationDelay = i;
						} catch (Exception ex) { graph.simulationDelay = 100; }
						graph.stepsBeforePause = 1;
						graph.batchSteps = 0;
						graph.setMaxSteps(1);
					} else {
						if(isStd.isSelected()) {
							graph.setTimeFrame(STGraphImpl.TIMEFRAME_STANDARD);
						} else {
							graph.setTimeFrame(STGraphImpl.TIMEFRAME_WINDOWED);
							try {
								int i = Integer.parseInt(windowSize.getText());
								if(i < 1) { i = 1; }
								graph.setMaxSteps(i);
							} catch (Exception ex) { graph.setMaxSteps(10); }
						}
						try {
							graph.setTime0(Double.parseDouble(initialTime.getText()));
						} catch (Exception ex) { graph.setTime0(0.0); }
						try {
							double d = Double.parseDouble(finalTime.getText());
							if(d < graph.getTime0()) { d = graph.getTime0(); }
							graph.setTime1(d);
						} catch (Exception ex) { graph.setTime1(10.0); }
						try {
							int i = Integer.parseInt(delay.getText());
							if(i < 1) { i = 100; }
							graph.simulationDelay = i;
						} catch (Exception ex) { graph.simulationDelay = 100; }
						try {
							int i = Integer.parseInt(steps.getText());
							if(i < 1) { i = 1; }
							graph.stepsBeforePause = i;
						} catch (Exception ex) { graph.stepsBeforePause = 1; }
						try {
							int i = Integer.parseInt(batchSteps.getText());
							if(i < 1) { i = 1; }
							graph.batchSteps = i;
						} catch (Exception ex) { graph.batchSteps = 1; }
					}
					if(isIns.isSelected()) {
						graph.setTimeD(1.0);
					} else {
						try {
							double d = Double.parseDouble(deltaTime.getText());
							if(d <= 0.0) { d = 1.0; }
							graph.setTimeD(d);
						} catch (Exception ex) { graph.setTimeD(1.0); }
					}
					graph.computeNumSteps();
					graph.integrationMethod = intMethod.getSelectedIndex();
					if(isIO0.isSelected()) {
						graph.setIO(0);
					} else {
						graph.setIO(1);
					}
					graph.setExceptionHandling(exceptionHandling.getSelectedIndex());
					graph.setWithInterrupts(withInterrupts.isSelected());
					graph.setDataSaved(dataSave.isSelected());
					graph.setForNet(forNet.isSelected());
					if(forNet.isSelected()) {
						graph.setServerAddress(serverAddress.getText());
					} else {
						graph.setServerAddress(null);
					}
					graph.setForWeb(forWeb.isSelected());
	        		graph.getInterpreter().handleVarsForWeb();
					if(forWeb.isSelected()) {
						STGraphC.getMyMenuBar().getMenu(STMenuBar.WEBMENU_NUM).setVisible(true);
						String x1 = graph.getModelLanguagesAsString(); // get it to handle the possible change of the handled languages
						String s = modelLanguages.getText().replaceAll(STTools.SPACE, STTools.BLANK);
						if(s.length() == 0) { s = STGraphImpl.DEFAULT_MODEL_LANGUAGE; } // at least one language must be there
						graph.setModelLanguagesAsString(s); // no spaces are allowed in language names
						String x2 = graph.getModelLanguagesAsString();
						String[] y1 = x1.split(STTools.COMMA);
						String[] y2 = x2.split(STTools.COMMA);
						if(!x2.equals(x1)) { // at the moment handle only rename of the first/default language and additions of new languages (more generally also deletions and reorderings, including changes of first/default language, could be handled)
							if(y1.length > y2.length) {
								STTools.messenger(STTools.MESSAGETYPE_ERR, "Sorry: at the moment deletion of languages is not supported"); //$NON-NLS-1$
								graph.setModelLanguagesAsString(x1); // just rollback
							} else {
								boolean isChanged = false;
								for(int i = 1; i < y1.length; i++) {
									if(!y2[i].equals(y1[i])) { isChanged = true; } // a rename of a non-default language
								}
								if(isChanged) {
									STTools.messenger(STTools.MESSAGETYPE_ERR, "Sorry: at the moment renaming of non-default languages or reordering is not supported"); //$NON-NLS-1$
									graph.setModelLanguagesAsString(x1); // just rollback
								} else {
									if(!y2[0].equals(y1[0])) { // the rename of the first/default language
										graph.setWebModelDescription(graph.getWebModelDescription(y1[0]), y2[0]);
										graph.setWebModelDescription(null, y1[0]);
										graph.setWebModelGroupData(graph.getWebModelGroupData(y1[0]), y2[0]);
										graph.setWebModelGroupData(null, y1[0]);
									}
									if(y2.length > y1.length) { // the addition of one or more languages
										for(int i = y1.length; i < y2.length; i++) { STWebDialog.addGroupInfoForNewLanguage(y2[i]); } 
									}
								}
							}
						}
					} else {
						STGraphC.getMyMenuBar().getMenu(STMenuBar.WEBMENU_NUM).setVisible(false);
						graph.setModelLanguagesAsString(null);
					}
					STGraph.getSTC().getCurrentDesktop().getGraphFrame1().graphChanged(null);
					graph.setAsModified(true);
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
				public void actionPerformed(final ActionEvent e) {
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

}
