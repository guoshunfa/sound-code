package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.ForwardException;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ServantObject;

public class POALocalCRDImpl extends LocalClientRequestDispatcherBase {
   private ORBUtilSystemException wrapper;
   private POASystemException poaWrapper;

   public POALocalCRDImpl(ORB var1, int var2, IOR var3) {
      super(var1, var2, var3);
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.poaWrapper = POASystemException.get(var1, "rpc.protocol");
   }

   private OAInvocationInfo servantEnter(ObjectAdapter var1) throws OADestroyed {
      var1.enter();
      OAInvocationInfo var2 = var1.makeInvocationInfo(this.objectId);
      this.orb.pushInvocationInfo(var2);
      return var2;
   }

   private void servantExit(ObjectAdapter var1) {
      try {
         var1.returnServant();
      } finally {
         var1.exit();
         this.orb.popInvocationInfo();
      }

   }

   public ServantObject servant_preinvoke(Object var1, String var2, Class var3) {
      ObjectAdapter var4 = this.oaf.find(this.oaid);
      OAInvocationInfo var5 = null;

      try {
         var5 = this.servantEnter(var4);
         var5.setOperation(var2);
      } catch (OADestroyed var8) {
         return this.servant_preinvoke(var1, var2, var3);
      }

      try {
         try {
            var4.getInvocationServant(var5);
            if (!this.checkForCompatibleServant(var5, var3)) {
               return null;
            }
         } catch (Throwable var9) {
            this.servantExit(var4);
            throw var9;
         }
      } catch (ForwardException var10) {
         RuntimeException var7 = new RuntimeException("deal with this.");
         var7.initCause(var10);
         throw var7;
      } catch (ThreadDeath var11) {
         throw this.wrapper.runtimeexception((Throwable)var11);
      } catch (Throwable var12) {
         if (var12 instanceof SystemException) {
            throw (SystemException)var12;
         }

         throw this.poaWrapper.localServantLookup(var12);
      }

      if (!this.checkForCompatibleServant(var5, var3)) {
         this.servantExit(var4);
         return null;
      } else {
         return var5;
      }
   }

   public void servant_postinvoke(Object var1, ServantObject var2) {
      ObjectAdapter var3 = this.orb.peekInvocationInfo().oa();
      this.servantExit(var3);
   }
}
