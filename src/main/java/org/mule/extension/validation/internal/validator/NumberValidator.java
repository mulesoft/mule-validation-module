/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.INVALID_NUMBER;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import org.mule.extension.validation.api.NumberType;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import java.util.Locale;

/**
 * A validator which tets that a given {@link String} {@link #value} can be parsed into a {@link Number} per the rules of a
 * {@link NumberType}, and that the resulting number is between two inclusive {@link #minValue} and {@link #maxValue} boundaries.
 *
 * @since 1.0
 */
public class NumberValidator extends AbstractValidator {

  /**
   * Value to validate
   */
  private String value;

  /**
   * The locale to use for the format
   */
  private Locale locale;

  /**
   * The pattern used to format the value
   */
  private String pattern;

  /**
   * The minimum value
   */
  private Number minValue;

  /**
   * The maximum value
   */
  private Number maxValue;

  private NumberType numberType;

  private I18nMessage errorMessage;

  public NumberValidator(String value, Locale locale, String pattern, Number minValue, Number maxValue, NumberType numberType,
                         ValidationContext validationContext) {
    super(validationContext);
    this.value = value;
    this.locale = locale;
    this.pattern = pattern;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.numberType = numberType;
  }

  @Override
  public ValidationResult validate() {
    Comparable<Number> newValue = (Comparable<Number>) numberType.toNumber(value, pattern, locale);


    if (newValue == null) {
      errorMessage = getMessages().invalidNumberType(value, numberType.name());
      return fail();
    }

    if (minValue != null) {
      if (newValue.compareTo(minValue) < 0) {
        errorMessage = getMessages().lowerThan(newValue, minValue);
        return fail();
      }
    }

    if (maxValue != null) {
      if (newValue.compareTo(maxValue) > 0) {
        errorMessage = getMessages().greaterThan(newValue, maxValue);
        return fail();
      }
    }

    return ok();
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return INVALID_NUMBER;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }
}
