package sun.security.krb5.internal;

import sun.security.krb5.Confounder;

public class LocalSeqNumber implements SeqNumber {
   private int lastSeqNumber;

   public LocalSeqNumber() {
      this.randInit();
   }

   public LocalSeqNumber(int var1) {
      this.init(var1);
   }

   public LocalSeqNumber(Integer var1) {
      this.init(var1);
   }

   public synchronized void randInit() {
      byte[] var1 = Confounder.bytes(4);
      var1[0] = (byte)(var1[0] & 63);
      int var2 = var1[3] & 255 | (var1[2] & 255) << 8 | (var1[1] & 255) << 16 | (var1[0] & 255) << 24;
      if (var2 == 0) {
         var2 = 1;
      }

      this.lastSeqNumber = var2;
   }

   public synchronized void init(int var1) {
      this.lastSeqNumber = var1;
   }

   public synchronized int current() {
      return this.lastSeqNumber;
   }

   public synchronized int next() {
      return this.lastSeqNumber + 1;
   }

   public synchronized int step() {
      return ++this.lastSeqNumber;
   }
}
