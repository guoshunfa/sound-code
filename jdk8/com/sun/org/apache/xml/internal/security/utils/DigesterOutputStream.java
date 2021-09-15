package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigesterOutputStream extends ByteArrayOutputStream {
   private static final Logger log = Logger.getLogger(DigesterOutputStream.class.getName());
   final MessageDigestAlgorithm mda;

   public DigesterOutputStream(MessageDigestAlgorithm var1) {
      this.mda = var1;
   }

   public void write(byte[] var1) {
      this.write(var1, 0, var1.length);
   }

   public void write(int var1) {
      this.mda.update((byte)var1);
   }

   public void write(byte[] var1, int var2, int var3) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Pre-digested input:");
         StringBuilder var4 = new StringBuilder(var3);

         for(int var5 = var2; var5 < var2 + var3; ++var5) {
            var4.append((char)var1[var5]);
         }

         log.log(Level.FINE, var4.toString());
      }

      this.mda.update(var1, var2, var3);
   }

   public byte[] getDigestValue() {
      return this.mda.digest();
   }
}
