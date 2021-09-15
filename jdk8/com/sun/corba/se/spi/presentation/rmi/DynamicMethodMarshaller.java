package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.spi.orb.ORB;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public interface DynamicMethodMarshaller {
   Method getMethod();

   Object[] copyArguments(Object[] var1, ORB var2) throws RemoteException;

   Object[] readArguments(InputStream var1);

   void writeArguments(OutputStream var1, Object[] var2);

   Object copyResult(Object var1, ORB var2) throws RemoteException;

   Object readResult(InputStream var1);

   void writeResult(OutputStream var1, Object var2);

   boolean isDeclaredException(Throwable var1);

   void writeException(OutputStream var1, Exception var2);

   Exception readException(ApplicationException var1);
}
