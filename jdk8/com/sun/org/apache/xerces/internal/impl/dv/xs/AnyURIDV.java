package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.URI;
import java.io.UnsupportedEncodingException;

public class AnyURIDV extends TypeValidator {
   private static final URI BASE_URI;
   private static boolean[] gNeedEscaping;
   private static char[] gAfterEscaping1;
   private static char[] gAfterEscaping2;
   private static char[] gHexChs;

   public short getAllowedFacets() {
      return 2079;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      try {
         if (content.length() != 0) {
            String encoded = encode(content);
            new URI(BASE_URI, encoded);
         }

         return content;
      } catch (URI.MalformedURIException var4) {
         throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{content, "anyURI"});
      }
   }

   private static String encode(String anyURI) {
      int len = anyURI.length();
      StringBuffer buffer = new StringBuffer(len * 3);

      int i;
      for(i = 0; i < len; ++i) {
         int ch = anyURI.charAt(i);
         if (ch >= 128) {
            break;
         }

         if (gNeedEscaping[ch]) {
            buffer.append('%');
            buffer.append(gAfterEscaping1[ch]);
            buffer.append(gAfterEscaping2[ch]);
         } else {
            buffer.append((char)ch);
         }
      }

      if (i < len) {
         Object var5 = null;

         byte[] bytes;
         try {
            bytes = anyURI.substring(i).getBytes("UTF-8");
         } catch (UnsupportedEncodingException var8) {
            return anyURI;
         }

         len = bytes.length;

         for(i = 0; i < len; ++i) {
            byte b = bytes[i];
            if (b < 0) {
               int ch = b + 256;
               buffer.append('%');
               buffer.append(gHexChs[ch >> 4]);
               buffer.append(gHexChs[ch & 15]);
            } else if (gNeedEscaping[b]) {
               buffer.append('%');
               buffer.append(gAfterEscaping1[b]);
               buffer.append(gAfterEscaping2[b]);
            } else {
               buffer.append((char)b);
            }
         }
      }

      return buffer.length() != len ? buffer.toString() : anyURI;
   }

   static {
      URI uri = null;

      try {
         uri = new URI("abc://def.ghi.jkl");
      } catch (URI.MalformedURIException var4) {
      }

      BASE_URI = uri;
      gNeedEscaping = new boolean[128];
      gAfterEscaping1 = new char[128];
      gAfterEscaping2 = new char[128];
      gHexChs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

      for(int i = 0; i <= 31; ++i) {
         gNeedEscaping[i] = true;
         gAfterEscaping1[i] = gHexChs[i >> 4];
         gAfterEscaping2[i] = gHexChs[i & 15];
      }

      gNeedEscaping[127] = true;
      gAfterEscaping1[127] = '7';
      gAfterEscaping2[127] = 'F';
      char[] escChs = new char[]{' ', '<', '>', '"', '{', '}', '|', '\\', '^', '~', '`'};
      int len = escChs.length;

      for(int i = 0; i < len; ++i) {
         char ch = escChs[i];
         gNeedEscaping[ch] = true;
         gAfterEscaping1[ch] = gHexChs[ch >> 4];
         gAfterEscaping2[ch] = gHexChs[ch & 15];
      }

   }
}
