/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.INVALID_TIME;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import java.util.Locale;

import org.joda.time.format.DateTimeFormat;

/**
 * An {@link AbstractValidator} which verifies that a {@link #time} represented as a {@link String} can be parsed using a given
 * {@link #locale} and {@link #pattern}
 *
 * @since 1.0
 */
public class TimeValidator extends AbstractValidator {

  private final String time;
  private final String locale;
  private final String pattern;
  private I18nMessage errorMessage;

  public TimeValidator(String time, String locale, String pattern, ValidationContext validationContext) {
    super(validationContext);
    this.time = time;
    this.locale = locale;
    this.pattern = pattern;
  }

  @Override
  public ValidationResult validate() {
    Locale locale = new Locale(this.locale);
    try {
      DateTimeFormat.forPattern(pattern).withLocale(locale).parseDateTime(time);
    } catch (IllegalArgumentException e) {
      errorMessage = getMessages().invalidTime(time, this.locale, pattern);
      return fail();
    }

    return ok();
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return INVALID_TIME;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }
}
