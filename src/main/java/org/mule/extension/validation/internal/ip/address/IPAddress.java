/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.ip.address;

import static java.lang.String.format;
import static org.mule.extension.validation.api.ValidationErrorType.INVALID_IP;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

import org.mule.extension.validation.internal.ip.IpMatcher;
import org.mule.runtime.api.i18n.I18nMessage;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.util.regex.Pattern;

/**
 * Represents a generic IP address.
 *
 * @since 1.1
 */
public abstract class IPAddress {

  private static final String IPV4_PARTIAL_ADDRESS = "(\\d{1,3})(\\.(\\d{1,3})(\\.(\\d{1,3}))?)?";
  private static final Pattern IPV4_PARTIAL_ADDRESS_PATTERN = Pattern.compile(IPV4_PARTIAL_ADDRESS);

  private final String internalValue;

  public IPAddress(String ipAddress) {
    this.internalValue = ipAddress;
  }

  public abstract boolean isValid();

  public boolean matches(IPAddress anotherIPAddress) {
    String ipRange = anotherIPAddress.internalValue;
    String ipAddress = this.internalValue;

    if (IPV4_PARTIAL_ADDRESS_PATTERN.matcher(ipRange).matches()) {
      return validatePartialIpv4(ipRange, ipAddress);
    }
    return matcherFor(ipRange).matchIp(ipAddress);
  }

  private IpMatcher matcherFor(String ipRange) {
    IpMatcher ipAddressMatcher;

    try {
      ipAddressMatcher = new IpMatcher(ipRange);
    } catch (Exception e) {
      I18nMessage message = createStaticMessage(format("'%s' is not a valid IP address or range"), ipRange);
      throw new ModuleException(message, INVALID_IP, e);
    }
    return ipAddressMatcher;
  }

  private boolean validatePartialIpv4(String partialIp, String ipAddress) {
    if (!partialIp.endsWith(".")) {
      partialIp = partialIp + ".";
    }
    String formattedPartialIp = partialIp.replaceAll("\\.", "\\\\.");
    return Pattern.compile(formattedPartialIp).matcher(ipAddress).find();
  }
}
