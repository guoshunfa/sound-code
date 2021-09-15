package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.ior.ObjectKeyTemplateBase;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.HashMap;
import java.util.Map;

public class TOAFactory implements ObjectAdapterFactory {
   private ORB orb;
   private ORBUtilSystemException wrapper;
   private TOAImpl toa;
   private Map codebaseToTOA;
   private TransientObjectManager tom;

   public ObjectAdapter find(ObjectAdapterId var1) {
      if (var1.equals(ObjectKeyTemplateBase.JIDL_OAID)) {
         return this.getTOA();
      } else {
         throw this.wrapper.badToaOaid();
      }
   }

   public void init(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "oa.lifecycle");
      this.tom = new TransientObjectManager(var1);
      this.codebaseToTOA = new HashMap();
   }

   public void shutdown(boolean var1) {
      if (Util.isInstanceDefined()) {
         Util.getInstance().unregisterTargetsForORB(this.orb);
      }

   }

   public synchronized TOA getTOA(String var1) {
      Object var2 = (TOA)((TOA)this.codebaseToTOA.get(var1));
      if (var2 == null) {
         var2 = new TOAImpl(this.orb, this.tom, var1);
         this.codebaseToTOA.put(var1, var2);
      }

      return (TOA)var2;
   }

   public synchronized TOA getTOA() {
      if (this.toa == null) {
         this.toa = new TOAImpl(this.orb, this.tom, (String)null);
      }

      return this.toa;
   }

   public ORB getORB() {
      return this.orb;
   }
}
