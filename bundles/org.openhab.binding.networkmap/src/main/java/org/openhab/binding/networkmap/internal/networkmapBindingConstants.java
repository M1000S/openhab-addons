/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.binding.networkmap.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link networkmapBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author PeterS - Initial contribution
 */
@NonNullByDefault
public class networkmapBindingConstants {

    private static final String BINDING_ID = "networkmap";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "subnetscan");

    // List of all Channel ids
    public static final String CHANNEL_ID_NUMBER_OF_HOSTS = "NumberOfHosts";
    public static final String CHANNEL_ID_NAMES_OF_HOSTS = "NamesOfHosts";
    public static final String CHANNEL_ID_ADDRESSES_OF_HOSTS = "AddressesOfHosts";
    public static final String CHANNEL_ID_PORTS_OF_HOSTS = "PortsOfHosts";
}
