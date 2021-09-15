package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;

public abstract class ObjectReferenceProducerBase {
   protected transient ORB orb;

   public abstract IORFactory getIORFactory();

   public abstract IORTemplateList getIORTemplateList();

   public ObjectReferenceProducerBase(ORB var1) {
      this.orb = var1;
   }

   public Object make_object(String var1, byte[] var2) {
      ObjectId var3 = IORFactories.makeObjectId(var2);
      IOR var4 = this.getIORFactory().makeIOR(this.orb, var1, var3);
      return ORBUtility.makeObjectReference(var4);
   }
}
