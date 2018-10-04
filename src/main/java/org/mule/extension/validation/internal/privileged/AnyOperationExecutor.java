/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.privileged;

import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.error;

import org.mule.extension.validation.api.MultipleValidationException;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.core.privileged.processor.chain.HasMessageProcessors;

import org.reactivestreams.Publisher;

import java.util.List;

/**
 * Custom executor for the {@code any} operation.
 * <p>
 * The reason why we have this custom executor is that unlike regular scopes, the {@code any} operation requires
 * that all processors are executed regardless of their failures, since what we really want is to aggregate
 * all the validation errors.
 * <p>
 * However, if one of the nested processors fail with a non validation error, then the execution is aborted and the
 * unexpected error is raised.
 * <p>
 * If only validation errors are found, a {@code VALIDATION:MULTIPLE} error is raised.
 *
 * @since 1.3
 */
public class AnyOperationExecutor extends AggregateOperationExecutor {

  @Override
  protected Publisher<Object> handleValidationErrors(HasMessageProcessors chain, List<Error> errors) {
    if (errors.size() < chain.getMessageProcessors().size()) {
      return empty();
    }

    return error(MultipleValidationException.of(errors));
  }

}
