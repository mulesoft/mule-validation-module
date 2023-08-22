/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.privileged;

/**
 * Sets {@link AnyOperationExecutor} as the executor of the {@code any} operation
 *
 * @since 1.3
 */
public class AnyOperationEnricher extends AggregateOperationEnricher {

  @Override
  String getOperationName() {
    return "any";
  }

  @Override
  AggregateOperationExecutor getOperationExecutor() {
    return new AnyOperationExecutor();
  }
}
