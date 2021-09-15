package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Object;

public class ForwardException extends RuntimeException {
   private ORB orb;
   private Object obj;
   private IOR ior;

   public ForwardException(ORB var1, IOR var2) {
      this.orb = var1;
      this.obj = null;
      this.ior = var2;
   }

   public ForwardException(ORB var1, Object var2) {
      if (var2 instanceof LocalObject) {
         throw new BAD_PARAM();
      } else {
         this.orb = var1;
         this.obj = var2;
         this.ior = null;
      }
   }

   public synchronized Object getObject() {
      if (this.obj == null) {
         this.obj = ORBUtility.makeObjectReference(this.ior);
      }

      return this.obj;
   }

   public synchronized IOR getIOR() {
      if (this.ior == null) {
         this.ior = ORBUtility.getIOR(this.obj);
      }

      return this.ior;
   }
}
