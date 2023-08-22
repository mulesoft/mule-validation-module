/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.api;

import org.mule.extension.validation.internal.CommonValidationOperations;
import org.mule.extension.validation.internal.NumberValidationOperation;
import org.mule.extension.validation.internal.ValidationMessages;
import org.mule.extension.validation.internal.ValidationStrategies;
import org.mule.extension.validation.internal.el.ValidationFunctions;
import org.mule.extension.validation.internal.error.BasicValidationErrorType;
import org.mule.extension.validation.internal.privileged.AllOperationEnricher;
import org.mule.extension.validation.internal.privileged.AnyOperationEnricher;
import org.mule.runtime.api.lifecycle.Initialisable;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.api.meta.NamedObject;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.config.Config;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.ExpressionFunctions;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.privileged.DeclarationEnrichers;

import java.util.Locale;

import javax.inject.Inject;

/**
 * A module which allows to perform data validations. If the validation fails, an Error is thrown.
 *
 * The error type might vary but it will always be a child of VALIDATION:VALIDATION
 *
 * @since 1.0
 */
@Extension(name = "Validation")
@Operations({CommonValidationOperations.class, ValidationStrategies.class,
    NumberValidationOperation.class})
@Export(resources = {"/META-INF/org/mule/runtime/core/i18n/validation-messages.properties"})
@ErrorTypes(ValidationErrorType.class)
@ExpressionFunctions(ValidationFunctions.class)
@Throws(BasicValidationErrorType.class)
@DeclarationEnrichers({AllOperationEnricher.class, AnyOperationEnricher.class})
public class ValidationExtension implements Config, NamedObject, Initialisable {

  public static final String DEFAULT_LOCALE = Locale.getDefault().getLanguage();

  public static String nullSafeLocale(String locale) {
    return locale == null ? DEFAULT_LOCALE : locale;
  }

  private ValidationMessages messageFactory;

  @Inject
  private MuleContext muleContext;

  /**
   * Allows to configure I18n for the standard error messages
   */
  @Parameter
  @Optional
  private I18NConfig i18n;

  @Override
  public void initialise() throws InitialisationException {
    initialiseMessageFactory();
  }

  private void initialiseMessageFactory() {
    if (i18n == null) {
      messageFactory = new ValidationMessages();
    } else {
      messageFactory = new ValidationMessages(i18n.getBundlePath(), i18n.getLocale());
    }
  }

  public ValidationMessages getMessageFactory() {
    return messageFactory;
  }

  @Override
  public String getName() {
    return "Validation";
  }

  public MuleContext getMuleContext() {
    return muleContext;
  }
}
