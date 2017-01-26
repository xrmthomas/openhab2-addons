/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.xpl.config;

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

    private String[] addresses = null;
    private String[] units = null;

    public String[] getAddresses() {
        if (addresses == null) {
            addresses = new String[6];
            addresses[0] = address1;
            addresses[1] = address2;
            addresses[2] = address3;
            addresses[3] = address4;
            addresses[4] = address5;
            addresses[5] = address6;
        }
        return addresses;
    }

    public String[] getUnits() {
        if (units == null) {
            units = new String[6];
            units[0] = unit1.toString();
            units[1] = unit2.toString();
            units[2] = unit3.toString();
            units[3] = unit4.toString();
            units[4] = unit5.toString();
            units[5] = unit6.toString();
        }
        return addresses;
    }

}
