/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.EMPTY_COLLECTION;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import java.util.Collection;

/**
 * Validates that {@link #value} is not an empty collection.
 *
 * @since 1.0
 */
public class NotEmptyCollectionValidator extends AbstractValidator {

  private final Collection<?> value;
  private I18nMessage errorMessage;

  public NotEmptyCollectionValidator(Collection<?> value, ValidationContext validationContext) {
    super(validationContext);
    this.value = value;
  }

  @Override
  public ValidationResult validate() {
    if (value == null) {
      errorMessage = getMessages().valueIsNull();
      return fail();
    }

    if (value.isEmpty()) {
      errorMessage = getMessages().collectionIsEmpty();
      return fail();
    }

    return ok();
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return EMPTY_COLLECTION;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }
}
