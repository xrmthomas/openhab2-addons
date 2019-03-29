/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ipx800.internal.config;

/**
 * The {@link Ipx800Configuration} class holds configuration informations of
 * the ipx800.
 *
 * @author Gaël L'hopital - Port to OH2
 */
public class Ipx800Configuration {
    public String hostname;
    public Integer portNumber;

    public Integer reconnectTimeout;
    public Integer sendTimeout;
    public Integer keepaliveTimeout;
    public Integer maxKeepAliveFailure;
}
