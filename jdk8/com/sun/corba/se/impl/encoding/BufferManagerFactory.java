package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.INTERNAL;

public class BufferManagerFactory {
   public static final int GROW = 0;
   public static final int COLLECT = 1;
   public static final int STREAM = 2;

   public static BufferManagerRead newBufferManagerRead(GIOPVersion var0, byte var1, ORB var2) {
      if (var1 != 0) {
         return new BufferManagerReadGrow(var2);
      } else {
         switch(var0.intValue()) {
         case 256:
            return new BufferManagerReadGrow(var2);
         case 257:
         case 258:
            return new BufferManagerReadStream(var2);
         default:
            throw new INTERNAL("Unknown GIOP version: " + var0);
         }
      }
   }

   public static BufferManagerRead newBufferManagerRead(int var0, byte var1, ORB var2) {
      if (var1 != 0) {
         if (var0 != 0) {
            ORBUtilSystemException var3 = ORBUtilSystemException.get(var2, "rpc.encoding");
            throw var3.invalidBuffMgrStrategy("newBufferManagerRead");
         } else {
            return new BufferManagerReadGrow(var2);
         }
      } else {
         switch(var0) {
         case 0:
            return new BufferManagerReadGrow(var2);
         case 1:
            throw new INTERNAL("Collect strategy invalid for reading");
         case 2:
            return new BufferManagerReadStream(var2);
         default:
            throw new INTERNAL("Unknown buffer manager read strategy: " + var0);
         }
      }
   }

   public static BufferManagerWrite newBufferManagerWrite(int var0, byte var1, ORB var2) {
      if (var1 != 0) {
         if (var0 != 0) {
            ORBUtilSystemException var3 = ORBUtilSystemException.get(var2, "rpc.encoding");
            throw var3.invalidBuffMgrStrategy("newBufferManagerWrite");
         } else {
            return new BufferManagerWriteGrow(var2);
         }
      } else {
         switch(var0) {
         case 0:
            return new BufferManagerWriteGrow(var2);
         case 1:
            return new BufferManagerWriteCollect(var2);
         case 2:
            return new BufferManagerWriteStream(var2);
         default:
            throw new INTERNAL("Unknown buffer manager write strategy: " + var0);
         }
      }
   }

   public static BufferManagerWrite newBufferManagerWrite(GIOPVersion var0, byte var1, ORB var2) {
      return (BufferManagerWrite)(var1 != 0 ? new BufferManagerWriteGrow(var2) : newBufferManagerWrite(var2.getORBData().getGIOPBuffMgrStrategy(var0), var1, var2));
   }

   public static BufferManagerRead defaultBufferManagerRead(ORB var0) {
      return new BufferManagerReadGrow(var0);
   }
}
