package com.sun.jndi.rmi.registry;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import javax.naming.NamingException;
import javax.naming.Reference;

public final class ReferenceWrapper_Stub extends RemoteStub implements RemoteReference, Remote {
   private static final long serialVersionUID = 2L;
   private static Method $method_getReference_0;
   // $FF: synthetic field
   static Class class$com$sun$jndi$rmi$registry$RemoteReference;

   static {
      try {
         $method_getReference_0 = (class$com$sun$jndi$rmi$registry$RemoteReference != null ? class$com$sun$jndi$rmi$registry$RemoteReference : (class$com$sun$jndi$rmi$registry$RemoteReference = class$("com.sun.jndi.rmi.registry.RemoteReference"))).getMethod("getReference");
      } catch (NoSuchMethodException var0) {
         throw new NoSuchMethodError("stub class initialization failed");
      }
   }

   public ReferenceWrapper_Stub(RemoteRef var1) {
      super(var1);
   }

   // $FF: synthetic method
   static Class class$(String var0) {
      try {
         return Class.forName(var0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   public Reference getReference() throws RemoteException, NamingException {
      try {
         Object var1 = super.ref.invoke(this, $method_getReference_0, (Object[])null, 3529874867989176284L);
         return (Reference)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (NamingException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }
}
