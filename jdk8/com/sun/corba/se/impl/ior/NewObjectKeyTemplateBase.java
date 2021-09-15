package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class NewObjectKeyTemplateBase extends ObjectKeyTemplateBase {
   public NewObjectKeyTemplateBase(ORB var1, int var2, int var3, int var4, String var5, ObjectAdapterId var6) {
      super(var1, var2, var3, var4, var5, var6);
      if (var2 != -1347695872) {
         throw this.wrapper.badMagic(new Integer(var2));
      }
   }

   public void write(ObjectId var1, OutputStream var2) {
      super.write(var1, var2);
      this.getORBVersion().write(var2);
   }

   public void write(OutputStream var1) {
      super.write(var1);
      this.getORBVersion().write(var1);
   }

   protected void setORBVersion(InputStream var1) {
      ORBVersion var2 = ORBVersionFactory.create(var1);
      this.setORBVersion(var2);
   }
}
