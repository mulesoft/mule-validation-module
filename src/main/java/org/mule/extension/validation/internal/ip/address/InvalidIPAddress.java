/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.ip.address;

/**
 * Represents an invalid IP address.
 *
 * @since 1.1
 */
public class InvalidIPAddress extends IPAddress {

  public InvalidIPAddress(String ipAddress) {
    super(ipAddress);
  }

  @Override
  public boolean isValid() {
    return false;
  }
}
