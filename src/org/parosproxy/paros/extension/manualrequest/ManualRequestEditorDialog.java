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

// ZAP: 2011/08/04 Changed to support new Features
// ZAP: 2011/08/04 Changed to support new interface
// ZAP: 2012/03/15 Changed so the display options can be modified dynamically.
// ZAP: 2012/07/02 Wraps no HttpMessage object, but more generalized Message.
// new map of supported message types; removed history list; removed unused
// methods.
// ZAP: 2012/07/16 Issue 326: Add response time and total length to manual request dialog 
// ZAP: 2012/07/31 Removed the instance variables followRedirect,
// useTrackingSessionState and httpSender. Removed the methods getHttpSender,
// getButtonFollowRedirect and getButtonUseTrackingSessionState and changed the
// methods windowClosing and setVisible.
// ZAP: 2012/08/01 Issue 332: added support for Modes
// ZAP: 2012/11/21 Heavily refactored extension to support non-HTTP messages.
// ZAP: 2013/05/02 Re-arranged all modifiers into Java coding standard order
// ZAP: 2014/01/28 Issue 207: Support keyboard shortcuts 

package org.parosproxy.paros.extension.manualrequest;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.control.Control.Mode;
import org.parosproxy.paros.extension.option.OptionsParamView;
import org.parosproxy.paros.view.AbstractFrame;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.httppanel.HttpPanelRequest;
import org.zaproxy.zap.extension.httppanel.Message;
import org.zaproxy.zap.extension.tab.Tab;
import org.zaproxy.zap.view.ZapMenuItem;

/**
 * Send custom crafted messages via HTTP or other TCP based protocols. 
 */
public abstract class ManualRequestEditorDialog extends AbstractFrame implements Tab {
	private static final long serialVersionUID = 1L;
	
    private static final Logger logger = Logger.getLogger(ManualRequestEditorDialog.class);
	
	private boolean isSendEnabled = true;
	
	protected String configurationKey;
	
	private JPanel panelWindow = null;

	private JButton btnSend = null;

	/**
	 * Non-abstract classes should call {@link #initialize()} in their constructor.
	 * 
	 * @param isSendEnabled
	 * @param configurationKey
	 * @throws HeadlessException
	 */
	public ManualRequestEditorDialog(boolean isSendEnabled, String configurationKey) throws HeadlessException {
		super();
		
		this.isSendEnabled = isSendEnabled;
		this.configurationKey = OptionsParamView.BASE_VIEW_KEY + "." + configurationKey + ".";
		
		this.setPreferredSize(new Dimension(700, 800));
	}

	protected void initialize() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				getMessageSender().cleanup();
				saveConfig();
			}
		});
		
		setContentPane(getWindowPanel());
	}

	/**
	 * Returns type of message it handles.
	 * 
	 * @return
	 */
	public abstract Class<? extends Message> getMessageType();
	
	/**
	 * Message sender for the given {@link #getMessageType()}.
	 * 
	 * @return
	 */
	protected abstract MessageSender getMessageSender();
	
	/**
	 * Menu item that calls this editor.
	 * 
	 * @return
	 */
	public abstract ZapMenuItem getMenuItem();

	protected JPanel getWindowPanel() {
		if (panelWindow == null) {
			panelWindow = new JPanel();
			panelWindow.setLayout(new BorderLayout());
			
			panelWindow.add(getManualSendPanel());
		}

		return panelWindow;
	}
	
	protected abstract Component getManualSendPanel();
	
	@Override
	public void setVisible(boolean show) {
		if (!show && getMessageSender() != null) {
           getMessageSender().cleanup();
		}
		
		super.setVisible(show);
	}

	public abstract void setDefaultMessage();
	
	public abstract void setMessage(Message aMessage);

	public abstract Message getMessage();

	public void clear() {
		getRequestPanel().clearView();
	}

	protected JButton getBtnSend() {
		if (btnSend == null) {
			btnSend = new JButton();
			btnSend.setText(Constant.messages.getString("manReq.button.send"));
			btnSend.setEnabled(isSendEnabled);
			btnSend.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					btnSend.setEnabled(false);
					
					// save current message (i.e. set payload/body)
					getRequestPanel().saveData();
					
					Mode mode = Control.getSingleton().getMode();
					if (mode.equals(Mode.safe)) {
						// Can happen if the user turns on safe mode with the dialog open
						View.getSingleton().showWarningDialog(Constant.messages.getString("manReq.safe.warning"));
						btnSend.setEnabled(true);
						return;
					} else if (mode.equals(Mode.protect)) {
						if (!getMessage().isInScope()) {
							// In protected mode and not in scope, so fail
							View.getSingleton().showWarningDialog(Constant.messages.getString("manReq.outofscope.warning"));
							btnSend.setEnabled(true);
							return;
						}
					}
					
					btnSendAction();
				}
			});
		}
		return btnSend;
	}
	
	/**
	 * Do not forget to enable the send button again i
	 */
	protected abstract void btnSendAction();

	protected void send(final Message aMessage) {
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                	getMessageSender().handleSendMessage(aMessage);                    
                    postSend();
                } catch (Exception e) {
                	logger.warn(e.getMessage(), e);
                    View.getSingleton().showWarningDialog(e.getMessage());
                } finally {
                    btnSend.setEnabled(true);
                }
            }
        });
		t.setPriority(Thread.NORM_PRIORITY);
		t.start();
	}

	protected void postSend() {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				// redraw, as message may have changed after sending
				getRequestPanel().updateContent();
			}
		});
	}
	
	protected abstract void saveConfig();

	protected abstract HttpPanelRequest getRequestPanel();
}