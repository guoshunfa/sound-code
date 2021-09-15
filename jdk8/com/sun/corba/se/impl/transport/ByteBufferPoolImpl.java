package com.sun.corba.se.impl.transport;

import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ByteBufferPoolImpl implements ByteBufferPool {
   private ORB itsOrb;
   private int itsByteBufferSize;
   private ArrayList itsPool;
   private int itsObjectCounter = 0;
   private boolean debug;

   public ByteBufferPoolImpl(ORB var1) {
      this.itsByteBufferSize = var1.getORBData().getGIOPFragmentSize();
      this.itsPool = new ArrayList();
      this.itsOrb = var1;
      this.debug = var1.transportDebugFlag;
   }

   public ByteBuffer getByteBuffer(int var1) {
      ByteBuffer var2 = null;
      if (var1 <= this.itsByteBufferSize && !this.itsOrb.getORBData().disableDirectByteBufferUse()) {
         int var3;
         synchronized(this.itsPool) {
            var3 = this.itsPool.size();
            if (var3 > 0) {
               var2 = (ByteBuffer)this.itsPool.remove(var3 - 1);
               var2.clear();
            }
         }

         if (var3 <= 0) {
            var2 = ByteBuffer.allocateDirect(this.itsByteBufferSize);
         }

         ++this.itsObjectCounter;
      } else {
         var2 = ByteBuffer.allocate(var1);
      }

      return var2;
   }

   public void releaseByteBuffer(ByteBuffer var1) {
      if (var1.isDirect()) {
         synchronized(this.itsPool) {
            boolean var3 = false;
            int var4 = 0;
            if (this.debug) {
               for(int var5 = 0; var5 < this.itsPool.size() && !var3; ++var5) {
                  ByteBuffer var6 = (ByteBuffer)this.itsPool.get(var5);
                  if (var1 == var6) {
                     var3 = true;
                     var4 = System.identityHashCode(var1);
                  }
               }
            }

            if (var3 && this.debug) {
               String var9 = Thread.currentThread().getName();
               Throwable var10 = new Throwable(var9 + ": Duplicate ByteBuffer reference (" + var4 + ")");
               var10.printStackTrace(System.out);
            } else {
               this.itsPool.add(var1);
            }
         }

         --this.itsObjectCounter;
      } else {
         var1 = null;
      }

   }

   public int activeCount() {
      return this.itsObjectCounter;
   }
}
