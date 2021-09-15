package java.lang;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import sun.misc.MessageUtils;
import sun.nio.cs.ArrayDecoder;
import sun.nio.cs.ArrayEncoder;
import sun.nio.cs.HistoricallyNamedCharset;

class StringCoding {
   private static final ThreadLocal<SoftReference<StringCoding.StringDecoder>> decoder = new ThreadLocal();
   private static final ThreadLocal<SoftReference<StringCoding.StringEncoder>> encoder = new ThreadLocal();
   private static boolean warnUnsupportedCharset = true;

   private StringCoding() {
   }

   private static <T> T deref(ThreadLocal<SoftReference<T>> var0) {
      SoftReference var1 = (SoftReference)var0.get();
      return var1 == null ? null : var1.get();
   }

   private static <T> void set(ThreadLocal<SoftReference<T>> var0, T var1) {
      var0.set(new SoftReference(var1));
   }

   private static byte[] safeTrim(byte[] var0, int var1, Charset var2, boolean var3) {
      return var1 != var0.length || !var3 && System.getSecurityManager() != null ? Arrays.copyOf(var0, var1) : var0;
   }

   private static char[] safeTrim(char[] var0, int var1, Charset var2, boolean var3) {
      return var1 != var0.length || !var3 && System.getSecurityManager() != null ? Arrays.copyOf(var0, var1) : var0;
   }

   private static int scale(int var0, float var1) {
      return (int)((double)var0 * (double)var1);
   }

   private static Charset lookupCharset(String var0) {
      if (Charset.isSupported(var0)) {
         try {
            return Charset.forName(var0);
         } catch (UnsupportedCharsetException var2) {
            throw new Error(var2);
         }
      } else {
         return null;
      }
   }

   private static void warnUnsupportedCharset(String var0) {
      if (warnUnsupportedCharset) {
         MessageUtils.err("WARNING: Default charset " + var0 + " not supported, using ISO-8859-1 instead");
         warnUnsupportedCharset = false;
      }

   }

   static char[] decode(String var0, byte[] var1, int var2, int var3) throws UnsupportedEncodingException {
      StringCoding.StringDecoder var4 = (StringCoding.StringDecoder)deref(decoder);
      String var5 = var0 == null ? "ISO-8859-1" : var0;
      if (var4 == null || !var5.equals(var4.requestedCharsetName()) && !var5.equals(var4.charsetName())) {
         var4 = null;

         try {
            Charset var6 = lookupCharset(var5);
            if (var6 != null) {
               var4 = new StringCoding.StringDecoder(var6, var5);
            }
         } catch (IllegalCharsetNameException var7) {
         }

         if (var4 == null) {
            throw new UnsupportedEncodingException(var5);
         }

         set(decoder, var4);
      }

      return var4.decode(var1, var2, var3);
   }

