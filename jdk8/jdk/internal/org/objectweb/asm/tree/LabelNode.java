package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LabelNode extends AbstractInsnNode {
   private Label label;

   public LabelNode() {
      super(-1);
   }

   public LabelNode(Label var1) {
      super(-1);
      this.label = var1;
   }

   public int getType() {
      return 8;
   }

   public Label getLabel() {
      if (this.label == null) {
         this.label = new Label();
      }

      return this.label;
   }

   public void accept(MethodVisitor var1) {
      var1.visitLabel(this.getLabel());
   }

   public AbstractInsnNode clone(Map<LabelNode, LabelNode> var1) {
      return (AbstractInsnNode)var1.get(this);
   }

   public void resetLabel() {
      this.label = null;
   }
}
