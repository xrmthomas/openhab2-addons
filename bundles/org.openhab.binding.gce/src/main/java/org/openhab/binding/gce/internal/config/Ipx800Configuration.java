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
package org.openhab.binding.gce.internal.config;

/**
 * The {@link Ipx800Configuration} class holds configuration informations of
 * the ipx800v3 thing.
 *
 * @author Gaël L'hopital - Initial implementation
 */
public class Ipx800Configuration {
    public String hostname;
    public Integer portNumber;

    public Integer reconnectTimeout;
    public Integer sendTimeout;
    public Integer maxKeepAliveFailure = 1;
    public Integer keepAlivePeriod;
    public Integer keepaliveTimeout;
}
