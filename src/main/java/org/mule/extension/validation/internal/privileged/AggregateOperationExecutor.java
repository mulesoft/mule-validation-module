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
import static reactor.core.publisher.Mono.error;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.reactivestreams.Publisher;

abstract class AggregateOperationExecutor implements ComponentExecutor<OperationModel> {

  @Override
  public Publisher<Object> execute(ExecutionContext<OperationModel> executionContext) {
    HasMessageProcessors chain = executionContext.getParameter("validations");
    final ExecutionContextAdapter<OperationModel> context = (ExecutionContextAdapter<OperationModel>) executionContext;
    final CoreEvent event = context.getEvent();
    final Optional<ComponentLocation> location = ofNullable(context.getComponent().getLocation());

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
          // propagated error must have its event tied to the context of the originally passed event, not a child
          return error(new EventProcessingException(CoreEvent.builder(event.getContext(), event).build(), e.getCause()));
        }
      } catch (Exception e) {
        childContext.error(e);
        return error(e);
      }
    }

    return handleValidationErrors(chain, errors);
  }

  protected abstract Publisher<Object> handleValidationErrors(HasMessageProcessors chain, List<Error> errors);

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
