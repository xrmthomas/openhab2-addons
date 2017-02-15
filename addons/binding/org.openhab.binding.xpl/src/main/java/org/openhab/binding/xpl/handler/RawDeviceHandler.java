/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xpl.handler;

import static org.openhab.binding.xpl.XplBindingConstants.CHANNEL_COMMAND;

import org.cdp1802.xpl.NamedValueI;
import org.cdp1802.xpl.NamedValuesI;
import org.cdp1802.xpl.xPL_MessageI;
import org.cdp1802.xpl.xPL_MutableMessageI;
import org.cdp1802.xpl.xPL_Utils;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.openhab.binding.xpl.config.RawDeviceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RawDeviceHandler extends XplDeviceHandler<RawDeviceConfiguration> {
    private static Logger logger = LoggerFactory.getLogger(RawDeviceHandler.class);
    private xPL_MutableMessageI configMessage;
    private String namedParameter;

    public RawDeviceHandler(Thing thing) {
        super(thing, RawDeviceConfiguration.class);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing Netatmo API bridge handler.");
        super.initialize();

        String[] configParts = configuration.rawMessage.trim().split(",");
        if (configParts.length == 5) {
            configMessage = xPL_Utils.createMessage();
            configMessage.setTarget(configParts[0]);

            // Parse message type
            if (configParts[1].equalsIgnoreCase("TRIGGER")) {
                configMessage.setType(xPL_MessageI.MessageType.TRIGGER);
            } else if (configParts[1].equalsIgnoreCase("STATUS")) {
                configMessage.setType(xPL_MessageI.MessageType.STATUS);
            } else if (configParts[1].equalsIgnoreCase("COMMAND")) {
                configMessage.setType(xPL_MessageI.MessageType.COMMAND);
            } else {
                configMessage.setType(xPL_MessageI.MessageType.UNKNOWN);
            }

            configMessage.setSchema(configParts[2]);

            // Parse name/value pairs
            String theName = null;
            String theValue = null;
            int delimPtr;

            for (int pairPtr = 3; pairPtr < configParts.length; pairPtr++) {
                delimPtr = configParts[pairPtr].indexOf("=");
                theName = configParts[pairPtr].substring(0, delimPtr);
                theValue = configParts[pairPtr].substring(delimPtr + 1);
                configMessage.addNamedValue(theName, theValue);
                if (theValue.equalsIgnoreCase("#COMMAND") || theValue.equalsIgnoreCase("#CURRENT")) {
                    namedParameter = new String(theName);
                }
            }

        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Raw xPL Thing configuration must contain 5 parameters : target,message type,schema and at least one body key/value pair");
            return;
        }
    }

    @Override
    protected boolean targetedBy(xPL_MessageI candidateMessage) {
        boolean result = false;

        NamedValuesI configBody = configMessage.getMessageBody();
        if ((configBody != null) && (!configBody.isEmpty())
                && (configMessage.getTarget().isBroadcastIdentifier()
                        || configMessage.getTarget().equals(candidateMessage.getSource()))
                && configMessage.getSchemaClass().equalsIgnoreCase(candidateMessage.getSchemaClass())
                && configMessage.getSchemaType().equalsIgnoreCase(candidateMessage.getSchemaType())) {

            boolean bodyMatched = true;
            for (NamedValueI configBodyValue : configBody.getAllNamedValues()) { // iterate through the item body to
                String aKey = configBodyValue.getName(); // see if ...
                String aValue = configBodyValue.getValue();
                String bValue = candidateMessage.getNamedValue(aKey);
                boolean lineMatched = (bValue != null)
                        && (aKey.equalsIgnoreCase(namedParameter) || aValue.equalsIgnoreCase(bValue));

                bodyMatched = bodyMatched && lineMatched;
            }

            result = bodyMatched;
        }

        return result;
    }

    @Override
    protected void internalHandleMessage(NamedValuesI messageBody) {
        String value = messageBody.getNamedValue(namedParameter);
        updateState(CHANNEL_COMMAND, new StringType(value));
    }

}
