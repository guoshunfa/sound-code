package com.sun.corba.se.spi.encoding;

import com.sun.corba.se.impl.encoding.BufferManagerWrite;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;
import java.io.IOException;

public abstract class CorbaOutputObject extends CDROutputStream implements OutputObject {
   public CorbaOutputObject(ORB var1, GIOPVersion var2, byte var3, boolean var4, BufferManagerWrite var5, byte var6, boolean var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public abstract void writeTo(CorbaConnection var1) throws IOException;
}
