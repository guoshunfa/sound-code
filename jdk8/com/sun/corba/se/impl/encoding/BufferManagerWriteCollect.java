package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;

public class BufferManagerWriteCollect extends BufferManagerWrite {
   private BufferQueue queue = new BufferQueue();
   private boolean sentFragment = false;
   private boolean debug = false;

   BufferManagerWriteCollect(ORB var1) {
      super(var1);
      if (var1 != null) {
         this.debug = var1.transportDebugFlag;
      }

   }

   public boolean sentFragment() {
      return this.sentFragment;
   }

   public int getBufferSize() {
      return this.orb.getORBData().getGIOPFragmentSize();
   }

   public void overflow(ByteBufferWithInfo var1) {
      MessageBase.setFlag(var1.byteBuffer, 2);
      this.queue.enqueue(var1);
      ByteBufferWithInfo var2 = new ByteBufferWithInfo(this.orb, this);
      var2.fragmented = true;
      ((CDROutputObject)this.outputObject).setByteBufferWithInfo(var2);
      FragmentMessage var3 = ((CDROutputObject)this.outputObject).getMessageHeader().createFragmentMessage();
      var3.write((CDROutputObject)this.outputObject);
   }

   public void sendMessage() {
      this.queue.enqueue(((CDROutputObject)this.outputObject).getByteBufferWithInfo());
      Iterator var1 = this.iterator();
      Connection var2 = ((OutputObject)this.outputObject).getMessageMediator().getConnection();
      var2.writeLock();

      try {
         ByteBufferWithInfo var4;
         for(ByteBufferPool var3 = this.orb.getByteBufferPool(); var1.hasNext(); var4 = null) {
            var4 = (ByteBufferWithInfo)var1.next();
            ((CDROutputObject)this.outputObject).setByteBufferWithInfo(var4);
            var2.sendWithoutLock((CDROutputObject)this.outputObject);
            this.sentFragment = true;
            if (this.debug) {
               int var5 = System.identityHashCode(var4.byteBuffer);
               StringBuffer var6 = new StringBuffer(80);
               var6.append("sendMessage() - releasing ByteBuffer id (");
               var6.append(var5).append(") to ByteBufferPool.");
               String var7 = var6.toString();
               this.dprint(var7);
            }

            var3.releaseByteBuffer(var4.byteBuffer);
            var4.byteBuffer = null;
         }

         this.sentFullMessage = true;
      } finally {
         var2.writeUnlock();
      }

   }

   public void close() {
      Iterator var1 = this.iterator();
      ByteBufferPool var2 = this.orb.getByteBufferPool();

      while(var1.hasNext()) {
         ByteBufferWithInfo var3 = (ByteBufferWithInfo)var1.next();
         if (var3 != null && var3.byteBuffer != null) {
            if (this.debug) {
               int var4 = System.identityHashCode(var3.byteBuffer);
               StringBuffer var5 = new StringBuffer(80);
               var5.append("close() - releasing ByteBuffer id (");
               var5.append(var4).append(") to ByteBufferPool.");
               String var6 = var5.toString();
               this.dprint(var6);
            }

            var2.releaseByteBuffer(var3.byteBuffer);
            var3.byteBuffer = null;
            var3 = null;
         }
      }

   }

   private void dprint(String var1) {
      ORBUtility.dprint("BufferManagerWriteCollect", var1);
   }

   private Iterator iterator() {
      return new BufferManagerWriteCollect.BufferManagerWriteCollectIterator();
   }

   private class BufferManagerWriteCollectIterator implements Iterator {
      private BufferManagerWriteCollectIterator() {
      }

      public boolean hasNext() {
         return BufferManagerWriteCollect.this.queue.size() != 0;
      }

      public Object next() {
         return BufferManagerWriteCollect.this.queue.dequeue();
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      BufferManagerWriteCollectIterator(Object var2) {
         this();
      }
   }
}
