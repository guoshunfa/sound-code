package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public class TABLESWITCH extends Select {
   TABLESWITCH() {
   }

   public TABLESWITCH(int[] match, InstructionHandle[] targets, InstructionHandle target) {
      super((short)170, match, targets, target);
      this.length = (short)(13 + this.match_length * 4);
      this.fixed_length = this.length;
   }

   public void dump(DataOutputStream out) throws IOException {
      super.dump(out);
      int low = this.match_length > 0 ? this.match[0] : 0;
      out.writeInt(low);
      int high = this.match_length > 0 ? this.match[this.match_length - 1] : 0;
      out.writeInt(high);

      for(int i = 0; i < this.match_length; ++i) {
         out.writeInt(this.indices[i] = this.getTargetOffset(this.targets[i]));
      }

   }

   protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
      super.initFromFile(bytes, wide);
      int low = bytes.readInt();
      int high = bytes.readInt();
      this.match_length = high - low + 1;
      this.fixed_length = (short)(13 + this.match_length * 4);
      this.length = (short)(this.fixed_length + this.padding);
      this.match = new int[this.match_length];
      this.indices = new int[this.match_length];
      this.targets = new InstructionHandle[this.match_length];

      int i;
      for(i = low; i <= high; this.match[i - low] = i++) {
      }

      for(i = 0; i < this.match_length; ++i) {
         this.indices[i] = bytes.readInt();
      }

   }

   public void accept(Visitor v) {
      v.visitVariableLengthInstruction(this);
      v.visitStackProducer(this);
      v.visitBranchInstruction(this);
      v.visitSelect(this);
      v.visitTABLESWITCH(this);
   }
}
