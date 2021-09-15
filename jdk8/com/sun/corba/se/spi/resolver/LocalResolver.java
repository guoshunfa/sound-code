package com.sun.corba.se.spi.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public interface LocalResolver extends Resolver {
   void register(String var1, Closure var2);
}
