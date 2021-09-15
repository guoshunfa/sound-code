package jdk.internal.instrumentation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

final class MethodCallInliner extends MethodVisitor {
   private final String newClass;
   private final MethodNode inlineTarget;
   private final List<MethodCallInliner.CatchBlock> blocks = new ArrayList();
   private boolean inlining;
   private final Logger logger;
   private final int maxLocals;

   public MethodCallInliner(int var1, String var2, MethodVisitor var3, MethodNode var4, String var5, int var6, Logger var7) {
      super(327680, var3);
      this.newClass = var5;
      this.inlineTarget = var4;
      this.logger = var7;
      this.maxLocals = var6;
      var7.trace("MethodCallInliner: targetMethod=" + var5 + "." + var4.name + var4.desc);
   }

   public void visitMethodInsn(int var1, String var2, String var3, String var4, boolean var5) {
      if (!this.shouldBeInlined(var2, var3, var4)) {
         this.mv.visitMethodInsn(var1, var2, var3, var4, var5);
      } else {
         this.logger.trace("Inlining call to " + var3 + var4);
         Label var6 = new Label();
         this.inlining = true;
         this.inlineTarget.instructions.resetLabels();
         MethodInliningAdapter var7 = new MethodInliningAdapter(this, var6, var1 == 184 ? 8 : 0, var4, this.maxLocals);
         this.inlineTarget.accept((MethodVisitor)var7);
         this.logger.trace("Inlining done");
         this.inlining = false;
         super.visitLabel(var6);
      }
   }

   private boolean shouldBeInlined(String var1, String var2, String var3) {
      return this.inlineTarget.desc.equals(var3) && this.inlineTarget.name.equals(var2) && var1.equals(this.newClass.replace('.', '/'));
   }

   public void visitTryCatchBlock(Label var1, Label var2, Label var3, String var4) {
      if (!this.inlining) {
         this.blocks.add(new MethodCallInliner.CatchBlock(var1, var2, var3, var4));
      } else {
         super.visitTryCatchBlock(var1, var2, var3, var4);
      }

   }

   public void visitMaxs(int var1, int var2) {
      Iterator var3 = this.blocks.iterator();

      while(var3.hasNext()) {
         MethodCallInliner.CatchBlock var4 = (MethodCallInliner.CatchBlock)var3.next();
         super.visitTryCatchBlock(var4.start, var4.end, var4.handler, var4.type);
      }

      super.visitMaxs(var1, var2);
   }

   static final class CatchBlock {
      final Label start;
      final Label end;
      final Label handler;
      final String type;

      CatchBlock(Label var1, Label var2, Label var3, String var4) {
         this.start = var1;
         this.end = var2;
         this.handler = var3;
         this.type = var4;
      }
   }
}
