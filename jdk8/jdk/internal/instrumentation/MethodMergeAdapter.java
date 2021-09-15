package jdk.internal.instrumentation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.RemappingMethodAdapter;
import jdk.internal.org.objectweb.asm.commons.SimpleRemapper;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

final class MethodMergeAdapter extends ClassVisitor {
   private final ClassNode cn;
   private final List<Method> methodFilter;
   private final Map<String, String> typeMap;
   private final Logger logger;

   public MethodMergeAdapter(ClassVisitor var1, ClassNode var2, List<Method> var3, TypeMapping[] var4, Logger var5) {
      super(327680, var1);
      this.cn = var2;
      this.methodFilter = var3;
      this.logger = var5;
      this.typeMap = new HashMap();
      TypeMapping[] var6 = var4;
      int var7 = var4.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         TypeMapping var9 = var6[var8];
         this.typeMap.put(var9.from().replace('.', '/'), var9.to().replace('.', '/'));
      }

   }

   public void visit(int var1, int var2, String var3, String var4, String var5, String[] var6) {
      super.visit(var1, var2, var3, var4, var5, var6);
      this.typeMap.put(this.cn.name, var3);
   }

   public MethodVisitor visitMethod(int var1, String var2, String var3, String var4, String[] var5) {
      if (this.methodInFilter(var2, var3)) {
         this.logger.trace("Deleting " + var2 + var3);
         return null;
      } else {
         return super.visitMethod(var1, var2, var3, var4, var5);
      }
   }

   public void visitEnd() {
      SimpleRemapper var1 = new SimpleRemapper(this.typeMap);
      LinkedList var2 = new LinkedList();
      Iterator var3 = this.cn.methods.iterator();

      while(var3.hasNext()) {
         MethodNode var4 = (MethodNode)var3.next();
         if (this.methodInFilter(var4.name, var4.desc)) {
            var2.add(var4);
         }
      }

      while(!var2.isEmpty()) {
         MethodNode var6 = (MethodNode)var2.remove(0);
         this.logger.trace("Copying method: " + var6.name + var6.desc);
         this.logger.trace("   with mapper: " + this.typeMap);
         String[] var7 = (String[])var6.exceptions.toArray(new String[0]);
         MethodVisitor var5 = this.cv.visitMethod(var6.access, var6.name, var6.desc, var6.signature, var7);
         var6.instructions.resetLabels();
         var6.accept((MethodVisitor)(new RemappingMethodAdapter(var6.access, var6.desc, var5, var1)));
         this.findMethodsReferencedByInvokeDynamic(var6, var2);
      }

      super.visitEnd();
   }

   private void findMethodsReferencedByInvokeDynamic(final MethodNode var1, final List<MethodNode> var2) {
      var1.accept(new MethodVisitor(327680) {
         public void visitInvokeDynamicInsn(String var1x, String var2x, Handle var3, Object... var4) {
            Object[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Object var8 = var5[var7];
               if (var8 instanceof Handle) {
                  Handle var9 = (Handle)var8;
                  MethodNode var10 = MethodMergeAdapter.findMethod(MethodMergeAdapter.this.cn, var9);
                  if (var10 == null) {
                     MethodMergeAdapter.this.logger.error("Could not find method " + var9.getName() + var9.getDesc() + " referenced from an invokedynamic in " + var1.name + var1.desc + " while processing class " + MethodMergeAdapter.this.cn.name);
                  }

                  MethodMergeAdapter.this.logger.trace("Adding method referenced from invokedynamic " + var10.name + var10.desc + " to the list of methods to be copied from " + MethodMergeAdapter.this.cn.name);
                  var2.add(var10);
               }
            }

         }
      });
   }

   private static MethodNode findMethod(ClassNode var0, Handle var1) {
      Iterator var2 = var0.methods.iterator();

      MethodNode var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (MethodNode)var2.next();
      } while(!var3.name.equals(var1.getName()) || !var3.desc.equals(var1.getDesc()));

      return var3;
   }

   private boolean methodInFilter(String var1, String var2) {
      Iterator var3 = this.methodFilter.iterator();

      Method var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (Method)var3.next();
      } while(!var4.getName().equals(var1) || !Type.getMethodDescriptor(var4).equals(var2));

      return true;
   }
}
