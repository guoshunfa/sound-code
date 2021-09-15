package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class TaggedProfileTemplateBase extends IdentifiableContainerBase implements TaggedProfileTemplate {
   public void write(OutputStream var1) {
      EncapsulationUtility.writeEncapsulation(this, var1);
   }

   public org.omg.IOP.TaggedComponent[] getIOPComponents(ORB var1, int var2) {
      int var3 = 0;

      Iterator var4;
      for(var4 = this.iteratorById(var2); var4.hasNext(); ++var3) {
         var4.next();
      }

      org.omg.IOP.TaggedComponent[] var5 = new org.omg.IOP.TaggedComponent[var3];
      int var6 = 0;

      TaggedComponent var7;
      for(var4 = this.iteratorById(var2); var4.hasNext(); var5[var6++] = var7.getIOPComponent(var1)) {
         var7 = (TaggedComponent)((TaggedComponent)var4.next());
      }

      return var5;
   }
}
