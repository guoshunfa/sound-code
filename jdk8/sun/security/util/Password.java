package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import sun.misc.SharedSecrets;

public class Password {
   private static volatile CharsetEncoder enc;

   public static char[] readPassword(InputStream var0) throws IOException {
      return readPassword(var0, false);
   }

   public static char[] readPassword(InputStream var0, boolean var1) throws IOException {
      char[] var2 = null;
      byte[] var3 = null;

      try {
         Console var4 = null;
         if (!var1 && var0 == System.in && (var4 = System.console()) != null) {
            var2 = var4.readPassword();
            if (var2 != null && var2.length == 0) {
               Object var17 = null;
               return (char[])var17;
            }

            var3 = convertToBytes(var2);
            var0 = new ByteArrayInputStream(var3);
         }

         char[] var5;
         char[] var6 = var5 = new char[128];
         int var8 = var6.length;
         int var9 = 0;
         boolean var11 = false;

         while(!var11) {
            int var10;
            switch(var10 = ((InputStream)var0).read()) {
            case -1:
            case 10:
               var11 = true;
               break;
            case 13:
               int var12 = ((InputStream)var0).read();
               if (var12 == 10 || var12 == -1) {
                  var11 = true;
                  break;
               } else {
                  if (!(var0 instanceof PushbackInputStream)) {
                     var0 = new PushbackInputStream((InputStream)var0);
                  }

                  ((PushbackInputStream)var0).unread(var12);
               }
            default:
               --var8;
               if (var8 < 0) {
                  var6 = new char[var9 + 128];
                  var8 = var6.length - var9 - 1;
                  System.arraycopy(var5, 0, var6, 0, var9);
                  Arrays.fill(var5, ' ');
                  var5 = var6;
               }

               var6[var9++] = (char)var10;
            }
         }

         if (var9 == 0) {
            Object var19 = null;
            return (char[])var19;
         } else {
            char[] var18 = new char[var9];
            System.arraycopy(var6, 0, var18, 0, var9);
            Arrays.fill(var6, ' ');
            char[] var13 = var18;
            return var13;
         }
      } finally {
         if (var2 != null) {
            Arrays.fill(var2, ' ');
         }

         if (var3 != null) {
            Arrays.fill((byte[])var3, (byte)0);
         }

      }
   }

   private static byte[] convertToBytes(char[] var0) {
      if (enc == null) {
         Class var1 = Password.class;
         synchronized(Password.class) {
            enc = SharedSecrets.getJavaIOAccess().charset().newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
         }
      }

      byte[] var7 = new byte[(int)(enc.maxBytesPerChar() * (float)var0.length)];
      ByteBuffer var2 = ByteBuffer.wrap(var7);
      synchronized(enc) {
         enc.reset().encode(CharBuffer.wrap(var0), var2, true);
      }

      if (var2.position() < var7.length) {
         var7[var2.position()] = 10;
      }

      return var7;
   }
}
