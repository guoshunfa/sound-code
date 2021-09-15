package com.sun.jmx.remote.internal;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;

public interface IIOPProxy {
   boolean isStub(Object var1);

   Object getDelegate(Object var1);

   void setDelegate(Object var1, Object var2);

   Object getOrb(Object var1);

   void connect(Object var1, Object var2) throws RemoteException;

   boolean isOrb(Object var1);

   Object createOrb(String[] var1, Properties var2);

   Object stringToObject(Object var1, String var2);

   String objectToString(Object var1, Object var2);

   <T> T narrow(Object var1, Class<T> var2);

   void exportObject(Remote var1) throws RemoteException;

   void unexportObject(Remote var1) throws NoSuchObjectException;

   Remote toStub(Remote var1) throws NoSuchObjectException;
}
