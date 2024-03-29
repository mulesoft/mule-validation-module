/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.api;

import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;

/**
 * A class which groups parameters which configure a {@link Validator} but are not the subject of the validation
 *
 * @since 1.0
 */
public final class ValidationOptions {

  /**
   * Specifies the message that is to be notified to the user if the validation fails. It's marked as not dynamic to allow eager
   * evaluation of the expression in case that the validation is successful and the message is not needed. Components consuming
   * this value are to manually check if this is an expression and evaluate it in case that the validation failed
   */
  @Parameter
  @Optional
  private String message = null;

  public ValidationOptions() {}

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
