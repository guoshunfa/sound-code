package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.TaggedComponent;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedComponentHelper;
import sun.corba.OutputStreamFactory;

public class TaggedComponentFactoryFinderImpl extends IdentifiableFactoryFinderBase implements TaggedComponentFactoryFinder {
   public TaggedComponentFactoryFinderImpl(ORB var1) {
      super(var1);
   }

   public Identifiable handleMissingFactory(int var1, InputStream var2) {
      return new GenericTaggedComponent(var1, var2);
   }

   public TaggedComponent create(org.omg.CORBA.ORB var1, org.omg.IOP.TaggedComponent var2) {
      EncapsOutputStream var3 = OutputStreamFactory.newEncapsOutputStream((ORB)var1);
      TaggedComponentHelper.write(var3, var2);
      InputStream var4 = (InputStream)((InputStream)var3.create_input_stream());
      var4.read_ulong();
      return (TaggedComponent)this.create(var2.tag, var4);
   }
}
