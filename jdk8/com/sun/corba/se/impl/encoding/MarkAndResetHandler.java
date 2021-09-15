package com.sun.corba.se.impl.encoding;

interface MarkAndResetHandler {
   void mark(RestorableInputStream var1);

   void fragmentationOccured(ByteBufferWithInfo var1);

   void reset();
}
