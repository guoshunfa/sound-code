package com.sun.xml.internal.ws.client;

public enum ContentNegotiation {
   none,
   pessimistic,
   optimistic;

   public static final String PROPERTY = "com.sun.xml.internal.ws.client.ContentNegotiation";

   public static ContentNegotiation obtainFromSystemProperty() {
      try {
         String value = System.getProperty("com.sun.xml.internal.ws.client.ContentNegotiation");
         return value == null ? none : valueOf(value);
      } catch (Exception var1) {
         return none;
      }
   }
}
