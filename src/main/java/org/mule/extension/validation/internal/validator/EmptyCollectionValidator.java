/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.error.ValidationErrorType.NOT_EMPTY_COLLECTION;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.error.ValidationErrorType;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import java.util.Collection;
import java.util.Map;

/**
 * A {@link AbstractValidator} which verifies that a given {@link #value} is empty. The definition of empty depends on the type of
 * {@link #value}. If it's a {@link String} it will check that it is not blank. If it's a {@link Collection}, array or {@link Map}
 * it will check that it's not empty. No other types are supported, an {@link IllegalArgumentException} will be thrown if any
 * other type is supplied
 *
 * @since 1.0
 */
public class EmptyCollectionValidator extends AbstractValidator {

  private final Collection<?> value;
  private I18nMessage errorMessage;

  public EmptyCollectionValidator(Collection<?> value, ValidationContext validationContext) {
    super(validationContext);
    this.value = value;
  }

  @Override
  public ValidationResult validate() {
    if (value != null && !value.isEmpty()) {
      errorMessage = getMessages().collectionIsNotEmpty();
      return fail();
    }
    return ok();
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return NOT_EMPTY_COLLECTION;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }
}
