package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ServantObject;

public class NotLocalLocalCRDImpl implements LocalClientRequestDispatcher {
   public boolean useLocalInvocation(Object var1) {
      return false;
   }

   public boolean is_local(Object var1) {
      return false;
   }

   public ServantObject servant_preinvoke(Object var1, String var2, Class var3) {
      return null;
   }

   public void servant_postinvoke(Object var1, ServantObject var2) {
   }
}
