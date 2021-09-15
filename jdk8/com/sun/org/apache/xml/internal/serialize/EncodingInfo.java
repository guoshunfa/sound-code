package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.util.EncodingMap;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class EncodingInfo {
   private Object[] fArgsForMethod = null;
   String ianaName;
   String javaName;
   int lastPrintable;
   Object fCharsetEncoder = null;
   Object fCharToByteConverter = null;
   boolean fHaveTriedCToB = false;
   boolean fHaveTriedCharsetEncoder = false;

   public EncodingInfo(String ianaName, String javaName, int lastPrintable) {
      this.ianaName = ianaName;
      this.javaName = EncodingMap.getIANA2JavaMapping(ianaName);
      this.lastPrintable = lastPrintable;
   }

   public String getIANAName() {
      return this.ianaName;
   }

   public Writer getWriter(OutputStream output) throws UnsupportedEncodingException {
      if (this.javaName != null) {
         return new OutputStreamWriter(output, this.javaName);
      } else {
         this.javaName = EncodingMap.getIANA2JavaMapping(this.ianaName);
         return this.javaName == null ? new OutputStreamWriter(output, "UTF8") : new OutputStreamWriter(output, this.javaName);
      }
   }

   public boolean isPrintable(char ch) {
      return ch <= this.lastPrintable ? true : this.isPrintable0(ch);
   }

   private boolean isPrintable0(char ch) {
      if (this.fCharsetEncoder == null && EncodingInfo.CharsetMethods.fgNIOCharsetAvailable && !this.fHaveTriedCharsetEncoder) {
         if (this.fArgsForMethod == null) {
            this.fArgsForMethod = new Object[1];
         }

         try {
            this.fArgsForMethod[0] = this.javaName;
            Object charset = EncodingInfo.CharsetMethods.fgCharsetForNameMethod.invoke((Object)null, this.fArgsForMethod);
            if ((Boolean)EncodingInfo.CharsetMethods.fgCharsetCanEncodeMethod.invoke(charset, (Object[])null)) {
               this.fCharsetEncoder = EncodingInfo.CharsetMethods.fgCharsetNewEncoderMethod.invoke(charset, (Object[])null);
            } else {
               this.fHaveTriedCharsetEncoder = true;
            }
         } catch (Exception var5) {
            this.fHaveTriedCharsetEncoder = true;
         }
      }

      if (this.fCharsetEncoder != null) {
         try {
            this.fArgsForMethod[0] = new Character(ch);
            return (Boolean)EncodingInfo.CharsetMethods.fgCharsetEncoderCanEncodeMethod.invoke(this.fCharsetEncoder, this.fArgsForMethod);
         } catch (Exception var6) {
            this.fCharsetEncoder = null;
            this.fHaveTriedCharsetEncoder = false;
         }
      }

      if (this.fCharToByteConverter == null) {
         if (this.fHaveTriedCToB || !EncodingInfo.CharToByteConverterMethods.fgConvertersAvailable) {
            return false;
         }

         if (this.fArgsForMethod == null) {
            this.fArgsForMethod = new Object[1];
         }

         try {
            this.fArgsForMethod[0] = this.javaName;
            this.fCharToByteConverter = EncodingInfo.CharToByteConverterMethods.fgGetConverterMethod.invoke((Object)null, this.fArgsForMethod);
         } catch (Exception var4) {
            this.fHaveTriedCToB = true;
            return false;
         }
      }

      try {
         this.fArgsForMethod[0] = new Character(ch);
         return (Boolean)EncodingInfo.CharToByteConverterMethods.fgCanConvertMethod.invoke(this.fCharToByteConverter, this.fArgsForMethod);
      } catch (Exception var3) {
         this.fCharToByteConverter = null;
         this.fHaveTriedCToB = false;
         return false;
      }
   }

   public static void testJavaEncodingName(String name) throws UnsupportedEncodingException {
      byte[] bTest = new byte[]{118, 97, 108, 105, 100};
      new String(bTest, name);
   }

   static class CharToByteConverterMethods {
      private static java.lang.reflect.Method fgGetConverterMethod = null;
      private static java.lang.reflect.Method fgCanConvertMethod = null;
      private static boolean fgConvertersAvailable = false;

      private CharToByteConverterMethods() {
      }

      static {
         try {
            Class clazz = Class.forName("sun.io.CharToByteConverter");
            fgGetConverterMethod = clazz.getMethod("getConverter", String.class);
            fgCanConvertMethod = clazz.getMethod("canConvert", Character.TYPE);
            fgConvertersAvailable = true;
         } catch (Exception var1) {
            fgGetConverterMethod = null;
            fgCanConvertMethod = null;
            fgConvertersAvailable = false;
         }

      }
   }

   static class CharsetMethods {
      private static java.lang.reflect.Method fgCharsetForNameMethod = null;
      private static java.lang.reflect.Method fgCharsetCanEncodeMethod = null;
      private static java.lang.reflect.Method fgCharsetNewEncoderMethod = null;
      private static java.lang.reflect.Method fgCharsetEncoderCanEncodeMethod = null;
      private static boolean fgNIOCharsetAvailable = false;

      private CharsetMethods() {
      }

      static {
         try {
            Class charsetClass = Class.forName("java.nio.charset.Charset");
            Class charsetEncoderClass = Class.forName("java.nio.charset.CharsetEncoder");
            fgCharsetForNameMethod = charsetClass.getMethod("forName", String.class);
            fgCharsetCanEncodeMethod = charsetClass.getMethod("canEncode");
            fgCharsetNewEncoderMethod = charsetClass.getMethod("newEncoder");
            fgCharsetEncoderCanEncodeMethod = charsetEncoderClass.getMethod("canEncode", Character.TYPE);
            fgNIOCharsetAvailable = true;
         } catch (Exception var2) {
            fgCharsetForNameMethod = null;
            fgCharsetCanEncodeMethod = null;
            fgCharsetEncoderCanEncodeMethod = null;
            fgCharsetNewEncoderMethod = null;
            fgNIOCharsetAvailable = false;
         }

      }
   }
}
