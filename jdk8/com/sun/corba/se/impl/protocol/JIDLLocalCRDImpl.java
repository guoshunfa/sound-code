package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class JIDLLocalCRDImpl extends LocalClientRequestDispatcherBase {
   protected ServantObject servant;

   public JIDLLocalCRDImpl(ORB var1, int var2, IOR var3) {
      super(var1, var2, var3);
   }

   public ServantObject servant_preinvoke(Object var1, String var2, Class var3) {
      return !this.checkForCompatibleServant(this.servant, var3) ? null : this.servant;
   }

   public void servant_postinvoke(Object var1, ServantObject var2) {
   }

   public void setServant(java.lang.Object var1) {
      if (var1 != null && var1 instanceof Tie) {
         this.servant = new ServantObject();
         this.servant.servant = ((Tie)var1).getTarget();
      } else {
         this.servant = null;
      }

   }

   public void unexport() {
      this.servant = null;
   }
}
