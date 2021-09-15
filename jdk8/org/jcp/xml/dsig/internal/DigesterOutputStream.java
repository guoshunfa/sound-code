package org.jcp.xml.dsig.internal;

import com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigesterOutputStream extends OutputStream {
   private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal");
   private final boolean buffer;
   private UnsyncByteArrayOutputStream bos;
   private final MessageDigest md;

   public DigesterOutputStream(MessageDigest var1) {
      this(var1, false);
   }

   public DigesterOutputStream(MessageDigest var1, boolean var2) {
      this.md = var1;
      this.buffer = var2;
      if (var2) {
         this.bos = new UnsyncByteArrayOutputStream();
      }

   }

   public void write(int var1) {
      if (this.buffer) {
         this.bos.write(var1);
      }

      this.md.update((byte)var1);
   }

   public void write(byte[] var1, int var2, int var3) {
      if (this.buffer) {
         this.bos.write(var1, var2, var3);
      }

      if (log.isLoggable(Level.FINE)) {
         log.log(Level.FINE, "Pre-digested input:");
         StringBuilder var4 = new StringBuilder(var3);

         for(int var5 = var2; var5 < var2 + var3; ++var5) {
            var4.append((char)var1[var5]);
         }

         log.log(Level.FINE, var4.toString());
      }

      this.md.update(var1, var2, var3);
   }

   public byte[] getDigestValue() {
      return this.md.digest();
   }

   public InputStream getInputStream() {
      return this.buffer ? new ByteArrayInputStream(this.bos.toByteArray()) : null;
   }

   public void close() throws IOException {
      if (this.buffer) {
         this.bos.close();
      }

   }
}
