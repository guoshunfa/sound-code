package com.sun.corba.se.pept.transport;

import java.nio.ByteBuffer;

public interface ByteBufferPool {
   ByteBuffer getByteBuffer(int var1);

   void releaseByteBuffer(ByteBuffer var1);

   int activeCount();
}
