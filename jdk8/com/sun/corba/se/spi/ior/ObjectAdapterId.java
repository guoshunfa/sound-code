package com.sun.corba.se.spi.ior;

import java.util.Iterator;

public interface ObjectAdapterId extends Writeable {
   int getNumLevels();

   Iterator iterator();

   String[] getAdapterName();
}
