/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal;

import static org.mule.extension.validation.api.ValidationExtension.nullSafeLocale;
import static org.mule.extension.validation.internal.validator.IpFilterValidator.CHECK_RANGE_EXCLUSION;
import static org.mule.extension.validation.internal.validator.IpFilterValidator.CHECK_RANGE_INCLUSION;
import static org.mule.runtime.extension.api.annotation.param.Optional.PAYLOAD;
import org.mule.extension.validation.api.IpFilterList;
import org.mule.extension.validation.api.ValidationExtension;
import org.mule.extension.validation.api.ValidationOptions;
import org.mule.extension.validation.api.Validator;
import org.mule.extension.validation.internal.error.BlankErrorType;
import org.mule.extension.validation.internal.error.BooleanErrorType;
import org.mule.extension.validation.internal.error.ElapsedErrorType;
import org.mule.extension.validation.internal.error.EmailErrorType;
import org.mule.extension.validation.internal.error.EmptyErrorType;
import org.mule.extension.validation.internal.error.IpErrorType;
import org.mule.extension.validation.internal.error.IpFilterErrorType;
import org.mule.extension.validation.internal.error.NotBlankErrorType;
import org.mule.extension.validation.internal.error.NotElapsedErrorType;
import org.mule.extension.validation.internal.error.NotEmptyErrorType;
import org.mule.extension.validation.internal.error.NotNullErrorType;
import org.mule.extension.validation.internal.error.NullErrorType;
import org.mule.extension.validation.internal.error.RegexErrorType;
import org.mule.extension.validation.internal.error.SizeErrorType;
import org.mule.extension.validation.internal.error.TimeErrorType;
import org.mule.extension.validation.internal.error.UrlErrorType;
import org.mule.extension.validation.internal.validator.BlankStringValidator;
import org.mule.extension.validation.internal.validator.BooleanValidator;
import org.mule.extension.validation.internal.validator.ElapsedValidator;
import org.mule.extension.validation.internal.validator.EmailValidator;
import org.mule.extension.validation.internal.validator.EmptyCollectionValidator;
import org.mule.extension.validation.internal.validator.IpFilterValidator;
import org.mule.extension.validation.internal.validator.IpValidator;
import org.mule.extension.validation.internal.validator.MatchesRegexValidator;
import org.mule.extension.validation.internal.validator.NotBlankStringValidator;
import org.mule.extension.validation.internal.validator.NotElapsedValidator;
import org.mule.extension.validation.internal.validator.NotEmptyCollectionValidator;
import org.mule.extension.validation.internal.validator.NotNullValidator;
import org.mule.extension.validation.internal.validator.NullValidator;
import org.mule.extension.validation.internal.validator.SizeValidator;
import org.mule.extension.validation.internal.validator.TimeValidator;
import org.mule.extension.validation.internal.validator.UrlValidator;
import org.mule.runtime.api.el.ExpressionLanguage;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.parameter.ParameterResolver;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;


/**
 * Defines the operations of {@link ValidationExtension} which executes the {@link Validator}s that the extension provides out of
 * the box
 *
 * @see ValidationExtension
 * @since 1.0
 */
@org.mule.runtime.extension.api.annotation.param.stereotype.Validator
public final class CommonValidationOperations extends ValidationSupport {

  @Inject
  private ExpressionLanguage expressionLanguage;

