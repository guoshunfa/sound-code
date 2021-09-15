package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.spi.resolver.Resolver;

public interface InitialServerRequestDispatcher extends CorbaServerRequestDispatcher {
   void init(Resolver var1);
}
