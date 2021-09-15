package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.copyobject.ObjectCopier;
import java.io.Serializable;
import java.rmi.Remote;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ORBStreamObjectCopierImpl implements ObjectCopier {
   private ORB orb;

   public ORBStreamObjectCopierImpl(ORB var1) {
      this.orb = var1;
   }

   public Object copy(Object var1) {
      if (var1 instanceof Remote) {
         return Utility.autoConnect(var1, this.orb, true);
      } else {
         OutputStream var2 = (OutputStream)this.orb.create_output_stream();
         var2.write_value((Serializable)var1);
         InputStream var3 = (InputStream)var2.create_input_stream();
         return var3.read_value();
      }
   }
}
