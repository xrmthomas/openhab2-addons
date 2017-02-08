/**
 * Copyright (c) 204-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xpl.handler;

import static org.openhab.binding.xpl.XplBindingConstants.*;

import org.cdp1802.xpl.NamedValuesI;
import org.cdp1802.xpl.xPL_MessageI;
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
        String unit = theMessage.getNamedValue(unitKey);
        result = xplSchema.equalsIgnoreCase(theMessage.getSchemaClass() + "." + theMessage.getSchemaType());
        result = result && adressMatches(address, unit);

        return result;
    }

    private boolean adressMatches(String address, String unit) {
        boolean result = false;

        if (unit != null) {
            Integer requestedUnit = configuration.getAddresses().get(address);
            result = (requestedUnit != null) && (requestedUnit.intValue() == Integer.parseInt(unit));
        }
        return result;
    }

    @Override
    protected void internalHandleCommand(String channelId, Command command, xPL_MutableMessageI message) {
        switch (channelId) {
            case CHANNEL_COMMAND:
                message.addNamedValue(channelId, command.toString().toLowerCase());
                if (getThing().getChannel(CHANNEL_LEVEL) != null) {
                    updateState(CHANNEL_LEVEL, command == OnOffType.ON ? PercentType.HUNDRED : PercentType.ZERO);
                }
                break;
            case CHANNEL_LEVEL:
                int valeur = Integer.parseInt(command.toString());
                valeur = (int) Math.round(valeur / 6.67);
                valeur = Math.max(0, valeur);
                valeur = Math.min(15, valeur);

                message.addNamedValue(CHANNEL_COMMAND, "preset");
                message.addNamedValue(CHANNEL_LEVEL, valeur);
                if (getThing().getChannel(CHANNEL_COMMAND) != null) {
                    updateState(CHANNEL_COMMAND, valeur == 0 ? OnOffType.OFF : OnOffType.ON);
                }
        }
    }

    @Override
    protected void internalHandleMessage(NamedValuesI messageBody) {
        String command = messageBody.getNamedValue(CHANNEL_COMMAND).toLowerCase();
        State boolState = null;
        State dimState = null;
        switch (command) {
            case "on":
                boolState = OnOffType.ON;
                dimState = PercentType.HUNDRED;
                break;
            case "off":
                boolState = OnOffType.OFF;
                dimState = PercentType.ZERO;
                break;
            case "preset":
                int value = Integer.parseInt(messageBody.getNamedValue(CHANNEL_LEVEL));
                boolState = value == 0 ? OnOffType.OFF : OnOffType.ON;
                value = (int) (value * 6.67);
                value = Math.min(value, 100);
                dimState = new PercentType(value);
        }

        if (dimState != null && getThing().getChannel(CHANNEL_LEVEL) != null) {
            updateState(CHANNEL_LEVEL, dimState);
        }

        if (boolState != null && getThing().getChannel(CHANNEL_COMMAND) != null) {
            updateState(CHANNEL_COMMAND, boolState);
        }

    }

    @Override
    protected void addIdentifiers(xPL_MutableMessageI message) {
        message.addNamedValue(addressKey, configuration.address1);
        message.addNamedValue(unitKey, configuration.unit1);
    }

}
