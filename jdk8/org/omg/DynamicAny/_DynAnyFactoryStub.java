package org.omg.DynamicAny;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

public class _DynAnyFactoryStub extends ObjectImpl implements DynAnyFactory {
   public static final Class _opsClass = DynAnyFactoryOperations.class;
   private static String[] __ids = new String[]{"IDL:omg.org/DynamicAny/DynAnyFactory:1.0"};

   public DynAny create_dyn_any(Any var1) throws InconsistentTypeCode {
      ServantObject var2 = this._servant_preinvoke("create_dyn_any", _opsClass);
      DynAnyFactoryOperations var3 = (DynAnyFactoryOperations)var2.servant;

      DynAny var4;
      try {
         var4 = var3.create_dyn_any(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

      return var4;
   }

   public DynAny create_dyn_any_from_type_code(TypeCode var1) throws InconsistentTypeCode {
      ServantObject var2 = this._servant_preinvoke("create_dyn_any_from_type_code", _opsClass);
      DynAnyFactoryOperations var3 = (DynAnyFactoryOperations)var2.servant;

      DynAny var4;
      try {
         var4 = var3.create_dyn_any_from_type_code(var1);
      } finally {
         this._servant_postinvoke(var2);
      }

      return var4;
   }

   public String[] _ids() {
      return (String[])((String[])__ids.clone());
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      String var2 = var1.readUTF();
      Object var3 = null;
      Object var4 = null;
      ORB var5 = ORB.init((String[])var3, (Properties)var4);

      try {
         org.omg.CORBA.Object var6 = var5.string_to_object(var2);
         Delegate var7 = ((ObjectImpl)var6)._get_delegate();
         this._set_delegate(var7);
      } finally {
         var5.destroy();
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      Object var2 = null;
      Object var3 = null;
      ORB var4 = ORB.init((String[])var2, (Properties)var3);

      try {
         String var5 = var4.object_to_string(this);
         var1.writeUTF(var5);
      } finally {
         var4.destroy();
      }

   }
}
