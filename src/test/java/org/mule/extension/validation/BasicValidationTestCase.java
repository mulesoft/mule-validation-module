/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mule.extension.validation.api.ValidationErrorType.ELAPSED_TIME;
import static org.mule.extension.validation.api.ValidationErrorType.NOT_ELAPSED_TIME;
import static org.mule.extension.validation.api.ValidationExtension.DEFAULT_LOCALE;
import static org.mule.runtime.api.metadata.MediaType.APPLICATION_JSON;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.functional.api.flow.FlowRunner;

import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BasicValidationTestCase extends ValidationTestCase {

  private static final String EMAIL_VALIDATION_FLOW = "email";
  private static final String VALIDATION_NAMESPACE = "VALIDATION";

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
    String time = "12:08 PM";

    assertValid(configureTimeRunner(flowRunner("time"), time, "h:mm a"));
    assertValid(configureTimeRunner(flowRunner("time"), "Wed, Jul 4, '01", "EEE, MMM d, ''yy"));
    String invalidPattern = "yyMMddHHmmssZ";
    assertInvalid(configureTimeRunner(flowRunner("time"), time, invalidPattern),
                  messages.invalidTime(time, DEFAULT_LOCALE, invalidPattern));

    invalidPattern = "MM/dd/YYYY";
    time = "34/02/2";
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
  public void isNull() throws Exception {
    final String flow = "isNull";

    assertValid(flowRunner(flow).withPayload(null));
    assertInvalid(flowRunner(flow).withPayload("NOT_NULL"), messages.wasExpectingNull());
  }

  @Test
  public void isNullWithMimeType() throws Exception {
    final String flow = "isNull";

    assertValid(flowRunner(flow).withPayload("null").withMediaType(APPLICATION_JSON));
    assertInvalid(flowRunner(flow).withPayload("\"NOT_NULL\"").withMediaType(APPLICATION_JSON), messages.wasExpectingNull());
  }

  @Test
  public void isNotNull() throws Exception {
    final String flow = "isNotNull";

    assertValid(flowRunner(flow).withPayload("NOT_NULL"));
    assertInvalid(flowRunner(flow).withPayload(null), messages.valueIsNull());
  }

  @Test
  public void isNotNullWithMimeType() throws Exception {
    final String flow = "isNotNull";

    assertValid(flowRunner(flow).withPayload("\"NOT_NULL\"").withMediaType(APPLICATION_JSON));
    assertInvalid(flowRunner(flow).withPayload("null").withMediaType(APPLICATION_JSON), messages.valueIsNull());
  }

  @Test
  public void usesValidatorAsRouter() throws Exception {
    final String flowName = "choice";

    assertThat(getPayloadAsString(flowRunner(flowName).withPayload(VALID_EMAIL).run().getMessage()), is("valid"));
    assertThat(getPayloadAsString(flowRunner(flowName).withPayload(INVALID_EMAIL).run().getMessage()), is("invalid"));
  }

  @Test
  public void notElapsedSuccess() throws Exception {
    assertValid(flowRunner("notElapsed").withVariable("time", LocalDateTime.now()));
  }

  @Test
  public void notElapsedFail() throws Exception {
    expected.expectErrorType(VALIDATION_NAMESPACE, ELAPSED_TIME.name());

    LocalDateTime currentTime = LocalDateTime.now().minus(1, HOURS);
    assertValid(flowRunner("notElapsed").withVariable("time", currentTime));
  }

  @Test
  public void elapsedSuccess() throws Exception {
    LocalDateTime currentTime = LocalDateTime.now().minus(1, HOURS);

    assertValid(flowRunner("elapsed").withVariable("time", currentTime));
  }

  @Test
  public void elapsedFail() throws Exception {
    expected.expectErrorType(VALIDATION_NAMESPACE, NOT_ELAPSED_TIME.name());

    assertValid(flowRunner("elapsed").withVariable("time", LocalDateTime.now()));
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

}
