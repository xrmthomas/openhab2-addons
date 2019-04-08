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
package org.openhab.binding.gce.internal.handler;

import static org.openhab.binding.gce.internal.GCEBindingConstants.*;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.gce.internal.config.AnalogInputConfiguration;
import org.openhab.binding.gce.internal.config.DigitalInputConfiguration;
import org.openhab.binding.gce.internal.config.Ipx800Configuration;
import org.openhab.binding.gce.internal.config.RelayOutputConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link Ipx800Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Gaël L'hopital - Initial contribution
 */
@NonNullByDefault
public class Ipx800Handler extends BaseThingHandler implements Ipx800EventListener {
    private static final String PROPERTY_SEPARATOR = "-";

    private final Logger logger = LoggerFactory.getLogger(Ipx800Handler.class);

    private @NonNullByDefault({}) Ipx800Configuration configuration;
    private @NonNullByDefault({}) Ipx800DeviceConnector connector;
    private @NonNullByDefault({}) Ipx800MessageParser parser;

    private final Map<String, @Nullable PortData> portDatas = new HashMap<>();

    private class LongPressEvaluator implements Runnable {
        private final ZonedDateTime referenceTime;
        private final String port;
        private final String channelId;

        public LongPressEvaluator(Channel channel, String port, PortData portData) {
            this.referenceTime = portData.timestamp;
            this.port = port;
            this.channelId = channel.getUID().getId();
        }

        @Override
        public void run() {
            PortData currentData = portDatas.get(port);
            if (currentData != null && currentData.value == 1 && currentData.timestamp == referenceTime) {
                triggerChannel(channelId + PROPERTY_SEPARATOR + CHANNEL_TYPE_PUSH_BUTTON_TRIGGER, EVENT_LONG_PRESS);
            }
        }
    }

    private class PortData {
        Double value = -1d;
        ZonedDateTime timestamp = ZonedDateTime.now();
        @Nullable
        ScheduledFuture<?> pulsing;
    }

    public Ipx800Handler(Thing thing) {
        super(thing);

        logger.debug("Create a IPX800 Handler for thing '{}'", getThing().getUID());
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.OFFLINE);

        configuration = getConfigAs(Ipx800Configuration.class);

