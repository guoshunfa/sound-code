package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

class DefaultDatagramSocketImplFactory {
   static Class<?> prefixImplClass = null;

   static DatagramSocketImpl createDatagramSocketImpl(boolean var0) throws SocketException {
      if (prefixImplClass != null) {
         try {
            return (DatagramSocketImpl)prefixImplClass.newInstance();
         } catch (Exception var2) {
            throw new SocketException("can't instantiate DatagramSocketImpl");
         }
      } else {
         return new PlainDatagramSocketImpl();
      }
   }

   static {
      String var0 = null;

      try {
         var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("impl.prefix", (String)null)));
         if (var0 != null) {
            prefixImplClass = Class.forName("java.net." + var0 + "DatagramSocketImpl");
         }
      } catch (Exception var2) {
         System.err.println("Can't find class: java.net." + var0 + "DatagramSocketImpl: check impl.prefix property");
      }

   }
}
