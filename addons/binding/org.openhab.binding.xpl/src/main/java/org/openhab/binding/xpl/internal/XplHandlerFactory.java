/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xpl.internal;

import static org.openhab.binding.xpl.XplBindingConstants.*;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.xpl.handler.ACDeviceHandler;
import org.openhab.binding.xpl.handler.OregonDeviceHandler;
import org.openhab.binding.xpl.handler.X10DeviceHandler;
//import org.eclipse.smarthome.model.script.engine.action.ActionService;
//import org.openhab.binding.xpl.deactivated.XplAction;
import org.openhab.binding.xpl.handler.XplBridgeHandler;

/**
 * The {@link XplHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class XplHandlerFactory extends BaseThingHandlerFactory /* implements ActionService */ {

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(BRIDGE_THING_TYPE)) {
            XplBridgeHandler bridgeHandler = new XplBridgeHandler((Bridge) thing);
            return bridgeHandler;
        } else if (thingTypeUID.equals(THING_TYPE_X10_LIGHTING)) {
            return new X10DeviceHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_AC_DIMMER)) {
            return new ACDeviceHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_AC_APPLIANCE)) {
            return new ACDeviceHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_OREGON_SENSOR)) {
            return new OregonDeviceHandler(thing);
        }
        return null;

    }
    /*
     * @Override
     * public String getActionClassName() {
     * return XplAction.class.getCanonicalName();
     * }
     *
     * @Override
     * public Class<?> getActionClass() {
     * return NotificationAction.class;
     * }
     */
}
