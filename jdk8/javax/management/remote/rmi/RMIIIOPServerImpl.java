package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.IIOPHelper;
import java.io.IOException;
import java.rmi.Remote;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Map;
import javax.security.auth.Subject;

public class RMIIIOPServerImpl extends RMIServerImpl {
   private final Map<String, ?> env;
   private final AccessControlContext callerACC;

   public RMIIIOPServerImpl(Map<String, ?> var1) throws IOException {
      super(var1);
      this.env = var1 == null ? Collections.emptyMap() : var1;
      this.callerACC = AccessController.getContext();
   }

   protected void export() throws IOException {
      IIOPHelper.exportObject(this);
   }

   protected String getProtocol() {
      return "iiop";
   }

   public Remote toStub() throws IOException {
      Remote var1 = IIOPHelper.toStub(this);
      return var1;
   }

   protected RMIConnection makeClient(String var1, Subject var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("Null connectionId");
      } else {
         RMIConnectionImpl var3 = new RMIConnectionImpl(this, var1, this.getDefaultClassLoader(), var2, this.env);
         IIOPHelper.exportObject(var3);
         return var3;
      }
   }

   protected void closeClient(RMIConnection var1) throws IOException {
      IIOPHelper.unexportObject(var1);
   }

   protected void closeServer() throws IOException {
      IIOPHelper.unexportObject(this);
   }

   RMIConnection doNewClient(final Object var1) throws IOException {
      if (this.callerACC == null) {
         throw new SecurityException("AccessControlContext cannot be null");
      } else {
         try {
            return (RMIConnection)AccessController.doPrivileged(new PrivilegedExceptionAction<RMIConnection>() {
               public RMIConnection run() throws IOException {
                  return RMIIIOPServerImpl.this.superDoNewClient(var1);
               }
            }, this.callerACC);
         } catch (PrivilegedActionException var3) {
            throw (IOException)var3.getCause();
         }
      }
   }

   RMIConnection superDoNewClient(Object var1) throws IOException {
      return super.doNewClient(var1);
   }
}
