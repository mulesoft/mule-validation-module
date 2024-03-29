/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal;

import org.mule.extension.validation.api.NumberType;
import org.mule.extension.validation.api.ValidationExtension;
import org.mule.extension.validation.api.ValidationOptions;
import org.mule.extension.validation.api.Validator;
import org.mule.extension.validation.internal.error.NumberErrorType;
import org.mule.extension.validation.internal.validator.NumberValidator;
import org.mule.runtime.core.api.util.StringUtils;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;

import java.util.Locale;

/**
 * Defines operations to validate numbers
 *
 * @since 1.0
 */
@org.mule.runtime.extension.api.annotation.param.stereotype.Validator
public class NumberValidationOperation extends ValidationSupport {

  /**
   * Receives a numeric {@code value} as a {@link String} and validates that it can be parsed per the rules of a
   * {@code numberType}
   *
   * @param value      the value to be tested
   * @param locale     The locale to use for the format. If not provided it defaults to the system {@link Locale}
   * @param pattern    The pattern used to format the value
   * @param minValue   If provided, check that the parsed value is greater or equal than this value
   * @param maxValue   If provided, check that the parsed value is less or equal than this value
   * @param numberType the type of number to test {@code value} against
   * @param options    the {@link ValidationOptions}
   */
  @Throws(NumberErrorType.class)
  public void isNumber(String value, @Optional String locale, @Optional String pattern,
                       @Optional String minValue,
                       @Optional String maxValue, NumberType numberType,
                       @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                       @Config ValidationExtension config)
      throws Exception {

    ValidationContext context = createContext(options, config);
    Validator validator = new NumberValidator(value, parseLocale(locale), pattern, parseNumber(minValue, numberType),
                                              parseNumber(maxValue, numberType), numberType, context);

    validateWith(validator, context);
  }

  private Number parseNumber(String value, NumberType numberType) {
    return StringUtils.isBlank(value) ? null : numberType.toNumber(value, null, parseLocale(null));
  }
}
