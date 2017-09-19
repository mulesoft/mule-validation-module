/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.api.message.Message.of;
import org.mule.extension.validation.api.NumberType;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.InternalEvent;
import org.mule.runtime.core.api.el.ExpressionManager;

import org.junit.Ignore;
import org.junit.Test;

public class ValidationElTestCase extends ValidationTestCase {

  private ExpressionManager expressionManager;

  @Override
  protected void doSetUp() throws Exception {
    expressionManager = muleContext.getExpressionManager();
  }

  @Override
  protected String[] getConfigFiles() {
    return new String[] {};
  }

  @Test
  public void email() throws Exception {
    final String expression = "#[Validation::isEmail(vars.email)]";
    InternalEvent event = eventBuilder().message(of("")).addVariable("email", VALID_EMAIL).build();

    assertValid(expression, event);

    event = InternalEvent.builder(event).addVariable("email", INVALID_EMAIL).build();
    assertInvalid(expression, event);
  }

  @Test
  public void matchesRegex() throws Exception {
    final String regex = "[tT]rue";
    final String expression = "#[Validation::matchesRegex(payload, vars.regexp, vars.caseSensitive)]";

    InternalEvent event = eventBuilder().message(of("true")).addVariable("regexp", regex)
        .addVariable("caseSensitive", false).build();

    assertValid(expression, event);

    event = InternalEvent.builder(event).message(Message.builder(event.getMessage()).value("TRUE").build()).build();
    assertValid(expression, event);

    event = InternalEvent.builder(event).addVariable("caseSensitive", true).build();
    assertInvalid(expression, event);

    event = InternalEvent.builder(event).message(Message.builder(event.getMessage()).value("tTrue").build()).build();
    assertInvalid(expression, event);
  }

  @Test
  public void isTime() throws Exception {
    final String time = "12:08 PM";

    InternalEvent event = InternalEvent.builder(eventBuilder()
        .message(of(time)).build())
        .addVariable("validPattern", "h:mm a")
        .addVariable("invalidPattern", "yyMMddHHmmssZ")
        .build();

    assertValid("#[Validation::isTime(payload, vars.validPattern)]", event);
    assertValid("#[Validation::isTime(payload, vars.validPattern, 'US')]", event);

    assertInvalid("#[Validation::isTime(payload, vars.invalidPattern)]", event);
    assertInvalid("#[Validation::isTime(payload, vars.invalidPattern, 'US')]", event);
  }

  @Test
  @Ignore
  public void isNumber() throws Exception {
    final String expression = "#[Validation::isNumber(payload, vars.numberType)]";
    assertNumberValue(expression, NumberType.LONG, "" + Long.MAX_VALUE / 2);
    assertNumberValue(expression, NumberType.INTEGER, "" + Integer.MAX_VALUE / 2);

    assertNumberValue(expression, NumberType.SHORT, new Short("100").toString());
    assertNumberValue(expression, NumberType.DOUBLE, "10");
    assertNumberValue(expression, NumberType.FLOAT, "10");
  }

  @Test
  public void ip() throws Exception {
    final String expression = "#[Validation::isIp(payload)]";
    assertValid(expression, eventBuilder().message(of("127.0.0.1")).build());
    assertInvalid(expression, eventBuilder().message(of("ET phone home")).build());
  }

  @Test
  public void url() throws Exception {
    final String expression = "#[Validation::isUrl(payload)]";
    assertValid(expression, eventBuilder().message(of(VALID_URL)).build());
    assertInvalid(expression, eventBuilder().message(of(INVALID_URL)).build());
  }

  private void assertNumberValue(String expression, NumberType numberType, String value) throws Exception {
    assertValid(expression, getNumberValidationEvent(value, numberType));
    final String invalid = "unparseable";
    assertInvalid(expression, getNumberValidationEvent(invalid, numberType));
  }

  private InternalEvent getNumberValidationEvent(String value, NumberType numberType) throws Exception {
    InternalEvent event = InternalEvent.builder(eventBuilder()
        .message(of(value)).build())
        .addVariable("numberType", numberType)
        .build();

    return event;
  }

  private boolean evaluate(String expression, InternalEvent event) {
    return (boolean) expressionManager.evaluate(expression, event).getValue();
  }

  private void assertValid(String expression, InternalEvent event) {
    testExpression(expression, event, true);
  }

  private void assertInvalid(String expression, InternalEvent event) {
    testExpression(expression, event, false);
  }

  private void testExpression(String expression, InternalEvent event, boolean expected) {
    assertThat(evaluate(expression, event), is(expected));
  }

}
