package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public abstract class BufferManagerWrite {
   protected ORB orb;
   protected ORBUtilSystemException wrapper;
   protected Object outputObject;
   protected boolean sentFullMessage = false;

   BufferManagerWrite(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.encoding");
   }

   public abstract boolean sentFragment();

   public boolean sentFullMessage() {
      return this.sentFullMessage;
   }

   public abstract int getBufferSize();

   public abstract void overflow(ByteBufferWithInfo var1);

   public abstract void sendMessage();

   public void setOutputObject(Object var1) {
      this.outputObject = var1;
   }

   public abstract void close();
}