        logger.debug("Initializing IPX800 handler for uid '{}'", getThing().getUID());
        scheduler.execute(this::doInitialization);
    }

    @Override
    public void dispose() {
        connector.destroyAndExit();
        super.dispose();
    }

    protected void doInitialization() {
        logger.debug("Initialize IPX800 input blocks handler.");

        connector = new Ipx800DeviceConnector(configuration);
        parser = new Ipx800MessageParser(connector);
        parser.addEventListener(this);
        updateStatus(ThingStatus.ONLINE);
        // connector.run();
    }

    @Override
    public void errorOccurred(Exception e) {
        logger.warn(e.getMessage());
        if (e instanceof InterruptedException) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        }
    }

    private void cancelIfPulsing(PortData portData) {
        if (portData.pulsing != null) {
            portData.pulsing.cancel(true);
            portData.pulsing = null;
        }
    }

    private @Nullable Channel getChannelForPort(String port) {
        String portKind = port.substring(0, 1);
        String portNum = port.replace(portKind, "");
        return thing.getChannel(portKind + "#" + portNum);
    }

    @Override
    public void dataReceived(String port, Double value) {
        Channel channel = getChannelForPort(port);
        PortData portData = portDatas.get(channel.getUID().getId());
        if (portData != null) {
            if (value.equals(portData.value)) {
                return;
            }

            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            boolean initializing = portData.value == -1;
            long sinceLastChange = Duration.between(portData.timestamp, now).toMillis();
            Configuration configuration = channel.getConfiguration();
            State state = UnDefType.UNDEF;
            switch (channel.getUID().getGroupId()) {
                case GROUP_COUNTER:
                    state = new DecimalType(value);
                    break;
                case GROUP_ANALOG_INPUT:
                    AnalogInputConfiguration config = configuration.as(AnalogInputConfiguration.class);
                    long histeresis = config.histeresis / 2;
                    if ((value < portData.value + histeresis || value > portData.value - histeresis) && !initializing) {
                        return;
                    }
                    state = new DecimalType(value);
                    break;
                case GROUP_DIGITAL_INPUT:
                    DigitalInputConfiguration config2 = configuration.as(DigitalInputConfiguration.class);
                    if (config2.debouncePeriod != 0
                            && now.isBefore(portData.timestamp.plus(config2.debouncePeriod, ChronoUnit.MILLIS))) {
                        return;
                    }
                    cancelIfPulsing(portData);
                    if (value == 1) {
                        state = OpenClosedType.CLOSED;
                        if (portData.value != -1) {
                            triggerPushButtonChannel(channel, EVENT_PRESSED);
                        }
                        if (config2.longPressTime != 0 && !initializing) {
                            scheduler.schedule(new LongPressEvaluator(channel, port, portData), config2.longPressTime,
                                    TimeUnit.MILLISECONDS);
                        } else if (config2.pulsePeriod != 0) {
                            portData.pulsing = scheduler.scheduleAtFixedRate(() -> {
                                triggerPushButtonChannel(channel, EVENT_PULSE);
                            }, config2.pulsePeriod, config2.pulsePeriod, TimeUnit.MILLISECONDS);
                            if (config2.pulseTimeout != 0) {
                                scheduler.schedule(() -> {
                                    cancelIfPulsing(portData);
                                }, config2.pulseTimeout, TimeUnit.MILLISECONDS);
                            }
                        }
                    } else {
                        state = OpenClosedType.OPEN;
                        triggerPushButtonChannel(channel, EVENT_RELEASED);
                        if (config2.longPressTime != 0 && sinceLastChange < config2.longPressTime && !initializing) {
                            triggerPushButtonChannel(channel, EVENT_SHORT_PRESS);
                        }
                    }
                    break;
                case GROUP_RELAY_OUTPUT:
                    state = value == 1 ? OnOffType.ON : OnOffType.OFF;
                    break;
            }
            updateState(channel.getUID().getId(), state);
            if (!initializing) {
                updateState(channel.getUID().getId() + PROPERTY_SEPARATOR + TIMESTAMP_CHANNEL_NAME,
                        new DateTimeType(now));
                updateState(channel.getUID().getId() + PROPERTY_SEPARATOR + LAST_STATE_DURATION_CHANNEL_NAME,
                        new QuantityType<>(sinceLastChange / 1000, SmartHomeUnits.SECOND));
            }
            portData.value = value;
            portData.timestamp = now;
        } else {
            logger.info("Received data '{}' for not configured port '{}'", value, port);
        }
    }

    protected void triggerPushButtonChannel(Channel channel, String event) {
        logger.debug("Triggering event '{}' on channel '{}'", event, channel.getUID());
        triggerChannel(channel.getUID().getId() + PROPERTY_SEPARATOR + CHANNEL_TYPE_PUSH_BUTTON_TRIGGER, event);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Received channel: {}, command: {}", channelUID, command);

        String groupId = channelUID.getGroupId();
        String channelName = channelUID.getIdWithoutGroup();

        if (channelName.chars().allMatch(Character::isDigit)) {
            switch (groupId) {
                case GROUP_RELAY_OUTPUT:
                    if (command instanceof OnOffType) {
                        Channel channel = thing.getChannel(channelUID.getId());
                        if (channel != null) {
                            RelayOutputConfiguration config = channel.getConfiguration()
                                    .as(RelayOutputConfiguration.class);
                            parser.setOutput(channelName, (OnOffType) command == OnOffType.ON ? 1 : 0, config.pulse);
                            return;
                        }

                    }
                case GROUP_COUNTER:
                    parser.resetCounter(channelName);
                    return;
            }
        }
        logger.warn("Can not handle command '{}' on channel '{}'", command, channelUID);
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        logger.debug("channelLinked: {}", channelUID);
        String channelId = channelUID.getId();
        String channelName = channelUID.getIdWithoutGroup();
        if (channelName.chars().allMatch(Character::isDigit)) {
            portDatas.put(channelId, new PortData());
        }
    }

    @Override
    public void channelUnlinked(ChannelUID channelUID) {
        super.channelUnlinked(channelUID);
        portDatas.remove(channelUID.getId());
    }

}
