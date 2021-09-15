package com.sun.org.omg.CORBA.ValueDefPackage;

import com.sun.org.omg.CORBA.AttributeDescription;
import com.sun.org.omg.CORBA.Initializer;
import com.sun.org.omg.CORBA.OperationDescription;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.IDLEntity;

public final class FullValueDescription implements IDLEntity {
   public String name = null;
   public String id = null;
   public boolean is_abstract = false;
   public boolean is_custom = false;
   public String defined_in = null;
   public String version = null;
   public OperationDescription[] operations = null;
   public AttributeDescription[] attributes = null;
   public ValueMember[] members = null;
   public Initializer[] initializers = null;
   public String[] supported_interfaces = null;
   public String[] abstract_base_values = null;
   public boolean is_truncatable = false;
   public String base_value = null;
   public TypeCode type = null;

   public FullValueDescription() {
   }

   public FullValueDescription(String var1, String var2, boolean var3, boolean var4, String var5, String var6, OperationDescription[] var7, AttributeDescription[] var8, ValueMember[] var9, Initializer[] var10, String[] var11, String[] var12, boolean var13, String var14, TypeCode var15) {
      this.name = var1;
      this.id = var2;
      this.is_abstract = var3;
      this.is_custom = var4;
      this.defined_in = var5;
      this.version = var6;
      this.operations = var7;
      this.attributes = var8;
      this.members = var9;
      this.initializers = var10;
      this.supported_interfaces = var11;
      this.abstract_base_values = var12;
      this.is_truncatable = var13;
      this.base_value = var14;
      this.type = var15;
   }
}
