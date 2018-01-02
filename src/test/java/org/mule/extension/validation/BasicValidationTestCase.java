/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mule.extension.validation.api.ValidationExtension.DEFAULT_LOCALE;
import static org.mule.extension.validation.api.error.ValidationErrorType.ELAPSED_TIME;
import static org.mule.extension.validation.internal.ImmutableValidationResult.error;
import static org.mule.runtime.extension.api.error.MuleErrors.EXPRESSION;

import org.mule.extension.validation.api.MultipleValidationException;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.Validator;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.functional.api.flow.FlowRunner;

import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

public class BasicValidationTestCase extends ValidationTestCase {

  private static final String CUSTOM_VALIDATOR_MESSAGE = "Do you wanna build a snowman?";
  private static final String EMAIL_VALIDATION_FLOW = "email";
  private static final String VALIDATION_NAMESPACE = "VALIDATION";
  private static final String MULTIPLE_ERROR = "MULTIPLE";

  @Rule
  public ExpectedError expected = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "basic-validations.xml";
  }

  @Test
  public void email() throws Exception {
    assertValid(flowRunner(EMAIL_VALIDATION_FLOW).withPayload(VALID_EMAIL));
    assertInvalidEmail(INVALID_EMAIL);
    assertInvalidEmail(" " + VALID_EMAIL);
    assertInvalidEmail(VALID_EMAIL + " ");
  }

  @Test
  public void ip() throws Exception {
    assertValid(flowRunner("ip").withPayload("127.0.0.1"));
    assertValid(flowRunner("ip").withPayload("FE80:0000:0000:0000:0202:B3FF:FE1E:8329"));
    assertValid(flowRunner("ip").withPayload("FE80::0202:B3FF:FE1E:8329"));
    assertValid(flowRunner("ip").withPayload("0.0.0.0"));
    assertValid(flowRunner("ip").withPayload("0.0.0.1"));
    assertValid(flowRunner("ip").withPayload("10.0.0.0"));
    assertValid(flowRunner("ip").withPayload("192.168.0.0"));
    assertValid(flowRunner("ip").withPayload("172.16.0.0"));
    assertInvalid(flowRunner("ip").withPayload("1.1.256.0"), messages.invalidIp("1.1.256.0"));
    assertInvalid(flowRunner("ip").withPayload("0.0.0.a"), messages.invalidIp("0.0.0.a"));
    assertInvalid(flowRunner("ip").withPayload("12.1.2."), messages.invalidIp("12.1.2."));
    assertInvalid(flowRunner("ip").withPayload("192.168.100.0/24"), messages.invalidIp("192.168.100.0/24"));
    assertInvalid(flowRunner("ip").withPayload(0), messages.invalidIp("0"));
    String invalidIp = "12.1.2";
    assertInvalid(flowRunner("ip").withPayload(invalidIp), messages.invalidIp(invalidIp));
    invalidIp = "FE80:0000:0000";
    assertInvalid(flowRunner("ip").withPayload(invalidIp), messages.invalidIp(invalidIp));
  }

  @Test
  public void url() throws Exception {
    assertValid(flowRunner("url").withPayload(VALID_URL));
    assertValid(flowRunner("url")
        .withPayload("http://username:password@example.com:8042/over/there/index.dtb?type=animal&name=narwhal#nose"));
    assertInvalid(flowRunner("url").withPayload(INVALID_URL), messages.invalidUrl("here"));
  }

  @Test
  public void time() throws Exception {
    final String time = "12:08 PM";

    assertValid(configureTimeRunner(flowRunner("time"), time, "h:mm a"));
    assertValid(configureTimeRunner(flowRunner("time"), "Wed, Jul 4, '01", "EEE, MMM d, ''yy"));
    final String invalidPattern = "yyMMddHHmmssZ";
    assertInvalid(configureTimeRunner(flowRunner("time"), time, invalidPattern),
                  messages.invalidTime(time, DEFAULT_LOCALE, invalidPattern));
  }

  private FlowRunner configureTimeRunner(FlowRunner runner, String time, String pattern) {
    return runner.withPayload(time).withVariable("pattern", pattern);
  }

  @Test
  public void matchesRegex() throws Exception {
    final String regex = "[tT]rue";

    FlowRunner runner =
        flowRunner("matchesRegex").withPayload("true").withVariable("regexp", regex).withVariable("caseSensitive", false);
    assertValid(runner);

    String testValue = "TRUE";
    assertValid(runner.withPayload(testValue));

    assertInvalid(runner.withVariable("caseSensitive", true), messages.regexDoesNotMatch(testValue, regex));

    testValue = "tTrue";
    assertInvalid(runner.withPayload(testValue), messages.regexDoesNotMatch(testValue, regex));
  }

  @Test
  public void size() throws Exception {
    assertSize("abc");
    assertSize(Arrays.asList("a", "b", "c"));
    assertSize(new String[] {"a", "b", "c"});

    Map<String, String> map = new HashMap<>();
    map.put("a", "a");
    map.put("b", "b");
    map.put("c", "c");

    assertSize(map);
  }

  @Test
  public void isTrue() throws Exception {
    assertValid(flowRunner("isTrue").withPayload(true));
    assertInvalid(flowRunner("isTrue").withPayload(false), messages.failedBooleanValidation(false, true));
  }

  @Test
  public void isFalse() throws Exception {
    assertValid(flowRunner("isFalse").withPayload(false));
    assertInvalid(flowRunner("isFalse").withPayload(true), messages.failedBooleanValidation(true, false));
  }

  @Test
  public void notEmpty() throws Exception {
    final String flow = "notEmpty";

    assertValid(flowRunner(flow).withPayload(singletonList("a")));
    assertValid(flowRunner(flow).withPayload(new String[] {"a"}));
    assertInvalid(flowRunner(flow).withPayload(null), messages.valueIsNull());
    assertInvalid(flowRunner(flow).withPayload(ImmutableList.of()), messages.collectionIsEmpty());
    assertInvalid(flowRunner(flow).withPayload(new String[] {}), messages.collectionIsEmpty());
    assertInvalid(flowRunner(flow).withPayload(new Object[] {}), messages.collectionIsEmpty());
    assertInvalid(flowRunner(flow).withPayload(new int[] {}), messages.collectionIsEmpty());
  }

  @Test
  public void notBlank() throws Exception {
    final String flow = "notBlank";

    assertValid(flowRunner(flow).withPayload("a"));
    assertInvalid(flowRunner(flow).withPayload(null), messages.valueIsNull());
    assertInvalid(flowRunner(flow).withPayload(""), messages.stringIsBlank());
  }

  @Test
  public void empty() throws Exception {
    final String flow = "empty";

    assertValid(flowRunner(flow).withPayload(ImmutableList.of()));
    assertValid(flowRunner(flow).withPayload(new String[] {}));
    assertInvalid(flowRunner(flow).withPayload(singletonList("a")), messages.collectionIsNotEmpty());
    assertInvalid(flowRunner(flow).withPayload(new String[] {"a"}), messages.collectionIsNotEmpty());
    assertInvalid(flowRunner(flow).withPayload(new Object[] {new Object()}), messages.collectionIsNotEmpty());
    assertInvalid(flowRunner(flow).withPayload(new int[] {0}), messages.collectionIsNotEmpty());
  }

  @Test
  public void blank() throws Exception {
    final String flow = "blank";

    assertValid(flowRunner(flow).withPayload(""));
    assertInvalid(flowRunner(flow).withPayload("a"), messages.stringIsNotBlank());
  }

  @Test
  public void keepsPayloadWhenAllValidationsPass() throws Exception {
    FlowRunner runner = configureGetAllRunner(flowRunner("all"), VALID_EMAIL, VALID_URL);

    assertThat(runner.buildEvent().getMessage().getPayload().getValue(),
               is(sameInstance(runner.run().getMessage().getPayload().getValue())));
  }

  @Test
  public void twoFailuresInAll() throws Exception {
    expected.expectErrorType(VALIDATION_NAMESPACE, MULTIPLE_ERROR);
    expected.expectCause(is(instanceOf(MultipleValidationException.class)));
    expected.expectMessage(equalTo(messages.invalidUrl(INVALID_URL) + "\n" + messages.invalidEmail(INVALID_EMAIL)));

    configureGetAllRunner(flowRunner("all"), INVALID_EMAIL, INVALID_URL).run();
  }

  @Test
  public void nonValidationErrorInsideAll() throws Exception {
    expected.expectErrorType("MULE", EXPRESSION.getType());
    configureGetAllRunner(flowRunner("allWithNonValidationError"), VALID_EMAIL, VALID_URL).run();
  }

  @Test
  public void nonValidationErrorMixedWithValidationErrorsInsideAll() throws Exception {
    expected.expectErrorType("MULE", EXPRESSION.getType());
    configureGetAllRunner(flowRunner("allWithNonValidationError"), INVALID_EMAIL, INVALID_URL).run();
  }

  @Test
  public void oneFailInAll() throws Exception {
    expected.expectErrorType(VALIDATION_NAMESPACE, MULTIPLE_ERROR);
    expected.expectCause(is(instanceOf(MultipleValidationException.class)));
    expected.expectMessage(containsString(messages.invalidEmail(INVALID_EMAIL).getMessage()));

    configureGetAllRunner(flowRunner("all"), INVALID_EMAIL, VALID_URL).run();
  }

  @Test
  public void customValidationByClass() throws Exception {
    assertCustomValidator("customValidationByClass", CUSTOM_VALIDATOR_MESSAGE, CUSTOM_VALIDATOR_MESSAGE);
  }

  @Test
  public void customValidationByRef() throws Exception {
    assertCustomValidator("customValidationByRef", null, CUSTOM_VALIDATOR_MESSAGE);
  }

  @Test
  public void customValidatorWithCustomMessage() throws Exception {
    final String customMessage = "doesn't have to be a snowman";
    assertCustomValidator("customValidationByClass", customMessage, customMessage);
  }

  @Test
  public void usesValidatorAsRouter() throws Exception {
    final String flowName = "choice";

    assertThat(getPayloadAsString(flowRunner(flowName).withPayload(VALID_EMAIL).run().getMessage()), is("valid"));
    assertThat(getPayloadAsString(flowRunner(flowName).withPayload(INVALID_EMAIL).run().getMessage()), is("invalid"));
  }

  @Test
  public void expirationSuccess() throws Exception {
    assertValid(flowRunner("expiration").withVariable("time", LocalDateTime.now()));
  }

  @Test
  public void expirationFail() throws Exception {
    expected.expectErrorType(VALIDATION_NAMESPACE, ELAPSED_TIME.name());

    LocalDateTime currentTime = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
    assertValid(flowRunner("expiration").withVariable("time", currentTime));
  }

  private void assertCustomValidator(String flowName, String customMessage, String expectedMessage) throws Exception {
    expected.expectMessage(containsString(expectedMessage));
    expected.expectErrorType("VALIDATION", "VALIDATION");

    flowRunner(flowName).withPayload("").withVariable("customMessage", customMessage).run();
  }

  private FlowRunner configureGetAllRunner(FlowRunner runner, String email, String url) {
    return runner.withPayload("").withVariable("url", url).withVariable(EMAIL_VALIDATION_FLOW, email);
  }

  private void assertInvalidEmail(String address) throws Exception {
    assertInvalid(flowRunner(EMAIL_VALIDATION_FLOW).withPayload(address), messages.invalidEmail(address));
  }

  private void assertSize(Object value) throws Exception {
    final String flowName = "size";
    final int expectedSize = 3;
    int minLength = 0;
    int maxLength = 3;

    assertValid(configureSizeValidationRunner(flowRunner(flowName), value, minLength, maxLength));
    minLength = 3;
    assertValid(configureSizeValidationRunner(flowRunner(flowName), value, minLength, maxLength));

    maxLength = 2;
    assertInvalid(configureSizeValidationRunner(flowRunner(flowName), value, minLength, maxLength),
                  messages.greaterThanMaxSize(value, maxLength, expectedSize));

    minLength = 5;
    maxLength = 10;
    assertInvalid(configureSizeValidationRunner(flowRunner(flowName), value, minLength, maxLength),
                  messages.lowerThanMinSize(value, minLength, expectedSize));
  }

  private FlowRunner configureSizeValidationRunner(FlowRunner runner, Object value, int minLength, int maxLength)
      throws Exception {
    runner.withPayload(value).withVariable("minLength", minLength).withVariable("maxLength", maxLength);

    return runner;
  }

  public static class TestCustomValidator implements Validator {

    @Override
    public ValidationResult validate() {
      return error(CUSTOM_VALIDATOR_MESSAGE);
    }
  }
}
