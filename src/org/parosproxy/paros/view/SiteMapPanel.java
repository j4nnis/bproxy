/*
 *
 * Paros and its related class files.
 * 
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 Chinotec Technologies Company
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Clarified Artistic License
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Clarified Artistic License for more details.
 * 
 * You should have received a copy of the Clarified Artistic License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
// ZAP: 2012/03/03 Moved popups to stdmenus extension
// ZAP: 2012/03/15 Changed to initiate the tree with a default model. Changed to
// clear the http panels when the root node is selected.
// ZAP: 2012/04/23 Added @Override annotation to all appropriate methods.
// ZAP: 2012/06/13 Added custom tree cell renderer to treeSite in getTreeSite().
// ZAP: 2013/01/25 Added method for removing listener.
// ZAP: 2013/11/16 Issue 886: Main pop up menu invoked twice on some components
// ZAP: 2014/01/28 Issue 207: Support keyboard shortcuts 
// ZAP: 2014/03/23 Tidy up, removed the instance variable rootTreePath, no need to
// cache the path
// ZAP: 2014/03/23 Issue 609: Provide a common interface to query the state and 
// access the data (HttpMessage and HistoryReference) displayed in the tabs
// ZAP: 2014/10/07 Issue 1357: Hide unused tabs
// ZAP: 2014/12/17 Issue 1174: Support a Site filter
// ZAP: 2014/12/22 Issue 1476: Display contexts in the Sites tree
// ZAP: 2015/02/09 Issue 1525: Introduce a database interface layer to allow for alternative implementations
// ZAP: 2015/02/10 Issue 1528: Support user defined font size
// ZAP: 2015/06/01 Issue 1653: Support context menu key for trees
// ZAP: 2016/04/14 Use View to display the HTTP messages
// ZAP: 2016/07/01 Issue 2642: Slow mouse wheel scrolling in site tree

package org.parosproxy.paros.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.extension.history.LogPanel;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.SiteMap;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.history.HistoryFilterPlusDialog;
import org.zaproxy.zap.model.Context;
import org.zaproxy.zap.model.Target;
import org.zaproxy.zap.utils.DisplayUtils;
import org.zaproxy.zap.view.ContextCreateDialog;
import org.zaproxy.zap.view.ContextGeneralPanel;
import org.zaproxy.zap.view.ContextsSitesPanel;
import org.zaproxy.zap.view.ContextsTreeCellRenderer;
import org.zaproxy.zap.view.LayoutHelper;
import org.zaproxy.zap.view.SiteMapListener;
import org.zaproxy.zap.view.SiteMapTreeCellRenderer;
import org.zaproxy.zap.view.SiteTreeFilter;
import org.zaproxy.zap.view.ZapToggleButton;
import org.zaproxy.zap.view.messagecontainer.http.DefaultSelectableHistoryReferencesContainer;
import org.zaproxy.zap.view.messagecontainer.http.SelectableHistoryReferencesContainer;


public class SiteMapPanel extends AbstractPanel {

	public static final String CONTEXT_TREE_COMPONENT_NAME	= "ContextTree";
	
	private static final long serialVersionUID = -3161729504065679088L;

	// ZAP: Added logger
    private static Logger log = Logger.getLogger(SiteMapPanel.class);

	private JTree treeSite = null;
	private JTree treeContext = null;
	private DefaultTreeModel contextTree = null;
	private View view = null;

	private javax.swing.JToolBar panelToolbar = null;
	private ZapToggleButton scopeButton = null;
	private JButton filterButton = null;
	private JLabel filterStatus = null;
	private HistoryFilterPlusDialog filterPlusDialog = null;
	private JButton createContextButton = null;
	private JButton importContextButton = null;
	private JButton exportContextButton = null;

	// ZAP: Added SiteMapListenners
	private List<SiteMapListener> listeners = new ArrayList<>();
	
	/**
	 * This is the default constructor
	 */
	public SiteMapPanel() {
		super();
		initialize();
	}

	private View getView() {
	    if (view == null) {
	        view = View.getSingleton();
	    }
	    
	    return view;
	}
	/**
	 * This method initializes this
	 */
	private  void initialize() {
		this.setHideable(false);
	    this.setIcon(new ImageIcon(View.class.getResource("/resource/icon/16/094.png")));
	    this.setName(Constant.messages.getString("sites.panel.title"));
		this.setDefaultAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK, false));
		this.setMnemonic(Constant.messages.getChar("sites.panel.mnemonic"));

	    if (Model.getSingleton().getOptionsParam().getViewParam().getWmUiHandlingOption() == 0) {
	    	this.setSize(300,200);
	    }
		
		this.setLayout(new GridBagLayout());
		this.add(this.getPanelToolbar(), LayoutHelper.getGBC(0, 0, 1, 0, new Insets(2,2,2,2)));
		this.add(new ContextsSitesPanel(getTreeContext(), getTreeSite(), "sitesPanelScrollPane"), 
				LayoutHelper.getGBC(0, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, new Insets(2,2,2,2)));

        expandRoot();
	}
	
	private javax.swing.JToolBar getPanelToolbar() {
		if (panelToolbar == null) {
			
			panelToolbar = new javax.swing.JToolBar();
			panelToolbar.setLayout(new GridBagLayout());
			panelToolbar.setEnabled(true);
			panelToolbar.setFloatable(false);
			panelToolbar.setRollover(true);
			panelToolbar.setPreferredSize(new Dimension(800,30));
			panelToolbar.setName("ScriptsListToolbar");
			
			int i = 1;
			panelToolbar.add(getScopeButton(), LayoutHelper.getGBC(i++, 0, 1, 0.0D));
			panelToolbar.add(getCreateContextButton(), LayoutHelper.getGBC(i++, 0, 1, 0.0D));
			panelToolbar.add(getImportContextButton(), LayoutHelper.getGBC(i++, 0, 1, 0.0D));
			panelToolbar.add(getExportContextButton(), LayoutHelper.getGBC(i++, 0, 1, 0.0D));
			
			// TODO Disabled for now due to problems with scrolling with sparcely populated filtered trees
			//panelToolbar.add(getFilterButton(), LayoutHelper.getGBC(i++, 0, 1, 0.0D));
			//panelToolbar.add(getFilterStatus(), LayoutHelper.getGBC(i++, 0, 1, 0.0D));
			panelToolbar.add(new JLabel(), LayoutHelper.getGBC(20, 0, 1, 1.0D));	// spacer
		}
		return panelToolbar;
	}

	private HistoryFilterPlusDialog getFilterPlusDialog() {
		if (filterPlusDialog == null) {
			filterPlusDialog = new HistoryFilterPlusDialog(getView().getMainFrame(), true);
			// Override the title as we're reusing the history filter dialog
			filterPlusDialog.setTitle(Constant.messages.getString("sites.filter.title"));
		}
		return filterPlusDialog;
	}
	
	private JLabel getFilterStatus() {
		filterStatus = new JLabel(Constant.messages.getString("history.filter.label.filter") + 
				Constant.messages.getString("history.filter.label.off"));
		return filterStatus;
	}

	private JButton getFilterButton() {
		if (filterButton == null) {
			filterButton = new JButton();
			filterButton.setIcon(DisplayUtils.getScaledIcon(
					new ImageIcon(LogPanel.class.getResource("/resource/icon/16/054.png"))));	// 'filter' icon
			filterButton.setToolTipText(Constant.messages.getString("history.filter.button.filter"));

			filterButton.addActionListener(new java.awt.event.ActionListener() { 

				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showFilterPlusDialog();
				}
			});
		}
		return filterButton;
	}
	
	private JButton getCreateContextButton() {
		if (createContextButton == null) {
			createContextButton = new JButton();
			createContextButton.setIcon(DisplayUtils.getScaledIcon(new ImageIcon(
					LogPanel.class.getResource("/resource/icon/fugue/application-blue-plus.png"))));
			createContextButton.setToolTipText(Constant.messages.getString("menu.file.context.create"));
			createContextButton.addActionListener(new java.awt.event.ActionListener() { 
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ContextCreateDialog ccd = new ContextCreateDialog(View.getSingleton().getMainFrame());
					ccd.setVisible(true);
				}
			});
		}
		return createContextButton;
	}

	private JButton getImportContextButton() {
		if (importContextButton == null) {
			importContextButton = new JButton();
			importContextButton.setIcon(DisplayUtils.getScaledIcon(new ImageIcon(
					LogPanel.class.getResource("/resource/icon/fugue/application-blue-import.png"))));
			importContextButton.setToolTipText(Constant.messages.getString("menu.file.context.import"));
			importContextButton.addActionListener(new java.awt.event.ActionListener() { 
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Control.getSingleton().getMenuFileControl().importContext();
				}
			});
		}
		return importContextButton;
	}

	private JButton getExportContextButton() {
		if (exportContextButton == null) {
			exportContextButton = new JButton();
			exportContextButton.setIcon(DisplayUtils.getScaledIcon(new ImageIcon(
					LogPanel.class.getResource("/resource/icon/fugue/application-blue-export.png"))));
			exportContextButton.setToolTipText(Constant.messages.getString("menu.file.context.export"));
			exportContextButton.addActionListener(new java.awt.event.ActionListener() { 
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Control.getSingleton().getMenuFileControl().exportContext();
				}
			});
		}
		return exportContextButton;
	}

	private void showFilterPlusDialog() {
		HistoryFilterPlusDialog dialog = getFilterPlusDialog();
		dialog.setModal(true);
    	try {
			dialog.setAllTags(Model.getSingleton().getDb().getTableTag().getAllTags());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
		}

		int exit = dialog.showDialog();
		SiteTreeFilter filter = new SiteTreeFilter(dialog.getFilter());
		filter.setInScope(this.getScopeButton().isSelected());
		if (exit != JOptionPane.CANCEL_OPTION) {
			setFilter();
		}
	}
	
	private void setFilter() {
		SiteTreeFilter filter = new SiteTreeFilter(getFilterPlusDialog().getFilter());
		filter.setInScope(scopeButton.isSelected());
		((SiteMap)treeSite.getModel()).setFilter(filter);
		((DefaultTreeModel)treeSite.getModel()).nodeStructureChanged((SiteNode)treeSite.getModel().getRoot());
    	getFilterStatus().setText(filter.toShortString());
    	getFilterStatus().setToolTipText(filter.toLongString());
		expandRoot();
		
		// Remove any out of scope contexts too
		this.reloadContextTree();
	}

	private JToggleButton getScopeButton() {
		if (scopeButton == null) {
			scopeButton = new ZapToggleButton();
			scopeButton.setIcon(DisplayUtils.getScaledIcon(new ImageIcon(SiteMapPanel.class.getResource("/resource/icon/fugue/target-grey.png"))));
			scopeButton.setToolTipText(Constant.messages.getString("history.scope.button.unselected"));
			scopeButton.setSelectedIcon(DisplayUtils.getScaledIcon(new ImageIcon(SiteMapPanel.class.getResource("/resource/icon/fugue/target.png"))));
			scopeButton.setSelectedToolTipText(Constant.messages.getString("history.scope.button.selected"));

			scopeButton.addActionListener(new java.awt.event.ActionListener() { 
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setFilter();
				}
			});
		}
		return scopeButton;
	}

	/**
	 * This method initializes treeSite	
	 * 	
	 * @return javax.swing.JTree	
	 */    
	public JTree getTreeSite() {
		if (treeSite == null) {
			treeSite = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode()));
			treeSite.setShowsRootHandles(true);
			treeSite.setName("treeSite");
			treeSite.setToggleClickCount(1);

			treeSite.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() { 

				@Override
				public void valueChanged(javax.swing.event.TreeSelectionEvent e) {    

				    SiteNode node = (SiteNode) treeSite.getLastSelectedPathComponent();
				    if (node == null) {
				        return;
				    }
				    if (!node.isRoot()) {
				    	HttpMessage msg = null;
                        try {
                            msg = node.getHistoryReference().getHttpMessage();
                        } catch (Exception e1) {
                        	// ZAP: Log exceptions
                        	log.warn(e1.getMessage(), e1);
                            return;
                            
                        }

                        getView().displayMessage(msg);

			        	// ZAP: Call SiteMapListenners
			            for (SiteMapListener listener : listeners) {
			            	listener.nodeSelected(node);
			            }
				    } else {
				    	// ZAP: clear the views when the root is selected
				        getView().displayMessage(null);
				    }
	
				}
			});
	        treeSite.setComponentPopupMenu(new SitesCustomPopupMenu());

			// ZAP: Add custom tree cell renderer.
	        DefaultTreeCellRenderer renderer = new SiteMapTreeCellRenderer(listeners);
			treeSite.setCellRenderer(renderer);
		}
		return treeSite;
	}
	
	
	public void reloadContextTree() {
		SiteNode root;
		if (this.contextTree == null) {
			root = new SiteNode(null, -1, Constant.messages.getString("context.list"));
			this.contextTree = new DefaultTreeModel(root);
		} else {
			root = (SiteNode)this.contextTree.getRoot();
			root.removeAllChildren();
		}
		for (Context ctx : Model.getSingleton().getSession().getContexts()) {
			if (ctx.isInScope() || ! this.getScopeButton().isSelected()) {
				// Add all in scope contexts, and out of scope ones if scope button not pressed
	            SiteNode node = new SiteNode(null, HistoryReference.TYPE_PROXIED, ctx.getName());
	            node.setUserObject(new Target(ctx));
				root.add(node);
			}
		}
		this.contextTree.nodeStructureChanged(root);
	}

	private JTree getTreeContext() {
		if (treeContext == null) {
			reloadContextTree();
			treeContext = new JTree(this.contextTree);
			treeContext.setShowsRootHandles(true);
			treeContext.setName(CONTEXT_TREE_COMPONENT_NAME);
			treeContext.setToggleClickCount(1);
			treeContext.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			
			treeContext.addMouseListener(new java.awt.event.MouseAdapter() { 
				@Override
				public void mousePressed(java.awt.event.MouseEvent e) {
				}
					
				@Override
				public void mouseReleased(java.awt.event.MouseEvent e) {
					mouseClicked(e);
				}
				
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
				    if (treeSite.getLastSelectedPathComponent() != null) {
				    	// They selected a context node, deselect any context
				    	getTreeSite().clearSelection();
				    }
				    if (e.getClickCount() > 1) {
				    	// Its a double click - show the relevant context dialog
					    SiteNode node = (SiteNode) treeContext.getLastSelectedPathComponent();
					    if (node != null && node.getUserObject() != null) {
					    	Target target = (Target)node.getUserObject();
					    	getView().showSessionDialog(Model.getSingleton().getSession(), 
					    			ContextGeneralPanel.getPanelName(target.getContext()));
					    }
				    }
				}
			});
	        treeContext.setComponentPopupMenu(new ContextsCustomPopupMenu());

			treeContext.setCellRenderer(new ContextsTreeCellRenderer());
		}
		return treeContext;
	}

	public void expandRoot() {
        TreeNode root = (TreeNode) treeSite.getModel().getRoot();
        if (root == null) {
            return;
        }
        final TreePath rootTreePath = new TreePath(root);
	    
		if (EventQueue.isDispatchThread()) {
		    getTreeSite().expandPath(rootTreePath);
		    return;
		}
		try {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
				    getTreeSite().expandPath(rootTreePath);
				}
			});
		} catch (Exception e) {
        	// ZAP: Log exceptions
        	log.warn(e.getMessage(), e);
		}
	}
	
	// ZAP: Added addSiteMapListenners
	public void addSiteMapListener(SiteMapListener listenner) {
		this.listeners.add(listenner);
	}

	public void removeSiteMapListener(SiteMapListener listener) {
		this.listeners.remove(listener);
	}
	
	public void showInSites (SiteNode node) {
		TreeNode[] path = node.getPath();
		TreePath tp = new TreePath(path);
		treeSite.setExpandsSelectedPaths(true);
		treeSite.setSelectionPath(tp);
		treeSite.scrollPathToVisible(tp);
	}

	public void contextChanged(Context c) {
		getTreeContext();
		SiteNode root = (SiteNode)this.contextTree.getRoot();
		for (int i=0; i < root.getChildCount(); i++) {
			SiteNode node = (SiteNode)root.getChildAt(i);
	    	Target target = (Target)node.getUserObject();
	    	if (c.getIndex() == target.getContext().getIndex()) {
	    		target.setContext(c);
	    		if (node.getNodeName().equals(c.getName())) {
	    			this.contextTree.nodeChanged(node);
	    		} else {
	    			this.reloadContextTree();
	    		}
	    		break;
	    	}
		}
	}
	
    protected class SitesCustomPopupMenu extends JPopupMenu {
        private static final long serialVersionUID = 1L;

        @Override
        public void show(Component invoker, int x, int y) {
    		// ZAP: Select site list item on right click / menu key
        	TreePath tp = treeSite.getPathForLocation( x, y );
        	if ( tp != null ) {
        		boolean select = true;
        		// Only select a new item if the current item is not
        		// already selected - this is to allow multiple items
        		// to be selected
    	    	if (treeSite.getSelectionPaths() != null) {
    	    		for (TreePath t : treeSite.getSelectionPaths()) {
    	    			if (t.equals(tp)) {
    	    				select = false;
    	    				break;
    	    			}
    	    		}
    	    	}
    	    	if (select) {
    	    		treeSite.getSelectionModel().setSelectionPath(tp);
    	    	}
        	}

            final int countSelectedNodes = treeSite.getSelectionCount();
            final List<HistoryReference> historyReferences = new ArrayList<>(countSelectedNodes);
            if (countSelectedNodes > 0) {
                for (TreePath path : treeSite.getSelectionPaths()) {
                    final SiteNode node = (SiteNode) path.getLastPathComponent();
                    final HistoryReference historyReference = node.getHistoryReference();
                    if (historyReference != null) {
                        historyReferences.add(historyReference);
                    }
                }
            }
            SelectableHistoryReferencesContainer messageContainer = new DefaultSelectableHistoryReferencesContainer(
                    treeSite.getName(),
                    treeSite,
                    Collections.<HistoryReference> emptyList(),
                    historyReferences);
    		View.getSingleton().getPopupMenu().show(messageContainer, x, y);
        }

    }

    protected class ContextsCustomPopupMenu extends JPopupMenu {
        private static final long serialVersionUID = 1L;

        @Override
        public void show(Component invoker, int x, int y) {
    		// Select context list item on right click
        	TreePath tp = treeContext.getPathForLocation(x, y);
        	if ( tp != null ) {
        		boolean select = true;
        		// Only select a new item if the current item is not
        		// already selected - this is to allow multiple items
        		// to be selected
    	    	if (treeContext.getSelectionPaths() != null) {
    	    		for (TreePath t : treeContext.getSelectionPaths()) {
    	    			if (t.equals(tp)) {
    	    				select = false;
    	    				break;
    	    			}
    	    		}
    	    	}
    	    	if (select) {
    	    		treeContext.getSelectionModel().setSelectionPath(tp);
    	    	}
        	}
    		View.getSingleton().getPopupMenu().show(treeContext, x, y);
        }
    	
    }

}
