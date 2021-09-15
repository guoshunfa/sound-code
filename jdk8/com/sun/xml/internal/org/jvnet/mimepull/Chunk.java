package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

final class Chunk {
   volatile Chunk next;
   volatile Data data;

   public Chunk(Data data) {
      this.data = data;
   }

   public Chunk createNext(DataHead dataHead, ByteBuffer buf) {
      return this.next = new Chunk(this.data.createNext(dataHead, buf));
   }
}
