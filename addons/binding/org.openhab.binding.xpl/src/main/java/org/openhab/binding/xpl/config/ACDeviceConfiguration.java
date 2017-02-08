/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xpl.config;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link ACDeviceConfiguration} is responsible for holding
 * configuration informations needed to define an xPL Device
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class ACDeviceConfiguration extends XplDeviceConfiguration {
    public String address1;
    public Integer unit1;
    public String address2;
    public Integer unit2;
    public String address3;
    public Integer unit3;
    public String address4;
    public Integer unit4;
    public String address5;
    public Integer unit5;
    public String address6;
    public Integer unit6;

    private Map<String, Integer> addresses = null;

    public Map<String, Integer> getAddresses() {
        if (addresses == null) {
            addresses = new HashMap<String, Integer>();
            addresses.put(address1, unit1);
            addresses.put(address2, unit2);
            addresses.put(address3, unit3);
            addresses.put(address4, unit4);
            addresses.put(address5, unit5);
            addresses.put(address6, unit6);
        }
        return addresses;
    }

}
