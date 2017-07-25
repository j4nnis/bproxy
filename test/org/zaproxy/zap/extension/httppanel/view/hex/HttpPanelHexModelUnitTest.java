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
package org.zaproxy.zap.extension.httppanel.view.hex;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class HttpPanelHexModelUnitTest {
	
	private HttpPanelHexModel model;
	
	@Before
	public void setup() {
		model = new HttpPanelHexModel();
	}
	
	@Test
	public void testGetData() {
		// setting value 'a0' in Hex view resulted in 'c2 a0'
		model.setData(new byte[]{(byte) 0xa0});

		assertArrayEquals(new byte[]{(byte) 0xa0}, model.getData());
	}
}
