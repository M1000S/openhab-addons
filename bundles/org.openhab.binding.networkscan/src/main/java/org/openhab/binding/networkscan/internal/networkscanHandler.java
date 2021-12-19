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
package org.openhab.binding.networkscan.internal;

import static org.openhab.binding.networkscan.internal.networkscanBindingConstants.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.networkscan.internal.nmapparser.NmapParser;
import org.openhab.core.io.net.exec.ExecUtil;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link networkscanHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author PeterS - Initial contribution
 */
@NonNullByDefault
public class networkscanHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(networkscanHandler.class);

    private @Nullable networkscanConfiguration config;
    private @Nullable ScheduledFuture<?> refreshJob;
    private int counter = 0;

    public networkscanHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_1.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    public boolean performPresenceDetection(boolean waitForDetectionToFinish) {
        String cmndline = ExecUtil.executeCommandLineAndWaitResponse(Duration.ofMinutes(5), "sudo", "/bin/nmap", "-oX",
                "testx.xml", "192.168.201.0/24");

        NmapParser nmapP = new NmapParser("testx.xml");

        counter++;

        updateState(CHANNEL_1, new StringType(nmapP.getHostString()));
        BigDecimal decimalValue = new BigDecimal(counter);
        updateState(CHANNEL_2, new DecimalType(decimalValue));
        decimalValue = new BigDecimal(nmapP.getHostCount());
        updateState(CHANNEL_3, new DecimalType(decimalValue));

        return true;
    }

    public void startAutomaticRefresh(ScheduledExecutorService scheduledExecutorService) {
        ScheduledFuture<?> future = refreshJob;
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        refreshJob = scheduledExecutorService.scheduleWithFixedDelay(() -> performPresenceDetection(true), 0, 5,
                TimeUnit.MINUTES);
    }

    @Override
    public void dispose() {
        refreshJob = null;
    }

    @Override
    public void initialize() {
        config = getConfigAs(networkscanConfiguration.class);

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            boolean thingReachable = true; // <background task with long running initialization here>
            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
                updateState(CHANNEL_1, new StringType(new String("First Update")));
                startAutomaticRefresh(scheduler);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

        // These logging types should be primarily used by bindings
        // logger.trace("Example trace message");
        // logger.debug("Example debug message");
        // logger.warn("Example warn message");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }
}