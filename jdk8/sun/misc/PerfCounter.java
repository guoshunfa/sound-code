package sun.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class PerfCounter {
   private static final Perf perf = (Perf)AccessController.doPrivileged((PrivilegedAction)(new Perf.GetPerfAction()));
   private static final int V_Constant = 1;
   private static final int V_Monotonic = 2;
   private static final int V_Variable = 3;
   private static final int U_None = 1;
   private final String name;
   private final LongBuffer lb;

   private PerfCounter(String var1, int var2) {
      this.name = var1;
      ByteBuffer var3 = perf.createLong(var1, var2, 1, 0L);
      var3.order(ByteOrder.nativeOrder());
      this.lb = var3.asLongBuffer();
   }

   static PerfCounter newPerfCounter(String var0) {
      return new PerfCounter(var0, 3);
   }

   static PerfCounter newConstantPerfCounter(String var0) {
      PerfCounter var1 = new PerfCounter(var0, 1);
      return var1;
   }

   public synchronized long get() {
      return this.lb.get(0);
   }

   public synchronized void set(long var1) {
      this.lb.put(0, var1);
   }

   public synchronized void add(long var1) {
      long var3 = this.get() + var1;
      this.lb.put(0, var3);
   }

   public void increment() {
      this.add(1L);
   }

   public void addTime(long var1) {
      this.add(var1);
   }

   public void addElapsedTimeFrom(long var1) {
      this.add(System.nanoTime() - var1);
   }

   public String toString() {
      return this.name + " = " + this.get();
   }

   public static PerfCounter getFindClasses() {
      return PerfCounter.CoreCounters.lc;
   }

   public static PerfCounter getFindClassTime() {
      return PerfCounter.CoreCounters.lct;
   }

   public static PerfCounter getReadClassBytesTime() {
      return PerfCounter.CoreCounters.rcbt;
   }

   public static PerfCounter getParentDelegationTime() {
      return PerfCounter.CoreCounters.pdt;
   }

   public static PerfCounter getZipFileCount() {
      return PerfCounter.CoreCounters.zfc;
   }

   public static PerfCounter getZipFileOpenTime() {
      return PerfCounter.CoreCounters.zfot;
   }

   public static PerfCounter getD3DAvailable() {
      return PerfCounter.WindowsClientCounters.d3dAvailable;
   }

   static class WindowsClientCounters {
      static final PerfCounter d3dAvailable = PerfCounter.newConstantPerfCounter("sun.java2d.d3d.available");
   }

   static class CoreCounters {
      static final PerfCounter pdt = PerfCounter.newPerfCounter("sun.classloader.parentDelegationTime");
      static final PerfCounter lc = PerfCounter.newPerfCounter("sun.classloader.findClasses");
      static final PerfCounter lct = PerfCounter.newPerfCounter("sun.classloader.findClassTime");
      static final PerfCounter rcbt = PerfCounter.newPerfCounter("sun.urlClassLoader.readClassBytesTime");
      static final PerfCounter zfc = PerfCounter.newPerfCounter("sun.zip.zipFiles");
      static final PerfCounter zfot = PerfCounter.newPerfCounter("sun.zip.zipFile.openTime");
   }
}
