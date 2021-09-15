package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;

public class NameService {
   private NamingContext rootContext = null;
   private POA nsPOA = null;
   private ServantManagerImpl contextMgr;
   private ORB theorb;

   public NameService(ORB var1, File var2) throws Exception {
      this.theorb = var1;
      POA var3 = (POA)var1.resolve_initial_references("RootPOA");
      var3.the_POAManager().activate();
      byte var4 = 0;
      Policy[] var5 = new Policy[4];
      int var8 = var4 + 1;
      var5[var4] = var3.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
      var5[var8++] = var3.create_request_processing_policy(RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
      var5[var8++] = var3.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID);
      var5[var8++] = var3.create_servant_retention_policy(ServantRetentionPolicyValue.NON_RETAIN);
      this.nsPOA = var3.create_POA("NameService", (POAManager)null, var5);
      this.nsPOA.the_POAManager().activate();
      this.contextMgr = new ServantManagerImpl(var1, var2, this);
      ServantManagerImpl var10000 = this.contextMgr;
      String var6 = ServantManagerImpl.getRootObjectKey();
      NamingContextImpl var7 = new NamingContextImpl(var1, var6, this, this.contextMgr);
      var7 = this.contextMgr.addContext(var6, var7);
      var7.setServantManagerImpl(this.contextMgr);
      var7.setORB(var1);
      var7.setRootNameService(this);
      this.nsPOA.set_servant_manager(this.contextMgr);
      this.rootContext = NamingContextHelper.narrow(this.nsPOA.create_reference_with_id(var6.getBytes(), NamingContextHelper.id()));
   }

   public NamingContext initialNamingContext() {
      return this.rootContext;
   }

   POA getNSPOA() {
      return this.nsPOA;
   }

   public NamingContext NewContext() throws SystemException {
      try {
         String var1 = this.contextMgr.getNewObjectKey();
         NamingContextImpl var2 = new NamingContextImpl(this.theorb, var1, this, this.contextMgr);
         NamingContextImpl var3 = this.contextMgr.addContext(var1, var2);
         if (var3 != null) {
            var2 = var3;
         }

         var2.setServantManagerImpl(this.contextMgr);
         var2.setORB(this.theorb);
         var2.setRootNameService(this);
         NamingContext var4 = NamingContextHelper.narrow(this.nsPOA.create_reference_with_id(var1.getBytes(), NamingContextHelper.id()));
         return var4;
      } catch (SystemException var5) {
         throw var5;
      } catch (Exception var6) {
         return null;
      }
   }

   Object getObjectReferenceFromKey(String var1) {
      Object var2 = null;

      try {
         var2 = this.nsPOA.create_reference_with_id(var1.getBytes(), NamingContextHelper.id());
      } catch (Exception var4) {
         var2 = null;
      }

      return var2;
   }

   String getObjectKey(Object var1) {
      byte[] var2;
      try {
         var2 = this.nsPOA.reference_to_id(var1);
      } catch (WrongAdapter var4) {
         return null;
      } catch (WrongPolicy var5) {
         return null;
      } catch (Exception var6) {
         return null;
      }

      String var3 = new String(var2);
      return var3;
   }
}
