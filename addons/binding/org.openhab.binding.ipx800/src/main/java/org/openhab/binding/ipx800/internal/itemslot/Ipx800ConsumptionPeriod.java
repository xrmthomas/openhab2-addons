/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ipx800.internal.itemslot;

/**
 * Power average/consumption periods
 * 
 * @author Seebag
 * @since 1.8.0
 *
 */
public enum Ipx800ConsumptionPeriod {
    DAY(60 * 60 * 24, "d"),
    HOUR(60 * 60, "h"),
    MINUTE(60, "m"),
    SECOND(1, "s");
    /** Period in second */
    public int time = 1;
    public String shortName;

    Ipx800ConsumptionPeriod(int time, String shortName) {
        this.time = time;
        this.shortName = shortName;
    }

    public static Ipx800ConsumptionPeriod getPeriod(String shortName) {
        for (Ipx800ConsumptionPeriod p : Ipx800ConsumptionPeriod.values()) {
            if (p.shortName.equals(shortName)) {
                return p;
            }
        }
        return null;
    }
};
