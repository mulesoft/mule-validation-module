/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mule.runtime.extension.api.error.MuleErrors.EXPRESSION;

import org.mule.extension.validation.api.MultipleValidationException;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.functional.api.flow.FlowRunner;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class AggregateValidationTestCase extends ValidationTestCase {

  private static final String EMAIL_VALIDATION_FLOW = "email";
  private static final String VALIDATION_NAMESPACE = "VALIDATION";
  private static final String MULTIPLE_ERROR = "MULTIPLE";

  @Rule
  public ExpectedError expected = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "aggregate-validations.xml";
  }

  @Test
  public void keepsPayloadWhenAllValidationsPass() throws Exception {
    FlowRunner runner = configureGetAllRunner(flowRunner("all"), VALID_EMAIL, VALID_URL);

    assertThat(runner.buildEvent().getMessage().getPayload().getValue(),
               is(sameInstance(runner.run().getMessage().getPayload().getValue())));
  }

  @Test
  @Ignore("MULE-15778")
  public void keepsPayloadWhenAllWithAnotherExtensionValidationsPass() throws Exception {
    FlowRunner runner = configureGetAllRunner(flowRunner("allWithAnotherExtension"), VALID_EMAIL, VALID_URL);

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
  public void keepsPayloadWhenAnyValidationsPass() throws Exception {
    FlowRunner runner = configureGetAllRunner(flowRunner("any"), VALID_EMAIL, VALID_URL);

    assertThat(runner.buildEvent().getMessage().getPayload().getValue(),
               is(sameInstance(runner.run().getMessage().getPayload().getValue())));
  }

  @Test
  @Ignore("MULE-15778")
  public void keepsPayloadWhenAnyWithAnotherExtensionValidationsPass() throws Exception {
    FlowRunner runner = configureGetAllRunner(flowRunner("anyWithAnotherExtension"), VALID_EMAIL, VALID_URL);

    assertThat(runner.buildEvent().getMessage().getPayload().getValue(),
               is(sameInstance(runner.run().getMessage().getPayload().getValue())));
  }

  @Test
  public void twoFailuresInAny() throws Exception {
    expected.expectErrorType(VALIDATION_NAMESPACE, MULTIPLE_ERROR);
    expected.expectCause(is(instanceOf(MultipleValidationException.class)));
    expected.expectMessage(equalTo(messages.invalidUrl(INVALID_URL) + "\n" + messages.invalidEmail(INVALID_EMAIL)));

    configureGetAllRunner(flowRunner("any"), INVALID_EMAIL, INVALID_URL).run();
  }

  @Test
  public void nonValidationErrorInsideAny() throws Exception {
    expected.expectErrorType("MULE", EXPRESSION.getType());
    configureGetAllRunner(flowRunner("anyWithNonValidationError"), VALID_EMAIL, VALID_URL).run();
  }

  @Test
  public void nonValidationErrorMixedWithValidationErrorsInsideAny() throws Exception {
    expected.expectErrorType("MULE", EXPRESSION.getType());
    configureGetAllRunner(flowRunner("anyWithNonValidationError"), INVALID_EMAIL, INVALID_URL).run();
  }

  @Test
  public void oneFailInAny() throws Exception {
    assertValid(configureGetAllRunner(flowRunner("any"), INVALID_EMAIL, VALID_URL));
  }

  private FlowRunner configureGetAllRunner(FlowRunner runner, String email, String url) {
    return runner.withPayload("").withVariable("url", url).withVariable(EMAIL_VALIDATION_FLOW, email);
  }

}
