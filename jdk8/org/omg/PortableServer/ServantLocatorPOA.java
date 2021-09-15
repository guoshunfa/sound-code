package org.omg.PortableServer;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class ServantLocatorPOA extends Servant implements ServantLocatorOperations, InvokeHandler {
   private static Hashtable _methods = new Hashtable();
   private static String[] __ids;

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) {
      throw new BAD_OPERATION();
   }

   public String[] _all_interfaces(POA var1, byte[] var2) {
      return (String[])((String[])__ids.clone());
   }

   public ServantLocator _this() {
      return ServantLocatorHelper.narrow(super._this_object());
   }

   public ServantLocator _this(ORB var1) {
      return ServantLocatorHelper.narrow(super._this_object(var1));
   }

   static {
      _methods.put("preinvoke", new Integer(0));
      _methods.put("postinvoke", new Integer(1));
      __ids = new String[]{"IDL:omg.org/PortableServer/ServantLocator:1.0", "IDL:omg.org/PortableServer/ServantManager:1.0"};
   }
}
