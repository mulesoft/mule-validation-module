/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.mule.extension.validation.api.NumberType;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.construct.FlowConstruct;
import org.mule.runtime.core.api.el.ExpressionManager;
import org.mule.runtime.core.api.event.CoreEvent;

import java.util.Optional;

import org.junit.Test;

public class ValidationElTestCase extends ValidationTestCase {

  private ExpressionManager expressionManager;
  private static final String NOT_EXISTENT_FLOW = "not_existent";

  @Override
  protected void doSetUp() throws Exception {
    expressionManager = muleContext.getExpressionManager();

    //Mock registry so that a flow that does not exist is lookedUp, it's found
    FlowConstruct flow = mock(FlowConstruct.class);
    when(flow.getName()).thenReturn(NOT_EXISTENT_FLOW);
    registry = spy(registry);
    when(registry.lookupByName(NOT_EXISTENT_FLOW)).thenReturn(Optional.of(flow));
  }

  @Override
  protected String[] getConfigFiles() {
    return new String[] {};
  }

  @Test
  public void email() throws Exception {
    final String expression = "#[Validation::isEmail(vars.email)]";

    CoreEvent event = flowRunner(NOT_EXISTENT_FLOW).withPayload("").withVariable("email", VALID_EMAIL).buildEvent();

    assertValid(expression, event);

    event = CoreEvent.builder(event).addVariable("email", INVALID_EMAIL).build();
    assertInvalid(expression, event);
  }

  @Test
  public void matchesRegex() throws Exception {
    final String regex = "[tT]rue";
    final String expression = "#[Validation::matchesRegex(payload, vars.regexp, vars.caseSensitive)]";

    CoreEvent event = flowRunner(NOT_EXISTENT_FLOW).withPayload("true")
        .withVariable("regexp", regex)
        .withVariable("caseSensitive", false)
        .buildEvent();

    assertValid(expression, event);

    event = CoreEvent.builder(event).message(Message.builder(event.getMessage()).value("TRUE").build()).build();
    assertValid(expression, event);

    event = CoreEvent.builder(event).addVariable("caseSensitive", true).build();
    assertInvalid(expression, event);

    event = CoreEvent.builder(event).message(Message.builder(event.getMessage()).value("tTrue").build()).build();
    assertInvalid(expression, event);
  }

  @Test
  public void isTime() throws Exception {
    final String time = "12:08 PM";

    CoreEvent event = flowRunner(NOT_EXISTENT_FLOW)
        .withPayload(time)
        .withVariable("validPattern", "h:mm a")
        .withVariable("invalidPattern", "yyMMddHHmmssZ")
        .buildEvent();

    assertValid("#[Validation::isTime(payload, vars.validPattern)]", event);
    assertValid("#[Validation::isTime(payload, vars.validPattern, 'US')]", event);

    assertInvalid("#[Validation::isTime(payload, vars.invalidPattern)]", event);
    assertInvalid("#[Validation::isTime(payload, vars.invalidPattern, 'US')]", event);
  }

  @Test
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
    assertValid(expression, flowRunner(NOT_EXISTENT_FLOW).withPayload("127.0.0.1").buildEvent());
    assertInvalid(expression, flowRunner(NOT_EXISTENT_FLOW).withPayload("ET phone home").buildEvent());
  }

  @Test
  public void url() throws Exception {
    final String expression = "#[Validation::isUrl(payload)]";
    assertValid(expression, flowRunner(NOT_EXISTENT_FLOW).withPayload(VALID_URL).buildEvent());
    assertInvalid(expression, flowRunner(NOT_EXISTENT_FLOW).withPayload(INVALID_URL).buildEvent());
  }

  private void assertNumberValue(String expression, NumberType numberType, String value) throws Exception {
    assertValid(expression, getNumberValidationEvent(value, numberType));
    final String invalid = "unparseable";
    assertInvalid(expression, getNumberValidationEvent(invalid, numberType));
  }

  private CoreEvent getNumberValidationEvent(String value, NumberType numberType) throws Exception {
    CoreEvent event = flowRunner(NOT_EXISTENT_FLOW)
        .withPayload(value)
        .withVariable("numberType", numberType)
        .buildEvent();

    return event;
  }

  private boolean evaluate(String expression, CoreEvent event) {
    return (boolean) expressionManager.evaluate(expression, event).getValue();
  }

  private void assertValid(String expression, CoreEvent event) {
    testExpression(expression, event, true);
  }

  private void assertInvalid(String expression, CoreEvent event) {
    testExpression(expression, event, false);
  }

  private void testExpression(String expression, CoreEvent event, boolean expected) {
    assertThat(evaluate(expression, event), is(expected));
  }
}
