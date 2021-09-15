package sun.management.jmxremote;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import sun.misc.ObjectInputFilter;
import sun.rmi.registry.RegistryImpl;

public class SingleEntryRegistry extends RegistryImpl {
   private final String name;
   private final Remote object;
   private static final long serialVersionUID = -4897238949499730950L;

   SingleEntryRegistry(int var1, String var2, Remote var3) throws RemoteException {
      super(var1, (RMIClientSocketFactory)null, (RMIServerSocketFactory)null, SingleEntryRegistry::singleRegistryFilter);
      this.name = var2;
      this.object = var3;
   }

   SingleEntryRegistry(int var1, RMIClientSocketFactory var2, RMIServerSocketFactory var3, String var4, Remote var5) throws RemoteException {
      super(var1, var2, var3, SingleEntryRegistry::singleRegistryFilter);
      this.name = var4;
      this.object = var5;
   }

   public String[] list() {
      return new String[]{this.name};
   }

   public Remote lookup(String var1) throws NotBoundException {
      if (var1.equals(this.name)) {
         return this.object;
      } else {
         throw new NotBoundException("Not bound: \"" + var1 + "\" (only bound name is \"" + this.name + "\")");
      }
   }

   public void bind(String var1, Remote var2) throws AccessException {
      throw new AccessException("Cannot modify this registry");
   }

   public void rebind(String var1, Remote var2) throws AccessException {
      throw new AccessException("Cannot modify this registry");
   }

   public void unbind(String var1) throws AccessException {
      throw new AccessException("Cannot modify this registry");
   }

   private static ObjectInputFilter.Status singleRegistryFilter(ObjectInputFilter.FilterInfo var0) {
      return var0.serialClass() == null && var0.depth() <= 2L && var0.references() <= 4L && var0.arrayLength() < 0L ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
   }
}
