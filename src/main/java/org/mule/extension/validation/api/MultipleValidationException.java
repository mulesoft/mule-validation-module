/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.api;

import org.mule.runtime.api.exception.ComposedErrorException;
import org.mule.runtime.api.message.Error;

import java.util.List;

/**
 * A specialization of {@link ValidationResult} which takes a {@link MultipleValidationResult} as a result.
 *
 * @since 3.7.0
 */
public final class MultipleValidationException extends ValidationException implements ComposedErrorException {

  private static final long serialVersionUID = -5590935258390057130L;

  private final List<Error> errors;

  public MultipleValidationException(MultipleValidationResult multipleValidationResult, List<Error> errors) {
    super(multipleValidationResult, multipleValidationResult.getErrorType());
    this.errors = errors;
  }

  @Override
  public List<Error> getErrors() {
    return errors;
  }

}
