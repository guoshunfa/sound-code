package com.sun.corba.se.spi.protocol;

import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public interface LocalClientRequestDispatcher {
   boolean useLocalInvocation(Object var1);

   boolean is_local(Object var1);

   ServantObject servant_preinvoke(Object var1, String var2, Class var3);

   void servant_postinvoke(Object var1, ServantObject var2);
}
