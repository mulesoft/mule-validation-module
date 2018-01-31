/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.BLANK_STRING;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import org.apache.commons.lang3.StringUtils;

/**
 * Validates that {@link #value} is not a blank String.
 *
 * @since 1.0
 */
public class NotBlankStringValidator extends AbstractValidator {

  private final String value;
  private I18nMessage errorMessage;

  public NotBlankStringValidator(String value, ValidationContext validationContext) {
    super(validationContext);
    this.value = value;
  }

  @Override
  public ValidationResult validate() {
    if (value == null) {
      errorMessage = getMessages().valueIsNull();
      return fail();
    }

    if (StringUtils.isBlank(value)) {
      errorMessage = getMessages().stringIsBlank();
      return fail();
    }

    return ok();
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return BLANK_STRING;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }
}
