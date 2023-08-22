/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.api;

import static org.mule.extension.validation.api.ValidationErrorType.MULTIPLE;
import org.mule.runtime.api.exception.ComposedErrorException;
import org.mule.runtime.api.i18n.I18nMessageFactory;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A specialization of {@link ValidationResult} which takes a {@link MultipleValidationResult} as a result.
 *
 * @since 1.0
 */
public final class MultipleValidationException extends ModuleException implements ComposedErrorException {

  private static final long serialVersionUID = -5590935258390057130L;

  private final List<Error> errors;

  public static MultipleValidationException of(List<Error> errors) {
    String message = errors.stream()
        .map(e -> e.getDescription())
        .collect(Collectors.joining("\n"));

    return new MultipleValidationException(message, errors);
  }

  private MultipleValidationException(String message, List<Error> errors) {
    super(I18nMessageFactory.createStaticMessage(message), MULTIPLE);
    this.errors = errors;
  }

  @Override
  public List<Error> getErrors() {
    return errors;
  }

}
