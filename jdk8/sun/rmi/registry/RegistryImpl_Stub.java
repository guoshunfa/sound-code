package sun.rmi.registry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.MarshalException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.UnmarshalException;
import java.rmi.registry.Registry;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class RegistryImpl_Stub extends RemoteStub implements Registry, Remote {
   private static final Operation[] operations = new Operation[]{new Operation("void bind(java.lang.String, java.rmi.Remote)"), new Operation("java.lang.String list()[]"), new Operation("java.rmi.Remote lookup(java.lang.String)"), new Operation("void rebind(java.lang.String, java.rmi.Remote)"), new Operation("void unbind(java.lang.String)")};
   private static final long interfaceHash = 4905912898345647071L;

   public RegistryImpl_Stub() {
   }

   public RegistryImpl_Stub(RemoteRef var1) {
      super(var1);
   }

   public void bind(String var1, Remote var2) throws AccessException, AlreadyBoundException, RemoteException {
      try {
         RemoteCall var3 = this.ref.newCall(this, operations, 0, 4905912898345647071L);

         try {
            ObjectOutput var4 = var3.getOutputStream();
            var4.writeObject(var1);
            var4.writeObject(var2);
         } catch (IOException var5) {
            throw new MarshalException("error marshalling arguments", var5);
         }

         this.ref.invoke(var3);
         this.ref.done(var3);
      } catch (RuntimeException var6) {
         throw var6;
      } catch (RemoteException var7) {
         throw var7;
      } catch (AlreadyBoundException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new UnexpectedException("undeclared checked exception", var9);
      }
   }

   public String[] list() throws AccessException, RemoteException {
      try {
         RemoteCall var1 = this.ref.newCall(this, operations, 1, 4905912898345647071L);
         this.ref.invoke(var1);

         String[] var2;
         try {
            ObjectInput var3 = var1.getInputStream();
            var2 = (String[])((String[])var3.readObject());
         } catch (IOException var11) {
            throw new UnmarshalException("error unmarshalling return", var11);
         } catch (ClassNotFoundException var12) {
            throw new UnmarshalException("error unmarshalling return", var12);
         } finally {
            this.ref.done(var1);
         }

         return var2;
      } catch (RuntimeException var14) {
         throw var14;
      } catch (RemoteException var15) {
         throw var15;
      } catch (Exception var16) {
         throw new UnexpectedException("undeclared checked exception", var16);
      }
   }

   public Remote lookup(String var1) throws AccessException, NotBoundException, RemoteException {
      try {
         RemoteCall var2 = this.ref.newCall(this, operations, 2, 4905912898345647071L);

         try {
            ObjectOutput var3 = var2.getOutputStream();
            var3.writeObject(var1);
         } catch (IOException var17) {
            throw new MarshalException("error marshalling arguments", var17);
         }

         this.ref.invoke(var2);

         Remote var22;
         try {
            ObjectInput var4 = var2.getInputStream();
            var22 = (Remote)var4.readObject();
         } catch (IOException var14) {
            throw new UnmarshalException("error unmarshalling return", var14);
         } catch (ClassNotFoundException var15) {
            throw new UnmarshalException("error unmarshalling return", var15);
         } finally {
            this.ref.done(var2);
         }

         return var22;
      } catch (RuntimeException var18) {
         throw var18;
      } catch (RemoteException var19) {
         throw var19;
      } catch (NotBoundException var20) {
         throw var20;
      } catch (Exception var21) {
         throw new UnexpectedException("undeclared checked exception", var21);
      }
   }

   public void rebind(String var1, Remote var2) throws AccessException, RemoteException {
      try {
         RemoteCall var3 = this.ref.newCall(this, operations, 3, 4905912898345647071L);

         try {
            ObjectOutput var4 = var3.getOutputStream();
            var4.writeObject(var1);
            var4.writeObject(var2);
         } catch (IOException var5) {
            throw new MarshalException("error marshalling arguments", var5);
         }

         this.ref.invoke(var3);
         this.ref.done(var3);
      } catch (RuntimeException var6) {
         throw var6;
      } catch (RemoteException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new UnexpectedException("undeclared checked exception", var8);
      }
   }

   public void unbind(String var1) throws AccessException, NotBoundException, RemoteException {
      try {
         RemoteCall var2 = this.ref.newCall(this, operations, 4, 4905912898345647071L);

         try {
            ObjectOutput var3 = var2.getOutputStream();
            var3.writeObject(var1);
         } catch (IOException var4) {
            throw new MarshalException("error marshalling arguments", var4);
         }

         this.ref.invoke(var2);
         this.ref.done(var2);
      } catch (RuntimeException var5) {
         throw var5;
      } catch (RemoteException var6) {
         throw var6;
      } catch (NotBoundException var7) {
         throw var7;
      } catch (Exception var8) {
         throw new UnexpectedException("undeclared checked exception", var8);
      }
   }
}
