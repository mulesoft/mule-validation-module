/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mule.extension.validation.internal.ImmutableValidationResult.error;
import static org.mule.extension.validation.internal.ValidationExtension.DEFAULT_LOCALE;
import org.mule.api.MuleEvent;
import org.mule.extension.validation.api.MultipleValidationException;
import org.mule.extension.validation.api.MultipleValidationResult;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.Validator;
import org.mule.mvel2.compiler.BlankLiteral;
import org.mule.util.ExceptionUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class BasicValidationTestCase extends ValidationTestCase
{

    private static final String CUSTOM_VALIDATOR_MESSAGE = "Do you wanna build a snowman?";

    @Override
    protected String getConfigFile()
    {
        return "basic-validations.xml";
    }

    @Test
    public void email() throws Exception
    {
        assertValid("email", getTestEvent(VALID_EMAIL));
        assertInvalid("email", getTestEvent(INVALID_EMAIL), messages.invalidEmail("@mulesoft.com"));
    }

    @Test
    public void ip() throws Exception
    {
        assertValid("ip", getTestEvent("127.0.0.1"));
        assertInvalid("ip", getTestEvent("12.1.2"), messages.invalidIp("12.1.2"));
    }

    @Test
    public void url() throws Exception
    {
        assertValid("url", getTestEvent(VALID_URL));
        assertValid("url", getTestEvent("http://username:password@example.com:8042/over/there/index.dtb?type=animal&name=narwhal#nose"));
        assertInvalid("url", getTestEvent(INVALID_URL), messages.invalidUrl("here"));
    }

    @Test
    public void time() throws Exception
    {
        final String time = "12:08 PM";

        assertValid("time", getTimeEvent(time, "h:mm a"));
        assertValid("time", getTimeEvent("Wed, Jul 4, '01", "EEE, MMM d, ''yy"));

        final String invalidPattern = "yyMMddHHmmssZ";
        assertInvalid("time", getTimeEvent(time, invalidPattern), messages.invalidTime(time, DEFAULT_LOCALE.toString(), invalidPattern));
    }

    private MuleEvent getTimeEvent(String time, String pattern) throws Exception {
        MuleEvent event = getTestEvent(time);
        event.setFlowVariable("pattern", pattern);

        return event;
    }

    @Test
    public void matchesRegex() throws Exception
    {
        final String regex = "[tT]rue";
        MuleEvent event = getTestEvent("true");
        event.setFlowVariable("regexp", regex);
        event.setFlowVariable("caseSensitive", false);

        assertValid("matchesRegex", event);

        String testValue = "TRUE";
        event.getMessage().setPayload(testValue);
        assertValid("matchesRegex", event);

        event.setFlowVariable("caseSensitive", true);
        assertInvalid("matchesRegex", event, messages.regexDoesNotMatch(testValue, regex));

        testValue = "tTrue";
        event.getMessage().setPayload(testValue);
        assertInvalid("matchesRegex", event, messages.regexDoesNotMatch(testValue, regex));
    }

    @Test
    public void size() throws Exception
    {
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
    public void isTrue() throws Exception
    {
        assertValid("isTrue", getTestEvent(true));
        assertInvalid("isTrue", getTestEvent(false), messages.failedBooleanValidation(false, true));
    }

    @Test
    public void isFalse() throws Exception
    {
        assertValid("isFalse", getTestEvent(false));
        assertInvalid("isFalse", getTestEvent(true), messages.failedBooleanValidation(true, false));
    }

    @Test
    public void notEmpty() throws Exception
    {
        final String flowName = "notEmpty";

        assertValid(flowName, getTestEvent("a"));
        assertValid(flowName, getTestEvent(Arrays.asList("a")));
        assertValid(flowName, getTestEvent(new String[] {"a"}));
        Map<String, String> map = new HashMap<>();
        map.put("a", "A");
        assertValid(flowName, getTestEvent(map));

        map.clear();
        assertInvalid(flowName, getTestEvent(null), messages.valueIsNull());
        assertInvalid(flowName, getTestEvent(""), messages.stringIsBlank());
        assertInvalid(flowName, getTestEvent(ImmutableList.of()), messages.collectionIsEmpty());
        assertInvalid(flowName, getTestEvent(new String[] {}), messages.arrayIsEmpty());
        assertInvalid(flowName, getTestEvent(new Object[] {}), messages.arrayIsEmpty());
        assertInvalid(flowName, getTestEvent(new int[] {}), messages.arrayIsEmpty());
        assertInvalid(flowName, getTestEvent(map), messages.mapIsEmpty());
        assertInvalid(flowName, getTestEvent(BlankLiteral.INSTANCE), messages.valueIsBlankLiteral());
    }

    @Test
    public void empty() throws Exception
    {
        final String flowName = "empty";

        assertValid(flowName, getTestEvent(""));
        assertValid(flowName, getTestEvent(ImmutableList.of()));
        assertValid(flowName, getTestEvent(new String[] {}));
        Map<String, String> map = new HashMap<>();
        assertValid(flowName, getTestEvent(map));

        assertInvalid(flowName, getTestEvent("a"), messages.stringIsNotBlank());
        assertInvalid(flowName, getTestEvent(Arrays.asList("a")), messages.collectionIsNotEmpty());
        assertInvalid(flowName, getTestEvent(new String[] {"a"}), messages.arrayIsNotEmpty());
        assertInvalid(flowName, getTestEvent(new Object[] {new Object()}), messages.arrayIsNotEmpty());
        assertInvalid(flowName, getTestEvent(new int[] {0}), messages.arrayIsNotEmpty());
        map.put("a", "a");
        assertInvalid(flowName, getTestEvent(map), messages.mapIsNotEmpty());
    }

    @Test
    public void successfulAll() throws Exception
    {
        MuleEvent event = getAllEvent(VALID_EMAIL, VALID_URL);
        MuleEvent responseEvent = runFlow("all", event);

        // validation was successful and payload was untouched
        assertThat(event.getMessage().getPayload(), is(sameInstance(responseEvent.getMessage().getPayload())));
    }

    @Test
    public void twoFailuresInAllWithoutException() throws Exception
    {
        try
        {
            runFlow("all", getAllEvent(INVALID_EMAIL, INVALID_URL));
            fail("was expecting a failure");
        }
        catch (Exception e)
        {
            Throwable root = ExceptionUtils.getRootCause(e);
            assertThat(root, is(instanceOf(MultipleValidationException.class)));
            MultipleValidationResult result = ((MultipleValidationException) root).getMultipleValidationResult();
            assertThat(result.getFailedValidationResults(), hasSize(2));
            assertThat(result.isError(), is(true));

            String expectedMessage = Joiner.on('\n').join(messages.invalidUrl(INVALID_URL),
                                                          messages.invalidEmail(INVALID_EMAIL));

            assertThat(result.getMessage(), is(expectedMessage));

            for (ValidationResult failedValidationResult : result.getFailedValidationResults())
            {
                assertThat(failedValidationResult.isError(), is(true));
            }
        }
    }

    @Test
    public void oneFailInAll() throws Exception
    {
        try
        {
            runFlow("all", getAllEvent(INVALID_EMAIL, VALID_URL));
            fail("was expecting a failure");
        }
        catch (Exception e)
        {
            Throwable root = ExceptionUtils.getRootCause(e);
            assertThat(root, is(instanceOf(MultipleValidationException.class)));
            MultipleValidationResult result = ((MultipleValidationException) root).getMultipleValidationResult();
            assertThat(result.getFailedValidationResults(), hasSize(1));
            assertThat(result.isError(), is(true));
            assertThat(result.getMessage(), is(messages.invalidEmail(INVALID_EMAIL).getMessage()));
        }
    }

    @Test
    public void customValidationByClass() throws Exception
    {
        assertCustomValidator("customValidationByClass", null, CUSTOM_VALIDATOR_MESSAGE);
    }

    @Test
    public void customValidationByRef() throws Exception
    {
        assertCustomValidator("customValidationByRef", null, CUSTOM_VALIDATOR_MESSAGE);
    }

    @Test
    public void customValidatorWithCustomMessage() throws Exception
    {
        final String customMessage = "doesn't have to be a snowman";
        assertCustomValidator("customValidationByClass", customMessage, customMessage);
    }

    @Test
    public void usesValidatorAsRouter() throws Exception
    {
        final String flowName = "choice";

        assertThat(runFlow(flowName, getTestEvent(VALID_EMAIL)).getMessage().getPayloadAsString(), is("valid"));
        assertThat(runFlow(flowName, getTestEvent(INVALID_EMAIL)).getMessage().getPayloadAsString(), is("invalid"));
    }

    private void assertCustomValidator(String flowName,String customMessage, String expectedMessage) throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("customMessage", customMessage);
        try
        {
            runFlow(flowName, event);
            fail("was expecting a failure");
        }
        catch (Exception e)
        {
            Throwable cause = ExceptionUtils.getRootCause(e);
            assertThat(cause.getMessage(), is(expectedMessage));
        }
    }

    private MuleEvent getAllEvent(String email, String url) throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("url", url);
        event.setFlowVariable("email", email);

        return event;
    }



    private void assertSize(Object value) throws Exception
    {
        final String flowName = "size";
        final int expectedSize = 3;
        int minLength = 0;
        int maxLength = 3;

        assertValid(flowName, getSizeValidationEvent(value, minLength, maxLength));

        maxLength = 2;
        assertInvalid(flowName, getSizeValidationEvent(value, minLength, maxLength), messages.greaterThanMaxSize(value, maxLength, expectedSize));

        minLength = 5;
        maxLength = 10;
        assertInvalid(flowName, getSizeValidationEvent(value, minLength, maxLength), messages.lowerThanMinSize(value, minLength, expectedSize));
    }

    private MuleEvent getSizeValidationEvent(Object value, int minLength, int maxLength) throws Exception
    {
        MuleEvent event = getTestEvent(value);
        event.setFlowVariable("minLength", minLength);
        event.setFlowVariable("maxLength", maxLength);

        return event;
    }

    public static class TestCustomValidator implements Validator
    {

        @Override
        public ValidationResult validate(MuleEvent event)
        {
            return error(CUSTOM_VALIDATOR_MESSAGE);
        }
    }
}
