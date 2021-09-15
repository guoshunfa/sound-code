package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.Remote;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RemoteStub;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.rmi.transport.ObjectTable;
import sun.rmi.transport.Target;

public class MarshalOutputStream extends ObjectOutputStream {
   public MarshalOutputStream(OutputStream var1) throws IOException {
      this(var1, 1);
   }

   public MarshalOutputStream(OutputStream var1, int var2) throws IOException {
      super(var1);
      this.useProtocolVersion(var2);
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            MarshalOutputStream.this.enableReplaceObject(true);
            return null;
         }
      });
   }

   protected final Object replaceObject(Object var1) throws IOException {
      if (var1 instanceof Remote && !(var1 instanceof RemoteStub)) {
         Target var2 = ObjectTable.getTarget((Remote)var1);
         if (var2 != null) {
            return var2.getStub();
         }
      }

      return var1;
   }

   protected void annotateClass(Class<?> var1) throws IOException {
      this.writeLocation(RMIClassLoader.getClassAnnotation(var1));
   }

   protected void annotateProxyClass(Class<?> var1) throws IOException {
      this.annotateClass(var1);
   }

   protected void writeLocation(String var1) throws IOException {
      this.writeObject(var1);
   }
}
