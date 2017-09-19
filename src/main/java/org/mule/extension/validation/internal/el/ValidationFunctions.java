/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.el;

import static org.mule.extension.validation.api.ValidationExtension.DEFAULT_LOCALE;
import static org.mule.extension.validation.api.ValidationExtension.nullSafeLocale;
import org.mule.extension.validation.api.NumberType;
import org.mule.extension.validation.api.ValidationOptions;
import org.mule.extension.validation.api.Validator;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.extension.validation.internal.ValidationMessages;
import org.mule.extension.validation.internal.validator.EmailValidator;
import org.mule.extension.validation.internal.validator.IpValidator;
import org.mule.extension.validation.internal.validator.MatchesRegexValidator;
import org.mule.extension.validation.internal.validator.NumberValidator;
import org.mule.extension.validation.internal.validator.TimeValidator;
import org.mule.extension.validation.internal.validator.UrlValidator;
import org.mule.runtime.extension.api.annotation.Ignore;
import org.mule.runtime.extension.api.annotation.param.Optional;

import java.util.Locale;

/**
 * A class which allows executing instances of {@link org.mule.extension.validation.internal.validator.AbstractValidator}'s from a
 * MEL context.
 * <p/>
 * Unlike regular validations which throw an exception upon failure, the methods in this class will only return boolean values to
 * indicate if the validation was successful or not. Also, no message is returned in either case.
 * <p/>
 * Since in this case we only care about the boolean result of the validation, all validations will be executed with the same
 * {@link ValidationContext}
 * <p/>
 * {@link Validator} instances are not reused. A new one is created each time
 *
 * @since 1.0
 */
public final class ValidationFunctions {

  private final ValidationContext validationContext;

  public ValidationFunctions() {
    validationContext = new ValidationContext(new ValidationMessages(), new ValidationOptions());
  }

  /**
   * Tests if the {@code email} address is validm
   *
   * @param email an email address
   * @return {@code true} if the validation succeeded. {@code false} otherwise
   */
  public boolean isEmail(String email) {
    return validate(new EmailValidator(email, validationContext));
  }

  /**
   * Tests if the {@code value} matches the {@code regex} regular expression
   *
   * @param value the value to check
   * @param regex the regular expression to check against
   * @param caseSensitive when {@code true} matching is case sensitive, otherwise matching is case in-sensitive
   * @return {@code true} if the validation succeeded. {@code false} otherwise
   */
  public boolean matchesRegex(String value, String regex, @Optional(defaultValue = "true") boolean caseSensitive) {
    return validate(new MatchesRegexValidator(value, regex, caseSensitive, validationContext));
  }

  /**
   * Tests if the {@code time} in {@link String} format is valid for the given {@code pattern} and {@code locale}. If no
   * pattern is provided, then the {@code locale}'s default will be used
   *
   * @param time A date in String format
   * @param pattern the pattern for the {@code date}
   * @param locale a {@link Locale} key
   * @return {@code true} if the validation succeeded. {@code false} otherwise
   */
  public boolean isTime(String time, @Optional String pattern, @Optional String locale) {
    return validate(new TimeValidator(time, nullSafeLocale(locale), pattern, validationContext));
  }

  /**
   * Tests if the {@code value} can be parsed into a {@link Number}, by the rules of a {@link NumberValidator}.
   * <p/>
   * No boundaries are checked. Default system pattern and {@link Locale} are used
   *
   * @param value the value to test
   * @param numberType the type of number to validate against
   * @return {@code true} if the validation succeeded. {@code false} otherwise
   */
  @Ignore
  public boolean isNumber(String value, NumberType numberType) {
    return validate(new NumberValidator(value, new Locale(DEFAULT_LOCALE), null, null, null, numberType, validationContext));
  }

  /**
   * Tests if the {@code ip} is valid
   *
   * @param ip the ip address to validate
   * @return {@code true} if the validation succeeded. {@code false} otherwise
   */
  public boolean isIp(String ip) {
    return validate(new IpValidator(ip, validationContext));
  }

  /**
   * Tests if the {@code url} is a valid one.
   *
   * @param url the URL to validate as a {@link String}
   * @return {@code true} if the validation succeeded. {@code false} otherwise
   */
  public boolean isUrl(String url) {
    return validate(new UrlValidator(url, validationContext));
  }

  private boolean validate(Validator validator) {
    return !validator.validate().isError();
  }
}
