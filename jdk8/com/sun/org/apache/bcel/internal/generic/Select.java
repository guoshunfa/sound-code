package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Select extends BranchInstruction implements VariableLengthInstruction, StackProducer {
   protected int[] match;
   protected int[] indices;
   protected InstructionHandle[] targets;
   protected int fixed_length;
   protected int match_length;
   protected int padding = 0;

   Select() {
   }

   Select(short opcode, int[] match, InstructionHandle[] targets, InstructionHandle target) {
      super(opcode, target);
      this.targets = targets;

      for(int i = 0; i < targets.length; ++i) {
         BranchInstruction.notifyTargetChanged(targets[i], this);
      }

      this.match = match;
      if ((this.match_length = match.length) != targets.length) {
         throw new ClassGenException("Match and target array have not the same length");
      } else {
         this.indices = new int[this.match_length];
      }
   }

   protected int updatePosition(int offset, int max_offset) {
      this.position += offset;
      short old_length = this.length;
      this.padding = (4 - (this.position + 1) % 4) % 4;
      this.length = (short)(this.fixed_length + this.padding);
      return this.length - old_length;
   }

   public void dump(DataOutputStream out) throws IOException {
      out.writeByte(this.opcode);

      for(int i = 0; i < this.padding; ++i) {
         out.writeByte(0);
      }

      this.index = this.getTargetOffset();
      out.writeInt(this.index);
   }

   protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
      this.padding = (4 - bytes.getIndex() % 4) % 4;

      for(int i = 0; i < this.padding; ++i) {
         bytes.readByte();
      }

      this.index = bytes.readInt();
   }

   public String toString(boolean verbose) {
      StringBuilder buf = new StringBuilder(super.toString(verbose));
      if (verbose) {
         for(int i = 0; i < this.match_length; ++i) {
            String s = "null";
            if (this.targets[i] != null) {
               s = this.targets[i].getInstruction().toString();
            }

            buf.append("(").append(this.match[i]).append(", ").append(s).append(" = {").append(this.indices[i]).append("})");
         }
      } else {
         buf.append(" ...");
      }

      return buf.toString();
   }

   public final void setTarget(int i, InstructionHandle target) {
      notifyTargetChanging(this.targets[i], this);
      this.targets[i] = target;
      notifyTargetChanged(this.targets[i], this);
   }

   public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih) {
      boolean targeted = false;
      if (this.target == old_ih) {
         targeted = true;
         this.setTarget(new_ih);
      }

      for(int i = 0; i < this.targets.length; ++i) {
         if (this.targets[i] == old_ih) {
            targeted = true;
            this.setTarget(i, new_ih);
         }
      }

      if (!targeted) {
         throw new ClassGenException("Not targeting " + old_ih);
      }
   }

   public boolean containsTarget(InstructionHandle ih) {
      if (this.target == ih) {
         return true;
      } else {
         for(int i = 0; i < this.targets.length; ++i) {
            if (this.targets[i] == ih) {
               return true;
            }
         }

         return false;
      }
   }

   void dispose() {
      super.dispose();

      for(int i = 0; i < this.targets.length; ++i) {
         this.targets[i].removeTargeter(this);
      }

   }

   public int[] getMatchs() {
      return this.match;
   }

   public int[] getIndices() {
      return this.indices;
   }

   public InstructionHandle[] getTargets() {
      return this.targets;
   }
}
