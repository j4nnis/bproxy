/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Copyright 2015 The ZAP Development Team
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
package org.zaproxy.zap;

import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.DefaultFileSystem;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import org.parosproxy.paros.CommandLine;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.Model;
import org.zaproxy.zap.control.ControlOverrides;

/**
 * ZAP's bootstrap process.
 * <p>
 * The bootstrap process consists, basically, in initialising the singletons {@code Model}, {@code Control} and, for
 * non-headless modes, the {@code View} and handling of command line arguments.
 * 
 * @since 2.4.2
 * @see Model
 * @see org.parosproxy.paros.control.Control
 * @see org.parosproxy.paros.view.View
 */
abstract class ZapBootstrap {

    private final CommandLine args;
    private final ControlOverrides controlOverrides;

    public ZapBootstrap(CommandLine args) {
        this.args = args;

        controlOverrides = new ControlOverrides();
        controlOverrides.setProxyPort(getArgs().getPort());
        controlOverrides.setProxyHost(getArgs().getHost());
        controlOverrides.setConfigs(getArgs().getConfigs());
        controlOverrides.setExperimentalDb(getArgs().isExperimentalDb());
    }

    /**
     * Starts the bootstrap process.
     *
     * @return the return code of the program
     */
    public int start() {
        // Nasty hack to prevent warning messages when running from the command line
        NullAppender na = new NullAppender();
        Logger.getRootLogger().addAppender(na);
        Logger.getRootLogger().setLevel(Level.OFF);
        Logger.getLogger(ConfigurationUtils.class).addAppender(na);
        Logger.getLogger(DefaultFileSystem.class).addAppender(na);

        try {
            Constant.getInstance();
        } catch (final Throwable e) {
            System.err.println(e.getMessage());
            return 1;
        }

        Constant.setLowMemoryOption(getArgs().isLowMem());

        return 0;
    }

    /**
     * Initialises the model, all bootstrap implementations should call this method after base {@code start()}.
     *
     * @throws Exception if an error occurs while initialising the {@code Model}
     * @see #start()
     * @see Model
     */
    protected void initModel() throws Exception {
        Model.getSingleton().init(getControlOverrides());
        Model.getSingleton().getOptionsParam().setGUI(getArgs().isGUI());
    }

    /**
     * Gets the command line arguments.
     *
     * @return the command line arguments
     */
    protected CommandLine getArgs() {
        return args;
    }

    /**
     * Gets the control overrides. Necessary for initialising {@code Control} and {@code Model} singletons.
     *
     * @return the control overrides
     * @see org.parosproxy.paros.control.Control
     * @see Model
     */
    protected ControlOverrides getControlOverrides() {
        return controlOverrides;
    }

    /**
     * Gets ZAP's starting message.
     *
     * @return the starting message
     */
    protected static String getStartingMessage() {
        return Constant.PROGRAM_NAME + " " + Constant.PROGRAM_VERSION + " started.";
    }
}
