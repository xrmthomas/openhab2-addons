/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xpl.handler;

import static org.openhab.binding.xpl.XplBindingConstants.*;

import org.cdp1802.xpl.NamedValuesI;
import org.cdp1802.xpl.xPL_MessageI;
import org.cdp1802.xpl.xPL_MessageI.MessageType;
import org.cdp1802.xpl.xPL_MutableMessageI;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.xpl.config.X10DeviceConfiguration;

public class X10SecurityHandler extends XplDeviceHandler<X10DeviceConfiguration> {
    private static String deviceKey = null;

    public X10SecurityHandler(Thing thing) {
        super(thing, X10DeviceConfiguration.class);
    }

    @Override
    public void initialize() {
        super.initialize();
        deviceKey = getProperty("idString1");
    }

    @Override
    protected boolean targetedBy(xPL_MessageI theMessage) {
        boolean result = false;
        String device = theMessage.getNamedValue(deviceKey);

        if (device != null) {
            result = device.equalsIgnoreCase(configuration.deviceId);
            result = result
                    && xplSchema.equalsIgnoreCase(theMessage.getSchemaClass() + "." + theMessage.getSchemaType());
            result = result && theMessage.getType() != MessageType.COMMAND;
        }

        return result;
    }

    @Override
    protected void internalHandleMessage(NamedValuesI messageBody) {
        String command = messageBody.getNamedValue(CHANNEL_COMMAND).toLowerCase();
        updateState(CHANNEL_COMMAND, new StringType(command));

        String tamper = messageBody.getNamedValue(CHANNEL_TAMPER);
        if (tamper != null) {
            updateState(CHANNEL_TAMPER, tamper.equalsIgnoreCase("true") ? OnOffType.ON : OnOffType.OFF);
        }

        String lowbat = messageBody.getNamedValue("low-battery");
        if (lowbat != null) {
            updateState(CHANNEL_LOW_BATTERY, lowbat.equalsIgnoreCase("true") ? OnOffType.ON : OnOffType.OFF);
        }
    }

    @Override
    protected void addIdentifiers(xPL_MutableMessageI message) {
        // message.addNamedValue(deviceKey, configuration.deviceId);
    }

    @Override
    protected void internalHandleCommand(String channelId, Command command, xPL_MutableMessageI message) {
        // Read only device
    }

}
