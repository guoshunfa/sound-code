package com.sun.corba.se.spi.orbutil.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public interface LinkedInvocationHandler extends InvocationHandler {
   void setProxy(Proxy var1);

   Proxy getProxy();
}
