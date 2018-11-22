/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.api.ValidationException;

import org.junit.Test;

public class ValidateFileListSizeFunctionalTestCase extends AbstractValidateSizeFunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "file-list-size-validation.xml";
  }

  @Test
  public void fileListWithValidSize() throws Exception {
    createFiles(VALID_SIZE);
    flowRunner("file-validation-test").run();
  }

  @Test
  public void fileListWithInvalidSize() throws Exception {
    expectedError.expectError(VALIDATION_NAMESPACE, ValidationErrorType.INVALID_SIZE, ValidationException.class,
                              "expected to have a size of at most 10 but it has 20");
    createFiles(INVALID_SIZE);
    flowRunner("file-validation-test").run();
  }

  private void createFiles(int numberOfFiles) throws Exception {
    for (int i = 0; i < numberOfFiles; i++) {
      temporaryFolder.newFile().createNewFile();
    }
  }
}
