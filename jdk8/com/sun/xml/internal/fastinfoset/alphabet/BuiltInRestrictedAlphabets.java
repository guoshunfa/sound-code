package com.sun.xml.internal.fastinfoset.alphabet;

public final class BuiltInRestrictedAlphabets {
   public static final char[][] table = new char[2][];

   static {
      table[0] = "0123456789-+.E ".toCharArray();
      table[1] = "0123456789-:TZ ".toCharArray();
   }
}
