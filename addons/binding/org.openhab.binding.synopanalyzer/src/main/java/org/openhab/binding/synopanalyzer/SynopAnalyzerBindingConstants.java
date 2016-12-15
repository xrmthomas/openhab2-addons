/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.synopanalyzer;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link SynopAnalyzerBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class SynopAnalyzerBindingConstants {

    public static final String BINDING_ID = "synopanalyzer";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_SYNOP = new ThingTypeUID(BINDING_ID, "synopanalyzer");

    // List of all Channel ids
    public final static String HORIZONTAL_VISIBILITY = "horizontal-visibility";
    public final static String OCTA = "octa";
    public final static String OVERCAST = "overcast";
    public final static String PRESSURE = "pressure";
    public final static String TEMPERATURE = "temperature";
    public final static String WIND_ANGLE = "wind-angle";
    public final static String WIND_DIRECTION = "wind-direction";
    public final static String WIND_SPEED_MS = "wind-speed-ms";
    public final static String WIND_SPEED_KNOTS = "wind-speed-knots";
    public final static String WIND_SPEED_BEAUFORT = "wind-speed-beaufort";
    public final static String TIME_UTC = "time-utc";

}
