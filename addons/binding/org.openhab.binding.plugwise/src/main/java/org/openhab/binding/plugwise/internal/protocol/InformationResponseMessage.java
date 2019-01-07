/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.plugwise.internal.protocol;

import static java.time.ZoneOffset.UTC;
import static org.openhab.binding.plugwise.internal.protocol.field.MessageType.DEVICE_INFORMATION_RESPONSE;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openhab.binding.plugwise.internal.protocol.field.DeviceType;
import org.openhab.binding.plugwise.internal.protocol.field.MACAddress;

/**
 * Contains generic device information. This message is the response of an {@link InformationRequestMessage}.
 *
 * @author Wouter Born, Karel Goderis - Initial contribution
 */
public class InformationResponseMessage extends Message {

    private static final Pattern PAYLOAD_PATTERN = Pattern
            .compile("(\\w{16})(\\w{2})(\\w{2})(\\w{4})(\\w{8})(\\w{2})(\\w{2})(\\w{12})(\\w{8})(\\w{2})");

    private int year;
    private int month;
    private int minutes;
    private int logAddress;
    private boolean powerState;
    private int hertz;
    private String hardwareVersion;
    private LocalDateTime firmwareVersion;
    private DeviceType deviceType;

    public InformationResponseMessage(int sequenceNumber, String payload) {
        super(DEVICE_INFORMATION_RESPONSE, sequenceNumber, payload);
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public LocalDateTime getFirmwareVersion() {
        return firmwareVersion;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public int getHertz() {
        return (hertz == 133) ? 50 : 60;
    }

    public int getLogAddress() {
        return logAddress;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getMonth() {
        return month;
    }

    public boolean getPowerState() {
        return powerState;
    }

    public int getYear() {
        return year;
    }

    private DeviceType intToDeviceType(int i) {
        switch (i) {
            case 0:
                return DeviceType.STICK;
            case 1:
                return DeviceType.CIRCLE_PLUS;
            case 2:
                return DeviceType.CIRCLE;
            case 3:
                return DeviceType.SWITCH;
            case 5:
                return DeviceType.SENSE;
            case 6:
                return DeviceType.SCAN;
            case 9:
                return DeviceType.STEALTH;
            default:
                return null;
        }
    }

    @Override
    protected void parsePayload() {
        Matcher matcher = PAYLOAD_PATTERN.matcher(payload);
        if (matcher.matches()) {
            macAddress = new MACAddress(matcher.group(1));
            year = Integer.parseInt(matcher.group(2), 16) + 2000;
            month = Integer.parseInt(matcher.group(3), 16);
            minutes = Integer.parseInt(matcher.group(4), 16);
            logAddress = (Integer.parseInt(matcher.group(5), 16) - 278528) / 32;
            powerState = (matcher.group(6).equals("01"));
            hertz = Integer.parseInt(matcher.group(7), 16);
            hardwareVersion = matcher.group(8).substring(0, 4) + "-" + matcher.group(8).substring(4, 8) + "-"
                    + matcher.group(8).substring(8, 12);
            firmwareVersion = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(matcher.group(9), 16)), UTC);
            deviceType = intToDeviceType(Integer.parseInt(matcher.group(10), 16));
        } else {
            throw new PlugwisePayloadMismatchException(DEVICE_INFORMATION_RESPONSE, PAYLOAD_PATTERN, payload);
        }
    }

}
