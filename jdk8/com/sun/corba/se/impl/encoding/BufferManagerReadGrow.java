package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;

public class BufferManagerReadGrow implements BufferManagerRead, MarkAndResetHandler {
   private ORB orb;
   private ORBUtilSystemException wrapper;
   private Object streamMemento;
   private RestorableInputStream inputStream;
   private boolean markEngaged = false;

   BufferManagerReadGrow(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.encoding");
   }

   public void processFragment(ByteBuffer var1, FragmentMessage var2) {
   }

   public void init(Message var1) {
   }

   public ByteBufferWithInfo underflow(ByteBufferWithInfo var1) {
      throw this.wrapper.unexpectedEof();
   }

   public void cancelProcessing(int var1) {
   }

   public MarkAndResetHandler getMarkAndResetHandler() {
      return this;
   }

   public void mark(RestorableInputStream var1) {
      this.markEngaged = true;
      this.inputStream = var1;
      this.streamMemento = this.inputStream.createStreamMemento();
   }

   public void fragmentationOccured(ByteBufferWithInfo var1) {
   }

   public void reset() {
      if (this.markEngaged) {
         this.markEngaged = false;
         this.inputStream.restoreInternalState(this.streamMemento);
         this.streamMemento = null;
      }
   }

   public void close(ByteBufferWithInfo var1) {
   }
}
