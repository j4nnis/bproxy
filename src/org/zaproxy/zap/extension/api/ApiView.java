/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
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
package org.zaproxy.zap.extension.api;

import java.util.List;

public class ApiView extends ApiElement {

	public ApiView(String name) {
		super(name);
	}

	public ApiView(String name, List<String> mandatoryParamNames, List<String> optionalParamNames) {
		super(name, mandatoryParamNames, optionalParamNames);
	}
	
	public ApiView(String name, String[] mandatoryParamNames, String[] optionalParamNames) {
		super(name, mandatoryParamNames, optionalParamNames);
	}
	
	public ApiView(String name, List<String> mandatoryParamNames) {
		super(name, mandatoryParamNames);
	}
	
	public ApiView(String name, String[] mandatoryParamNames) {
		super(name, mandatoryParamNames);
	}
}
