package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.UnsupportedCharsetException;

public class CodeSetConversion {
   private static CodeSetConversion implementation;
   private static final int FALLBACK_CODESET = 0;
   private CodeSetCache cache;

   public CodeSetConversion.CTBConverter getCTBConverter(OSFCodeSetRegistry.Entry var1) {
      int var2 = !var1.isFixedWidth() ? 1 : var1.getMaxBytesPerChar();
      return new CodeSetConversion.JavaCTBConverter(var1, var2);
   }

   public CodeSetConversion.CTBConverter getCTBConverter(OSFCodeSetRegistry.Entry var1, boolean var2, boolean var3) {
      if (var1 == OSFCodeSetRegistry.UCS_2) {
         return new CodeSetConversion.UTF16CTBConverter(var2);
      } else if (var1 == OSFCodeSetRegistry.UTF_16) {
         return var3 ? new CodeSetConversion.UTF16CTBConverter() : new CodeSetConversion.UTF16CTBConverter(var2);
      } else {
         int var4 = !var1.isFixedWidth() ? 1 : var1.getMaxBytesPerChar();
         return new CodeSetConversion.JavaCTBConverter(var1, var4);
      }
   }

   public CodeSetConversion.BTCConverter getBTCConverter(OSFCodeSetRegistry.Entry var1) {
      return new CodeSetConversion.JavaBTCConverter(var1);
   }

   public CodeSetConversion.BTCConverter getBTCConverter(OSFCodeSetRegistry.Entry var1, boolean var2) {
      return (CodeSetConversion.BTCConverter)(var1 != OSFCodeSetRegistry.UTF_16 && var1 != OSFCodeSetRegistry.UCS_2 ? new CodeSetConversion.JavaBTCConverter(var1) : new CodeSetConversion.UTF16BTCConverter(var2));
   }

   private int selectEncoding(CodeSetComponentInfo.CodeSetComponent var1, CodeSetComponentInfo.CodeSetComponent var2) {
      int var3 = var2.nativeCodeSet;
      if (var3 == 0) {
         if (var2.conversionCodeSets.length <= 0) {
            return 0;
         }

         var3 = var2.conversionCodeSets[0];
      }

      if (var1.nativeCodeSet == var3) {
         return var3;
      } else {
         int var4;
         for(var4 = 0; var4 < var1.conversionCodeSets.length; ++var4) {
            if (var3 == var1.conversionCodeSets[var4]) {
               return var3;
            }
         }

         for(var4 = 0; var4 < var2.conversionCodeSets.length; ++var4) {
            if (var1.nativeCodeSet == var2.conversionCodeSets[var4]) {
               return var1.nativeCodeSet;
            }
         }

         for(var4 = 0; var4 < var2.conversionCodeSets.length; ++var4) {
            for(int var5 = 0; var5 < var1.conversionCodeSets.length; ++var5) {
               if (var2.conversionCodeSets[var4] == var1.conversionCodeSets[var5]) {
                  return var2.conversionCodeSets[var4];
               }
            }
         }

         return 0;
      }
   }

   public CodeSetComponentInfo.CodeSetContext negotiate(CodeSetComponentInfo var1, CodeSetComponentInfo var2) {
      int var3 = this.selectEncoding(var1.getCharComponent(), var2.getCharComponent());
      if (var3 == 0) {
         var3 = OSFCodeSetRegistry.UTF_8.getNumber();
      }

      int var4 = this.selectEncoding(var1.getWCharComponent(), var2.getWCharComponent());
      if (var4 == 0) {
         var4 = OSFCodeSetRegistry.UTF_16.getNumber();
      }

      return new CodeSetComponentInfo.CodeSetContext(var3, var4);
   }

   private CodeSetConversion() {
      this.cache = new CodeSetCache();
   }

   public static final CodeSetConversion impl() {
      return CodeSetConversion.CodeSetConversionHolder.csc;
   }

