/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal;

import org.mule.extension.validation.api.MultipleValidationException;
import org.mule.extension.validation.internal.error.MultipleErrorType;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.stereotype.AllowedStereotypes;
import org.mule.runtime.extension.api.annotation.param.stereotype.Validator;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.extension.api.runtime.route.Chain;
import org.mule.runtime.extension.api.stereotype.ValidatorStereotype;

/**
 * A class containing operations which performs validations according to different strategies
 *
 * @since 1.0
 */
@Validator
public final class ValidationStrategies {

  /**
   * Perform a list of nested validation operations and informs only one {@code VALIDATION:MULTIPLE} error which summarizes all of
   * the found errors (if any).
   *
   * @param validations the nested validation operations
   * @throws MultipleValidationException if at least one validator fails and {@code throwsException} is {@code true}
   */
  @Throws(MultipleErrorType.class)
  public void all(@AllowedStereotypes(ValidatorStereotype.class) Chain validations,
                  CompletionCallback<Void, Void> callback)
      throws MultipleValidationException {
    // implemented as privileged operation in AllOperationExecutor
  }

  /**
   * Perform a list of nested validation operations and informs only one {@code VALIDATION:MULTIPLE} error which summarizes all of
   * the found errors (if all failed).
   *
   * @param validations the nested validation operations
   * @throws MultipleValidationException if all validators fail and {@code throwsException} is {@code true}
   *
   * @since 1.3
   */
  @Throws(MultipleErrorType.class)
  public void any(@AllowedStereotypes(ValidatorStereotype.class) Chain validations,
                  CompletionCallback<Void, Void> callback)
      throws MultipleValidationException {
    // implemented as privileged operation in AnyOperationExecutor
  }
}
