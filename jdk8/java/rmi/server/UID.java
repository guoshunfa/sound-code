package java.rmi.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;

public final class UID implements Serializable {
   private static int hostUnique;
   private static boolean hostUniqueSet = false;
   private static final Object lock = new Object();
   private static long lastTime = System.currentTimeMillis();
   private static short lastCount = -32768;
   private static final long serialVersionUID = 1086053664494604050L;
   private final int unique;
   private final long time;
   private final short count;

   public UID() {
      synchronized(lock) {
         if (!hostUniqueSet) {
            hostUnique = (new SecureRandom()).nextInt();
            hostUniqueSet = true;
         }

         this.unique = hostUnique;
         if (lastCount == 32767) {
            boolean var2 = Thread.interrupted();
            boolean var3 = false;

            while(!var3) {
               long var4 = System.currentTimeMillis();
               if (var4 == lastTime) {
                  try {
                     Thread.sleep(1L);
                  } catch (InterruptedException var8) {
                     var2 = true;
                  }
               } else {
                  lastTime = var4 < lastTime ? lastTime + 1L : var4;
                  lastCount = -32768;
                  var3 = true;
               }
            }

            if (var2) {
               Thread.currentThread().interrupt();
            }
         }

         this.time = lastTime;
         short var10001 = lastCount;
         lastCount = (short)(var10001 + 1);
         this.count = var10001;
      }
   }

   public UID(short var1) {
      this.unique = 0;
      this.time = 0L;
      this.count = var1;
   }

   private UID(int var1, long var2, short var4) {
      this.unique = var1;
      this.time = var2;
      this.count = var4;
   }

   public int hashCode() {
      return (int)this.time + this.count;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof UID)) {
         return false;
      } else {
         UID var2 = (UID)var1;
         return this.unique == var2.unique && this.count == var2.count && this.time == var2.time;
      }
   }

   public String toString() {
      return Integer.toString(this.unique, 16) + ":" + Long.toString(this.time, 16) + ":" + Integer.toString(this.count, 16);
   }

   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.unique);
      var1.writeLong(this.time);
      var1.writeShort(this.count);
   }

   public static UID read(DataInput var0) throws IOException {
      int var1 = var0.readInt();
      long var2 = var0.readLong();
      short var4 = var0.readShort();
      return new UID(var1, var2, var4);
   }
}
