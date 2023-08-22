/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.NOT_ELAPSED_TIME;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;

import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * An {@link AbstractValidator} which verifies that a time has elapsed after a given point.
 *
 * @since 1.1
 */
public class ElapsedValidator extends AbstractValidator {

  private final Duration interval;
  private final LocalDateTime since;
  private I18nMessage errorMessage;

  public ElapsedValidator(Long time, TimeUnit timeUnit, LocalDateTime since, ValidationContext validationContext) {
    super(validationContext);
    this.since = since;
    this.interval = Duration.of(time, chronoUnit(timeUnit));
  }

  @Override
  public ValidationResult validate() {
    LocalDateTime currentTime = LocalDateTime.now();
    if (currentTime.isBefore(since.plus(interval))) {
      errorMessage = getMessages().notElapsedTime(since, interval, currentTime);
      return fail();
    } else {
      return ok();
    }
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return NOT_ELAPSED_TIME;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }

  /**
   * Converts a {@code TimeUnit} to a {@code ChronoUnit}.
   * <p>
   * This handles the seven units declared in {@code TimeUnit}.
   *
   * @param unit the unit to convert, not null
   * @return the converted unit, not null
   */
  public static ChronoUnit chronoUnit(TimeUnit unit) {
    Objects.requireNonNull(unit, "unit");
    switch (unit) {
      case NANOSECONDS:
        return ChronoUnit.NANOS;
      case MICROSECONDS:
        return ChronoUnit.MICROS;
      case MILLISECONDS:
        return ChronoUnit.MILLIS;
      case SECONDS:
        return ChronoUnit.SECONDS;
      case MINUTES:
        return ChronoUnit.MINUTES;
      case HOURS:
        return ChronoUnit.HOURS;
      case DAYS:
        return ChronoUnit.DAYS;
      default:
        throw new IllegalArgumentException("Unknown TimeUnit constant");
    }
  }
}
