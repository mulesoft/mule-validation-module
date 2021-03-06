/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.internal.validator;

import static org.mule.extension.validation.api.ValidationErrorType.INVALID_URL;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.internal.ValidationContext;
import org.mule.runtime.api.i18n.I18nMessage;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * An {@link AbstractValidator} which verifies that an instance of {@link URL} can be created from a given {@link #url}. If
 * {@link URL#URL(String)} throws exception when invoked with {@link #url}, then the validation will fail
 *
 * @since 1.0
 */
public class UrlValidator extends AbstractValidator {

  /**
   * the url to be tested
   */
  private final String url;


  public UrlValidator(String url, ValidationContext validationContext) {
    super(validationContext);
    this.url = url;
  }

  @Override
  public ValidationResult validate() {
    try {
      new URL(url);
      return ok();
    } catch (MalformedURLException e) {
      return fail();
    }
  }

  @Override
  protected ValidationErrorType getErrorType() {
    return INVALID_URL;
  }

  @Override
  protected I18nMessage getDefaultErrorMessage() {
    return getMessages().invalidUrl(url);
  }
}
