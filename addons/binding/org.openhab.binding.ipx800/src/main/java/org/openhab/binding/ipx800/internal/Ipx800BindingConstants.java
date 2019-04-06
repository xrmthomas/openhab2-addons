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
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.CoreItemFactory;
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
    public static final ThingTypeUID IPXV3_THING_TYPE = new ThingTypeUID(BINDING_ID, "ipx800v3");

    // Module Properties
    public static final String PROPERTY_DIGITAL_INPUT = "I";
    public static final String PROPERTY_ANALOG_INPUT = "A";
    public static final String PROPERTY_COUNTER = "C";
    public static final String PROPERTY_RELAY_OUTPUT = "O";

    public static final String TIMESTAMP_CHANNEL_NAME = "timestamp";
    public static final String LAST_STATE_DURATION_CHANNEL_NAME = "lastStateDuration";
    public static final String CHANNEL_TYPE_PUSH_BUTTON_TRIGGER = "pushButtonTrigger";

    // Channel configuration entries
    public static final String CONFIGURATION_PULSE = "pulse";
    public static final String CONFIGURATION_DEBOUNCE = "debouncePeriod";
    public static final String CONFIGURATION_HISTERESIS = "Histeresis";
    public static final String CONFIGURATION_LONG_PRESS = "longPressTime";
    public static final String CONFIGURATION_PULSE_TIMEOUT = "pulseTimeout";
    public static final String CONFIGURATION_PULSE_PERIOD = "pulsePeriod";

    public static final String EVENT_PRESSED = "PRESSED";
    public static final String EVENT_RELEASED = "RELEASED";
    public static final String EVENT_SHORT_PRESS = "SHORT_PRESS";
    public static final String EVENT_LONG_PRESS = "LONG_PRESS";
    public static final String EVENT_PULSE = "PULSE";

    // List of adressable things
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(IPXV3_THING_TYPE);

    public static final Map<String, String> PROPERTY_TYPE_MAP = Stream
            .of(new AbstractMap.SimpleEntry<>(PROPERTY_DIGITAL_INPUT, CoreItemFactory.CONTACT),
                    new AbstractMap.SimpleEntry<>(PROPERTY_ANALOG_INPUT, CoreItemFactory.NUMBER),
                    new AbstractMap.SimpleEntry<>(PROPERTY_RELAY_OUTPUT, CoreItemFactory.SWITCH),
                    new AbstractMap.SimpleEntry<>(PROPERTY_COUNTER, CoreItemFactory.NUMBER))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static final Map<String, String> PROPERTY_PREFIX_LABELS = Stream
            .of(new AbstractMap.SimpleEntry<>(PROPERTY_DIGITAL_INPUT, "Digital Input"),
                    new AbstractMap.SimpleEntry<>(PROPERTY_ANALOG_INPUT, "Analog Input"),
                    new AbstractMap.SimpleEntry<>(PROPERTY_RELAY_OUTPUT, "Relay Ouput"),
                    new AbstractMap.SimpleEntry<>(PROPERTY_COUNTER, "Counter"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
}
