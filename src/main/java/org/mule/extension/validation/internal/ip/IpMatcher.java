/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.ip;

import org.mule.runtime.core.api.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * IP address range matcher.
 *
 * @since 1.1
 */
public class IpMatcher {

  private int mask;
  private InetAddress address;

  public IpMatcher(String ipAddress) {

    if (ipAddress.indexOf('/') > 0) {
      String[] addressAndMask = StringUtils.splitAndTrim(ipAddress, "/");
      ipAddress = addressAndMask[0];
      mask = Integer.parseInt(addressAndMask[1]);
    } else {
      mask = -1;
    }
    address = parseAddress(ipAddress);
  }

  public boolean matchIp(String ipAddress) {
    InetAddress remoteAddress = parseAddress(ipAddress);
    if (!address.getClass().equals(remoteAddress.getClass())) {
      return false;
    }

    if (mask < 0) {
      return remoteAddress.equals(address);
    }

    int oddBits = mask % 8;
    int nMaskBytes = mask / 8 + (oddBits == 0 ? 0 : 1);
    byte[] mask = new byte[nMaskBytes];

    Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);

    if (oddBits != 0) {
      int finalByte = (1 << oddBits) - 1;
      finalByte <<= 8 - oddBits;
      mask[mask.length - 1] = (byte) finalByte;
    }

    byte[] remoteAddressBytes = remoteAddress.getAddress();
    byte[] requiredAddressBytes = address.getAddress();

    for (int i = 0; i < mask.length; i++) {
      if ((remoteAddressBytes[i] & mask[i]) != (requiredAddressBytes[i] & mask[i])) {
        return false;
      }
    }

    return true;
  }

  private InetAddress parseAddress(String address) {
    try {
      return InetAddress.getByName(address);
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Failed to parse address" + address, e);
    }
  }
}
