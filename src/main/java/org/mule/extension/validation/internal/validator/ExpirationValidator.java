/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.error.ValidationErrorType.EXPIRED_TIME;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;

import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.error.ValidationErrorType;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * An {@link AbstractValidator} which verifies that a {@link java.time.LocalDateTime} did not occur more than a given duration
 * from the current time.
 *
 * @since 1.1
 */
public class ExpirationValidator extends AbstractValidator {

  private final LocalDateTime time;
  private final Duration expiresIn;
  private I18nMessage errorMessage;

  public ExpirationValidator(LocalDateTime time, Duration expiresIn, ValidationContext validationContext) {
    super(validationContext);
    this.time = time;
    this.expiresIn = expiresIn;
  }

  @Override
  public ValidationResult validate() {
    LocalDateTime currentTime = LocalDateTime.now();
    if (!currentTime.isBefore(time.plus(expiresIn))) {
      errorMessage = getMessages().expiredTime(time, expiresIn, currentTime);
      return fail();
    } else {
      return ok();
    }
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return EXPIRED_TIME;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }
}
