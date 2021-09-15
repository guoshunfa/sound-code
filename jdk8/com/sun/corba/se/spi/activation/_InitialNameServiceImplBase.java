package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBoundHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Object;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _InitialNameServiceImplBase extends ObjectImpl implements InitialNameService, InvokeHandler {
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
            try {
               String var6 = var2.read_string();
               Object var7 = ObjectHelper.read(var2);
               boolean var8 = var2.read_boolean();
               this.bind(var6, var7, var8);
               var4 = var3.createReply();
            } catch (NameAlreadyBound var9) {
               var4 = var3.createExceptionReply();
               NameAlreadyBoundHelper.write(var4, var9);
            }

            return var4;
         default:
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
         }
      }
   }

   public String[] _ids() {
      return (String[])((String[])__ids.clone());
   }

   static {
      _methods.put("bind", new Integer(0));
      __ids = new String[]{"IDL:activation/InitialNameService:1.0"};
   }
}
