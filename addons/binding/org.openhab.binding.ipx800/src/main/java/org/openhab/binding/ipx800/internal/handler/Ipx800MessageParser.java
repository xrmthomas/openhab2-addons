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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.openhab.binding.ipx800.internal.itemslot.Ipx800OutputItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NonNullByDefault
public class Ipx800MessageParser {
    private static final Logger logger = LoggerFactory.getLogger(Ipx800DeviceConnector.class);
    private static final String IO_DESCRIPTOR = "(\\d{32})";
    private static final Pattern IO_PATTERN = Pattern.compile(IO_DESCRIPTOR);
    private static final Pattern VALIDATION_PATTERN = Pattern
            .compile("I=" + IO_DESCRIPTOR + "&O=" + IO_DESCRIPTOR + "&([AC]\\d{1,2}=\\d+&)*[^I]*");

    private String expectedResponse = "";
    // private int expectedPortResponse = -1;
    private final Ipx800DeviceConnector connector;

    private List<Ipx800EventListener> listeners = new ArrayList<>();

    public Ipx800MessageParser(Ipx800DeviceConnector connector) {
        this.connector = connector;
        connector.setParser(this);
    }

    public synchronized void addEventListener(Ipx800EventListener ipx800EventListener) {
        if (!listeners.contains(ipx800EventListener)) {
            listeners.add(ipx800EventListener);
        }
    }

    /**
     * Set output of the device sending the command corresponding to the state to the device
     *
     * @param slot
     * @param state
     */
    public void setOutput(int portNumber, org.eclipse.smarthome.core.types.State state) {
        logger.debug("Sending {} to {}", state, portNumber);
        connector.send(String.format("Set%02d%d", portNumber, state == OnOffType.ON ? 1 : 0));
    }

    /**
     * FIXME use only this method using items also for redirect
     *
     * @param slot
     * @param item
     */
    public synchronized void setOutput(Ipx800OutputItem item) {
        org.eclipse.smarthome.core.types.State state = item.getState();
        Ipx800Port port = item.getPort();
        if (item.isPulseMode()) {
            logger.debug("Sending {} to {} in pulse mode", state, port);
            connector.send(String.format("Set%02d%dp", port.getPortNumber(), state == OnOffType.ON ? 1 : 0));
        } else {
            logger.debug("Sending {} to {}", state, port);
            connector.send(String.format("Set%02d%d", port.getPortNumber(), state == OnOffType.ON ? 1 : 0));
        }
    }

    /**
     *
     * @param data
     */
    public void unsollicitedUpdate(String data) {
        final Matcher matcher2 = IO_PATTERN.matcher(data);
        if (matcher2.matches()) {
            String portKind = "GetOutputs".equalsIgnoreCase(expectedResponse) ? "O"
                    : "GetInputs".equalsIgnoreCase(expectedResponse) ? "I" : null;
            if (portKind != null) {
                for (int count = 0; count < data.length(); count++) {
                    setStatus(portKind + String.valueOf(count), new Double(data.charAt(count) - '0'));
                    // setStatus(portKind, count, new Double(data.charAt(count) - '0'));
                }
                expectedResponse = "";
            }
        } else {
            final Matcher matcher = VALIDATION_PATTERN.matcher(data);
            if (matcher.matches()) { // Workaround of an IPX800 bug
                for (String status : data.split("&")) {
                    String statusPart[] = status.split("=");
                    String portKind = statusPart[0].substring(0, 1);
                    int portNumShift = 0;
                    switch (portKind) {
                        case "I":
                        case "O": {
                            for (int count = 0; count < statusPart[1].length(); count++) {
                                setStatus(portKind + String.valueOf(count), new Double(data.charAt(count) - '0'));
                                // setStatus(portKind, count, new Double(statusPart[1].charAt(count) - '0'));
                            }
                            break;
                        }
                        case "C":
                            portNumShift = -1; // Align counters on 0 based array
                        case "A": {
                            int portNumber = Integer.parseInt(statusPart[0].substring(1));
                            setStatus(portKind + String.valueOf(portNumber + portNumShift),
                                    Double.parseDouble(statusPart[1]));
                            // setStatus(portKind, portNumber + portNumShift, Double.parseDouble(statusPart[1]));
                        }
                    }
                }
            } // else if ("GetCount".equals(expectedResponse) || "GetAn".equals(expectedResponse)) {
              // setStatus(expectedResponse.substring(3, 4), expectedPortResponse, Double.parseDouble(data)); }
            else /* if (expectedResponse.startsWith("GetCount") || expectedResponse.startsWith("GetAn")) */ {
                setStatus(expectedResponse, Double.parseDouble(data));
                expectedResponse = "";
            }

        }
    }

    // private void setStatus(String portKind, int portNumber, Double value) {
    // listeners.forEach(listener -> listener.dataReceived(portKind, portNumber, value));
    // }

    private void setStatus(String port, Double value) {
        System.out.println(String.format("Received %s : %s", port, value.toString()));
        listeners.forEach(listener -> listener.dataReceived(port, value));
    }

    /*
     * private @Nullable String intAtEnd(String string) {
     * int i, j;
     * i = j = string.length();
     * while (--i > 0) {
     * if (Character.isDigit(string.charAt(i))) {
     * continue;
     * }
     * i++;
     * break;
     * }
     * if (j - i > 0) {
     * return string.substring(i);
     * }
     * return null;
     * }
     */

    public void setExpectedResponse(String expectedResponse) {
        if (expectedResponse.endsWith("s")) { // GetInputs or GetOutputs
            this.expectedResponse = expectedResponse;
        } else { // GetAnx or GetCountx
            this.expectedResponse = expectedResponse.replaceAll("GetAn", "A").replaceAll("GetCount", "C")
                    .replaceAll("GetIn", "I").replaceAll("GetOut", "O");
            /*
             * String portNum = intAtEnd(expectedResponse);
             * if (portNum != null) {
             * this.expectedResponse = expectedResponse.substring(0, expectedResponse.length() - portNum.length());
             * expectedPortResponse = Integer.parseInt(portNum) - 1;
             * } else {
             * listeners.forEach(
             * listener -> listener.errorOccurred("Unandled expected response : " + expectedResponse));
             * }
             */
        }
    }

}
