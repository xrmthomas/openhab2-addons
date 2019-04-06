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
     * Set output of the device sending the corresponding command
     *
     * @param targetPort
     * @param targetValue
     */
    public void setOutput(String targetPort, int targetValue, boolean pulse) {
        logger.debug("Sending {} to {}", targetValue, targetPort);
        int port = Integer.parseInt(targetPort);
        String command = String.format("Set%02d%d", port, targetValue);
        command += pulse ? "p" : "";
        connector.send(command);
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
                }
                expectedResponse = "";
            }
        } else {
            final Matcher matcher = VALIDATION_PATTERN.matcher(data);
            if (matcher.matches()) {
                for (String status : data.split("&")) {
                    String statusPart[] = status.split("=");
                    String portKind = statusPart[0].substring(0, 1);
                    int portNumShift = 0;
                    switch (portKind) {
                        case "I":
                        case "O": {
                            for (int count = 0; count < statusPart[1].length(); count++) {
                                setStatus(portKind + String.valueOf(count + 1),
                                        new Double(statusPart[1].charAt(count) - '0'));
                            }
                            break;
                        }
                        case "C":
                            portNumShift = -1; // Align counters on 1 based array
                        case "A": {
                            int portNumber = Integer.parseInt(statusPart[0].substring(1));
                            setStatus(portKind + String.valueOf(portNumber + portNumShift + 1),
                                    Double.parseDouble(statusPart[1]));
                        }
                    }
                }
            } else if (expectedResponse != "") {
                setStatus(expectedResponse, Double.parseDouble(data));
                expectedResponse = "";
            }

        }
    }

    private void setStatus(String port, Double value) {
        System.out.println(String.format("Received %s : %s", port, value.toString()));
        listeners.forEach(listener -> listener.dataReceived(port, value));
    }

    public void setExpectedResponse(String expectedResponse) {
        if (expectedResponse.endsWith("s")) { // GetInputs or GetOutputs
            this.expectedResponse = expectedResponse;
        } else { // GetAnx or GetCountx
            this.expectedResponse = expectedResponse.replaceAll("GetAn", "A").replaceAll("GetCount", "C")
                    .replaceAll("GetIn", "I").replaceAll("GetOut", "O");
        }
    }

    /**
     * Resets the counter value to 0
     *
     * @param targetCounter
     */
    public void resetCounter(String targetCounter) {
        logger.debug("Resetting counter {} to 0", targetCounter);
        int counter = Integer.parseInt(targetCounter);
        connector.send(String.format("ResetCount%d", counter));
        try {
            Thread.sleep(200);
            String request = String.format("GetCount%d", counter);
            setExpectedResponse(request);
            connector.send(request);
        } catch (InterruptedException e) {
            errorOccurred(e);
        }
    }

    public void errorOccurred(Exception e) {
        System.out.println(String.format("Error received from connector : %s", e.getMessage()));
        listeners.forEach(listener -> listener.errorOccurred(e));
    }

}
