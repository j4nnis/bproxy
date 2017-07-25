/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2011 The Zed Attack Proxy team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.pscan;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.core.scanner.Plugin.AlertThreshold;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.view.AbstractParamPanel;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.control.AddOn;
import org.zaproxy.zap.utils.DisplayUtils;
import org.zaproxy.zap.view.LayoutHelper;

public class PolicyPassiveScanPanel extends AbstractParamPanel {

    private static final long serialVersionUID = 1L;
    private JTable tableTest = null;
    private JScrollPane jScrollPane = null;
    private PolicyPassiveScanTableModel passiveScanTableModel = null;
    private JComboBox<String> applyToThreshold = null;
    private JComboBox<String> applyToThresholdTarget = null;

    public PolicyPassiveScanPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setLayout(new GridBagLayout());
        if (Model.getSingleton().getOptionsParam().getViewParam().getWmUiHandlingOption() == 0) {
            this.setSize(375, 204);
        }
        this.setName(Constant.messages.getString("pscan.options.policy.title"));
        
        // 'Apply to' controls
        JPanel applyToPanel = new JPanel();
        applyToPanel.setLayout(new GridBagLayout());
        applyToPanel.add(new JLabel(Constant.messages.getString("pscan.options.policy.apply.label")), 
        		LayoutHelper.getGBC(0, 0, 1, 0.0, new Insets(2, 2, 2, 2)));
        applyToPanel.add(getApplyToThreshold(), LayoutHelper.getGBC(1, 0, 1, 0.0));
        applyToPanel.add(new JLabel(Constant.messages.getString("pscan.options.policy.thresholdTo.label")), 
        		LayoutHelper.getGBC(2, 0, 1, 0.0, new Insets(2, 2, 2, 2)));
        applyToPanel.add(getApplyToThresholdTarget(), LayoutHelper.getGBC(3, 0, 1, 0.0));
        applyToPanel.add(new JLabel(Constant.messages.getString("pscan.options.policy.rules.label")), LayoutHelper.getGBC(4, 0, 1, 0.0, new Insets(2, 2, 2, 2)));
        JButton applyThresholdButton = new JButton(Constant.messages.getString("pscan.options.policy.go.button"));
        applyThresholdButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyThreshold (strToThreshold((String)getApplyToThreshold().getSelectedItem()),
						(String)getApplyToThresholdTarget().getSelectedItem());
				getPassiveScanTableModel().fireTableDataChanged();
				
			}});
        applyToPanel.add(applyThresholdButton, LayoutHelper.getGBC(5, 0, 1, 0.0));
        applyToPanel.add(new JLabel(""), LayoutHelper.getGBC(6, 0, 1, 1.0));	// Spacer
        
        
        this.add(applyToPanel,
                LayoutHelper.getGBC(0, 0, 3, 0.0D, 0.0D, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0)));

        
        this.add(getJScrollPane(), 
        		LayoutHelper.getGBC(0, 1, 1, 1.0, 1.0,
        				GridBagConstraints.BOTH, GridBagConstraints.NORTHWEST, new Insets(0, 0, 0, 0)));
    }

    private JComboBox<String> getApplyToThreshold() {
        if (applyToThreshold == null) {
            applyToThreshold = new JComboBox<>();
            applyToThreshold.addItem(Constant.messages.getString("ascan.options.level.off"));
            applyToThreshold.addItem(Constant.messages.getString("ascan.options.level.low"));
            applyToThreshold.addItem(Constant.messages.getString("ascan.options.level.medium"));
            applyToThreshold.addItem(Constant.messages.getString("ascan.options.level.high"));
            // Might as well default to medium, cant think of anything better :/
            applyToThreshold.setSelectedItem(Constant.messages.getString("ascan.options.level.medium"));
        }
        return applyToThreshold;
    }

    private JComboBox<String> getApplyToThresholdTarget() {
        if (applyToThresholdTarget == null) {
            applyToThresholdTarget = new JComboBox<>();
            applyToThresholdTarget.addItem(Constant.messages.getString("ascan.policy.table.quality.all"));
            View view = View.getSingleton();
            applyToThresholdTarget.addItem(view.getStatusUI(AddOn.Status.release).toString());
            applyToThresholdTarget.addItem(view.getStatusUI(AddOn.Status.beta).toString());
            applyToThresholdTarget.addItem(view.getStatusUI(AddOn.Status.alpha).toString());
        }
        return applyToThresholdTarget;
    }
    
    private AlertThreshold strToThreshold(String str) {
    	if (str.equals(Constant.messages.getString("ascan.options.level.low"))) {
    		return AlertThreshold.LOW;
    	}
    	if (str.equals(Constant.messages.getString("ascan.options.level.medium"))) {
    		return AlertThreshold.MEDIUM;
    	}
    	if (str.equals(Constant.messages.getString("ascan.options.level.high"))) {
    		return AlertThreshold.HIGH;
    	}
		return AlertThreshold.OFF;
    }

    private void applyThreshold(AlertThreshold threshold, String target) {
		if (target.equals(Constant.messages.getString("ascan.policy.table.quality.all"))) {
			this.getPassiveScanTableModel().applyThresholdToAll(threshold);
		} else {
			this.getPassiveScanTableModel().applyThreshold(threshold, target);
		}
    }

    private static final int[] width = {300, 60, 100};

    /**
     * This method initializes tableTest
     *
     * @return javax.swing.JTable
     */
    private JTable getTableTest() {
        if (tableTest == null) {
            tableTest = new JTable();
            tableTest.setModel(getPassiveScanTableModel());
            tableTest.setRowHeight(DisplayUtils.getScaledSize(18));
            tableTest.setIntercellSpacing(new java.awt.Dimension(1, 1));
            tableTest.setAutoCreateRowSorter(true);
            
            //Default sort by name (column 0)
            List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>(1);
            sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
            tableTest.getRowSorter().setSortKeys(sortKeys);
            
            for (int i = 0; i < tableTest.getColumnCount()-1; i++) {
                TableColumn column = tableTest.getColumnModel().getColumn(i);
                column.setPreferredWidth(width[i]);
            }
            
            JComboBox<String> jcb1 = new JComboBox<>();
            for (AlertThreshold level : AlertThreshold.values()) {
                jcb1.addItem(Constant.messages.getString("ascan.policy.level." + level.name().toLowerCase()));
            }
            
            tableTest.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(jcb1));
        }
        
        return tableTest;
    }

    @Override
    public void initParam(Object obj) {
    	this.getPassiveScanTableModel().reset();
    }

    @Override
    public void validateParam(Object obj) throws Exception {
    }

    @Override
    public void saveParam(Object obj) throws Exception {
    	this.getPassiveScanTableModel().persistChanges();
    }

    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getTableTest());
            jScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        }
        
        return jScrollPane;
    }

    /**
     * This method initializes categoryTableModel
     *
     * @return org.parosproxy.paros.plugin.scanner.CategoryTableModel
     */
    public PolicyPassiveScanTableModel getPassiveScanTableModel() {
        if (passiveScanTableModel == null) {
            passiveScanTableModel = new PolicyPassiveScanTableModel();
        }

        return passiveScanTableModel;
    }

    public void setPassiveScanTableModel(PolicyPassiveScanTableModel categoryTableModel) {
        this.passiveScanTableModel = categoryTableModel;
    }

    @Override
    public String getHelpIndex() {
        return "ui.dialogs.options.pscanrules";
    }
}
