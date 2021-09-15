package com.sun.corba.se.spi.orbutil.proxy;

import java.lang.reflect.InvocationHandler;

public interface InvocationHandlerFactory {
   InvocationHandler getInvocationHandler();

   Class[] getProxyInterfaces();
}
