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
package org.openhab.binding.gce.internal.test;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.Test;
import org.openhab.binding.gce.internal.config.Ipx800Configuration;
import org.openhab.binding.gce.internal.handler.Ipx800DeviceConnector;
import org.openhab.binding.gce.internal.handler.Ipx800MessageParser;

/**
 *
 * @author Seebag
 * @since 1.8.0
 *
 */
@NonNullByDefault
public class Ipx800DeviceConnectorTest {
    @Test
    public void validateDeviceListModel() {
        // Example of
        // I=00000000000000000000000000000000&O=10000000000000000000000000000000&\
        // A0=0&A1=0&A2=0&A3=0&A4=0&A5=0&A6=0&A7=0&A8=0&A9=0&A10=0&A11=0&A12=0&A13=0&A14=0&A15=0&C1=47&C2=0&C3=0&C4=0&C5=0&C6=0&C7=0&C8=0
        // Command :
        // I=01000000000000000000000000000000&O=01000000000000000000000000000000&A0=0&A1=0&A2=0&A3=0&A4=0&A5=0&A6=0&A7=0&A8=0&A9=0&A10=0&A11=0&A12=0&A13=0&A14=0&A15=0&C1=2064&C2=1&C3=3&C4=4&C5=5&C6=6&C7=7&C8=8
        // Command :
        // I=01000000000000000000000000000000&O=00000000000000000000000000000000&A0=0&A1=0&A2=0&A3=0&A4=0&A5=0&A6=0&A7=0&A8=0&A9=0&A10=0&A11=0&A12=0&A13=0&A14=0&A15=0&C1=2064&C2=1&C3=3&C4=4&C5=5&C6=6&C7=7&C8=8
        // Command :
        // I=01000000000000000000000000000000&O=00000001000000000000000000000000&A0=0&A1=0&A2=0&A3=0&A4=0&A5=0&A6=0&A7=0&A8=0&A9=0&A10=0&A11=0&A12=0&A13=0&A14=0&A15=0&C1=2064&C2=1&C3=3&C4=4&C5=5&C6=6&C7=7&C8=8
        // Command :
        // I=00000000000000000000000000000000&O=00000001000000000000000000000000&A0=0&A1=0&A2=0&A3=0&A4=0&A5=0&A6=0&A7=0&A8=0&A9=0&A10=0&A11=0&A12=0&A13=0&A14=0&A15=0&C1=2064&C2=1&C3=3&C4=4&C5=5&C6=6&C7=7&C8=8
        // Command :
        // I=01000000000000000000000000000000&O=00000000000000000000000000000000&A0=0&A1=0&A2=0&A3=0&A4=0&A5=0&A6=0&A7=0&A8=0&A9=0&A10=0&A11=0&A12=0&A13=0&A14=0&A15=0&C1=2064&C2=1&C3=3&C4=4&C5=5&C6=6&C7=7&C8=8
        // Command :
        // I=00000000000000000000000000000000&O=00000000000000000000000000000000&A0=0&A1=0&A2=0&A3=0&A4=0&A5=0&A6=0&A7=0&A8=0&A9=0&A10=0&A11=0&A12=0&A13=0&A14=0&A15=0&C1=2064&C2=1&C3=3&C4=4&C5=5&C6=6&C7=7&C8=8
        // Command :
        // I=10000000000000000000000000000000&O=00000000000000000000000000000000&A0=0&A1=0&A2=0&A3=0&A4=0&A5=0&A6=0&A7=0&A8=0&A9=0&A10=0&A11=0&A12=0&A13=0&A14=0&A15=0&C1=2064&C2=1&C3=3&C4=4&C5=5&C6=6&C7=7&C8=8

        Ipx800Configuration configuration = new Ipx800Configuration();
        Ipx800DeviceConnector connector = new Ipx800DeviceConnector(configuration);
        Ipx800MessageParser parser = new Ipx800MessageParser(connector);
        parser.setExpectedResponse("GetOutputs");
        parser.unsollicitedUpdate("01010010100101010101010101010101");
        parser.setExpectedResponse("GetInputs");
        parser.unsollicitedUpdate("01000000234005670000000000000000");
        parser.unsollicitedUpdate(
                "I=01000000000000000000000000000000&O=01000000000000000000000000000000&A0=0&A1=0&A2=0&A3=0&A4=0&A5=0&A6=0&A7=0&A8=0&A9=0&A10=0&A11=0&A12=0&A13=0&A14=0&A15=0&C1=2064&C2=1&C3=3&C4=4&C5=5&C6=6&C7=7&C8=8");
        parser.setExpectedResponse("GetAn1");
        parser.unsollicitedUpdate("234");
        parser.setExpectedResponse("GetCount12");
        parser.unsollicitedUpdate("4294967295"); // Test maximum value
        parser.setExpectedResponse("GetIn5");
        parser.unsollicitedUpdate("1");
        parser.setExpectedResponse("GetOut3");
        parser.unsollicitedUpdate("0");
    }
}
