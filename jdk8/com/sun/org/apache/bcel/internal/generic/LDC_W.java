package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;

public class LDC_W extends LDC {
   LDC_W() {
   }

   public LDC_W(int index) {
      super(index);
   }

   protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
      this.setIndex(bytes.readUnsignedShort());
      this.opcode = 19;
   }
}
