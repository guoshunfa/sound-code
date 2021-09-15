package jdk.internal.instrumentation;

import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;

final class MethodInliningAdapter extends MethodVisitor {
   private final Label end;
   private final int remapOffset;

   public MethodInliningAdapter(MethodVisitor var1, Label var2, int var3, String var4, int var5) {
      super(327680, var1);
      this.remapOffset = var5;
      this.end = var2;
      Type[] var6 = Type.getArgumentTypes(var4);
      int var7 = this.isStatic(var3) ? 0 : 1;
      Type[] var8 = var6;
      int var9 = var6.length;

      int var10;
      for(var10 = 0; var10 < var9; ++var10) {
         Type var11 = var8[var10];
         var7 += var11.getSize();
      }

      int var12 = var7;

      for(var9 = var6.length - 1; var9 >= 0; --var9) {
         var12 -= var6[var9].getSize();
         var10 = var12 + var5;
         int var13 = var6[var9].getOpcode(54);
         super.visitVarInsn(var13, var10);
      }

      if (!this.isStatic(var3)) {
         super.visitVarInsn(58, 0 + var5);
      }

   }

   private boolean isStatic(int var1) {
      return (var1 & 8) != 0;
   }

   public void visitInsn(int var1) {
      if (var1 != 177 && var1 != 172 && var1 != 176 && var1 != 173) {
         super.visitInsn(var1);
      } else {
         super.visitJumpInsn(167, this.end);
      }

   }

   public void visitVarInsn(int var1, int var2) {
      super.visitVarInsn(var1, var2 + this.remapOffset);
   }

   public void visitIincInsn(int var1, int var2) {
      super.visitIincInsn(var1 + this.remapOffset, var2);
   }

   public void visitMaxs(int var1, int var2) {
   }
}
