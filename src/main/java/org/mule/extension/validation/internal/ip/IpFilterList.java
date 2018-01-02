/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.ip;

import static java.util.stream.Collectors.toList;

import org.mule.extension.validation.internal.ip.address.IPAddress;
import org.mule.extension.validation.internal.ip.address.IPAddressFactory;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.List;

/**
 * List of IPs for filter configuration.
 *
 * @since 1.1
 */
@TypeDsl(allowTopLevelDefinition = true)
public class IpFilterList {

  private IPAddressFactory factory = new IPAddressFactory();

  @Parameter
  private List<String> ips;

  public List<String> getIps() {
    return ips;
  }

  public List<IPAddress> ipAddresses() {
    return ips.stream().map(factory::create).collect(toList());
  }

}
