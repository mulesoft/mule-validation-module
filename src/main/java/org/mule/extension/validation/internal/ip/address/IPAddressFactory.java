/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.ip.address;

import java.util.regex.Pattern;

/**
 * IP address factory.
 *
 * @since 1.1
 */
public class IPAddressFactory {

  private static final Pattern IPV4_PATTERN =
      Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
  private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
  private static final Pattern IPV6_HEX_COMPRESSED_PATTERN =
      Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

  public IPAddress create(String ipAddress) {
    if (isIPv4(ipAddress)) {
      return new IPv4Address(ipAddress);
    } else if (isIPv6(ipAddress)) {
      return new IPv6Address(ipAddress);
    }
    return new InvalidIPAddress(ipAddress);
  }

  private boolean isIPv4(final String input) {
    return IPV4_PATTERN.matcher(input).matches();
  }

  private boolean isIPv6(final String input) {
    return isIPv6Std(input) || isIPv6HexCompressed(input);
  }

  private boolean isIPv6Std(final String input) {
    // This is used to match IPV6 addresses with scopes
    String ip = input.indexOf("%") > 0 ? input.substring(0, input.indexOf("%")) : input;
    return IPV6_STD_PATTERN.matcher(ip).matches();
  }

  private boolean isIPv6HexCompressed(final String input) {
    return IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
  }

}
