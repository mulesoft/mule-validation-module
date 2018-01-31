/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.INVALID_IP;
import static org.mule.extension.validation.api.ValidationErrorType.REJECTED_IP;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;

import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.extension.validation.api.IpFilterList;
import org.mule.extension.validation.internal.ip.address.IPAddress;
import org.mule.extension.validation.internal.ip.address.IPAddressFactory;
import org.mule.runtime.api.i18n.I18nMessage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A validator for IP address lists and ranges.
 *
 * @since 1.1
 */
public class IpFilterValidator extends AbstractValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(IpFilterValidator.class);
  private static final Pattern REMOTE_ADDRESS_PATTERN = Pattern.compile("([^/]*)/(.*):(.*)");

  public static final boolean CHECK_RANGE_EXCLUSION = false;
  public static final boolean CHECK_RANGE_INCLUSION = true;

  private IPAddressFactory factory = new IPAddressFactory();

  private final String ip;
  private final IpFilterList ipList;
  private final boolean expectedRangeCondition;
  private ValidationErrorType errorType;
  private I18nMessage errorMessage;

  public IpFilterValidator(String ip, IpFilterList ipList, boolean expectedRangeCondition,
                           ValidationContext validationContext) {
    super(validationContext);
    this.ip = ip;
    this.ipList = ipList;
    this.expectedRangeCondition = expectedRangeCondition;
  }

  @Override
  public ValidationResult validate() {
    String actualIp = format(ip);
    IPAddress ipAddress = factory.create(actualIp);
    LOGGER.trace("Verifying IP: {}", actualIp);

    if (!ipAddress.isValid()) {
      errorType = INVALID_IP;
      errorMessage = getMessages().invalidIp(ip);
      return fail();
    }

    if (!isInRange(ipAddress, ipList.ipAddresses()) == expectedRangeCondition) {
      errorType = REJECTED_IP;
      errorMessage = getMessages().rejectedIp(ip);
      return fail();
    }

    LOGGER.trace("IP {} is valid.", actualIp);
    return ok();
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return errorType;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }

  private boolean isInRange(IPAddress ipAddress, List<IPAddress> ranges) {
    return ranges.stream()
        .anyMatch(range -> ipAddress.matches(range));
  }

  private String format(String ipAddress) {
    String formattedIp = ipAddress;

    Matcher ipMatcher = REMOTE_ADDRESS_PATTERN.matcher(ipAddress);
    if (ipMatcher.find()) {
      formattedIp = ipMatcher.group(2);
    }

    return formattedIp;
  }
}
