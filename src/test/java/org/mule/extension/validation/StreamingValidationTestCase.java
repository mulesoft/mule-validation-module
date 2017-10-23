/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.validation;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mule.runtime.api.streaming.object.CursorIterator;
import org.mule.runtime.api.streaming.object.CursorIteratorProvider;

import java.util.Iterator;
import java.util.function.Supplier;

import org.junit.Test;
import org.mockito.stubbing.Answer;

public class StreamingValidationTestCase extends ValidationTestCase {

  @Override
  protected String getConfigFile() {
    return "streaming-validations.xml";
  }

  @Test
  public void sizeOfRegularIterator() throws Exception {
    Iterator<String> payload = asList("a").iterator();
    flowRunner("sizeOfRegularIteratorIsOne").withPayload(payload).run();
  }

  @Test
  public void sizeOfStreamingIteratorWithKnownSize() throws Exception {
    assertSizeOfStreamingIterator(() -> mockStreamingIterator(1));
  }

  @Test
  public void sizeOfStreamingIteratorWithUnknownSize() throws Exception {
    assertSizeOfStreamingIterator(() -> mockStreamingIterator(-1));
  }

  private void assertSizeOfStreamingIterator(Supplier<Iterator<String>> payloadSupplier) throws Exception {
    CursorIteratorProvider cursorProvider = mock(CursorIteratorProvider.class);
    when(cursorProvider.openCursor()).thenAnswer((Answer<Iterator<String>>) invocationOnMock -> payloadSupplier.get());
    flowRunner("sizeOfStreamingIteratorIsOne").withPayload(cursorProvider).run();
  }

  private CursorIterator<String> mockStreamingIterator(int size) {
    CursorIterator<String> iterator = mock(CursorIterator.class);
    when(iterator.getSize()).thenReturn(size);
    when(iterator.hasNext()).thenReturn(true).thenReturn(false);
    when(iterator.next()).thenReturn("a");

    return iterator;
  }

}