   // $FF: synthetic method
   CodeSetConversion(Object var1) {
      this();
   }

   private static class CodeSetConversionHolder {
      static final CodeSetConversion csc = new CodeSetConversion();
   }

   private class UTF16BTCConverter extends CodeSetConversion.JavaBTCConverter {
      private boolean defaultToLittleEndian;
      private boolean converterUsesBOM = true;
      private static final char UTF16_BE_MARKER = '\ufeff';
      private static final char UTF16_LE_MARKER = '\ufffe';

      public UTF16BTCConverter(boolean var2) {
         super(OSFCodeSetRegistry.UTF_16);
         this.defaultToLittleEndian = var2;
      }

      public char[] getChars(byte[] var1, int var2, int var3) {
         if (this.hasUTF16ByteOrderMarker(var1, var2, var3)) {
            if (!this.converterUsesBOM) {
               this.switchToConverter(OSFCodeSetRegistry.UTF_16);
            }

            this.converterUsesBOM = true;
            return super.getChars(var1, var2, var3);
         } else {
            if (this.converterUsesBOM) {
               if (this.defaultToLittleEndian) {
                  this.switchToConverter(OSFCodeSetRegistry.UTF_16LE);
               } else {
                  this.switchToConverter(OSFCodeSetRegistry.UTF_16BE);
               }

               this.converterUsesBOM = false;
            }

            return super.getChars(var1, var2, var3);
         }
      }

      private boolean hasUTF16ByteOrderMarker(byte[] var1, int var2, int var3) {
         if (var3 < 4) {
            return false;
         } else {
            int var4 = var1[var2] & 255;
            int var5 = var1[var2 + 1] & 255;
            char var6 = (char)(var4 << 8 | var5 << 0);
            return var6 == '\ufeff' || var6 == '\ufffe';
         }
      }

      private void switchToConverter(OSFCodeSetRegistry.Entry var1) {
         this.btc = super.getConverter(var1.getName());
      }
   }

   private class JavaBTCConverter extends CodeSetConversion.BTCConverter {
      private ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.encoding");
      private OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
      protected CharsetDecoder btc;
      private char[] buffer;
      private int resultingNumChars;
      private OSFCodeSetRegistry.Entry codeset;

      public JavaBTCConverter(OSFCodeSetRegistry.Entry var2) {
         this.btc = this.getConverter(var2.getName());
         this.codeset = var2;
      }

      public final boolean isFixedWidthEncoding() {
         return this.codeset.isFixedWidth();
      }

      public final int getFixedCharWidth() {
         return this.codeset.getMaxBytesPerChar();
      }

      public final int getNumChars() {
         return this.resultingNumChars;
      }

      public char[] getChars(byte[] var1, int var2, int var3) {
         try {
            ByteBuffer var4 = ByteBuffer.wrap(var1, var2, var3);
            CharBuffer var5 = this.btc.decode(var4);
            this.resultingNumChars = var5.limit();
            if (var5.limit() == var5.capacity()) {
               this.buffer = var5.array();
            } else {
               this.buffer = new char[var5.limit()];
               var5.get(this.buffer, 0, var5.limit()).position(0);
            }

            return this.buffer;
         } catch (IllegalStateException var6) {
            throw this.wrapper.btcConverterFailure((Throwable)var6);
         } catch (MalformedInputException var7) {
            throw this.wrapper.badUnicodePair((Throwable)var7);
         } catch (UnmappableCharacterException var8) {
            throw this.omgWrapper.charNotInCodeset((Throwable)var8);
         } catch (CharacterCodingException var9) {
            throw this.wrapper.btcConverterFailure((Throwable)var9);
         }
      }

      protected CharsetDecoder getConverter(String var1) {
         CharsetDecoder var2 = null;

         try {
            var2 = CodeSetConversion.this.cache.getByteToCharConverter(var1);
            if (var2 == null) {
               Charset var3 = Charset.forName(var1);
               var2 = var3.newDecoder();
               CodeSetConversion.this.cache.setConverter(var1, (CharsetDecoder)var2);
            }

            return var2;
         } catch (IllegalCharsetNameException var4) {
            throw this.wrapper.invalidBtcConverterName((Throwable)var4, var1);
         }
      }
   }

