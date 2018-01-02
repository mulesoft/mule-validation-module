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
import static org.mule.extension.validation.api.error.ValidationErrorType.INVALID_IP;
import static org.mule.extension.validation.api.error.ValidationErrorType.REJECTED_IP;
import static org.mule.extension.validation.api.error.ValidationErrorType.VALIDATION;
import static org.mule.tck.junit4.matcher.ErrorTypeMatcher.errorType;

import org.mule.functional.api.flow.FlowRunner;

import java.util.List;

import org.junit.Test;

public class IpFilterTestCase extends ValidationTestCase {

  private List<String> invalidBlackListIps = asList("10.0.0.5");
  private List<String> validBlackListIps = asList("127.0.0.1");

  private List<String> invalidWhiteListIps = asList("192.168.2.1", "193.100.0.1");
  private List<String> validWhiteListIps = asList("192.168.1.1", "127.0.0.1", "193.1.0.1");


  private List<String> invalidBlackListIpsV6 = asList("2001:db8:0:0:0:0:0:0");
  private List<String> validBlackListIpsV6 = asList("2002:db8:0:ffff:ffff:ffff:ffff:ffff");

  private List<String> invalidWhiteListIpsV6 = asList("2002:db8:0:ffff:ffff:ffff:ffff:ffff");
  private List<String> validWhiteListIpsV6 = asList("2001:db8:0:0:0:0:0:0");

  @Override
  protected String getConfigFile() {
    return "ip-filter-validations.xml";
  }

  @Test
  public void ipFilterIpv4ConfigurationWhitelistValidIps() throws Exception {
    for (String ip : validWhiteListIps) {
      assertNotNull(createFlowRunner("flow_whitelist_ipv4", ip).run());
    }
  }

  @Test
  public void ipFilterIpv4ConfigurationWhitelistInvalidIps() throws Exception {
    for (String ip : invalidWhiteListIps) {
      createFlowRunner("flow_whitelist_ipv4", ip)
          .runExpectingException(errorType(VALIDATION.name(), REJECTED_IP.name()));
    }
  }

  @Test
  public void ipFilterIpv4MalformedIp() throws Exception {
    createFlowRunner("flow_whitelist_ipv4", "400.24.1.1900")
        .runExpectingException(errorType(VALIDATION.name(), INVALID_IP.name()));
  }

  @Test
  public void ipFilterIpv4ConfigurationBlacklistValidIps() throws Exception {
    for (String ip : validBlackListIps) {
      assertNotNull("ip " + ip + " should not be blocked", createFlowRunner("flow_blacklist_ipv4", ip).run());
    }
  }

  @Test
  public void ipFilterIpv4ConfigurationBlacklistInvalidIps() throws Exception {
    for (String ip : invalidBlackListIps) {
      createFlowRunner("flow_blacklist_ipv4", ip)
          .runExpectingException(errorType(VALIDATION.name(), REJECTED_IP.name()));
    }
  }

  @Test
  public void ipFilterIpv6ValidWhiteList() throws Exception {
    for (String ip : validWhiteListIpsV6) {
      assertNotNull("ip " + ip + " should not be blocked", createFlowRunner("flow_whitelist_ipv6", ip).run());
    }
  }

  @Test
  public void ipFilterIpv6InvalidWhiteList() throws Exception {
    for (String ip : invalidWhiteListIpsV6) {
      createFlowRunner("flow_whitelist_ipv6", ip).runExpectingException(errorType(VALIDATION.name(), REJECTED_IP.name()));
    }
  }

  @Test
  public void ipFilterIpv6ValidBlackList() throws Exception {
    for (String ip : validBlackListIpsV6) {
      assertNotNull("ip " + ip + " should not be blocked", flowRunner("flow_blackist_ipv6")
          .withAttributes(getIpAttributes(ip)).run());
    }
  }

  @Test
  public void ipFilterIpv6InvalidBlackList() throws Exception {
    for (String ip : invalidBlackListIpsV6) {
      flowRunner("flow_blackist_ipv6").withAttributes(getIpAttributes(ip))
          .runExpectingException(errorType(VALIDATION.name(), REJECTED_IP.name()));
    }
  }

  @Test
  public void whiteListExpressionResolvesToNull() throws Exception {
    flowRunner("flow_whitelist_ipv4").runExpectingException(instanceOf(IllegalArgumentException.class));
  }

  @Test
  public void blackListExpressionResolvesToNull() throws Exception {
    flowRunner("flow_blacklist_ipv4").runExpectingException(instanceOf(IllegalArgumentException.class));
  }

  private Object getIpAttributes(String ipFilter) {
    return new Object() {

      private String ip = ipFilter;

    };
  }

  private FlowRunner createFlowRunner(String flowName, String ip) {
    return flowRunner(flowName).withAttributes(getIpAttributes(ip));
  }
}
