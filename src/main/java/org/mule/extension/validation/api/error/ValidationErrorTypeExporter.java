/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.api.error;

import org.mule.api.annotation.NoExtend;
import org.mule.extension.validation.api.ValidationResult;

/**
 * The only porpouse of this class is to export the {@link ValidationErrorType} with the
 * {@link org.mule.runtime.extension.api.annotation.Export} annotation. {@link ValidationErrorType} should be exported because if
 * a user needs to create a custom validator, implementing the {@link org.mule.extension.validation.api.Validator} interface, they
 * should be able to create a {@link ValidationResult} which returns a {@link ValidationErrorType} for the method
 * {@link ValidationResult#getErrorType()}
 *
 * @since 1.2.0
 */

@NoExtend
public class ValidationErrorTypeExporter {
}
