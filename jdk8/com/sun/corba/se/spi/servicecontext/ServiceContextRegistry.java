package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Enumeration;
import java.util.Vector;
import org.omg.CORBA.BAD_PARAM;

public class ServiceContextRegistry {
   private ORB orb;
   private Vector scCollection = new Vector();

   private void dprint(String var1) {
      ORBUtility.dprint((Object)this, var1);
   }

   public ServiceContextRegistry(ORB var1) {
      this.orb = var1;
   }

   public void register(Class var1) {
      if (ORB.ORBInitDebug) {
         this.dprint("Registering service context class " + var1);
      }

      ServiceContextData var2 = new ServiceContextData(var1);
      if (this.findServiceContextData(var2.getId()) == null) {
         this.scCollection.addElement(var2);
      } else {
         throw new BAD_PARAM("Tried to register duplicate service context");
      }
   }

   public ServiceContextData findServiceContextData(int var1) {
      if (ORB.ORBInitDebug) {
         this.dprint("Searching registry for service context id " + var1);
      }

      Enumeration var2 = this.scCollection.elements();

      ServiceContextData var3;
      do {
         if (!var2.hasMoreElements()) {
            if (ORB.ORBInitDebug) {
               this.dprint("Service context data not found");
            }

            return null;
         }

         var3 = (ServiceContextData)((ServiceContextData)var2.nextElement());
      } while(var3.getId() != var1);

      if (ORB.ORBInitDebug) {
         this.dprint("Service context data found: " + var3);
      }

      return var3;
   }
}