   private class UTF16CTBConverter extends CodeSetConversion.JavaCTBConverter {
      public UTF16CTBConverter() {
         super(OSFCodeSetRegistry.UTF_16, 2);
      }

      public UTF16CTBConverter(boolean var2) {
         super(var2 ? OSFCodeSetRegistry.UTF_16LE : OSFCodeSetRegistry.UTF_16BE, 2);
      }
   }

   private class JavaCTBConverter extends CodeSetConversion.CTBConverter {
      private ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.encoding");
      private OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
      private CharsetEncoder ctb;
      private int alignment;
      private char[] chars = null;
      private int numBytes = 0;
      private int numChars = 0;
      private ByteBuffer buffer;
      private OSFCodeSetRegistry.Entry codeset;

      public JavaCTBConverter(OSFCodeSetRegistry.Entry var2, int var3) {
         try {
            this.ctb = CodeSetConversion.this.cache.getCharToByteConverter(var2.getName());
            if (this.ctb == null) {
               Charset var4 = Charset.forName(var2.getName());
               this.ctb = var4.newEncoder();
               CodeSetConversion.this.cache.setConverter(var2.getName(), (CharsetEncoder)this.ctb);
            }
         } catch (IllegalCharsetNameException var5) {
            throw this.wrapper.invalidCtbConverterName((Throwable)var5, var2.getName());
         } catch (UnsupportedCharsetException var6) {
            throw this.wrapper.invalidCtbConverterName((Throwable)var6, var2.getName());
         }

         this.codeset = var2;
         this.alignment = var3;
      }

      public final float getMaxBytesPerChar() {
         return this.ctb.maxBytesPerChar();
      }

      public void convert(char var1) {
         if (this.chars == null) {
            this.chars = new char[1];
         }

         this.chars[0] = var1;
         this.numChars = 1;
         this.convertCharArray();
      }

      public void convert(String var1) {
         if (this.chars == null || this.chars.length < var1.length()) {
            this.chars = new char[var1.length()];
         }

         this.numChars = var1.length();
         var1.getChars(0, this.numChars, this.chars, 0);
         this.convertCharArray();
      }

      public final int getNumBytes() {
         return this.numBytes;
      }

      public final int getAlignment() {
         return this.alignment;
      }

      public final boolean isFixedWidthEncoding() {
         return this.codeset.isFixedWidth();
      }

      public byte[] getBytes() {
         return this.buffer.array();
      }

      private void convertCharArray() {
         try {
            this.buffer = this.ctb.encode(CharBuffer.wrap((char[])this.chars, 0, this.numChars));
            this.numBytes = this.buffer.limit();
         } catch (IllegalStateException var2) {
            throw this.wrapper.ctbConverterFailure((Throwable)var2);
         } catch (MalformedInputException var3) {
            throw this.wrapper.badUnicodePair((Throwable)var3);
         } catch (UnmappableCharacterException var4) {
            throw this.omgWrapper.charNotInCodeset((Throwable)var4);
         } catch (CharacterCodingException var5) {
            throw this.wrapper.ctbConverterFailure((Throwable)var5);
         }
      }
   }

   public abstract static class BTCConverter {
      public abstract boolean isFixedWidthEncoding();

      public abstract int getFixedCharWidth();

      public abstract int getNumChars();

      public abstract char[] getChars(byte[] var1, int var2, int var3);
   }

   public abstract static class CTBConverter {
      public abstract void convert(char var1);

      public abstract void convert(String var1);

      public abstract int getNumBytes();

      public abstract float getMaxBytesPerChar();

      public abstract boolean isFixedWidthEncoding();

      public abstract int getAlignment();

      public abstract byte[] getBytes();
   }
}
