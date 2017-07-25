/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2013 The ZAP Development Team
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
package org.zaproxy.zap.extension.authorization;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.db.RecordContext;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.Session;
import org.zaproxy.zap.model.Context;
import org.zaproxy.zap.model.ContextDataFactory;
import org.zaproxy.zap.view.AbstractContextPropertiesPanel;
import org.zaproxy.zap.view.ContextPanelFactory;

/**
 * The Authorization Extension allows ZAP users to define how authorized/unauthorized requests to
 * web applications are identified.
 */
public class ExtensionAuthorization extends ExtensionAdaptor implements ContextPanelFactory,
		ContextDataFactory {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(ExtensionAuthorization.class);

	/** The NAME of the extension. */
	public static final String NAME = "ExtensionAuthorization";

	/** The map of context panels. */
	private Map<Integer, ContextAuthorizationPanel> contextPanelsMap = new HashMap<>();

	/**
	 * Instantiates the extension.
	 */
	public ExtensionAuthorization() {
		super();
		initialize();
	}

	/**
	 * Initialize the extension.
	 */
	private void initialize() {
		this.setName(NAME);
		this.setOrder(205);
	}

	@Override
	public void hook(ExtensionHook extensionHook) {
		super.hook(extensionHook);

		// Register this where needed
		Model.getSingleton().addContextDataFactory(this);

		if (getView() != null) {
			// Factory for generating Session Context UserAuth panels
			getView().addContextPanelFactory(this);
		}

		extensionHook.addApiImplementor(new AuthorizationAPI());
	}

	@Override
	public AbstractContextPropertiesPanel getContextPanel(Context context) {
		ContextAuthorizationPanel panel = this.contextPanelsMap.get(context.getIndex());
		if (panel == null) {
			panel = new ContextAuthorizationPanel(this, context.getIndex());
			this.contextPanelsMap.put(context.getIndex(), panel);
		}
		return panel;
	}

	@Override
	public void discardContexts() {
		this.contextPanelsMap.clear();
	}

	@Override
	public void discardContext(Context ctx) {
		this.contextPanelsMap.remove(ctx.getIndex());
	}

	@Override
	public void loadContextData(Session session, Context context) {
		try {
			List<String> loadedData = session.getContextDataStrings(context.getIndex(),
					RecordContext.TYPE_AUTHORIZATION_METHOD_TYPE);
			if (loadedData != null && loadedData.size() > 0) {
				int type = Integer.parseInt(loadedData.get(0));
				// Based on the type, call the appropriate method loader
				switch (type) {
				case BasicAuthorizationDetectionMethod.METHOD_UNIQUE_ID:
					context.setAuthorizationDetectionMethod(BasicAuthorizationDetectionMethod
							.loadMethodFromSession(session, context.getIndex()));
					break;
				}
			}
		} catch (DatabaseException e) {
			log.error("Unable to load Authorization Detection method.", e);
		}
	}

	@Override
	public void persistContextData(Session session, Context context) {
		try {
			// Persist the method type first and then the method data itself
			int type = context.getAuthorizationDetectionMethod().getMethodUniqueIdentifier();
			session.setContextData(context.getIndex(), RecordContext.TYPE_AUTHORIZATION_METHOD_TYPE,
					Integer.toString(type));
			context.getAuthorizationDetectionMethod().persistMethodToSession(session, context.getIndex());
		} catch (DatabaseException e) {
			log.error("Unable to persist Authorization Detection method.", e);
		}
	}

	@Override
	public URL getURL() {
		try {
			return new URL(Constant.ZAP_HOMEPAGE);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public String getAuthor() {
		return Constant.ZAP_TEAM;
	}

	@Override
	public void exportContextData(Context ctx, Configuration config) {
		config.setProperty(AuthorizationDetectionMethod.CONTEXT_CONFIG_AUTH_TYPE, ctx
				.getAuthorizationDetectionMethod().getMethodUniqueIdentifier());
		ctx.getAuthorizationDetectionMethod().exportMethodData(config);
	}

	@Override
	public void importContextData(Context ctx, Configuration config) throws ConfigurationException {
		int type = config.getInt(AuthorizationDetectionMethod.CONTEXT_CONFIG_AUTH_TYPE);
		switch (type) {
		case BasicAuthorizationDetectionMethod.METHOD_UNIQUE_ID:
			ctx.setAuthorizationDetectionMethod(new BasicAuthorizationDetectionMethod(config));
			break;
		}
	}

}
