package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.orb.ORB;
import java.nio.ByteBuffer;

public class ByteBufferWithInfo {
   private ORB orb;
   private boolean debug;
   private int index;
   public ByteBuffer byteBuffer;
   public int buflen;
   public int needed;
   public boolean fragmented;

   public ByteBufferWithInfo(org.omg.CORBA.ORB var1, ByteBuffer var2, int var3) {
      this.orb = (ORB)var1;
      this.debug = this.orb.transportDebugFlag;
      this.byteBuffer = var2;
      if (var2 != null) {
         this.buflen = var2.limit();
      }

      this.position(var3);
      this.needed = 0;
      this.fragmented = false;
   }

   public ByteBufferWithInfo(org.omg.CORBA.ORB var1, ByteBuffer var2) {
      this(var1, var2, 0);
   }

   public ByteBufferWithInfo(org.omg.CORBA.ORB var1, BufferManagerWrite var2) {
      this(var1, var2, true);
   }

   public ByteBufferWithInfo(org.omg.CORBA.ORB var1, BufferManagerWrite var2, boolean var3) {
      this.orb = (ORB)var1;
      this.debug = this.orb.transportDebugFlag;
      int var4 = var2.getBufferSize();
      if (var3) {
         ByteBufferPool var5 = this.orb.getByteBufferPool();
         this.byteBuffer = var5.getByteBuffer(var4);
         if (this.debug) {
            int var6 = System.identityHashCode(this.byteBuffer);
            StringBuffer var7 = new StringBuffer(80);
            var7.append("constructor (ORB, BufferManagerWrite) - got ").append("ByteBuffer id (").append(var6).append(") from ByteBufferPool.");
            String var8 = var7.toString();
            this.dprint(var8);
         }
      } else {
         this.byteBuffer = ByteBuffer.allocate(var4);
      }

      this.position(0);
      this.buflen = var4;
      this.byteBuffer.limit(this.buflen);
      this.needed = 0;
      this.fragmented = false;
   }

   public ByteBufferWithInfo(ByteBufferWithInfo var1) {
      this.orb = var1.orb;
      this.debug = var1.debug;
      this.byteBuffer = var1.byteBuffer;
      this.buflen = var1.buflen;
      this.byteBuffer.limit(this.buflen);
      this.position(var1.position());
      this.needed = var1.needed;
      this.fragmented = var1.fragmented;
   }

   public int getSize() {
      return this.position();
   }

   public int getLength() {
      return this.buflen;
   }

   public int position() {
      return this.index;
   }

   public void position(int var1) {
      this.byteBuffer.position(var1);
      this.index = var1;
   }

   public void setLength(int var1) {
      this.buflen = var1;
      this.byteBuffer.limit(this.buflen);
   }

   public void growBuffer(ORB var1) {
      int var2;
      for(var2 = this.byteBuffer.limit() * 2; this.position() + this.needed >= var2; var2 *= 2) {
      }

      ByteBufferPool var3 = var1.getByteBufferPool();
      ByteBuffer var4 = var3.getByteBuffer(var2);
      int var5;
      StringBuffer var6;
      String var7;
      if (this.debug) {
         var5 = System.identityHashCode(var4);
         var6 = new StringBuffer(80);
         var6.append("growBuffer() - got ByteBuffer id (");
         var6.append(var5).append(") from ByteBufferPool.");
         var7 = var6.toString();
         this.dprint(var7);
      }

      this.byteBuffer.position(0);
      var4.put(this.byteBuffer);
      if (this.debug) {
         var5 = System.identityHashCode(this.byteBuffer);
         var6 = new StringBuffer(80);
         var6.append("growBuffer() - releasing ByteBuffer id (");
         var6.append(var5).append(") to ByteBufferPool.");
         var7 = var6.toString();
         this.dprint(var7);
      }

      var3.releaseByteBuffer(this.byteBuffer);
      this.byteBuffer = var4;
      this.buflen = var2;
      this.byteBuffer.limit(this.buflen);
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer("ByteBufferWithInfo:");
      var1.append(" buflen = " + this.buflen);
      var1.append(" byteBuffer.limit = " + this.byteBuffer.limit());
      var1.append(" index = " + this.index);
      var1.append(" position = " + this.position());
      var1.append(" needed = " + this.needed);
      var1.append(" byteBuffer = " + (this.byteBuffer == null ? "null" : "not null"));
      var1.append(" fragmented = " + this.fragmented);
      return var1.toString();
   }

   protected void dprint(String var1) {
      ORBUtility.dprint("ByteBufferWithInfo", var1);
   }
}
