/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright The ZAP Development Team
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
package org.zaproxy.zap.model;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.parosproxy.paros.model.SiteNode;

public class StructuralSiteNodeIterator implements Iterator<StructuralNode> {

	private Enumeration<SiteNode> children;
	
	@SuppressWarnings("unchecked")
	public StructuralSiteNodeIterator(StructuralSiteNode parent){
		children = parent.getSiteNode().children();
	}
	
	@Override
	public boolean hasNext() {
		return children.hasMoreElements();
	}

	@Override
	public StructuralSiteNode next() {
		if (! hasNext()) {
			throw new NoSuchElementException();
		}
		return new StructuralSiteNode(children.nextElement());
	}

	@Override
	public void remove() {
		// TODO 
	}
}
