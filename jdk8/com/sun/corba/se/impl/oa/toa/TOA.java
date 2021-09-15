package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.spi.oa.ObjectAdapter;
import org.omg.CORBA.Object;

public interface TOA extends ObjectAdapter {
   void connect(Object var1);

   void disconnect(Object var1);
}
