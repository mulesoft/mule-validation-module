/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal;

import static org.mule.extension.validation.api.ValidationErrorType.VALIDATION;
import static org.mule.runtime.core.api.util.StringUtils.EMPTY;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.runtime.api.i18n.I18nMessage;
import org.mule.extension.validation.api.ValidationResult;

/**
 * An immutable implementation of {@link ValidationResult}. It provides a series of static factory methods for creating a result
 * in which the validation succeeded ({@link #ok()}), and for validations that failed ({@link #error(I18nMessage)},
 * {@link #error(String)} {@link #error(I18nMessage, ValidationErrorType)} and {@link #error(String, ValidationErrorType)}).
 *
 * @since 1.0
 */
public class ImmutableValidationResult implements ValidationResult {

  /**
   * Since this class is immutable, we can always use the same instance for results which represent a successful validation
   */
  private static final ValidationResult OK = new ImmutableValidationResult(EMPTY, null, false);

  private final String message;
  private final ValidationErrorType type;
  private final boolean error;

  /**
   * Creates a new instance with the given {@code message} and which {@link #isError()} returns {@code true}, with error type
   * {@link ValidationErrorType#VALIDATION}.
   *
   * @param message a message
   * @return a new instance of {@link ImmutableValidationResult}
   */
  public static ValidationResult error(String message) {
    return new ImmutableValidationResult(message, true);
  }

  /**
   * Creates a new instance with the given {@code message} and which {@link #isError()} returns {@code true}, with error type
   * {@code type}.
   *
   * @param message a message
   * @param type    an error type
   * @return a new instance of {@link ImmutableValidationResult}
   */
  public static ValidationResult error(String message, ValidationErrorType type) {
    return new ImmutableValidationResult(message, type, true);
  }

  /**
   * Creates a new instance with the given {@code message} and which {@link #isError()} returns {@code true}, with error type
   * {@link ValidationErrorType#VALIDATION}.
   *
   * @param message a message
   * @return a new instance of {@link ImmutableValidationResult}
   */
  public static ValidationResult error(I18nMessage message) {
    return error(message.getMessage());
  }

  /**
   * Creates a new instance with the given {@code message} and which {@link #isError()} returns {@code true}, with error type
   * {@code type}.
   *
   * @param message a message
   * @param type    an error type
   * @return a new instance of {@link ImmutableValidationResult}
   */
  public static ValidationResult error(I18nMessage message, ValidationErrorType type) {
    return error(message.getMessage(), type);
  }

  /**
   * returns a {@link ImmutableValidationResult} without message and which {@link #isError()} method returns {@code false}. Since
   * this class is immutable, the same instance is always returned
   *
   * @return {@link #OK}
   */
  public static ValidationResult ok() {
    return OK;
  }

  private ImmutableValidationResult(String message, boolean error) {
    this(message, VALIDATION, error);
  }

  public ImmutableValidationResult(String message, ValidationErrorType type, boolean error) {
    this.message = message;
    this.error = error;
    this.type = type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMessage() {
    return message;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ValidationErrorType getErrorType() {
    return type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isError() {
    return error;
  }
}
