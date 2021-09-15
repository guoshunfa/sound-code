package sun.reflect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Label {
   private List<Label.PatchInfo> patches = new ArrayList();

   public Label() {
   }

   void add(ClassFileAssembler var1, short var2, short var3, int var4) {
      this.patches.add(new Label.PatchInfo(var1, var2, var3, var4));
   }

   public void bind() {
      Iterator var1 = this.patches.iterator();

      while(var1.hasNext()) {
         Label.PatchInfo var2 = (Label.PatchInfo)var1.next();
         short var3 = var2.asm.getLength();
         short var4 = (short)(var3 - var2.instrBCI);
         var2.asm.emitShort(var2.patchBCI, var4);
         var2.asm.setStack(var2.stackDepth);
      }

   }

   static class PatchInfo {
      final ClassFileAssembler asm;
      final short instrBCI;
      final short patchBCI;
      final int stackDepth;

      PatchInfo(ClassFileAssembler var1, short var2, short var3, int var4) {
         this.asm = var1;
         this.instrBCI = var2;
         this.patchBCI = var3;
         this.stackDepth = var4;
      }
   }
}
