/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.ipx800.internal.handler;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.openhab.binding.ipx800.internal.config.ExtensionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ExtensionHandler} is responsible for handling common behaviours of
 * all IPX800 expansion devices
 *
 * @author Gaël L'hopital - Initial contribution
 */
public abstract class ExtensionHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(ExtensionHandler.class);
    protected Ipx800Handler bridgeHandler;
    protected ExtensionConfiguration configuration;

    public ExtensionHandler(Thing thing) {
        super(thing);
        logger.debug("Create a X880 Handler for thing '{}'", getThing().getUID());
    }

    protected Ipx800Handler getBridgeHandler() {
        if (bridgeHandler == null) {
            Bridge bridge = getBridge();
            if (bridge != null) {
                bridgeHandler = (Ipx800Handler) bridge.getHandler();
            }
        }
        return bridgeHandler;
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.OFFLINE);
        configuration = getConfigAs(ExtensionConfiguration.class);

        logger.debug("Initializing extension handler for uid '{}'", getThing().getUID());
    }

}