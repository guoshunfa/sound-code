package java.util.zip;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import sun.nio.cs.ArrayDecoder;
import sun.nio.cs.ArrayEncoder;

final class ZipCoder {
   private Charset cs;
   private CharsetDecoder dec;
   private CharsetEncoder enc;
   private boolean isUTF8;
   private ZipCoder utf8;

   String toString(byte[] var1, int var2) {
      CharsetDecoder var3 = this.decoder().reset();
      int var4 = (int)((float)var2 * var3.maxCharsPerByte());
      char[] var5 = new char[var4];
      if (var4 == 0) {
         return new String(var5);
      } else if (this.isUTF8 && var3 instanceof ArrayDecoder) {
         int var9 = ((ArrayDecoder)var3).decode(var1, 0, var2, var5);
         if (var9 == -1) {
            throw new IllegalArgumentException("MALFORMED");
         } else {
            return new String(var5, 0, var9);
         }
      } else {
         ByteBuffer var6 = ByteBuffer.wrap(var1, 0, var2);
         CharBuffer var7 = CharBuffer.wrap(var5);
         CoderResult var8 = var3.decode(var6, var7, true);
         if (!var8.isUnderflow()) {
            throw new IllegalArgumentException(var8.toString());
         } else {
            var8 = var3.flush(var7);
            if (!var8.isUnderflow()) {
               throw new IllegalArgumentException(var8.toString());
            } else {
               return new String(var5, 0, var7.position());
            }
         }
      }
   }

   String toString(byte[] var1) {
      return this.toString(var1, var1.length);
   }

   byte[] getBytes(String var1) {
      CharsetEncoder var2 = this.encoder().reset();
      char[] var3 = var1.toCharArray();
      int var4 = (int)((float)var3.length * var2.maxBytesPerChar());
      byte[] var5 = new byte[var4];
      if (var4 == 0) {
         return var5;
      } else if (this.isUTF8 && var2 instanceof ArrayEncoder) {
         int var9 = ((ArrayEncoder)var2).encode(var3, 0, var3.length, var5);
         if (var9 == -1) {
            throw new IllegalArgumentException("MALFORMED");
         } else {
            return Arrays.copyOf(var5, var9);
         }
      } else {
         ByteBuffer var6 = ByteBuffer.wrap(var5);
         CharBuffer var7 = CharBuffer.wrap(var3);
         CoderResult var8 = var2.encode(var7, var6, true);
         if (!var8.isUnderflow()) {
            throw new IllegalArgumentException(var8.toString());
         } else {
            var8 = var2.flush(var6);
            if (!var8.isUnderflow()) {
               throw new IllegalArgumentException(var8.toString());
            } else {
               return var6.position() == var5.length ? var5 : Arrays.copyOf(var5, var6.position());
            }
         }
      }
   }

   byte[] getBytesUTF8(String var1) {
      if (this.isUTF8) {
         return this.getBytes(var1);
      } else {
         if (this.utf8 == null) {
            this.utf8 = new ZipCoder(StandardCharsets.UTF_8);
         }

         return this.utf8.getBytes(var1);
      }
   }

   String toStringUTF8(byte[] var1, int var2) {
      if (this.isUTF8) {
         return this.toString(var1, var2);
      } else {
         if (this.utf8 == null) {
            this.utf8 = new ZipCoder(StandardCharsets.UTF_8);
         }

         return this.utf8.toString(var1, var2);
      }
   }

   boolean isUTF8() {
      return this.isUTF8;
   }

   private ZipCoder(Charset var1) {
      this.cs = var1;
      this.isUTF8 = var1.name().equals(StandardCharsets.UTF_8.name());
   }

   static ZipCoder get(Charset var0) {
      return new ZipCoder(var0);
   }

   private CharsetDecoder decoder() {
      if (this.dec == null) {
         this.dec = this.cs.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
      }

      return this.dec;
   }

   private CharsetEncoder encoder() {
      if (this.enc == null) {
         this.enc = this.cs.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
      }

      return this.enc;
   }
}
