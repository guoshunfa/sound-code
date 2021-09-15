package java.lang.ref;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

final class FinalizerHistogram {
   static FinalizerHistogram.Entry[] getFinalizerHistogram() {
      HashMap var0 = new HashMap();
      ReferenceQueue var1 = Finalizer.getQueue();
      var1.forEach((var1x) -> {
         Object var2 = var1x.get();
         if (var2 != null) {
            ((FinalizerHistogram.Entry)var0.computeIfAbsent(var2.getClass().getName(), FinalizerHistogram.Entry::new)).increment();
            var2 = null;
         }

      });
      FinalizerHistogram.Entry[] var2 = (FinalizerHistogram.Entry[])var0.values().toArray(new FinalizerHistogram.Entry[var0.size()]);
      Arrays.sort(var2, Comparator.comparingInt(FinalizerHistogram.Entry::getInstanceCount).reversed());
      return var2;
   }

   private static final class Entry {
      private int instanceCount;
      private final String className;

      int getInstanceCount() {
         return this.instanceCount;
      }

      void increment() {
         ++this.instanceCount;
      }

      Entry(String var1) {
         this.className = var1;
      }
   }
}
