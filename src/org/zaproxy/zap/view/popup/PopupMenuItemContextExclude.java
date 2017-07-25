/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2014 The ZAP Development Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.view.popup;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.ExtensionPopupMenuItem;
import org.parosproxy.paros.model.Model;
import org.parosproxy.paros.model.Session;
import org.parosproxy.paros.model.SiteNode;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.model.Context;

/**
 * @since 2.3.0
 */
public class PopupMenuItemContextExclude extends PopupMenuItemSiteNodeContainer {

    private static final long serialVersionUID = 2282358266003940700L;

    private List<ExtensionPopupMenuItem> subMenus = new ArrayList<>();

    public PopupMenuItemContextExclude() {
        super("excludeFromContextX", true);
    }

    @Override
    public String getParentMenuName() {
        return Constant.messages.getString("context.exclude.popup");
    }

    @Override
    public boolean isSubMenu() {
        return true;
    }

    @Override
    public boolean isDummyItem() {
        return true;
    }

    @Override
    public void performAction(SiteNode sn) {
        // Do nothing
    }

    @Override
    public boolean isButtonEnabledForSiteNode(SiteNode sn) {
        reCreateSubMenu();
        return false;
    }

    protected void reCreateSubMenu() {
        final List<JMenuItem> mainPopupMenuItems = View.getSingleton().getPopupList();
        for (ExtensionPopupMenuItem menu : subMenus) {
            mainPopupMenuItems.remove(menu);
        }
        subMenus.clear();

        Session session = Model.getSingleton().getSession();
        List<Context> contexts = session.getContexts();
        for (Context context : contexts) {
            ExtensionPopupMenuItem piicm = createPopupExcludeFromContextMenu(context);
            piicm.setMenuIndex(this.getMenuIndex());
            mainPopupMenuItems.add(piicm);
            this.subMenus.add(piicm);
        }
    }

    protected ExtensionPopupMenuItem createPopupExcludeFromContextMenu(Context context) {
        return new PopupMenuItemExcludeFromContext(context);
    }

    @Override
    public boolean isSafe() {
        return true;
    }

}
