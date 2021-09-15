package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;
import java.util.Hashtable;
import java.util.Stack;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;

public class FVDCodeBaseImpl extends _CodeBaseImplBase {
   private static Hashtable fvds = new Hashtable();
   private transient ORB orb = null;
   private transient OMGSystemException wrapper = OMGSystemException.get("rpc.encoding");
   private transient ValueHandlerImpl vhandler = null;

   void setValueHandler(ValueHandler var1) {
      this.vhandler = (ValueHandlerImpl)var1;
   }

   public Repository get_ir() {
      return null;
   }

   public String implementation(String var1) {
      try {
         if (this.vhandler == null) {
            this.vhandler = ValueHandlerImpl.getInstance(false);
         }

         String var2 = Util.getCodebase(this.vhandler.getClassFromType(var1));
         return var2 == null ? "" : var2;
      } catch (ClassNotFoundException var3) {
         throw this.wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE, var3);
      }
   }

   public String[] implementations(String[] var1) {
      String[] var2 = new String[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = this.implementation(var1[var3]);
      }

      return var2;
   }

   public FullValueDescription meta(String var1) {
      try {
         FullValueDescription var2 = (FullValueDescription)fvds.get(var1);
         if (var2 == null) {
            if (this.vhandler == null) {
               this.vhandler = ValueHandlerImpl.getInstance(false);
            }

            try {
               var2 = ValueUtility.translate(this._orb(), ObjectStreamClass.lookup(this.vhandler.getAnyClassFromType(var1)), this.vhandler);
            } catch (Throwable var4) {
               if (this.orb == null) {
                  this.orb = ORB.init();
               }

               var2 = ValueUtility.translate(this.orb, ObjectStreamClass.lookup(this.vhandler.getAnyClassFromType(var1)), this.vhandler);
            }

            if (var2 == null) {
               throw this.wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE);
            }

            fvds.put(var1, var2);
         }

         return var2;
      } catch (Throwable var5) {
         throw this.wrapper.incompatibleValueImpl(CompletionStatus.COMPLETED_MAYBE, var5);
      }
   }

   public FullValueDescription[] metas(String[] var1) {
      FullValueDescription[] var2 = new FullValueDescription[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = this.meta(var1[var3]);
      }

      return var2;
   }

   public String[] bases(String var1) {
      try {
         if (this.vhandler == null) {
            this.vhandler = ValueHandlerImpl.getInstance(false);
         }

         Stack var2 = new Stack();

         for(Class var3 = ObjectStreamClass.lookup(this.vhandler.getClassFromType(var1)).forClass().getSuperclass(); !var3.equals(Object.class); var3 = var3.getSuperclass()) {
            var2.push(this.vhandler.createForAnyType(var3));
         }

         String[] var4 = new String[var2.size()];

         for(int var5 = var4.length - 1; var5 >= 0; ++var5) {
            var4[var5] = (String)var2.pop();
         }

         return var4;
      } catch (Throwable var6) {
         throw this.wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE, var6);
      }
   }
}
