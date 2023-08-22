/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.INVALID_BOOLEAN;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;

import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.runtime.api.i18n.I18nMessage;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.internal.ValidationContext;

/**
 * An {@link AbstractValidator} which tests a {@link #value} to match a {@link #expected} one
 *
 * @since 1.0
 */
public class BooleanValidator extends AbstractValidator {

  private final boolean value;
  private final boolean expected;

  public BooleanValidator(boolean value, boolean expected, ValidationContext validationContext) {
    super(validationContext);
    this.value = value;
    this.expected = expected;
  }

  @Override
  public ValidationResult validate() {
    return value == expected ? ok() : fail();
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return INVALID_BOOLEAN;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return getMessages().failedBooleanValidation(value, expected);
  }
}
