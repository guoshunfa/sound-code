package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;

public abstract class OldObjectKeyTemplateBase extends ObjectKeyTemplateBase {
   public OldObjectKeyTemplateBase(ORB var1, int var2, int var3, int var4, String var5, ObjectAdapterId var6) {
      super(var1, var2, var3, var4, var5, var6);
      if (var2 == -1347695874) {
         this.setORBVersion(ORBVersionFactory.getOLD());
      } else {
         if (var2 != -1347695873) {
            throw this.wrapper.badMagic(new Integer(var2));
         }

         this.setORBVersion(ORBVersionFactory.getNEW());
      }

   }
}
