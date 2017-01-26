package org.openhab.binding.xpl.handler;

import static org.openhab.binding.xpl.XplBindingConstants.*;

import org.cdp1802.xpl.NamedValuesI;
import org.cdp1802.xpl.xPL_MessageI;
import org.cdp1802.xpl.xPL_MessageI.MessageType;
import org.cdp1802.xpl.xPL_MutableMessageI;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.xpl.config.OregonDeviceConfiguration;

public class OregonDeviceHandler extends XplDeviceHandler<OregonDeviceConfiguration> {
    private static String deviceKey = null;
    private String deviceId = null;

    public OregonDeviceHandler(Thing thing) {
        super(thing, OregonDeviceConfiguration.class);
    }

    @Override
    public void initialize() {
        super.initialize();
        deviceKey = getProperty("idString1");
        deviceId = configuration.type + " " + configuration.id;
    }

    @Override
    protected boolean targetedBy(xPL_MessageI theMessage) {
        boolean result = false;
        String device = theMessage.getNamedValue(deviceKey);

        if (device != null) {
            result = device.equalsIgnoreCase(deviceId);
            result = result
                    && xplSchema.equalsIgnoreCase(theMessage.getSchemaClass() + "." + theMessage.getSchemaType());
            result = result && theMessage.getType() == MessageType.STATUS;
        }

        return result;
    }

    @Override
    protected void internalHandleMessage(NamedValuesI messageBody) {
        String messageChannelType = messageBody.getNamedValue("type");
        Channel channel = getThing().getChannel(messageChannelType);
        if (channel != null) {
            String currentValue = messageBody.getNamedValue("current");
            State state = new DecimalType(currentValue);
            updateState(channel.getUID(), state);
            if (messageChannelType.equalsIgnoreCase(CHANNEL_BATTERY_LEVEL)) {
                updateState(CHANNEL_LOW_BATTERY, isLowBattery(state));
            }
            if (messageChannelType.equalsIgnoreCase(CHANNEL_HUMIDITY)) {
                StringType description = new StringType(messageBody.getNamedValue("description"));
                updateState(CHANNEL_HUMIDITY_STATUS, description);
            }
            if (messageChannelType.equalsIgnoreCase(CHANNEL_POWER)) {
                updateState(CHANNEL_INSTANT_AMP, new DecimalType(((DecimalType) state).doubleValue() * 1000 / 230));
            }
        }
    }

    /**
     * Check if battery level is below low battery threshold level.
     *
     * @param batteryLevel Internal battery level
     * @return OnOffType
     */
    private State isLowBattery(State batteryLevel) {
        final int lowBattery = Integer.parseInt(getProperty("lowBatteryLevel"));
        int level = ((DecimalType) batteryLevel).intValue();
        if (level <= lowBattery) {
            return OnOffType.ON;
        } else {
            return OnOffType.OFF;
        }
    }

    @Override
    protected void addIdentifiers(xPL_MutableMessageI message) {
        message.addNamedValue(deviceKey, deviceId);
    }

    @Override
    protected void internalHandleCommand(String channelId, Command command, xPL_MutableMessageI message) {
        // Not handling commands
    }

}