  /**
   * Validates that the given {@code value} is {@code true}
   *
   * @param expression the boolean to test
   * @param options    the {@link ValidationOptions}
   * @param config     the current {@link ValidationExtension} that serves as config
   * @throws Exception if the value is not {@code true}
   */
  @Throws(BooleanErrorType.class)
  public void isTrue(boolean expression, @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                     @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new BooleanValidator(expression, true, context), context);
  }

  /**
   * Validates that the given {@code value} is {@code false}
   *
   * @param expression the boolean to test
   * @param options    the {@link ValidationOptions}
   * @param config     the current {@link ValidationExtension} that serves as config
   * @throws Exception if the value is not {@code true}
   */
  @Throws(BooleanErrorType.class)
  public void isFalse(boolean expression, @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                      @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new BooleanValidator(expression, false, context), context);
  }

  /**
   * Validates that the {@code email} address is valid
   *
   * @param email   an email address
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @Throws(EmailErrorType.class)
  public void isEmail(String email, @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                      @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new EmailValidator(email, context), context);
  }

  /**
   * Validates that an {@code ip} address represented as a {@link String} is valid
   *
   * @param ip      the ip address to validate
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @DisplayName("Is IP")
  @Throws(IpErrorType.class)
  public void isIp(String ip, @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                   @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new IpValidator(ip, context), context);
  }

  /**
   * Validates that {@code value} has a size between certain inclusive boundaries. This validator is capable of handling instances
   * of {@link String}, {@link Collection}, {@link Map} and arrays
   *
   * @param value   the value to validate
   * @param min     the minimum expected length (inclusive, defaults to zero)
   * @param max     the maximum expected length (inclusive). Leave unspecified or {@code null} to allow any max length
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @Throws(SizeErrorType.class)
  public void validateSize(TypedValue<Object> value, @Optional(defaultValue = "0") int min, @Optional Integer max,
                           @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                           @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new SizeValidator(value, min, max, context, expressionLanguage), context);
  }

  /**
   * Validates that {@code value} is not a blank String.
   *
   * @param value   the String to check
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @Throws(BlankErrorType.class)
  public void isNotBlankString(@Optional(defaultValue = PAYLOAD) String value,
                               @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                               @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new NotBlankStringValidator(value, context), context);
  }

  /**
   * Validates that {@code value} is not an empty collection.
   *
   * @param values  the value to check
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @Throws(EmptyErrorType.class)
  public void isNotEmptyCollection(@Optional(defaultValue = PAYLOAD) Collection<Object> values,
                                   @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                                   @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new NotEmptyCollectionValidator(values, context), context);
  }

  /**
   * Validates that {@code value} is a blank String.
   *
   * @param value   the value to check
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @Throws(NotBlankErrorType.class)
  public void isBlankString(String value, @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                            @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new BlankStringValidator(value, context), context);
  }

  /**
   * Validates that {@code value} is an empty collection.
   *
   * @param values  the value to check
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @Throws(NotEmptyErrorType.class)
  public void isEmptyCollection(@Optional(defaultValue = PAYLOAD) Collection<Object> values,
                                @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                                @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new EmptyCollectionValidator(values, context), context);
  }

  /**
   * Validates that the given {@code value} is not {@code null}. Keep in mind that the definition of {@code null} may vary
   * depending on the value's mimeType. For example, for an {@code application/java} mimeType, null means a blank pointer.
   * However, is the mimeType is {@code application/json} then the String &quot;null&quot; is also a null value.
   *
   * @param value   the value to test
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @Throws(NullErrorType.class)
  public void isNotNull(ParameterResolver<TypedValue<Object>> value,
                        @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                        @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new NotNullValidator(value.resolve(), context, expressionLanguage), context);
  }

  /**
   * Validates that the given {@code value} is {@code null}. Keep in mind that the definition of {@code null} may vary depending
   * on the value's mimeType. For example, for an {@code application/java} mimeType, null means a blank pointer. However, is the
   * mimeType is {@code application/json} then the String &quot;null&quot; is also a null value.
   *
   * @param value   the value to test
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @Throws(NotNullErrorType.class)
  public void isNull(ParameterResolver<TypedValue<Object>> value,
                     @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                     @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new NullValidator(value.resolve(), context, expressionLanguage), context);
  }

  /**
   * Validates that a {@code time} in {@link String} format is valid for the given {@code pattern} and {@code locale}. If no
   * pattern is provided, then the {@code locale}'s default will be used
   *
   * @param time    A date in String format
   * @param locale  the locale of the String
   * @param pattern the pattern for the {@code date}
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @Throws(TimeErrorType.class)
  public void isTime(String time, @Optional String locale, @Optional String pattern,
                     @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                     @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new TimeValidator(time, nullSafeLocale(locale), pattern, context), context);
  }

  /**
   * Validates the amount of time that has elapsed since the moment in the {@code since} parameter is greater than an specified
   * amount of {@code time}.
   *
   * @param time     the interval size
   * @param timeUnit the interval unit (as a {@link TimeUnit})
   * @param since    the time to validate
   * @param options  the {@link ValidationOptions}
   * @param config   the current {@link ValidationExtension} that serves as config
   *
   * @since 1.1
   */
  @Throws(NotElapsedErrorType.class)
  public void isElapsed(Long time, TimeUnit timeUnit, LocalDateTime since,
                        @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                        @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new ElapsedValidator(time, timeUnit, since, context), context);
  }

  /**
   * Validates the amount of time that has elapsed since the moment in the {@code since} parameter is greater than an specified
   * amount of {@code time}.
   *
   * @param time     the interval size
   * @param timeUnit the interval unit (as a {@link TimeUnit})
   * @param since    the time to validate
   * @param options  the {@link ValidationOptions}
   * @param config   the current {@link ValidationExtension} that serves as config
   *
   * @since 1.1
   */
  @Throws(ElapsedErrorType.class)
  public void isNotElapsed(Long time, TimeUnit timeUnit, LocalDateTime since,
                           @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                           @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new NotElapsedValidator(time, timeUnit, since, context), context);
  }

  /**
   * Validates that {@code url} is a valid one
   *
   * @param url     the URL to validate as a {@link String}
   * @param options the {@link ValidationOptions}
   * @param config  the current {@link ValidationExtension} that serves as config
   */
  @DisplayName("Is URL")
  @Throws(UrlErrorType.class)
  public void isUrl(@DisplayName("URL") String url, @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                    @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new UrlValidator(url, context), context);
  }

  /**
   * Validates that {@code value} matches the {@code regex} regular expression
   *
   * @param value         the value to check
   * @param regex         the regular expression to check against
   * @param caseSensitive when {@code true} matching is case sensitive, otherwise matching is case in-sensitive
   * @param options       the {@link ValidationOptions}
   * @param config        the current {@link ValidationExtension} that serves as config
   */
  @Throws(RegexErrorType.class)
  public void matchesRegex(String value, String regex, @Optional(defaultValue = "true") boolean caseSensitive,
                           @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                           @Config ValidationExtension config)
      throws Exception {
    ValidationContext context = createContext(options, config);
    validateWith(new MatchesRegexValidator(value, regex, caseSensitive, context), context);
  }

  /**
   * Validates that a {@code ipAddress} is present in the {@code ipList}.
   *
   * @param ipAddress the address to validate
   * @param allowList the list of allowed addresses
   * @param options   the {@link ValidationOptions}
   * @param config    the current {@link ValidationExtension} that serves as config
   *
   * @since 1.1
   */
  @Throws({IpErrorType.class, IpFilterErrorType.class})
  public void isAllowedIp(String ipAddress,
                          IpFilterList allowList,
                          @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                          @Config ValidationExtension config)
      throws Exception {

    ValidationContext context = createContext(options, config);
    validateWith(new IpFilterValidator(ipAddress, allowList, CHECK_RANGE_INCLUSION, context), context);
  }

  /**
   * Validates that a {@code ipAddress} is not present in the {@code ipList}.
   *
   * @param ipAddress the address to validate
   * @param denyList  the list of denied addresses
   * @param options   the {@link ValidationOptions}
   * @param config    the current {@link ValidationExtension} that serves as config
   *
   * @since 1.1
   */
  @Throws({IpErrorType.class, IpFilterErrorType.class})
  public void isNotDeniedIp(String ipAddress,
                            IpFilterList denyList,
                            @ParameterGroup(name = ERROR_GROUP) ValidationOptions options,
                            @Config ValidationExtension config)
      throws Exception {

    ValidationContext context = createContext(options, config);
    validateWith(new IpFilterValidator(ipAddress, denyList, CHECK_RANGE_EXCLUSION, context), context);
  }
}
