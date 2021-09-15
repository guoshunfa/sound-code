package com.sun.xml.internal.messaging.saaj.util;

import java.io.CharArrayWriter;

public class CharWriter extends CharArrayWriter {
   public CharWriter() {
   }

   public CharWriter(int size) {
      super(size);
   }

   public char[] getChars() {
      return this.buf;
   }

   public int getCount() {
      return this.count;
   }
}
