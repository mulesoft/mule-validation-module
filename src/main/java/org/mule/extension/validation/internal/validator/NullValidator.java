/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.NOT_NULL;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import static org.mule.runtime.api.metadata.DataType.BOOLEAN;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.el.BindingContext;
import org.mule.runtime.api.el.ExpressionLanguage;
import org.mule.runtime.api.i18n.I18nMessage;
import org.mule.runtime.api.metadata.TypedValue;

/**
 * An {@link AbstractValidator} which verifies that a {@link #value} is {@code null}.
 *
 * @since 1.0
 */
public class NullValidator extends AbstractValidator {

  private final TypedValue<Object> value;
  private final ExpressionLanguage expressionLanguage;

  public NullValidator(TypedValue<Object> value, ValidationContext validationContext, ExpressionLanguage expressionLanguage) {
    super(validationContext);
    this.value = value;
    this.expressionLanguage = expressionLanguage;
  }

  @Override
  public ValidationResult validate() {
    if (value == null) {
      return ok();
    }

    BindingContext ctx = BindingContext.builder().addBinding("payload", value).build();
    TypedValue<Boolean> eval = (TypedValue<Boolean>) expressionLanguage.evaluate("payload == null", BOOLEAN, ctx);

    return eval.getValue() ? ok() : fail();
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return NOT_NULL;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return getMessages().wasExpectingNull();
  }
}
