package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;

public class BufferManagerWriteGrow extends BufferManagerWrite {
   BufferManagerWriteGrow(ORB var1) {
      super(var1);
   }

   public boolean sentFragment() {
      return false;
   }

   public int getBufferSize() {
      ORBData var1 = null;
      int var2 = 1024;
      if (this.orb != null) {
         var1 = this.orb.getORBData();
         if (var1 != null) {
            var2 = var1.getGIOPBufferSize();
            this.dprint("BufferManagerWriteGrow.getBufferSize: bufferSize == " + var2);
         } else {
            this.dprint("BufferManagerWriteGrow.getBufferSize: orbData reference is NULL");
         }
      } else {
         this.dprint("BufferManagerWriteGrow.getBufferSize: orb reference is NULL");
      }

      return var2;
   }

   public void overflow(ByteBufferWithInfo var1) {
      var1.growBuffer(this.orb);
      var1.fragmented = false;
   }

   public void sendMessage() {
      Connection var1 = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
      var1.writeLock();

      try {
         var1.sendWithoutLock((OutputObject)this.outputObject);
         this.sentFullMessage = true;
      } finally {
         var1.writeUnlock();
      }

   }

   public void close() {
   }

   private void dprint(String var1) {
      if (this.orb.transportDebugFlag) {
         ORBUtility.dprint((Object)this, var1);
      }

   }
}
