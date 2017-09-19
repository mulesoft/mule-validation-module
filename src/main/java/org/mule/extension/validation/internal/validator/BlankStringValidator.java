/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.error.ValidationErrorType.NOT_BLANK_STRING;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.error.ValidationErrorType;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import org.apache.commons.lang3.StringUtils;

/**
 * A {@link AbstractValidator} which verifies that a given {@link #value} is an empty String.
 *
 * @since 1.0
 */
public class BlankStringValidator extends AbstractValidator {

  private final String value;
  private I18nMessage errorMessage;

  public BlankStringValidator(String value, ValidationContext validationContext) {
    super(validationContext);
    this.value = value;
  }

  @Override
  public ValidationResult validate() {
    if (value == null) {
      return ok();
    }

    if (!StringUtils.isBlank(value)) {
      errorMessage = getMessages().stringIsNotBlank();
      return fail();
    }

    return ok();
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return NOT_BLANK_STRING;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }
}
