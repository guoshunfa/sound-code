package javax.naming.ldap;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;

public abstract class ControlFactory {
   protected ControlFactory() {
   }

   public abstract Control getControlInstance(Control var1) throws NamingException;

   public static Control getControlInstance(Control var0, Context var1, Hashtable<?, ?> var2) throws NamingException {
      FactoryEnumeration var3 = ResourceManager.getFactories("java.naming.factory.control", var2, var1);
      if (var3 == null) {
         return var0;
      } else {
         Control var4;
         ControlFactory var5;
         for(var4 = null; var4 == null && var3.hasMore(); var4 = var5.getControlInstance(var0)) {
            var5 = (ControlFactory)var3.next();
         }

         return var4 != null ? var4 : var0;
      }
   }
}
