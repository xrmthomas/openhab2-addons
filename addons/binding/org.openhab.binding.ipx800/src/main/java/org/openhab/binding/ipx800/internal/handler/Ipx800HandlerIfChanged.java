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
package org.openhab.binding.ipx800.internal.handler;

import java.util.Map;

import org.eclipse.smarthome.core.thing.Bridge;
import org.openhab.binding.ipx800.internal.itemslot.Ipx800Item;

/**
 * This handler update items if the state changed
 *
 * @author Seebag
 * @since 1.8.0
 *
 */
public class Ipx800HandlerIfChanged extends Ipx800Handler {
    public Ipx800HandlerIfChanged(Bridge bridge) {
        super(bridge);
        // TODO Auto-generated constructor stub
    }

    /** Previous state of this port as string */
    private String lastState = "";

    // @Override
    public boolean updateState(Map<String, Ipx800Item> items, String state) {
        boolean changed = false;
        if (!state.equals(lastState)) {
            for (Ipx800Item itemSlot : items.values()) {
                changed |= itemSlot.updateState(state);
            }
            lastState = state;
        }
        return changed;
    }
}
