package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignerOutputStream extends ByteArrayOutputStream {
   private static Logger log = Logger.getLogger(SignerOutputStream.class.getName());
   final SignatureAlgorithm sa;

   public SignerOutputStream(SignatureAlgorithm var1) {
      this.sa = var1;
   }

   public void write(byte[] var1) {
      try {
         this.sa.update(var1);
      } catch (XMLSignatureException var3) {
         throw new RuntimeException("" + var3);
      }
   }

   public void write(int var1) {
      try {
         this.sa.update((byte)var1);
      } catch (XMLSignatureException var3) {
         throw new RuntimeException("" + var3);
      }
   }

   public void write(byte[] var1, int var2, int var3) {
      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Canonicalized SignedInfo:");
         StringBuilder var4 = new StringBuilder(var3);

         for(int var5 = var2; var5 < var2 + var3; ++var5) {
            var4.append((char)var1[var5]);
         }

         log.log(Level.FINE, var4.toString());
      }

      try {
         this.sa.update(var1, var2, var3);
      } catch (XMLSignatureException var6) {
         throw new RuntimeException("" + var6);
      }
   }
}
