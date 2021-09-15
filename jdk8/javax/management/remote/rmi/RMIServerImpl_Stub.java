package javax.management.remote.rmi;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public final class RMIServerImpl_Stub extends RemoteStub implements RMIServer {
   private static final long serialVersionUID = 2L;
   private static Method $method_getVersion_0;
   private static Method $method_newClient_1;
   // $FF: synthetic field
   static Class class$javax$management$remote$rmi$RMIServer;
   // $FF: synthetic field
   static Class class$java$lang$Object;

   static {
      try {
         $method_getVersion_0 = (class$javax$management$remote$rmi$RMIServer != null ? class$javax$management$remote$rmi$RMIServer : (class$javax$management$remote$rmi$RMIServer = class$("javax.management.remote.rmi.RMIServer"))).getMethod("getVersion");
         $method_newClient_1 = (class$javax$management$remote$rmi$RMIServer != null ? class$javax$management$remote$rmi$RMIServer : (class$javax$management$remote$rmi$RMIServer = class$("javax.management.remote.rmi.RMIServer"))).getMethod("newClient", class$java$lang$Object != null ? class$java$lang$Object : (class$java$lang$Object = class$("java.lang.Object")));
      } catch (NoSuchMethodException var0) {
         throw new NoSuchMethodError("stub class initialization failed");
      }
   }

   public RMIServerImpl_Stub(RemoteRef var1) {
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

   public String getVersion() throws RemoteException {
      try {
         Object var1 = super.ref.invoke(this, $method_getVersion_0, (Object[])null, -8081107751519807347L);
         return (String)var1;
      } catch (RuntimeException var2) {
         throw var2;
      } catch (RemoteException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new UnexpectedException("undeclared checked exception", var4);
      }
   }

   public RMIConnection newClient(Object var1) throws IOException {
      try {
         Object var2 = super.ref.invoke(this, $method_newClient_1, new Object[]{var1}, -1089742558549201240L);
         return (RMIConnection)var2;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (IOException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new UnexpectedException("undeclared checked exception", var5);
      }
   }
}
