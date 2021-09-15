package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.SystemException;

public class BufferManagerWriteStream extends BufferManagerWrite {
   private int fragmentCount = 0;

   BufferManagerWriteStream(ORB var1) {
      super(var1);
   }

   public boolean sentFragment() {
      return this.fragmentCount > 0;
   }

   public int getBufferSize() {
      return this.orb.getORBData().getGIOPFragmentSize();
   }

   public void overflow(ByteBufferWithInfo var1) {
      MessageBase.setFlag(var1.byteBuffer, 2);

      try {
         this.sendFragment(false);
      } catch (SystemException var3) {
         this.orb.getPIHandler().invokeClientPIEndingPoint(2, var3);
         throw var3;
      }

      var1.position(0);
      var1.buflen = var1.byteBuffer.limit();
      var1.fragmented = true;
      FragmentMessage var2 = ((CDROutputObject)this.outputObject).getMessageHeader().createFragmentMessage();
      var2.write((CDROutputObject)this.outputObject);
   }

   private void sendFragment(boolean var1) {
      Connection var2 = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
      var2.writeLock();

      try {
         var2.sendWithoutLock((OutputObject)this.outputObject);
         ++this.fragmentCount;
      } finally {
         var2.writeUnlock();
      }

   }

   public void sendMessage() {
      this.sendFragment(true);
      this.sentFullMessage = true;
   }

   public void close() {
   }
}
