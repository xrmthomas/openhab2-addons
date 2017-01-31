/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xpl.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import org.cdp1802.xpl.xPL_IdentifierI;
import org.cdp1802.xpl.xPL_Manager;
import org.cdp1802.xpl.xPL_MediaHandlerException;
import org.cdp1802.xpl.xPL_MessageI;
import org.cdp1802.xpl.xPL_MessageListenerI;
import org.cdp1802.xpl.xPL_MutableMessageI;
import org.cdp1802.xpl.ethernet.EthernetHandler;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.ConfigStatusBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.xpl.XplBindingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link XplBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class XplBridgeHandler extends ConfigStatusBridgeHandler implements xPL_MessageListenerI {

    private static final String BINDTO_PARAM = "bindTo";
    private static final String STARTHUB_PARAM = "startHub";

    private Logger logger = LoggerFactory.getLogger(XplBridgeHandler.class);

    private static xPL_IdentifierI sourceIdentifier = null;
    private static final xPL_Manager theManager = xPL_Manager.getManager();
    // private XplBridgeConfiguration configuration;

    private Boolean startHub;
    private String instance;
    private String bindTo;

    public XplBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {

        Configuration config = getThing().getConfiguration();

        startHub = (Boolean) config.get(STARTHUB_PARAM);
        instance = (String) config.get("instance");
        bindTo = (String) config.get(BINDTO_PARAM);

        setInstance(instance);
        theManager.addMessageListener(this);
        updateStatus(ThingStatus.ONLINE);
        // configuration = getConfigAs(XplBridgeConfiguration.class);

        // EthernetHandler ethernetHandler = new EthernetHandler();
        // try {
        // ethernetHandler.setNetworkInterface(
        // configuration.bindTo == null ? null : InetAddress.getByName(configuration.bindTo));
        // ethernetHandler.setConnectMode(configuration.startHub ? EthernetHandler.ConnectMode.STANDALONE
        // : EthernetHandler.ConnectMode.VIA_HUB);
        // ethernetHandler.startHandler();
        // theManager.addMediaHandler(ethernetHandler);
        // setInstance(configuration.instance);
        // theManager.addMessageListener(this);
        // updateStatus(ThingStatus.ONLINE);
        // } catch (UnknownHostException e) {
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Invalid or malformed BindTo address");
        // } catch (xPL_MediaHandlerException e) {
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        // }
    }

    @Override
    public void dispose() {
        theManager.removeMessageListener(this);
        theManager.doSmartShutdown();
        logger.debug("xPL transport has been stopped");
    }

    protected void setInstance(String instance) {
        sourceIdentifier = theManager.getIdentifierManager().parseNamedIdentifier(
                XplBindingConstants.CLINIQUE_VENDOR_ID + "-" + XplBindingConstants.OPENHAB_DEVICE_ID + "." + instance);
        logger.info("xPL Binding source address set to " + sourceIdentifier.toString());
    }

    protected String getInstance() {
        if (sourceIdentifier == null) {
            // Ce cas ne devrait pas survenir... supprimer ceci après avoir controlé que c'est vrai
            setInstance(XplBindingConstants.OPENHAB_DEVICE_ID);
        }
        return sourceIdentifier.getInstanceID();
    }

    public void sendMessage(xPL_MutableMessageI message) {
        if (message.getSource() == null) {
            message.setSource(sourceIdentifier);
        }
        if (message.getTarget() == null) {
            message.setTarget("*");
        }
        logger.debug(message.toString());
        theManager.sendMessage(message);
    }

    public xPL_IdentifierI parseNamedIdentifier(String target) {
        return theManager.getIdentifierManager().parseNamedIdentifier(target);
    }

    @Override
    public void handleXPLMessage(xPL_MessageI theMessage) {
        logger.debug(theMessage.toString());
        for (Thing thing : getThing().getThings()) {
            ThingHandler thingHandler = thing.getHandler();
            XplDeviceHandler<?> deviceHandler = (XplDeviceHandler<?>) thingHandler;

            if (deviceHandler.targetedBy(theMessage)) {
                deviceHandler.handeXPLMessage(theMessage.getType(),
                        theMessage.getSchemaClass() + "." + theMessage.getSchemaType(), theMessage.getMessageBody());
            }
        }
    }

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        Collection<ConfigStatusMessage> configStatus = new ArrayList<>();

        EthernetHandler ethernetHandler = new EthernetHandler();
        try {
            ethernetHandler.setNetworkInterface(bindTo == null ? null : InetAddress.getByName(bindTo));
            ethernetHandler.setConnectMode(
                    startHub ? EthernetHandler.ConnectMode.STANDALONE : EthernetHandler.ConnectMode.VIA_HUB);
            ethernetHandler.startHandler();
            theManager.addMediaHandler(ethernetHandler);
        } catch (UnknownHostException e) {
            configStatus.add(ConfigStatusMessage.Builder.error(BINDTO_PARAM)
                    .withMessageKeySuffix("Invalid or malformed BindTo address").withArguments(bindTo).build());
        } catch (xPL_MediaHandlerException e) {
            configStatus.add(ConfigStatusMessage.Builder.error(STARTHUB_PARAM).withMessageKeySuffix(e.getMessage())
                    .withArguments(startHub).build());
        }

        return configStatus;
    }
}