package java.net;

import java.io.CharArrayWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.BitSet;
import sun.security.action.GetPropertyAction;

public class URLEncoder {
   static BitSet dontNeedEncoding = new BitSet(256);
   static final int caseDiff = 32;
   static String dfltEncName = null;

   private URLEncoder() {
   }

   /** @deprecated */
   @Deprecated
   public static String encode(String var0) {
      String var1 = null;

      try {
         var1 = encode(var0, dfltEncName);
      } catch (UnsupportedEncodingException var3) {
      }

      return var1;
   }

   public static String encode(String var0, String var1) throws UnsupportedEncodingException {
      boolean var2 = false;
      StringBuffer var3 = new StringBuffer(var0.length());
      CharArrayWriter var5 = new CharArrayWriter();
      if (var1 == null) {
         throw new NullPointerException("charsetName");
      } else {
         Charset var4;
         try {
            var4 = Charset.forName(var1);
         } catch (IllegalCharsetNameException var12) {
            throw new UnsupportedEncodingException(var1);
         } catch (UnsupportedCharsetException var13) {
            throw new UnsupportedEncodingException(var1);
         }

         int var6 = 0;

         while(true) {
            while(var6 < var0.length()) {
               char var7 = var0.charAt(var6);
               if (dontNeedEncoding.get(var7)) {
                  if (var7 == ' ') {
                     var7 = '+';
                     var2 = true;
                  }

                  var3.append((char)var7);
                  ++var6;
               } else {
                  do {
                     var5.write(var7);
                     if (var7 >= '\ud800' && var7 <= '\udbff' && var6 + 1 < var0.length()) {
                        char var8 = var0.charAt(var6 + 1);
                        if (var8 >= '\udc00' && var8 <= '\udfff') {
                           var5.write(var8);
                           ++var6;
                        }
                     }

                     ++var6;
                  } while(var6 < var0.length() && !dontNeedEncoding.get(var7 = var0.charAt(var6)));

                  var5.flush();
                  String var14 = new String(var5.toCharArray());
                  byte[] var9 = var14.getBytes(var4);

                  for(int var10 = 0; var10 < var9.length; ++var10) {
                     var3.append('%');
                     char var11 = Character.forDigit(var9[var10] >> 4 & 15, 16);
                     if (Character.isLetter(var11)) {
                        var11 = (char)(var11 - 32);
                     }

                     var3.append(var11);
                     var11 = Character.forDigit(var9[var10] & 15, 16);
                     if (Character.isLetter(var11)) {
                        var11 = (char)(var11 - 32);
                     }

                     var3.append(var11);
                  }

                  var5.reset();
                  var2 = true;
               }
            }

            return var2 ? var3.toString() : var0;
         }
      }
   }

   static {
      int var0;
      for(var0 = 97; var0 <= 122; ++var0) {
         dontNeedEncoding.set(var0);
      }

      for(var0 = 65; var0 <= 90; ++var0) {
         dontNeedEncoding.set(var0);
      }

      for(var0 = 48; var0 <= 57; ++var0) {
         dontNeedEncoding.set(var0);
      }

      dontNeedEncoding.set(32);
      dontNeedEncoding.set(45);
      dontNeedEncoding.set(95);
      dontNeedEncoding.set(46);
      dontNeedEncoding.set(42);
      dfltEncName = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("file.encoding")));
   }
}
