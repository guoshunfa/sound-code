package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import java.nio.ByteBuffer;

public interface BufferManagerRead {
   void processFragment(ByteBuffer var1, FragmentMessage var2);

   ByteBufferWithInfo underflow(ByteBufferWithInfo var1);

   void init(Message var1);

   MarkAndResetHandler getMarkAndResetHandler();

   void cancelProcessing(int var1);

   void close(ByteBufferWithInfo var1);
}
