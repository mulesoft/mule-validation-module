/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.api;

import java.io.Serializable;

/**
 * The result of a validation
 *
 * @see Validator
 * @since 1.0
 */
public interface ValidationResult extends Serializable {

  /**
   * Returns a message associated with the execution of the validation. If the validation failed (which means that
   * {@link #isError()} is {@code true}), then it will contain the reason why the error was generated. Otherwise, it might or
   * might not contain some additional consideration about the validation result
   *
   * @return a {@link String} or {@code null}
   */
  String getMessage();

  /**
   * Returns the error type associated to the result, if {@link #isError()} is {@code true}.
   *
   * @return a {@link ValidationErrorType} or {@code null} if there was no error
   */
  ValidationErrorType getErrorType();

  /**
   * Whether the validation has failed or not
   *
   * @return {code true} if the validation failed. {@code false} otherwise
   */
  boolean isError();

}
