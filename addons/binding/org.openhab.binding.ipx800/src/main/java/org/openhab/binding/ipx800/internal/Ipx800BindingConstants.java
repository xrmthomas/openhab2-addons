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
package org.openhab.binding.ipx800.internal;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link Ipx800BindingConstants} class defines common constants, which are used
 * across the whole binding.
 *
 * @author Gaël L'hopital - Initial Contribution
 */
@NonNullByDefault
public class Ipx800BindingConstants {

    public static final String BINDING_ID = "ipx800";

    // List of Bridge Type UIDs
    public static final ThingTypeUID BRIDGE_THING_TYPE = new ThingTypeUID(BINDING_ID, "ipx800v3");

    // List of Extension Things Type UIDs
    public static final ThingTypeUID X400_THING_TYPE = new ThingTypeUID(BINDING_ID, "x400");
    public static final ThingTypeUID X880_THING_TYPE = new ThingTypeUID(BINDING_ID, "x880");

    // Module Properties
    public static final String PROPERTY_DIGITAL_INPUT = "digitalInput";
    public static final String PROPERTY_ANALOG_INPUT = "analogInput";
    public static final String PROPERTY_RELAY_OUTPUT = "relayOuput";
    public static final String PROPERTY_COUNTER = "counter";

    // List of all item types
    public static final String ANALOG_ITEM = "Number";
    public static final String DATE_TIME_ITEM = "DateTime";
    public static final String DIGITAL_INPUT_ITEM = "Contact";
    public static final String DIGITAL_OUTPUT_ITEM = "Switch";
    public static final String INFORMATION_ITEM = "String";

    // List of all supported physical devices and modules
    public static final Set<ThingTypeUID> SUPPORTED_DEVICE_THING_TYPES_UIDS = Stream
            .of(X400_THING_TYPE, X880_THING_TYPE).collect(Collectors.toSet());

    // List of all adressable things in OH = SUPPORTED_DEVICE_THING_TYPES_UIDS + the virtual bridge
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Stream
            .concat(SUPPORTED_DEVICE_THING_TYPES_UIDS.stream(), Stream.of(BRIDGE_THING_TYPE))
            .collect(Collectors.toSet());

    public static final Set<String> MODULE_PROPERTIES = Stream
            .of(PROPERTY_DIGITAL_INPUT, PROPERTY_ANALOG_INPUT, PROPERTY_RELAY_OUTPUT, PROPERTY_COUNTER)
            .collect(Collectors.toSet());

    public static final Map<String, String> PROPERTY_TYPE_MAP = Stream
            .of(new AbstractMap.SimpleEntry<>(PROPERTY_DIGITAL_INPUT, DIGITAL_INPUT_ITEM),
                    new AbstractMap.SimpleEntry<>(PROPERTY_ANALOG_INPUT, ANALOG_ITEM),
                    new AbstractMap.SimpleEntry<>(PROPERTY_RELAY_OUTPUT, DIGITAL_OUTPUT_ITEM),
                    new AbstractMap.SimpleEntry<>(PROPERTY_COUNTER, ANALOG_ITEM))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static final Map<String, String> PROPERTY_PREFIX_MAP = Stream
            .of(new AbstractMap.SimpleEntry<>(PROPERTY_DIGITAL_INPUT, "I"),
                    new AbstractMap.SimpleEntry<>(PROPERTY_ANALOG_INPUT, "A"),
                    new AbstractMap.SimpleEntry<>(PROPERTY_RELAY_OUTPUT, "O"),
                    new AbstractMap.SimpleEntry<>(PROPERTY_COUNTER, "C"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
}
