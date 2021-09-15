package com.sun.corba.se.spi.activation;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _ServerImplBase extends ObjectImpl implements Server, InvokeHandler {
   private static Hashtable _methods = new Hashtable();
   private static String[] __ids;

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) {
      OutputStream var4 = null;
      Integer var5 = (Integer)_methods.get(var1);
      if (var5 == null) {
         throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
      } else {
         switch(var5) {
         case 0:
            this.shutdown();
            var4 = var3.createReply();
            break;
         case 1:
            this.install();
            var4 = var3.createReply();
            break;
         case 2:
            this.uninstall();
            var4 = var3.createReply();
            break;
         default:
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
         }

         return var4;
      }
   }

   public String[] _ids() {
      return (String[])((String[])__ids.clone());
   }

   static {
      _methods.put("shutdown", new Integer(0));
      _methods.put("install", new Integer(1));
      _methods.put("uninstall", new Integer(2));
      __ids = new String[]{"IDL:activation/Server:1.0"};
   }
}
