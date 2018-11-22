/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static org.mule.functional.api.exception.ExpectedError.none;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.tck.junit4.rule.SystemProperty;

import java.io.File;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractValidateSizeFunctionalTestCase extends ValidationTestCase {

  protected static int VALID_SIZE = 8;
  protected static int INVALID_SIZE = 20;

  protected static int MIN_SIZE = 5;
  protected static int MAX_SIZE = 10;

  protected static String VALIDATION_NAMESPACE = "VALIDATION";

  @ClassRule
  public static SystemProperty minSize = new SystemProperty("minSize", String.valueOf(MIN_SIZE));

  @ClassRule
  public static SystemProperty maxSize = new SystemProperty("maxSize", String.valueOf(MAX_SIZE));

  @ClassRule
  public static TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Rule
  public ExpectedError expectedError = none();

  @Rule
  public SystemProperty workingDir = new SystemProperty("workingDir", temporaryFolder.getRoot().getAbsolutePath());

  @After
  public void clearTemporaryFolder() {
    // This only deletes files within the temporaryFolder and empty directories.
    for (File file : temporaryFolder.getRoot().listFiles()) {
      file.delete();
    }
  }

}
