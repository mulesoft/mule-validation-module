/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation.api;

import static org.mule.runtime.core.api.util.StringUtils.EMPTY;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.util.Objects;

/**
 * A simple object to configure internationalization.
 *
 * @since 1.0
 */
@Alias("i18n")
@TypeDsl(allowTopLevelDefinition = true)
public class I18NConfig {

  /**
   * The path to a bundle file containing the messages. If {@code null} then the platform will choose a default one
   */
  @Parameter
  private String bundlePath;

  /**
   * The locale of the {@link #bundlePath}. If {@code null} the platform will choose the system default
   */
  @Parameter
  @Optional(defaultValue = EMPTY)
  private String locale;

  public String getBundlePath() {
    return bundlePath;
  }

  public String getLocale() {
    return locale;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    I18NConfig that = (I18NConfig) o;
    return Objects.equals(bundlePath, that.bundlePath) &&
        Objects.equals(locale, that.locale);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundlePath, locale);
  }
}
