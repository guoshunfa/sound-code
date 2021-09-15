package com.sun.corba.se.spi.presentation.rmi;

import java.rmi.RemoteException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.OutputStream;

public interface DynamicStub extends Object {
   void setDelegate(Delegate var1);

   Delegate getDelegate();

   ORB getORB();

   String[] getTypeIds();

   void connect(ORB var1) throws RemoteException;

   boolean isLocal();

   OutputStream request(String var1, boolean var2);
}
