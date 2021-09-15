package jdk.internal.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Comparator;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;

public class TryCatchBlockSorter extends MethodNode {
   public TryCatchBlockSorter(MethodVisitor var1, int var2, String var3, String var4, String var5, String[] var6) {
      this(327680, var1, var2, var3, var4, var5, var6);
   }

   protected TryCatchBlockSorter(int var1, MethodVisitor var2, int var3, String var4, String var5, String var6, String[] var7) {
      super(var1, var3, var4, var5, var6, var7);
      this.mv = var2;
   }

   public void visitEnd() {
      Comparator var1 = new Comparator<TryCatchBlockNode>() {
         public int compare(TryCatchBlockNode var1, TryCatchBlockNode var2) {
            int var3 = this.blockLength(var1);
            int var4 = this.blockLength(var2);
            return var3 - var4;
         }

         private int blockLength(TryCatchBlockNode var1) {
            int var2 = TryCatchBlockSorter.this.instructions.indexOf(var1.start);
            int var3 = TryCatchBlockSorter.this.instructions.indexOf(var1.end);
            return var3 - var2;
         }
      };
      Collections.sort(this.tryCatchBlocks, var1);

      for(int var2 = 0; var2 < this.tryCatchBlocks.size(); ++var2) {
         ((TryCatchBlockNode)this.tryCatchBlocks.get(var2)).updateIndex(var2);
      }

      if (this.mv != null) {
         this.accept(this.mv);
      }

   }
}
