package com.sun.org.omg.SendingContext;

import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.RepositoryHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLSeqHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _CodeBaseImplBase extends ObjectImpl implements CodeBase, InvokeHandler {
   private static Hashtable _methods = new Hashtable();
   private static String[] __ids;

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) {
      OutputStream var4 = var3.createReply();
      Integer var5 = (Integer)_methods.get(var1);
      if (var5 == null) {
         throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
      } else {
         String var6;
         String[] var7;
         String[] var8;
         switch(var5) {
         case 0:
            var6 = null;
            Repository var9 = this.get_ir();
            RepositoryHelper.write(var4, var9);
            break;
         case 1:
            var6 = RepositoryIdHelper.read(var2);
            var7 = null;
            String var12 = this.implementation(var6);
            var4.write_string(var12);
            break;
         case 2:
            var8 = RepositoryIdSeqHelper.read(var2);
            var7 = null;
            var7 = this.implementations(var8);
            URLSeqHelper.write(var4, var7);
            break;
         case 3:
            var6 = RepositoryIdHelper.read(var2);
            var7 = null;
            FullValueDescription var11 = this.meta(var6);
            FullValueDescriptionHelper.write(var4, var11);
            break;
         case 4:
            var8 = RepositoryIdSeqHelper.read(var2);
            var7 = null;
            FullValueDescription[] var10 = this.metas(var8);
            ValueDescSeqHelper.write(var4, var10);
            break;
         case 5:
            var6 = RepositoryIdHelper.read(var2);
            var7 = null;
            var7 = this.bases(var6);
            RepositoryIdSeqHelper.write(var4, var7);
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
      _methods.put("get_ir", new Integer(0));
      _methods.put("implementation", new Integer(1));
      _methods.put("implementations", new Integer(2));
      _methods.put("meta", new Integer(3));
      _methods.put("metas", new Integer(4));
      _methods.put("bases", new Integer(5));
      __ids = new String[]{"IDL:omg.org/SendingContext/CodeBase:1.0", "IDL:omg.org/SendingContext/RunTime:1.0"};
   }
}
