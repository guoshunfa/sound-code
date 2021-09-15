package com.sun.xml.internal.bind.v2.schemagen;

public final class Util {
   private Util() {
   }

   public static String escapeURI(String s) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < s.length(); ++i) {
         char c = s.charAt(i);
         if (Character.isSpaceChar(c)) {
            sb.append("%20");
         } else {
            sb.append(c);
         }
      }

      return sb.toString();
   }

   public static String getParentUriPath(String uriPath) {
      int idx = uriPath.lastIndexOf(47);
      if (uriPath.endsWith("/")) {
         uriPath = uriPath.substring(0, idx);
         idx = uriPath.lastIndexOf(47);
      }

      return uriPath.substring(0, idx) + "/";
   }

   public static String normalizeUriPath(String uriPath) {
      if (uriPath.endsWith("/")) {
         return uriPath;
      } else {
         int idx = uriPath.lastIndexOf(47);
         return uriPath.substring(0, idx + 1);
      }
   }

   public static boolean equalsIgnoreCase(String s, String t) {
      if (s == t) {
         return true;
      } else {
         return s != null && t != null ? s.equalsIgnoreCase(t) : false;
      }
   }

   public static boolean equal(String s, String t) {
      if (s == t) {
         return true;
      } else {
         return s != null && t != null ? s.equals(t) : false;
      }
   }
}
