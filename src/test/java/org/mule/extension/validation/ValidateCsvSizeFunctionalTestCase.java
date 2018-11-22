/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import org.mule.extension.validation.api.ValidationErrorType;
import org.mule.extension.validation.api.ValidationException;
import org.mule.tck.junit4.rule.SystemProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.ClassRule;
import org.junit.Test;

public class ValidateCsvSizeFunctionalTestCase extends AbstractValidateSizeFunctionalTestCase {

  private static String CSV_FILE_NAME = "file.csv";

  private static String CSV_HEADERS = "A,B,C\n";
  private static String CSV_ROW = "1,2,3\n";

  @ClassRule
  public static SystemProperty csvFileName = new SystemProperty("csvFileName", CSV_FILE_NAME);

  @Override
  protected String getConfigFile() {
    return "csv-size-validation.xml";
  }

  @Test
  public void validateValidCsvSize() throws Exception {
    createCsvFile(VALID_SIZE);
    flowRunner("csv-validation-test").run();
  }

  @Test
  public void validateInvalidCsvSize() throws Exception {
    expectedError.expectError(VALIDATION_NAMESPACE, ValidationErrorType.INVALID_SIZE, ValidationException.class,
                              "expected to have a size of at most 10 but it has 20");
    createCsvFile(INVALID_SIZE);
    flowRunner("csv-validation-test").run();
  }

  private void createCsvFile(int size) throws IOException {
    File csvFile = temporaryFolder.newFile(CSV_FILE_NAME);
    OutputStream csvFileOutputStream = new FileOutputStream(csvFile, false);
    String csvContent = createCsvContent(size);
    csvFileOutputStream.write(csvContent.getBytes());
    csvFileOutputStream.close();
  }

  private String createCsvContent(int size) {
    StringBuilder stringBuilder = new StringBuilder();
    writeHeader(stringBuilder);
    writeRows(stringBuilder, size);
    return stringBuilder.toString();
  }

  private void writeRows(StringBuilder stringBuilder, int size) {
    for (int i = 0; i < size; i++) {
      stringBuilder.append(CSV_ROW);
    }
  }

  private void writeHeader(StringBuilder stringBuilder) {
    stringBuilder.append(CSV_HEADERS);
  }

}
