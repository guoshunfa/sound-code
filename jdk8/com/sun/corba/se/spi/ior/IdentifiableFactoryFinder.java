package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public interface IdentifiableFactoryFinder {
   Identifiable create(int var1, InputStream var2);

   void registerFactory(IdentifiableFactory var1);
}
