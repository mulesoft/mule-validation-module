/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal;

import org.mule.extension.validation.api.MultipleValidationException;
import org.mule.extension.validation.api.MultipleValidationResult;
import org.mule.extension.validation.api.ValidationException;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.internal.error.AllErrorType;
import org.mule.runtime.extension.api.annotation.Ignore;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.stereotype.AllowedStereotypes;
import org.mule.runtime.extension.api.annotation.param.stereotype.Stereotype;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.mule.runtime.extension.api.runtime.process.Chain;
import org.mule.runtime.extension.api.stereotype.ValidatorStereotype;

/**
 * A class containing operations which performs validations according to different strategies
 *
 * @since 3.7.0
 */
@Stereotype(ValidatorStereotype.class)
public final class ValidationStrategies {

  /**
   * Perform a list of nested validation operations and informs only one {@link MultipleValidationResult} which summarizes all of
   * the found errors (if any).
   * <p>
   * If {@code throwsException} is {@code true}, then the {@link ValidationResult} is communicated by throwing a
   * {@link ValidationException}. On the other hand, if {@code throwsException} is {@code false}, then the
   * {@link ValidationResult} is set as the message payload.
   * <p>
   * When configured through XML, all the {@code validations} must include the All the child processors must contain the
   * {@code validator-message-processor} substitution group.
   *
   * @param validations the nested validation operations
   * @throws MultipleValidationException if at least one validator fails and {@code throwsException} is {@code true}
   */
  @Throws(AllErrorType.class)
  @Ignore
  //TODO MULE-13440
  public void all(@AllowedStereotypes(ValidatorStereotype.class) Chain validations,
                  CompletionCallback<Void, Void> callback)
      throws MultipleValidationException {

    // final List<Error> errors = new LinkedList<>();
    // final List<ValidationResult> results = new ArrayList<>(validations.getOperations().size());
    //
    // validations
    //   .onEachError((previous, error) -> {
    //     Throwable rootCause = ExceptionUtils.getRootCause(error);
    //     if (rootCause == null) {
    //       rootCause = error;
    //     }
    //     results.add(ImmutableValidationResult.error(rootCause.getMessage()));
    //     errors.add(((MessagingException) error).getEvent().getError().get());
    //     return previous;
    //   })
    //   .onSuccess(result -> {
    //     MultipleValidationResult composedValidation = ImmutableMultipleValidationResult.of(results);
    //     if (composedValidation.isError()) {
    //       callback.error(new MultipleValidationException(composedValidation, errors));
    //     }
    //     callback.success(result);
    //   })
    //   .process();
  }
}
