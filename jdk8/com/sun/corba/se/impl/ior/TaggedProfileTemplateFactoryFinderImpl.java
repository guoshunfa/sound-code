package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

public class TaggedProfileTemplateFactoryFinderImpl extends IdentifiableFactoryFinderBase {
   public TaggedProfileTemplateFactoryFinderImpl(ORB var1) {
      super(var1);
   }

   public Identifiable handleMissingFactory(int var1, InputStream var2) {
      throw this.wrapper.taggedProfileTemplateFactoryNotFound(new Integer(var1));
   }
}
