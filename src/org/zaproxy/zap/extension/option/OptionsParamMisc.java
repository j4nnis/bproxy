/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2010 psiinon@gmail.com
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

package org.zaproxy.zap.extension.option;

import org.parosproxy.paros.common.AbstractParam;

public class OptionsParamMisc extends AbstractParam {

	private static final String SAVE_DIR = "misc.saveDir";

	private String saveDirectory = "";
	
    public OptionsParamMisc() {
    }

    @Override
    protected void parse() {
        

	    // use temp variable to check.  Exception will be flagged if any error.
	    saveDirectory = getConfig().getString(SAVE_DIR);
		
    }

	/**
	 * @return Returns the skipImage.
	 */
	public String getSaveDirectory() {
		return saveDirectory;
	}
	
	/**
	 * @param saveDirectory processImages 0 = not to process.  Other = process images
	 */
	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
		getConfig().setProperty(SAVE_DIR, saveDirectory);
	}
}
