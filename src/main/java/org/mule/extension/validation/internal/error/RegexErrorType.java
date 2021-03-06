/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.error;

import static org.mule.extension.validation.api.ValidationErrorType.MISMATCH;

import org.mule.extension.validation.api.ValidationErrorType;

public class RegexErrorType extends BasicValidationErrorType {

  @Override
  protected ValidationErrorType getErrorType() {
    return MISMATCH;
  }

}
