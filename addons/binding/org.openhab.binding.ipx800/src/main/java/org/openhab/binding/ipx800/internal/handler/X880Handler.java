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

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link X880Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Seebag - Initial contribution
 * @author Gaël L'hopital - Port to OH2
 */
public class X880Handler extends ExtensionHandler {

    private Logger logger = LoggerFactory.getLogger(X880Handler.class);

    public X880Handler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Received channel: {}, command: {}", channelUID, command);

        String channel = channelUID.getId();

        logger.warn("Channel '{}' not supported", channelUID);
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        logger.debug("channelLinked: {}", channelUID);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

}
