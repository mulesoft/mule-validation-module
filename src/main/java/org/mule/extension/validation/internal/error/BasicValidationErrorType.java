/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.error;

import static java.util.Collections.singleton;
import static org.mule.extension.validation.api.ValidationErrorType.VALIDATION;

import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.api.ValidationExtension;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.Set;

/**
 * General {@link ErrorTypeDefinition} for {@link ValidationExtension} operations.
 *
 * @since 1.0
 */
public class BasicValidationErrorType implements ErrorTypeProvider {

  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    return singleton(getErrorType());
  }

  /**
   * Defines the error type to throw. Subclasses can override this as desired.
   *
   * @return the error type to declare as thrown
   */
  protected ValidationErrorType getErrorType() {
    return VALIDATION;
  }

}
