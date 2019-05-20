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

import org.eclipse.smarthome.config.core.Configuration;

/**
 * The {@link DigitalInputConfiguration} class holds configuration informations of
 * an ipx800 Digital Input port.
 *
 * @author Gaël L'hopital - Initial Implementation
 */
public class DigitalInputConfiguration extends Configuration {
    public Long debouncePeriod;
    public Long longPressTime;
    public Long pulsePeriod;
    public Long pulseTimeout;
}
