package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import org.omg.CORBA.portable.ServantObject;

public abstract class LocalClientRequestDispatcherBase implements LocalClientRequestDispatcher {
   protected ORB orb;
   int scid;
   protected boolean servantIsLocal;
   protected ObjectAdapterFactory oaf;
   protected ObjectAdapterId oaid;
   protected byte[] objectId;
   private static final ThreadLocal isNextCallValid = new ThreadLocal() {
      protected synchronized Object initialValue() {
         return Boolean.TRUE;
      }
   };

   protected LocalClientRequestDispatcherBase(ORB var1, int var2, IOR var3) {
      this.orb = var1;
      IIOPProfile var4 = var3.getProfile();
      this.servantIsLocal = var1.getORBData().isLocalOptimizationAllowed() && var4.isLocal();
      ObjectKeyTemplate var5 = var4.getObjectKeyTemplate();
      this.scid = var5.getSubcontractId();
      RequestDispatcherRegistry var6 = var1.getRequestDispatcherRegistry();
      this.oaf = var6.getObjectAdapterFactory(var2);
      this.oaid = var5.getObjectAdapterId();
      ObjectId var7 = var4.getObjectId();
      this.objectId = var7.getId();
   }

   public byte[] getObjectId() {
      return this.objectId;
   }

   public boolean is_local(org.omg.CORBA.Object var1) {
      return false;
   }

   public boolean useLocalInvocation(org.omg.CORBA.Object var1) {
      if (isNextCallValid.get() == Boolean.TRUE) {
         return this.servantIsLocal;
      } else {
         isNextCallValid.set(Boolean.TRUE);
         return false;
      }
   }

   protected boolean checkForCompatibleServant(ServantObject var1, Class var2) {
      if (var1 == null) {
         return false;
      } else if (!var2.isInstance(var1.servant)) {
         isNextCallValid.set(Boolean.FALSE);
         return false;
      } else {
         return true;
      }
   }
}
