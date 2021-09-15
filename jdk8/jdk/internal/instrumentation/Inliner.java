package jdk.internal.instrumentation;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

final class Inliner extends ClassVisitor {
   private final String instrumentationClassName;
   private final Logger logger;
   private final ClassNode targetClassNode;
   private final List<Method> instrumentationMethods;
   private final MaxLocalsTracker maxLocalsTracker;

   Inliner(int var1, ClassVisitor var2, String var3, ClassReader var4, List<Method> var5, MaxLocalsTracker var6, Logger var7) {
      super(var1, var2);
      this.instrumentationClassName = var3;
      this.instrumentationMethods = var5;
      this.maxLocalsTracker = var6;
      this.logger = var7;
      ClassNode var8 = new ClassNode(327680);
      var4.accept(var8, 8);
      this.targetClassNode = var8;
   }

   public MethodVisitor visitMethod(int var1, String var2, String var3, String var4, String[] var5) {
      MethodVisitor var6 = super.visitMethod(var1, var2, var3, var4, var5);
      if (this.isInstrumentationMethod(var2, var3)) {
         MethodNode var7 = this.findTargetMethodNode(var2, var3);
         if (var7 == null) {
            throw new IllegalArgumentException("Could not find the method to instrument in the target class");
         } else if ((var7.access & 256) == 1) {
            throw new IllegalArgumentException("Cannot instrument native methods: " + this.targetClassNode.name + "." + var7.name + var7.desc);
         } else {
            this.logger.trace("Inliner processing method " + var2 + var3);
            MethodCallInliner var8 = new MethodCallInliner(var1, var3, var6, var7, this.instrumentationClassName, this.maxLocalsTracker.getMaxLocals(var2, var3), this.logger);
            return var8;
         }
      } else {
         return var6;
      }
   }

   private boolean isInstrumentationMethod(String var1, String var2) {
      Iterator var3 = this.instrumentationMethods.iterator();

      Method var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (Method)var3.next();
      } while(!var4.getName().equals(var1) || !Type.getMethodDescriptor(var4).equals(var2));

      return true;
   }

   private MethodNode findTargetMethodNode(String var1, String var2) {
      Iterator var3 = this.targetClassNode.methods.iterator();

      MethodNode var4;
      do {
         if (!var3.hasNext()) {
            throw new IllegalArgumentException("could not find MethodNode for " + var1 + var2);
         }

         var4 = (MethodNode)var3.next();
      } while(!var4.desc.equals(var2) || !var4.name.equals(var1));

      return var4;
   }
}
