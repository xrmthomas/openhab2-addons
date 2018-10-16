/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xmltv.internal;

import static org.openhab.binding.xmltv.XmlTVBindingConstants.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.xmltv.handler.ChannelHandler;
import org.openhab.binding.xmltv.handler.XmlTVHandler;
import org.openhab.binding.xmltv.internal.discovery.XmlTVDiscoveryService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link XmlTVHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Gaël L'hopital - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.xmltv", service = ThingHandlerFactory.class)
public class XmlTVHandlerFactory extends BaseThingHandlerFactory {
    private final Logger logger = LoggerFactory.getLogger(XmlTVHandlerFactory.class);
    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (XMLTV_FILE_BRIDGE_TYPE.equals(thingTypeUID)) {
            XmlTVHandler bridgeHandler;
            try {
                bridgeHandler = new XmlTVHandler((Bridge) thing);
                registerDeviceDiscoveryService(bridgeHandler);
                return bridgeHandler;
            } catch (JAXBException e) {
                logger.error("Unable to create XmlTVHandler : {}", e.getMessage());
            }
        } else if (XMLTV_CHANNEL_THING_TYPE.equals(thingTypeUID)) {
            return new ChannelHandler(thing);
        }

        return null;
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof XmlTVHandler) {
            ThingUID thingUID = thingHandler.getThing().getUID();
            unregisterDeviceDiscoveryService(thingUID);
        }
        super.removeHandler(thingHandler);
    }

    private void registerDeviceDiscoveryService(XmlTVHandler bridgeHandler) {
        XmlTVDiscoveryService discoveryService = new XmlTVDiscoveryService(bridgeHandler);
        discoveryServiceRegs.put(bridgeHandler.getThing().getUID(), bundleContext
                .registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));
    }

    private void unregisterDeviceDiscoveryService(ThingUID thingUID) {
        ServiceRegistration<?> serviceReg = discoveryServiceRegs.get(thingUID);
        serviceReg.unregister();
        discoveryServiceRegs.remove(thingUID);
    }
}
