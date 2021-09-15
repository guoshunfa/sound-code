package org.omg.CosNaming;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public abstract class NamingContextPOA extends Servant implements NamingContextOperations, InvokeHandler {
   private static Hashtable _methods = new Hashtable();
   private static String[] __ids;

   public OutputStream _invoke(String var1, InputStream var2, ResponseHandler var3) {
      OutputStream var4 = null;
      Integer var5 = (Integer)_methods.get(var1);
      if (var5 == null) {
         throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
      } else {
         NameComponent[] var6;
         NamingContext var7;
         Object var37;
         switch(var5) {
         case 0:
            try {
               var6 = NameHelper.read(var2);
               var37 = ObjectHelper.read(var2);
               this.bind(var6, var37);
               var4 = var3.createReply();
            } catch (NotFound var30) {
               var4 = var3.createExceptionReply();
               NotFoundHelper.write(var4, var30);
            } catch (CannotProceed var31) {
               var4 = var3.createExceptionReply();
               CannotProceedHelper.write(var4, var31);
            } catch (InvalidName var32) {
               var4 = var3.createExceptionReply();
               InvalidNameHelper.write(var4, var32);
            } catch (AlreadyBound var33) {
               var4 = var3.createExceptionReply();
               AlreadyBoundHelper.write(var4, var33);
            }
            break;
         case 1:
            try {
               var6 = NameHelper.read(var2);
               var7 = NamingContextHelper.read(var2);
               this.bind_context(var6, var7);
               var4 = var3.createReply();
            } catch (NotFound var26) {
               var4 = var3.createExceptionReply();
               NotFoundHelper.write(var4, var26);
            } catch (CannotProceed var27) {
               var4 = var3.createExceptionReply();
               CannotProceedHelper.write(var4, var27);
            } catch (InvalidName var28) {
               var4 = var3.createExceptionReply();
               InvalidNameHelper.write(var4, var28);
            } catch (AlreadyBound var29) {
               var4 = var3.createExceptionReply();
               AlreadyBoundHelper.write(var4, var29);
            }
            break;
         case 2:
            try {
               var6 = NameHelper.read(var2);
               var37 = ObjectHelper.read(var2);
               this.rebind(var6, var37);
               var4 = var3.createReply();
            } catch (NotFound var23) {
               var4 = var3.createExceptionReply();
               NotFoundHelper.write(var4, var23);
            } catch (CannotProceed var24) {
               var4 = var3.createExceptionReply();
               CannotProceedHelper.write(var4, var24);
            } catch (InvalidName var25) {
               var4 = var3.createExceptionReply();
               InvalidNameHelper.write(var4, var25);
            }
            break;
         case 3:
            try {
               var6 = NameHelper.read(var2);
               var7 = NamingContextHelper.read(var2);
               this.rebind_context(var6, var7);
               var4 = var3.createReply();
            } catch (NotFound var20) {
               var4 = var3.createExceptionReply();
               NotFoundHelper.write(var4, var20);
            } catch (CannotProceed var21) {
               var4 = var3.createExceptionReply();
               CannotProceedHelper.write(var4, var21);
            } catch (InvalidName var22) {
               var4 = var3.createExceptionReply();
               InvalidNameHelper.write(var4, var22);
            }
            break;
         case 4:
            try {
               var6 = NameHelper.read(var2);
               var7 = null;
               var37 = this.resolve(var6);
               var4 = var3.createReply();
               ObjectHelper.write(var4, var37);
            } catch (NotFound var17) {
               var4 = var3.createExceptionReply();
               NotFoundHelper.write(var4, var17);
            } catch (CannotProceed var18) {
               var4 = var3.createExceptionReply();
               CannotProceedHelper.write(var4, var18);
            } catch (InvalidName var19) {
               var4 = var3.createExceptionReply();
               InvalidNameHelper.write(var4, var19);
            }
            break;
         case 5:
            try {
               var6 = NameHelper.read(var2);
               this.unbind(var6);
               var4 = var3.createReply();
            } catch (NotFound var14) {
               var4 = var3.createExceptionReply();
               NotFoundHelper.write(var4, var14);
            } catch (CannotProceed var15) {
               var4 = var3.createExceptionReply();
               CannotProceedHelper.write(var4, var15);
            } catch (InvalidName var16) {
               var4 = var3.createExceptionReply();
               InvalidNameHelper.write(var4, var16);
            }
            break;
         case 6:
            int var35 = var2.read_ulong();
            BindingListHolder var36 = new BindingListHolder();
            BindingIteratorHolder var8 = new BindingIteratorHolder();
            this.list(var35, var36, var8);
            var4 = var3.createReply();
            BindingListHelper.write(var4, var36.value);
            BindingIteratorHelper.write(var4, var8.value);
            break;
         case 7:
            var6 = null;
            NamingContext var34 = this.new_context();
            var4 = var3.createReply();
            NamingContextHelper.write(var4, var34);
            break;
         case 8:
            try {
               var6 = NameHelper.read(var2);
               var7 = null;
               var7 = this.bind_new_context(var6);
               var4 = var3.createReply();
               NamingContextHelper.write(var4, var7);
            } catch (NotFound var10) {
               var4 = var3.createExceptionReply();
               NotFoundHelper.write(var4, var10);
            } catch (AlreadyBound var11) {
               var4 = var3.createExceptionReply();
               AlreadyBoundHelper.write(var4, var11);
            } catch (CannotProceed var12) {
               var4 = var3.createExceptionReply();
               CannotProceedHelper.write(var4, var12);
            } catch (InvalidName var13) {
               var4 = var3.createExceptionReply();
               InvalidNameHelper.write(var4, var13);
            }
            break;
         case 9:
            try {
               this.destroy();
               var4 = var3.createReply();
            } catch (NotEmpty var9) {
               var4 = var3.createExceptionReply();
               NotEmptyHelper.write(var4, var9);
            }
            break;
         default:
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
         }

         return var4;
      }
   }

   public String[] _all_interfaces(POA var1, byte[] var2) {
      return (String[])((String[])__ids.clone());
   }

   public NamingContext _this() {
      return NamingContextHelper.narrow(super._this_object());
   }

   public NamingContext _this(ORB var1) {
      return NamingContextHelper.narrow(super._this_object(var1));
   }

   static {
      _methods.put("bind", new Integer(0));
      _methods.put("bind_context", new Integer(1));
      _methods.put("rebind", new Integer(2));
      _methods.put("rebind_context", new Integer(3));
      _methods.put("resolve", new Integer(4));
      _methods.put("unbind", new Integer(5));
      _methods.put("list", new Integer(6));
      _methods.put("new_context", new Integer(7));
      _methods.put("bind_new_context", new Integer(8));
      _methods.put("destroy", new Integer(9));
      __ids = new String[]{"IDL:omg.org/CosNaming/NamingContext:1.0"};
   }
}
