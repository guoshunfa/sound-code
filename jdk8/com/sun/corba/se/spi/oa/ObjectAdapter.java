package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public interface ObjectAdapter {
   ORB getORB();

   Policy getEffectivePolicy(int var1);

   IORTemplate getIORTemplate();

   int getManagerId();

   short getState();

   ObjectReferenceTemplate getAdapterTemplate();

   ObjectReferenceFactory getCurrentFactory();

   void setCurrentFactory(ObjectReferenceFactory var1);

   Object getLocalServant(byte[] var1);

   void getInvocationServant(OAInvocationInfo var1);

   void enter() throws OADestroyed;

   void exit();

   void returnServant();

   OAInvocationInfo makeInvocationInfo(byte[] var1);

   String[] getInterfaces(java.lang.Object var1, byte[] var2);
}
