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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.core.types.Type;
import org.openhab.binding.ipx800.internal.itemslot.Ipx800Item;

/**
 * This class represent a ipx800 port which can be input, output, analog or counter (even if counter is not a real but a
 * computed port)
 *
 * @author Seebag
 * @since 1.8.0
 *
 */
public class Ipx800Port {
    /** items linked to this port */
    private Map<String, Ipx800Item> items = new HashMap<String, Ipx800Item>();
    /** Number of the current port */
    private int portNumber = 0;
    /** Type */
    private /* Ipx800PortType */ Object commandType;
    /** Device of this port */
    private Ipx800DeviceConnector device;
    /** Handler for ipx800 port update */
    private Ipx800Handler handler = null; // new Ipx800HandlerIfChanged();

    public Ipx800Port(/* Ipx800PortType */ Object commandType, int slotNumber) {
        // commandType.assertSlotCompatible(slotNumber);
        this.commandType = commandType;
        this.portNumber = slotNumber;
    }

    public Ipx800Port(/* Ipx800PortType */ Object commandType, int slotNumber, Ipx800DeviceConnector device) {
        this(commandType, slotNumber);
        this.device = device;
    }

    @Override
    public String toString() {
        return /* this.commandType.name() + */ ":" + this.portNumber;
    }

    /**
     *
     * @return the current port number
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     *
     * @return The command type
     */
    public /* Ipx800PortType */ Object getCommandType() {
        return commandType;
    }

    public Ipx800DeviceConnector getDevice() {
        return device;
    }

    /**
     * Set ipx800 handler for input commands
     */
    public void switchToMultiHandler() {
        // if (!(handler instanceof Ipx800HandlerMulti)) {
        // handler = new Ipx800HandlerMulti();
        // }
    }

    public void attachItem(String itemName, Ipx800Item item) {
        item.setPort(this);
        items.put(itemName, item);
    }

    public Ipx800Item getItemSlot(String itemName) {
        return items.get(itemName);
    }

    public Map<String, Ipx800Item> getItems() {
        return items;
    }

    public <T extends Ipx800Item> Map<String, T> getItems(Class<? extends Ipx800Item> itemSlotClass) {
        Map<String, T> ret = new HashMap<String, T>();
        for (String itemName : items.keySet()) {
            @SuppressWarnings("unchecked")
            T slot = (T) items.get(itemName);
            if (itemSlotClass.isInstance(slot)) {
                ret.put(itemName, slot);
            }
        }
        return ret;
    }

    /**
     * Destroy the item itemName (ie: stop item tasks and remove it from current port)
     *
     * @param itemName
     */
    public void destroyItem(String itemName) {
        Ipx800Item it = items.get(itemName);
        if (it != null) {
            it.destroy();
            items.remove(itemName);
        }
    }

    /**
     * Destroy all items linked to this port
     */
    public void destroy() {
        for (Ipx800Item item : items.values()) {
            item.destroy();
        }
    }

    /**
     * Update the state of all items linked to this port
     *
     * @param state The state to update
     */
    public void updateState(Type state) {
        for (Ipx800Item itemSlot : items.values()) {
            itemSlot.updateState(state);
        }
    }

    /**
     * Update the state of all items if state is different than the previous one
     *
     * @param state The state to update
     * @return true if state has been updated
     */
    public boolean updateStateIfChanged(String state) {
        // return handler.updateState(items, state);
        return true;
    }
}
