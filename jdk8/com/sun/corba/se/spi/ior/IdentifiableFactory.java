package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public interface IdentifiableFactory {
   int getId();

   Identifiable create(InputStream var1);
}
