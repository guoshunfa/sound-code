package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;

public final class UnmarshallerChain {
   private int offset = 0;
   public final JAXBContextImpl context;

   public UnmarshallerChain(JAXBContextImpl context) {
      this.context = context;
   }

   public int allocateOffset() {
      return this.offset++;
   }

   public int getScopeSize() {
      return this.offset;
   }
}
