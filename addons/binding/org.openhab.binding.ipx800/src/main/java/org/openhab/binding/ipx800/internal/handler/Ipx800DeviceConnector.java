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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.ipx800.internal.config.Ipx800Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link Ipx800DeviceConnector} is responsible for connecting,
 * reading, writing and disconnecting from the Ipx800.
 *
 * @author Seebag - Initial contribution
 * @author Gaël L'hopital - Port to OH2
 */
@NonNullByDefault
public class Ipx800DeviceConnector extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Ipx800DeviceConnector.class);
    private final static String ENDL = "\r\n";

    private final Ipx800Configuration config;
    private @Nullable Ipx800MessageParser parser = null;

    private boolean interrupted = false;
    private boolean connected = false;

    private @NonNullByDefault({}) Socket client;
    private @NonNullByDefault({}) BufferedReader in;
    private @NonNullByDefault({}) PrintWriter out;

    private int failedKeepalive = 0;
    private boolean waitingKeepaliveResponse = false;

    /**
     *
     * @param config
     */
    public Ipx800DeviceConnector(Ipx800Configuration config) {
        this.config = config;
    }

    public synchronized void send(String message) {
        logger.debug("Sending '{}' to Ipx800", message);
        // Ceci marche-t'il aussi avec out.println(message) ?
        out.write(message + ENDL);
    }

    /**
     * Connect to the ipx800
     *
     * @throws IOException
     */
    private void connect() throws IOException {
        disconnect();
        logger.debug("Connecting {}:{}...", config.hostname, config.portNumber);
        client = new Socket(config.hostname, config.portNumber);
        client.setSoTimeout(config.keepaliveTimeout);
        client.getInputStream().skip(client.getInputStream().available());
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        connected = true;
        logger.debug("Connected to {}:{}", config.hostname, config.portNumber);
    }

    /**
     * Disconnect the device
     */
    public void disconnect() {
        if (connected) {
            logger.debug("Disconnecting");
            try {
                client.close();
            } catch (IOException e) {
                logger.error("Unable to disconnect {}", e.getMessage());
            }
            connected = false;
            logger.debug("Disconnected");
        }
    }

    /**
     * Stop the device thread
     */
    public void destroyAndExit() {
        interrupted = true;
        disconnect();
    }

    /**
     * Send an arbitrary keepalive command which cause the IPX to send an update.
     * If we don't receive the update maxKeepAliveFailure time, the connection is closed and reopened
     */
    private void sendKeepalive() {
        if (waitingKeepaliveResponse) {
            failedKeepalive++;
            logger.debug("Sending keepalive, attempt {}", failedKeepalive);
        } else {
            failedKeepalive = 0;
            logger.trace("Sending keepalive");
        }
        out.println("GetIn01");
        out.flush();
        waitingKeepaliveResponse = true;
    }

    @Override
    public void run() {
        interrupted = false;
        while (!interrupted) {
            try {
                waitingKeepaliveResponse = false;
                failedKeepalive = 0;
                connect();
                while (!interrupted) {
                    if (failedKeepalive > config.maxKeepAliveFailure) {
                        throw new IOException("Max keep alive attempts has been reached");
                    }
                    try {
                        String command = in.readLine();
                        waitingKeepaliveResponse = false;
                        if (parser != null) {
                            parser.unsollicitedUpdate(command);
                        }
                    } catch (SocketTimeoutException e) {
                        sendKeepalive();
                    }
                }
                disconnect();
            } catch (IOException e) {
                logger.error(e.getMessage() + " will retry in " + config.reconnectTimeout + "ms");
            }
            try {
                Thread.sleep(config.reconnectTimeout);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void setParser(Ipx800MessageParser parser) {
        this.parser = parser;
    }
}
