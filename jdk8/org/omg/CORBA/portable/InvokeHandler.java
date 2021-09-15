package org.omg.CORBA.portable;

import org.omg.CORBA.SystemException;

public interface InvokeHandler {
   OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) throws SystemException;
}
