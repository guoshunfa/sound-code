package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.StubNotFoundException;
import java.rmi.UnknownHostException;
import java.rmi.UnmarshalException;
import java.rmi.activation.ActivateFailedException;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.Operation;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public class ActivatableRef implements RemoteRef {
   private static final long serialVersionUID = 7579060052569229166L;
   protected ActivationID id;
   protected RemoteRef ref;
   transient boolean force = false;
   private static final int MAX_RETRIES = 3;
   private static final String versionComplaint = "activation requires 1.2 stubs";

   public ActivatableRef() {
   }

   public ActivatableRef(ActivationID var1, RemoteRef var2) {
      this.id = var1;
      this.ref = var2;
   }

   public static Remote getStub(ActivationDesc var0, ActivationID var1) throws StubNotFoundException {
      String var2 = var0.getClassName();

      try {
         Class var3 = RMIClassLoader.loadClass(var0.getLocation(), var2);
         ActivatableRef var4 = new ActivatableRef(var1, (RemoteRef)null);
         return Util.createProxy(var3, var4, false);
      } catch (IllegalArgumentException var5) {
         throw new StubNotFoundException("class implements an illegal remote interface", var5);
      } catch (ClassNotFoundException var6) {
         throw new StubNotFoundException("unable to load class: " + var2, var6);
      } catch (MalformedURLException var7) {
         throw new StubNotFoundException("malformed URL", var7);
      }
   }

   public Object invoke(Remote var1, Method var2, Object[] var3, long var4) throws Exception {
      boolean var6 = false;
      Object var8 = null;
      RemoteRef var7;
      synchronized(this) {
         if (this.ref == null) {
            var7 = this.activate(var6);
            var6 = true;
         } else {
            var7 = this.ref;
         }
      }

      for(int var9 = 3; var9 > 0; --var9) {
         try {
            return var7.invoke(var1, var2, var3, var4);
         } catch (NoSuchObjectException var16) {
            var8 = var16;
         } catch (ConnectException var17) {
            var8 = var17;
         } catch (UnknownHostException var18) {
            var8 = var18;
         } catch (ConnectIOException var19) {
            var8 = var19;
         } catch (MarshalException var20) {
            throw var20;
         } catch (ServerError var21) {
            throw var21;
         } catch (ServerException var22) {
            throw var22;
         } catch (RemoteException var23) {
            synchronized(this) {
               if (var7 == this.ref) {
                  this.ref = null;
               }
            }

            throw var23;
         }

         if (var9 > 1) {
            synchronized(this) {
               if (!var7.remoteEquals(this.ref) && this.ref != null) {
                  var7 = this.ref;
                  var6 = false;
               } else {
                  RemoteRef var11 = this.activate(var6);
                  if (var11.remoteEquals(var7) && var8 instanceof NoSuchObjectException && !var6) {
                     var11 = this.activate(true);
                  }

                  var7 = var11;
                  var6 = true;
               }
            }
         }
      }

      throw (Exception)var8;
   }

   private synchronized RemoteRef getRef() throws RemoteException {
      if (this.ref == null) {
         this.ref = this.activate(false);
      }

      return this.ref;
   }

   private RemoteRef activate(boolean var1) throws RemoteException {
      assert Thread.holdsLock(this);

      this.ref = null;

      try {
         Remote var2 = this.id.activate(var1);
         ActivatableRef var3 = null;
         if (var2 instanceof RemoteStub) {
            var3 = (ActivatableRef)((RemoteStub)var2).getRef();
         } else {
            RemoteObjectInvocationHandler var4 = (RemoteObjectInvocationHandler)Proxy.getInvocationHandler(var2);
            var3 = (ActivatableRef)var4.getRef();
         }

         this.ref = var3.ref;
         return this.ref;
      } catch (ConnectException var5) {
         throw new ConnectException("activation failed", var5);
      } catch (RemoteException var6) {
         throw new ConnectIOException("activation failed", var6);
      } catch (UnknownObjectException var7) {
         throw new NoSuchObjectException("object not registered");
      } catch (ActivationException var8) {
         throw new ActivateFailedException("activation failed", var8);
      }
   }

   public synchronized RemoteCall newCall(RemoteObject var1, Operation[] var2, int var3, long var4) throws RemoteException {
      throw new UnsupportedOperationException("activation requires 1.2 stubs");
   }

   public void invoke(RemoteCall var1) throws Exception {
      throw new UnsupportedOperationException("activation requires 1.2 stubs");
   }

   public void done(RemoteCall var1) throws RemoteException {
      throw new UnsupportedOperationException("activation requires 1.2 stubs");
   }

   public String getRefClass(ObjectOutput var1) {
      return "ActivatableRef";
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      RemoteRef var2 = this.ref;
      var1.writeObject(this.id);
      if (var2 == null) {
         var1.writeUTF("");
      } else {
         var1.writeUTF(var2.getRefClass(var1));
         var2.writeExternal(var1);
      }

   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      this.id = (ActivationID)var1.readObject();
      this.ref = null;
      String var2 = var1.readUTF();
      if (!var2.equals("")) {
         try {
            Class var3 = Class.forName("sun.rmi.server." + var2);
            this.ref = (RemoteRef)var3.newInstance();
            this.ref.readExternal(var1);
         } catch (InstantiationException var4) {
            throw new UnmarshalException("Unable to create remote reference", var4);
         } catch (IllegalAccessException var5) {
            throw new UnmarshalException("Illegal access creating remote reference");
         }
      }
   }

   public String remoteToString() {
      return Util.getUnqualifiedName(this.getClass()) + " [remoteRef: " + this.ref + "]";
   }

   public int remoteHashCode() {
      return this.id.hashCode();
   }

   public boolean remoteEquals(RemoteRef var1) {
      return var1 instanceof ActivatableRef ? this.id.equals(((ActivatableRef)var1).id) : false;
   }
}
