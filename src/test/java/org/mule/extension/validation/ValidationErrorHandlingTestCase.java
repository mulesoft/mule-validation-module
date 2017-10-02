/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mule.extension.validation.AllureConstants.HttpFeature.ValidationStory.ERROR_HANDLING;
import static org.mule.extension.validation.api.NumberType.INTEGER;
import static org.mule.functional.junit4.matchers.MessageMatchers.hasPayload;
import static org.mule.tck.junit4.matcher.EventMatcher.hasMessage;

import org.mule.functional.api.flow.FlowRunner;

import org.junit.Ignore;
import org.junit.Test;

import io.qameta.allure.Story;

@Story(ERROR_HANDLING)
public class ValidationErrorHandlingTestCase extends ValidationTestCase {

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"basic-validations.xml", "number-validations.xml", "error-handling.xml"};
  }

  @Test
  public void email() throws Exception {
    verifyHandlerMessage(flowRunner("email").withPayload(INVALID_EMAIL), "Email error");
  }

  @Test
  public void ip() throws Exception {
    verifyHandlerMessage(flowRunner("ip").withPayload("1.1.256.0"), "IP error");
  }

  @Test
  public void url() throws Exception {
    verifyHandlerMessage(flowRunner("url").withPayload(INVALID_URL), "URL error");
  }

  @Test
  public void time() throws Exception {
    verifyHandlerMessage(flowRunner("time").withPayload("12:08 PM").withVariable("pattern", "yyMMddHHmmssZ"), "Time error");
  }

  @Test
  @Ignore("MULE-13688")
  public void regex() throws Exception {
    final String regex = "[tT]rue";
    verifyHandlerMessage(flowRunner("matchesRegex")
        .withPayload("tTrue")
        .withVariable("regexp", regex)
        .withVariable("caseSensitive", false), "Regex error");
  }

  @Test
  public void size() throws Exception {
    verifyHandlerMessage(flowRunner("size")
        .withPayload("abcd")
        .withVariable("minLength", 1)
        .withVariable("maxLength", 3), "Size error");
  }

  @Test
  public void isTrue() throws Exception {
    verifyHandlerMessage(flowRunner("isTrue").withPayload(false), "Boolean error");
  }

  @Test
  public void isFalse() throws Exception {
    verifyHandlerMessage(flowRunner("isFalse").withPayload(true), "Boolean error");
  }

  @Test
  @Ignore("MULE-13688")
  public void notEmpty() throws Exception {
    verifyHandlerMessage(flowRunner("notEmpty").withPayload(""), "Empty error");
  }

  @Test
  @Ignore("MULE-13688")
  public void empty() throws Exception {
    verifyHandlerMessage(flowRunner("empty").withPayload("You know nothing, Jon Snow."), "Not empty error");
  }

  @Test
  public void customValidation() throws Exception {
    verifyHandlerMessage(flowRunner("customValidationByClass").withPayload(TEST_PAYLOAD), "Validation error");
  }

  @Test
  public void number() throws Exception {

    verifyHandlerMessage(flowRunner("validateNumber")
        .withPayload(42)
        .withVariable("minValue", 2)
        .withVariable("maxValue", 30)
        .withVariable("numberType", INTEGER), "Number error");
  }

  @Test
  @Ignore("MULE-13440")
  public void all() throws Exception {
    verifyHandlerMessage(flowRunner("all")
        .withPayload(TEST_PAYLOAD)
        .withVariable("email", INVALID_EMAIL)
        .withVariable("url", INVALID_URL), "Composed error: INVALID_EMAIL INVALID_URL");
  }

  @Test
  public void genericMuleValidationError() throws Exception {
    verifyHandlerMessage(flowRunner("fallbackToMuleValidationError").withPayload("NotAnEmail"),
                         "HANDLED");
  }

  private void verifyHandlerMessage(FlowRunner runnerConfig, String value) throws Exception {
    assertThat(runnerConfig.run(), hasMessage(hasPayload(containsString(value))));
  }

}
