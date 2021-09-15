package com.sun.corba.se.spi.resolver;

import java.util.Set;
import org.omg.CORBA.Object;

public interface Resolver {
   Object resolve(String var1);

   Set list();
}
