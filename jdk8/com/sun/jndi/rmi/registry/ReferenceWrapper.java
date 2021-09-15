package com.sun.jndi.rmi.registry;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.naming.NamingException;
import javax.naming.Reference;

public class ReferenceWrapper extends UnicastRemoteObject implements RemoteReference {
   protected Reference wrappee;
   private static final long serialVersionUID = 6078186197417641456L;

   public ReferenceWrapper(Reference var1) throws NamingException, RemoteException {
      this.wrappee = var1;
   }

   public Reference getReference() throws RemoteException {
      return this.wrappee;
   }
}
