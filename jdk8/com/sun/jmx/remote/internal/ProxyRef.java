package com.sun.jmx.remote.internal;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;

public class ProxyRef implements RemoteRef {
   private static final long serialVersionUID = -6503061366316814723L;
   protected RemoteRef ref;

   public ProxyRef(RemoteRef var1) {
      this.ref = var1;
   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.ref.readExternal(var1);
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      this.ref.writeExternal(var1);
   }

   /** @deprecated */
   @Deprecated
   public void invoke(RemoteCall var1) throws Exception {
      this.ref.invoke(var1);
   }

   public Object invoke(Remote var1, Method var2, Object[] var3, long var4) throws Exception {
      return this.ref.invoke(var1, var2, var3, var4);
   }

   /** @deprecated */
   @Deprecated
   public void done(RemoteCall var1) throws RemoteException {
      this.ref.done(var1);
   }

   public String getRefClass(ObjectOutput var1) {
      return this.ref.getRefClass(var1);
   }

   /** @deprecated */
   @Deprecated
   public RemoteCall newCall(RemoteObject var1, Operation[] var2, int var3, long var4) throws RemoteException {
      return this.ref.newCall(var1, var2, var3, var4);
   }

   public boolean remoteEquals(RemoteRef var1) {
      return this.ref.remoteEquals(var1);
   }

   public int remoteHashCode() {
      return this.ref.remoteHashCode();
   }

   public String remoteToString() {
      return this.ref.remoteToString();
   }
}
