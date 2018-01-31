/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.ELAPSED_TIME;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import static org.mule.extension.validation.internal.validator.ElapsedValidator.chronoUnit;

import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * An {@link AbstractValidator} which verifies that a time has not elapsed after a given point.
 *
 * @since 1.1
 */
public class NotElapsedValidator extends AbstractValidator {

  private final Duration interval;
  private final LocalDateTime since;
  private I18nMessage errorMessage;

  public NotElapsedValidator(Long time, TimeUnit timeUnit, LocalDateTime since, ValidationContext validationContext) {
    super(validationContext);
    this.since = since;
    this.interval = Duration.of(time, chronoUnit(timeUnit));
  }

  @Override
  public ValidationResult validate() {
    LocalDateTime currentTime = LocalDateTime.now();
    if (!currentTime.isBefore(since.plus(interval))) {
      errorMessage = getMessages().elapsedTime(since, interval, currentTime);
      return fail();
    } else {
      return ok();
    }
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return ELAPSED_TIME;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }
}
