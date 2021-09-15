package com.sun.org.apache.xml.internal.resolver.helpers;

public abstract class PublicId {
   protected PublicId() {
   }

   public static String normalize(String publicId) {
      String normal = publicId.replace('\t', ' ');
      normal = normal.replace('\r', ' ');
      normal = normal.replace('\n', ' ');

      int pos;
      for(normal = normal.trim(); (pos = normal.indexOf("  ")) >= 0; normal = normal.substring(0, pos) + normal.substring(pos + 1)) {
      }

      return normal;
   }

   public static String encodeURN(String publicId) {
      String urn = normalize(publicId);
      urn = stringReplace(urn, "%", "%25");
      urn = stringReplace(urn, ";", "%3B");
      urn = stringReplace(urn, "'", "%27");
      urn = stringReplace(urn, "?", "%3F");
      urn = stringReplace(urn, "#", "%23");
      urn = stringReplace(urn, "+", "%2B");
      urn = stringReplace(urn, " ", "+");
      urn = stringReplace(urn, "::", ";");
      urn = stringReplace(urn, ":", "%3A");
      urn = stringReplace(urn, "//", ":");
      urn = stringReplace(urn, "/", "%2F");
      return "urn:publicid:" + urn;
   }

   public static String decodeURN(String urn) {
      String publicId = "";
      if (urn.startsWith("urn:publicid:")) {
         publicId = urn.substring(13);
         publicId = stringReplace(publicId, "%2F", "/");
         publicId = stringReplace(publicId, ":", "//");
         publicId = stringReplace(publicId, "%3A", ":");
         publicId = stringReplace(publicId, ";", "::");
         publicId = stringReplace(publicId, "+", " ");
         publicId = stringReplace(publicId, "%2B", "+");
         publicId = stringReplace(publicId, "%23", "#");
         publicId = stringReplace(publicId, "%3F", "?");
         publicId = stringReplace(publicId, "%27", "'");
         publicId = stringReplace(publicId, "%3B", ";");
         publicId = stringReplace(publicId, "%25", "%");
         return publicId;
      } else {
         return urn;
      }
   }

   private static String stringReplace(String str, String oldStr, String newStr) {
      String result = "";

      for(int pos = str.indexOf(oldStr); pos >= 0; pos = str.indexOf(oldStr)) {
         result = result + str.substring(0, pos);
         result = result + newStr;
         str = str.substring(pos + 1);
      }

      return result + str;
   }
}
