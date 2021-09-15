package com.sun.xml.internal.bind.v2.runtime.output;

import java.io.IOException;

public abstract class Pcdata implements CharSequence {
   public abstract void writeTo(UTF8XmlOutput var1) throws IOException;

   public void writeTo(char[] buf, int start) {
      this.toString().getChars(0, this.length(), buf, start);
   }

   public abstract String toString();
}
