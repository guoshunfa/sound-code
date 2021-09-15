package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public class NoEscapeHandler implements CharacterEscapeHandler {
   public static final NoEscapeHandler theInstance = new NoEscapeHandler();

   public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
      out.write(ch, start, length);
   }
}
