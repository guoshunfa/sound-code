package java.security;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DigestOutputStream extends FilterOutputStream {
   private boolean on = true;
   protected MessageDigest digest;

   public DigestOutputStream(OutputStream var1, MessageDigest var2) {
      super(var1);
      this.setMessageDigest(var2);
   }

   public MessageDigest getMessageDigest() {
      return this.digest;
   }

   public void setMessageDigest(MessageDigest var1) {
      this.digest = var1;
   }

   public void write(int var1) throws IOException {
      this.out.write(var1);
      if (this.on) {
         this.digest.update((byte)var1);
      }

   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      if (this.on) {
         this.digest.update(var1, var2, var3);
      }

   }

   public void on(boolean var1) {
      this.on = var1;
   }

   public String toString() {
      return "[Digest Output Stream] " + this.digest.toString();
   }
}
