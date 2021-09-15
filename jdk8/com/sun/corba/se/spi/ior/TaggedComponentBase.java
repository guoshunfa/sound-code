package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedComponentHelper;
import sun.corba.OutputStreamFactory;

public abstract class TaggedComponentBase extends IdentifiableBase implements TaggedComponent {
   public org.omg.IOP.TaggedComponent getIOPComponent(ORB var1) {
      EncapsOutputStream var2 = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)var1);
      this.write(var2);
      InputStream var3 = (InputStream)((InputStream)var2.create_input_stream());
      return TaggedComponentHelper.read(var3);
   }
}
