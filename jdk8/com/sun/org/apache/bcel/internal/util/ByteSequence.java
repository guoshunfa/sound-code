package com.sun.org.apache.bcel.internal.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public final class ByteSequence extends DataInputStream {
   private ByteSequence.ByteArrayStream byte_stream;

   public ByteSequence(byte[] bytes) {
      super(new ByteSequence.ByteArrayStream(bytes));
      this.byte_stream = (ByteSequence.ByteArrayStream)this.in;
   }

   public final int getIndex() {
      return this.byte_stream.getPosition();
   }

   final void unreadByte() {
      this.byte_stream.unreadByte();
   }

   private static final class ByteArrayStream extends ByteArrayInputStream {
      ByteArrayStream(byte[] bytes) {
         super(bytes);
      }

      final int getPosition() {
         return this.pos;
      }

      final void unreadByte() {
         if (this.pos > 0) {
            --this.pos;
         }

      }
   }
}
