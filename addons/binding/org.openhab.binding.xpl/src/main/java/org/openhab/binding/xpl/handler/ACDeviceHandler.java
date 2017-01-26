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
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.xpl.config.ACDeviceConfiguration;

public class ACDeviceHandler extends XplDeviceHandler<ACDeviceConfiguration> {
    private static String addressKey = null;
    private static String unitKey = null;

    public ACDeviceHandler(Thing thing) {
        super(thing, ACDeviceConfiguration.class);
    }

    @Override
    public void initialize() {
        addressKey = getProperty("idString1");
        unitKey = getProperty("idString2");
        super.initialize();
    }

    @Override
    protected boolean targetedBy(xPL_MessageI theMessage) {
        boolean result = false;
        String address = theMessage.getNamedValue(addressKey);
        if (address != null) {
            String unit = theMessage.getNamedValue(unitKey);
            if (unit != null) {
                for (int i = 0; i < 6 && !result; i++) {
                    result = address.equalsIgnoreCase(configuration.getAddresses()[i]);
                    result = result && unit.equalsIgnoreCase(configuration.getUnits()[i]);
                }
                result = result
                        && xplSchema.equalsIgnoreCase(theMessage.getSchemaClass() + "." + theMessage.getSchemaType());
                result = result && theMessage.getType() != MessageType.COMMAND;
            }
        }

        return result;
    }

    @Override
    protected void internalHandleCommand(String channelId, Command command, xPL_MutableMessageI message) {
        switch (channelId) {
            case CHANNEL_COMMAND:
                message.addNamedValue(channelId, command.toString().toLowerCase());
                updateState(CHANNEL_LEVEL, command == OnOffType.ON ? PercentType.HUNDRED : PercentType.ZERO);
                break;
            case CHANNEL_LEVEL:
                int valeur = Integer.parseInt(command.toString());
                valeur = Math.round(valeur / 6);
                valeur = Math.max(0, valeur);
                valeur = Math.min(15, valeur);

                message.addNamedValue("command", "preset");
                message.addNamedValue(CHANNEL_LEVEL, valeur);
                updateState(CHANNEL_COMMAND, valeur == 0 ? OnOffType.OFF : OnOffType.ON);
        }
    }

    @Override
    protected void internalHandleMessage(NamedValuesI messageBody) {
        State state = messageBody.getNamedValue("command").equalsIgnoreCase("ON") ? OnOffType.ON : OnOffType.OFF;
        updateState(CHANNEL_COMMAND, state);
    }

    @Override
    protected void addIdentifiers(xPL_MutableMessageI message) {
        message.addNamedValue(addressKey, configuration.getAddresses()[0]);
        message.addNamedValue(unitKey, configuration.getUnits()[0]);

    }

}
