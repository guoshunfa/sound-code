package java.security;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DigestInputStream extends FilterInputStream {
   private boolean on = true;
   protected MessageDigest digest;

   public DigestInputStream(InputStream var1, MessageDigest var2) {
      super(var1);
      this.setMessageDigest(var2);
   }

   public MessageDigest getMessageDigest() {
      return this.digest;
   }

   public void setMessageDigest(MessageDigest var1) {
      this.digest = var1;
   }

   public int read() throws IOException {
      int var1 = this.in.read();
      if (this.on && var1 != -1) {
         this.digest.update((byte)var1);
      }

      return var1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.in.read(var1, var2, var3);
      if (this.on && var4 != -1) {
         this.digest.update(var1, var2, var4);
      }

      return var4;
   }

   public void on(boolean var1) {
      this.on = var1;
   }

   public String toString() {
      return "[Digest Input Stream] " + this.digest.toString();
   }
}
