package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA.ORB;

public interface ObjectKey extends Writeable {
   ObjectId getId();

   ObjectKeyTemplate getTemplate();

   byte[] getBytes(ORB var1);

   CorbaServerRequestDispatcher getServerRequestDispatcher(com.sun.corba.se.spi.orb.ORB var1);
}
