package org.omg.CosNaming;

import java.util.Dictionary;
import java.util.Hashtable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.TCKind;

public abstract class _BindingIteratorImplBase extends DynamicImplementation implements BindingIterator {
   private static final String[] _type_ids = new String[]{"IDL:omg.org/CosNaming/BindingIterator:1.0"};
   private static Dictionary _methods = new Hashtable();

   public String[] _ids() {
      return (String[])((String[])_type_ids.clone());
   }

   public void invoke(ServerRequest var1) {
      NVList var2;
      Any var3;
      switch((Integer)_methods.get(var1.op_name())) {
      case 0:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(BindingHelper.type());
         var2.add_value("b", var3, 2);
         var1.params(var2);
         BindingHolder var9 = new BindingHolder();
         boolean var10 = this.next_one(var9);
         BindingHelper.insert(var3, var9.value);
         Any var11 = this._orb().create_any();
         var11.insert_boolean(var10);
         var1.result(var11);
         break;
      case 1:
         var2 = this._orb().create_list(0);
         var3 = this._orb().create_any();
         var3.type(ORB.init().get_primitive_tc(TCKind.tk_ulong));
         var2.add_value("how_many", var3, 1);
         Any var4 = this._orb().create_any();
         var4.type(BindingListHelper.type());
         var2.add_value("bl", var4, 2);
         var1.params(var2);
         int var5 = var3.extract_ulong();
         BindingListHolder var6 = new BindingListHolder();
         boolean var7 = this.next_n(var5, var6);
         BindingListHelper.insert(var4, var6.value);
         Any var8 = this._orb().create_any();
         var8.insert_boolean(var7);
         var1.result(var8);
         break;
      case 2:
         var2 = this._orb().create_list(0);
         var1.params(var2);
         this.destroy();
         var3 = this._orb().create_any();
         var3.type(this._orb().get_primitive_tc(TCKind.tk_void));
         var1.result(var3);
         break;
      default:
         throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
      }

   }

   static {
      _methods.put("next_one", new Integer(0));
      _methods.put("next_n", new Integer(1));
      _methods.put("destroy", new Integer(2));
   }
}
