package com.sun.corba.se.spi.orb;

import java.util.Properties;

public interface DataCollector {
   boolean isApplet();

   boolean initialHostIsLocal();

   void setParser(PropertyParser var1);

   Properties getProperties();
}
