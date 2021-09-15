package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.RequestCanceledException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.ListIterator;

public class BufferManagerReadStream implements BufferManagerRead, MarkAndResetHandler {
   private boolean receivedCancel = false;
   private int cancelReqId = 0;
   private boolean endOfStream = true;
   private BufferQueue fragmentQueue = new BufferQueue();
   private long FRAGMENT_TIMEOUT = 60000L;
   private ORB orb;
   private ORBUtilSystemException wrapper;
   private boolean debug = false;
   private boolean markEngaged = false;
   private LinkedList fragmentStack = null;
   private RestorableInputStream inputStream = null;
   private Object streamMemento = null;

   BufferManagerReadStream(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.encoding");
      this.debug = var1.transportDebugFlag;
   }

   public void cancelProcessing(int var1) {
      synchronized(this.fragmentQueue) {
         this.receivedCancel = true;
         this.cancelReqId = var1;
         this.fragmentQueue.notify();
      }
   }

   public void processFragment(ByteBuffer var1, FragmentMessage var2) {
      ByteBufferWithInfo var3 = new ByteBufferWithInfo(this.orb, var1, var2.getHeaderLength());
      synchronized(this.fragmentQueue) {
         if (this.debug) {
            int var5 = System.identityHashCode(var1);
            StringBuffer var6 = new StringBuffer(80);
            var6.append("processFragment() - queueing ByteBuffer id (");
            var6.append(var5).append(") to fragment queue.");
            String var7 = var6.toString();
            this.dprint(var7);
         }

         this.fragmentQueue.enqueue(var3);
         this.endOfStream = !var2.moreFragmentsToFollow();
         this.fragmentQueue.notify();
      }
   }

   public ByteBufferWithInfo underflow(ByteBufferWithInfo var1) {
      ByteBufferWithInfo var2 = null;
      synchronized(this.fragmentQueue) {
         if (this.receivedCancel) {
            throw new RequestCanceledException(this.cancelReqId);
         } else {
            do {
               if (this.fragmentQueue.size() != 0) {
                  var2 = this.fragmentQueue.dequeue();
                  var2.fragmented = true;
                  if (this.debug) {
                     int var11 = System.identityHashCode(var2.byteBuffer);
                     StringBuffer var5 = new StringBuffer(80);
                     var5.append("underflow() - dequeued ByteBuffer id (");
                     var5.append(var11).append(") from fragment queue.");
                     String var6 = var5.toString();
                     this.dprint(var6);
                  }

                  if (!this.markEngaged && var1 != null && var1.byteBuffer != null) {
                     ByteBufferPool var12 = this.getByteBufferPool();
                     if (this.debug) {
                        int var13 = System.identityHashCode(var1.byteBuffer);
                        StringBuffer var14 = new StringBuffer(80);
                        var14.append("underflow() - releasing ByteBuffer id (");
                        var14.append(var13).append(") to ByteBufferPool.");
                        String var7 = var14.toString();
                        this.dprint(var7);
                     }

                     var12.releaseByteBuffer(var1.byteBuffer);
                     var1.byteBuffer = null;
                     var1 = null;
                  }

                  return var2;
               }

               if (this.endOfStream) {
                  throw this.wrapper.endOfStream();
               }

               boolean var4 = false;

               try {
                  this.fragmentQueue.wait(this.FRAGMENT_TIMEOUT);
               } catch (InterruptedException var9) {
                  var4 = true;
               }

               if (!var4 && this.fragmentQueue.size() == 0) {
                  throw this.wrapper.bufferReadManagerTimeout();
               }
            } while(!this.receivedCancel);

            throw new RequestCanceledException(this.cancelReqId);
         }
      }
   }

   public void init(Message var1) {
      if (var1 != null) {
         this.endOfStream = !var1.moreFragmentsToFollow();
      }

   }

   public void close(ByteBufferWithInfo var1) {
      int var2 = 0;
      int var6;
      StringBuffer var7;
      String var8;
      if (this.fragmentQueue != null) {
         synchronized(this.fragmentQueue) {
            if (var1 != null) {
               var2 = System.identityHashCode(var1.byteBuffer);
            }

            ByteBufferWithInfo var4 = null;
            ByteBufferPool var5 = this.getByteBufferPool();

            while(true) {
               if (this.fragmentQueue.size() == 0) {
                  break;
               }

               var4 = this.fragmentQueue.dequeue();
               if (var4 != null && var4.byteBuffer != null) {
                  var6 = System.identityHashCode(var4.byteBuffer);
                  if (var2 != var6 && this.debug) {
                     var7 = new StringBuffer(80);
                     var7.append("close() - fragmentQueue is ").append("releasing ByteBuffer id (").append(var6).append(") to ").append("ByteBufferPool.");
                     var8 = var7.toString();
                     this.dprint(var8);
                  }

                  var5.releaseByteBuffer(var4.byteBuffer);
               }
            }
         }

         this.fragmentQueue = null;
      }

      if (this.fragmentStack != null && this.fragmentStack.size() != 0) {
         if (var1 != null) {
            var2 = System.identityHashCode(var1.byteBuffer);
         }

         ByteBufferWithInfo var3 = null;
         ByteBufferPool var11 = this.getByteBufferPool();
         ListIterator var12 = this.fragmentStack.listIterator();

         while(var12.hasNext()) {
            var3 = (ByteBufferWithInfo)var12.next();
            if (var3 != null && var3.byteBuffer != null) {
               var6 = System.identityHashCode(var3.byteBuffer);
               if (var2 != var6) {
                  if (this.debug) {
                     var7 = new StringBuffer(80);
                     var7.append("close() - fragmentStack - releasing ").append("ByteBuffer id (" + var6 + ") to ").append("ByteBufferPool.");
                     var8 = var7.toString();
                     this.dprint(var8);
                  }

                  var11.releaseByteBuffer(var3.byteBuffer);
               }
            }
         }

         this.fragmentStack = null;
      }

   }

   protected ByteBufferPool getByteBufferPool() {
      return this.orb.getByteBufferPool();
   }

   private void dprint(String var1) {
      ORBUtility.dprint("BufferManagerReadStream", var1);
   }

   public void mark(RestorableInputStream var1) {
      this.inputStream = var1;
      this.markEngaged = true;
      this.streamMemento = var1.createStreamMemento();
      if (this.fragmentStack != null) {
         this.fragmentStack.clear();
      }

   }

   public void fragmentationOccured(ByteBufferWithInfo var1) {
      if (this.markEngaged) {
         if (this.fragmentStack == null) {
            this.fragmentStack = new LinkedList();
         }

         this.fragmentStack.addFirst(new ByteBufferWithInfo(var1));
      }
   }

   public void reset() {
      if (this.markEngaged) {
         this.markEngaged = false;
         if (this.fragmentStack != null && this.fragmentStack.size() != 0) {
            ListIterator var1 = this.fragmentStack.listIterator();
            synchronized(this.fragmentQueue) {
               while(var1.hasNext()) {
                  this.fragmentQueue.push((ByteBufferWithInfo)var1.next());
               }
            }

            this.fragmentStack.clear();
         }

         this.inputStream.restoreInternalState(this.streamMemento);
      }
   }

   public MarkAndResetHandler getMarkAndResetHandler() {
      return this;
   }
}
