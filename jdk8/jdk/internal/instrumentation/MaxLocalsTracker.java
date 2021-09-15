package jdk.internal.instrumentation;

import java.util.HashMap;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

final class MaxLocalsTracker extends ClassVisitor {
   private final HashMap<String, Integer> maxLocalsMap = new HashMap();

   public MaxLocalsTracker() {
      super(327680);
   }

   public MethodVisitor visitMethod(int var1, String var2, String var3, String var4, String[] var5) {
      return new MaxLocalsTracker.MaxLocalsMethodVisitor(key(var2, var3));
   }

   public int getMaxLocals(String var1, String var2) {
      Integer var3 = (Integer)this.maxLocalsMap.get(key(var1, var2));
      if (var3 == null) {
         throw new IllegalArgumentException("No maxLocals could be found for " + var1 + var2);
      } else {
         return var3;
      }
   }

   private static String key(String var0, String var1) {
      return var0 + var1;
   }

   private final class MaxLocalsMethodVisitor extends MethodVisitor {
      private String key;

      public MaxLocalsMethodVisitor(String var2) {
         super(327680);
         this.key = var2;
      }

      public void visitMaxs(int var1, int var2) {
         MaxLocalsTracker.this.maxLocalsMap.put(this.key, var2);
      }
   }
}
