package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA_2_3.portable.OutputStream;

public interface ObjectKeyTemplate extends Writeable {
   ORBVersion getORBVersion();

   int getSubcontractId();

   int getServerId();

   String getORBId();

   ObjectAdapterId getObjectAdapterId();

   byte[] getAdapterId();

   void write(ObjectId var1, OutputStream var2);

   CorbaServerRequestDispatcher getServerRequestDispatcher(ORB var1, ObjectId var2);
}
