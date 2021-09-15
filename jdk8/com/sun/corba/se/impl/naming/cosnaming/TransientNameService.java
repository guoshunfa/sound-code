package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class TransientNameService {
   private Object theInitialNamingContext;

   public TransientNameService(ORB var1) throws INITIALIZE {
      this.initialize(var1, "NameService");
   }

   public TransientNameService(ORB var1, String var2) throws INITIALIZE {
      this.initialize(var1, var2);
   }

   private void initialize(ORB var1, String var2) throws INITIALIZE {
      NamingSystemException var3 = NamingSystemException.get(var1, "naming");

      try {
         POA var4 = (POA)var1.resolve_initial_references("RootPOA");
         var4.the_POAManager().activate();
         byte var5 = 0;
         Policy[] var6 = new Policy[3];
         int var12 = var5 + 1;
         var6[var5] = var4.create_lifespan_policy(LifespanPolicyValue.TRANSIENT);
         var6[var12++] = var4.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID);
         var6[var12++] = var4.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN);
         POA var7 = var4.create_POA("TNameService", (POAManager)null, var6);
         var7.the_POAManager().activate();
         TransientNamingContext var8 = new TransientNamingContext(var1, (Object)null, var7);
         byte[] var9 = var7.activate_object(var8);
         var8.localRoot = var7.id_to_reference(var9);
         this.theInitialNamingContext = var8.localRoot;
         var1.register_initial_reference(var2, this.theInitialNamingContext);
      } catch (SystemException var10) {
         throw var3.transNsCannotCreateInitialNcSys((Throwable)var10);
      } catch (Exception var11) {
         throw var3.transNsCannotCreateInitialNc((Throwable)var11);
      }
   }

   public Object initialNamingContext() {
      return this.theInitialNamingContext;
   }
}
