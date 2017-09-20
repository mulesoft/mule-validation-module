/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mule.extension.validation.internal.ImmutableValidationResult.ok;

import org.mule.extension.validation.api.CustomValidatorFactory;
import org.mule.extension.validation.api.ValidationExtension;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.Validator;
import org.mule.extension.validation.internal.CustomValidatorOperation;
import org.mule.runtime.core.api.Injector;
import org.mule.runtime.core.api.MuleContext;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import org.junit.Before;
import org.junit.Test;

@SmallTest
public class CustomValidationInjectionTestCase extends AbstractMuleTestCase {

  private final Injector injector = mock(Injector.class);
  private final ValidationExtension config = mock(ValidationExtension.class);
  private final MuleContext muleContext = mock(MuleContext.class);

  @Before
  public void initializeMocks() throws Exception {
    when(config.getMuleContext()).thenReturn(muleContext);
    when(muleContext.getInjector()).thenReturn(injector);
  }

  @Test
  public void injectionInCustomValidator() throws Exception {
    CustomValidatorOperation validator = new CustomValidatorOperation();
    CustomValidatorFactory objectSource = new CustomValidatorFactory(TestValidator.class.getName(), null);
    validator.customValidator(objectSource, null, config);
    verify(injector, atLeastOnce()).inject(any(TestValidator.class));
  }

  public static class TestValidator implements Validator {

    @Override
    public ValidationResult validate() {
      return ok();
    }
  }

}
