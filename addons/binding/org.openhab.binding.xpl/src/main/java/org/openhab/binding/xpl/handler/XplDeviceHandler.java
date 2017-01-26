/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xpl.handler;

import static org.openhab.binding.xpl.XplBindingConstants.CHANNEL_LAST_MESSAGE;

import java.util.Calendar;
import java.util.Map;

import org.cdp1802.xpl.NamedValuesI;
import org.cdp1802.xpl.xPL_MessageI;
import org.cdp1802.xpl.xPL_MessageI.MessageType;
import org.cdp1802.xpl.xPL_MutableMessageI;
import org.cdp1802.xpl.xPL_Utils;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.xpl.config.XplDeviceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XplDeviceHandler<X extends XplDeviceConfiguration> extends BaseThingHandler {
    private static Logger logger = LoggerFactory.getLogger(XplDeviceHandler.class);
    protected String xplSchema = null;

    final Class<X> configurationClass;
    protected X configuration = null;

    public XplDeviceHandler(Thing thing, Class<X> configurationClass) {
        super(thing);
        this.configurationClass = configurationClass;
    }

    @Override
    public void initialize() {
        configuration = getConfigAs(configurationClass);
        xplSchema = getProperty("schema");
        super.initialize();
    }

    protected String getProperty(String propertyName) {
        final Map<String, String> properties = thing.getProperties();
        if (properties.containsKey(propertyName)) {
            return properties.get(propertyName);
        } else {
            logger.warn("Unable to load property {}", propertyName);
            return null;
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (!(command instanceof RefreshType)) {
            xPL_MutableMessageI message = xPL_Utils.createMessage();
            message.setSchema(xplSchema);
            message.setType(MessageType.COMMAND);

            addIdentifiers(message);

            internalHandleCommand(channelUID.getId(), command, message);

            ((XplBridgeHandler) this.getBridge().getHandler()).sendMessage(message);
        }

    }

    protected abstract void internalHandleCommand(String channelId, Command command, xPL_MutableMessageI message);

    protected abstract void addIdentifiers(xPL_MutableMessageI message);

    protected abstract void internalHandleMessage(NamedValuesI messageBody);

    protected abstract boolean targetedBy(xPL_MessageI theMessage);

    public void handeXPLMessage(MessageType type, String schema, NamedValuesI messageBody) {
        if (schema.equalsIgnoreCase(xplSchema)) {

            internalHandleMessage(messageBody);

        }
        updateState(CHANNEL_LAST_MESSAGE, new DateTimeType(Calendar.getInstance()));
    }

}
