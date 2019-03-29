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

import static org.openhab.binding.ipx800.internal.Ipx800BindingConstants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.ipx800.internal.config.Ipx800Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link Ipx800Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Seebag - Initial contribution
 * @author Gaël L'hopital - Port to OH2
 */
@NonNullByDefault
public class Ipx800Handler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(Ipx800Handler.class);

    /* Global configuration for IPX800 Thing */
    private @NonNullByDefault({}) Ipx800Configuration configuration;
    /* Connection indicator for listening thread. */
    private boolean connected = false;
    /* Client socket */
    private @NonNullByDefault({}) Socket client;
    /* The reader */
    private @NonNullByDefault({}) BufferedReader in;
    /* The writer */
    private @NonNullByDefault({}) PrintWriter out;

    public Ipx800Handler(Bridge bridge) {
        super(bridge);

        logger.debug("Create a IPX800 Handler for thing '{}'", getThing().getUID());
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
        updateStatus(ThingStatus.OFFLINE);

        configuration = getConfigAs(Ipx800Configuration.class);

        logger.debug("Initializing IPX800 handler for uid '{}'", getThing().getUID());
        scheduler.execute(this::doInitialization);
    }

    private int getPropertyValue(String propertyName) {
        return thing.getProperties().containsKey(propertyName)
                ? Integer.valueOf(thing.getProperties().get(propertyName)).intValue()
                : 0;
    }

    protected void doInitialization() {
        Thing thing = getThing();
        logger.debug("Initialize IPX800 input blocks handler.");

        ThingBuilder tBuilder = editThing();

        MODULE_PROPERTIES.forEach(property -> {
            int count = getPropertyValue(property);
            for (int i = 0; i < count; i++) {
                String name = property + String.valueOf(i + 1);
                ChannelUID uid = new ChannelUID(thing.getUID(), name);
                ChannelBuilder cBuilder = ChannelBuilder.create(uid, PROPERTY_TYPE_MAP.get(property));
                cBuilder.withType(new ChannelTypeUID(BINDING_ID, property));
                cBuilder.withLabel(name);
                tBuilder.withChannel(cBuilder.build());
            }
        });

        updateThing(tBuilder.build());
        try {
            connect();
            updateStatus(ThingStatus.ONLINE);
        } catch (IOException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }

    }

    /**
     * Connect to the ipx800
     *
     * @throws IOException
     */
    private void connect() throws IOException {
        disconnect();
        logger.debug("Connecting {}:{}...", configuration.hostname, configuration.portNumber);
        client = new Socket(configuration.hostname, configuration.portNumber);
        client.setSoTimeout(configuration.keepaliveTimeout);
        client.getInputStream().skip(client.getInputStream().available());
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        connected = true;
        logger.debug("Connected to {}:{}", configuration.hostname, configuration.portNumber);
    }

    /**
     * Disconnect the device
     */
    public void disconnect() {
        if (connected) {
            logger.debug("Disconnecting");
            try {
                client.close();
            } catch (IOException e) {
                logger.error("Unable to disconnect {}", e.getMessage());
            }
            connected = false;
            logger.debug("Disconnected");
        }
    }

}
