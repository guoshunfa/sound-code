package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.TaggedComponent;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

public class GenericTaggedComponent extends GenericIdentifiable implements TaggedComponent {
   public GenericTaggedComponent(int var1, InputStream var2) {
      super(var1, var2);
   }

   public GenericTaggedComponent(int var1, byte[] var2) {
      super(var1, var2);
   }

   public org.omg.IOP.TaggedComponent getIOPComponent(ORB var1) {
      return new org.omg.IOP.TaggedComponent(this.getId(), this.getData());
   }
}
