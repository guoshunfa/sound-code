package sun.rmi.transport;

import java.io.IOException;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.LogStream;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import sun.rmi.runtime.Log;
import sun.rmi.server.Dispatcher;
import sun.rmi.server.UnicastServerRef;
import sun.security.action.GetPropertyAction;

public abstract class Transport {
   static final int logLevel = LogStream.parseLevel(getLogLevel());
   static final Log transportLog;
   private static final ThreadLocal<Transport> currentTransport;
   private static final ObjID dgcID;
   private static final AccessControlContext SETCCL_ACC;

   private static String getLogLevel() {
      return (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.rmi.transport.logLevel")));
   }

   public abstract Channel getChannel(Endpoint var1);

   public abstract void free(Endpoint var1);

   public void exportObject(Target var1) throws RemoteException {
      var1.setExportedTransport(this);
      ObjectTable.putTarget(var1);
   }

   protected void targetUnexported() {
   }

   static Transport currentTransport() {
      return (Transport)currentTransport.get();
   }

   protected abstract void checkAcceptPermission(AccessControlContext var1);

   private static void setContextClassLoader(ClassLoader var0) {
      AccessController.doPrivileged(() -> {
         Thread.currentThread().setContextClassLoader(var0);
         return null;
      }, SETCCL_ACC);
   }

   public boolean serviceCall(final RemoteCall var1) {
      try {
         ObjID var39;
         try {
            var39 = ObjID.read(var1.getInputStream());
         } catch (IOException var33) {
            throw new MarshalException("unable to read objID", var33);
         }

         Transport var40 = var39.equals(dgcID) ? null : this;
         Target var5 = ObjectTable.getTarget(new ObjectEndpoint(var39, var40));
         final Remote var37;
         if (var5 != null && (var37 = var5.getImpl()) != null) {
            final Dispatcher var6 = var5.getDispatcher();
            var5.incrementCallCount();

            boolean var8;
            try {
               transportLog.log(Log.VERBOSE, "call dispatcher");
               final AccessControlContext var7 = var5.getAccessControlContext();
               ClassLoader var41 = var5.getContextClassLoader();
               ClassLoader var9 = Thread.currentThread().getContextClassLoader();

               try {
                  setContextClassLoader(var41);
                  currentTransport.set(this);

                  try {
                     AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                        public Void run() throws IOException {
                           Transport.this.checkAcceptPermission(var7);
                           var6.dispatch(var37, var1);
                           return null;
                        }
                     }, var7);
                     return true;
                  } catch (PrivilegedActionException var31) {
                     throw (IOException)var31.getException();
                  }
               } finally {
                  setContextClassLoader(var9);
                  currentTransport.set((Object)null);
               }
            } catch (IOException var34) {
               transportLog.log(Log.BRIEF, "exception thrown by dispatcher: ", var34);
               var8 = false;
            } finally {
               var5.decrementCallCount();
            }

            return var8;
         }

         throw new NoSuchObjectException("no such object in table");
      } catch (RemoteException var36) {
         RemoteException var2 = var36;
         if (UnicastServerRef.callLog.isLoggable(Log.BRIEF)) {
            String var3 = "";

            try {
               var3 = "[" + RemoteServer.getClientHost() + "] ";
            } catch (ServerNotActiveException var30) {
            }

            String var4 = var3 + "exception: ";
            UnicastServerRef.callLog.log(Log.BRIEF, var4, var36);
         }

         try {
            ObjectOutput var38 = var1.getResultStream(false);
            UnicastServerRef.clearStackTraces(var2);
            var38.writeObject(var2);
            var1.releaseOutputStream();
         } catch (IOException var29) {
            transportLog.log(Log.BRIEF, "exception thrown marshalling exception: ", var29);
            return false;
         }
      }

      return true;
   }

   static {
      transportLog = Log.getLog("sun.rmi.transport.misc", "transport", logLevel);
      currentTransport = new ThreadLocal();
      dgcID = new ObjID(2);
      Permissions var0 = new Permissions();
      var0.add(new RuntimePermission("setContextClassLoader"));
      ProtectionDomain[] var1 = new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, var0)};
      SETCCL_ACC = new AccessControlContext(var1);
   }
}
