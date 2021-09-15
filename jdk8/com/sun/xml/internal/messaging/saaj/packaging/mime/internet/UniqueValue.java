package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

class UniqueValue {
   private static int part = 0;

   public static String getUniqueBoundaryValue() {
      StringBuffer s = new StringBuffer();
      s.append("----=_Part_").append(part++).append("_").append(s.hashCode()).append('.').append(System.currentTimeMillis());
      return s.toString();
   }
}
