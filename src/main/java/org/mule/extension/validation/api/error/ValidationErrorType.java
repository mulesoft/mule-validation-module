/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.api.error;


import static java.util.Optional.of;
import org.mule.extension.validation.api.ValidationExtension;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.error.MuleErrors;

import java.util.Optional;

/**
 * List of {@link ErrorTypeDefinition} that throws the {@link ValidationExtension}
 *
 * @since 1.0
 */
public enum ValidationErrorType implements ErrorTypeDefinition<ValidationErrorType> {

  /**
   * Indicates that a validation failure occurred
   */
  VALIDATION(MuleErrors.VALIDATION),

  INVALID_IP(VALIDATION),

  INVALID_BOOLEAN(VALIDATION),

  INVALID_EMAIL(VALIDATION),

  NOT_EMPTY_COLLECTION(VALIDATION),

  EMPTY_COLLECTION(VALIDATION),

  BLANK_STRING(VALIDATION),

  NOT_BLANK_STRING(VALIDATION),

  NULL(VALIDATION),

  NOT_NULL(VALIDATION),

  MISMATCH(VALIDATION),

  INVALID_NUMBER(VALIDATION),

  INVALID_SIZE(VALIDATION),

  INVALID_TIME(VALIDATION),

  INVALID_URL(VALIDATION),

  ELAPSED_TIME(VALIDATION),

  MULTIPLE(VALIDATION);

  private ErrorTypeDefinition<?> parentErrorType;

  ValidationErrorType(ErrorTypeDefinition<?> parentErrorType) {
    this.parentErrorType = parentErrorType;
  }

  @Override
  public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
    return of(parentErrorType);
  }

}
