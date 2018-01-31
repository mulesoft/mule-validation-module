/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mule.extension.validation.AllureConstants.HttpFeature.ValidationStory.ERROR_HANDLING;
import static org.mule.extension.validation.api.NumberType.INTEGER;
import static org.mule.extension.validation.api.ValidationErrorType.EMPTY_COLLECTION;
import static org.mule.extension.validation.api.ValidationErrorType.INVALID_BOOLEAN;
import static org.mule.extension.validation.api.ValidationErrorType.INVALID_IP;
import static org.mule.extension.validation.api.ValidationErrorType.INVALID_NUMBER;
import static org.mule.extension.validation.api.ValidationErrorType.INVALID_SIZE;
import static org.mule.extension.validation.api.ValidationErrorType.INVALID_TIME;
import static org.mule.extension.validation.api.ValidationErrorType.MISMATCH;
import static org.mule.extension.validation.api.ValidationErrorType.NOT_EMPTY_COLLECTION;
import static org.mule.functional.api.exception.ExpectedError.none;
import static org.mule.functional.junit4.matchers.MessageMatchers.hasPayload;
import static org.mule.tck.junit4.matcher.EventMatcher.hasMessage;
import org.mule.extension.validation.api.ValidationException;
import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.functional.api.flow.FlowRunner;

import io.qameta.allure.Story;
import org.junit.Rule;
import org.junit.Test;

@Story(ERROR_HANDLING)
public class ValidationErrorHandlingTestCase extends ValidationTestCase {

  private static final String VALIDATION = "VALIDATION";

  @Rule
  public ExpectedError expectedError = none();

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"basic-validations.xml", "number-validations.xml", "error-handling.xml"};
  }

  @Test
  public void email() throws Exception {
    expectedError.expectError(VALIDATION, ValidationErrorType.INVALID_EMAIL, ValidationException.class,
                              "@mulesoft.com is not a valid email address");
    flowRunner("email").withPayload(INVALID_EMAIL).run();
  }

  @Test
  public void ip() throws Exception {
    expectedError.expectError(VALIDATION, INVALID_IP, ValidationException.class, "1.1.256.0 is not a valid ip address");
    flowRunner("ip").withPayload("1.1.256.0").run();
  }

  @Test
  public void url() throws Exception {
    expectedError.expectError(VALIDATION, ValidationErrorType.INVALID_URL, ValidationException.class, "here is not a valid url");
    flowRunner("url").withPayload(INVALID_URL).run();
  }

  @Test
  public void time() throws Exception {
    expectedError.expectError(VALIDATION, INVALID_TIME, ValidationException.class,
                              "12:08 PM is not a valid time for the pattern yyMMddHHmmssZ under locale en");
    verifyHandlerMessage(flowRunner("time").withPayload("12:08 PM").withVariable("pattern", "yyMMddHHmmssZ"), "Time error");
  }

  @Test
  public void regex() throws Exception {
    expectedError.expectError(VALIDATION, MISMATCH, ValidationException.class,
                              "tTrue is not valid under the terms of regex [tT]rue");
    final String regex = "[tT]rue";
    verifyHandlerMessage(flowRunner("matchesRegex")
        .withPayload("tTrue")
        .withVariable("regexp", regex)
        .withVariable("caseSensitive", false), "Regex error");
  }

  @Test
  public void size() throws Exception {
    expectedError.expectError(VALIDATION, INVALID_SIZE, ValidationException.class,
                              "value abcd was expected to have a length of at most 3 characters but it has 4");
    verifyHandlerMessage(flowRunner("size")
        .withPayload("abcd")
        .withVariable("minLength", 1)
        .withVariable("maxLength", 3), "Size error");
  }

  @Test
  public void isTrue() throws Exception {
    expectedError.expectError(VALIDATION, INVALID_BOOLEAN, ValidationException.class,
                              "Value was expected to be true but it was false instead");
    verifyHandlerMessage(flowRunner("isTrue").withPayload(false), "Boolean error");
  }

  @Test
  public void isFalse() throws Exception {
    expectedError.expectError(VALIDATION, INVALID_BOOLEAN, ValidationException.class,
                              "Value was expected to be false but it was true instead");
    verifyHandlerMessage(flowRunner("isFalse").withPayload(true), "Boolean error");
  }

  @Test
  public void notEmpty() throws Exception {
    expectedError.expectError(VALIDATION, EMPTY_COLLECTION, ValidationException.class, "Collection is empty");
    flowRunner("notEmpty").withPayload(emptyList()).run();
  }

  @Test
  public void notEmptyWithCustomMessage() throws Exception {
    expectedError.expectError(VALIDATION, EMPTY_COLLECTION, ValidationException.class, "Payload is empty");
    flowRunner("notEmptyWithCustomMessage").withPayload(emptyList()).run();
  }

  @Test
  public void empty() throws Exception {
    expectedError.expectError(VALIDATION, NOT_EMPTY_COLLECTION, ValidationException.class, "Collection is not empty");
    verifyHandlerMessage(flowRunner("empty").withPayload(singletonList("A")), "Not empty error");
  }

  @Test
  public void number() throws Exception {
    expectedError.expectError(VALIDATION, INVALID_NUMBER, ValidationException.class, "42 is greater that 30");
    verifyHandlerMessage(flowRunner("validateNumber")
        .withPayload(42)
        .withVariable("minValue", 2)
        .withVariable("maxValue", 30)
        .withVariable("numberType", INTEGER), "Number error");
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
