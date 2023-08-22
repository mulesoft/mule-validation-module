/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.error;

import static org.mule.extension.validation.api.ValidationErrorType.REJECTED_IP;

import org.mule.extension.validation.api.ValidationErrorType;

/**
 * Error thrown by the IP filter validators.
 *
 * @since 1.1
 */
public class IpFilterErrorType extends BasicValidationErrorType {

  @Override
  protected ValidationErrorType getErrorType() {
    return REJECTED_IP;
  }
}
