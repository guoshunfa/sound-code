package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.encoding.CorbaInputObject;
import com.sun.corba.se.spi.encoding.CorbaOutputObject;
import com.sun.corba.se.spi.ior.IOR;

public interface IORTransformer {
   IOR unmarshal(CorbaInputObject var1);

   void marshal(CorbaOutputObject var1, IOR var2);
}
