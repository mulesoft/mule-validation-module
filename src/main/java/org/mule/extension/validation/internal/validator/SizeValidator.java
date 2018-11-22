/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.INVALID_SIZE;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import static org.mule.runtime.api.metadata.DataType.NUMBER;
import static org.mule.runtime.api.util.Preconditions.checkArgument;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.el.BindingContext;
import org.mule.runtime.api.el.ExpressionLanguage;
import org.mule.runtime.api.i18n.I18nMessage;
import org.mule.runtime.api.metadata.TypedValue;

import java.util.Collection;
import java.util.Map;

/**
 * An {@link AbstractValidator} which verifies that {@link #typedValue} has a size between certain inclusive boundaries. This
 * validator is capable of handling instances of {@link String}, {@link Collection}, {@link Map} and arrays
 *
 * @since 1.0
 */
public class SizeValidator extends AbstractValidator {

  private final TypedValue typedValue;
  private final int minSize;
  private final Integer maxSize;
  private final ExpressionLanguage expressionLanguage;

  private I18nMessage errorMessage;

  public SizeValidator(TypedValue typedValue, int minSize, Integer maxSize, ValidationContext validationContext,
                       ExpressionLanguage expressionLanguage) {
    super(validationContext);
    this.typedValue = typedValue;
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.expressionLanguage = expressionLanguage;
  }

  @Override
  public ValidationResult validate() {
    int inputLength = getSize(typedValue);
    if (inputLength < minSize) {
      errorMessage = getMessages().lowerThanMinSize(typedValue.getValue(), minSize, inputLength);
      return fail();
    }

    if (maxSize != null && inputLength > maxSize) {
      errorMessage = getMessages().greaterThanMaxSize(typedValue.getValue(), maxSize, inputLength);
      return fail();
    }

    return ok();
  }

  private int getSize(TypedValue typedValue) {
    checkArgument(typedValue.getValue() != null, "Cannot check size of a null value");

    BindingContext context = BindingContext.builder().addBinding("payload", typedValue).build();
    Object expressionValue = expressionLanguage.evaluate("#[sizeOf(payload)]",
                                                         NUMBER,
                                                         context)
        .getValue();
    if (expressionValue instanceof Integer) {
      return (Integer) expressionValue;
    } else {
      throw new IllegalArgumentException("There was a problem while calculating the size for the validation");
    }
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return INVALID_SIZE;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return errorMessage;
  }
}
