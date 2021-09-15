package sun.rmi.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sun.misc.ObjectStreamClassValidator;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import sun.security.action.GetPropertyAction;

public class MarshalInputStream extends ObjectInputStream {
   private volatile MarshalInputStream.StreamChecker streamChecker = null;
   private static final boolean useCodebaseOnlyProperty = !((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.rmi.server.useCodebaseOnly", "true")))).equalsIgnoreCase("false");
   protected static Map<String, Class<?>> permittedSunClasses = new HashMap(3);
   private boolean skipDefaultResolveClass = false;
   private final Map<Object, Runnable> doneCallbacks = new HashMap(3);
   private boolean useCodebaseOnly;

   public MarshalInputStream(InputStream var1) throws IOException, StreamCorruptedException {
      super(var1);
      this.useCodebaseOnly = useCodebaseOnlyProperty;
   }

   public Runnable getDoneCallback(Object var1) {
      return (Runnable)this.doneCallbacks.get(var1);
   }

   public void setDoneCallback(Object var1, Runnable var2) {
      this.doneCallbacks.put(var1, var2);
   }

   public void done() {
      Iterator var1 = this.doneCallbacks.values().iterator();

      while(var1.hasNext()) {
         Runnable var2 = (Runnable)var1.next();
         var2.run();
      }

      this.doneCallbacks.clear();
   }

   public void close() throws IOException {
      this.done();
      super.close();
   }

   protected Class<?> resolveClass(ObjectStreamClass var1) throws IOException, ClassNotFoundException {
      Object var2 = this.readLocation();
      String var3 = var1.getName();
      ClassLoader var4 = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
      String var5 = null;
      if (!this.useCodebaseOnly && var2 instanceof String) {
         var5 = (String)var2;
      }

      try {
         return RMIClassLoader.loadClass(var5, var3, var4);
      } catch (AccessControlException var9) {
         return this.checkSunClass(var3, var9);
      } catch (ClassNotFoundException var10) {
         try {
            if (Character.isLowerCase(var3.charAt(0)) && var3.indexOf(46) == -1) {
               return super.resolveClass(var1);
            }
         } catch (ClassNotFoundException var8) {
         }

         throw var10;
      }
   }

   protected Class<?> resolveProxyClass(String[] var1) throws IOException, ClassNotFoundException {
      MarshalInputStream.StreamChecker var2 = this.streamChecker;
      if (var2 != null) {
         var2.checkProxyInterfaceNames(var1);
      }

      Object var3 = this.readLocation();
      ClassLoader var4 = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
      String var5 = null;
      if (!this.useCodebaseOnly && var3 instanceof String) {
         var5 = (String)var3;
      }

      return RMIClassLoader.loadProxyClass(var5, var1, var4);
   }

   private static ClassLoader latestUserDefinedLoader() {
      return VM.latestUserDefinedLoader();
   }

   private Class<?> checkSunClass(String var1, AccessControlException var2) throws AccessControlException {
      Permission var3 = var2.getPermission();
      String var4 = null;
      if (var3 != null) {
         var4 = var3.getName();
      }

      Class var5 = (Class)permittedSunClasses.get(var1);
      if (var4 != null && var5 != null && (var4.equals("accessClassInPackage.sun.rmi.server") || var4.equals("accessClassInPackage.sun.rmi.registry"))) {
         return var5;
      } else {
         throw var2;
      }
   }

   protected Object readLocation() throws IOException, ClassNotFoundException {
      return this.readObject();
   }

   void skipDefaultResolveClass() {
      this.skipDefaultResolveClass = true;
   }

   void useCodebaseOnly() {
      this.useCodebaseOnly = true;
   }

   synchronized void setStreamChecker(MarshalInputStream.StreamChecker var1) {
      this.streamChecker = var1;
      SharedSecrets.getJavaObjectInputStreamAccess().setValidator(this, var1);
   }

   protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
      ObjectStreamClass var1 = super.readClassDescriptor();
      this.validateDesc(var1);
      return var1;
   }

   private void validateDesc(ObjectStreamClass var1) {
      MarshalInputStream.StreamChecker var2;
      synchronized(this) {
         var2 = this.streamChecker;
      }

      if (var2 != null) {
         var2.validateDescriptor(var1);
      }

   }

   static {
      try {
         String var0 = "sun.rmi.server.Activation$ActivationSystemImpl_Stub";
         String var1 = "sun.rmi.registry.RegistryImpl_Stub";
         permittedSunClasses.put(var0, Class.forName(var0));
         permittedSunClasses.put(var1, Class.forName(var1));
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError("Missing system class: " + var2.getMessage());
      }
   }

   interface StreamChecker extends ObjectStreamClassValidator {
      void checkProxyInterfaceNames(String[] var1);
   }
}
