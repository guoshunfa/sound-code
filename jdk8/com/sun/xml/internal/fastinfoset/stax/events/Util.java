package com.sun.xml.internal.fastinfoset.stax.events;

public class Util {
   public static boolean isEmptyString(String s) {
      return s == null || s.equals("");
   }

   public static final String getEventTypeString(int eventType) {
      switch(eventType) {
      case 1:
         return "START_ELEMENT";
      case 2:
         return "END_ELEMENT";
      case 3:
         return "PROCESSING_INSTRUCTION";
      case 4:
         return "CHARACTERS";
      case 5:
         return "COMMENT";
      case 6:
      default:
         return "UNKNOWN_EVENT_TYPE";
      case 7:
         return "START_DOCUMENT";
      case 8:
         return "END_DOCUMENT";
      case 9:
         return "ENTITY_REFERENCE";
      case 10:
         return "ATTRIBUTE";
      case 11:
         return "DTD";
      case 12:
         return "CDATA";
      }
   }
}