   static char[] decode(Charset var0, byte[] var1, int var2, int var3) {
      CharsetDecoder var4 = var0.newDecoder();
      int var5 = scale(var3, var4.maxCharsPerByte());
      char[] var6 = new char[var5];
      if (var3 == 0) {
         return var6;
      } else {
         boolean var7 = false;
         if (System.getSecurityManager() != null && !(var7 = var0.getClass().getClassLoader0() == null)) {
            var1 = Arrays.copyOfRange(var1, var2, var2 + var3);
            var2 = 0;
         }

         var4.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).reset();
         if (var4 instanceof ArrayDecoder) {
            int var12 = ((ArrayDecoder)var4).decode(var1, var2, var3, var6);
            return safeTrim(var6, var12, var0, var7);
         } else {
            ByteBuffer var8 = ByteBuffer.wrap(var1, var2, var3);
            CharBuffer var9 = CharBuffer.wrap(var6);

            try {
               CoderResult var10 = var4.decode(var8, var9, true);
               if (!var10.isUnderflow()) {
                  var10.throwException();
               }

               var10 = var4.flush(var9);
               if (!var10.isUnderflow()) {
                  var10.throwException();
               }
            } catch (CharacterCodingException var11) {
               throw new Error(var11);
            }

            return safeTrim(var6, var9.position(), var0, var7);
         }
      }
   }

   static char[] decode(byte[] var0, int var1, int var2) {
      String var3 = Charset.defaultCharset().name();

      try {
         return decode(var3, var0, var1, var2);
      } catch (UnsupportedEncodingException var6) {
         warnUnsupportedCharset(var3);

         try {
            return decode("ISO-8859-1", var0, var1, var2);
         } catch (UnsupportedEncodingException var5) {
            MessageUtils.err("ISO-8859-1 charset not available: " + var5.toString());
            System.exit(1);
            return null;
         }
      }
   }

   static byte[] encode(String var0, char[] var1, int var2, int var3) throws UnsupportedEncodingException {
      StringCoding.StringEncoder var4 = (StringCoding.StringEncoder)deref(encoder);
      String var5 = var0 == null ? "ISO-8859-1" : var0;
      if (var4 == null || !var5.equals(var4.requestedCharsetName()) && !var5.equals(var4.charsetName())) {
         var4 = null;

         try {
            Charset var6 = lookupCharset(var5);
            if (var6 != null) {
               var4 = new StringCoding.StringEncoder(var6, var5);
            }
         } catch (IllegalCharsetNameException var7) {
         }

         if (var4 == null) {
            throw new UnsupportedEncodingException(var5);
         }

         set(encoder, var4);
      }

      return var4.encode(var1, var2, var3);
   }

   static byte[] encode(Charset var0, char[] var1, int var2, int var3) {
      CharsetEncoder var4 = var0.newEncoder();
      int var5 = scale(var3, var4.maxBytesPerChar());
      byte[] var6 = new byte[var5];
      if (var3 == 0) {
         return var6;
      } else {
         boolean var7 = false;
         if (System.getSecurityManager() != null && !(var7 = var0.getClass().getClassLoader0() == null)) {
            var1 = Arrays.copyOfRange(var1, var2, var2 + var3);
            var2 = 0;
         }

         var4.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).reset();
         if (var4 instanceof ArrayEncoder) {
            int var12 = ((ArrayEncoder)var4).encode(var1, var2, var3, var6);
            return safeTrim(var6, var12, var0, var7);
         } else {
            ByteBuffer var8 = ByteBuffer.wrap(var6);
            CharBuffer var9 = CharBuffer.wrap(var1, var2, var3);

            try {
               CoderResult var10 = var4.encode(var9, var8, true);
               if (!var10.isUnderflow()) {
                  var10.throwException();
               }

               var10 = var4.flush(var8);
               if (!var10.isUnderflow()) {
                  var10.throwException();
               }
            } catch (CharacterCodingException var11) {
               throw new Error(var11);
            }

            return safeTrim(var6, var8.position(), var0, var7);
         }
      }
   }

   static byte[] encode(char[] var0, int var1, int var2) {
      String var3 = Charset.defaultCharset().name();

      try {
         return encode(var3, var0, var1, var2);
      } catch (UnsupportedEncodingException var6) {
         warnUnsupportedCharset(var3);

         try {
            return encode("ISO-8859-1", var0, var1, var2);
         } catch (UnsupportedEncodingException var5) {
            MessageUtils.err("ISO-8859-1 charset not available: " + var5.toString());
            System.exit(1);
            return null;
         }
      }
   }

   private static class StringEncoder {
      private Charset cs;
      private CharsetEncoder ce;
      private final String requestedCharsetName;
      private final boolean isTrusted;

      private StringEncoder(Charset var1, String var2) {
         this.requestedCharsetName = var2;
         this.cs = var1;
         this.ce = var1.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
         this.isTrusted = var1.getClass().getClassLoader0() == null;
      }

      String charsetName() {
         return this.cs instanceof HistoricallyNamedCharset ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name();
      }

      final String requestedCharsetName() {
         return this.requestedCharsetName;
      }

      byte[] encode(char[] var1, int var2, int var3) {
         int var4 = StringCoding.scale(var3, this.ce.maxBytesPerChar());
         byte[] var5 = new byte[var4];
         if (var3 == 0) {
            return var5;
         } else if (this.ce instanceof ArrayEncoder) {
            int var10 = ((ArrayEncoder)this.ce).encode(var1, var2, var3, var5);
            return StringCoding.safeTrim(var5, var10, this.cs, this.isTrusted);
         } else {
            this.ce.reset();
            ByteBuffer var6 = ByteBuffer.wrap(var5);
            CharBuffer var7 = CharBuffer.wrap(var1, var2, var3);

            try {
               CoderResult var8 = this.ce.encode(var7, var6, true);
               if (!var8.isUnderflow()) {
                  var8.throwException();
               }

               var8 = this.ce.flush(var6);
               if (!var8.isUnderflow()) {
                  var8.throwException();
               }
            } catch (CharacterCodingException var9) {
               throw new Error(var9);
            }

            return StringCoding.safeTrim(var5, var6.position(), this.cs, this.isTrusted);
         }
      }

      // $FF: synthetic method
      StringEncoder(Charset var1, String var2, Object var3) {
         this(var1, var2);
      }
   }

   private static class StringDecoder {
      private final String requestedCharsetName;
      private final Charset cs;
      private final CharsetDecoder cd;
      private final boolean isTrusted;

      private StringDecoder(Charset var1, String var2) {
         this.requestedCharsetName = var2;
         this.cs = var1;
         this.cd = var1.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
         this.isTrusted = var1.getClass().getClassLoader0() == null;
      }

      String charsetName() {
         return this.cs instanceof HistoricallyNamedCharset ? ((HistoricallyNamedCharset)this.cs).historicalName() : this.cs.name();
      }

      final String requestedCharsetName() {
         return this.requestedCharsetName;
      }

      char[] decode(byte[] var1, int var2, int var3) {
         int var4 = StringCoding.scale(var3, this.cd.maxCharsPerByte());
         char[] var5 = new char[var4];
         if (var3 == 0) {
            return var5;
         } else if (this.cd instanceof ArrayDecoder) {
            int var10 = ((ArrayDecoder)this.cd).decode(var1, var2, var3, var5);
            return StringCoding.safeTrim(var5, var10, this.cs, this.isTrusted);
         } else {
            this.cd.reset();
            ByteBuffer var6 = ByteBuffer.wrap(var1, var2, var3);
            CharBuffer var7 = CharBuffer.wrap(var5);

            try {
               CoderResult var8 = this.cd.decode(var6, var7, true);
               if (!var8.isUnderflow()) {
                  var8.throwException();
               }

               var8 = this.cd.flush(var7);
               if (!var8.isUnderflow()) {
                  var8.throwException();
               }
            } catch (CharacterCodingException var9) {
               throw new Error(var9);
            }

            return StringCoding.safeTrim(var5, var7.position(), this.cs, this.isTrusted);
         }
      }

      // $FF: synthetic method
      StringDecoder(Charset var1, String var2, Object var3) {
         this(var1, var2);
      }
   }
}
