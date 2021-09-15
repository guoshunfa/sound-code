package com.sun.jmx.remote.internal;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

public interface RMIExporter {
   String EXPORTER_ATTRIBUTE = "com.sun.jmx.remote.rmi.exporter";

   Remote exportObject(Remote var1, int var2, RMIClientSocketFactory var3, RMIServerSocketFactory var4) throws RemoteException;

   boolean unexportObject(Remote var1, boolean var2) throws NoSuchObjectException;
}
