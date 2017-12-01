/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.privileged;

import static java.util.Optional.ofNullable;
import static org.mule.runtime.core.privileged.processor.MessageProcessors.newChildContext;
import static org.mule.runtime.extension.api.error.MuleErrors.VALIDATION;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.error;

import org.mule.extension.validation.api.MultipleValidationException;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.message.ErrorType;
import org.mule.runtime.api.meta.model.operation.OperationModel;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.privileged.event.BaseEventContext;
import org.mule.runtime.core.privileged.exception.EventProcessingException;
import org.mule.runtime.core.privileged.processor.chain.HasMessageProcessors;
import org.mule.runtime.extension.api.runtime.operation.ComponentExecutor;
import org.mule.runtime.extension.api.runtime.operation.ExecutionContext;
import org.mule.runtime.module.extension.api.runtime.privileged.ExecutionContextAdapter;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Custom executor for the {@code all} operation.
 * <p>
 * The reason why we have this custom executor is that unlike regular scopes, the {@code all} operation requires
 * that all processors are executed regardless of their failures, since what we really want is to aggregate
 * all the validation errors.
 * <p>
 * However, if one of the nested processors fail with a non validation error, then the execution is aborted and the
 * unexpected error is raised.
 * <p>
 * If only validation errors are found, a {@code VALIDATION:MULTIPLE} error is raised.
 *
 * @since 1.0
 */
public class AllOperationExecutor implements ComponentExecutor<OperationModel> {

  @Override
  public Publisher<Object> execute(ExecutionContext<OperationModel> executionContext) {
    HasMessageProcessors chain = executionContext.getParameter("validations");
    final ExecutionContextAdapter<OperationModel> context = (ExecutionContextAdapter<OperationModel>) executionContext;
    final CoreEvent event = context.getEvent();
    final Optional<ComponentLocation> location = ofNullable(context.getComponentLocation());

    List<Error> errors = new ArrayList<>(chain.getMessageProcessors().size());
    for (Processor processor : chain.getMessageProcessors()) {
      BaseEventContext childContext = newChildContext(event, location);
      final CoreEvent processEvent = CoreEvent.builder(childContext, event).build();
      try {
        CoreEvent result = processor.process(processEvent);
        childContext.success(result);
      } catch (EventProcessingException e) {
        childContext.error(e);
        Error error = e.getEvent().getError().orElse(null);
        if (error != null && isValidation(error.getErrorType())) {
          errors.add(error);
        } else {
          return error(e);
        }
      } catch (Exception e) {
        childContext.error(e);
        return error(e);
      }
    }

    if (errors.isEmpty()) {
      return empty();
    }

    return error(MultipleValidationException.of(errors));
  }

  private boolean isValidation(ErrorType errorType) {
    if (errorType == null) {
      return false;
    }

    if (VALIDATION.getType().equals(errorType.getIdentifier()) && "MULE".equals(errorType.getNamespace())) {
      return true;
    }

    return isValidation(errorType.getParentErrorType());
  }
}
