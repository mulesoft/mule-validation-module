/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;
import static org.mule.extension.validation.AllureConstants.HttpFeature.VALIDATION_EXTENSION;
import static org.mule.functional.junit4.matchers.ThrowableMessageMatcher.hasMessage;

import org.mule.extension.validation.api.ValidationException;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.internal.ValidationMessages;
import org.mule.functional.api.flow.FlowRunner;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.i18n.I18nMessage;
import org.mule.test.runner.ArtifactClassLoaderRunnerConfig;

import io.qameta.allure.Feature;

@ArtifactClassLoaderRunnerConfig(exportPluginClasses = {ValidationMessages.class, ValidationErrorType.class})
@Feature(VALIDATION_EXTENSION)
abstract class ValidationTestCase extends MuleArtifactFunctionalTestCase {

  static final String VALID_URL = "http://localhost:8080";
  static final String INVALID_URL = "here";

  static final String VALID_EMAIL = "mariano.gonzalez@mulesoft.com";
  static final String INVALID_EMAIL = "@mulesoft.com";

  protected ValidationMessages messages;

  @Override
  protected boolean isDisposeContextPerClass() {
    return true;
  }

  @Override
  protected void doSetUp() throws Exception {
    messages = new ValidationMessages();
  }

  protected void assertValid(FlowRunner runner) throws Exception {
    assertThat(!runner.run().getError().isPresent(), is(true));
    runner.reset();
  }

  protected void assertInvalid(FlowRunner runner, I18nMessage expectedMessage) throws Exception {
    runner.runExpectingException(allOf(instanceOf(ValidationException.class),
                                       hasMessage(allOf(is(expectedMessage.getMessage()),
                                                        // assert that all placeholders were replaced in message
                                                        not(containsString("${"))))));
    runner.reset();
  }
}
