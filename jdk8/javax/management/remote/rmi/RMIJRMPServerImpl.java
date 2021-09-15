package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.RMIExporter;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import sun.reflect.misc.ReflectUtil;
import sun.rmi.server.DeserializationChecker;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;

public class RMIJRMPServerImpl extends RMIServerImpl {
   private final RMIJRMPServerImpl.ExportedWrapper exportedWrapper;
   private final int port;
   private final RMIClientSocketFactory csf;
   private final RMIServerSocketFactory ssf;
   private final Map<String, ?> env;

   public RMIJRMPServerImpl(int var1, RMIClientSocketFactory var2, RMIServerSocketFactory var3, Map<String, ?> var4) throws IOException {
      super(var4);
      if (var1 < 0) {
         throw new IllegalArgumentException("Negative port: " + var1);
      } else {
         this.port = var1;
         this.csf = var2;
         this.ssf = var3;
         this.env = var4 == null ? Collections.emptyMap() : var4;
         String[] var5 = (String[])((String[])this.env.get("jmx.remote.rmi.server.credential.types"));
         ArrayList var6 = null;
         if (var5 != null) {
            var6 = new ArrayList();
            String[] var7 = var5;
            int var8 = var5.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               String var10 = var7[var9];
               if (var10 == null) {
                  throw new IllegalArgumentException("A credential type is null.");
               }

               ReflectUtil.checkPackageAccess(var10);
               var6.add(var10);
            }
         }

         this.exportedWrapper = var6 != null ? new RMIJRMPServerImpl.ExportedWrapper(this, var6) : null;
      }
   }

   protected void export() throws IOException {
      if (this.exportedWrapper != null) {
         this.export(this.exportedWrapper);
      } else {
         this.export(this);
      }

   }

   private void export(Remote var1) throws RemoteException {
      RMIExporter var2 = (RMIExporter)this.env.get("com.sun.jmx.remote.rmi.exporter");
      boolean var3 = EnvHelp.isServerDaemon(this.env);
      if (var3 && var2 != null) {
         throw new IllegalArgumentException("If jmx.remote.x.daemon is specified as true, com.sun.jmx.remote.rmi.exporter cannot be used to specify an exporter!");
      } else {
         if (var3) {
            if (this.csf == null && this.ssf == null) {
               (new UnicastServerRef(this.port)).exportObject(var1, (Object)null, true);
            } else {
               (new UnicastServerRef2(this.port, this.csf, this.ssf)).exportObject(var1, (Object)null, true);
            }
         } else if (var2 != null) {
            var2.exportObject(var1, this.port, this.csf, this.ssf);
         } else {
            UnicastRemoteObject.exportObject(var1, this.port, this.csf, this.ssf);
         }

      }
   }

   private void unexport(Remote var1, boolean var2) throws NoSuchObjectException {
      RMIExporter var3 = (RMIExporter)this.env.get("com.sun.jmx.remote.rmi.exporter");
      if (var3 == null) {
         UnicastRemoteObject.unexportObject(var1, var2);
      } else {
         var3.unexportObject(var1, var2);
      }

   }

   protected String getProtocol() {
      return "rmi";
   }

   public Remote toStub() throws IOException {
      return this.exportedWrapper != null ? RemoteObject.toStub(this.exportedWrapper) : RemoteObject.toStub(this);
   }

   protected RMIConnection makeClient(String var1, Subject var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("Null connectionId");
      } else {
         RMIConnectionImpl var3 = new RMIConnectionImpl(this, var1, this.getDefaultClassLoader(), var2, this.env);
         this.export(var3);
         return var3;
      }
   }

   protected void closeClient(RMIConnection var1) throws IOException {
      this.unexport(var1, true);
   }

   protected void closeServer() throws IOException {
      if (this.exportedWrapper != null) {
         this.unexport(this.exportedWrapper, true);
      } else {
         this.unexport(this, true);
      }

   }

   private static class ExportedWrapper implements RMIServer, DeserializationChecker {
      private final RMIServer impl;
      private final List<String> allowedTypes;

      private ExportedWrapper(RMIServer var1, List<String> var2) {
         this.impl = var1;
         this.allowedTypes = var2;
      }

      public String getVersion() throws RemoteException {
         return this.impl.getVersion();
      }

      public RMIConnection newClient(Object var1) throws IOException {
         return this.impl.newClient(var1);
      }

      public void check(Method var1, ObjectStreamClass var2, int var3, int var4) {
         String var5 = var2.getName();
         if (!this.allowedTypes.contains(var5)) {
            throw new ClassCastException("Unsupported type: " + var5);
         }
      }

      public void checkProxyClass(Method var1, String[] var2, int var3, int var4) {
         if (var2 != null && var2.length > 0) {
            String[] var5 = var2;
            int var6 = var2.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = var5[var7];
               if (!this.allowedTypes.contains(var8)) {
                  throw new ClassCastException("Unsupported type: " + var8);
               }
            }
         }

      }

      // $FF: synthetic method
      ExportedWrapper(RMIServer var1, List var2, Object var3) {
         this(var1, var2);
      }
   }
}
