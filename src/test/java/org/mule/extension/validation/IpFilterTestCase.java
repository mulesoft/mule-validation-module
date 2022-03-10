/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.mule.extension.validation.api.ValidationErrorType.INVALID_IP;
import static org.mule.extension.validation.api.ValidationErrorType.REJECTED_IP;
import static org.mule.extension.validation.api.ValidationErrorType.VALIDATION;
import static org.mule.tck.junit4.matcher.ErrorTypeMatcher.errorType;

import org.mule.functional.api.flow.FlowRunner;

import java.util.List;

import org.junit.Test;

public class IpFilterTestCase extends ValidationTestCase {

  private List<String> invalidDenyListIps = asList("10.0.0.5");
  private List<String> validDenyListIps = asList("127.0.0.1");

  private List<String> invalidAllowListIps = asList("192.168.2.1", "193.100.0.1");
  private List<String> validAllowListIps = asList("192.168.1.1", "127.0.0.1", "193.1.0.1");


  private List<String> invalidDenyListIpsV6 = asList("2001:db8:0:0:0:0:0:0");
  private List<String> validDenyListIpsV6 = asList("2002:db8:0:ffff:ffff:ffff:ffff:ffff");

  private List<String> invalidAllowListIpsV6 = asList("2002:db8:0:ffff:ffff:ffff:ffff:ffff");
  private List<String> validAllowListIpsV6 = asList("2001:db8:0:0:0:0:0:0");

  @Override
  protected String getConfigFile() {
    return "ip-filter-validations.xml";
  }

  @Test
  public void ipFilterIpv4ConfigurationAllowListValidIps() throws Exception {
    for (String ip : validAllowListIps) {
      assertNotNull(createFlowRunner("flow_allowlist_ipv4", ip).run());
    }
  }

  @Test
  public void ipFilterIpv4ConfigurationAllowlistInvalidIps() throws Exception {
    for (String ip : invalidAllowListIps) {
      createFlowRunner("flow_allowlist_ipv4", ip)
          .runExpectingException(errorType(VALIDATION.name(), REJECTED_IP.name()));
    }
  }

  @Test
  public void ipFilterIpv4MalformedIp() throws Exception {
    createFlowRunner("flow_allowlist_ipv4", "400.24.1.1900")
        .runExpectingException(errorType(VALIDATION.name(), INVALID_IP.name()));
  }

  @Test
  public void ipFilterIpv4ConfigurationDenyListValidIps() throws Exception {
    for (String ip : validDenyListIps) {
      assertNotNull("ip " + ip + " should not be blocked", createFlowRunner("flow_denylist_ipv4", ip).run());
    }
  }

  @Test
  public void ipFilterIpv4ConfigurationDenyListInvalidIps() throws Exception {
    for (String ip : invalidDenyListIps) {
      createFlowRunner("flow_denylist_ipv4", ip)
          .runExpectingException(errorType(VALIDATION.name(), REJECTED_IP.name()));
    }
  }

  @Test
  public void ipFilterIpv6ValidAllowList() throws Exception {
    for (String ip : validAllowListIpsV6) {
      assertNotNull("ip " + ip + " should not be blocked", createFlowRunner("flow_allowlist_ipv6", ip).run());
    }
  }

  @Test
  public void ipFilterIpv6InvalidAllowList() throws Exception {
    for (String ip : invalidAllowListIpsV6) {
      createFlowRunner("flow_allowlist_ipv6", ip).runExpectingException(errorType(VALIDATION.name(), REJECTED_IP.name()));
    }
  }

  @Test
  public void ipFilterIpv6ValidDenyList() throws Exception {
    for (String ip : validDenyListIpsV6) {
      assertNotNull("ip " + ip + " should not be blocked", createFlowRunner("flow_denylist_ipv6", ip).run());
    }
  }

  @Test
  public void ipFilterIpv6InvalidDenyList() throws Exception {
    for (String ip : invalidDenyListIpsV6) {
      createFlowRunner("flow_denylist_ipv6", ip)
          .runExpectingException(errorType(VALIDATION.name(), REJECTED_IP.name()));
    }
  }

  @Test
  public void allowListExpressionResolvesToNull() throws Exception {
    flowRunner("flow_allowlist_ipv4").runExpectingException(instanceOf(IllegalArgumentException.class));
  }

  @Test
  public void denyListExpressionResolvesToNull() throws Exception {
    flowRunner("flow_denylist_ipv4").runExpectingException(instanceOf(IllegalArgumentException.class));
  }

  private FlowRunner createFlowRunner(String flowName, String ip) {
    return flowRunner(flowName).withVariable("ip", ip);
  }
}
