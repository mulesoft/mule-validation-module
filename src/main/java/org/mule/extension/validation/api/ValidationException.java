/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.api;

import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

import org.mule.runtime.api.exception.ErrorMessageAwareException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * The exception to be thrown by default when a validation fails. It's a pretty simple {@link MuleException} with the added
 * ability to provide the {@link ValidationResult} which failed as part of {@link Error#getErrorMessage()}.
 * <p>
 * The exception message is set to match the one in {@link ValidationResult#getMessage()}
 *
 * @since 1.0
 */
// TODO MULE-12397 merge this with org.mule.runtime.core.internal.routing.ValidationException
public class ValidationException extends ModuleException implements ErrorMessageAwareException {

  private static final long serialVersionUID = -7191589190396052480L;

  private final ValidationResult validationResult;

  /**
   * Creates a new instance for the given {@code validationResult}
   * 
   * @param validationResult a failing {@link ValidationResult}
   *
   */
  public ValidationException(ValidationResult validationResult, ValidationErrorType errorType) {
    super(createStaticMessage(validationResult.getMessage()), errorType);
    this.validationResult = validationResult;
  }

  /**
   * @return the {@link ValidationResult} which caused this exception
   */
  @Override
  public Message getErrorMessage() {
    return Message.of(validationResult);
  }

  public ValidationResult getValidationResult() {
    return validationResult;
  }
}
