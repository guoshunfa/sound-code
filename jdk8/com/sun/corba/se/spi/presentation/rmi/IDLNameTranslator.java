package com.sun.corba.se.spi.presentation.rmi;

import java.lang.reflect.Method;

public interface IDLNameTranslator {
   Class[] getInterfaces();

   Method[] getMethods();

   Method getMethod(String var1);

   String getIDLName(Method var1);
}
