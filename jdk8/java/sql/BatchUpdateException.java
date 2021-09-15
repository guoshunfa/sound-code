package java.sql;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class BatchUpdateException extends SQLException {
   private int[] updateCounts;
   private long[] longUpdateCounts;
   private static final long serialVersionUID = 5977529877145521757L;

   public BatchUpdateException(String var1, String var2, int var3, int[] var4) {
      super(var1, var2, var3);
      this.updateCounts = var4 == null ? null : Arrays.copyOf(var4, var4.length);
      this.longUpdateCounts = var4 == null ? null : copyUpdateCount(var4);
   }

   public BatchUpdateException(String var1, String var2, int[] var3) {
      this(var1, var2, 0, var3);
   }

   public BatchUpdateException(String var1, int[] var2) {
      this(var1, (String)null, 0, var2);
   }

   public BatchUpdateException(int[] var1) {
      this((String)null, (String)null, 0, var1);
   }

   public BatchUpdateException() {
      this((String)null, (String)null, 0, (int[])null);
   }

   public BatchUpdateException(Throwable var1) {
      this(var1 == null ? null : var1.toString(), (String)null, 0, (int[])((int[])null), var1);
   }

   public BatchUpdateException(int[] var1, Throwable var2) {
      this(var2 == null ? null : var2.toString(), (String)null, 0, (int[])var1, var2);
   }

   public BatchUpdateException(String var1, int[] var2, Throwable var3) {
      this(var1, (String)null, 0, (int[])var2, var3);
   }

   public BatchUpdateException(String var1, String var2, int[] var3, Throwable var4) {
      this(var1, var2, 0, (int[])var3, var4);
   }

   public BatchUpdateException(String var1, String var2, int var3, int[] var4, Throwable var5) {
      super(var1, var2, var3, var5);
      this.updateCounts = var4 == null ? null : Arrays.copyOf(var4, var4.length);
      this.longUpdateCounts = var4 == null ? null : copyUpdateCount(var4);
   }

   public int[] getUpdateCounts() {
      return this.updateCounts == null ? null : Arrays.copyOf(this.updateCounts, this.updateCounts.length);
   }

   public BatchUpdateException(String var1, String var2, int var3, long[] var4, Throwable var5) {
      super(var1, var2, var3, var5);
      this.longUpdateCounts = var4 == null ? null : Arrays.copyOf(var4, var4.length);
      this.updateCounts = this.longUpdateCounts == null ? null : copyUpdateCount(this.longUpdateCounts);
   }

   public long[] getLargeUpdateCounts() {
      return this.longUpdateCounts == null ? null : Arrays.copyOf(this.longUpdateCounts, this.longUpdateCounts.length);
   }

   private static long[] copyUpdateCount(int[] var0) {
      long[] var1 = new long[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = (long)var0[var2];
      }

      return var1;
   }

   private static int[] copyUpdateCount(long[] var0) {
      int[] var1 = new int[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = (int)var0[var2];
      }

      return var1;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      int[] var3 = (int[])((int[])var2.get("updateCounts", (Object)null));
      long[] var4 = (long[])((long[])var2.get("longUpdateCounts", (Object)null));
      if (var3 != null && var4 != null && var3.length != var4.length) {
         throw new InvalidObjectException("update counts are not the expected size");
      } else {
         if (var3 != null) {
            this.updateCounts = (int[])var3.clone();
         }

         if (var4 != null) {
            this.longUpdateCounts = (long[])var4.clone();
         }

         if (this.updateCounts == null && this.longUpdateCounts != null) {
            this.updateCounts = copyUpdateCount(this.longUpdateCounts);
         }

         if (this.longUpdateCounts == null && this.updateCounts != null) {
            this.longUpdateCounts = copyUpdateCount(this.updateCounts);
         }

      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException, ClassNotFoundException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      var2.put("updateCounts", this.updateCounts);
      var2.put("longUpdateCounts", this.longUpdateCounts);
      var1.writeFields();
   }
}
