package org.jcp.xml.dsig.internal;

import java.io.ByteArrayOutputStream;
import javax.crypto.Mac;

public class MacOutputStream extends ByteArrayOutputStream {
   private final Mac mac;

   public MacOutputStream(Mac var1) {
      this.mac = var1;
   }

   public void write(int var1) {
      super.write(var1);
      this.mac.update((byte)var1);
   }

   public void write(byte[] var1, int var2, int var3) {
      super.write(var1, var2, var3);
      this.mac.update(var1, var2, var3);
   }
}
