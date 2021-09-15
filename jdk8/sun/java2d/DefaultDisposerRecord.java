package sun.java2d;

public class DefaultDisposerRecord implements DisposerRecord {
   private long dataPointer;
   private long disposerMethodPointer;

   public DefaultDisposerRecord(long var1, long var3) {
      this.disposerMethodPointer = var1;
      this.dataPointer = var3;
   }

   public void dispose() {
      invokeNativeDispose(this.disposerMethodPointer, this.dataPointer);
   }

   public long getDataPointer() {
      return this.dataPointer;
   }

   public long getDisposerMethodPointer() {
      return this.disposerMethodPointer;
   }

   public static native void invokeNativeDispose(long var0, long var2);
}
