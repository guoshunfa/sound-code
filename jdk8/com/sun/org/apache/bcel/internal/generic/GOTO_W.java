package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class GOTO_W extends GotoInstruction {
   GOTO_W() {
   }

   public GOTO_W(InstructionHandle target) {
      super((short)200, target);
      this.length = 5;
   }

   public void dump(DataOutputStream out) throws IOException {
      this.index = this.getTargetOffset();
      out.writeByte(this.opcode);
      out.writeInt(this.index);
   }

   protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
      this.index = bytes.readInt();
      this.length = 5;
   }

   public void accept(Visitor v) {
      v.visitUnconditionalBranch(this);
      v.visitBranchInstruction(this);
      v.visitGotoInstruction(this);
      v.visitGOTO_W(this);
   }
}
