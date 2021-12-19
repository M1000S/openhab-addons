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

import static org.openhab.binding.networkmap.internal.networkmapBindingConstants.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.networkmap.internal.subnet.Subnet;
import org.openhab.core.io.net.exec.ExecUtil;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link networkmapHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author PeterS - Initial contribution
 */
@NonNullByDefault
public class networkmapHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(networkmapHandler.class);
    private Subnet mySubnet;
    private networkmapConfiguration config = new networkmapConfiguration();
    private @Nullable ScheduledFuture<?> refreshJob;
    protected @Nullable ExecutorService executorService;

    public networkmapHandler(Thing thing) {
        super(thing);
        mySubnet = new Subnet();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_ID_NUMBER_OF_HOSTS.equals(channelUID.getId())) {
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

    public void performPortScan(Integer hostId) {
        String hostAddr = mySubnet.getHost(hostId).getHostIPv4();
        logger.info("Thread: {}", hostAddr);
        String cmndline = ExecUtil.executeCommandLineAndWaitResponse(Duration.ofMinutes(2), "sudo", "/bin/nmap", "-oG",
                "-", hostAddr);
        logger.info(cmndline);
        mySubnet.parseNmapGreppable(cmndline, 0);
    }

    public boolean performScan() {
        /*
         * String cmndline = ExecUtil.executeCommandLineAndWaitResponse(Duration.ofMinutes(5), "sudo", "/bin/nmap",
         * "-oX",
         * "testx.xml", "192.168.201.0/24");
         *
         * NmapParser nmapP = new NmapParser("testx.xml");
         */
        String cmndline = ExecUtil.executeCommandLineAndWaitResponse(Duration.ofMinutes(2), "sudo", "/bin/nmap", "-sn",
                "-oG", "-", config.subnet);

        this.mySubnet.parseNmapGreppable(cmndline, 1);
        logger.info("{}", mySubnet.getHostCount());
        logger.info(mySubnet.getHostNames(config.domain));
        logger.info(mySubnet.getHostAddresses());

        updateState(CHANNEL_ID_NAMES_OF_HOSTS, new StringType(mySubnet.getHostNames(config.domain)));
        updateState(CHANNEL_ID_ADDRESSES_OF_HOSTS, new StringType(mySubnet.getHostAddresses()));
        BigDecimal decimalValue = new BigDecimal(mySubnet.getHostCount());
        updateState(CHANNEL_ID_NUMBER_OF_HOSTS, new DecimalType(decimalValue));
        updateState(CHANNEL_ID_STATE_OF_HOSTS, new StringType(mySubnet.getHostStates()));
        updateState(CHANNEL_ID_SEEN_OF_HOSTS, new StringType(mySubnet.getHostLastSeen()));
        updateState(CHANNEL_ID_UPDATE_OF_HOSTS, new StringType(mySubnet.getHostLastUpdate()));

        if (this.executorService == null) {
            final ExecutorService executorService = Executors.newFixedThreadPool(mySubnet.getHostCount());
            this.executorService = executorService;

            for (int i = 0; i < mySubnet.getHostCount(); i++) {
                Integer hostId = i;
                executorService.execute(() -> {
                    Thread.currentThread().setName("portscan_" + mySubnet.getHost(hostId).getHostName());
                    performPortScan(hostId);
                });
            }
            try {
                executorService.awaitTermination(90, TimeUnit.SECONDS);
                executorService.shutdownNow();
                this.executorService = null;
                logger.info("Thread terminated: {}", executorService.isTerminated());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Reset interrupt flag
                executorService.shutdownNow();
            }
        }
        return true;
    }

    private void startAutomaticScan() {
        ScheduledFuture<?> future = refreshJob;
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        refreshJob = scheduler.scheduleWithFixedDelay(() -> performScan(), 0, 5, TimeUnit.MINUTES);

    }

    @Override
    public void initialize() {
        config = getConfigAs(networkmapConfiguration.class);

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.
        logger.info(config.subnet);
        logger.info("Scan ports? {}", config.ports);
        logger.info("Refresh  {}", config.refreshInterval);

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);
        mySubnet = new Subnet();
        // Example for background initialization:
        scheduler.execute(() -> {
            boolean thingReachable = false; // <background task with long running initialization here>
            String result = ExecUtil.executeCommandLineAndWaitResponse(Duration.ofMillis(100), "sudo", "/bin/nmap");
            if (result != null && !result.isBlank()) {
                if (result.contains("nmap.org")) {
                    thingReachable = true;
                }
            }

            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
                startAutomaticScan();
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                        "Ensure nmap is installed, path to nmap is configured correct and nmap can be sudoed without password.");
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

    @Override
    public void dispose() {
        ScheduledFuture<?> future = refreshJob;
        if (future != null) {
            if (!future.isDone()) {
                future.cancel(true);
            }
            refreshJob = null;
        }
    }

}
