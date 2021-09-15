package com.sun.org.apache.bcel.internal.generic;

import java.io.DataOutputStream;
import java.io.IOException;

public class JSR extends JsrInstruction implements VariableLengthInstruction {
   JSR() {
   }

   public JSR(InstructionHandle target) {
      super((short)168, target);
   }

   public void dump(DataOutputStream out) throws IOException {
      this.index = this.getTargetOffset();
      if (this.opcode == 168) {
         super.dump(out);
      } else {
         this.index = this.getTargetOffset();
         out.writeByte(this.opcode);
         out.writeInt(this.index);
      }

   }

   protected int updatePosition(int offset, int max_offset) {
      int i = this.getTargetOffset();
      this.position += offset;
      if (Math.abs(i) >= 32767 - max_offset) {
         this.opcode = 201;
         this.length = 5;
         return 2;
      } else {
         return 0;
      }
   }

   public void accept(Visitor v) {
      v.visitStackProducer(this);
      v.visitVariableLengthInstruction(this);
      v.visitBranchInstruction(this);
      v.visitJsrInstruction(this);
      v.visitJSR(this);
   }
}
