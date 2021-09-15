package org.omg.CosNaming;

import java.util.Dictionary;
import java.util.Hashtable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.TCKind;
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

public abstract class _NamingContextImplBase extends DynamicImplementation implements NamingContext {
   private static final String[] _type_ids = new String[]{"IDL:omg.org/CosNaming/NamingContext:1.0"};
   private static Dictionary _methods = new Hashtable();

   public String[] _ids() {
      return (String[])((String[])_type_ids.clone());
   }

   public void invoke(ServerRequest var1) {
      NVList var2;
      Any var3;
      Any var4;
      Any var6;
      Any var7;
      NameComponent[] var36;
      Any var37;
      NameComponent[] var39;
      NamingContext var41;
      Object var42;
      Any var44;
      switch((Integer)_methods.get(var1.op_name())) {
      case 0:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(NameHelper.type());
         var2.add_value("n", var3, 1);
         var4 = this._orb().create_any();
         var4.type(ORB.init().get_primitive_tc(TCKind.tk_objref));
         var2.add_value("obj", var4, 1);
         var1.params(var2);
         var39 = NameHelper.extract(var3);
         var42 = var4.extract_Object();

         try {
            this.bind(var39, var42);
         } catch (NotFound var31) {
            var44 = this._orb().create_any();
            NotFoundHelper.insert(var44, var31);
            var1.except(var44);
            return;
         } catch (CannotProceed var32) {
            var44 = this._orb().create_any();
            CannotProceedHelper.insert(var44, var32);
            var1.except(var44);
            return;
         } catch (InvalidName var33) {
            var44 = this._orb().create_any();
            InvalidNameHelper.insert(var44, var33);
            var1.except(var44);
            return;
         } catch (AlreadyBound var34) {
            var44 = this._orb().create_any();
            AlreadyBoundHelper.insert(var44, var34);
            var1.except(var44);
            return;
         }

         var7 = this._orb().create_any();
         var7.type(this._orb().get_primitive_tc(TCKind.tk_void));
         var1.result(var7);
         break;
      case 1:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(NameHelper.type());
         var2.add_value("n", var3, 1);
         var4 = this._orb().create_any();
         var4.type(NamingContextHelper.type());
         var2.add_value("nc", var4, 1);
         var1.params(var2);
         var39 = NameHelper.extract(var3);
         var41 = NamingContextHelper.extract(var4);

         try {
            this.bind_context(var39, var41);
         } catch (NotFound var27) {
            var44 = this._orb().create_any();
            NotFoundHelper.insert(var44, var27);
            var1.except(var44);
            return;
         } catch (CannotProceed var28) {
            var44 = this._orb().create_any();
            CannotProceedHelper.insert(var44, var28);
            var1.except(var44);
            return;
         } catch (InvalidName var29) {
            var44 = this._orb().create_any();
            InvalidNameHelper.insert(var44, var29);
            var1.except(var44);
            return;
         } catch (AlreadyBound var30) {
            var44 = this._orb().create_any();
            AlreadyBoundHelper.insert(var44, var30);
            var1.except(var44);
            return;
         }

         var7 = this._orb().create_any();
         var7.type(this._orb().get_primitive_tc(TCKind.tk_void));
         var1.result(var7);
         break;
      case 2:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(NameHelper.type());
         var2.add_value("n", var3, 1);
         var4 = this._orb().create_any();
         var4.type(ORB.init().get_primitive_tc(TCKind.tk_objref));
         var2.add_value("obj", var4, 1);
         var1.params(var2);
         var39 = NameHelper.extract(var3);
         var42 = var4.extract_Object();

         try {
            this.rebind(var39, var42);
         } catch (NotFound var24) {
            var44 = this._orb().create_any();
            NotFoundHelper.insert(var44, var24);
            var1.except(var44);
            return;
         } catch (CannotProceed var25) {
            var44 = this._orb().create_any();
            CannotProceedHelper.insert(var44, var25);
            var1.except(var44);
            return;
         } catch (InvalidName var26) {
            var44 = this._orb().create_any();
            InvalidNameHelper.insert(var44, var26);
            var1.except(var44);
            return;
         }

         var7 = this._orb().create_any();
         var7.type(this._orb().get_primitive_tc(TCKind.tk_void));
         var1.result(var7);
         break;
      case 3:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(NameHelper.type());
         var2.add_value("n", var3, 1);
         var4 = this._orb().create_any();
         var4.type(NamingContextHelper.type());
         var2.add_value("nc", var4, 1);
         var1.params(var2);
         var39 = NameHelper.extract(var3);
         var41 = NamingContextHelper.extract(var4);

         try {
            this.rebind_context(var39, var41);
         } catch (NotFound var21) {
            var44 = this._orb().create_any();
            NotFoundHelper.insert(var44, var21);
            var1.except(var44);
            return;
         } catch (CannotProceed var22) {
            var44 = this._orb().create_any();
            CannotProceedHelper.insert(var44, var22);
            var1.except(var44);
            return;
         } catch (InvalidName var23) {
            var44 = this._orb().create_any();
            InvalidNameHelper.insert(var44, var23);
            var1.except(var44);
            return;
         }

         var7 = this._orb().create_any();
         var7.type(this._orb().get_primitive_tc(TCKind.tk_void));
         var1.result(var7);
         break;
      case 4:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(NameHelper.type());
         var2.add_value("n", var3, 1);
         var1.params(var2);
         var36 = NameHelper.extract(var3);

         Object var38;
         try {
            var38 = this.resolve(var36);
         } catch (NotFound var18) {
            var7 = this._orb().create_any();
            NotFoundHelper.insert(var7, var18);
            var1.except(var7);
            return;
         } catch (CannotProceed var19) {
            var7 = this._orb().create_any();
            CannotProceedHelper.insert(var7, var19);
            var1.except(var7);
            return;
         } catch (InvalidName var20) {
            var7 = this._orb().create_any();
            InvalidNameHelper.insert(var7, var20);
            var1.except(var7);
            return;
         }

         var6 = this._orb().create_any();
         var6.insert_Object(var38);
         var1.result(var6);
         break;
      case 5:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(NameHelper.type());
         var2.add_value("n", var3, 1);
         var1.params(var2);
         var36 = NameHelper.extract(var3);

         try {
            this.unbind(var36);
         } catch (NotFound var15) {
            var6 = this._orb().create_any();
            NotFoundHelper.insert(var6, var15);
            var1.except(var6);
            return;
         } catch (CannotProceed var16) {
            var6 = this._orb().create_any();
            CannotProceedHelper.insert(var6, var16);
            var1.except(var6);
            return;
         } catch (InvalidName var17) {
            var6 = this._orb().create_any();
            InvalidNameHelper.insert(var6, var17);
            var1.except(var6);
            return;
         }

         var37 = this._orb().create_any();
         var37.type(this._orb().get_primitive_tc(TCKind.tk_void));
         var1.result(var37);
         break;
      case 6:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(ORB.init().get_primitive_tc(TCKind.tk_ulong));
         var2.add_value("how_many", var3, 1);
         var4 = this._orb().create_any();
         var4.type(BindingListHelper.type());
         var2.add_value("bl", var4, 2);
         var37 = this._orb().create_any();
         var37.type(BindingIteratorHelper.type());
         var2.add_value("bi", var37, 2);
         var1.params(var2);
         int var40 = var3.extract_ulong();
         BindingListHolder var43 = new BindingListHolder();
         BindingIteratorHolder var8 = new BindingIteratorHolder();
         this.list(var40, var43, var8);
         BindingListHelper.insert(var4, var43.value);
         BindingIteratorHelper.insert(var37, var8.value);
         Any var9 = this._orb().create_any();
         var9.type(this._orb().get_primitive_tc(TCKind.tk_void));
         var1.result(var9);
         break;
      case 7:
         var2 = this._orb().create_list(0);
         var1.params(var2);
         NamingContext var35 = this.new_context();
         var4 = this._orb().create_any();
         NamingContextHelper.insert(var4, var35);
         var1.result(var4);
         break;
      case 8:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(NameHelper.type());
         var2.add_value("n", var3, 1);
         var1.params(var2);
         var36 = NameHelper.extract(var3);

         NamingContext var5;
         try {
            var5 = this.bind_new_context(var36);
         } catch (NotFound var11) {
            var7 = this._orb().create_any();
            NotFoundHelper.insert(var7, var11);
            var1.except(var7);
            return;
         } catch (AlreadyBound var12) {
            var7 = this._orb().create_any();
            AlreadyBoundHelper.insert(var7, var12);
            var1.except(var7);
            return;
         } catch (CannotProceed var13) {
            var7 = this._orb().create_any();
            CannotProceedHelper.insert(var7, var13);
            var1.except(var7);
            return;
         } catch (InvalidName var14) {
            var7 = this._orb().create_any();
            InvalidNameHelper.insert(var7, var14);
            var1.except(var7);
            return;
         }

         var6 = this._orb().create_any();
         NamingContextHelper.insert(var6, var5);
         var1.result(var6);
         break;
      case 9:
         var2 = this._orb().create_list(0);
         var1.params(var2);

         try {
            this.destroy();
         } catch (NotEmpty var10) {
            var4 = this._orb().create_any();
            NotEmptyHelper.insert(var4, var10);
            var1.except(var4);
            return;
         }

         var3 = this._orb().create_any();
         var3.type(this._orb().get_primitive_tc(TCKind.tk_void));
         var1.result(var3);
         break;
      default:
         throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
      }

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
   }
}
