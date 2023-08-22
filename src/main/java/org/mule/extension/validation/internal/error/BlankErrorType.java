/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.error;

import static org.mule.extension.validation.api.ValidationErrorType.BLANK_STRING;

import org.mule.extension.validation.api.ValidationErrorType;

public class BlankErrorType extends BasicValidationErrorType {

  @Override
  protected ValidationErrorType getErrorType() {
    return BLANK_STRING;
  }

}
