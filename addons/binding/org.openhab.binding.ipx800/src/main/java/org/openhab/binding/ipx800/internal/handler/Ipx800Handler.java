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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
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
public class Ipx800Handler extends BaseBridgeHandler implements Ipx800EventListener {
    private final Logger logger = LoggerFactory.getLogger(Ipx800Handler.class);

    private @NonNullByDefault({}) Ipx800Configuration configuration;
    private @NonNullByDefault({}) Ipx800DeviceConnector connector;
    private @NonNullByDefault({}) Ipx800MessageParser parser;

    private final Map<String, @Nullable Double> portValues = new HashMap<>();

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

    @Override
    public void dispose() {
        connector.disconnect();
        super.dispose();
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
                if (this.getThing().getChannel(uid.getId()) == null) {
                    ChannelBuilder cBuilder = ChannelBuilder.create(uid, PROPERTY_TYPE_MAP.get(property));
                    cBuilder.withType(new ChannelTypeUID(BINDING_ID, property));
                    cBuilder.withLabel(name);
                    tBuilder.withChannel(cBuilder.build());
                }
            }
        });

        updateThing(tBuilder.build());

        connector = new Ipx800DeviceConnector(configuration);
        parser = new Ipx800MessageParser(connector);
        parser.addEventListener(this);
        updateStatus(ThingStatus.ONLINE);
        connector.run();
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());

    }

    @Override
    public void dataReceived(String portKind, int portNumber, Double value) {

    }

    @Override
    public void errorOccurred(String error) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dataReceived(String port, Double value) {
        Double portValue = portValues.get(port);
        if (portValue == null || !portValue.equals(value)) {
            portValues.put(port, value);
            this.getThing().getChannel(BINDING_ID).postUpdate(value);
        }
    }

}
