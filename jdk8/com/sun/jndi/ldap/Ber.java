package com.sun.jndi.ldap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import sun.misc.HexDumpEncoder;

public abstract class Ber {
   protected byte[] buf;
   protected int offset;
   protected int bufsize;
   public static final int ASN_BOOLEAN = 1;
   public static final int ASN_INTEGER = 2;
   public static final int ASN_BIT_STRING = 3;
   public static final int ASN_SIMPLE_STRING = 4;
   public static final int ASN_OCTET_STR = 4;
   public static final int ASN_NULL = 5;
   public static final int ASN_OBJECT_ID = 6;
   public static final int ASN_SEQUENCE = 16;
   public static final int ASN_SET = 17;
   public static final int ASN_PRIMITIVE = 0;
   public static final int ASN_UNIVERSAL = 0;
   public static final int ASN_CONSTRUCTOR = 32;
   public static final int ASN_APPLICATION = 64;
   public static final int ASN_CONTEXT = 128;
   public static final int ASN_PRIVATE = 192;
   public static final int ASN_ENUMERATED = 10;

   protected Ber() {
   }

   public static void dumpBER(OutputStream var0, String var1, byte[] var2, int var3, int var4) {
      try {
         var0.write(10);
         var0.write(var1.getBytes("UTF8"));
         (new HexDumpEncoder()).encodeBuffer(new ByteArrayInputStream(var2, var3, var4), var0);
         var0.write(10);
      } catch (IOException var8) {
         try {
            var0.write("Ber.dumpBER(): error encountered\n".getBytes("UTF8"));
         } catch (IOException var7) {
         }
      }

   }

   static final class DecodeException extends IOException {
      private static final long serialVersionUID = 8735036969244425583L;

      DecodeException(String var1) {
         super(var1);
      }
   }

   static final class EncodeException extends IOException {
      private static final long serialVersionUID = -5247359637775781768L;

      EncodeException(String var1) {
         super(var1);
      }
   }
}
