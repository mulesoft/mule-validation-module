/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.privileged;

import static java.util.Optional.ofNullable;

import static org.mule.runtime.core.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
import static org.mule.runtime.core.privileged.processor.MessageProcessors.newChain;
import static org.mule.runtime.core.privileged.processor.MessageProcessors.newChildContext;
import static org.mule.runtime.extension.api.error.MuleErrors.VALIDATION;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.message.ErrorType;
import org.mule.runtime.api.meta.model.operation.OperationModel;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.context.MuleContextAware;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.processor.Processor;
import org.mule.runtime.core.privileged.event.BaseEventContext;
import org.mule.runtime.core.privileged.exception.EventProcessingException;
import org.mule.runtime.core.privileged.processor.chain.HasMessageProcessors;
import org.mule.runtime.core.privileged.processor.chain.MessageProcessorChain;
import org.mule.runtime.extension.api.runtime.operation.CompletableComponentExecutor;
import org.mule.runtime.extension.api.runtime.operation.ExecutionContext;
import org.mule.runtime.module.extension.api.runtime.privileged.ExecutionContextAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

abstract class AggregateOperationExecutor implements CompletableComponentExecutor<OperationModel>, MuleContextAware {

  private MuleContext muleContext;

  public void setMuleContext(MuleContext context) {
    this.muleContext = context;
  }

  public void execute(ExecutionContext<OperationModel> executionContext, ExecutorCallback callback) {

    HasMessageProcessors chain = executionContext.getParameter("validations");

    final ExecutionContextAdapter<OperationModel> context = (ExecutionContextAdapter<OperationModel>) executionContext;
    final CoreEvent event = context.getEvent();
    final Optional<ComponentLocation> location = ofNullable(context.getComponent().getLocation());

    List<Error> errors = new ArrayList<>(chain.getMessageProcessors().size());
    for (Processor processor : chain.getMessageProcessors()) {
      // A new chain is created for each processor so that they can be intercepted by MUnit
      MessageProcessorChain messageChain = newChain(Optional.empty(),
                                                    Collections.singletonList(processor));
      BaseEventContext childContext = newChildContext(event, location);
      final CoreEvent processEvent = CoreEvent.builder(childContext, event).build();
      try {
        // The chain is initialized with the muleContext so that it can run correctly
        initialiseIfNeeded(messageChain, muleContext);
        CoreEvent result = messageChain.process(processEvent);
        childContext.success(result);
      } catch (EventProcessingException e) {
        childContext.error(e);
        Error error =
            e.getEvent().getError().orElse(null);
        if (error != null && isValidation(error.getErrorType())) {
          errors.add(error);
        } else {
          // propagated error must have its event tied to the context of the originally passed event, not a child
          callback.error(new EventProcessingException(CoreEvent.builder(event.getContext(), event).error(error).build(),
                                                      e.getCause()));
        }
      } catch (Exception e) {
        childContext.error(e);
        callback.error(e);
      }
    }
    handleValidationErrors(callback, chain, errors);
  }

  protected abstract void handleValidationErrors(ExecutorCallback callback, HasMessageProcessors chain, List<Error> errors);

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
