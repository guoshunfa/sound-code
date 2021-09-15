package org.jcp.xml.dsig.internal;

import java.io.ByteArrayOutputStream;
import java.security.Signature;
import java.security.SignatureException;

public class SignerOutputStream extends ByteArrayOutputStream {
   private final Signature sig;

   public SignerOutputStream(Signature var1) {
      this.sig = var1;
   }

   public void write(int var1) {
      super.write(var1);

      try {
         this.sig.update((byte)var1);
      } catch (SignatureException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void write(byte[] var1, int var2, int var3) {
      super.write(var1, var2, var3);

      try {
         this.sig.update(var1, var2, var3);
      } catch (SignatureException var5) {
         throw new RuntimeException(var5);
      }
   }
}
