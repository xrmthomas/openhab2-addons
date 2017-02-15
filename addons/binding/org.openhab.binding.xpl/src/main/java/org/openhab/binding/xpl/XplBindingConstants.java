/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xpl;

import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

import com.google.common.collect.ImmutableSet;

/**
 * The {@link XplBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class XplBindingConstants {

    public static final String BINDING_ID = "xpl";
    public static final String CLINIQUE_VENDOR_ID = "clinique";
    public static final String OPENHAB_DEVICE_ID = "openhab";

    // List of all Thing Type UIDs
    public final static ThingTypeUID BRIDGE_THING_TYPE = new ThingTypeUID(BINDING_ID, "xplbridge");
    public final static ThingTypeUID DEVICE_THING_TYPE = new ThingTypeUID(BINDING_ID, "xpldevice");

    public final static ThingTypeUID THING_TYPE_X10_LIGHTING = new ThingTypeUID(BINDING_ID, "X10Appliance");
    public final static ThingTypeUID THING_TYPE_AC_DIMMER = new ThingTypeUID(BINDING_ID, "ACDimmer");
    public final static ThingTypeUID THING_TYPE_AC_APPLIANCE = new ThingTypeUID(BINDING_ID, "ACAppliance");
    public final static ThingTypeUID THING_TYPE_RAW_MESSAGE = new ThingTypeUID(BINDING_ID, "RawMessage");
    public final static ThingTypeUID THING_TYPE_OREGON_SENSOR = new ThingTypeUID(BINDING_ID, "oregonsensor");

    // List of all Channel ids
    public final static String CHANNEL_TEMPERATURE = "temperature";
    public final static String CHANNEL_HUMIDITY = "humidity";
    public final static String CHANNEL_HUMIDITY_STATUS = "status";
    public final static String CHANNEL_BATTERY_LEVEL = "battery";
    public final static String CHANNEL_LOW_BATTERY = "lowBattery";
    public final static String CHANNEL_ENERGY = "energy";
    public final static String CHANNEL_POWER = "power";
    public final static String CHANNEL_INSTANT_AMP = "instantAmp";
    public final static String CHANNEL_COMMAND = "command";
    public final static String CHANNEL_LEVEL = "level";
    public final static String CHANNEL_LAST_MESSAGE = "lastMessage";

    // List of all adressable things in OH
    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = ImmutableSet.of(BRIDGE_THING_TYPE,
            DEVICE_THING_TYPE, THING_TYPE_OREGON_SENSOR, THING_TYPE_X10_LIGHTING, THING_TYPE_AC_DIMMER,
            THING_TYPE_AC_APPLIANCE);
}
