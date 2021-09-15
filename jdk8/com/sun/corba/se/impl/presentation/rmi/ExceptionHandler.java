package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.OutputStream;

public interface ExceptionHandler {
   boolean isDeclaredException(Class var1);

   void writeException(OutputStream var1, Exception var2);

   Exception readException(ApplicationException var1);
}
